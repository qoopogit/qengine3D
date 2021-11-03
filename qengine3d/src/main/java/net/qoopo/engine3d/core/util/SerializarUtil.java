/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para la serializacion de objetos
 *
 * @author alberto
 */
public class SerializarUtil {

    public static byte[] convertirBytes(Object object) {
        byte[] salida = null;
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.close();
            salida = bos.toByteArray();
            bos.close();
            out = null;
            bos = null;
        } catch (IOException ex) {
        }
        return salida;
    }

    public static void escribirArchivo(Object object, String rutaArchivo) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo));
        out.writeObject(object);
        out.close();
    }

    public static void escribirStream(Object object, OutputStream outStream) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(outStream);
        out.writeObject(object);
        out.close();
    }

    public static Object leerObjeto(String rutaArchivo) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(rutaArchivo));
        Object object = in.readObject();
        in.close();
        in = null;
        return object;
    }

    public static Object leerObjeto(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bin);
        Object object = in.readObject();
        in.close();
        bin.close();
        bin = null;
        in = null;
        return object;
    }

    public static Object leerObjeto(InputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(stream);
        Object object = in.readObject();
        in.close();
        return object;
    }

    public static void agregarObjeto(String filename, Object obj, boolean append, boolean comprimir) {
        File file = new File(filename);
        //si no existe la carpeta contenedora, la creo
        File carpe = file.getParentFile();
        if (!carpe.exists()) {
            carpe.mkdirs();
        }

        ObjectOutputStream out = null;
        try {
            if (!file.exists() || !append) {
                out = new ObjectOutputStream(new FileOutputStream(filename));
            } else {
                out = new AppendableObjectOutputStream(new FileOutputStream(filename, append));
            }
            if (comprimir) {
                out.writeObject(comprimirObjeto(obj));
            } else {
                out.writeObject(obj);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Object> leerObjetos(String filename) {
        File file = new File(filename);
        List<Object> lst = new ArrayList<>();
        if (file.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(filename));
                while (true) {
                    lst.add(ois.readObject());
//                    String s = (String) ois.readObject();
//                    System.out.println(s);
                }
            } catch (EOFException e) {

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lst;
    }

    public static int contarObjetos(String filename) {
        File file = new File(filename);
        int total = 0;
        if (file.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(filename));
                while (true) {
                    ois.readObject();
                    total++;
//                    String s = (String) ois.readObject();
//                    System.out.println(s);
                }
            } catch (EOFException e) {

            } catch (Exception e) {
//                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return total;
    }

    public static Object leerObjeto(String filename, int indice) {
        return leerObjeto(filename, indice, false);
    }

    public static Object leerObjeto(String filename, int indice, boolean comprimido) {
        File file = new File(filename);
        int total = 0;
        Object objeto = null;
        boolean seguir = true;
        if (file.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(filename));
                while (seguir) {
                    objeto = ois.readObject();
                    if (total == indice) {
                        //descomprimo
                        if (comprimido) {
                            objeto = descomprimirObjeto((byte[]) objeto);
                        }
                        seguir = false;
                    } else {
                        objeto = null;
                    }
                    total++;
//                    String s = (String) ois.readObject();
//                    System.out.println(s);
                }
                ois = null;
            } catch (EOFException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return objeto;
    }

    private static class AppendableObjectOutputStream extends ObjectOutputStream {

        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // do not write a header, but reset:
            // this line added after another question
            // showed a problem with the original
            reset();
        }
    }
//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        List list = new ArrayList();
//        list.add("uno");
//        list.add("dos");
//        list.add("tres");
//        list.add("cuatro");
//        SerializarUtil.convertirBytes(list, "prueba.txt");
//        List otherList = (List) SerializarUtil.leerObjeto("prueba.txt");
//        System.out.println("La lista original es igual a la leida ? " + list.equals(otherList));
//    }

    public static byte[] comprimirObjeto(Object objeto) {

        byte[] bytes = null;
        ObjectOutputStream objectOut = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            objectOut = new ObjectOutputStream(baos);
            objectOut.writeObject(objeto);
            objectOut.close();
            bytes = Compresor.comprimirGZIP(baos.toByteArray());
        } catch (Exception ex) {
        } finally {
            try {
                objectOut.close();
            } catch (Exception ex) {
            }
        }
        return bytes;
    }

    public static Object descomprimirObjeto(byte[] bytes) {
        ObjectInputStream objectIn = null;
        if (bytes == null) {
            return null;
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Compresor.descomprimirGZIP(bytes));
            objectIn = new ObjectInputStream(bais);
            return (Object) objectIn.readObject();
        } catch (Exception ex) {
        } finally {
            try {
                if (objectIn != null) {
                    objectIn.close();
                }
            } catch (IOException ex) {
            }
        }
        return null;
    }

}
