package com.utp.venta.Repository;

import com.utp.venta.Modelos.Opportunity;
import com.utp.venta.Modelos.Producto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductosRepository extends CrudRepository<Producto, Integer> {

    Producto findFirstByCodigo(String codigo);
    Producto findFirstById(Integer id);
    List<Producto> findByOportunidad(Opportunity opportunity);

    List<Producto> findByIdIn(List<Integer> productIds);


}
