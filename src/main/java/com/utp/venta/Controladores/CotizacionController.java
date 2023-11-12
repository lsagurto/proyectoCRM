package com.utp.venta.Controladores;

import com.utp.venta.Modelos.Actividad;
import com.utp.venta.Modelos.Cotizacion;
import com.utp.venta.Modelos.Opportunity;
import com.utp.venta.Modelos.TipoActividad;
import com.utp.venta.Repository.ActividadRepository;
import com.utp.venta.Repository.CotizacionRepository;
import com.utp.venta.Repository.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Validated
@Controller
@RequestMapping(path = "/cotizacion")
public class CotizacionController {
    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @GetMapping(value = "/add/{opportunityId}")
    public String addCotizacion(@PathVariable Integer opportunityId, Model model) {

        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElse(null);
        if (opportunity == null) {
            return "redirect:/opportunity/show";
        }
        model.addAttribute("cotizacion", new Cotizacion());
        model.addAttribute("opportunity", opportunity);


        return "cotizacion/add_cotizacion";
    }

    @PostMapping(value = "/save/{id}")
    public String saveCotizacion (@PathVariable int id, @ModelAttribute("cotizacion") @Valid Cotizacion cotizacion, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Falta completar los datos")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cotizacion/add_cotizacion";
        }
        Opportunity opportunity = opportunityRepository.findById(id).orElse(null);
        if (opportunity == null) {
            return "redirect:/opportunity/show";
        }
        cotizacion.setOportunidad(opportunity);

        cotizacion.setCliente(opportunity.getCliente());

        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clase", "success");

        cotizacionRepository.save(cotizacion);
        System.out.println("Cotizacion guardada: " + cotizacion.toString());

        return "redirect:/opportunity/detail_opportunity/" + opportunity.getId();

    }
}
