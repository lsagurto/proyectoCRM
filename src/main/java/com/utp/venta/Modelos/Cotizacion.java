package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "cotizacion")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Debes especificar el asunto de la cotizacion")
    @NotBlank(message = "El asunto de la cotizacion es obligatorio")
    @Size(min = 1, max = 50, message = "El asunto de la cotizacion debe medir entre 1 y 50")
    private String asunto_cotizacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fecha_inicio;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fecha_fin;

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public Opportunity getOportunidad() {
        return oportunidad;
    }

    public void setOportunidad(Opportunity oportunidad) {
        this.oportunidad = oportunidad;
    }

    @ManyToOne
    @JoinColumn(name = "oportunidad_id")
    private Opportunity oportunidad;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAsunto_cotizacion() {
        return asunto_cotizacion;
    }

    public void setAsunto_cotizacion(String asunto_cotizacion) {
        this.asunto_cotizacion = asunto_cotizacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }


}
