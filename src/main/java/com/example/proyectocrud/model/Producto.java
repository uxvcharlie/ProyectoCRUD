package com.example.proyectocrud.model;

import java.time.LocalDate;

public class Producto {

    private int idProducto;
    private String codigo;

    public Producto(int idProducto, String codigo, String descripcion, String nombre, Double precio, int stock, LocalDate fechaCreacion, int idCategoria, String categoria, int idMarca, String marca, int estado) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.fechaCreacion = fechaCreacion;
        this.idCategoria = idCategoria;
        this.categoria = categoria;
        this.idMarca = idMarca;
        this.marca = marca;
        this.estado = estado;
    }

    private String descripcion;

    public Producto() {
    }

    private String nombre;
    private Double precio;
    private int stock;
    private LocalDate fechaCreacion;

    private int idCategoria;
    private String categoria;

    private int idMarca;
    private String marca;

    private int estado;
}
