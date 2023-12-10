package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class ProductSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Float cantidad;

    private Float precio;

    @Column(name = "ingreso")
    private Float ingreso;

    @Column(name = "ingreso_with_igv")
    private Float ingreso_with_igv;

    @ManyToOne
    @JoinColumn(name = "oportunidad_id")
    private Opportunity oportunidad;

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Opportunity getOportunidad() {
        return oportunidad;
    }

    public void setOportunidad(Opportunity oportunidad) {
        this.oportunidad = oportunidad;
    }

    public Float getIngreso() {
        return ingreso;
    }

    public void setIngreso(Float ingreso) {
        this.ingreso = ingreso;
    }

    public Float getIngreso_with_igv() {
        return ingreso_with_igv;
    }

    public void setIngreso_with_igv(Float ingreso_with_igv) {
        this.ingreso_with_igv = ingreso_with_igv;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }
}
