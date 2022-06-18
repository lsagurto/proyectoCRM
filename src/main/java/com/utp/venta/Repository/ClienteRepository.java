package com.utp.venta.Repository;

import com.utp.venta.Modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

   //public Usuario findFirstByUsuario(String usuario);
    public Cliente findByNumeroDocumento (String numeroDocumento);
}
