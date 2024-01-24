package com.utp.venta.Modelos;

import com.utp.venta.Venta;

import javax.persistence.*;

@Entity
public class ProductoVendido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Float cantidad, precio;
    private String nombre, codigo;
    @ManyToOne
    @JoinColumn
    private Venta venta;

    public ProductoVendido(Float cantidad, Float precio, String nombre, String codigo, Venta venta) {
        this.cantidad = cantidad;
        this.precio = precio;
        this.nombre = nombre;
        this.codigo = codigo;
        this.venta = venta;
    }

    public ProductoVendido() {
    }

    public Float getTotal() {
        return this.cantidad * this.precio;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getUsuario_creacion() {
        return usuario_creacion;
    }

    public void setUsuario_creacion(String usuario_creacion) {
        this.usuario_creacion = usuario_creacion;
    }

    public String getUsuario_modificacion() {
        return usuario_modificacion;
    }

    public void setUsuario_modificacion(String usuario_modificacion) {
        this.usuario_modificacion = usuario_modificacion;
    }

    @Column(name = "usuario_creacion")
    private String usuario_creacion;


    @Column(name = "usuario_modificacion")
    private String usuario_modificacion;

}
