package com.utp.venta.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.utp.venta.Modelos.ProductSale;
import com.utp.venta.Modelos.Producto;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.lowagie.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//import com.itextpdf.text.Image;
import org.springframework.web.client.RestTemplate;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.lowagie.text.BadElementException;

@Service
public class PDFGeneratorServiceV0 {
    public void exportList(HttpServletResponse response, String test, List<ProductSale> productos, String nombreCliente, float totalBruto) throws IOException {
        Document document = new Document(PageSize.A4);
        //  PdfWriter res = PdfWriter.getInstance(document, response.getOutputStream());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter res = PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();

        try {
            Image image = Image.getInstance("classpath:static/img/img-logo.jpeg");
            image.scaleToFit(80, 80);
            document.add(image);
            System.out.println(image);
        }catch (BadElementException e) {
            throw new RuntimeException(e);
        }

        /*Font date_address = FontFactory();
        date_address.setSize(12);*/

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

        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.add(Calendar.MONTH, 2);

        Date futureDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
        String formattedFutureDate = dateFormat.format(futureDate);

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
        String formattedDate = dateFormat2.format(currentDate);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(12);
        font.setStyle(Font.NORMAL);

        Paragraph date_address = new Paragraph("Lima: " + formattedDate, font);

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

        Paragraph text_after_table = new Paragraph("Esta cotización tiene validez a partir del día " + formattedDate + " hasta "+formattedFutureDate , font);
        text_after_table.setSpacingBefore(15);

        Paragraph text_atentamente = new Paragraph("Atentamente, " , font);
        text_atentamente.setSpacingBefore(15);

        text_atentamente.add(Chunk.NEWLINE);
        text_atentamente.add(Chunk.NEWLINE);
        text_atentamente.add(Chunk.NEWLINE);

        Paragraph emptyParagraph = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 1));
        emptyParagraph.setIndentationLeft(0);
        emptyParagraph.setIndentationRight(0);

        text_atentamente.add(emptyParagraph );

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineWidth(1);  // Establecer el ancho de la línea según sea necesario
        lineSeparator.setPercentage(20);  // Ajustar el porcentaje de ancho según sea necesario
        text_atentamente.add(lineSeparator);



        Paragraph paragraph2 = new Paragraph("ID: 000000000" + test, fontParagraph);
        paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);
        paragraph2.setSpacingBefore(25);


        // Agrega los productos al documento
        Font fontProduct = FontFactory.getFont(FontFactory.HELVETICA);
        fontProduct.setSize(10);

        document.add(date_address);
        document.add(clienteLabelParagraph);
        document.add(nombreClienteParagraph);
        document.add(saludoLabel);
        document.add(mensajeLabel);
        //document.add(paragraph2);
        document.add(table);
        document.add(text_after_table);
        document.add(text_atentamente);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();

        // Convierte los bytes a una cadena Base64
        String base64Content = Base64.getEncoder().encodeToString(pdfBytes);

        // Resto del código...

        JSONObject JSONTEXT = new JSONObject();
        JSONTEXT.put("token", "iwj7zlonuqzsrnn2");
        JSONTEXT.put("to", "+51935546045");
        JSONTEXT.put("filename", "Cotización.pdf");
        JSONTEXT.put("document", base64Content);
        JSONTEXT.put("caption", "Estimado cliente, se adjunta cotización.");

        // Construye las cabeceras de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construye la entidad HTTP con el objeto JSON y las cabeceras
        HttpEntity<String> requestEntity = new HttpEntity<>(JSONTEXT.toString(), headers);

        // Realiza la solicitud HTTP usando RestTemplate
        String url = "https://api.ultramsg.com/instance68416/messages/document";
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(JSONTEXT.toString());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        System.out.println("Respuesta de la API: " + responseEntity.getBody());
        String texts = "aea";

        texts = "a";

    }


}
