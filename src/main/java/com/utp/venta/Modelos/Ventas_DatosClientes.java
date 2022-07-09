package com.utp.venta.Modelos;
import com.utp.venta.Venta;

import javax.persistence.*;

@Entity
public class Ventas_DatosClientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String razonSocial, tipoDocumento, numeroDocumento, Direccion;
    @OneToOne
    @JoinColumn
    private Venta venta;

    public Ventas_DatosClientes(String razonSocial, String tipoDocumento, String numeroDocumento, String Direccion, Venta venta) {
        this.razonSocial = razonSocial;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.Direccion = Direccion;
        this.venta = venta;
    }

    public Ventas_DatosClientes() {
    }
    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }


}
