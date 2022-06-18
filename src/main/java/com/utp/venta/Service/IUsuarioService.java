package com.utp.venta.Service;

import com.utp.venta.Modelos.Usuario;

public interface IUsuarioService {

    public Usuario findByUsername(String username);
    public Usuario registrar(Usuario u);
}
