package com.utp.venta.Servicio;

import com.utp.venta.UsuarioRegistroDTO;
import com.utp.venta.Usuario;

public interface UsuarioServicio  {
    public Usuario guardar (UsuarioRegistroDTO registroDTO);
}
