<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-j2e.png" width="300" />
</p>

> This demo secures a Jakarta EE (CDI) application with **[jee-pac4j](https://github.com/pac4j/jee-pac4j)**, the Jakarta EE / Java EE implementation of **[pac4j](https://github.com/pac4j/pac4j)**, the security engine for Java.
> If it is useful to you, please ⭐ **[star pac4j on GitHub](https://github.com/pac4j/pac4j)**: it helps other developers discover it!

This `jee-pac4j-cdi-demo` project is a Jakarta EE web application to test the [jee-pac4j](https://github.com/pac4j/jee-pac4j) security library with various authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML, OpenID Connect, JWT...

## Start and test

The demo version is **8.0.4-SNAPSHOT** and uses the released **jakartaee-pac4j 8.0.4**
with **pac4j 6.5.9**. Use **JDK 21 or later** with Payara 7 / Jakarta EE 11.
The startup commands and local authentication flows are tested on JDK 25 in CI.
Payara supplies Faces; the WAR does not bundle another implementation.


Build the project and launch the web app via the [Payara Server](https://www.payara.fish/) on [http://localhost:8080](http://localhost:8080)
   with either the payara-micro maven plugin or embedded-payara plugin:

    cd jee-pac4j-cdi-demo
    mvn clean package payara-micro:start
    
OR

    cd jee-pac4j-cdi-demo
    mvn clean package embedded-payara:run

To test, you can call a protected URL by clicking on the "Protected url by **xxx**" link, which will start the authentication process with the **xxx** provider.


Run one server at a time on port 8080. Exit Embedded with `X` followed by Enter;
stop Micro with Ctrl+C. The `.mvn/jvm.config` file supplies the module access
required by Embedded Payara and Weld when they run inside Maven's JVM.

To verify both startup commands and the local form, JSF/CDI, Basic, JWT, forced-login
and logout flows (Python 3 required):

```sh
python3 ci/test_startup.py
```

The test starts each server, checks the rendered application and authentication flows,
and stops it before starting the other. Logs are saved in `target/embedded-payara.log`
and `target/payara-micro.log`. OAuth, OIDC, CAS and SAML logins additionally require
the corresponding identity provider; they are not exercised by this startup test.
