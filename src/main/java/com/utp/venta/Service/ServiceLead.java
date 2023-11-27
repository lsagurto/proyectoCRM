package com.utp.venta.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.utp.venta.Modelos.Cliente;
import com.utp.venta.Modelos.Lead;
import com.utp.venta.Modelos.LeadFromWSP;
import com.utp.venta.Repository.ClienteRepository;
import com.utp.venta.Repository.LeadRepository;
import com.utp.venta.Repository.LeadRepositoryFromWSP;

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
    private LeadRepositoryFromWSP leadRepositoryFromWSP;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private LeadRepository leadRepository;

    public ArrayList<LeadFromWSP> obtenerLeads(){
        return (ArrayList<LeadFromWSP>) leadRepositoryFromWSP.findAll();
    }

    public LeadFromWSP guardLeadFromWSP(LeadFromWSP leadFromWSP){

        Cliente cliente = clienteRepository.findByNumeroDocumento(leadFromWSP.getDni());

        if (cliente != null) {
            List<Lead> leadsbyDni = leadRepository.findByClienteNumeroDocumento(leadFromWSP.getDni());

            LeadFromWSP existingLeadInFromWsp = leadRepositoryFromWSP.findByDni(leadFromWSP.getDni());

            if (!leadsbyDni.isEmpty()) {

                Lead existingLead = leadsbyDni.get(0);

                System.out.println("existingLeadInWsp "+existingLeadInFromWsp);
                if(existingLeadInFromWsp==null){

                    leadFromWSP = leadRepositoryFromWSP.save(leadFromWSP);

                    leadFromWSP.setNom_cliente(cliente.getNombre());
                    leadFromWSP.setEmail(cliente.getEmail());
                    leadFromWSP.setLead_id(existingLead);
                    System.out.println("se creo el lead from wsp");

                }else{
                    return existingLeadInFromWsp;
                }


            }else{

                Lead nuevoLead = new Lead();
                nuevoLead.setCliente(cliente);
                nuevoLead.setAsunto_lead(leadFromWSP.getAsunto_lead());
                nuevoLead.setEstado("Contactado");

                System.out.println("cliente dni "+cliente.getEmail());
                System.out.println("cliente nombre "+cliente.getNombre());

                leadFromWSP.setNom_cliente(cliente.getNombre());
                leadFromWSP.setEmail(cliente.getEmail());
                leadFromWSP.setLead_id(nuevoLead);

                leadRepository.save(nuevoLead);
                leadFromWSP = leadRepositoryFromWSP.save(leadFromWSP);

            }

        }else{
            // El cliente no existe, crealo con la información proporcionada
            cliente = new Cliente();
            cliente.setNumeroDocumento(leadFromWSP.getDni());
            cliente.setDireccion("");
            cliente.setTelefono("");
            cliente.setTipoDocumento("1");
            cliente.setNombre(leadFromWSP.getNom_cliente());
            cliente.setEmail(leadFromWSP.getEmail());

            // Guarda el nuevo cliente en la base de datos
            cliente = clienteRepository.save(cliente);

            Lead nuevoLead = new Lead();
            nuevoLead.setCliente(cliente);
            nuevoLead.setAsunto_lead(leadFromWSP.getAsunto_lead());
            nuevoLead.setEstado("En Proceso"); // Asigna el estado "En Proceso"

            // Guarda el nuevo lead en la base de datos
            nuevoLead = leadRepository.save(nuevoLead);

            // Asocia el nuevo lead al LeadFromWSP
            leadFromWSP.setLead_id(nuevoLead);
        }

        return leadRepositoryFromWSP.save(leadFromWSP);
    }


}
