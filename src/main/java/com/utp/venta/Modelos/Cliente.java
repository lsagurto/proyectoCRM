package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public Cliente() {
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Integer getidUsuario() {
        return idUsuario;
    }

    public void setidUsuario(Integer idUsuario) {
       this.idUsuario = idUsuario;
    }
    public Cliente(Integer id, String nombre, String email, String direccion, String telefono, String numeroDocumento, String tipoDocumento, Integer idUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.numeroDocumento = numeroDocumento;
        this.tipoDocumento = tipoDocumento;
        this.idUsuario = idUsuario;
    }
    public Cliente(String nombre, String email, String direccion, String telefono, String numeroDocumento, String tipoDocumento, Integer idUsuario) {
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.numeroDocumento = numeroDocumento;
        this.tipoDocumento = tipoDocumento;
        this.idUsuario = idUsuario;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Debes especificar el nombre")
    @Size(min = 1, max = 50, message = "El nombre debe medir entre 1 y 50")
    private String nombre;

    @NotNull(message = "Debes especificar el email")
    @Size(min = 1, max = 50, message = "El email debe medir entre 1 y 50")
    private String email;

    //@NotNull(message = "Debes especificar la direccion")
    //@Min(value = 0, message = "La direccion debe medir entre 1 y 50")
    private String direccion;

    //@NotNull(message = "Debes especificar el telefono")
    //@Min(value = 0, message = "La telefono debe medir entre 1 y 50")
    private String telefono;

    @NotNull(message = "Debes especificar el numero documento")
    @Min(value = 0, message = "El dni debe medir entre 1 y 50")
    private String numeroDocumento;

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

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

    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @NotNull(message = "Debes especificar el tipo de documento")
    @Min(value = 0, message = "El tipo documento debe medir entre 1 y 50")
    private String tipoDocumento;

    private Integer idUsuario;
}
