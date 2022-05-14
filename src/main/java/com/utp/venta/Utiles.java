
package com.utp.venta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Utiles {
    // https://parzibyte.me/blog/2019/03/04/obtener-formatear-fecha-hora-actual-java/
    static String obtenerFechaYHoraActual() {
        String formato = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern(formato);
        LocalDateTime ahora = LocalDateTime.now();
        return formateador.format(ahora);
    }
}
