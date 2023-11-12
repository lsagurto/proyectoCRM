package com.utp.venta.Controladores;

import com.utp.venta.Modelos.*;
import com.utp.venta.ProductoParaVender;
import com.utp.venta.Repository.OpportunityRepository;
import com.utp.venta.Repository.ProductSaleRepository;
import com.utp.venta.Repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/productos")
public class ProductosController {
    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ProductSaleRepository productSaleRepository;

    @GetMapping(value = "/agregar")
    public String agregarProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/agregar_producto";
    }

    @GetMapping(value = "/mostrar")
    public String mostrarProductos(Model model) {
        model.addAttribute("productos", productosRepository.findAll());
        return "productos/ver_productos";
    }

    @PostMapping(value = "/eliminar")
    public String eliminarProducto(@ModelAttribute Producto producto, RedirectAttributes redirectAttrs) {
        redirectAttrs
                .addFlashAttribute("mensaje", "Eliminado correctamente")
                .addFlashAttribute("clase", "warning");
        productosRepository.deleteById(producto.getId());
        return "redirect:/productos/mostrar";
    }

    // Se colocó el parámetro ID para eso de los errores, ya sé el id se puede recuperar
    // a través del modelo, pero lo que yo quiero es que se vea la misma URL para regresar la vista con
    // los errores en lugar de hacer un redirect, ya que si hago un redirect, no se muestran los errores del formulario
    // y por eso regreso mejor la vista ;)
    @PostMapping(value = "/editar/{id}")
    public String actualizarProducto(@ModelAttribute @Valid Producto producto, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            if (producto.getId() != null) {
                return "productos/editar_producto";
            }
            return "redirect:/productos/mostrar";
        }
        Producto posibleProductoExistente = productosRepository.findFirstByCodigo(producto.getCodigo());

        if (posibleProductoExistente != null && !posibleProductoExistente.getId().equals(producto.getId())) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un producto con ese código")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/productos/agregar";
        }
        productosRepository.save(producto);
        redirectAttrs
                .addFlashAttribute("mensaje", "Editado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/productos/mostrar";
    }

    @GetMapping(value = "/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("producto", productosRepository.findById(id).orElse(null));
        return "productos/editar_producto";
    }
    @GetMapping(value = "/add_product/{opportunityId}")
    public String addLead(@PathVariable Integer opportunityId, Model model) {

        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElse(null);
        if (opportunity == null) {
            return "redirect:/opportunity/show";
        }
        model.addAttribute("producto", new Producto());
        model.addAttribute("opportunity", opportunity);


        return "productos/add_product_opportunity";
    }

    @GetMapping(value = "/edit_product/{id}")
    public String showEditProductOpportunity(@PathVariable int id, Model model) {
        model.addAttribute("producto", productosRepository.findById(id).orElse(null));
        return "productos/edit_product_opportunity";
    }

    @PostMapping(value = "/editar_product/{id}")
    public String editProductoOportunidad(@PathVariable int id, @ModelAttribute @Valid Producto producto, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        Producto productoExistente = productosRepository.findById(id).orElse(null);

        if (productoExistente != null) {
            Opportunity opportunity = productoExistente.getOportunidad(); // Obtener la oportunidad a través de la relación

            productoExistente.setNombre(producto.getNombre());
            productoExistente.setExistencia(producto.getExistencia());
            productoExistente.setPrecio(producto.getPrecio());

            double ingreso = productoExistente.calcularIngreso2(); // Debes tener un método calcularIngreso en la entidad Producto
            double ingresoWithIGV = ingreso * 1.18; // Multiplicar por 1.18 para agregar el 18% de IGV
            productoExistente.setIngreso_with_igv(ingresoWithIGV);

            productoExistente.calcularIngreso2();

            productosRepository.save(productoExistente);

            // Calcular el Total Bruto y el Total Neto de la oportunidad
            List<Producto> productosDeOportunidad = opportunity.getProductos();
            float totalBruto = 0.0f;
            float totalNeto = 0.0f;
            for (Producto prod : productosDeOportunidad) {
                totalBruto += prod.getIngreso_with_igv();
                totalNeto += prod.calcularIngreso();
            }
            opportunity.setTotalBruto(totalBruto);
            opportunity.setTotalNeto(totalNeto);
            opportunityRepository.save(opportunity);

            redirectAttrs
                    .addFlashAttribute("mensaje", "Editado correctamente")
                    .addFlashAttribute("clase", "success");


            return "redirect:/opportunity/detail_opportunity/" + opportunity.getId();

        }else{
            return "redirect:/error";
        }

    }

    @PostMapping(value = "/finish_sale/{id}")
    public String finishSale(@PathVariable int id, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);

        Opportunity opportunity = opportunityRepository.findById(id).orElse(null);

        // Obtén el formato de la configuración regional actual
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();

        // Cambia el separador decimal a punto
        symbols.setDecimalSeparator('.');

        // Crea un nuevo formato con la configuración modificada
        DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);

        float totalNeto = 0.0f;
        float totalBruto = 0.0f;

        // Itera a través de los productos en el carrito
        for (ProductoParaVender productoParaVender : carrito) {
            Producto productoBuscadoPorCodigo = productosRepository.findFirstByCodigo(productoParaVender.getCodigo());

            System.out.println("productoExistente: "+productoBuscadoPorCodigo.getNombre());

            if (productoBuscadoPorCodigo != null) {

                float cantidadVendida = productoParaVender.getCantidad();

                System.out.println("cantidadVendida: "+cantidadVendida);

                if (cantidadVendida > 0) {

                    // Verifica si hay suficiente stock para la venta

                    if (productoBuscadoPorCodigo.getExistencia() >= cantidadVendida) {
                        // Actualiza el stock del producto

                        ProductSale venta = new ProductSale();
                        venta.setProducto(productoBuscadoPorCodigo);
                        venta.setCantidad(cantidadVendida);
                        venta.setPrecio(productoBuscadoPorCodigo.getPrecio());

                        float ingreso = cantidadVendida * venta.getPrecio();
                        float ingresoConIGV = ingreso * 1.18f;

                        // Formatea los valores a dos decimales
                        ingreso = Float.parseFloat(decimalFormat.format(ingreso));
                        ingresoConIGV = Float.parseFloat(decimalFormat.format(ingresoConIGV));


                        venta.setIngreso(ingreso);
                        venta.setIngreso_with_igv(ingresoConIGV);
                        venta.setOportunidad(opportunity);

                        totalNeto += ingreso;
                        totalBruto += ingresoConIGV;

                        productoBuscadoPorCodigo.setExistencia(productoBuscadoPorCodigo.getExistencia() - cantidadVendida);
                        productosRepository.save(productoBuscadoPorCodigo);
                        productSaleRepository.save(venta);


                    } else {
                        // No hay suficiente stock para la venta, maneja el caso apropiadamente
                        redirectAttrs.addFlashAttribute("mensaje", "No hay suficiente stock para " + productoBuscadoPorCodigo.getNombre());
                        redirectAttrs.addFlashAttribute("clase", "warning");
                        return "redirect:/opportunity/detail_opportunity/" + id;
                    }
                }
            }
        }

        opportunity.setEstado("Ganada");
        opportunity.setTotalNeto(totalNeto);
        opportunity.setTotalBruto(totalBruto);
        opportunityRepository.save(opportunity);


        // Borra el carrito una vez que se ha completado la venta
        carrito.clear();
        this.guardarCarrito(carrito, request);
        redirectAttrs
                .addFlashAttribute("mensaje", "Venta finalizada correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/opportunity/detail_opportunity/" + id;
    }
    @PostMapping(value = "/agregar2/{indice}/{id}")
    public String agregarAlCarrito2(@PathVariable int indice, @PathVariable int id, @ModelAttribute Producto producto, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        //Producto productoExistente = productosRepository.findById(id).orElse(null);
        //Opportunity opportunity = productoExistente.getOportunidad();

        Producto productoBuscadoPorCodigo = productosRepository.findFirstByCodigo(String.valueOf(indice));
        if (productoBuscadoPorCodigo == null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto con el código " + producto.getCodigo() + " no existe")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/opportunity/detail_opportunity/" + id;
        }
        if (productoBuscadoPorCodigo.sinExistencia()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto está agotado")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/opportunity/detail_opportunity/" + id;
        }
        boolean encontrado = false;
        for (ProductoParaVender productoParaVenderActual : carrito) {
            if (productoParaVenderActual.getCodigo().equals(productoBuscadoPorCodigo.getCodigo())) {
                productoParaVenderActual.aumentarCantidad();
                float ingresoConIGV = (float) (Math.round(productoParaVenderActual.getPrecio() * productoParaVenderActual.getCantidad() * 1.18f * 100.0) / 100.0);
                productoParaVenderActual.setIngresoConIGV(ingresoConIGV);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new ProductoParaVender(productoBuscadoPorCodigo.getNombre(), productoBuscadoPorCodigo.getCodigo(), productoBuscadoPorCodigo.getPrecio(), productoBuscadoPorCodigo.getExistencia(), productoBuscadoPorCodigo.getId(), 1f));
        }
        this.guardarCarrito(carrito, request);
        return "redirect:/opportunity/detail_opportunity/" + id;
    }

    @PostMapping(value = "/agregar3/{indice}/{id}")
    public String agregarAlCarrito3(@PathVariable int indice, @PathVariable int id, @ModelAttribute Producto producto, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        //Producto productoExistente = productosRepository.findById(id).orElse(null);
        //Opportunity opportunity = productoExistente.getOportunidad();

        Producto productoBuscadoPorCodigo = productosRepository.findFirstByCodigo(String.valueOf(indice));
        if (productoBuscadoPorCodigo == null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto con el código " + producto.getCodigo() + " no existe")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/opportunity/detail_opportunity/" + id;
        }
        if (productoBuscadoPorCodigo.sinExistencia()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto está agotado")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/opportunity/detail_opportunity/" + id;
        }
        boolean encontrado = false;
        for (ProductoParaVender productoParaVenderActual : carrito) {
            if (productoParaVenderActual.getCodigo().equals(productoBuscadoPorCodigo.getCodigo())) {
                productoParaVenderActual.aumentarCantidad2();
                float ingresoConIGV = (float) (Math.round(productoParaVenderActual.getPrecio() * productoParaVenderActual.getCantidad() * 1.18f * 100.0) / 100.0);
                productoParaVenderActual.setIngresoConIGV(ingresoConIGV);

                if (productoParaVenderActual.getCantidad() == 0.0) {
                    carrito.remove(productoParaVenderActual);
                }

                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new ProductoParaVender(productoBuscadoPorCodigo.getNombre(), productoBuscadoPorCodigo.getCodigo(), productoBuscadoPorCodigo.getPrecio(), productoBuscadoPorCodigo.getExistencia(), productoBuscadoPorCodigo.getId(), 1f));
        }
        this.guardarCarrito(carrito, request);
        return "redirect:/opportunity/detail_opportunity/" + id;
    }
    private void guardarCarrito(ArrayList<ProductoParaVender> carrito, HttpServletRequest request) {
        request.getSession().setAttribute("carrito_product", carrito);
    }
    private ArrayList<ProductoParaVender> obtenerCarrito(HttpServletRequest request) {
        ArrayList<ProductoParaVender> carrito = (ArrayList<ProductoParaVender>) request.getSession().getAttribute("carrito_product");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        return carrito;
    }
    @PostMapping(value = "/save_in_opportunity/{id}")
    public String saveProduct(@PathVariable int id, HttpServletRequest request, @ModelAttribute Producto producto, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        System.out.println("id de la oportunidad "+id);
        //Producto productoExistente = productosRepository.findById(id).orElse(null);
        //Opportunity opportunity = productoExistente.getOportunidad();

        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        Producto productoBuscadoPorCodigo = productosRepository.findFirstById(producto.getId());
        System.out.println("este es el producto: "+ productoBuscadoPorCodigo.getId());

        if (productoBuscadoPorCodigo == null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto con el código " + producto.getId() + " no existe")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/opportunity/detail_opportunity/" + id;
        }

        if (productoBuscadoPorCodigo.sinExistencia()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto está agotado")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/opportunity/detail_opportunity/" + id;
        }

        boolean encontrado = false;
        System.out.println(carrito);

        for (ProductoParaVender productoParaVenderActual : carrito) {
            if (productoParaVenderActual.getId().equals(productoBuscadoPorCodigo.getId())) {
                productoParaVenderActual.aumentarCantidad();
                System.out.println("este es un for "+ productoParaVenderActual.getId());
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.out.println(encontrado);
            Float cantidad = 1f;
            ProductoParaVender nuevoProducto = new ProductoParaVender(productoBuscadoPorCodigo.getNombre(), productoBuscadoPorCodigo.getCodigo(), productoBuscadoPorCodigo.getPrecio(), productoBuscadoPorCodigo.getExistencia(), productoBuscadoPorCodigo.getIngreso(), productoBuscadoPorCodigo.getId(), cantidad);
            float ingresoConIGV = (float) (Math.round (nuevoProducto.getPrecio() * cantidad * 1.18f * 100.0)/100.0);

            nuevoProducto.setIngresoConIGV(ingresoConIGV);
            carrito.add(nuevoProducto);

           // carrito.add(new ProductoParaVender(productoBuscadoPorCodigo.getNombre(), productoBuscadoPorCodigo.getCodigo(), productoBuscadoPorCodigo.getPrecio(), productoBuscadoPorCodigo.getExistencia(), productoBuscadoPorCodigo.getIngreso(), ingresoConIGV, productoBuscadoPorCodigo.getId(), 1f));
        }
        this.guardarCarrito(carrito, request);

        return "redirect:/opportunity/detail_opportunity/" + id;
    }

    @PostMapping(value = "/save/{id}")
    public String guardarProductoOportunidad(@PathVariable int id, @ModelAttribute @Valid Producto producto, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Falta completar los datos")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/cliente/add";
        }
        Opportunity opportunity = opportunityRepository.findById(id).orElse(null);
        if (opportunity == null) {
            return "redirect:/opportunity/show";
        }
        producto.setOportunidad(opportunity);

        Float ingreso = producto.getExistencia() * producto.getPrecio();

        producto.setIngreso(ingreso);

        // Calcula el ingreso con IGV (18%)
        double igv = ingreso * 0.18;
        double ingresoConIgv = ingreso + igv;

        producto.setIngreso_with_igv(ingresoConIgv);

        productosRepository.save(producto);

        // Calcular el Total Bruto y el Total Neto de la oportunidad
        List<Producto> productosDeOportunidad = opportunity.getProductos();
        float totalBruto = 0.0f;
        float totalNeto = 0.0f;
        for (Producto prod : productosDeOportunidad) {
            totalBruto += prod.getIngreso_with_igv();
            totalNeto += prod.getIngreso();
        }

        opportunity.setTotalBruto(totalBruto);
        opportunity.setTotalNeto(totalNeto);
        opportunityRepository.save(opportunity);

        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clase", "success");

        return "redirect:/opportunity/detail_opportunity/" + opportunity.getId();
    }
    @PostMapping(value = "/agregar")
    public String guardarProducto(@ModelAttribute @Valid Producto producto, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "productos/agregar_producto";
        }
        if (productosRepository.findFirstByCodigo(producto.getCodigo()) != null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Ya existe un producto con ese código")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/productos/agregar";
        }
        productosRepository.save(producto);
        redirectAttrs
                .addFlashAttribute("mensaje", "Agregado correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/productos/agregar";
    }
}
