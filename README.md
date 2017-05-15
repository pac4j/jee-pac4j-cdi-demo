<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-j2e.png" width="300" />
</p>

This `j2e-pac4j-cdi-demo` project is a JavaEE web application to test the [j2e-pac4j](https://github.com/pac4j/j2e-pac4j) security library with various authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML, OpenID Connect, JWT...

## Setup

To use the Payara Maven plugin, we temporarily need to build it locally:

    git clone https://github.com/payara/maven-plugins payara-maven-plugins
    cd payara-maven-plugins
    cd payara-micro-maven-plugin
    mvn install

## Start and test

Build the project and launch the web app via the [Payara Server](http://www.payara.fish/) on [http://localhost:8080](http://localhost:8080):

    cd j2-pac4j-cdi-demo
    mvn clean package payara-micro:start

To test, you can call a protected URL by clicking on the "Protected url by **xxx**" link, which will start the authentication process with the **xxx** provider.
