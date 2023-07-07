package com.allthattour.web.config.security;

import org.springframework.context.ApplicationEvent;

public class AskToExpireSessionEvent extends ApplicationEvent {
    public AskToExpireSessionEvent(final Object source) {
        super(source);
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }

    public static AskToExpireSessionEvent of(final String sessionId) {
        return new AskToExpireSessionEvent(sessionId);
    }
}