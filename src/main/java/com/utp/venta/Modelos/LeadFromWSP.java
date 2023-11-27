package com.utp.venta.Modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "LeadFromWSP")
public class LeadFromWSP implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dni")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    @Pattern(regexp = "\\d+", message = "El DNI debe contener solo dígitos")
    private String dni;

    @Column(name = "nom_cliente")
    private String nom_cliente;

    @Column(name = "email") // Nombre de la columna en la tabla Lead que almacena el estado
    private String email;

    @NotNull(message = "Debes especificar el asunto del lead")
    @NotBlank(message = "El asunto del lead es obligatorio")
    @Size(min = 1, max = 50, message = "El asunto del lead debe medir entre 1 y 50")
    private String asunto_lead;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "lead_id")
    private Lead lead_id;

    public Lead getLead_id() {
        return lead_id;
    }

    public void setLead_id(Lead lead_id) {
        this.lead_id = lead_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom_cliente() {
        return nom_cliente;
    }

    public void setNom_cliente(String nom_cliente) {
        this.nom_cliente = nom_cliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAsunto_lead() {
        return asunto_lead;
    }

    public void setAsunto_lead(String asunto_lead) {
        this.asunto_lead = asunto_lead;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }


}
