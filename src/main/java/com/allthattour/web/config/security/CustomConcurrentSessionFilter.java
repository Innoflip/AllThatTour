package com.allthattour.web.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

public class CustomConcurrentSessionFilter extends ConcurrentSessionFilter {

    public CustomConcurrentSessionFilter(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
    }

    public CustomConcurrentSessionFilter(SessionRegistry sessionRegistry,
            SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
        super(sessionRegistry, sessionInformationExpiredStrategy);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        super.doFilter(req, res, chain);
    }

}