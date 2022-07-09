package com.utp.venta.Controladores;

import com.utp.venta.Repository.ClienteRepository;
import com.utp.venta.Repository.UsuarioRepository;
import com.utp.venta.Repository.VentasRepository;
import com.utp.venta.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping(path = "/Reportes")
public class ReporteVentasController {
    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    private UsuarioRepository usuariosRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping(value = "/")
    public String mostrarVentas(Model model) {
        ArrayList<Venta> ventas = (ArrayList<Venta>) ventasRepository.findAll();

//        ventas.forEach((ventita) -> {
//            Cliente clienteBuscadoPorCodigo = clienteRepository.findById(ventita.getIDCliente().toString());
//            ventita.setNumeroDocumento(clienteBuscadoPorCodigo.getNumeroDocumento());
//            Usuario usuario = usuariosRepository.findById(ventita.getVendedor().toString());
//            ventita.setVendedor(usuario.getUsername());
//        });

        model.addAttribute("ventas", ventas);
        return "Reportes/ReporteClienteXVenta";
    }
}
