package com.app.backend.global.security.util;

import com.app.backend.global.rs.RsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthResponseUtil {

    public static void success(HttpServletResponse response, String accessToken, Cookie cookie, int status, RsData<?> rsData, ObjectMapper om) throws IOException {
        response.setHeader("Authorization", accessToken);
        response.addCookie(cookie);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(om.writeValueAsString(rsData));
    }

    public static void failLogin(HttpServletResponse response, RsData<?> rsData, int status, ObjectMapper om) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(om.writeValueAsString(rsData));
    }

}
