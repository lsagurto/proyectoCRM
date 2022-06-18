package com.utp.venta.Service;

import com.utp.venta.Modelos.Usuario;
import com.utp.venta.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImp implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepDao;  // UsuarioRepository = IUsuarioDAO
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepDao.findByUsername(username);
    }

    @Override
    public Usuario registrar(Usuario u) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return usuarioRepDao.save(u);
    }
}
