package com.utp.venta.Repository;

import com.utp.venta.Modelos.Actividad;
import com.utp.venta.Modelos.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {
    List<Actividad> findByOportunidad(Opportunity opportunity);
}
