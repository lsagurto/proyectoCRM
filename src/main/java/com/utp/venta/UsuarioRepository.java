package com.utp.venta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UsuarioRepository  extends JpaRepository<Usuario, Long> {

}
