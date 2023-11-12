package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "tipo_actividad")
public class TipoActividad {

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

    @NotNull(message = "Debes especificar el nombre del tipo de actividad")
    @NotBlank(message = "El nombre del tipo de actividad es obligatorio")
    @Size(min = 1, max = 50, message = "El nombre del tipo de actividad debe medir entre 1 y 50")
    private String nombre;
}
