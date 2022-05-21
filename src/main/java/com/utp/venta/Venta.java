package com.utp.venta.Modelos;

import com.utp.venta.Modelos.ProductoVendido;
import com.utp.venta.Utilitarios.*;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public   Integer id;
    public String fechaYHora;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    public Set<ProductoVendido> productos;

    public Venta() {
        this.fechaYHora = Utiles.obtenerFechaYHoraActual();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getTotal() {
        Float total = 0f;
        for (ProductoVendido productoVendido : this.productos) {
            total += productoVendido.getTotal();
        }
        return total;
    }

    public String getFechaYHora() {
        return fechaYHora;
    }

    public void setFechaYHora(String fechaYHora) {
        this.fechaYHora = fechaYHora;
    }

    public Set<ProductoVendido> getProductos() {
        return productos;
    }

    public void setProductos(Set<ProductoVendido> productos) {
        this.productos = productos;
    }
}
