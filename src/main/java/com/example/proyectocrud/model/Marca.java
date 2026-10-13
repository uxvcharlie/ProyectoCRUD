package com.example.proyectocrud.model;

public class Marca {

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Marca() {
    }

    public Marca(int idMarca, String nombre) {
        this.idMarca = idMarca;
        this.nombre = nombre;
    }

    private int idMarca;
    private String nombre;

    /*
     * El ComboBox usa toString() para mostrar cada elemento
     * */
    @Override
    public String toString() {
        return nombre;
    }
}
