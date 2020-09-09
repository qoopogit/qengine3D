/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.editor.entidad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.editor.Principal;
import net.qoopo.engine3d.editor.util.ImagePreviewPanel;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.QRender;
import net.qoopo.engine3d.engines.render.superficie.QJPanel;
import net.qoopo.engine3d.engines.render.superficie.Superficie;

/**
 *
 * @author alberto
 */
public class EditorMaterial extends javax.swing.JPanel {

    private QMaterialBas activeMaterial;
    private QMotorRender renderVistaPrevia;
    private QEntidad fondoVistaPrevia = null;
    private QEntidad entidadVistaPrevia = new QEntidad("MaterialTest");
    private final JFileChooser chooser = new JFileChooser();
    private final ImagePreviewPanel preview = new ImagePreviewPanel();
    private BufferedImage mapaDifuso = null;
    private BufferedImage mapaEspecular = null;
    private BufferedImage mapaEmision = null;
    private BufferedImage mapaNormal = null;
    private BufferedImage mapaDesplazamiento = null;
    private BufferedImage mapaEntorno = null;
    private BufferedImage mapaAlpha = null;
    private BufferedImage mapaAO = null;
    private BufferedImage mapaRugosidad = null;
    private BufferedImage mapaMetalico = null;
    private BufferedImage mapaIrradiacion = null;

    private boolean objectLock = false;

    /**
     * Creates new form pnlEditorMaterial
     */
    public EditorMaterial() {
        initComponents();
        chooser.setCurrentDirectory(new File(QGlobal.RECURSOS));
        chooser.setAccessory(preview);
        chooser.addPropertyChangeListener(preview);
        txtNombre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (activeMaterial != null) {
                    activeMaterial.setNombre(txtNombre.getText());
                }
            }
        });

        iniciarVistaPrevia();
        spnSpecExp.setModel(new SpinnerNumberModel(50, 1, Double.POSITIVE_INFINITY, 1));
        spnNormalAmount.setModel(new SpinnerNumberModel(0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, .01));
//        spnFactorRefraccion.setModel(new SpinnerNumberModel(1, 0.25, 4, .05));
        spnFactorRefraccion.setModel(new SpinnerNumberModel(1.33, 0.0, 4, .05));
        spnFactorEmision.setModel(new SpinnerNumberModel(0, 0, 1, .05));
    }

    public EditorMaterial(QMaterialBas activeMaterial) {
        this();
        this.activeMaterial = activeMaterial;
        populateMaterialControl(activeMaterial);
        applyMaterialControl();
    }

    public void liberar() {
//         renderVistaPrevia.detener();
    }

    private void iniciarVistaPrevia() {
        try {
            QEscena universo = new QEscena();
            QEscena.INSTANCIA = null;// no lo vinculo a la instancia global

            pnlVistaPrevia.setLayout(new BorderLayout());
            QJPanel pnl = new QJPanel();
            pnlVistaPrevia.add(pnl, BorderLayout.CENTER);

            renderVistaPrevia = new QRender(universo, "Vista Previa", new Superficie(pnl), 0, 0);
            QCamara camara = new QCamara("Material");
            camara.lookAtPosicionObjetivo(new QVector3(1.2f, 1.2f, 1.2f), new QVector3(0, 0, 0), new QVector3(0, 1, 0));
            camara.frustrumLejos = 20.0f;
            renderVistaPrevia.setCamara(camara);

            QEntidad luz = new QEntidad("luz");
            luz.agregarComponente(new QLuzDireccional(new QVector3(-1f, -0.5f, -1.5f)));

            fondoVistaPrevia = crearFondoCuadros();
            entidadVistaPrevia = crearTestEsfera(new QMaterialBas(QColor.LIGHT_GRAY, 50));
            renderVistaPrevia.getEscena().agregarEntidad(fondoVistaPrevia);
            renderVistaPrevia.getEscena().agregarEntidad(entidadVistaPrevia);
            renderVistaPrevia.getEscena().agregarEntidad(luz);
            renderVistaPrevia.setMostrarEstadisticas(false);
            renderVistaPrevia.opciones.setDibujarLuces(false);
            renderVistaPrevia.setEfectosPostProceso(null);
            renderVistaPrevia.setInteractuar(false);
//            renderVistaPrevia.iniciar();
            renderVistaPrevia.resize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateMaterialControl(QMaterialBas material) {
        objectLock = true;
        try {
            this.activeMaterial = material;
//         renderVistaPrevia.iniciar();
            txtNombre.setText(material.getNombre());
            //vista previa
//        mtrVistaPrevia.getUniverso().getListaEntidades().get(0).get
            renderVistaPrevia.getEscena().eliminarEntidadSindestruir(entidadVistaPrevia);
            entidadVistaPrevia = crearTestCubo(material);
            renderVistaPrevia.getEscena().agregarEntidad(entidadVistaPrevia);
            pnlDiffuseColor.setBackground(material.getColorBase().getColor());
//        pnlSpecularColor.setBackground(material.getColorEspecular().getColor());
            if (material.getColorTransparente() != null) {
                pnlAlphaColor.setBackground(material.getColorTransparente().getColor());
            } else {
                pnlAlphaColor.setBackground(Color.BLACK);
            }
            spnSpecExp.setValue((float) material.getSpecularExponent());
            sldAlpha.setValue((int) (material.getTransAlfa() * sldAlpha.getMaximum()));
            sldMetalico.setValue((int) (material.getMetalico() * sldMetalico.getMaximum()));
            sldRugosidad.setValue((int) (material.getRugosidad() * sldRugosidad.getMaximum()));
            sldEspecularidad.setValue((int) (material.getEspecular() * sldEspecularidad.getMaximum()));

            mapaDifuso = new BufferedImage(pnlDifuso.getWidth(), pnlDifuso.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaNormal = new BufferedImage(pnlNormalMap.getWidth(), pnlNormalMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaDesplazamiento = new BufferedImage(pnlDispMap.getWidth(), pnlDispMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaEspecular = new BufferedImage(pnlEspecularMap.getWidth(), pnlEspecularMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaEmision = new BufferedImage(pnlEmisivoMap.getWidth(), pnlEmisivoMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaEntorno = new BufferedImage(pnlEntornoMap.getWidth(), pnlEntornoMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaAlpha = new BufferedImage(pnlAlphaMap.getWidth(), pnlAlphaMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaAO = new BufferedImage(pnlAOMap.getWidth(), pnlAOMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaRugosidad = new BufferedImage(pnlRugMap.getWidth(), pnlRugMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaMetalico = new BufferedImage(pnlMetalMap.getWidth(), pnlMetalMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
            mapaIrradiacion = new BufferedImage(pnlIrrMap.getWidth(), pnlIrrMap.getHeight(), BufferedImage.TYPE_INT_ARGB);

            if (material.getMapaColor() != null) {
                mapaDifuso.getGraphics().drawImage(material.getMapaColor().getTexture(pnlDifuso.getSize()), 0, 0, pnlDifuso);
            }

            if (material.getMapaEspecular() != null) {
                mapaEspecular.getGraphics().drawImage(material.getMapaEspecular().getTexture(pnlEspecularMap.getSize()), 0, 0, pnlEspecularMap);
            }

            if (material.getMapaEmisivo() != null) {
                mapaEmision.getGraphics().drawImage(material.getMapaEmisivo().getTexture(pnlEmisivoMap.getSize()), 0, 0, pnlEmisivoMap);
            }

            if (material.getMapaNormal() != null) {
                mapaNormal.getGraphics().drawImage(material.getMapaNormal().getTexture(pnlNormalMap.getSize()), 0, 0, pnlNormalMap);
            }
            if (material.getMapaDesplazamiento() != null) {
                mapaDesplazamiento.getGraphics().drawImage(material.getMapaDesplazamiento().getTexture(pnlDispMap.getSize()), 0, 0, pnlDispMap);
            }
            if (material.getMapaEntorno() != null) {
                mapaEntorno.getGraphics().drawImage(material.getMapaEntorno().getTexture(pnlEntornoMap.getSize()), 0, 0, pnlEntornoMap);
            }
            if (material.getMapaTransparencia() != null) {
                mapaAlpha.getGraphics().drawImage(material.getMapaTransparencia().getTexture(pnlAlphaMap.getSize()), 0, 0, pnlAlphaMap);
            }
            if (material.getMapaSAO() != null) {
                mapaAO.getGraphics().drawImage(material.getMapaSAO().getTexture(pnlAOMap.getSize()), 0, 0, pnlAOMap);
            }
            if (material.getMapaRugosidad() != null) {
                mapaRugosidad.getGraphics().drawImage(material.getMapaRugosidad().getTexture(pnlRugMap.getSize()), 0, 0, pnlRugMap);
            }
            if (material.getMapaMetalico() != null) {
                mapaMetalico.getGraphics().drawImage(material.getMapaMetalico().getTexture(pnlMetalMap.getSize()), 0, 0, pnlMetalMap);
            }
            if (material.getMapaIrradiacion() != null) {
                mapaIrradiacion.getGraphics().drawImage(material.getMapaIrradiacion().getTexture(pnlIrrMap.getSize()), 0, 0, pnlIrrMap);
            }
            pnlDifuso.paint(pnlDifuso.getGraphics());
            pnlNormalMap.paint(pnlNormalMap.getGraphics());
            pnlDispMap.paint(pnlDispMap.getGraphics());
            pnlEmisivoMap.paint(pnlEmisivoMap.getGraphics());
            pnlEspecularMap.paint(pnlEspecularMap.getGraphics());
            pnlEntornoMap.paint(pnlEntornoMap.getGraphics());
            pnlAlphaMap.paint(pnlAlphaMap.getGraphics());
            pnlAOMap.paint(pnlAOMap.getGraphics());
            pnlRugMap.paint(pnlRugMap.getGraphics());
            pnlMetalMap.paint(pnlMetalMap.getGraphics());
            pnlIrrMap.paint(pnlIrrMap.getGraphics());
            spnNormalAmount.setValue(material.getFactorNormal());
            spnFactorRefraccion.setValue(material.getIndiceRefraccion());
            spnFactorEmision.setValue(material.getFactorEmision());
            chkTransparencia.setSelected(material.isTransparencia());
            chkReflexion.setSelected(material.isReflexion());
            chkRefraccion.setSelected(material.isRefraccion());
            optCubo.setSelected(material.getTipoMapaEntorno() == 1);
            optHDRI.setSelected(material.getTipoMapaEntorno() == 2);
            objectLock = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QEntidad crearFondoCuadros() {
        try {
            QEntidad entidad = new QEntidad("Fondo");
            QTextura text = new QTextura(ImageIO.read(Principal.class.getResourceAsStream("/res/fondo.jpg")));
            QMaterialBas matFondo = new QMaterialBas(text, 50);
            entidad.agregarComponente(QUtilNormales.invertirNormales(QMaterialUtil.aplicarMaterial(new QCaja(10f, 5.0f), matFondo)));
            entidad.mover(3, 3, 3);
            return entidad;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private QEntidad crearTestPlano(QMaterialBas material) {
        QUtilComponentes.eliminarComponenteGeometria(entidadVistaPrevia);
        entidadVistaPrevia.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlano(2.0f, 2.0f), material));
        entidadVistaPrevia.rotar(Math.toRadians(45), Math.toRadians(45), 0);
//        entidadVistaPrevia.transformacion.getRotacion().getCuaternion().lookAt(QVector3.unitario_xyz, QVector3.unitario_y);
//        entidadVistaPrevia.transformacion.getRotacion().actualizarAngulos();
        entidadVistaPrevia.escalar(1, 1, 1);
        return entidadVistaPrevia;
    }

    private QEntidad crearTestCubo(QMaterialBas material) {
        QUtilComponentes.eliminarComponenteGeometria(entidadVistaPrevia);
        entidadVistaPrevia.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(1.0f), material));
        entidadVistaPrevia.getTransformacion().getRotacion().getCuaternion().set(0, 0, 0, 1);
        entidadVistaPrevia.getTransformacion().getRotacion().actualizarAngulos();
        entidadVistaPrevia.escalar(1, 1, 1);
        return entidadVistaPrevia;
    }

    private QEntidad crearTestEsfera(QMaterialBas material) {
        QUtilComponentes.eliminarComponenteGeometria(entidadVistaPrevia);
        entidadVistaPrevia.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(.5f), material));
        entidadVistaPrevia.getTransformacion().getRotacion().getCuaternion().set(0, 0, 0, 1);
        entidadVistaPrevia.getTransformacion().getRotacion().actualizarAngulos();
        entidadVistaPrevia.escalar(1, 1, 1);
        return entidadVistaPrevia;
    }

    private QEntidad crearTetera(QMaterialBas material) {
        QUtilComponentes.eliminarComponenteGeometria(entidadVistaPrevia);
        QEntidad tetera = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot.obj")).get(0);
        for (QComponente comp : tetera.getComponentes()) {
            if (comp instanceof QGeometria) {
                QMaterialUtil.aplicarMaterial((QGeometria) comp, material);
            }
        }
        entidadVistaPrevia.agregarComponente(QUtilComponentes.getGeometria(tetera));
        entidadVistaPrevia.getTransformacion().getRotacion().getCuaternion().set(0, 0, 0, 1);
        entidadVistaPrevia.getTransformacion().getRotacion().actualizarAngulos();
        entidadVistaPrevia.escalar(0.5f, 0.5f, 0.5f);
        return entidadVistaPrevia;
    }

    private void applyMaterialControl() {
        if (!objectLock) {
            activeMaterial.setColorBase(new QColor(1, (float) pnlDiffuseColor.getBackground().getRed() / 255.0f,
                    (float) pnlDiffuseColor.getBackground().getGreen() / 255.0f,
                    (float) pnlDiffuseColor.getBackground().getBlue() / 255.0f));

//            activeMaterial.setColorEspecular(new QColor(1, (float) pnlSpecularColor.getBackground().getRed() / 255.0f,
//                    (float) pnlSpecularColor.getBackground().getGreen() / 255.0f,
//                    (float) pnlSpecularColor.getBackground().getBlue() / 255.0f));
            activeMaterial.setColorTransparente(new QColor(1, (float) pnlAlphaColor.getBackground().getRed() / 255.0f,
                    (float) pnlAlphaColor.getBackground().getGreen() / 255.0f,
                    (float) pnlAlphaColor.getBackground().getBlue() / 255.0f));

            activeMaterial.setSpecularExponent((int) getFloatFromSpinner(spnSpecExp));
            activeMaterial.setTransAlfa((float) sldAlpha.getValue() / sldAlpha.getMaximum());
            activeMaterial.setFactorNormal(getFloatFromSpinner(spnNormalAmount));
            activeMaterial.setRugosidad((float) sldRugosidad.getValue() / sldRugosidad.getMaximum());
            activeMaterial.setMetalico((float) sldMetalico.getValue() / sldMetalico.getMaximum());
            activeMaterial.setEspecular((float) sldEspecularidad.getValue() / sldEspecularidad.getMaximum());

            sldRugosidad.setToolTipText("Rugosidad (" + activeMaterial.getRugosidad() + "):");
            sldMetalico.setToolTipText("Metalico (" + activeMaterial.getMetalico() + "):");
            sldEspecularidad.setToolTipText("Especularidad (" + activeMaterial.getEspecular() + "):");

//            activeMaterial.setFactorReflexion(getFloatFromSpinner(spnFactorReflexion));
            activeMaterial.setIndiceRefraccion(getFloatFromSpinner(spnFactorRefraccion));
            activeMaterial.setFactorEmision(getFloatFromSpinner(spnFactorEmision));
            activeMaterial.setTransparencia(chkTransparencia.isSelected());
            activeMaterial.setReflexion(chkReflexion.isSelected());
            activeMaterial.setRefraccion(chkRefraccion.isSelected());

            if (optHDRI.isSelected()) {
                activeMaterial.setTipoMapaEntorno(2);
            } else {
                activeMaterial.setTipoMapaEntorno(1);
            }
            if (activeMaterial.getMapaColor() != null) {
                activeMaterial.getMapaColor().setMuestrasU(Float.parseFloat(txtMU_Difusa.getText()));
                activeMaterial.getMapaColor().setMuestrasV(Float.parseFloat(txtMV_Difusa.getText()));
            }
            if (activeMaterial.getMapaEspecular() != null) {
                activeMaterial.getMapaEspecular().setMuestrasU(Float.parseFloat(txtMU_Especular.getText()));
                activeMaterial.getMapaEspecular().setMuestrasV(Float.parseFloat(txtMV_Especular.getText()));
            }
            if (activeMaterial.getMapaEmisivo() != null) {
                activeMaterial.getMapaEmisivo().setMuestrasU(Float.parseFloat(txtMU_Emisivo.getText()));
                activeMaterial.getMapaEmisivo().setMuestrasV(Float.parseFloat(txtMV_Emisivo.getText()));
            }
            if (activeMaterial.getMapaNormal() != null) {
                activeMaterial.getMapaNormal().setMuestrasU(Float.parseFloat(txtMU_Normal.getText()));
                activeMaterial.getMapaNormal().setMuestrasV(Float.parseFloat(txtMV_Normal.getText()));
            }
            if (activeMaterial.getMapaDesplazamiento() != null) {
                activeMaterial.getMapaDesplazamiento().setMuestrasU(Float.parseFloat(txtMU_Disp.getText()));
                activeMaterial.getMapaDesplazamiento().setMuestrasV(Float.parseFloat(txtMV_Disp.getText()));
            }
            if (activeMaterial.getMapaTransparencia() != null) {
                activeMaterial.getMapaTransparencia().setMuestrasU(Float.parseFloat(txtMU_Transp.getText()));
                activeMaterial.getMapaTransparencia().setMuestrasV(Float.parseFloat(txtMV_Transp.getText()));
            }
            if (activeMaterial.getMapaEntorno() != null) {
                activeMaterial.getMapaEntorno().setMuestrasU(Float.parseFloat(txtMU_Entorno.getText()));
                activeMaterial.getMapaEntorno().setMuestrasV(Float.parseFloat(txtMV_Entorno.getText()));
            }
            if (activeMaterial.getMapaSAO() != null) {
                activeMaterial.getMapaSAO().setMuestrasU(Float.parseFloat(txtMU_AO.getText()));
                activeMaterial.getMapaSAO().setMuestrasV(Float.parseFloat(txtMV_AO.getText()));
            }
            if (activeMaterial.getMapaRugosidad() != null) {
                activeMaterial.getMapaRugosidad().setMuestrasU(Float.parseFloat(txtMU_Rugosidad.getText()));
                activeMaterial.getMapaRugosidad().setMuestrasV(Float.parseFloat(txtMV_Rugosidad.getText()));
            }
            if (activeMaterial.getMapaMetalico() != null) {
                activeMaterial.getMapaMetalico().setMuestrasU(Float.parseFloat(txtMU_Metalico.getText()));
                activeMaterial.getMapaMetalico().setMuestrasV(Float.parseFloat(txtMV_Metalico.getText()));
            }
            if (activeMaterial.getMapaIrradiacion() != null) {
                activeMaterial.getMapaIrradiacion().setMuestrasU(Float.parseFloat(txtMU_Irradiacion.getText()));
                activeMaterial.getMapaIrradiacion().setMuestrasV(Float.parseFloat(txtMV_Irradiacion.getText()));
            }

//            activeMaterial.environmentReflection = (float) sldEnvReflection.getValue() / sldEnvReflection.getMaximum();
        }
    }

    private float getFloatFromSpinner(JSpinner spinner) {
        Object value = spinner.getValue();
        if (value instanceof Double) {
            return (float) (double) (Double) value;
        } else if (value instanceof Float) {
            return (float) value;
        }
        return -1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel13 = new javax.swing.JPanel();
        pnlVistaPrevia = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        pnlDiffuseColor = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblSpecExp1 = new javax.swing.JLabel();
        sldAlpha = new javax.swing.JSlider();
        jLabel14 = new javax.swing.JLabel();
        chkTransparencia = new javax.swing.JCheckBox();
        pnlAlphaColor = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        pnlMapas = new javax.swing.JPanel();
        pnlNormalMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaNormal != null)
                g.drawImage(mapaNormal, 0, 0, this);
            }
        };
        jLabel11 = new javax.swing.JLabel();
        pnlDifuso = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaDifuso != null)
                g.drawImage(mapaDifuso, 0, 0, this);
            }
        };
        jLabel10 = new javax.swing.JLabel();
        btnSetMapaDifuso = new javax.swing.JButton();
        btnNormalMap = new javax.swing.JButton();
        pnlEspecularMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaDifuso != null)
                g.drawImage(mapaEspecular, 0, 0, this);
            }
        };
        jLabel15 = new javax.swing.JLabel();
        pnlEmisivoMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaDifuso != null)
                g.drawImage(mapaEmision, 0, 0, this);
            }
        };
        jLabel16 = new javax.swing.JLabel();
        btnMapaEspecular = new javax.swing.JButton();
        btnMapaEmisivo = new javax.swing.JButton();
        btnEliminaMapaDifuso = new javax.swing.JButton();
        btnEliminaMapaEspecular = new javax.swing.JButton();
        btnEliminaMapaEmisivo = new javax.swing.JButton();
        btnEliminaMapaNormal = new javax.swing.JButton();
        btnEliminaMapaEntorno = new javax.swing.JButton();
        btnEntornoMap = new javax.swing.JButton();
        pnlEntornoMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaEntorno != null)
                g.drawImage(mapaEntorno, 0, 0, this);
            }
        };
        jLabel12 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        btnEliminaMapaAlpha = new javax.swing.JButton();
        btnAlphaMap = new javax.swing.JButton();
        pnlAlphaMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaNormal != null)
                g.drawImage(mapaAlpha, 0, 0, this);
            }
        };
        jLabel13 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        btnEliminaMapaDisp = new javax.swing.JButton();
        btnDispMap = new javax.swing.JButton();
        pnlDispMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaDesplazamiento != null)
                g.drawImage(mapaDesplazamiento, 0, 0, this);
            }
        };
        jLabel17 = new javax.swing.JLabel();
        btnEntornoMapCrear = new javax.swing.JButton();
        btnSetMapaDifusoCrear = new javax.swing.JButton();
        txtMU_Difusa = new javax.swing.JTextField();
        txtMV_Difusa = new javax.swing.JTextField();
        txtMU_Especular = new javax.swing.JTextField();
        txtMV_Especular = new javax.swing.JTextField();
        txtMU_Emisivo = new javax.swing.JTextField();
        txtMV_Emisivo = new javax.swing.JTextField();
        txtMU_Normal = new javax.swing.JTextField();
        txtMV_Normal = new javax.swing.JTextField();
        txtMU_Disp = new javax.swing.JTextField();
        txtMV_Disp = new javax.swing.JTextField();
        txtMV_Transp = new javax.swing.JTextField();
        txtMU_Transp = new javax.swing.JTextField();
        txtMV_Entorno = new javax.swing.JTextField();
        txtMU_Entorno = new javax.swing.JTextField();
        txtMV_AO = new javax.swing.JTextField();
        txtMU_AO = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        btnEliminaMapaAO = new javax.swing.JButton();
        btnAOMap = new javax.swing.JButton();
        pnlAOMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaEntorno != null)
                g.drawImage(mapaAO, 0, 0, this);
            }
        };
        jLabel18 = new javax.swing.JLabel();
        txtMU_Rugosidad = new javax.swing.JTextField();
        txtMV_Rugosidad = new javax.swing.JTextField();
        pnlRugMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaDifuso != null)
                g.drawImage(mapaRugosidad, 0, 0, this);
            }
        };
        jLabel19 = new javax.swing.JLabel();
        btnMapaRugosidad = new javax.swing.JButton();
        btnEliminaMapaRugosidad = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        txtMU_Metalico = new javax.swing.JTextField();
        txtMV_Metalico = new javax.swing.JTextField();
        pnlMetalMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaDifuso != null)
                g.drawImage(mapaMetalico, 0, 0, this);
            }
        };
        jLabel20 = new javax.swing.JLabel();
        btnMapaMetalico = new javax.swing.JButton();
        btnEliminaMapaMetalico = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        txtMV_Irradiacion = new javax.swing.JTextField();
        txtMU_Irradiacion = new javax.swing.JTextField();
        jButton15 = new javax.swing.JButton();
        btnEliminaMapaIrradiacion = new javax.swing.JButton();
        btnIrrMap = new javax.swing.JButton();
        pnlIrrMap = new javax.swing.JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (mapaEntorno != null)
                g.drawImage(mapaIrradiacion, 0, 0, this);
            }
        };
        jLabel21 = new javax.swing.JLabel();
        btnIrradiacionMapCrear = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        chkRefraccion = new javax.swing.JCheckBox();
        lblNormalAmount3 = new javax.swing.JLabel();
        spnFactorRefraccion = new javax.swing.JSpinner();
        jPanel15 = new javax.swing.JPanel();
        chkReflexion = new javax.swing.JCheckBox();
        jPanel16 = new javax.swing.JPanel();
        lblSpecExp = new javax.swing.JLabel();
        spnSpecExp = new javax.swing.JSpinner();
        spnNormalAmount = new javax.swing.JSpinner();
        lblNormalAmount = new javax.swing.JLabel();
        lblNormalAmount2 = new javax.swing.JLabel();
        spnFactorEmision = new javax.swing.JSpinner();
        lblNormalAmount4 = new javax.swing.JLabel();
        optHDRI = new javax.swing.JRadioButton();
        optCubo = new javax.swing.JRadioButton();
        lblRugosidad = new javax.swing.JLabel();
        sldRugosidad = new javax.swing.JSlider();
        lblMetalico = new javax.swing.JLabel();
        sldMetalico = new javax.swing.JSlider();
        lblEspecularidad = new javax.swing.JLabel();
        sldEspecularidad = new javax.swing.JSlider();

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Vista Previa", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        pnlVistaPrevia.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout pnlVistaPreviaLayout = new javax.swing.GroupLayout(pnlVistaPrevia);
        pnlVistaPrevia.setLayout(pnlVistaPreviaLayout);
        pnlVistaPreviaLayout.setHorizontalGroup(
            pnlVistaPreviaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlVistaPreviaLayout.setVerticalGroup(
            pnlVistaPreviaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/quad_16.png"))); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/cube_16.png"))); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/sphere_16.png"))); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/teapot_16.png"))); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(pnlVistaPrevia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton11)
                    .addComponent(jButton12)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlVistaPrevia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton14))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));

        pnlDiffuseColor.setBackground(new java.awt.Color(0, 0, 0));
        pnlDiffuseColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlDiffuseColorMousePressed(evt);
            }
        });

        javax.swing.GroupLayout pnlDiffuseColorLayout = new javax.swing.GroupLayout(pnlDiffuseColor);
        pnlDiffuseColor.setLayout(pnlDiffuseColorLayout);
        pnlDiffuseColorLayout.setHorizontalGroup(
            pnlDiffuseColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlDiffuseColorLayout.setVerticalGroup(
            pnlDiffuseColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel8.setText("Color Base:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(30, 30, 30)
                .addComponent(pnlDiffuseColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDiffuseColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transparencia RGB", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        lblSpecExp1.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblSpecExp1.setText("Alpha");

        sldAlpha.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        sldAlpha.setValue(200);
        sldAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldAlphaStateChanged(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel14.setText("Color Transparente:");

        chkTransparencia.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        chkTransparencia.setText("Tiene transparencia");
        chkTransparencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTransparenciaActionPerformed(evt);
            }
        });

        pnlAlphaColor.setBackground(new java.awt.Color(0, 0, 0));
        pnlAlphaColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlAlphaColorMousePressed(evt);
            }
        });

        javax.swing.GroupLayout pnlAlphaColorLayout = new javax.swing.GroupLayout(pnlAlphaColor);
        pnlAlphaColor.setLayout(pnlAlphaColorLayout);
        pnlAlphaColorLayout.setHorizontalGroup(
            pnlAlphaColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlAlphaColorLayout.setVerticalGroup(
            pnlAlphaColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(chkTransparencia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(lblSpecExp1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sldAlpha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAlphaColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(chkTransparencia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sldAlpha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSpecExp1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14)
                    .addComponent(pnlAlphaColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Texturas/Mapas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        jScrollPane6.setBorder(null);

        pnlMapas.setName(""); // NOI18N

        pnlNormalMap.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout pnlNormalMapLayout = new javax.swing.GroupLayout(pnlNormalMap);
        pnlNormalMap.setLayout(pnlNormalMapLayout);
        pnlNormalMapLayout.setHorizontalGroup(
            pnlNormalMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlNormalMapLayout.setVerticalGroup(
            pnlNormalMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel11.setText("Nor.:");

        pnlDifuso.setBackground(new java.awt.Color(0, 0, 0));
        pnlDifuso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlDifusoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlDifusoLayout = new javax.swing.GroupLayout(pnlDifuso);
        pnlDifuso.setLayout(pnlDifusoLayout);
        pnlDifusoLayout.setHorizontalGroup(
            pnlDifusoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );
        pnlDifusoLayout.setVerticalGroup(
            pnlDifusoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel10.setText("Difuso:");

        btnSetMapaDifuso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnSetMapaDifuso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMapaDifusoActionPerformed(evt);
            }
        });

        btnNormalMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnNormalMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNormalMapActionPerformed(evt);
            }
        });

        pnlEspecularMap.setBackground(new java.awt.Color(0, 0, 0));
        pnlEspecularMap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlEspecularMapMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlEspecularMapLayout = new javax.swing.GroupLayout(pnlEspecularMap);
        pnlEspecularMap.setLayout(pnlEspecularMapLayout);
        pnlEspecularMapLayout.setHorizontalGroup(
            pnlEspecularMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlEspecularMapLayout.setVerticalGroup(
            pnlEspecularMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel15.setText("Esp.:");

        pnlEmisivoMap.setBackground(new java.awt.Color(0, 0, 0));
        pnlEmisivoMap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlEmisivoMapMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlEmisivoMapLayout = new javax.swing.GroupLayout(pnlEmisivoMap);
        pnlEmisivoMap.setLayout(pnlEmisivoMapLayout);
        pnlEmisivoMapLayout.setHorizontalGroup(
            pnlEmisivoMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlEmisivoMapLayout.setVerticalGroup(
            pnlEmisivoMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel16.setText("Emi.:");

        btnMapaEspecular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnMapaEspecular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaEspecularActionPerformed(evt);
            }
        });

        btnMapaEmisivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnMapaEmisivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaEmisivoActionPerformed(evt);
            }
        });

        btnEliminaMapaDifuso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaDifuso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaDifusoActionPerformed(evt);
            }
        });

        btnEliminaMapaEspecular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaEspecular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaEspecularActionPerformed(evt);
            }
        });

        btnEliminaMapaEmisivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaEmisivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaEmisivoActionPerformed(evt);
            }
        });

        btnEliminaMapaNormal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaNormalActionPerformed(evt);
            }
        });

        btnEliminaMapaEntorno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaEntorno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaEntornoActionPerformed(evt);
            }
        });

        btnEntornoMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnEntornoMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntornoMapActionPerformed(evt);
            }
        });

        pnlEntornoMap.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout pnlEntornoMapLayout = new javax.swing.GroupLayout(pnlEntornoMap);
        pnlEntornoMap.setLayout(pnlEntornoMapLayout);
        pnlEntornoMapLayout.setHorizontalGroup(
            pnlEntornoMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlEntornoMapLayout.setVerticalGroup(
            pnlEntornoMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel12.setText("Ent.:");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        btnEliminaMapaAlpha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaAlpha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaAlphaActionPerformed(evt);
            }
        });

        btnAlphaMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnAlphaMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlphaMapActionPerformed(evt);
            }
        });

        pnlAlphaMap.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout pnlAlphaMapLayout = new javax.swing.GroupLayout(pnlAlphaMap);
        pnlAlphaMap.setLayout(pnlAlphaMapLayout);
        pnlAlphaMapLayout.setHorizontalGroup(
            pnlAlphaMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlAlphaMapLayout.setVerticalGroup(
            pnlAlphaMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel13.setText("Tran.:");

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        btnEliminaMapaDisp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaDisp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaDispActionPerformed(evt);
            }
        });

        btnDispMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnDispMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDispMapActionPerformed(evt);
            }
        });

        pnlDispMap.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout pnlDispMapLayout = new javax.swing.GroupLayout(pnlDispMap);
        pnlDispMap.setLayout(pnlDispMapLayout);
        pnlDispMapLayout.setHorizontalGroup(
            pnlDispMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlDispMapLayout.setVerticalGroup(
            pnlDispMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel17.setText("Disp.:");

        btnEntornoMapCrear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnEntornoMapCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntornoMapCrearActionPerformed(evt);
            }
        });

        btnSetMapaDifusoCrear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnSetMapaDifusoCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetMapaDifusoCrearActionPerformed(evt);
            }
        });

        txtMU_Difusa.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Difusa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Difusa.setText("1");
        txtMU_Difusa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_DifusaActionPerformed(evt);
            }
        });
        txtMU_Difusa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMU_DifusaKeyPressed(evt);
            }
        });

        txtMV_Difusa.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Difusa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Difusa.setText("1");
        txtMV_Difusa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_DifusaActionPerformed(evt);
            }
        });
        txtMV_Difusa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMV_DifusaKeyPressed(evt);
            }
        });

        txtMU_Especular.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Especular.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Especular.setText("1");
        txtMU_Especular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_EspecularActionPerformed(evt);
            }
        });

        txtMV_Especular.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Especular.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Especular.setText("1");
        txtMV_Especular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_EspecularActionPerformed(evt);
            }
        });

        txtMU_Emisivo.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Emisivo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Emisivo.setText("1");
        txtMU_Emisivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_EmisivoActionPerformed(evt);
            }
        });

        txtMV_Emisivo.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Emisivo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Emisivo.setText("1");
        txtMV_Emisivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_EmisivoActionPerformed(evt);
            }
        });

        txtMU_Normal.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Normal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Normal.setText("1");
        txtMU_Normal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_NormalActionPerformed(evt);
            }
        });

        txtMV_Normal.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Normal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Normal.setText("1");
        txtMV_Normal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_NormalActionPerformed(evt);
            }
        });

        txtMU_Disp.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Disp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Disp.setText("1");
        txtMU_Disp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_DispActionPerformed(evt);
            }
        });

        txtMV_Disp.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Disp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Disp.setText("1");
        txtMV_Disp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_DispActionPerformed(evt);
            }
        });

        txtMV_Transp.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Transp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Transp.setText("1");
        txtMV_Transp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_TranspActionPerformed(evt);
            }
        });

        txtMU_Transp.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Transp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Transp.setText("1");
        txtMU_Transp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_TranspActionPerformed(evt);
            }
        });

        txtMV_Entorno.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Entorno.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Entorno.setText("1");
        txtMV_Entorno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_EntornoActionPerformed(evt);
            }
        });

        txtMU_Entorno.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Entorno.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Entorno.setText("1");
        txtMU_Entorno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_EntornoActionPerformed(evt);
            }
        });

        txtMV_AO.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_AO.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_AO.setText("1");
        txtMV_AO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_AOActionPerformed(evt);
            }
        });

        txtMU_AO.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_AO.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_AO.setText("1");
        txtMU_AO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_AOActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        btnEliminaMapaAO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaAO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaAOActionPerformed(evt);
            }
        });

        btnAOMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnAOMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAOMapActionPerformed(evt);
            }
        });

        pnlAOMap.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout pnlAOMapLayout = new javax.swing.GroupLayout(pnlAOMap);
        pnlAOMap.setLayout(pnlAOMapLayout);
        pnlAOMapLayout.setHorizontalGroup(
            pnlAOMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlAOMapLayout.setVerticalGroup(
            pnlAOMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel18.setText("AO:");

        txtMU_Rugosidad.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Rugosidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Rugosidad.setText("1");
        txtMU_Rugosidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_RugosidadActionPerformed(evt);
            }
        });

        txtMV_Rugosidad.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Rugosidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Rugosidad.setText("1");
        txtMV_Rugosidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_RugosidadActionPerformed(evt);
            }
        });

        pnlRugMap.setBackground(new java.awt.Color(0, 0, 0));
        pnlRugMap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlRugMapMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlRugMapLayout = new javax.swing.GroupLayout(pnlRugMap);
        pnlRugMap.setLayout(pnlRugMapLayout);
        pnlRugMapLayout.setHorizontalGroup(
            pnlRugMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlRugMapLayout.setVerticalGroup(
            pnlRugMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel19.setText("Rug.");

        btnMapaRugosidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnMapaRugosidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaRugosidadActionPerformed(evt);
            }
        });

        btnEliminaMapaRugosidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaRugosidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaRugosidadActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        txtMU_Metalico.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Metalico.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Metalico.setText("1");
        txtMU_Metalico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_MetalicoActionPerformed(evt);
            }
        });

        txtMV_Metalico.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Metalico.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Metalico.setText("1");
        txtMV_Metalico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_MetalicoActionPerformed(evt);
            }
        });

        pnlMetalMap.setBackground(new java.awt.Color(0, 0, 0));
        pnlMetalMap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlMetalMapMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlMetalMapLayout = new javax.swing.GroupLayout(pnlMetalMap);
        pnlMetalMap.setLayout(pnlMetalMapLayout);
        pnlMetalMapLayout.setHorizontalGroup(
            pnlMetalMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );
        pnlMetalMapLayout.setVerticalGroup(
            pnlMetalMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        jLabel20.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel20.setText("Metal.");

        btnMapaMetalico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnMapaMetalico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaMetalicoActionPerformed(evt);
            }
        });

        btnEliminaMapaMetalico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaMetalico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaMetalicoActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        txtMV_Irradiacion.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMV_Irradiacion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMV_Irradiacion.setText("1");
        txtMV_Irradiacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMV_IrradiacionActionPerformed(evt);
            }
        });

        txtMU_Irradiacion.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMU_Irradiacion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMU_Irradiacion.setText("1");
        txtMU_Irradiacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMU_IrradiacionActionPerformed(evt);
            }
        });

        jButton15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_quad_16.png"))); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        btnEliminaMapaIrradiacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_16.png"))); // NOI18N
        btnEliminaMapaIrradiacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminaMapaIrradiacionActionPerformed(evt);
            }
        });

        btnIrrMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnIrrMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIrrMapActionPerformed(evt);
            }
        });

        pnlIrrMap.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout pnlIrrMapLayout = new javax.swing.GroupLayout(pnlIrrMap);
        pnlIrrMap.setLayout(pnlIrrMapLayout);
        pnlIrrMapLayout.setHorizontalGroup(
            pnlIrrMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );
        pnlIrrMapLayout.setVerticalGroup(
            pnlIrrMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel21.setText("Irr.");

        btnIrradiacionMapCrear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/text_16.png"))); // NOI18N
        btnIrradiacionMapCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIrradiacionMapCrearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlMapasLayout = new javax.swing.GroupLayout(pnlMapas);
        pnlMapas.setLayout(pnlMapasLayout);
        pnlMapasLayout.setHorizontalGroup(
            pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapasLayout.createSequentialGroup()
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlMapasLayout.createSequentialGroup()
                        .addComponent(pnlIrrMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnIrrMap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminaMapaIrradiacion, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlMapasLayout.createSequentialGroup()
                        .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlMapasLayout.createSequentialGroup()
                                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel17)
                                    .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel21)
                                        .addComponent(jLabel18))
                                    .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel19)
                                        .addComponent(jLabel15)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(pnlEntornoMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlAlphaMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlDispMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlNormalMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlEmisivoMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlEspecularMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlAOMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pnlDifuso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pnlRugMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(btnEntornoMap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(btnAlphaMap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(btnDispMap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(btnNormalMap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(btnMapaEmisivo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(btnMapaEspecular, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(btnSetMapaDifuso, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnAOMap, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlMapasLayout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(btnMapaRugosidad, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlMapasLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pnlMetalMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMapaMetalico, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEliminaMapaRugosidad, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnEliminaMapaEntorno, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnEliminaMapaAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnEliminaMapaDisp, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnEliminaMapaNormal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnEliminaMapaEmisivo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnEliminaMapaEspecular, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnEliminaMapaDifuso, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminaMapaAO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnEliminaMapaMetalico, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 21, Short.MAX_VALUE)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlMapasLayout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnlMapasLayout.createSequentialGroup()
                                    .addComponent(btnSetMapaDifusoCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtMU_Difusa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtMV_Difusa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlMapasLayout.createSequentialGroup()
                                    .addGap(25, 25, 25)
                                    .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnlMapasLayout.createSequentialGroup()
                                            .addComponent(txtMU_Emisivo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMV_Emisivo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnlMapasLayout.createSequentialGroup()
                                            .addComponent(txtMU_Especular, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMV_Especular, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnlMapasLayout.createSequentialGroup()
                                            .addComponent(txtMU_Normal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMV_Normal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnlMapasLayout.createSequentialGroup()
                                            .addComponent(txtMU_Disp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMV_Disp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pnlMapasLayout.createSequentialGroup()
                                            .addComponent(txtMU_Transp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMV_Transp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(pnlMapasLayout.createSequentialGroup()
                                    .addComponent(btnEntornoMapCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnlMapasLayout.createSequentialGroup()
                                            .addComponent(txtMU_Entorno, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMV_Entorno, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMapasLayout.createSequentialGroup()
                                            .addComponent(txtMU_AO, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMV_AO, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMapasLayout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pnlMapasLayout.createSequentialGroup()
                                    .addComponent(txtMU_Metalico, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtMV_Metalico, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlMapasLayout.createSequentialGroup()
                                    .addComponent(txtMU_Rugosidad, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtMV_Rugosidad, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMapasLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnIrradiacionMapCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMU_Irradiacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMV_Irradiacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        pnlMapasLayout.setVerticalGroup(
            pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMapasLayout.createSequentialGroup()
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10)
                    .addComponent(txtMU_Difusa)
                    .addComponent(btnSetMapaDifusoCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaDifuso, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlDifuso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSetMapaDifuso, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMV_Difusa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15)
                    .addComponent(btnMapaEspecular, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaEspecular, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtMV_Especular)
                    .addComponent(txtMU_Especular)
                    .addComponent(pnlEspecularMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnMapaRugosidad, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaRugosidad, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtMV_Rugosidad)
                    .addComponent(txtMU_Rugosidad)
                    .addComponent(pnlRugMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel20)
                    .addComponent(pnlMetalMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMapaMetalico, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminaMapaMetalico, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtMU_Metalico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtMV_Metalico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel16)
                    .addComponent(btnMapaEmisivo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaEmisivo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtMU_Emisivo)
                    .addComponent(txtMV_Emisivo)
                    .addComponent(pnlEmisivoMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11)
                    .addComponent(btnNormalMap, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaNormal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtMU_Normal)
                    .addComponent(txtMV_Normal)
                    .addComponent(pnlNormalMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel17)
                    .addComponent(btnDispMap, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaDisp, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtMU_Disp)
                    .addComponent(txtMV_Disp)
                    .addComponent(pnlDispMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13)
                    .addComponent(btnAlphaMap, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtMU_Transp)
                    .addComponent(txtMV_Transp)
                    .addComponent(pnlAlphaMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12)
                    .addComponent(txtMU_Entorno)
                    .addComponent(txtMV_Entorno)
                    .addComponent(btnEntornoMap, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaEntorno, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEntornoMapCrear, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEntornoMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18)
                    .addComponent(txtMU_AO)
                    .addComponent(txtMV_AO)
                    .addComponent(btnAOMap, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnEliminaMapaAO, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlAOMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMapasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtMU_Irradiacion)
                        .addComponent(txtMV_Irradiacion)
                        .addComponent(btnIrrMap, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(btnEliminaMapaIrradiacion, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(pnlIrrMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(btnIrradiacionMapCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jScrollPane6.setViewportView(pnlMapas);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel1.setText("Nombre:");

        txtNombre.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Entorno - Refracción", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        chkRefraccion.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        chkRefraccion.setText("Tiene refracción");
        chkRefraccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRefraccionActionPerformed(evt);
            }
        });

        lblNormalAmount3.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblNormalAmount3.setText("Índice Refracción:");
        lblNormalAmount3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        spnFactorRefraccion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnFactorRefraccionStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(chkRefraccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(lblNormalAmount3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnFactorRefraccion))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(chkRefraccion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNormalAmount3)
                    .addComponent(spnFactorRefraccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Entorno - Reflexión", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        chkReflexion.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        chkReflexion.setText("Tiene reflexión");
        chkReflexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkReflexionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(chkReflexion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(chkReflexion)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Propiedades"));

        lblSpecExp.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblSpecExp.setText("Exp. especular:");
        lblSpecExp.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        spnSpecExp.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnSpecExp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnSpecExpStateChanged(evt);
            }
        });

        spnNormalAmount.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnNormalAmount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnNormalAmountStateChanged(evt);
            }
        });

        lblNormalAmount.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblNormalAmount.setText("Factor normal:");
        lblNormalAmount.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lblNormalAmount2.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblNormalAmount2.setText("Factor emisión:");
        lblNormalAmount2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        spnFactorEmision.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnFactorEmision.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnFactorEmisionStateChanged(evt);
            }
        });

        lblNormalAmount4.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblNormalAmount4.setText("Tipo Mapa Entorno:");
        lblNormalAmount4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        buttonGroup1.add(optHDRI);
        optHDRI.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        optHDRI.setSelected(true);
        optHDRI.setText("HDRI");
        optHDRI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optHDRIActionPerformed(evt);
            }
        });

        buttonGroup1.add(optCubo);
        optCubo.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        optCubo.setText("Cúbico");
        optCubo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optCuboActionPerformed(evt);
            }
        });

        lblRugosidad.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblRugosidad.setText("Rugosidad:");

        sldRugosidad.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldRugosidadStateChanged(evt);
            }
        });

        lblMetalico.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblMetalico.setText("Metálico:");

        sldMetalico.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldMetalicoStateChanged(evt);
            }
        });

        lblEspecularidad.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblEspecularidad.setText("Especularidad:");

        sldEspecularidad.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldEspecularidadStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNormalAmount)
                            .addComponent(lblNormalAmount2)
                            .addComponent(lblNormalAmount4)
                            .addComponent(lblSpecExp, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(spnNormalAmount)
                                    .addComponent(spnFactorEmision)
                                    .addComponent(spnSpecExp, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(optHDRI)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(optCubo)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblEspecularidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblMetalico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblRugosidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sldMetalico, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(sldRugosidad, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sldEspecularidad, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRugosidad)
                    .addComponent(sldRugosidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sldMetalico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMetalico))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEspecularidad)
                    .addComponent(sldEspecularidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSpecExp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spnSpecExp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnNormalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNormalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNormalAmount2)
                    .addComponent(spnFactorEmision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNormalAmount4)
                    .addComponent(optHDRI)
                    .addComponent(optCubo))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombre))
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        try {
            renderVistaPrevia.getEscena().eliminarEntidadSindestruir(entidadVistaPrevia);
        } catch (Exception e) {
            e.printStackTrace();
        }
        entidadVistaPrevia = crearTestPlano(activeMaterial);
        renderVistaPrevia.getEscena().agregarEntidad(entidadVistaPrevia);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        try {
            renderVistaPrevia.getEscena().eliminarEntidadSindestruir(entidadVistaPrevia);
        } catch (Exception e) {
            e.printStackTrace();
        }

        entidadVistaPrevia = crearTestCubo(activeMaterial);
        renderVistaPrevia.getEscena().agregarEntidad(entidadVistaPrevia);
        renderVistaPrevia.update();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        try {
            renderVistaPrevia.getEscena().eliminarEntidadSindestruir(entidadVistaPrevia);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        renderVistaPrevia.getUniverso().eliminarEntidades();
        //        renderVistaPrevia.getUniverso().agregarEntidad(crearFondoCuadros());
        entidadVistaPrevia = crearTestEsfera(activeMaterial);
        renderVistaPrevia.getEscena().agregarEntidad(entidadVistaPrevia);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        try {
            renderVistaPrevia.getEscena().eliminarEntidadSindestruir(entidadVistaPrevia);
        } catch (Exception e) {
            e.printStackTrace();
        }
        entidadVistaPrevia = crearTetera(activeMaterial);
        renderVistaPrevia.getEscena().agregarEntidad(entidadVistaPrevia);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void pnlDiffuseColorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlDiffuseColorMousePressed
        Color newColor = JColorChooser.showDialog(this, "Escoja un color", pnlDiffuseColor.getBackground());
        if (newColor != null) {
            pnlDiffuseColor.setBackground(newColor);
            applyMaterialControl();
        }
    }//GEN-LAST:event_pnlDiffuseColorMousePressed

    private void spnSpecExpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnSpecExpStateChanged
        if (getFloatFromSpinner(spnSpecExp) < 1) {
            spnSpecExp.setValue(1.0f);
//            spnSpecExp.setValue(0.0f);
        }
        applyMaterialControl();
    }//GEN-LAST:event_spnSpecExpStateChanged

    private void sldAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldAlphaStateChanged
        applyMaterialControl();
    }//GEN-LAST:event_sldAlphaStateChanged

    private void spnNormalAmountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnNormalAmountStateChanged
        applyMaterialControl();
    }//GEN-LAST:event_spnNormalAmountStateChanged

    private void spnFactorEmisionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnFactorEmisionStateChanged
        applyMaterialControl();
    }//GEN-LAST:event_spnFactorEmisionStateChanged

    private void pnlAlphaColorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlAlphaColorMousePressed
        Color newColor = JColorChooser.showDialog(this, "Escoja un color", pnlAlphaColor.getBackground());
        if (newColor != null) {
            pnlAlphaColor.setBackground(newColor);
            applyMaterialControl();
        }
    }//GEN-LAST:event_pnlAlphaColorMousePressed

    private void chkTransparenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTransparenciaActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_chkTransparenciaActionPerformed

    private void spnFactorRefraccionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnFactorRefraccionStateChanged
        applyMaterialControl();
    }//GEN-LAST:event_spnFactorRefraccionStateChanged

    private void sldMetalicoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldMetalicoStateChanged
        applyMaterialControl();
    }//GEN-LAST:event_sldMetalicoStateChanged

    private void chkRefraccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRefraccionActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_chkRefraccionActionPerformed

    private void optHDRIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optHDRIActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_optHDRIActionPerformed

    private void optCuboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optCuboActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_optCuboActionPerformed

    private void chkReflexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkReflexionActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_chkReflexionActionPerformed

    private void sldRugosidadStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldRugosidadStateChanged
        applyMaterialControl();
    }//GEN-LAST:event_sldRugosidadStateChanged

    private void sldEspecularidadStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldEspecularidadStateChanged
        applyMaterialControl();
    }//GEN-LAST:event_sldEspecularidadStateChanged

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaMetalico()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void btnEliminaMapaMetalicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaMetalicoActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaMetalico(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaMetalicoActionPerformed

    private void btnMapaMetalicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaMetalicoActionPerformed
        if (activeMaterial != null) {
            //            chooser.setCurrentDirectory(new File(QGlobal.RECURSOS));
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    activeMaterial.setMapaMetalico(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnMapaMetalicoActionPerformed

    private void pnlMetalMapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlMetalMapMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_pnlMetalMapMouseClicked

    private void txtMV_MetalicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_MetalicoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMV_MetalicoActionPerformed

    private void txtMU_MetalicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_MetalicoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMU_MetalicoActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaRugosidad()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void btnEliminaMapaRugosidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaRugosidadActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaRugosidad(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaRugosidadActionPerformed

    private void btnMapaRugosidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaRugosidadActionPerformed
        if (activeMaterial != null) {
            //            chooser.setCurrentDirectory(new File(QGlobal.RECURSOS));
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                try {
                    activeMaterial.setMapaRugosidad(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                    //                if (activeMaterial.getMapaDifusa() != null) {
                    //
                    //                } else {
                    //
                    //                }
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnMapaRugosidadActionPerformed

    private void pnlRugMapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRugMapMouseClicked

    }//GEN-LAST:event_pnlRugMapMouseClicked

    private void txtMV_RugosidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_RugosidadActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_RugosidadActionPerformed

    private void txtMU_RugosidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_RugosidadActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_RugosidadActionPerformed

    private void btnAOMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAOMapActionPerformed
        if (activeMaterial != null) {
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    activeMaterial.setMapaSAO(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnAOMapActionPerformed

    private void btnEliminaMapaAOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaAOActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaSAO(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaAOActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        try {
            new FrmVistaPrevia(activeMaterial.getMapaSAO()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void txtMU_AOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_AOActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_AOActionPerformed

    private void txtMV_AOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_AOActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_AOActionPerformed

    private void txtMU_EntornoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_EntornoActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_EntornoActionPerformed

    private void txtMV_EntornoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_EntornoActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_EntornoActionPerformed

    private void txtMU_TranspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_TranspActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_TranspActionPerformed

    private void txtMV_TranspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_TranspActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_TranspActionPerformed

    private void txtMV_DispActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_DispActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_DispActionPerformed

    private void txtMU_DispActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_DispActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_DispActionPerformed

    private void txtMV_NormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_NormalActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_NormalActionPerformed

    private void txtMU_NormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_NormalActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_NormalActionPerformed

    private void txtMV_EmisivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_EmisivoActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_EmisivoActionPerformed

    private void txtMU_EmisivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_EmisivoActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_EmisivoActionPerformed

    private void txtMV_EspecularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_EspecularActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_EspecularActionPerformed

    private void txtMU_EspecularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_EspecularActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_EspecularActionPerformed

    private void txtMV_DifusaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMV_DifusaKeyPressed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_DifusaKeyPressed

    private void txtMV_DifusaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_DifusaActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMV_DifusaActionPerformed

    private void txtMU_DifusaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMU_DifusaKeyPressed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_DifusaKeyPressed

    private void txtMU_DifusaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_DifusaActionPerformed
        applyMaterialControl();
    }//GEN-LAST:event_txtMU_DifusaActionPerformed

    private void btnSetMapaDifusoCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMapaDifusoCrearActionPerformed
        if (activeMaterial != null) {
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));

            JOptionPane.showMessageDialog(this, "Debe ingresar las imágenes en este orde: +X, +Y, +Z, -X,-Y, -Z", "Ingreso de textura", 0);
            //            String[] titulos = {
            //                "Positivo Y", "Positivo X", "Positivo Z",
            //                "Negativo Y", "Negativo X", "Negativo Z"
            //            };
            QTextura[] texturas = new QTextura[6];
            int c = 0;
            for (int i = 0; i < 6; i++) {

                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        c++;
                        texturas[i] = new QTextura(ImageIO.read(chooser.getSelectedFile()));
                    } catch (IOException ex) {
                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (c == 6) {

                QMapaCubo tmp = new QMapaCubo(QMapaCubo.FORMATO_MAPA_HDRI,
                        texturas[0],
                        texturas[1],
                        texturas[2],
                        texturas[3],
                        texturas[4],
                        texturas[5]
                );

                activeMaterial.setMapaColor(new QProcesadorSimple(tmp.getTexturaSalida()));

                populateMaterialControl(activeMaterial);
                tmp.destruir();
                tmp = null;
            }
        }
    }//GEN-LAST:event_btnSetMapaDifusoCrearActionPerformed

    private void btnEntornoMapCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntornoMapCrearActionPerformed
        if (activeMaterial != null) {
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));

            JOptionPane.showMessageDialog(this, "Debe ingresar las imágenes en este orden: +X, +Y, +Z, -X,-Y, -Z", "Ingreso de textura", 0);
            QTextura[] texturas = new QTextura[6];
            int c = 0;
            for (int i = 0; i < 6; i++) {
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        c++;
                        texturas[i] = new QTextura(ImageIO.read(chooser.getSelectedFile()));
                    } catch (Exception ex) {
                    }
                }
            }
            if (c == 6) {

                QMapaCubo tmp = new QMapaCubo(QMapaCubo.FORMATO_MAPA_CUBO,
                        texturas[0],
                        texturas[1],
                        texturas[2],
                        texturas[3],
                        texturas[4],
                        texturas[5]
                );

                activeMaterial.setMapaEntorno(new QProcesadorSimple(tmp.getTexturaSalida()));
                activeMaterial.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);//mapa cubico
                populateMaterialControl(activeMaterial);
                tmp.destruir();
                tmp = null;
            }
        }
    }//GEN-LAST:event_btnEntornoMapCrearActionPerformed

    private void btnDispMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDispMapActionPerformed
        if (activeMaterial != null) {
            //            chooser.setCurrentDirectory(new File(QGlobal.RECURSOS));
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                try {
                    activeMaterial.setMapaDesplazamiento(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);

                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnDispMapActionPerformed

    private void btnEliminaMapaDispActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaDispActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaDesplazamiento(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaDispActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaDesplazamiento()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btnAlphaMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlphaMapActionPerformed
        if (activeMaterial != null) {
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    activeMaterial.setMapaTransparencia(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnAlphaMapActionPerformed

    private void btnEliminaMapaAlphaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaAlphaActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaTransparencia(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaAlphaActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaTransparencia()).setVisible(true);
        } catch (Exception e) {
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaEntorno()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaNormal()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaEmisivo()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaEspecular()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaColor()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnEntornoMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntornoMapActionPerformed
        if (activeMaterial != null) {
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    activeMaterial.setMapaEntorno(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    activeMaterial.setTipoMapaEntorno(2);//por default HDRI
                    populateMaterialControl(activeMaterial);
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnEntornoMapActionPerformed

    private void btnEliminaMapaEntornoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaEntornoActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaEntorno(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaEntornoActionPerformed

    private void btnEliminaMapaNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaNormalActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaNormal(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaNormalActionPerformed

    private void btnEliminaMapaEmisivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaEmisivoActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaEmisivo(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaEmisivoActionPerformed

    private void btnEliminaMapaEspecularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaEspecularActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaEspecular(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaEspecularActionPerformed

    private void btnEliminaMapaDifusoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaDifusoActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaColor(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaDifusoActionPerformed

    private void btnMapaEmisivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaEmisivoActionPerformed
        if (activeMaterial != null) {
            //            chooser.setCurrentDirectory(new File(QGlobal.RECURSOS));
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                try {
                    activeMaterial.setMapaEmisivo(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                    //                if (activeMaterial.getMapaDifusa() != null) {
                    //
                    //                } else {
                    //
                    //                }
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnMapaEmisivoActionPerformed

    private void btnMapaEspecularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaEspecularActionPerformed
        if (activeMaterial != null) {
            //            chooser.setCurrentDirectory(new File(QGlobal.RECURSOS));
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                try {
                    activeMaterial.setMapaEspecular(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                    //                if (activeMaterial.getMapaDifusa() != null) {
                    //
                    //                } else {
                    //
                    //                }
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnMapaEspecularActionPerformed

    private void pnlEmisivoMapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlEmisivoMapMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_pnlEmisivoMapMouseClicked

    private void pnlEspecularMapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlEspecularMapMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_pnlEspecularMapMouseClicked

    private void btnNormalMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNormalMapActionPerformed
        if (activeMaterial != null) {
            //            chooser.setCurrentDirectory(new File(QGlobal.RECURSOS));
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                try {
                    activeMaterial.setMapaNormal(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                    //                if (activeMaterial.getMapaDifusa() != null) {
                    //
                    //                } else {
                    //
                    //                }
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnNormalMapActionPerformed

    private void btnSetMapaDifusoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetMapaDifusoActionPerformed

        if (activeMaterial != null) {

            //            chooser.setCurrentDirectory(new File(QGlobal.RECURSOS));
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                try {
                    activeMaterial.setMapaColor(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                    //                if (activeMaterial.getMapaDifusa() != null) {
                    //
                    //                } else {
                    //
                    //                }
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnSetMapaDifusoActionPerformed

    private void pnlDifusoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlDifusoMouseClicked
        pnlDifuso.getGraphics().drawImage(mapaDifuso, 0, 0, this);
    }//GEN-LAST:event_pnlDifusoMouseClicked

    private void txtMV_IrradiacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMV_IrradiacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMV_IrradiacionActionPerformed

    private void txtMU_IrradiacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMU_IrradiacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMU_IrradiacionActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        try {
            new FrmVistaPrevia(activeMaterial.getMapaIrradiacion()).setVisible(true);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void btnEliminaMapaIrradiacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminaMapaIrradiacionActionPerformed
        if (activeMaterial != null) {
            activeMaterial.setMapaIrradiacion(null);
            populateMaterialControl(activeMaterial);
        }
    }//GEN-LAST:event_btnEliminaMapaIrradiacionActionPerformed

    private void btnIrrMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIrrMapActionPerformed
        if (activeMaterial != null) {
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    activeMaterial.setMapaIrradiacion(new QProcesadorSimple(new QTextura(ImageIO.read(chooser.getSelectedFile()))));
                    populateMaterialControl(activeMaterial);
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnIrrMapActionPerformed

    private void btnIrradiacionMapCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIrradiacionMapCrearActionPerformed
         if (activeMaterial != null) {
            chooser.setFileFilter(new FileNameExtensionFilter("Archivos soportados", "png", "jpg", "jpeg", "bmp", "gif"));

            JOptionPane.showMessageDialog(this, "Debe ingresar las imágenes en este orden: +X, +Y, +Z, -X,-Y, -Z", "Ingreso de textura", 0);
            QTextura[] texturas = new QTextura[6];
            int c = 0;
            for (int i = 0; i < 6; i++) {
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        c++;
                        texturas[i] = new QTextura(ImageIO.read(chooser.getSelectedFile()));
                    } catch (Exception ex) {
                    }
                }
            }
            if (c == 6) {

                QMapaCubo tmp = new QMapaCubo(QMapaCubo.FORMATO_MAPA_CUBO,
                        texturas[0],
                        texturas[1],
                        texturas[2],
                        texturas[3],
                        texturas[4],
                        texturas[5]
                );

                activeMaterial.setMapaIrradiacion(new QProcesadorSimple(tmp.getTexturaSalida()));
                activeMaterial.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);//mapa cubico
                populateMaterialControl(activeMaterial);
                tmp.destruir();
                tmp = null;
            }
        }
    }//GEN-LAST:event_btnIrradiacionMapCrearActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAOMap;
    private javax.swing.JButton btnAlphaMap;
    private javax.swing.JButton btnDispMap;
    private javax.swing.JButton btnEliminaMapaAO;
    private javax.swing.JButton btnEliminaMapaAlpha;
    private javax.swing.JButton btnEliminaMapaDifuso;
    private javax.swing.JButton btnEliminaMapaDisp;
    private javax.swing.JButton btnEliminaMapaEmisivo;
    private javax.swing.JButton btnEliminaMapaEntorno;
    private javax.swing.JButton btnEliminaMapaEspecular;
    private javax.swing.JButton btnEliminaMapaIrradiacion;
    private javax.swing.JButton btnEliminaMapaMetalico;
    private javax.swing.JButton btnEliminaMapaNormal;
    private javax.swing.JButton btnEliminaMapaRugosidad;
    private javax.swing.JButton btnEntornoMap;
    private javax.swing.JButton btnEntornoMapCrear;
    private javax.swing.JButton btnIrrMap;
    private javax.swing.JButton btnIrradiacionMapCrear;
    private javax.swing.JButton btnMapaEmisivo;
    private javax.swing.JButton btnMapaEspecular;
    private javax.swing.JButton btnMapaMetalico;
    private javax.swing.JButton btnMapaRugosidad;
    private javax.swing.JButton btnNormalMap;
    private javax.swing.JButton btnSetMapaDifuso;
    private javax.swing.JButton btnSetMapaDifusoCrear;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkReflexion;
    private javax.swing.JCheckBox chkRefraccion;
    private javax.swing.JCheckBox chkTransparencia;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblEspecularidad;
    private javax.swing.JLabel lblMetalico;
    private javax.swing.JLabel lblNormalAmount;
    private javax.swing.JLabel lblNormalAmount2;
    private javax.swing.JLabel lblNormalAmount3;
    private javax.swing.JLabel lblNormalAmount4;
    private javax.swing.JLabel lblRugosidad;
    private javax.swing.JLabel lblSpecExp;
    private javax.swing.JLabel lblSpecExp1;
    private javax.swing.JRadioButton optCubo;
    private javax.swing.JRadioButton optHDRI;
    private javax.swing.JPanel pnlAOMap;
    private javax.swing.JPanel pnlAlphaColor;
    private javax.swing.JPanel pnlAlphaMap;
    private javax.swing.JPanel pnlDiffuseColor;
    private javax.swing.JPanel pnlDifuso;
    private javax.swing.JPanel pnlDispMap;
    private javax.swing.JPanel pnlEmisivoMap;
    private javax.swing.JPanel pnlEntornoMap;
    private javax.swing.JPanel pnlEspecularMap;
    private javax.swing.JPanel pnlIrrMap;
    private javax.swing.JPanel pnlMapas;
    private javax.swing.JPanel pnlMetalMap;
    private javax.swing.JPanel pnlNormalMap;
    private javax.swing.JPanel pnlRugMap;
    private javax.swing.JPanel pnlVistaPrevia;
    private javax.swing.JSlider sldAlpha;
    private javax.swing.JSlider sldEspecularidad;
    private javax.swing.JSlider sldMetalico;
    private javax.swing.JSlider sldRugosidad;
    private javax.swing.JSpinner spnFactorEmision;
    private javax.swing.JSpinner spnFactorRefraccion;
    private javax.swing.JSpinner spnNormalAmount;
    private javax.swing.JSpinner spnSpecExp;
    private javax.swing.JTextField txtMU_AO;
    private javax.swing.JTextField txtMU_Difusa;
    private javax.swing.JTextField txtMU_Disp;
    private javax.swing.JTextField txtMU_Emisivo;
    private javax.swing.JTextField txtMU_Entorno;
    private javax.swing.JTextField txtMU_Especular;
    private javax.swing.JTextField txtMU_Irradiacion;
    private javax.swing.JTextField txtMU_Metalico;
    private javax.swing.JTextField txtMU_Normal;
    private javax.swing.JTextField txtMU_Rugosidad;
    private javax.swing.JTextField txtMU_Transp;
    private javax.swing.JTextField txtMV_AO;
    private javax.swing.JTextField txtMV_Difusa;
    private javax.swing.JTextField txtMV_Disp;
    private javax.swing.JTextField txtMV_Emisivo;
    private javax.swing.JTextField txtMV_Entorno;
    private javax.swing.JTextField txtMV_Especular;
    private javax.swing.JTextField txtMV_Irradiacion;
    private javax.swing.JTextField txtMV_Metalico;
    private javax.swing.JTextField txtMV_Normal;
    private javax.swing.JTextField txtMV_Rugosidad;
    private javax.swing.JTextField txtMV_Transp;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
