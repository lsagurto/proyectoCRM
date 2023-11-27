package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "lead")
public class Lead implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Debes especificar el asunto del lead")
    @NotBlank(message = "El asunto del lead es obligatorio")
    @Size(min = 1, max = 50, message = "El asunto del lead debe medir entre 1 y 50")
    private String asunto_lead;

    @ManyToOne
    @JoinColumn(name = "cliente_id") // Nombre de la columna en la tabla Lead que almacena la relación con Cliente
    private Cliente cliente;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    @Column(name = "estado") // Nombre de la columna en la tabla Lead que almacena el estado
    private String estado;


    public Lead(Integer id, String asunto_lead, Cliente cliente, String estado) {
        this.id = id;
        this.asunto_lead = asunto_lead;
        this.cliente = cliente;
        this.estado = estado;
    }
    public Lead() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAsunto_lead() {
        return asunto_lead;
    }

    public void setAsunto_lead(String asunto_lead) {
        this.asunto_lead = asunto_lead;
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

}
