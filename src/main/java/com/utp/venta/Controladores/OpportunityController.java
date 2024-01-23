package com.utp.venta.Controladores;

import com.utp.venta.Modelos.*;
import com.utp.venta.ProductoParaVender;
import com.utp.venta.Repository.*;
import com.utp.venta.Service.PDFGeneratorServiceV0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.utp.venta.Service.PDFGeneratorServiceV0;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@Controller
@RequestMapping(path = "/opportunity")
public class OpportunityController {
    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private LeadRepositoryFromWSP leadRepositoryFromWSP;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private ProductSaleRepository productSaleRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    private PDFGeneratorServiceV0 pdfGeneratorService;

    @Autowired
    public OpportunityController(PDFGeneratorServiceV0 pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping(value = "/show")
    public String showOpportunity(Model model) {
        model.addAttribute("opportunity", opportunityRepository.findAll());
        return "opportunity/show_opportunity";
    }

    @GetMapping(value = "/add")
    public String addOpportunity(Model model) {
        List<Cliente> clientes = clienteRepository.findAll();
        List<Opportunity> opportunity = opportunityRepository.findAll();

        model.addAttribute("opportunity", new Opportunity());
        model.addAttribute("clientes", clientes);

        return "opportunity/add_opportunity";
    }

    @PostMapping(value = "/save")
    public String saveLead (@ModelAttribute("opportunity") @Valid Opportunity opportunity, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            // La validación ha fallado, redirige de vuelta al formulario
            redirectAttrs
                    .addFlashAttribute("mensaje", "Falta completar los datos")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/add";
        }

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Date fechaActual = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);

        calendar.add(Calendar.HOUR_OF_DAY, -5);

        Date nuevaFechaCreacion = calendar.getTime();

        opportunity.setFechaCreacion(nuevaFechaCreacion);

        opportunity.setEstado("En proceso");

        opportunityRepository.save(opportunity);

        redirectAttrs
                .addFlashAttribute("mensaje", "Lead agregado correctamente")
                .addFlashAttribute("clase", "success");


        return "redirect:/opportunity/show";
    }

    @GetMapping(value = "/editar/{id}")
    public String showFormEdit(@PathVariable int id, Model model) {
        Opportunity opportunity = opportunityRepository.findById(id).orElse(null);
        model.addAttribute("opportunity", opportunity);

        if (opportunity != null) {
            Cliente cliente = opportunity.getCliente();
            model.addAttribute("cliente", cliente);
        }

        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);

        return "opportunity/edit_opportunity";
    }

    @PostMapping(value = "/editar/{id}")
    public String actualizarOpportunity(@PathVariable int id, @ModelAttribute @Valid Opportunity opportunity, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            if (opportunity.getId() != null) {
                return "opportunity/edit_opportunity";
            }
            return "redirect:/opportunity/opportunity";
        }

        Opportunity existingOpportunity = opportunityRepository.findById(id).orElse(null);

        if (existingOpportunity == null) {
            // Maneja la oportunidad inexistente, si es necesario.
            return "redirect:/opportunity/show";
        }


        // Actualiza los campos de la oportunidad
        existingOpportunity.setAsunto(opportunity.getAsunto());
        existingOpportunity.setEstado(opportunity.getEstado());
        existingOpportunity.setCliente(opportunity.getCliente());

        // Configura la zona horaria para la fecha de modificación
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        opportunity.setFechaCreacion(existingOpportunity.getFechaCreacion());

        // Obtiene la fecha y hora actual
        Date fechaActual = new Date();

        // Crea un objeto Calendar y lo inicializa con la fecha y hora actual
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);

        // Resta 5 horas a la fecha y hora actual
        calendar.add(Calendar.HOUR_OF_DAY, -5);

        // Obtiene la nueva fecha y hora después de restarle 5 horas
        Date nuevaFechaModificacion = calendar.getTime();

        existingOpportunity.setFechaModificacion(nuevaFechaModificacion);

        opportunityRepository.save(existingOpportunity);
        redirectAttrs
                .addFlashAttribute("mensaje", "Editado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/opportunity/show";
    }

    @PostMapping(value = "/delete")
    public String eliminarOpportunity(@ModelAttribute Opportunity opportunity, RedirectAttributes redirectAttrs) {
        Integer opportunityId = opportunity.getId();

        List<Lead> leadsAsociados = leadRepository.findByOpportunityId(opportunityId);
        System.out.println(leadsAsociados);
        if (leadsAsociados.size() > 0) {
            for (Lead lead : leadsAsociados) {
                Integer leadId = lead.getId();
                LeadFromWSP leadFromWSP = leadRepositoryFromWSP.findByLead(lead);


                if (leadFromWSP != null) {
                    leadRepositoryFromWSP.delete(leadFromWSP);
                }

                leadRepository.delete(lead);

            }
        }else{
            System.out.println(opportunity.getId());

            opportunityRepository.deleteById(opportunity.getId());
        }

        redirectAttrs
                .addFlashAttribute("mensaje", "Eliminado correctamente")
                .addFlashAttribute("clase", "warning");
        return "redirect:/opportunity/show";
    }

    private ArrayList<ProductoParaVender> obtenerCarrito(HttpServletRequest request) {
        ArrayList<ProductoParaVender> carrito = (ArrayList<ProductoParaVender>) request.getSession().getAttribute("carrito_product");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        return carrito;
    }

    @GetMapping(value = "/detail_opportunity/{id}")
    public String DetailOpportunity(@PathVariable int id, Model model, HttpServletRequest request) {
        Opportunity opportunity = opportunityRepository.findById(id).orElse(null);
        List<Actividad> actividades = actividadRepository.findByOportunidad(opportunity);
        List<Producto> productos = productosRepository.findByOportunidad(opportunity);
        List<Cotizacion> cotizacion = cotizacionRepository.findByOportunidad(opportunity);

        List<Producto> productos_list = (List<Producto>) productosRepository.findAll();

        List<ProductSale> productSales = productSaleRepository.findByOportunidad(opportunity);
        List<ProductoParaVender> productosEnCarrito = this.obtenerCarrito(request);

        DecimalFormatSymbols symbols2 = new DecimalFormatSymbols();
        symbols2.setDecimalSeparator('.');

        DecimalFormat decimalFormatIngresoIGV = new DecimalFormat("#.##", symbols2);
        DecimalFormat decimalFormatIngreso = new DecimalFormat("#.##", symbols2);

        // Combina las listas de product_sale y session.carrito_product
        List<ProductoParaVender> productosCombinados = new ArrayList<>(productosEnCarrito);

        List<ProductoParaVender> productosEspeciales = new ArrayList<>();

        for (ProductSale productSale : productSales) {
            Producto productoEnVenta = productSale.getProducto();
            Float cantidad = productSale.getCantidad();

            ProductoParaVender productoParaVender = new ProductoParaVender(productoEnVenta, cantidad);
            productoParaVender.setExistencia(productSale.getCantidad());

            if (productoParaVender.getIngreso() != null) {
                Float ingreso = Float.parseFloat(decimalFormatIngreso.format((double) productoParaVender.getIngreso()));
                productoParaVender.setIngreso(ingreso);

            }

            productoParaVender.setIngresoConIGV(productSale.getIngreso_with_igv());

            if (productoParaVender.getIngresoConIGV() != null) {
                Float ingresoConIGV = Float.parseFloat(decimalFormatIngresoIGV.format((double) productoParaVender.getIngresoConIGV()));
                productoParaVender.setIngresoConIGV(ingresoConIGV);
            }

            productoParaVender.formatearIngresoConIGV(decimalFormatIngresoIGV);
            productoParaVender.formatearIngreso(decimalFormatIngreso);

            if (productoEnVenta.getCodigo().equals("codigoEmbalaje") || productoEnVenta.getCodigo().equals("codigoDelivery")) {
                productosEspeciales.add(productoParaVender);
            } else {
                productosCombinados.add(productoParaVender);
            }


        }

        productosCombinados.addAll(productosEspeciales);



        // Calcula el total bruto y total neto de los productos vendidos

        double totalBruto = productSales.stream().mapToDouble(ProductSale::getIngreso_with_igv).sum();
        double totalNeto = productSales.stream().mapToDouble(ProductSale::getIngreso).sum();

        // Calcula el IGV (18%)
        // float igv = (float) (totalBruto * 0.18);

        // Calcula el total neto
        // double totalNeto = totalBruto - igv;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.0", symbols);

        totalBruto = Double.parseDouble(decimalFormat.format(totalBruto));
        totalNeto = Double.parseDouble(decimalFormat.format(totalNeto));


        // Obtén la lista de IDs de productos
        List<Integer> productoIds = productSales.stream()
                .map(ProductSale::getId)
                .collect(Collectors.toList());

        model.addAttribute("opportunity", opportunity);
        model.addAttribute("actividades", actividades);
        model.addAttribute("productos", productos);
        model.addAttribute("productos_list", productos_list);
        model.addAttribute("producto", new Producto());

        model.addAttribute("cotizacion", cotizacion);
        model.addAttribute("totalBruto", totalBruto);
        // model.addAttribute("igv", igv);
        model.addAttribute("totalNeto", totalNeto);
        model.addAttribute("productosCombinados", productosCombinados);  // Nueva lista combinada

        model.addAttribute("productoIds", productoIds);


        return "opportunity/detail_opportunity";
    }

    @GetMapping(value = "/PDF")
    public void generatePDF(HttpServletResponse response, @RequestParam("opportunityId") int opportunityId, @RequestParam("productIds") List<Integer> productIds) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        // Obtener el nombre del cliente
        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElse(null);
        float totalBruto = opportunity.getTotalBruto(); // Asumiendo que tienes un método 'getTotalBruto' en tu clase Opportunity


        String nombreCliente = opportunity.getCliente().getNombre();
        String dniCliente = opportunity.getCliente().getNumeroDocumento();
        String numero = opportunity.getCliente().getTelefono();

        List<ProductSale> productos = productSaleRepository.findByIdIn(productIds);

        this.pdfGeneratorService.exportList(response,"001", productos, nombreCliente, dniCliente,numero, totalBruto);
        //this.pdfGeneratorServic
    }

}
