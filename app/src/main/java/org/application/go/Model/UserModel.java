package org.application.go.Model;

public class UserModel {
    private String userUid;
    private String userEmail;
    private String userPassword;
    private String userName;
    private String userGoLevel;

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGoLevel() {
        return userGoLevel;
    }

    public void setUserGoLevel(String userGoLevel) {
        this.userGoLevel = userGoLevel;
    }
}
