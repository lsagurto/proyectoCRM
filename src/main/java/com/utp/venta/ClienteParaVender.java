package com.utp.venta;

import com.utp.venta.Modelos.Cliente;

public class ClienteParaVender extends Cliente {
    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    //    private Float cantidad;
private String fechaEmision;
private String vendedor;

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    private Integer idVendedor;
    public ClienteParaVender(Integer id, String nombre, String email, String direccion, String telefono, String numeroDocumento, String tipoDocumento, String vendedor, String FechaEmision) {

    }

    public ClienteParaVender(String nombre, String email, String direccion, String telefono, String numeroDocumento, String tipoDocumento) {

    }
    public ClienteParaVender(){

    }
//
//    public void aumentarCantidad() {
//        this.cantidad++;
//    }
//
//    public void aumentarCantidad2() {
//        this.cantidad--;
//    }
//
//    public Float getCantidad() {
//        return cantidad;
//    }
//
//    public Float getTotal() {
//        return this.getPrecio() * this.cantidad;
//    }
}
