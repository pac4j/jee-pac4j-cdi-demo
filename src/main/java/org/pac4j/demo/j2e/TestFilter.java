package org.pac4j.demo.j2e;

import org.pac4j.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.*;
import java.io.IOException;

public class TestFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TestFilter.class);

    @Inject
    private Config config;

    public void init(FilterConfig var1) throws ServletException {}

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        logger.info("Injected config: {}", config);
    }

    public void destroy() {}
}
