package com.utp.venta.Controladores;

import com.utp.venta.Modelos.LeadFromWSP;
import com.utp.venta.Service.ServiceLead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/leadFromWSP")
public class LeadFromWSPController {
    @Autowired
    ServiceLead serviceLead;

    @GetMapping(value = "/show")
    public ArrayList<LeadFromWSP> obtenerLeadFromWSPs(){
        return  serviceLead.obtenerLeads();
    }

    @PostMapping(value = "/add")
    public LeadFromWSP guardarLeadFromWSP(@RequestBody LeadFromWSP leadFromWSP){
        System.out.println("leadFromWSP: "+ leadFromWSP);
        return this.serviceLead.guardLeadFromWSP(leadFromWSP);
    }
}
