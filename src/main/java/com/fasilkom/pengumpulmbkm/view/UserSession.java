package com.fasilkom.pengumpulmbkm.view;

public class UserSession {
    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        UserSession.token = token;
    }
}
