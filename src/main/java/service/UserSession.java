package service;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

    public class UserSession {

    private static volatile UserSession instance;

    private String userName;
    private String password;
    private String privileges;

    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;

        Preferences userPreferences = Preferences.userRoot();
        userPreferences.put("USERNAME", userName);
        userPreferences.put("PASSWORD", password);
        userPreferences.put("PRIVILEGES", privileges);
    }

    public static UserSession getInstance(String userName, String password, String privileges) {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession(userName, password, privileges);
                }
            }
        }
        return instance;
    }

    public static UserSession getInstance(String userName, String password) {
        return getInstance(userName, password, "NONE");
    }

    public static boolean validateUser(String username, String password) {
        Preferences prefs = Preferences.userRoot();
        String storedPassword = prefs.get("USER_" + username, null);
        return storedPassword != null && storedPassword.equals(password);
    }

    public synchronized String getUserName() {
        return this.userName;
    }

    public synchronized String getPassword() {
        return this.password;
    }

    public synchronized String getPrivileges() {
        return this.privileges;
    }

    public synchronized void cleanUserSession() {
        this.userName = ""; // or null
        this.password = "";
        this.privileges = ""; // or null
    }

    @Override
    public synchronized String toString() {
        return "UserSession{" +
                "userName='" + userName + '\'' +
                ", privileges=" + privileges +
                '}';
    }
}
