package com.utp.venta.Controladores;

import com.utp.venta.Modelos.Cliente;
import com.utp.venta.Modelos.Lead;
import com.utp.venta.Modelos.Opportunity;
import com.utp.venta.Modelos.Producto;
import com.utp.venta.Repository.ClienteRepository;
import com.utp.venta.Repository.LeadRepository;
import com.utp.venta.Repository.OpportunityRepository;
import com.utp.venta.Service.ServiceLead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Controller
@RequestMapping(path = "/lead")
public class LeadController {
    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServiceLead serviceLead;

    @PostMapping("/crear")
    public ResponseEntity<Lead> crearLeadService(@RequestParam String nombre, @RequestParam Cliente cliente) {
        Lead nuevoLead = serviceLead.crearLead(nombre, cliente);
        return new ResponseEntity<>(nuevoLead, HttpStatus.CREATED);
    }

    @GetMapping(value = "/add")
    public String addLead(Model model) {
        List<Cliente> clientes = clienteRepository.findAll();
        List<Lead> leads = leadRepository.findAll();

        /*List<Cliente> clientesDisponibles = clientes.stream()
                .filter(cliente -> !leads.stream().anyMatch(lead -> lead.getCliente().equals(cliente)))
                .collect(Collectors.toList()); */

        model.addAttribute("lead", new Lead());
        model.addAttribute("clientes", clientes);
        return "lead/add_lead";
    }

    @GetMapping(value = "/show")
    public String showLead(Model model) {
        model.addAttribute("lead", leadRepository.findAll());
        return "lead/show_lead";
    }

    @PostMapping(value = "/delete")
    public String eliminarLead(@ModelAttribute Lead lead, RedirectAttributes redirectAttrs) {
        Opportunity opportunity = lead.getOpportunity();
        if (opportunity != null) {
            opportunityRepository.delete(opportunity); // Elimina la oportunidad asociada
        }
        redirectAttrs
                .addFlashAttribute("mensaje", "Eliminado correctamente")
                .addFlashAttribute("clase", "warning");
        leadRepository.deleteById(lead.getId());
        return "redirect:/lead/show";
    }

    @PostMapping(value = "/convert/{id}")
    public String convertLeadToOpportunity(@PathVariable int id, RedirectAttributes redirectAttrs) {
        Lead lead = leadRepository.findById(id).orElse(null);

        if (lead != null) {

            Opportunity opportunity = new Opportunity();
            opportunity.setAsunto(lead.getAsunto_lead());
            opportunity.setCliente(lead.getCliente());
            opportunity.setEstado("En Proceso");

            opportunityRepository.save(opportunity);

            lead.setOpportunity(opportunity);
            lead.setEstado("Convertido");

            leadRepository.save(lead);

            Integer opportunityId = opportunity.getId();

            String opportunityURL = "/opportunity/detail_opportunity/" + opportunityId;  // Ajusta la URL según tu estructura de rutas

            redirectAttrs
                    .addFlashAttribute("mensaje", "Lead convertido a oportunidad correctamente")
                    .addFlashAttribute("clase", "success");

            return "redirect:" + opportunityURL;
        }else {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Lead no encontrado")
                    .addFlashAttribute("clase", "danger");
        }

        return "redirect:/lead/show";
    }

    @PostMapping(value = "/save")
    public String saveLead (@ModelAttribute("lead") @Valid Lead lead, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            // La validación ha fallado, redirige de vuelta al formulario
            redirectAttrs
                    .addFlashAttribute("mensaje", "Falta completar los datos")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/add";
        }

        redirectAttrs
                .addFlashAttribute("mensaje", "Lead agregado correctamente")
                .addFlashAttribute("clase", "success");
        lead.setEstado("Contactado");

        leadRepository.save(lead);

        Integer leadId = lead.getId();

        return "redirect:/lead/editar/" + leadId;
    }

    @GetMapping(value = "/editar/{id}")
    public String showFormEdit(@PathVariable int id, Model model) {
        Lead lead = leadRepository.findById(id).orElse(null);
        model.addAttribute("lead", lead);

        if (lead != null) {
            Cliente cliente = lead.getCliente();
            model.addAttribute("cliente", cliente);
        }

        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);

        return "lead/edit_lead";
    }


    @PostMapping(value = "/editar/{id}")
    public String actualizarLead(@ModelAttribute @Valid Lead lead, BindingResult bindingResult, RedirectAttributes redirectAttrs, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            if (id != null) {
                return "redirect:/lead/editar/" + id;
            }
            return "redirect:/lead/lead";
        }

        leadRepository.save(lead);
        redirectAttrs
                .addFlashAttribute("mensaje", "Editado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/lead/editar/" + id;
    }

}
