package com.example.ecoxchange.auth;

public class AuthUser {

    private String email;
    private String password;

    public AuthUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthUser(){}

    public AuthUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public AuthUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void login(){

    }

    public void logout(){

    }

}
