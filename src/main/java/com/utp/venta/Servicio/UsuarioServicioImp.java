package com.utp.venta.Servicio;

import com.utp.venta.Rol;
import com.utp.venta.Usuario;
import com.utp.venta.UsuarioRegistroDTO;
import com.utp.venta.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UsuarioServicioImp implements UsuarioServicio{

    private UsuarioRepository usuarioRepository;

    public UsuarioServicioImp(UsuarioRepository usuarioRepository) {
        super();
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario guardar(UsuarioRegistroDTO registroDTO) {
        Usuario usuario=new Usuario(registroDTO.getNombre(),registroDTO.getApellido(),registroDTO.getEmail(),registroDTO.getPassword(), Arrays.asList(new Rol("ROLE_USER")));
        return usuarioRepository.save(usuario);
    }
}
