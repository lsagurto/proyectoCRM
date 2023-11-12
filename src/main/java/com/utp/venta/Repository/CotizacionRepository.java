package com.utp.venta.Repository;

import com.utp.venta.Modelos.Cotizacion;
import com.utp.venta.Modelos.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {

    List<Cotizacion> findByOportunidad(Opportunity opportunity);

}
