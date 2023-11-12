package com.utp.venta;

import com.utp.venta.Modelos.Producto;

import java.text.DecimalFormat;

public class ProductoParaVender extends Producto {
    private Float cantidad;
    private Float ingresoConIGV;

    public Float getIngresoConIGV() {
        return ingresoConIGV;
    }

    public void setIngresoConIGV(Float ingresoConIGV) {
        this.ingresoConIGV = ingresoConIGV;
    }

    public void formatearIngresoConIGV(DecimalFormat decimalFormat) {
        if (this.getIngresoConIGV() != null) {
            Float ingresoConIGV = Float.parseFloat(decimalFormat.format((double) this.getIngresoConIGV()));
            this.setIngresoConIGV(ingresoConIGV);
        }
    }

    public ProductoParaVender(String nombre, String codigo, Float precio, Float existencia, Float ingreso, Integer id, Float cantidad) {
        super(nombre, codigo, precio, existencia, ingreso, id);
        this.cantidad = cantidad;
    }
    public ProductoParaVender(String nombre, String codigo, Float precio, Float existencia, Integer id, Float cantidad) {
        super(nombre, codigo, precio, existencia, id);
        this.cantidad = cantidad;
    }

    public ProductoParaVender(String nombre, String codigo, Float precio, Float existencia, Float cantidad) {
        super(nombre, codigo, precio, existencia);
        this.cantidad = cantidad;
    }

    public ProductoParaVender(Producto producto, Float cantidad) {
        super(producto.getNombre(), producto.getCodigo(), producto.getPrecio(), producto.getExistencia(), producto.getIngreso(), producto.getId());
        this.cantidad = cantidad;
    }

    public void actualizarIngresoConIGV(DecimalFormat decimalFormat) {
        this.ingresoConIGV = this.getPrecio() * this.cantidad * 1.18f;
    }
    public ProductoParaVender(){

    }

    public void aumentarCantidad() {
        this.cantidad++;
    }

    public void aumentarCantidad2() {
        this.cantidad--;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public Float getTotal() {
        return this.getPrecio() * this.cantidad;
    }
}
