package com.utp.venta.Repository;

import com.utp.venta.Modelos.Lead;
import com.utp.venta.Modelos.LeadFromWSP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Integer> {
    List<Lead> findByClienteNumeroDocumento(String numeroDocumento);
    List<Lead> findByOpportunityId(Integer opportunityId);

}
