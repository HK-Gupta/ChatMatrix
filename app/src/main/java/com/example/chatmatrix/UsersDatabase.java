package com.example.chatmatrix;

import android.net.Uri;

public class UsersDatabase {
    private String profilePic, userEmail, userName, password, userId, lastMessage, userStatus;

    public UsersDatabase() {}
    public UsersDatabase(String userId, String userEmail, String userName, String password, String profilePic, String userStatue) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.password = password;
        this.profilePic = profilePic;
        this.userStatus = userStatue;

    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String status) {
        this.userStatus = status;
    }
}
