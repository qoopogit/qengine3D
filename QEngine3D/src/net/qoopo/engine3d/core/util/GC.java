package net.qoopo.engine3d.core.util;

public class GC extends Thread {

    private static GC instancia;
    private static boolean ejecutando = false;

    public static void detener() {
        ejecutando = false;
    }

    public static void iniciar() {
        try {

            if (instancia == null || !ejecutando) {
                ejecutando = true;
                instancia = new GC();
                instancia.start();
            }
        } catch (Exception e) {
        }
    }

    public static void gc() {
        try {
            System.gc();
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        try {
            while (ejecutando) {
                try {
                    //sleep(120000);//cada 2 minutos
                    sleep(60000);//cada  minuto
                } catch (Exception ex) {
                }
                gc();
            }
        } catch (Exception e) {

        }
    }

}
