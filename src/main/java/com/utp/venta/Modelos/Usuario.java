package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRol() {
        return rol;
    }

    public void setRol(Integer rol) {
        this.rol = rol;
    }


    public Usuario(Integer id, String nombre, String email, String username, String password, Integer rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Usuario(String nombre, String email, String username, String password, Integer rol) {
        this.nombre = nombre;
        this.email = email;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Usuario() {
    }


    @NotNull(message = "Debes especificar el nombre")
    @Size(min = 1, max = 50, message = "El nombre debe medir entre 1 y 50")
    private String nombre;

    @NotNull(message = "Debes especificar el email")
    @Size(min = 1, max = 50, message = "El email debe medir entre 1 y 50")
    private String email;

    @NotNull(message = "Debes especificar el usuario")
   // @Min(value = 0, message = "El usuario debe medir entre 1 y 50")
    private String username;

    @NotNull(message = "Debes especificar la contraseña")
   // @Min(value = 0, message = "La contraseña debe medir entre 1 y 50")
    private String password;


    private Integer rol;
}
/*
    public String getUsuario_creacion() {
        return usuario_creacion;

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
*/
//}
