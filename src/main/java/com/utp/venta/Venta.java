package com.utp.venta;

import com.utp.venta.Modelos.ProductoVendido;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fechaYHora;
    private Integer IDCliente;
    private String Nombre;
    private String vendedor;
    private Integer IDUsuario;
    public Integer getIDCliente() {
        return IDCliente;
    }
    public String getNombre() {
        return Nombre;
    }

    public void setNumeroDocumento(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }



    public void setIDCliente(Integer IDCliente) {
        this.IDCliente = IDCliente;
    }

    public Integer getIDUsuario() {
        return IDUsuario;
    }

    public void setIDUsuario(Integer IDUsuario) {
        this.IDUsuario = IDUsuario;
    }


    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private Set<ProductoVendido> productos;

    public Venta(Integer IDCliente, Integer idUsuario, String Nombre, String vendedor ) {
        this.fechaYHora = Utiles.obtenerFechaYHoraActual();
        this.IDCliente = IDCliente;
        this.IDUsuario = idUsuario;
        this.Nombre = Nombre;
        this.vendedor = vendedor;
    }
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

    public void setDatosCliente(Set<ProductoVendido> productos) {
        this.productos = productos;
    }
}
