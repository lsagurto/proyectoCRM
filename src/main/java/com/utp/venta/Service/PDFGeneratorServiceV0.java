package com.utp.venta.Service;

import com.itextpdf.text.BaseColor;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.utp.venta.Modelos.ProductSale;
import com.utp.venta.Modelos.Producto;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.lowagie.text.Image;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//import com.itextpdf.text.Image;
import org.springframework.web.client.RestTemplate;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.text.SimpleDateFormat;
import java.util.List;

import com.lowagie.text.BadElementException;

@Service
public class PDFGeneratorServiceV0 {
    public void exportList(HttpServletResponse response, String test, List<ProductSale> productos, String nombreCliente, String dniCliente, float totalBruto) throws IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter res = PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();

        float documentWidth = document.getPageSize().getWidth();
        float logoXPosition = documentWidth - 140;
        float logoYPosition = document.top() - 115;

        try {
            Image image = Image.getInstance("classpath:static/img/img-logo.jpeg");
            image.scaleToFit(130, 130);

            image.setAbsolutePosition(logoXPosition, logoYPosition);

            document.add(image);
            System.out.println(image);
        }catch (BadElementException e) {
            throw new RuntimeException(e);
        }

        // Crear una tabla para los productos
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100); // La tabla ocupa el 100% del ancho disponible

        table.getDefaultCell().setMinimumHeight(18);
        float headerRowHeight = 18;

        PdfPTable table2 = createSecondTable();

        // Encabezados de la tabla
        PdfPCell cellNombre = createHeaderCell("MODELO");
        PdfPCell cellCantidad = createHeaderCell("CANTIDAD");
        PdfPCell cellPrecio = createHeaderCell("P.UNIT.");
        PdfPCell cellIngreso = createHeaderCell("SUB TOTAL");

        float[] columnWidths = { 2f, 0.5f, 0.5f, 0.5f };
        table.setWidths(columnWidths);

        cellCantidad.setMinimumHeight(headerRowHeight);
        cellNombre.setMinimumHeight(headerRowHeight);
        cellPrecio.setMinimumHeight(headerRowHeight);
        cellIngreso.setMinimumHeight(headerRowHeight);

        //cellNombre.setColspan(2);
        table.addCell(cellNombre);

        table.addCell(cellCantidad);
        table.addCell(cellPrecio);
        table.addCell(cellIngreso);

        table.setSpacingBefore(10);

        // Agregar los productos a la tabla
        for (ProductSale producto : productos) {
            table.addCell(producto.getProducto().getNombre());
            table.addCell(String.valueOf(producto.getCantidad()));
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

        Font font_normal = FontFactory.getFont(FontFactory.HELVETICA);
        font_normal.setSize(11);
        font_normal.setStyle(Font.NORMAL);

        Font font_bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font_bold.setSize(11);
        //font_bold.setStyle(Font.BOLD);

        Paragraph number_order_title = new Paragraph("ORDEN DE PEDIDO: 011-019", font_bold);
        number_order_title.setAlignment(Element.ALIGN_CENTER);
        number_order_title.setSpacingBefore(-20f);

        Paragraph razon_social = new Paragraph();
        razon_social.add(new Chunk("RAZON SOCIAL: ", font_normal));
        razon_social.add(new Chunk("R.B COLLECTION COMPANY", font_bold));


        razon_social.setAlignment(Element.ALIGN_LEFT);
        razon_social.setSpacingBefore(20);


        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontParagraph.setSize(12);

        Paragraph number_RUC = new Paragraph();
        number_RUC.add(new Chunk("RUC:", font_normal));
        number_RUC.add(new Chunk("10400536894", font_bold));
        number_RUC.setSpacingBefore(5);

        Paragraph number_phone = new Paragraph();
        number_phone.add(new Chunk("CELULAR:", font_normal));
        number_phone.add(new Chunk("969288315", font_bold));
        number_phone.setSpacingBefore(5);

        Paragraph contact_name = new Paragraph();
        contact_name.add(new Chunk("CONTACTO:", font_normal));
        contact_name.add(new Chunk("Yuliana", font_bold));
        contact_name.setSpacingBefore(5);

        LineSeparator lineSeparatorTop = new LineSeparator();
        lineSeparatorTop.setLineWidth(1);
        lineSeparatorTop.setPercentage(100);

        Paragraph nombreClienteParagraph = new Paragraph("CLIENTE: "+nombreCliente, font_normal);

        Paragraph dniClienteParagraph = new Paragraph("DNI: "+dniCliente, font_normal);
        dniClienteParagraph.setSpacingBefore(5);

        Date date_issue = new Date();
        SimpleDateFormat dateFormatIssue = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDateIssue = dateFormatIssue.format(date_issue);

        Paragraph fechaParagraph = new Paragraph("FECHA DE EMISIÓN: " + formattedDateIssue, font_normal);
        fechaParagraph.setSpacingBefore(5);

        Paragraph empresaatiende = new Paragraph("ATENDIDO POR : TIENDA RB MAYORISTA" , font_normal);
        empresaatiende.setSpacingBefore(5);


        // Agrega los productos al documento
        Font fontProduct = FontFactory.getFont(FontFactory.HELVETICA);
        fontProduct.setSize(10);

        document.add(number_order_title);
        document.add(razon_social);
        document.add(number_RUC);
        document.add(number_phone);
        document.add(contact_name);
        document.add(new Chunk(lineSeparatorTop));


        document.add(nombreClienteParagraph);
        document.add(dniClienteParagraph);
        document.add(fechaParagraph);
        document.add(empresaatiende);

        document.add(table);
        document.add(table2);

        document.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=archivo.pdf");
        response.setContentLength(pdfBytes.length);

        try {
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        headers.add("user-agent", "Application");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construye la entidad HTTP con el objeto JSON y las cabeceras
        HttpEntity<String> requestEntity = new HttpEntity<>(JSONTEXT.toString(), headers);

        // Realiza la solicitud HTTP usando RestTemplate
        /*String url = "https://api.ultramsg.com/instance68416/messages/document";
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(JSONTEXT.toString());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        System.out.println("Respuesta de la API: " + responseEntity.getBody());
        String texts = "aea";

        texts = "a";*/

    }

    private static PdfPTable createSecondTable() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100); // La tabla ocupa el 100% del ancho disponible

        table.getDefaultCell().setMinimumHeight(30);
        float headerRowHeight = 15;

        // Encabezado grande
        PdfPCell cellEncabezadoGrande = createHeaderCellGreen("ENVIO POR SHALOM(PAGO DEL ENVÍO A DESTINO)", 10);
        cellEncabezadoGrande.setColspan(2);
        table.addCell(cellEncabezadoGrande);


        float[] columnWidths = { 2f, 0.7f };
        table.setWidths(columnWidths);

        // Encabezado izquierdo
        PdfPCell cellEncabezadoIzquierdo = createHeaderCellGray("PAGOS EN AGENTE CERO COMISIÓN", 10, 0);
        table.addCell(cellEncabezadoIzquierdo);

        // Contenido izquierdo
        PdfPCell cellEncabezadoDerecho = createHeaderCellGray("CATÁLOGO VIRTUAL", 10, 1);

        PdfPCell cellBancoNacion = new PdfPCell(new Phrase("BCP: 191-70753103-0-79 (a nombre de Rocío Fernández Chapilliquén)\n\n" +
                "BANCO DE LA NACIÓN: 04-076722655 (a nombre de Rocío Fernández Chapilliquén)\n\n"+
                "ACEPTAMOS YAPE ó PLIN: 958680299 (a nombre de Rocío Fernández Chapilliquén)", FontFactory.getFont(FontFactory.HELVETICA, 10)));

        table.addCell(cellEncabezadoDerecho);
        cellBancoNacion.setFixedHeight(70);

        table.addCell(cellBancoNacion);


        // Contenido derecho (imagen QR)
        PdfPCell cellImagenQR = new PdfPCell();

        try {
            Image image = Image.getInstance("classpath:static/img/img-qr.PNG");
            image.scaleToFit(100, 100);
            cellImagenQR.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellImagenQR.setVerticalAlignment(Element.ALIGN_MIDDLE);

            cellImagenQR.addElement(image);
            cellImagenQR.setRowspan(3);
            cellImagenQR.setFixedHeight(100);
            image.setAlignment(Element.ALIGN_CENTER);
            table.addCell(cellImagenQR);
            System.out.println(image);
            System.out.println(cellImagenQR);
        }catch (BadElementException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PdfPCell cellObservaciones = createHeaderCellGray("***OBSERVACIONES:", 10, 0);
        table.addCell(cellObservaciones);

        PdfPCell cellValidezCotizacion = new PdfPCell(new Phrase("*Validez de cotización: 2 días (SUJETO A VARIACIÓN DE STOCK)\n\n"+
                "CUALQUIER CAMBIO O RECLAMO SE ATENDERA DENTRO DE 7 DÍAS DE CALENDARIO.\n\n"+
                "DESDE LA FECHA QUE RECIBISTE TU PEDIDO.", FontFactory.getFont(FontFactory.HELVETICA,10)));
        cellValidezCotizacion.setFixedHeight(80);
        table.addCell(cellValidezCotizacion);

        return table;
    }

    private static PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        float headerRowHeight = 18;
        cell.setMinimumHeight(headerRowHeight);
        cell.setBackgroundColor(new Color(255, 249, 22));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return cell;
    }

    private static PdfPCell createHeaderCellGray(String text, float fontSize, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize)));
        float headerRowHeight = 18;
        cell.setMinimumHeight(headerRowHeight);
        cell.setBackgroundColor(new Color(192, 192, 192));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return cell;
    }

    private static PdfPCell createHeaderCellGreen(String text, float fontSize ) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD,fontSize)));
        float headerRowHeight = 18;
        cell.setMinimumHeight(headerRowHeight);
        cell.setBackgroundColor(new Color(61, 199, 76));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return cell;
    }


}
