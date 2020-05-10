/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.editor.entidad.componentes.fisica.dinamico;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.compuesta.QColisionCompuesta;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaConvexa;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaIndexada;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionTerreno;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCaja;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCapsula;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCilindro;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCilindroX;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCono;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionEsfera;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class PnlFisicoRigido extends javax.swing.JPanel {

    private QObjetoRigido rigido;
    private List<QFormaColision> listaColision = new ArrayList<>();

    /**
     * Creates new form PnlAudioListener
     *
     * @param rigido
     */
    public PnlFisicoRigido(QObjetoRigido rigido) {
        initComponents();
        this.rigido = rigido;
        this.txtMasa.setText(String.valueOf(rigido.getMasa()));
        optEstatico.setSelected(rigido.tipoDinamico == 1);
        optDinamico.setSelected(rigido.tipoDinamico == 2);
        optCinematico.setSelected(rigido.tipoDinamico == 3);
        this.txtFriccion.setText(String.valueOf(rigido.getFriccion()));
        this.txtRestitucion.setText(String.valueOf(rigido.getRestitucion()));
        this.txtDampLineal.setText(String.valueOf(rigido.getAmortiguacion_rotacion()));
        this.txtDampAng.setText(String.valueOf(rigido.getAmortiguacion_rotacion()));

        DefaultComboBoxModel modelo = new DefaultComboBoxModel();
        for (QComponente comp : rigido.entidad.getComponentes()) {
            if (comp instanceof QFormaColision) {
                listaColision.add((QFormaColision) comp);
                if (comp instanceof QColisionCaja) {
                    modelo.addElement("Caja");
                } else if (comp instanceof QColisionCapsula) {
                    modelo.addElement("Cápsula");
                } else if (comp instanceof QColisionCilindro) {
                    modelo.addElement("Cilindro");
                } else if (comp instanceof QColisionCilindroX) {
                    modelo.addElement("Cilindro X");
                } else if (comp instanceof QColisionCono) {
                    modelo.addElement("Cono");
                } else if (comp instanceof QColisionEsfera) {
                    modelo.addElement("Esfera");
                } else if (comp instanceof QColisionTerreno) {
                    modelo.addElement("Terreno");
                } else if (comp instanceof QColisionMallaConvexa) {
                    modelo.addElement("Convexa " + ((QColisionMallaConvexa) comp).getMalla().nombre);
                } else if (comp instanceof QColisionMallaIndexada) {
                    modelo.addElement("Indexada " + ((QColisionMallaIndexada) comp).getMalla().nombre);
                } else if (comp instanceof QColisionCompuesta) {
                    modelo.addElement("Compuesta ");
                } else if (comp instanceof AABB) {
                    modelo.addElement("AABB");
                } else {
                    modelo.addElement("Foma colisión");
                }
            }
        }
        cboColision.setModel(modelo);
        aplicarCambios();
    }

    private void aplicarCambios() {
        rigido.setMasa(Float.parseFloat(txtMasa.getText()), QVector3.zero);
        rigido.setFriccion(Float.parseFloat(txtFriccion.getText()));
        rigido.setRestitucion(Float.parseFloat(txtRestitucion.getText()));
        rigido.setAmortiguacion_traslacion(Float.parseFloat(txtDampLineal.getText()));
        rigido.setAmortiguacion_rotacion(Float.parseFloat(txtDampAng.getText()));
        if (optEstatico.isSelected()) {
            rigido.tipoDinamico = 1;
        }
        if (optDinamico.isSelected()) {
            rigido.tipoDinamico = 2;
        }
        if (optCinematico.isSelected()) {
            rigido.tipoDinamico = 3;
        }
        //busca el componente de forma de colision para ponerla en el contenedor
//        for (QComponente comp : rigido.entidad.componentes) {
//            if (comp instanceof QFormaColision) {
//                rigido.setFormaColision((QFormaColision) comp);
//            }
//        }
        rigido.setFormaColision(listaColision.get(cboColision.getSelectedIndex()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupoTipo = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        optDinamico = new javax.swing.JRadioButton();
        optEstatico = new javax.swing.JRadioButton();
        optCinematico = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txtMasa = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtFriccion = new javax.swing.JTextField();
        txtRestitucion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDampLineal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDampAng = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cboColision = new javax.swing.JComboBox<>();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Físico Rígido", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 9))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel1.setText("Tipo:");

        grupoTipo.add(optDinamico);
        optDinamico.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        optDinamico.setText("Dinámico");
        optDinamico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optDinamicoActionPerformed(evt);
            }
        });

        grupoTipo.add(optEstatico);
        optEstatico.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        optEstatico.setText("Estático");
        optEstatico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optEstaticoActionPerformed(evt);
            }
        });

        grupoTipo.add(optCinematico);
        optCinematico.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        optCinematico.setText("Cinemático");
        optCinematico.setEnabled(false);
        optCinematico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optCinematicoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel2.setText("Masa:");

        txtMasa.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtMasa.setText("1");
        txtMasa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMasaActionPerformed(evt);
            }
        });
        txtMasa.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtMasaPropertyChange(evt);
            }
        });
        txtMasa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMasaKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel3.setText("Fricción:");

        txtFriccion.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtFriccion.setText("1");
        txtFriccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFriccionActionPerformed(evt);
            }
        });
        txtFriccion.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtFriccionPropertyChange(evt);
            }
        });
        txtFriccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFriccionKeyReleased(evt);
            }
        });

        txtRestitucion.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtRestitucion.setText("1");
        txtRestitucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRestitucionActionPerformed(evt);
            }
        });
        txtRestitucion.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtRestitucionPropertyChange(evt);
            }
        });
        txtRestitucion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRestitucionKeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel4.setText("Restitución:");

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel5.setText("Damp. Lineal:");

        txtDampLineal.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtDampLineal.setText("1");
        txtDampLineal.setToolTipText("Metal pesado=0.001;  Pluma=10");
        txtDampLineal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDampLinealActionPerformed(evt);
            }
        });
        txtDampLineal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtDampLinealPropertyChange(evt);
            }
        });
        txtDampLineal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDampLinealKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel6.setText("Damp. Ang.:");

        txtDampAng.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        txtDampAng.setText("1");
        txtDampAng.setToolTipText("Metal pesado=0.001;  Pluma=10");
        txtDampAng.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDampAngActionPerformed(evt);
            }
        });
        txtDampAng.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtDampAngPropertyChange(evt);
            }
        });
        txtDampAng.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDampAngKeyReleased(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jLabel7.setText("F. Colisión:");

        cboColision.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        cboColision.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboColision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboColisionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optCinematico)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(optDinamico)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optEstatico)))
                .addGap(0, 81, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboColision, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDampLineal)
                    .addComponent(txtRestitucion)
                    .addComponent(txtMasa)
                    .addComponent(txtFriccion)
                    .addComponent(txtDampAng)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(optDinamico)
                    .addComponent(optEstatico))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(optCinematico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboColision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMasa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtFriccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtRestitucion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDampLineal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtDampAng, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void optDinamicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optDinamicoActionPerformed
        if (Float.valueOf(txtMasa.getText()) <= 0.0f) {
            txtMasa.setText(String.valueOf(1.0f));
        }
        aplicarCambios();
    }//GEN-LAST:event_optDinamicoActionPerformed

    private void optEstaticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optEstaticoActionPerformed
        txtMasa.setText(String.valueOf(0.0f));
        aplicarCambios();
    }//GEN-LAST:event_optEstaticoActionPerformed

    private void optCinematicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optCinematicoActionPerformed
        aplicarCambios();
    }//GEN-LAST:event_optCinematicoActionPerformed

    private void txtMasaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMasaActionPerformed
        aplicarCambios();
    }//GEN-LAST:event_txtMasaActionPerformed

    private void txtMasaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtMasaPropertyChange
        aplicarCambios();
    }//GEN-LAST:event_txtMasaPropertyChange

    private void txtFriccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFriccionActionPerformed
        aplicarCambios();
    }//GEN-LAST:event_txtFriccionActionPerformed

    private void txtFriccionPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtFriccionPropertyChange
        aplicarCambios();
    }//GEN-LAST:event_txtFriccionPropertyChange

    private void txtRestitucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRestitucionActionPerformed
        aplicarCambios();
    }//GEN-LAST:event_txtRestitucionActionPerformed

    private void txtRestitucionPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtRestitucionPropertyChange
        aplicarCambios();
    }//GEN-LAST:event_txtRestitucionPropertyChange

    private void txtDampLinealActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDampLinealActionPerformed
        aplicarCambios();
    }//GEN-LAST:event_txtDampLinealActionPerformed

    private void txtDampLinealPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtDampLinealPropertyChange
        aplicarCambios();
    }//GEN-LAST:event_txtDampLinealPropertyChange

    private void txtDampAngActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDampAngActionPerformed
        aplicarCambios();
    }//GEN-LAST:event_txtDampAngActionPerformed

    private void txtDampAngPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtDampAngPropertyChange
        aplicarCambios();
    }//GEN-LAST:event_txtDampAngPropertyChange

    private void txtMasaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMasaKeyReleased
        aplicarCambios();        // TODO add your handling code here:
    }//GEN-LAST:event_txtMasaKeyReleased

    private void txtFriccionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFriccionKeyReleased
        aplicarCambios();        // TODO add your handling code here:
    }//GEN-LAST:event_txtFriccionKeyReleased

    private void txtRestitucionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRestitucionKeyReleased
        aplicarCambios();        // TODO add your handling code here:
    }//GEN-LAST:event_txtRestitucionKeyReleased

    private void txtDampLinealKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDampLinealKeyReleased
        aplicarCambios();        // TODO add your handling code here:
    }//GEN-LAST:event_txtDampLinealKeyReleased

    private void txtDampAngKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDampAngKeyReleased
        aplicarCambios();
    }//GEN-LAST:event_txtDampAngKeyReleased

    private void cboColisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboColisionActionPerformed
        aplicarCambios();
    }//GEN-LAST:event_cboColisionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboColision;
    private javax.swing.ButtonGroup grupoTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton optCinematico;
    private javax.swing.JRadioButton optDinamico;
    private javax.swing.JRadioButton optEstatico;
    private javax.swing.JTextField txtDampAng;
    private javax.swing.JTextField txtDampLineal;
    private javax.swing.JTextField txtFriccion;
    private javax.swing.JTextField txtMasa;
    private javax.swing.JTextField txtRestitucion;
    // End of variables declaration//GEN-END:variables
}
