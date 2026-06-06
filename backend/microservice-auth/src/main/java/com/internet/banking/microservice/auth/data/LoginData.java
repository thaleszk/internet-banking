package com.internet.banking.microservice.auth.data;

import com.fasterxml.jackson.annotation.JsonAlias;

public class LoginData {

    @JsonAlias("login")
    private String username;

    @JsonAlias("senha")
    private String password;

    public LoginData() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
