package com.utp.venta.Controladores;

import com.utp.venta.Modelos.*;
import com.utp.venta.Repository.ActividadRepository;
import com.utp.venta.Repository.ClienteRepository;
import com.utp.venta.Repository.OpportunityRepository;
import com.utp.venta.Repository.TipoActividadRepository;
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
@RequestMapping(path = "/actividad")
public class ActividadController {

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private TipoActividadRepository tipoActividadRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping(value = "/add/{opportunityId}")
    public String addLead(@PathVariable Integer opportunityId, Model model) {
        List<TipoActividad> tipoActividad = tipoActividadRepository.findAll();

        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElse(null);
        if (opportunity == null) {
            return "redirect:/opportunity/show";
        }
        model.addAttribute("actividad", new Actividad());
        model.addAttribute("tipoActividadList", tipoActividad);
        model.addAttribute("tipoActividad", null);
        model.addAttribute("opportunity", opportunity);


        return "actividad/add_actividad";
    }

    @GetMapping(value = "/editar/{id}")
    public String showFormEdit(@PathVariable int id, Model model) {
        Actividad actividad = actividadRepository.findById(id).orElse(null);
        model.addAttribute("actividad", actividad);

        if (actividad != null) {
            Opportunity opportunity = actividad.getOportunidad();
            Cliente cliente = actividad.getCliente();
            TipoActividad tipoActividad = actividad.getTipoActividad(); // Obtener el tipo de actividad

            model.addAttribute("cliente", cliente);
            model.addAttribute("opportunity", opportunity);
            model.addAttribute("tipoActividad", tipoActividad); // Agregar el tipo de actividad al modelo

        }

        List<Cliente> clientes = clienteRepository.findAll();
        List<TipoActividad> tiposDeActividad = tipoActividadRepository.findAll(); // Obtener todos los tipos de actividad
        model.addAttribute("clientes", clientes);
        model.addAttribute("tiposDeActividad", tiposDeActividad); // Agregar la lista de tipos de actividad al modelo


        return "actividad/edit_actividad";
    }

    @PostMapping(value = "/editar/{id}")
    public String actualizarActividad(@PathVariable int id, @ModelAttribute @Valid Actividad actividad, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        Actividad actividadExistente = actividadRepository.findById(id).orElse(null);

        if (actividadExistente != null) {
            Opportunity opportunity = actividadExistente.getOportunidad(); // Obtener la oportunidad a través de la relación

            actividadExistente.setAsunto_actividad(actividad.getAsunto_actividad());
            actividadExistente.setCliente(actividad.getCliente());
            actividadExistente.setEstado(actividad.getEstado());
            actividadExistente.setTipoActividad(actividad.getTipoActividad());
            actividadExistente.setNote_activity(actividad.getNote_activity());

            actividadRepository.save(actividadExistente);

            redirectAttrs
                    .addFlashAttribute("mensaje", "Editado correctamente")
                    .addFlashAttribute("clase", "success");


            return "redirect:/opportunity/detail_opportunity/" + opportunity.getId();

        }else{
            return "redirect:/error";
        }

    }

    @PostMapping(value = "/save/{id}")
    public String saveActividad (@PathVariable int id,@ModelAttribute("actividad") @Valid Actividad actividad, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Falta completar los datos")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/add";
        }
        Opportunity opportunity = opportunityRepository.findById(id).orElse(null);
        if (opportunity == null) {
            return "redirect:/opportunity/show";
        }
        actividad.setOportunidad(opportunity);
        actividad.setEstado("Activo");

        actividad.setCliente(opportunity.getCliente());

        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clase", "success");

        actividadRepository.save(actividad);
        return "redirect:/opportunity/detail_opportunity/" + opportunity.getId();

    }
}
