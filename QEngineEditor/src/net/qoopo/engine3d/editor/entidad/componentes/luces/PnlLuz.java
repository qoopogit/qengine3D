/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.editor.entidad.componentes.luces;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public class PnlLuz extends javax.swing.JPanel {

    private QLuz luz;
    private int Xtemp = -1;
    private boolean lock = false;

    /**
     * Creates new form PnlLuz
     *
     * @param luz
     */
    public PnlLuz(QLuz luz) {
        initComponents();
        try {
            this.luz = luz;

            MouseMotionListener spinnerMouseMotion = new MouseMotionAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {
//                int deltaX = 0;
                    if (Xtemp == -1) {
                        Xtemp = e.getXOnScreen();
                    }

                    int deltaX = e.getXOnScreen() - Xtemp;

                    JLabel sourceLabel = (JLabel) e.getSource();
                    JSpinner source = null;
                    if (sourceLabel == lblEnergia) {
                        source = spnEnergia;
                    } else if (sourceLabel == lblAngCono) {
                        source = spnAngCono;
                    } else if (sourceLabel == lblAngCono2) {
                        source = spnAngCono2;

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
            spnEnergia.setModel(new SpinnerNumberModel(0, 0, Double.POSITIVE_INFINITY, .01));
            spnAngCono.setModel(new SpinnerNumberModel(45, 0, 180, .01));
            spnAngCono2.setModel(new SpinnerNumberModel(45, 0, 180, .01));
            ChangeListener lightChangeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    applyLightControl();
                }
            };
            spnEnergia.addChangeListener(lightChangeListener);
            spnAngCono.addChangeListener(lightChangeListener);
            spnAngCono2.addChangeListener(lightChangeListener);
            lblEnergia.addMouseMotionListener(spinnerMouseMotion);
            lblEnergia.addMouseListener(spinnerMouseInput);
            lblAngCono.addMouseMotionListener(spinnerMouseMotion);
            lblAngCono.addMouseListener(spinnerMouseInput);
            lblAngCono2.addMouseMotionListener(spinnerMouseMotion);
            lblAngCono2.addMouseListener(spinnerMouseInput);

            populateLightControl(this.luz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateLightControl(QLuz luz) {
        try {
            lock = true;
            bolEstadoLuz.setSelected(luz.isEnable());
            if (luz instanceof QLuzDireccional) {
                cboLightType.setSelectedIndex(1);
            } else if (luz instanceof QLuzPuntual) {
                cboLightType.setSelectedIndex(0);
            } else if (luz instanceof QLuzSpot) {
                cboLightType.setSelectedIndex(2);
            }
            pnlColorLuz.setBackground(luz.color.getColor());
            chkProyectarSombra.setSelected(luz.isProyectarSombras());
            txtMapaSombraResolucion.setText(String.valueOf(luz.getResolucionMapaSombra()));
            chkSombraDinamica.setSelected(luz.isSombraDinamica());
            txtLuzRadio.setText(String.valueOf(luz.radio));
            spnEnergia.setValue(luz.energia);
            if (luz instanceof QLuzSpot) {
                spnAngCono.setValue(Math.toDegrees(((QLuzSpot) luz).getAnguloExterno()));
                spnAngCono2.setValue(Math.toDegrees(((QLuzSpot) luz).getAnguloInterno()));
                spnAngCono.setVisible(true);
                lblAngCono.setVisible(true);
                spnAngCono2.setVisible(true);
                lblAngCono2.setVisible(true);
            } else {
                spnAngCono.setVisible(false);
                lblAngCono.setVisible(false);
                spnAngCono2.setVisible(false);
                lblAngCono2.setVisible(false);
            }
//            txtMapaSombraRadio.setText(String.valueOf(luz.getRadioSombra()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        lock = false;
    }

    private void applyLightControl() {
        try {
            if (!lock && luz != null) {
                luz.energia = getFloatFromSpinner(spnEnergia);
                luz.color = new QColor(pnlColorLuz.getBackground());
                luz.setEnable(bolEstadoLuz.isSelected());
                luz.setProyectarSombras(chkProyectarSombra.isSelected());
                luz.setResolucionMapaSombra(Integer.parseInt(txtMapaSombraResolucion.getText()));
                luz.setSombraDinamica(chkSombraDinamica.isSelected());
                luz.radio = Float.parseFloat(txtLuzRadio.getText());
//                luz.setRadioSombra(Float.parseFloat(txtMapaSombraRadio.getText()));
                if (luz instanceof QLuzSpot) {
                    ((QLuzSpot) luz).setAnguloExterno((float) Math.toRadians(getFloatFromSpinner(spnAngCono)));
                    ((QLuzSpot) luz).setAnguloInterno((float) Math.toRadians(getFloatFromSpinner(spnAngCono2)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        pnlSombras = new javax.swing.JPanel();
        chkProyectarSombra = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        txtMapaSombraResolucion = new javax.swing.JTextField();
        chkSombraDinamica = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        txtMapaSombraRadio = new javax.swing.JTextField();
        lblLightType = new javax.swing.JLabel();
        pnlColorLuz = new javax.swing.JPanel();
        cboLightType = new javax.swing.JComboBox<>();
        lblEnergia = new javax.swing.JLabel();
        spnEnergia = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        bolEstadoLuz = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        txtLuzRadio = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lblAngCono = new javax.swing.JLabel();
        spnAngCono = new javax.swing.JSpinner();
        lblAngCono2 = new javax.swing.JLabel();
        spnAngCono2 = new javax.swing.JSpinner();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Luz", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        pnlSombras.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sombras", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        chkProyectarSombra.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        chkProyectarSombra.setText("Proyectar Sombra");
        chkProyectarSombra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkProyectarSombraActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel11.setText("Mapa de sombra Tam:");

        txtMapaSombraResolucion.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMapaSombraResolucion.setText("512");
        txtMapaSombraResolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMapaSombraResolucionActionPerformed(evt);
            }
        });

        chkSombraDinamica.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        chkSombraDinamica.setText("Dinámica");
        chkSombraDinamica.setToolTipText("Actualiza el mapa de sombras en cada pasada del render.");
        chkSombraDinamica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSombraDinamicaActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel12.setText("Radio Sombra:");

        txtMapaSombraRadio.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMapaSombraRadio.setText("0");
        txtMapaSombraRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMapaSombraRadioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSombrasLayout = new javax.swing.GroupLayout(pnlSombras);
        pnlSombras.setLayout(pnlSombrasLayout);
        pnlSombrasLayout.setHorizontalGroup(
            pnlSombrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkProyectarSombra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlSombrasLayout.createSequentialGroup()
                .addGroup(pnlSombrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSombrasLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMapaSombraResolucion))
                    .addGroup(pnlSombrasLayout.createSequentialGroup()
                        .addComponent(chkSombraDinamica)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlSombrasLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(47, 47, 47)
                        .addComponent(txtMapaSombraRadio)))
                .addContainerGap())
        );
        pnlSombrasLayout.setVerticalGroup(
            pnlSombrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSombrasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkProyectarSombra, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkSombraDinamica)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSombrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtMapaSombraResolucion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSombrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtMapaSombraRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        lblLightType.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblLightType.setText("Tipo:");
        lblLightType.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        pnlColorLuz.setBackground(new java.awt.Color(0, 0, 0));
        pnlColorLuz.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlColorLuzMousePressed(evt);
            }
        });

        javax.swing.GroupLayout pnlColorLuzLayout = new javax.swing.GroupLayout(pnlColorLuz);
        pnlColorLuz.setLayout(pnlColorLuzLayout);
        pnlColorLuzLayout.setHorizontalGroup(
            pnlColorLuzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlColorLuzLayout.setVerticalGroup(
            pnlColorLuzLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        cboLightType.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        cboLightType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Punto de Luz", "Luz Direccional", "Luz Spot" }));
        cboLightType.setEnabled(false);
        cboLightType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLightTypeActionPerformed(evt);
            }
        });

        lblEnergia.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblEnergia.setText("Energía:");
        lblEnergia.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        spnEnergia.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel17.setText("Estado:");

        bolEstadoLuz.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        bolEstadoLuz.setText("Encendida");
        bolEstadoLuz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bolEstadoLuzActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel3.setText("Radio:");

        txtLuzRadio.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtLuzRadio.setText("0");
        txtLuzRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLuzRadioActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel1.setText("Color:");

        lblAngCono.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblAngCono.setText("Áng. Cono:");
        lblAngCono.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        spnAngCono.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        lblAngCono2.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        lblAngCono2.setText("Fundido:");
        lblAngCono2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        spnAngCono2.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlSombras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLightType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEnergia, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAngCono, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblAngCono2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnAngCono)
                            .addComponent(spnEnergia)
                            .addComponent(bolEstadoLuz, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtLuzRadio)
                            .addComponent(spnAngCono2)))
                    .addComponent(cboLightType, 0, 1, Short.MAX_VALUE)
                    .addComponent(pnlColorLuz, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlColorLuz, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboLightType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLightType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEnergia)
                    .addComponent(spnEnergia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(bolEstadoLuz, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtLuzRadio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAngCono)
                    .addComponent(spnAngCono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAngCono2)
                    .addComponent(spnAngCono2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlSombras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pnlColorLuzMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlColorLuzMousePressed
        JColorChooser colorChooser = new JColorChooser();
        Color newColor = colorChooser.showDialog(this, "Seleccionar color", pnlColorLuz.getBackground());
        if (newColor != null) {
            pnlColorLuz.setBackground(newColor);
            applyLightControl();
        }
    }//GEN-LAST:event_pnlColorLuzMousePressed

    private void cboLightTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLightTypeActionPerformed
        applyLightControl();
    }//GEN-LAST:event_cboLightTypeActionPerformed

    private void bolEstadoLuzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bolEstadoLuzActionPerformed
        applyLightControl();
    }//GEN-LAST:event_bolEstadoLuzActionPerformed

    private void txtLuzRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLuzRadioActionPerformed
        applyLightControl();
    }//GEN-LAST:event_txtLuzRadioActionPerformed

    private void chkProyectarSombraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkProyectarSombraActionPerformed
        applyLightControl();
    }//GEN-LAST:event_chkProyectarSombraActionPerformed

    private void txtMapaSombraResolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMapaSombraResolucionActionPerformed
        applyLightControl();
    }//GEN-LAST:event_txtMapaSombraResolucionActionPerformed

    private void chkSombraDinamicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSombraDinamicaActionPerformed
        applyLightControl();
    }//GEN-LAST:event_chkSombraDinamicaActionPerformed

    private void txtMapaSombraRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMapaSombraRadioActionPerformed
        applyLightControl();
    }//GEN-LAST:event_txtMapaSombraRadioActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton bolEstadoLuz;
    private javax.swing.JComboBox<String> cboLightType;
    private javax.swing.JCheckBox chkProyectarSombra;
    private javax.swing.JCheckBox chkSombraDinamica;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblAngCono;
    private javax.swing.JLabel lblAngCono2;
    private javax.swing.JLabel lblEnergia;
    private javax.swing.JLabel lblLightType;
    private javax.swing.JPanel pnlColorLuz;
    private javax.swing.JPanel pnlSombras;
    private javax.swing.JSpinner spnAngCono;
    private javax.swing.JSpinner spnAngCono2;
    private javax.swing.JSpinner spnEnergia;
    private javax.swing.JTextField txtLuzRadio;
    private javax.swing.JTextField txtMapaSombraRadio;
    private javax.swing.JTextField txtMapaSombraResolucion;
    // End of variables declaration//GEN-END:variables
}
