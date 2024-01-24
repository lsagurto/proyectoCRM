package com.utp.venta.Controladores;

import com.utp.venta.Modelos.*;
import com.utp.venta.Repository.ClienteRepository;
import com.utp.venta.Repository.LeadRepository;
import com.utp.venta.Repository.LeadRepositoryFromWSP;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Validated
@Controller
@RequestMapping(path = "/lead")
public class LeadController {
    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private LeadRepositoryFromWSP leadRepositoryFromWSP;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServiceLead serviceLead;

    /*@PostMapping("/crear")
    public ResponseEntity<Lead> crearLeadService(@RequestParam String nombre, @RequestParam Cliente cliente) {
        Lead nuevoLead = serviceLead.crearLead(nombre, cliente);
        return new ResponseEntity<>(nuevoLead, HttpStatus.CREATED);
    }*/

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
        model.addAttribute("lead", leadRepositoryFromWSP.findAll());
        return "lead/show_lead";
    }

    @PostMapping(value = "/delete")
    public String eliminarLead(@ModelAttribute Lead lead, RedirectAttributes redirectAttrs) {
        Opportunity opportunity = lead.getOpportunity();
        if (opportunity != null) {
            opportunityRepository.delete(opportunity); // Elimina la oportunidad asociada
        }

        // Busca y elimina el registro en LeadFromWSP asociado al Lead
        LeadFromWSP leadFromWSP = leadRepositoryFromWSP.findByLead(lead);
        if (leadFromWSP != null) {
            leadRepositoryFromWSP.delete(leadFromWSP);
        }
        leadRepository.delete(lead);

        redirectAttrs
                .addFlashAttribute("mensaje", "Eliminado correctamente")
                .addFlashAttribute("clase", "warning");
        return "redirect:/lead/show";
    }

    @PostMapping(value = "/convert/{id}")
    public String convertLeadToOpportunity(@PathVariable int id, RedirectAttributes redirectAttrs,HttpServletRequest request) {
        Lead lead = leadRepository.findById(id).orElse(null);
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        if (lead != null) {

            Opportunity opportunity = new Opportunity();
            opportunity.setAsunto(lead.getAsunto_lead());
            opportunity.setCliente(lead.getCliente());
            opportunity.setEstado("En Proceso");

            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

            Date fechaActual = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaActual);

            calendar.add(Calendar.HOUR_OF_DAY, -5);

            Date nuevaFechaCreacion = calendar.getTime();
            lead.setUsuario_creacion(usuarioBuscadoPorCodigo.getUsername());
            lead.setUsuario_modificacion(usuarioBuscadoPorCodigo.getUsername());

            opportunity.setFechaCreacion(nuevaFechaCreacion);

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
    private Usuario obtenerUsuario(HttpServletRequest request){
        Usuario usuarios = (Usuario) request.getSession().getAttribute("usuario");
        if(usuarios == null){
            usuarios = new Usuario();
        }
        return usuarios;
    }
    @PostMapping(value = "/save")
    public String saveLead (@ModelAttribute("lead") @Valid Lead lead, BindingResult bindingResult, RedirectAttributes redirectAttrs,HttpServletRequest request) {
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        if (bindingResult.hasErrors()) {
            // La validación ha fallado, redirige de vuelta al formulario
            redirectAttrs
                    .addFlashAttribute("mensaje", "Falta completar los datos")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/add";
        }

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Date fechaActual = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);

        calendar.add(Calendar.HOUR_OF_DAY, -5);

        Date nuevaFechaCreacion = calendar.getTime();

        lead.setFechaCreacion(nuevaFechaCreacion);
        lead.setUsuario_creacion(usuarioBuscadoPorCodigo.getUsername());
        lead.setUsuario_modificacion(usuarioBuscadoPorCodigo.getUsername());

        lead.setEstado("Contactado");

        leadRepository.save(lead);

        Cliente cliente = clienteRepository.findById(lead.getCliente().getId()).orElse(null);

        if (cliente != null) {
            LeadFromWSP leadFromWSP = new LeadFromWSP();
            leadFromWSP.setAsunto_lead(lead.getAsunto_lead());
            leadFromWSP.setDni(cliente.getNumeroDocumento());
            leadFromWSP.setEmail(cliente.getEmail());
            leadFromWSP.setNom_cliente(cliente.getNombre());
            leadFromWSP.setLead_id(lead);
            leadFromWSP.setUsuario_creacion(usuarioBuscadoPorCodigo.getUsername());
            leadFromWSP.setUsuario_modificacion(usuarioBuscadoPorCodigo.getUsername());
            leadRepositoryFromWSP.save(leadFromWSP);
        }

        redirectAttrs
                .addFlashAttribute("mensaje", "Lead agregado correctamente")
                .addFlashAttribute("clase", "success");

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
    public String actualizarLead(@ModelAttribute @Valid Lead lead, BindingResult bindingResult, RedirectAttributes redirectAttrs, @PathVariable("id") Integer id,HttpServletRequest request) {
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        if (bindingResult.hasErrors()) {
            if (id != null) {
                return "redirect:/lead/editar/" + id;
            }
            return "redirect:/lead/lead";
        }
        LeadFromWSP leadFromWSP = leadRepositoryFromWSP.findByLead(lead);
        if (leadFromWSP != null) {
            leadFromWSP.setUsuario_creacion(usuarioBuscadoPorCodigo.getUsername());
            leadFromWSP.setUsuario_modificacion(usuarioBuscadoPorCodigo.getUsername());
        }

        Lead existingLead = leadRepository.findById(id).orElse(null);


        if (existingLead != null) {
            existingLead.setAsunto_lead(lead.getAsunto_lead());
            existingLead.setCliente(lead.getCliente());
            existingLead.setEstado(lead.getEstado());
            System.out.println(lead.getEstado());
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

            Date fechaActual = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaActual);

            calendar.add(Calendar.HOUR_OF_DAY, -5);

            Date nuevaFechaModificacion = calendar.getTime();

            existingLead.setFechaModificacion(nuevaFechaModificacion);
            existingLead.setUsuario_creacion(usuarioBuscadoPorCodigo.getUsername());
            existingLead.setUsuario_modificacion(usuarioBuscadoPorCodigo.getUsername());

            leadRepository.save(existingLead);


            redirectAttrs
                    .addFlashAttribute("mensaje", "Editado correctamente")
                    .addFlashAttribute("clase", "success");
            return "redirect:/lead/editar/" + id;
        } else {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Lead no encontrado")
                    .addFlashAttribute("clase", "danger");
            return "redirect:/lead/lead";
        }
    }

}
