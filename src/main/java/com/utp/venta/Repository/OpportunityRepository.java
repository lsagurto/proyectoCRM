package com.utp.venta.Repository;

import com.utp.venta.Modelos.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityRepository extends JpaRepository<Opportunity, Integer> {

}
