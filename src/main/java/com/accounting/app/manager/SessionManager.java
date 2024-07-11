package com.accounting.app.manager;

public class SessionManager {
    private static int userId;

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int id) {
        userId = id;
    }
}

