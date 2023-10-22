package com.utp.venta.Controladores;

import com.utp.venta.Repository.ClienteRepository;
import com.utp.venta.Repository.UsuarioRepository;
import com.utp.venta.Repository.VentasRepository;
import com.utp.venta.Service.PDFGeneratorServiceV0;
import com.utp.venta.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Controller
@RequestMapping(path = "/ventas")
public class VentasController {
    @Autowired
    VentasRepository ventasRepository;
    @Autowired
    private UsuarioRepository usuariosRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    private final PDFGeneratorServiceV0 pdfGeneratorService;
    public VentasController(PDFGeneratorServiceV0 pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping(value = "/")
    public String mostrarVentas(Model model) {
        ArrayList<Venta> ventas = (ArrayList<Venta>) ventasRepository.findAll();

//        ventas.forEach((ventita) -> {
//            Cliente clienteBuscadoPorCodigo = clienteRepository.findById(ventita.getIDCliente().toString());
//            ventita.setNumeroDocumento(clienteBuscadoPorCodigo.getNumeroDocumento());
//            Usuario usuario = usuariosRepository.findById(ventita.getVendedor().toString());
//            ventita.setVendedor(usuario.getUsername());
//        });

        model.addAttribute("ventas", ventas);
        return "ventas/ver_ventas";
    }
    @GetMapping(value = "/PDF")
    public void generatePDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.pdfGeneratorService.export(response,"001");
    }
}
