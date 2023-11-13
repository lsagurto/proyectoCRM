package com.utp.venta.Service;

import com.itextpdf.text.pdf.draw.LineSeparator;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.utp.venta.Modelos.ProductSale;
import com.utp.venta.Modelos.Producto;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.itextpdf.text.Image;
import org.springframework.web.client.RestTemplate;

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

    public void exportList(HttpServletResponse response, String test, List<ProductSale> productos, String nombreCliente, float totalBruto) throws IOException {
        Document document = new Document(PageSize.A4);
        //  PdfWriter res = PdfWriter.getInstance(document, response.getOutputStream());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter res = PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        // Crear una tabla para los productos
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100); // La tabla ocupa el 100% del ancho disponible

        table.getDefaultCell().setMinimumHeight(30);
        float headerRowHeight = 30;

        // Encabezados de la tabla
        PdfPCell cellCantidad = new PdfPCell(new Phrase("Cantidad", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cellNombre = new PdfPCell(new Phrase("Nombre", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cellPrecio = new PdfPCell(new Phrase("Precio", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cellIngreso = new PdfPCell(new Phrase("Ingreso", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

        cellCantidad.setMinimumHeight(headerRowHeight);
        cellNombre.setMinimumHeight(headerRowHeight);
        cellPrecio.setMinimumHeight(headerRowHeight);
        cellIngreso.setMinimumHeight(headerRowHeight);

        table.addCell(cellCantidad);
        table.addCell(cellNombre);
        table.addCell(cellPrecio);
        table.addCell(cellIngreso);

        table.setSpacingBefore(10);

        // Agregar los productos a la tabla
        for (ProductSale producto : productos) {
            table.addCell(String.valueOf(producto.getCantidad()));
            table.addCell(producto.getProducto().getNombre());
            table.addCell(String.valueOf("S/. "+producto.getPrecio()));
            table.addCell(String.valueOf("S/. "+producto.getIngreso()));
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);

        double sumaIngresos = 0.0;

        for (ProductSale producto : productos) {
            sumaIngresos += producto.getIngreso();
            System.out.println("sumaIngresos "+sumaIngresos);

        }
        double igv = sumaIngresos * 0.18;
        String igvFormateado = decimalFormat.format(igv);

        System.out.println("igv "+igvFormateado);


        // Agregar una fila de subtotal y la suma de ingresos
        PdfPCell cellEmpty = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cellEmpty.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellEmpty2 = new PdfPCell(new Phrase(String.valueOf(""), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cellEmpty2.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellSubtotalLabel = new PdfPCell(new Phrase("Subtotal", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cellTotalIngresos = new PdfPCell(new Phrase("S/. "+String.valueOf(sumaIngresos), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

        cellSubtotalLabel.setMinimumHeight(headerRowHeight);
        cellTotalIngresos.setMinimumHeight(headerRowHeight);

        table.addCell(cellEmpty2);
        table.addCell(cellEmpty);
        table.addCell(cellSubtotalLabel);
        table.addCell(cellTotalIngresos);

        PdfPCell cellEmpty3 = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cellEmpty3.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellEmpty4 = new PdfPCell(new Phrase(String.valueOf(""), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cellEmpty4.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellIgvLabel = new PdfPCell(new Phrase("IGV (18%)", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cellTotalIgv = new PdfPCell(new Phrase("S/. "+String.valueOf(igvFormateado), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

        cellIgvLabel.setMinimumHeight(headerRowHeight);
        cellTotalIgv.setMinimumHeight(headerRowHeight);

        table.addCell(cellEmpty3);
        table.addCell(cellEmpty4);
        table.addCell(cellIgvLabel);
        table.addCell(cellTotalIgv);

        PdfPCell cellEmpty5 = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cellEmpty5.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellEmpty6 = new PdfPCell(new Phrase(String.valueOf(""), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cellEmpty6.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellTotalLabel = new PdfPCell(new Phrase("Total", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        PdfPCell cellTotal = new PdfPCell(new Phrase("S/. "+String.valueOf(totalBruto), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));


        cellTotalLabel.setMinimumHeight(headerRowHeight);
        cellTotal.setMinimumHeight(headerRowHeight);

        table.addCell(cellEmpty5);
        table.addCell(cellEmpty6);
        table.addCell(cellTotalLabel);
        table.addCell(cellTotal);

        Paragraph paragraph = new Paragraph("COTIZACIÓN", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontParagraph.setSize(12);

        Paragraph clienteLabelParagraph = new Paragraph("Cliente:", FontFactory.getFont(FontFactory.HELVETICA_BOLD));
        clienteLabelParagraph.setSpacingBefore(20);

        Paragraph nombreClienteParagraph = new Paragraph(nombreCliente);

        Paragraph asuntoCotizacion = new Paragraph("Asunto: cotización N° 00030");
        asuntoCotizacion.setSpacingBefore(15);

        Paragraph saludoLabel = new Paragraph("Cordial saludo señor: "+ nombreCliente + ",");
        saludoLabel.setSpacingBefore(15);

        Paragraph mensajeLabel = new Paragraph("Le escribimos con el fin de darle respuesta a su solicitud de cotización que usted nos ha solicitado");
        mensajeLabel.setSpacingBefore(15);

        Paragraph paragraph2 = new Paragraph("ID: 000000000" + test, fontParagraph);
        paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);
        paragraph2.setSpacingBefore(25);


        // Agrega los productos al documento
        Font fontProduct = FontFactory.getFont(FontFactory.HELVETICA);
        fontProduct.setSize(10);

        document.add(paragraph);
        document.add(clienteLabelParagraph);
        document.add(nombreClienteParagraph);
        document.add(saludoLabel);
        document.add(mensajeLabel);
        document.add(paragraph2);
        document.add(table);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();

        // Convierte los bytes a una cadena Base64
        String base64Content = Base64.getEncoder().encodeToString(pdfBytes);

        // Resto del código...

        JSONObject JSONTEXT = new JSONObject();
        JSONTEXT.put("token", "tsi9kx557ie8bdq9");
        JSONTEXT.put("to", "+51997315973");
        JSONTEXT.put("filename", "Cotización.pdf");
        JSONTEXT.put("document", base64Content);
        JSONTEXT.put("caption", "Estimado cliente, se adjunta cotización.");

        // Construye las cabeceras de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construye la entidad HTTP con el objeto JSON y las cabeceras
        HttpEntity<String> requestEntity = new HttpEntity<>(JSONTEXT.toString(), headers);

        // Realiza la solicitud HTTP usando RestTemplate
        String url = "https://api.ultramsg.com/instance68203/messages/document";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        System.out.println("Respuesta de la API: " + responseEntity.getBody());
        String texts = "aea";
        texts = "a";

    }


}
