/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author alberto
 */
public class Compresor {

    /**
     * Obtiene el array de bytes comprimido a partir de otro array de bytes que
     * se quiere comprimir.
     *
     * @param datos los datos descomprimidos
     * @return los datos comprimidos.
     * @throws IOException de vez en cuando
     */
    public static byte[] comprimirGZIP(byte[] datos) throws IOException {

        if (datos == null) {
            return null;
        }

        ByteArrayOutputStream gzdata = new ByteArrayOutputStream();
        GZIPOutputStream gzipper = new GZIPOutputStream(gzdata);
        ByteArrayInputStream data = new ByteArrayInputStream(datos);
        byte[] readed = new byte[1024];
        int actual = 1;
        while ((actual = data.read(readed)) > 0) {
            gzipper.write(readed, 0, actual);
        }
        gzipper.finish();
        data.close();
        byte[] compressed = gzdata.toByteArray();
        gzdata.close();
        data = null;
        gzdata = null;
        readed = null;
        gzipper = null;
        return compressed;
    }

    /**
     * Obtiene el array de bytes descomprimido a partir de otro array de bytes
     * comprimido
     *
     * @param datos los datos comprimidos
     * @return los datos descomprimidos.
     * @throws IOException de vez en cuando
     */
    public static byte[] descomprimirGZIP(byte[] datos) throws IOException {
        if (datos == null) {
            return null;
        }
        ByteArrayInputStream gzdata = new ByteArrayInputStream(datos);
        GZIPInputStream gunzipper = new GZIPInputStream(gzdata, datos.length);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] readed = new byte[1024];
        int actual = 1;
        while ((actual = gunzipper.read(readed)) > 0) {
            data.write(readed, 0, actual);
        }
        gzdata.close();
        gunzipper.close();
        byte[] returndata = data.toByteArray();

        data.close();
        data = null;
        gzdata = null;
        readed = null;
        gunzipper = null;

        return returndata;
    }

}
