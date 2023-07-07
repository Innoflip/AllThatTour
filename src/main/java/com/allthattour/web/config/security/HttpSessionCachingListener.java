package com.allthattour.web.config.security;

import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class HttpSessionCachingListener {

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionCachingListener.class);

    private final Map<String, HttpSession> sessionCache = new WeakHashMap<>();

    void onHttpSessionCreatedEvent(final HttpSessionCreatedEvent event) {
        if (event != null && event.getSession() != null && event.getSession().getId() != null) {
            sessionCache.put(event.getSession().getId(), event.getSession());
        }
    }

    void onHttpSessionDestroyedEvent(final HttpSessionDestroyedEvent event) {
        if (event != null && event.getSession() != null && event.getSession().getId() != null) {
            sessionCache.remove(event.getSession().getId());
        }
    }

    public void timeOutSession(final String sessionId) {
        if (sessionId != null) {
            final HttpSession httpSession = sessionCache.get(sessionId);
            if (null != httpSession) {
                logger.debug("invalidating session {} in 1 second", sessionId);
                httpSession.setMaxInactiveInterval(1);
            }
        }
    }

    @Component
    static class HttpSessionCreatedLisener implements ApplicationListener<HttpSessionCreatedEvent> {

        @Autowired
        HttpSessionCachingListener parent;

        @Override
        public void onApplicationEvent(final HttpSessionCreatedEvent event) {
            parent.onHttpSessionCreatedEvent(event);
        }
    }

    @Component
    static class HttpSessionDestroyedLisener implements ApplicationListener<HttpSessionDestroyedEvent> {

        @Autowired
        HttpSessionCachingListener parent;

        @Override
        public void onApplicationEvent(final HttpSessionDestroyedEvent event) {
            parent.onHttpSessionDestroyedEvent(event);
        }
    }

    @Component
    static class AskToTimeOutSessionLisener implements ApplicationListener<AskToExpireSessionEvent> {

        @Autowired
        HttpSessionCachingListener parent;

        @Override
        public void onApplicationEvent(final AskToExpireSessionEvent event) {
            if (event != null) {
                parent.timeOutSession(event.getSource());
            }
        }
    }

}