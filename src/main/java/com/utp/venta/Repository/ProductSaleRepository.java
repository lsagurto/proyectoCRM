package com.utp.venta.Repository;

import com.utp.venta.Modelos.Opportunity;
import com.utp.venta.Modelos.ProductSale;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductSaleRepository extends CrudRepository<ProductSale, Integer> {
    List<ProductSale> findByOportunidad(Opportunity opportunity);
    List<ProductSale> findByIdIn(List<Integer> productIds);

}
