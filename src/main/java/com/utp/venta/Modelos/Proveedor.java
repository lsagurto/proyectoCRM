package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "proveedores")
public class Proveedor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Debes especificar la razon social")
    @Size(min = 1, max = 50, message = "La razon social debe medir entre 1 y 50")
    private String razonSocial;

    @NotNull(message = "Debes especificar el email")
    @Size(min = 1, max = 50, message = "El email debe medir entre 1 y 50")
    private String email;

    @NotNull(message = "Debes especificar el tipo de documento")
    @Min(value = 0, message = "El tipo de documento debe medir entre 1 y 50")
    private String tipoDocumento;

    @NotNull(message = "Debes especificar el documento")
    @Min(value = 0, message = "El documento debe medir entre 1 y 50")
    private String documento;

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    private List<Producto> productos;

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

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

    public Proveedor() {
    }

    public Proveedor(String razonSocial, String email, String tipoDocumento, String documento) {
        this.razonSocial = razonSocial;
        this.email = email;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
    }

    public Proveedor(Integer id, String razonSocial, String email, String tipoDocumento, String documento) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.email = email;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
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
