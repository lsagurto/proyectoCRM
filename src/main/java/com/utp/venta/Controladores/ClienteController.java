package com.utp.venta.Controladores;

import com.utp.venta.Modelos.Cliente;
import com.utp.venta.Modelos.Proveedor;
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
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@CrossOrigin(origins = {"http://localhost:4200"})
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
    public String actualizarCliente(@PathVariable Integer id, @ModelAttribute @Valid Cliente cliente, BindingResult bindingResult, RedirectAttributes redirectAttrs, HttpServletRequest request) {
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        if (bindingResult.hasErrors()) {
            if (cliente.getId() != null) {
                return "cliente/editar_cliente";
            }
            return "redirect:/cliente/cliente";
        }
        Cliente clienteExistente = clienteRepository.findById(id).orElse(null);

        Cliente posibleClienteExistente = clienteRepository.findByNumeroDocumento(cliente.getNumeroDocumento());
        Cliente existingCliente = clienteRepository.findById(cliente.getId()).orElse(null);

        if (posibleClienteExistente != null && !posibleClienteExistente.getId().equals(cliente.getId())) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un cliente con ese numero de documento")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/agregar";
        }

        existingCliente.setDireccion(cliente.getDireccion());
        existingCliente.setEmail(cliente.getEmail());
        existingCliente.setId(cliente.getId());
        existingCliente.setNombre(cliente.getNombre());
        existingCliente.setTelefono(cliente.getTelefono());
        existingCliente.setNumeroDocumento(cliente.getNumeroDocumento());
        existingCliente.setUsuario_modificacion(usuarioBuscadoPorCodigo.getUsername());

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        cliente.setFechaCreacion(clienteExistente.getFechaCreacion());

        Date fechaActual = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);

        calendar.add(Calendar.HOUR_OF_DAY, -5);

        Date nuevaFechaModificacion = calendar.getTime();

        existingCliente.setFechaModificacion(nuevaFechaModificacion);


        clienteRepository.save(existingCliente);
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
    public String guardarCliente(@ModelAttribute @Valid Cliente cliente,@ModelAttribute Usuario usuarios, BindingResult bindingResult, RedirectAttributes redirectAttrs,HttpServletRequest request) {
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        if (bindingResult.hasErrors()) {
            return "cliente/agregar_cliente";
        }
        if (clienteRepository.findByNumeroDocumento(cliente.getNumeroDocumento()) != null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un Cliente con ese numero de documento")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/agregar";
        }
        else
        {
            // setear nombre y apellido
            String apiUrl = "https://api.apis.net.pe/v2/reniec/dni";
            String dni = cliente.getNumeroDocumento();
            String token = "apis-token-6640.hm3GQAjuy7k8koT8uWp7Hym-mjzRuMZ-";

            WebClient webClient = WebClient.builder()
                    .baseUrl(apiUrl)
                    .defaultHeader("Accept", "application/json")
                    .defaultHeader("Authorization", "Bearer " + token)
                    .build();

            String nombres, apellidoPaterno, apellidoMaterno, tipoDocumento, numeroDocumento, digitoVerificador;

            String jsonResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("numero", dni).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // En un entorno real, evita usar block() y maneja la respuesta de manera reactiva.

            // Parsing del JSON manualmente (puedes usar una librería JSON como Jackson si prefieres)
            nombres = obtenerValorDelCampo(jsonResponse, "nombres");
            apellidoPaterno = obtenerValorDelCampo(jsonResponse, "apellidoPaterno");
            apellidoMaterno = obtenerValorDelCampo(jsonResponse, "apellidoMaterno");

            cliente.setNombre(nombres+" "+apellidoMaterno+" "+apellidoPaterno);
            cliente.setUsuario_creacion(usuarioBuscadoPorCodigo.getUsername());
            cliente.setUsuario_modificacion(usuarioBuscadoPorCodigo.getUsername());


        }
        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clasxe", "success");

        cliente.setidUsuario(usuarioBuscadoPorCodigo.getId());

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Date fechaActual = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);

        calendar.add(Calendar.HOUR_OF_DAY, -5);

        Date nuevaFechaCreacion = calendar.getTime();

        cliente.setFechaCreacion(nuevaFechaCreacion);

        clienteRepository.save(cliente);
        return "redirect:/cliente/agregar";
    }

    private static String obtenerValorDelCampo(String jsonResponse, String campo) {
        // Parsing simple del JSON (puedes usar una librería JSON como Jackson si prefieres)
        String campoBuscado = "\"" + campo + "\":\"";
        int inicio = jsonResponse.indexOf(campoBuscado) + campoBuscado.length();
        int fin = jsonResponse.indexOf("\"", inicio);
        return jsonResponse.substring(inicio, fin);
    }
}
