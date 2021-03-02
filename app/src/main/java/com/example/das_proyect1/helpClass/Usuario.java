package com.example.das_proyect1.helpClass;

public class Usuario {
    String user;
    String email;
    String pass;

    public Usuario(String user, String email, String pass) {
        this.user = user;
        this.email = email;
        this.pass = pass;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "user='" + user + '\'' +
                ", email='" + email + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }
}
