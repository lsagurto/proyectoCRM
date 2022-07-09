package com.utp.venta.Controladores;

import com.utp.venta.Service.PDFGeneratorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class PDFExportController {
    private final PDFGeneratorService pdfGeneratorService;

    public PDFExportController(PDFGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping("/pdf/generate")
    public void generatePDF(HttpServletResponse response){
        response.setContentType("application/pdf");
        DateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy mm:ss");
        String currenDateTime = dateFormater.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment, filename=pdf_"+currenDateTime+".pfg";
        response.setHeader(headerKey,headerValue);
    }
}
