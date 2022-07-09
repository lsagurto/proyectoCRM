package com.utp.venta.Controladores;

import com.utp.venta.Modelos.Usuario;
import com.utp.venta.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuariosController {
    @Autowired
    private UsuarioRepository usuariosRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping(value = "/agregar")
    public String agregarusuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/agregar_usuario";
    }

    @GetMapping(value = "/mostrar")
    public String mostrarUsuarios(Model model) {
        model.addAttribute("usuario", usuariosRepository.findAll());
        return "usuarios/ver_usuario";
    }

    @PostMapping(value = "/eliminar")
    public String eliminarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttrs) {
        redirectAttrs
                .addFlashAttribute("mensaje", "Eliminado correctamente")
                .addFlashAttribute("clase", "warning");
        usuariosRepository.deleteById(usuario.getId());
        return "redirect:/usuarios/mostrar";
    }

    // Se colocó el parámetro ID para eso de los errores, ya sé el id se puede recuperar
    // a través del modelo, pero lo que yo quiero es que se vea la misma URL para regresar la vista con
    // los errores en lugar de hacer un redirect, ya que si hago un redirect, no se muestran los errores del formulario
    // y por eso regreso mejor la vista ;)
    @PostMapping(value = "/editar/{id}")
    public String actualizarUsuario(@ModelAttribute @Valid Usuario usuario, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {

            if (usuario.getId() != null) {
                return "usuarios/editar_usuario";
            }
            return "redirect:/usuarios/mostrar";
        }
        Usuario posibleUsuarioExistente = usuariosRepository.findByUsername(usuario.getUsername());

            if (posibleUsuarioExistente != null && !posibleUsuarioExistente.getId().equals(usuario.getId())) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un usuario con ese usuario")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/usuarios/agregar";
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuariosRepository.save(usuario);
        redirectAttrs
                .addFlashAttribute("mensaje", "Editado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/usuarios/mostrar";
    }

    @GetMapping(value = "/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Usuario user = usuariosRepository.findById(id).orElse(null);
        user.setPassword("****************");
        model.addAttribute("usuario", user);
        return "usuarios/editar_usuario";
    }


    @PostMapping(value = "/agregar")
    public String guardarUsuario(@ModelAttribute @Valid Usuario usuario, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "usuarios/agregar_usuario";
        }
        if (usuariosRepository.findByUsername(usuario.getUsername()) != null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un usuario con ese código")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/usuarios/agregar";
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuariosRepository.save(usuario);
        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/usuarios/agregar";
    }
}
