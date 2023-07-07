package com.allthattour.web.config.security.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSession {
    private String username;
    private List<SessionInfo> sessions;

    public int getCount() {
        return sessions.size();
    }
}
