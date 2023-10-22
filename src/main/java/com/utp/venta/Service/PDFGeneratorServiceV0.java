package com.utp.venta.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class PDFGeneratorServiceV0 {
    public void export(HttpServletResponse reponse, String test) throws IOException{
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, reponse.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph paragraph = new Paragraph("Documento de venta",fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontParagraph.setSize(12);

        Paragraph paragraph2 = new Paragraph("ID: 000000000"+test,fontParagraph);
        paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);

        document.add(paragraph);
        document.add(paragraph2);
        document.close();
    }
}
