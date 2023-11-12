package com.utp.venta.Modelos;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "opportunity")
public class Opportunity implements Serializable {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
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

    public Float getTotalBruto() {
        return TotalBruto;
    }

    public void setTotalBruto(Float totalBruto) {
        TotalBruto = totalBruto;
    }

    public Float getTotalNeto() {
        return TotalNeto;
    }

    public void setTotalNeto(Float totalNeto) {
        TotalNeto = totalNeto;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "oportunidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Actividad> actividades;

    @OneToMany(mappedBy = "oportunidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos;

    @NotNull(message = "Debes especificar el asunto de la oportunidad")
    @NotBlank(message = "El asunto de la oportunidad es obligatorio")
    @Size(min = 1, max = 50, message = "El asunto de la oportunidad debe medir entre 1 y 50")
    private String asunto;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "estado") // Nombre de la columna en la tabla Lead que almacena el estado
    private String estado;

    @Min(value = 0, message = "El total Bruto mínimo es 0")
    private Float TotalBruto;

    @Min(value = 0, message = "El total Neto mínimo es 0")
    private Float TotalNeto;
}
