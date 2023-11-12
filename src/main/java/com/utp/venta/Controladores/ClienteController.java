package com.utp.venta.Controladores;

import com.utp.venta.Modelos.Cliente;
import com.utp.venta.Modelos.Usuario;
import com.utp.venta.Repository.UsuarioRepository;
import com.utp.venta.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Controller
@RequestMapping(path = "/cliente")
public class ClienteController {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuariosRepository;

    private Usuario obtenerUsuario(HttpServletRequest request){
        Usuario usuarios = (Usuario) request.getSession().getAttribute("usuario");
        if(usuarios == null){
            usuarios = new Usuario();
        }
        return usuarios;
    }

    @GetMapping(value = "/agregar")
    public String agregarcliente(Model model) {
        model.addAttribute("cliente", new Cliente());

        return "cliente/agregar_cliente";
    }

    @GetMapping(value = "/mostrar")
    public String mostrarCliente(Model model,  HttpServletRequest request ){
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        if(usuarioBuscadoPorCodigo.getRol() != 1) {
            model.addAttribute("cliente", clienteRepository.findByIdUsuario(usuarioBuscadoPorCodigo.getId()));
        }
        else
        {
            model.addAttribute("cliente", clienteRepository.findAll());
        }
        return "cliente/ver_cliente";
    }

    @PostMapping(value = "/eliminar")
    public String eliminarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttrs) {
        redirectAttrs
                .addFlashAttribute("mensaje", "Eliminado correctamente")
                .addFlashAttribute("clase", "warning");
        clienteRepository.deleteById(cliente.getId());
        return "redirect:/cliente/mostrar";
    }

    // Se colocó el parámetro ID para eso de los errores, ya sé el id se puede recuperar
    // a través del modelo, pero lo que yo quiero es que se vea la misma URL para regresar la vista con
    // los errores en lugar de hacer un redirect, ya que si hago un redirect, no se muestran los errores del formulario
    // y por eso regreso mejor la vista ;)
    @PostMapping(value = "/editar/{id}")
    public String actualizarCliente(@ModelAttribute @Valid Cliente cliente, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            if (cliente.getId() != null) {
                return "cliente/editar_cliente";
            }
            return "redirect:/cliente/cliente";
        }
        Cliente posibleClienteExistente = clienteRepository.findByNumeroDocumento(cliente.getNumeroDocumento());

            if (posibleClienteExistente != null && !posibleClienteExistente.getId().equals(cliente.getId())) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un cliente con ese numero de documento")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/agregar";
        }

        clienteRepository.save(cliente);
        redirectAttrs
                .addFlashAttribute("mensaje", "Editado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/cliente/mostrar";
    }

    @GetMapping(value = "/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("cliente", clienteRepository.findById(id).orElse(null));
        return "CLIENTE/editar_cliente";
    }

    @PostMapping(value = "/agregar")
    public String guardarCliente(@ModelAttribute @Valid Cliente cliente, BindingResult bindingResult, RedirectAttributes redirectAttrs,HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "cliente/agregar_cliente";
        }
        if (clienteRepository.findByNumeroDocumento(cliente.getNumeroDocumento()) != null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un Cliente con ese numero de documento")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/agregar";
        }
        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clasxe", "success");
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        cliente.setidUsuario(usuarioBuscadoPorCodigo.getId());
        clienteRepository.save(cliente);
        return "redirect:/cliente/agregar";
    }
}
