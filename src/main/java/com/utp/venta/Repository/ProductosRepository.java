package com.utp.venta.Repository;

import com.utp.venta.Modelos.Producto;
import org.springframework.data.repository.CrudRepository;

public interface ProductosRepository extends CrudRepository<Producto, Integer> {

    Producto findFirstByCodigo(String codigo);
}
