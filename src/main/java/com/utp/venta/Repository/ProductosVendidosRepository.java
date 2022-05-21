package com.utp.venta;

import com.utp.venta.Modelos.ProductoVendido;
import org.springframework.data.repository.CrudRepository;

public interface ProductosVendidosRepository extends CrudRepository<ProductoVendido, Integer> {

}
