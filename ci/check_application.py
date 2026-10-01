"""HTTP regression checks for the deployed JSF/CDI demo (standard library only)."""

import base64
import html
import http.cookiejar
import re
from urllib.error import HTTPError
from urllib.parse import urlencode
from urllib.request import build_opener, HTTPCookieProcessor, HTTPRedirectHandler, Request

BASE_URL = "http://localhost:8080"


class NoRedirect(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def session():
    return build_opener(HTTPCookieProcessor(http.cookiejar.CookieJar()), NoRedirect())


def check(client, path, status, marker=None, data=None, headers=None):
    request = Request(
        BASE_URL + path,
        data=None if data is None else urlencode(data).encode(),
        headers=headers or {},
    )
    try:
        response = client.open(request, timeout=30)
    except HTTPError as error:
        response = error
    with response:
        body = response.read().decode()
        if response.status != status:
            raise AssertionError(f"{path.split('?')[0]}: expected {status}, got {response.status}: {body[:300]}")
        if marker and marker not in body:
            raise AssertionError(f"{path.split('?')[0]}: missing {marker!r}: {body[:300]}")
        print(path.split("?")[0], response.status, flush=True)
        return response.headers, body


def check_application():
    client = session()
    check(client, "/", 200, "Pac4J JSF/CDI Demo")
    check(client, "/facebook/notprotected.action", 200)
    headers, _ = check(client, "/form/index.action", 302)
    assert "/loginForm.action" in headers["Location"]
    check(client, "/loginForm.action", 200, 'name="username"')
    check(client, "/callback?client_name=FormClient", 303,
          data={"username": "demo", "password": "demo"})
    check(client, "/form/index.action", 200, "Id: demo")
    check(client, "/", 200, "Id: demo")

    _, body = check(client, "/jwt.action", 200)
    token = re.search(r"eyJ[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+", body)
    assert token, "JWT generation did not return a signed token"
    check(session(), "/rest-jwt/index.action?" + urlencode({"token": token.group()}),
          200, "Id: demo")

    check(client, "/logout", 302)
    _, body = check(client, "/", 200)
    assert "Id: demo" not in body, "Logout did not clear the authenticated profile"

    check(session(), "/dba/index.action", 401)
    credentials = base64.b64encode(b"demo:demo").decode()
    check(session(), "/dba/index.action", 200, "Id: demo",
          headers={"Authorization": "Basic " + credentials})
    headers, _ = check(session(), "/forceLogin?client_name=FormClient", 302)
    assert "/loginForm.action" in headers["Location"]

    client = session()
    _, body = check(client, "/jsfLoginForm.action", 200)
    inputs = {
        html.unescape(name): html.unescape(value)
        for name, value in re.findall(r'<input[^>]*name="([^"]+)"[^>]*value="([^"]*)"', body)
    }
    assert "jakarta.faces.ViewState" in inputs, "The JSF login form was not rendered"
    inputs.update({"form1:username": "jsfdemo", "form1:password": "jsfdemo"})
    check(client, "/jsfLoginForm.action", 302, data=inputs)
    check(client, "/jsfform/index.action", 200, "Id: jsfdemo")
    print("All local HTTP checks passed", flush=True)


if __name__ == "__main__":
    check_application()
