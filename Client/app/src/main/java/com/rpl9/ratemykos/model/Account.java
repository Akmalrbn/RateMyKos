package com.rpl9.ratemykos.model;


public class Account {
    private int kos_id;
    private String email;
    private String username;
    private User_type usertype;

    public int getID() {
        return kos_id;
    }
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public User_type getUser_type(){ return usertype; }
}