package com.utp.venta.Repository;

import com.utp.venta.Modelos.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, Integer> {

}
