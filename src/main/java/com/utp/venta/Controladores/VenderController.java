package com.utp.venta.Controladores;

import com.utp.venta.ClienteParaVender;
import com.utp.venta.Modelos.*;
import com.utp.venta.ProductoParaVender;
import com.utp.venta.Repository.*;
import com.utp.venta.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequestMapping(path = "/vender")
public class VenderController {
    @Autowired
    private ProductosRepository productosRepository;
    @Autowired
    private VentasRepository ventasRepository;
    @Autowired
    private ProductosVendidosRepository productosVendidosRepository;

    @Autowired
    private UsuarioRepository usuariosRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping(value = "/quitar/{indice}")
    public String quitarDelCarrito(@PathVariable int indice, HttpServletRequest request) {
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        if (carrito != null && carrito.size() > 0 && carrito.get(indice) != null) {
            carrito.remove(indice);
            this.guardarCarrito(carrito, request);
        }
        return "redirect:/vender/";
    }

    //@PostMapping(value = "/quitarCliente")
    //public String quitarCliente(this)

    private void limpiarCarrito(HttpServletRequest request) {
        this.guardarCarrito(new ArrayList<>(), request);
    }

    @GetMapping(value = "/limpiar")
    public String cancelarVenta(HttpServletRequest request, RedirectAttributes redirectAttrs) {
        this.limpiarCarrito(request);
        redirectAttrs
                .addFlashAttribute("mensaje", "Venta cancelada")
                .addFlashAttribute("clase", "info");
        return "redirect:/vender/";
    }

    @PostMapping(value = "/terminar")
    public String terminarVenta(@ModelAttribute Cliente cliente, @ModelAttribute Usuario usuarios, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        // Si no hay carrito o está vacío, regresamos inmediatamente
        ClienteParaVender carritoCliente = this.obtenerCliente(request);
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        Cliente clienteBuscadoPorCodigo = clienteRepository.findByNumeroDocumento(carritoCliente.getNumeroDocumento());
        if (carrito == null || carrito.size() <= 0) {
            return "redirect:/vender/";
        }
        Venta v = ventasRepository.save(new Venta(carritoCliente.getId(),carritoCliente.getIdVendedor(),clienteBuscadoPorCodigo.getNombre(),usuarioBuscadoPorCodigo.getUsername()));
        // Recorrer el carrito
        for (ProductoParaVender productoParaVender : carrito) {
            // Obtener el producto fresco desde la base de datos
            Producto p = productosRepository.findById(productoParaVender.getId()).orElse(null);
            if (p == null) continue; // Si es nulo o no existe, ignoramos el siguiente código con continue
            // Le restamos existencia
            p.restarExistencia(productoParaVender.getCantidad());
            // Lo guardamos con la existencia ya restada
            productosRepository.save(p);
            // Creamos un nuevo producto que será el que se guarda junto con la venta
            ProductoVendido productoVendido = new ProductoVendido(productoParaVender.getCantidad(), productoParaVender.getPrecio(), productoParaVender.getNombre(), productoParaVender.getCodigo(), v);
            // Y lo guardamos
            productosVendidosRepository.save(productoVendido);
        }

        // Al final limpiamos el carrito
        this.limpiarCarrito(request);
        // e indicamos una venta exitosa
        redirectAttrs
                .addFlashAttribute("mensaje", "Venta realizada correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/vender/";
    }

    @GetMapping(value = "/")
    public String interfazVender(Model model, HttpServletRequest request) {
        model.addAttribute("producto", new Producto());
        // --
        model.addAttribute("carritoCliente", new ProductoParaVender());
        float total = 0;
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        ClienteParaVender carritoCliente = this.obtenerCliente(request);
        for (ProductoParaVender p: carrito) total += p.getTotal();
        model.addAttribute("total", total);
        model.addAttribute("carritoCliente",carritoCliente);
        return "vender/vender";
    }

    private ArrayList<ProductoParaVender> obtenerCarrito(HttpServletRequest request) {
        ArrayList<ProductoParaVender> carrito = (ArrayList<ProductoParaVender>) request.getSession().getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        return carrito;
    }

    private ClienteParaVender obtenerCliente(HttpServletRequest request) {
        ClienteParaVender clienteVenta = (ClienteParaVender) request.getSession().getAttribute("carritoCliente");
        if (clienteVenta == null) {
            clienteVenta = new ClienteParaVender(null,null,null,null,null,null,null,null,null);
        }
        return clienteVenta;
    }
    private Usuario obtenerUsuario(HttpServletRequest request){
        Usuario usuarios = (Usuario) request.getSession().getAttribute("usuario");
        if(usuarios == null){
            usuarios = new Usuario();
        }
        return usuarios;
    }
    private void guardarCarrito(ArrayList<ProductoParaVender> carrito, HttpServletRequest request) {
        request.getSession().setAttribute("carrito", carrito);
    }
    private void guardarCliente(ClienteParaVender carritoCliente, HttpServletRequest request) {
        request.getSession().setAttribute("carritoCliente", carritoCliente);
    }

    @PostMapping(value = "/agregarCliente")
    public String agregarCliente(@ModelAttribute Cliente cliente, @ModelAttribute Usuario usuarios, BindingResult bindingResult, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        ClienteParaVender carritoCliente = this.obtenerCliente(request);
        Usuario usuarioBuscadoPorCodigo = this.obtenerUsuario(request);
        Cliente clienteBuscadoPorCodigo = clienteRepository.findByNumeroDocumento(cliente.getNumeroDocumento());
        boolean encontrado = false;
        if (clienteBuscadoPorCodigo == null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El cliente no existe")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/vender/";
        }
        else
        {
            encontrado = true;
        }

        if (!encontrado) {
          //  carritoCliente.add(new ClienteParaVender(clienteBuscadoPorCodigo.getId(),clienteBuscadoPorCodigo.getNombre(),clienteBuscadoPorCodigo.getTipoDocumento(),clienteBuscadoPorCodigo.getNumeroDocumento()));

         //   carritoCliente.add(new ClienteParaVender(clienteBuscadoPorCodigo.getId(),clienteBuscadoPorCodigo.getNombre(),clienteBuscadoPorCodigo.getEmail(),clienteBuscadoPorCodigo.getDireccion(),clienteBuscadoPorCodigo.getTelefono(),clienteBuscadoPorCodigo.getNumeroDocumento(),clienteBuscadoPorCodigo.getTipoDocumento()));
        }
        carritoCliente.setNumeroDocumento(clienteBuscadoPorCodigo.getNumeroDocumento());
        carritoCliente.setDireccion(clienteBuscadoPorCodigo.getDireccion());
        carritoCliente.setEmail(clienteBuscadoPorCodigo.getDireccion());
        carritoCliente.setTipoDocumento(clienteBuscadoPorCodigo.getTipoDocumento());
        carritoCliente.setNombre(clienteBuscadoPorCodigo.getNombre());
        carritoCliente.setTelefono(clienteBuscadoPorCodigo.getTelefono());
        carritoCliente.setId(clienteBuscadoPorCodigo.getId());
        carritoCliente.setVendedor(usuarioBuscadoPorCodigo.getUsername());
        carritoCliente.setIdVendedor(usuarioBuscadoPorCodigo.getId());
        carritoCliente.setFechaEmision(LocalDate.now().toString());
        this.guardarCliente(carritoCliente,request);
        return "redirect:/vender/";
    }

    @PostMapping(value = "/agregar")
    public String agregarAlCarrito(@ModelAttribute Producto producto, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        Producto productoBuscadoPorCodigo = productosRepository.findFirstByCodigo(producto.getCodigo());
        if (productoBuscadoPorCodigo == null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto con el código " + producto.getCodigo() + " no existe")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/vender/";
        }
        if (productoBuscadoPorCodigo.sinExistencia()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto está agotado")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/vender/";
        }
        boolean encontrado = false;
        for (ProductoParaVender productoParaVenderActual : carrito) {
            if (productoParaVenderActual.getCodigo().equals(productoBuscadoPorCodigo.getCodigo())) {
                productoParaVenderActual.aumentarCantidad();
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new ProductoParaVender(productoBuscadoPorCodigo.getNombre(), productoBuscadoPorCodigo.getCodigo(), productoBuscadoPorCodigo.getPrecio(), productoBuscadoPorCodigo.getExistencia(), productoBuscadoPorCodigo.getId(), 1f));
        }
        this.guardarCarrito(carrito, request);
        return "redirect:/vender/";
    }


    @PostMapping(value = "/agregar2/{indice}")
    public String agregarAlCarrito2(@PathVariable int indice, @ModelAttribute Producto producto, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        Producto productoBuscadoPorCodigo = productosRepository.findFirstByCodigo(String.valueOf(indice));
        if (productoBuscadoPorCodigo == null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto con el código " + producto.getCodigo() + " no existe")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/vender/";
        }
        if (productoBuscadoPorCodigo.sinExistencia()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto está agotado")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/vender/";
        }
        boolean encontrado = false;
        for (ProductoParaVender productoParaVenderActual : carrito) {
            if (productoParaVenderActual.getCodigo().equals(productoBuscadoPorCodigo.getCodigo())) {
                productoParaVenderActual.aumentarCantidad();
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new ProductoParaVender(productoBuscadoPorCodigo.getNombre(), productoBuscadoPorCodigo.getCodigo(), productoBuscadoPorCodigo.getPrecio(), productoBuscadoPorCodigo.getExistencia(), productoBuscadoPorCodigo.getId(), 1f));
        }
        this.guardarCarrito(carrito, request);
        return "redirect:/vender/";
    }
    @PostMapping(value = "/agregar3/{indice}")
    public String agregarAlCarrito3(@PathVariable int indice, @ModelAttribute Producto producto, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        ArrayList<ProductoParaVender> carrito = this.obtenerCarrito(request);
        Producto productoBuscadoPorCodigo = productosRepository.findFirstByCodigo(String.valueOf(indice));
        if (productoBuscadoPorCodigo == null) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto con el código " + producto.getCodigo() + " no existe")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/vender/";
        }
        if (productoBuscadoPorCodigo.sinExistencia()) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "El producto está agotado")
                    .addFlashAttribute("clase", "warning");
            return "redirect:/vender/";
        }
        boolean encontrado = false;
        for (ProductoParaVender productoParaVenderActual : carrito) {
            if (productoParaVenderActual.getCodigo().equals(productoBuscadoPorCodigo.getCodigo())) {
                productoParaVenderActual.aumentarCantidad2();
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new ProductoParaVender(productoBuscadoPorCodigo.getNombre(), productoBuscadoPorCodigo.getCodigo(), productoBuscadoPorCodigo.getPrecio(), productoBuscadoPorCodigo.getExistencia(), productoBuscadoPorCodigo.getId(), 1f));
        }
        this.guardarCarrito(carrito, request);
        return "redirect:/vender/";
    }
}
