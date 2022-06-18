package com.utp.venta.Repository;

import com.utp.venta.Modelos.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    public Proveedor findByDocumento (String Documento);
}
