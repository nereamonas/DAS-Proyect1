package com.example.das_proyect1.helpClass;

public class ImagenFirebase {

    private String nombre;
    private String url;

    public ImagenFirebase(String nombre, String url) {
        this.nombre = nombre;
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
