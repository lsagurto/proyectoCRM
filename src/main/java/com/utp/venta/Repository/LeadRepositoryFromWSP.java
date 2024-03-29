package com.utp.venta.Repository;

import com.utp.venta.Modelos.Lead;
import com.utp.venta.Modelos.LeadFromWSP;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepositoryFromWSP extends CrudRepository<LeadFromWSP, Integer> {
    LeadFromWSP findByDni(String dni);

    @Query("SELECT lw FROM LeadFromWSP lw WHERE lw.lead_id = :lead")
    LeadFromWSP findByLead(@Param("lead") Lead lead);

    //LeadFromWSP findByLead_id(Integer leadId);


}
