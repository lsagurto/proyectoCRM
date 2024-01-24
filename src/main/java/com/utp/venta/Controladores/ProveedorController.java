package com.utp.venta.Controladores;


import com.utp.venta.Modelos.Producto;
import com.utp.venta.Modelos.Proveedor;
import com.utp.venta.Modelos.Usuario;
import com.utp.venta.Repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Controller
@RequestMapping(path = "/proveedor")
public class ProveedorController {
    @Autowired
    private ProveedorRepository proveedorRepository;


    @GetMapping(value = "/agregar")
    public String agregarProveedor(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/agregar_proveedor";
    }

    @GetMapping(value = "/mostrar")
    public String mostrarProveedor(Model model) {
        model.addAttribute("proveedor", proveedorRepository.findAll());
        return "proveedores/ver_proveedor";
    }

    @PostMapping(value = "/eliminar")
    public String eliminarProveedor(@ModelAttribute Proveedor proveedor, RedirectAttributes redirectAttrs) {
        redirectAttrs
                .addFlashAttribute("mensaje", "Eliminado correctamente")
                .addFlashAttribute("clase", "warning");
        proveedorRepository.deleteById(proveedor.getId());
        return "redirect:/proveedor/mostrar";
    }

    // Se colocó el parámetro ID para eso de los errores, ya sé el id se puede recuperar
    // a través del modelo, pero lo que yo quiero es que se vea la misma URL para regresar la vista con
    // los errores en lugar de hacer un redirect, ya que si hago un redirect, no se muestran los errores del formulario
    // y por eso regreso mejor la vista ;)
    private Usuario obtenerUsuario(HttpServletRequest request){
        Usuario usuarios = (Usuario) request.getSession().getAttribute("usuario");
        if(usuarios == null){
            usuarios = new Usuario();
        }
        return usuarios;
    }
    @PostMapping(value = "/editar/{id}")
    public String actualizarProveedor(@PathVariable Integer id, @ModelAttribute @Valid Proveedor proveedor, BindingResult bindingResult, RedirectAttributes redirectAttrs,HttpServletRequest request) {
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        if (bindingResult.hasErrors()) {
            if (proveedor.getId() != null) {
                return "proveedores/editar_proveedor";
            }
            return "redirect:/proveedor/proveedor";
        }
        Proveedor proveedorExistente = proveedorRepository.findById(id).orElse(null);

        Proveedor posibleProveedorExistente = proveedorRepository.findByDocumento(proveedor.getDocumento());

            if (posibleProveedorExistente != null && !posibleProveedorExistente.getId().equals(proveedor.getId())) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un proveedor con ese numero de documento")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/proveedor/agregar";
        }

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        proveedor.setFechaCreacion(proveedorExistente.getFechaCreacion());

        Date fechaActual = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);

        calendar.add(Calendar.HOUR_OF_DAY, -5);

        Date nuevaFechaModificacion = calendar.getTime();
        proveedor.setUsuario_creacion(usuarioBuscadoPorCodigo.getUsername());
        proveedor.setUsuario_modificacion(usuarioBuscadoPorCodigo.getUsername());
        //proveedor.setFechaCreacion(proveedor.getFechaCreacion());
        proveedor.setFechaModificacion(nuevaFechaModificacion);

        proveedorRepository.save(proveedor);
        redirectAttrs
                .addFlashAttribute("mensaje", "Editado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/proveedor/mostrar";
    }

    @GetMapping(value = "/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("proveedor", proveedorRepository.findById(id).orElse(null));
        return "proveedores/editar_proveedor";
    }

    @PostMapping(value = "/agregar")
    public String guardarProveedor(@ModelAttribute @Valid Proveedor proveedor, BindingResult bindingResult, RedirectAttributes redirectAttrs,HttpServletRequest request) {
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        if (bindingResult.hasErrors()) {
            return "proveedores/agregar_proveedor";
        }
        if (proveedorRepository.findByDocumento(proveedor.getDocumento()) != null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un proveedor con ese numero de documento")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/proveedor/agregar";
        }
        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clase", "success");

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Date fechaActual = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);
        proveedor.setUsuario_creacion(usuarioBuscadoPorCodigo.getUsername());
        calendar.add(Calendar.HOUR_OF_DAY, -5);

        Date nuevaFechaCreacion = calendar.getTime();

        proveedor.setFechaCreacion(nuevaFechaCreacion);

        proveedorRepository.save(proveedor);

        return "redirect:/proveedor/agregar";
    }
}
