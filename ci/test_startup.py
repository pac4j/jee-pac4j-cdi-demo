#!/usr/bin/env python3
"""Run both documented Maven commands and check the deployed application."""

import os
from pathlib import Path
import shutil
import signal
import socket
import subprocess
import tempfile
import time
from urllib.error import URLError
from urllib.request import urlopen

from check_application import check_application

PROJECT = Path(__file__).resolve().parent.parent
GOALS = ("embedded-payara:run", "payara-micro:start")


def stop(process, goal):
    if process.poll() is None:
        if goal == "embedded-payara:run":
            try:
                process.stdin.write("X\n")
                process.stdin.flush()
            except (BrokenPipeError, OSError):
                pass
        else:
            os.killpg(process.pid, signal.SIGINT)
        try:
            process.wait(timeout=30)
        except subprocess.TimeoutExpired:
            os.killpg(process.pid, signal.SIGTERM)
            try:
                process.wait(timeout=10)
            except subprocess.TimeoutExpired:
                os.killpg(process.pid, signal.SIGKILL)
                process.wait(timeout=10)
    # The Micro plugin starts a child JVM; clean up this test's process group too.
    try:
        os.killpg(process.pid, signal.SIGTERM)
    except ProcessLookupError:
        pass
    process.stdin.close()


def wait_for_application(process):
    deadline = time.monotonic() + 180
    while time.monotonic() < deadline:
        if process.poll() is not None:
            raise RuntimeError(f"Maven exited before deployment (code {process.returncode})")
        try:
            with urlopen("http://localhost:8080/", timeout=5) as response:
                body = response.read().decode()
                if response.status == 200 and "Pac4J JSF/CDI Demo" in body:
                    return
        except (URLError, socket.timeout, TimeoutError, ConnectionError):
            pass
        time.sleep(1)
    raise RuntimeError("The demo did not deploy within 180 seconds")


def main():
    with tempfile.TemporaryDirectory(prefix="jee-cdi-startup-") as directory:
        logs = Path(directory)
        try:
            for goal in GOALS:
                with socket.socket() as port_check:
                    if port_check.connect_ex(("localhost", 8080)) == 0:
                        raise RuntimeError("Port 8080 is in use; stop the existing server first")
                print(f"Starting: mvn clean package {goal}", flush=True)
                log_path = logs / (goal.split(":")[0] + ".log")
                with log_path.open("w") as output:
                    process = subprocess.Popen(
                        ["mvn", "clean", "package", goal], cwd=PROJECT,
                        stdin=subprocess.PIPE, stdout=output, stderr=subprocess.STDOUT,
                        text=True, start_new_session=True,
                    )
                    try:
                        wait_for_application(process)
                        check_application()
                        print(f"PASS: {goal}", flush=True)
                    except Exception:
                        print(log_path.read_text()[-12000:], flush=True)
                        raise
                    finally:
                        stop(process, goal)
        finally:
            target = PROJECT / "target"
            target.mkdir(exist_ok=True)
            for log_path in logs.glob("*.log"):
                shutil.copyfile(log_path, target / log_path.name)


if __name__ == "__main__":
    main()
