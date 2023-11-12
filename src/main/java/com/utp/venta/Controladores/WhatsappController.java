package com.utp.venta.Controladores;

import com.twilio.Twilio;
import com.utp.venta.Modelos.Cliente;
import com.utp.venta.Modelos.Lead;
import com.utp.venta.Service.ServiceLead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whatsapp")
public class WhatsappController {

    @Autowired
    private ServiceLead serviceLead;

    @PostMapping("/crear-cliente")
    public void crearCliente(@RequestParam String nombre, @RequestParam Cliente cliente, @RequestParam String numeroTelefono) {
        // Crea el lead y obtén el cliente (puedes ajustar esto según tus necesidades)
        Lead lead = new Lead();
        lead.setAsunto_lead(nombre);
        lead.setCliente(cliente);

        Lead nuevoLead = serviceLead.crearLead(nombre, cliente);

        // Aquí deberías enviar un mensaje de confirmación al usuario a través de Twilio
        enviarMensajeConfirmacion(numeroTelefono, "Lead creado con éxito. Asunto: " + nuevoLead.getAsunto_lead());
    }

    private void enviarMensajeConfirmacion(String numeroTelefono, String mensaje) {
        // Aquí utiliza la API de Twilio para enviar un mensaje al número de teléfono proporcionado
        // Puedes consultar la documentación de Twilio para obtener detalles sobre cómo enviar mensajes
    }

}
