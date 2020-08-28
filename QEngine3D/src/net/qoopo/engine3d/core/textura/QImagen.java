package net.qoopo.engine3d.core.textura;

import net.qoopo.engine3d.core.math.QColor;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class QImagen implements Serializable {

    transient private BufferedImage bi;
    transient private WritableRaster raster;
    private int[] data;
    private int ancho;
    private int alto;

    public QImagen(String resource) {
        try {
            InputStream is = getClass().getResourceAsStream(resource);
            if (is == null) {
                throw new RuntimeException("No se puede cargar el recurso " + resource + " !");
            }
            BufferedImage biProv = ImageIO.read(is);
            this.bi = new BufferedImage(biProv.getWidth(), biProv.getHeight(), BufferedImage.TYPE_INT_ARGB);
            this.bi.getGraphics().drawImage(biProv, 0, 0, null);
        } catch (IOException ex) {
            System.err.println("No se puede cargar el recurso " + resource + " !");
            Logger.getLogger(QImagen.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.ancho = this.bi.getWidth();
        this.alto = this.bi.getHeight();
        this.raster = this.bi.getRaster();
        this.data = ((DataBufferInt) this.raster.getDataBuffer()).getData();
    }

    public QImagen(Image biProv) {
        setImage(biProv);
    }

    public void setImage(Image biProv) {
        try {
//            this.bi = new BufferedImage(biProv.getWidth(), biProv.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            this.bi.getGraphics().drawImage(biProv, 0, 0, null);

            //Get current GraphicsConfiguration
            GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

            //Create a Compatible BufferedImage for translucent image
            this.bi = graphicsConfiguration.createCompatibleImage(biProv.getWidth(null), biProv.getHeight(null), Transparency.TRANSLUCENT);

            this.bi.getGraphics().drawImage(biProv, 0, 0, null);
            this.bi.getGraphics().dispose();
//            System.out.println("tipo de imagen " + bi.getType());
//            System.out.println("transparencia=" + bi.getTransparency());
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(QImagen.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.ancho = this.bi.getWidth();
        this.alto = this.bi.getHeight();
        this.raster = this.bi.getRaster();
        this.data = ((DataBufferInt) this.raster.getDataBuffer()).getData();
    }

    public int getAncho() {
        return this.ancho;
    }

    public int getAlto() {
        return this.alto;
    }

    public void setPixel(int x, int y, int[] c) {
        this.data[(x + y * this.ancho)] = (c[3] + (c[2] << 8) + (c[1] << 16) + (c[0] << 24));
    }

    public void setPixel(int x, int y, int rgb) {
        this.data[(x + y * this.ancho)] = rgb;
    }

    public int getPixel(int x, int y) {
        return this.data[(x + y * this.ancho)];
    }

    public void setPixel(int x, int y, QColor color) {
        setPixel(x, y, color.toRGB());
    }

    public void llenarColor(QColor color) {
        Arrays.fill(data, color.toRGB()); //1 ms, el sombreador de pixeles demora mas al inicio, es muy raro
//        //10 ms, pero el sombreador de pixeles no muere
//        int rgb = color.toRGB();
//        for (int i = 0; i < data.length; i++) {
//            data[i] = rgb;
////            data[i] = color.toRGB();
//        }
//        for (int x = 0; x < ancho; x++) {
//            for (int y = 0; y < alto; y++) {
//                setPixel(x, y, color);
////                setPixel(x, y, rgb);
//            }
//        }
    }

    public QColor getPixelQARGB(int x, int y) {
        return QColor.toQARGB(this.data[(x + y * this.ancho)]);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(ancho); //ancho
        out.writeInt(alto); //alto
        out.writeInt(data.length);//largo del arreglo de enteros
        for (int i : data) {
            out.writeInt(i);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        ancho = in.readInt();//ancho
        alto = in.readInt();//alto
        data = new int[in.readInt()];
        for (int i = 0; i < data.length; i++) {
            data[i] = in.readInt();
        }
    }

    public void destruir() {
        data = null;
        raster = null;
        bi = null;
    }

    public BufferedImage getBi() {
        return bi;
    }

    public void setBi(BufferedImage bi) {
        this.bi = bi;
    }

    @Override
    public QImagen clone()  {
        QImagen nueva = new QImagen(bi);
        nueva.setImage(bi);

        return nueva;
    }

}
