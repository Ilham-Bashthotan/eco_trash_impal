package com.tubes_impal.utils;

import jakarta.servlet.http.HttpServletRequest;

public class SessionHelper {

    public static Integer getUserId(HttpServletRequest request, String role) {
        Object userId = MultiSessionManager.getSessionAttribute(request, role, "userId");
        return userId != null ? (Integer) userId : null;
    }

    public static String getUsername(HttpServletRequest request, String role) {
        Object username = MultiSessionManager.getSessionAttribute(request, role, "username");
        return username != null ? (String) username : null;
    }

    public static String getRole(HttpServletRequest request, String role) {
        Object roleValue = MultiSessionManager.getSessionAttribute(request, role, "role");
        return roleValue != null ? (String) roleValue : null;
    }

    public static boolean isAuthenticated(HttpServletRequest request, String expectedRole) {
        return MultiSessionManager.isAuthenticated(request, expectedRole);
    }
}
