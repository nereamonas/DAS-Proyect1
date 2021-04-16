package com.example.das_proyect1.helpClass;

public class ImagenFirebase {
    //Clase adaptador para las imagenes subidas a firebase que se mostraran en una recyclerview+cardview. se guardara la url de la imagen y el nombre

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
