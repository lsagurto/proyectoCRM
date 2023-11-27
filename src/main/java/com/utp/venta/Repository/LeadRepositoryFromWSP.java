package com.utp.venta.Repository;

import com.utp.venta.Modelos.Lead;
import com.utp.venta.Modelos.LeadFromWSP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepositoryFromWSP extends CrudRepository<LeadFromWSP, Long> {
    LeadFromWSP findByDni(String dni);
}
