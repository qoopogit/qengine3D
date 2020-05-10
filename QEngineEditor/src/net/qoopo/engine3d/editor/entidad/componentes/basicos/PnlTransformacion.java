/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.editor.entidad.componentes.basicos;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;

/**
 *
 * @author alberto
 */
public class PnlTransformacion extends javax.swing.JPanel {

    private QTransformacion transformacion;

    private int Xtemp = -1;
    private boolean lock = false;

    /**
     * Creates new form pnlTransformacion
     * @param transformacion
     */
    public PnlTransformacion(QTransformacion transformacion) {
        initComponents();

        MouseMotionListener spinnerMouseMotion = new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
//                int deltaX = 0;
                if (Xtemp == -1) {
                    Xtemp = e.getXOnScreen();
                }

                int deltaX = e.getXOnScreen() - Xtemp;

                JLabel sourceLabel = (JLabel) e.getSource();
                JSpinner source = spnLocX;
                if (sourceLabel == lblLocX) {
                    source = spnLocX;
                } else if (sourceLabel == lblLocY) {
                    source = spnLocY;
                } else if (sourceLabel == lblLocZ) {
                    source = spnLocZ;
                } else if (sourceLabel == lblRotX) {
                    source = spnRotX;
                    deltaX *= 40;
                } else if (sourceLabel == lblRotY) {
                    source = spnRotY;
                    deltaX *= 40;
                } else if (sourceLabel == lblRotZ) {
                    source = spnRotZ;
                    deltaX *= 40;
                } else if (sourceLabel == lblScaleX) {
                    source = spnScaleX;
                } else if (sourceLabel == lblScaleY) {
                    source = spnScaleY;
                } else if (sourceLabel == lblScaleZ) {
                    source = spnScaleZ;

//                } else if (sourceLabel == lblSpecExp) {
//                    source = spnSpecExp;
//                    deltaX *= 100;
//                } else if (sourceLabel == lblNormalAmount) {
//                    source = spnNormalAmount;
                }
                source.setValue(getFloatFromSpinner(source) + (float) deltaX / 100);

            }
        };

        MouseInputListener spinnerMouseInput = new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Xtemp = -1;
            }
        };

        spnLocX.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));
        spnLocY.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));
        spnLocZ.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));
        spnRotX.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));
        spnRotY.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));
        spnRotZ.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));
        spnScaleX.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));
        spnScaleY.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));
        spnScaleZ.setModel(new SpinnerNumberModel(0, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, .01));

        ChangeListener objectSpinnerListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                applyObjectControl();
            }
        };

        spnLocX.addChangeListener(objectSpinnerListener);
        spnLocY.addChangeListener(objectSpinnerListener);
        spnLocZ.addChangeListener(objectSpinnerListener);
        spnRotX.addChangeListener(objectSpinnerListener);
        spnRotY.addChangeListener(objectSpinnerListener);
        spnRotZ.addChangeListener(objectSpinnerListener);
        spnScaleX.addChangeListener(objectSpinnerListener);
        spnScaleY.addChangeListener(objectSpinnerListener);
        spnScaleZ.addChangeListener(objectSpinnerListener);
        lblLocX.addMouseMotionListener(spinnerMouseMotion);
        lblLocY.addMouseMotionListener(spinnerMouseMotion);
        lblLocZ.addMouseMotionListener(spinnerMouseMotion);
        lblRotX.addMouseMotionListener(spinnerMouseMotion);
        lblRotY.addMouseMotionListener(spinnerMouseMotion);
        lblRotZ.addMouseMotionListener(spinnerMouseMotion);
        lblScaleX.addMouseMotionListener(spinnerMouseMotion);
        lblScaleY.addMouseMotionListener(spinnerMouseMotion);
        lblScaleZ.addMouseMotionListener(spinnerMouseMotion);

        lblLocX.addMouseListener(spinnerMouseInput);
        lblLocY.addMouseListener(spinnerMouseInput);
        lblLocZ.addMouseListener(spinnerMouseInput);
        lblRotX.addMouseListener(spinnerMouseInput);
        lblRotY.addMouseListener(spinnerMouseInput);
        lblRotZ.addMouseListener(spinnerMouseInput);
        lblScaleX.addMouseListener(spinnerMouseInput);
        lblScaleY.addMouseListener(spinnerMouseInput);
        lblScaleZ.addMouseListener(spinnerMouseInput);

        this.transformacion = transformacion;
        leerValores();
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

    private void leerValores() {
        lock = true;
        spnLocX.setValue((float) transformacion.getTraslacion().x);
        spnLocY.setValue(transformacion.getTraslacion().y);
        spnLocZ.setValue(transformacion.getTraslacion().z);
        spnRotX.setValue(transformacion.getRotacion().getAngulos().getAnguloX() * 180 / Math.PI);
        spnRotY.setValue(transformacion.getRotacion().getAngulos().getAnguloY() * 180 / Math.PI);
        spnRotZ.setValue(transformacion.getRotacion().getAngulos().getAnguloZ() * 180 / Math.PI);
        spnScaleX.setValue(transformacion.getEscala().x);
        spnScaleY.setValue(transformacion.getEscala().y);
        spnScaleZ.setValue(transformacion.getEscala().z);
        lock = false;
    }

    private void applyObjectControl() {
        if (!lock) {
            transformacion.getTraslacion().setXYZ(getFloatFromSpinner(spnLocX), getFloatFromSpinner(spnLocY), getFloatFromSpinner(spnLocZ));
            transformacion.getRotacion().rotarX((float) (getFloatFromSpinner(spnRotX) * Math.PI / 180));
            transformacion.getRotacion().rotarY((float) (getFloatFromSpinner(spnRotY) * Math.PI / 180));
            transformacion.getRotacion().rotarZ((float) (getFloatFromSpinner(spnRotZ) * Math.PI / 180));

            transformacion.getEscala().setXYZ(getFloatFromSpinner(spnScaleX), getFloatFromSpinner(spnScaleY), getFloatFromSpinner(spnScaleZ));

//            if (entidad instanceof QGeometria) {
//                for (QPoligono face : ((QGeometria) entidad).listaPoligonos) {
//                    face.smooth = btnObjectoSuavizado.isSelected();
//                }
//            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        spnScaleX = new javax.swing.JSpinner();
        spnScaleY = new javax.swing.JSpinner();
        spnScaleZ = new javax.swing.JSpinner();
        lblScaleX = new javax.swing.JLabel();
        lblScaleY = new javax.swing.JLabel();
        lblScaleZ = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        spnRotX = new javax.swing.JSpinner();
        spnRotY = new javax.swing.JSpinner();
        spnRotZ = new javax.swing.JSpinner();
        lblRotX = new javax.swing.JLabel();
        lblRotY = new javax.swing.JLabel();
        lblRotZ = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        spnLocX = new javax.swing.JSpinner();
        spnLocY = new javax.swing.JSpinner();
        spnLocZ = new javax.swing.JSpinner();
        lblLocY = new javax.swing.JLabel();
        lblLocX = new javax.swing.JLabel();
        lblLocZ = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transformación", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Escala");
        jLabel14.setToolTipText("");

        spnScaleX.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnScaleX.setMinimumSize(new java.awt.Dimension(46, 20));

        spnScaleY.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnScaleY.setMinimumSize(new java.awt.Dimension(46, 20));

        spnScaleZ.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnScaleZ.setMinimumSize(new java.awt.Dimension(46, 20));

        lblScaleX.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblScaleX.setText("X:");

        lblScaleY.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblScaleY.setText("Y:");

        lblScaleZ.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblScaleZ.setText("Z:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblScaleY)
                            .addComponent(lblScaleZ))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addContainerGap(9, Short.MAX_VALUE)
                        .addComponent(lblScaleX)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(spnScaleY, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                    .addComponent(spnScaleX, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spnScaleZ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(13, 13, 13))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spnScaleX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblScaleX))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnScaleY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblScaleY)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblScaleZ)
                            .addComponent(spnScaleZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(2, 2, 2))
        );

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Rotación");

        spnRotX.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnRotX.setMinimumSize(new java.awt.Dimension(46, 20));

        spnRotY.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnRotY.setMinimumSize(new java.awt.Dimension(46, 20));

        spnRotZ.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnRotZ.setMinimumSize(new java.awt.Dimension(46, 20));

        lblRotX.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblRotX.setText("X:");

        lblRotY.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblRotY.setText("Y:");

        lblRotZ.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblRotZ.setText("Z:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRotX)
                            .addComponent(lblRotY)
                            .addComponent(lblRotZ))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(spnRotY, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(spnRotX, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(spnRotZ, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spnRotX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRotX))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnRotY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRotY)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblRotZ)
                            .addComponent(spnRotZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(2, 2, 2))
        );

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Traslación");

        spnLocX.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnLocX.setMinimumSize(new java.awt.Dimension(46, 20));

        spnLocY.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnLocY.setMinimumSize(new java.awt.Dimension(46, 20));

        spnLocZ.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        spnLocZ.setMinimumSize(new java.awt.Dimension(46, 20));

        lblLocY.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblLocY.setText("Y:");

        lblLocX.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblLocX.setText("X:");

        lblLocZ.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblLocZ.setText("Z:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLocX)
                    .addComponent(lblLocY)
                    .addComponent(lblLocZ))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spnLocX, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spnLocY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spnLocZ, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spnLocX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLocX))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnLocY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLocY)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLocZ)
                            .addComponent(spnLocZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(2, 2, 2))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel lblLocX;
    private javax.swing.JLabel lblLocY;
    private javax.swing.JLabel lblLocZ;
    private javax.swing.JLabel lblRotX;
    private javax.swing.JLabel lblRotY;
    private javax.swing.JLabel lblRotZ;
    private javax.swing.JLabel lblScaleX;
    private javax.swing.JLabel lblScaleY;
    private javax.swing.JLabel lblScaleZ;
    private javax.swing.JSpinner spnLocX;
    private javax.swing.JSpinner spnLocY;
    private javax.swing.JSpinner spnLocZ;
    private javax.swing.JSpinner spnRotX;
    private javax.swing.JSpinner spnRotY;
    private javax.swing.JSpinner spnRotZ;
    private javax.swing.JSpinner spnScaleX;
    private javax.swing.JSpinner spnScaleY;
    private javax.swing.JSpinner spnScaleZ;
    // End of variables declaration//GEN-END:variables
}
