package com.utp.venta.Repository;

import com.utp.venta.Modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

   //public Usuario findFirstByUsuario(String usuario);
    public Usuario findByUsername(String username);
    public Usuario findById(String username);
}
