package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "actividad")
public class Actividad implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Debes especificar el asunto de la actividad")
    @NotBlank(message = "El asunto de la actividad es obligatorio")
    @Size(min = 1, max = 50, message = "El asunto de la actividad debe medir entre 1 y 50")
    private String asunto_actividad;

    public String getAsunto_actividad() {
        return asunto_actividad;
    }

    public void setAsunto_actividad(String asunto_actividad) {
        this.asunto_actividad = asunto_actividad;
    }

    @ManyToOne
    @JoinColumn(name = "oportunidad_id")
    private Opportunity oportunidad;


    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "tipo_actividad_id")
    private TipoActividad tipoActividad;

    @Column(name = "note_activity")
    private String note_activity;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Opportunity getOportunidad() {
        return oportunidad;
    }

    public void setOportunidad(Opportunity oportunidad) {
        this.oportunidad = oportunidad;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNote_activity() {
        return note_activity;
    }

    public void setNote_activity(String note_activity) {
        this.note_activity = note_activity;
    }
}
