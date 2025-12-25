package com.tubes_impal.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MultiSessionManager {

    public static void setSessionAttribute(HttpServletRequest request, HttpServletResponse response,
            String role, String key, Object value) {
        HttpSession session = request.getSession(true);
        session.setAttribute(getSessionKey(role, key), value);
        session.setAttribute(getSessionKey(role, "role"), role);
    }

    public static Object getSessionAttribute(HttpServletRequest request, String role, String key) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return session.getAttribute(getSessionKey(role, key));
    }

    public static void invalidateSession(HttpServletRequest request, HttpServletResponse response, String role) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Remove all attributes for this role
            session.removeAttribute(getSessionKey(role, "userId"));
            session.removeAttribute(getSessionKey(role, "username"));
            session.removeAttribute(getSessionKey(role, "role"));
        }
    }

    private static String getSessionKey(String role, String key) {
        return role.toUpperCase() + "_" + key;
    }

    public static boolean isAuthenticated(HttpServletRequest request, String role) {
        Object userId = getSessionAttribute(request, role, "userId");
        Object sessionRole = getSessionAttribute(request, role, "role");
        return userId != null && role.equals(sessionRole);
    }
}
