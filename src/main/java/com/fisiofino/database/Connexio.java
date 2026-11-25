package com.fisiofino.database;

import java.io.IOException;

/**** AQUESTA CLASSE DEFINEIX LA CONNEXIÓ I EL DATASOURCE */

public class Connexio {

    /**************
     * S'HA D'IMPLEMENTAR TOT INCLOSES LES FUNCIONS
     * DEFINIDES A SOTA
     ********************/

    /**
     * Inicia el pool de connexions amb el fitxer de configuració EXTERN (fora del jar).
     *
     * @param connPropertiesPath ruta del fitxer conn.properties (absoluta o relativa al directori de
     *                           treball). Conté DB_URL, DB_USER, DB_PASSWORD i DB_MAX_POOL; com que
     *                           inclou la contrasenya, no s'ha de pujar al repositori.
     */
    public static void iniciaConnexio(String connPropertiesPath) throws IOException {
        /************* HAS DE COMPLETAR EL CODI ******************/
    }

    public static void tancaConnexio() {
        /************* HAS DE COMPLETAR EL CODI ******************/
    }

}
