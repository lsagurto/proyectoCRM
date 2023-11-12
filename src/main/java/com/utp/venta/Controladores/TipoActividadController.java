package com.utp.venta.Controladores;

import com.utp.venta.Repository.TipoActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Controller
@RequestMapping(path = "/tipo_activdad")
public class TipoActividadController {
    @Autowired
    private TipoActividadRepository tipoActividadRepository;

    @GetMapping(value = "/show")
    public String showTipoActividad(Model model) {
        model.addAttribute("tipo_actividad", tipoActividadRepository.findAll());
        return "tipo_actividad/show_tipo_actividad";
    }
}
