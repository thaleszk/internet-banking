package com.internet.banking.microservice.auth.model;

public class UserModel {

    private String id;
    private String login;
    private String password;
    private com.internet.banking.microservice.auth.model.UserType type;

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public com.internet.banking.microservice.auth.model.UserType getType() {
        return type;
    }

    public void setType(com.internet.banking.microservice.auth.model.UserType type) {
        this.type = type;
    }
}