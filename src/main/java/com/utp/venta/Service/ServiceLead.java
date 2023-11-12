package com.utp.venta.Service;

import com.twilio.Twilio;
import com.utp.venta.Modelos.Cliente;
import com.utp.venta.Modelos.Lead;
import com.utp.venta.Repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServiceLead {
    @Value("${twilio.accountSID}")
    private String accountSID;

    @Value("${twilio.authToken}")
    private String authToken;
    @Autowired
    private LeadRepository leadRepository;

    public Lead crearLead(String nombre, Cliente cliente) {

        Twilio.init(accountSID, authToken);

        Lead lead = new Lead();
        lead.setAsunto_lead(nombre);
        lead.setCliente(cliente);

        return leadRepository.save(lead);
    }
}
