package com.example.proyectocrud.model;

public class Categoria {

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria() {
    }

    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
    }

    private int idCategoria;
    private String nombre;

    /*
     * El ComboBox usa toString() para mostrar cada elemento
     * */
    @Override
    public String toString() {
        return nombre;
    }
}
