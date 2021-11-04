/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.editor.assets;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.audio.openal.QBufferSonido;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.editor.util.QArbolWrapper;
import net.qoopo.engine3d.editor.util.Util;

/**
 *
 * @author alberto
 */
public class PnlGestorRecursos extends javax.swing.JPanel {

    public static PnlGestorRecursos instancia=null;

    /**
     * Creates new form PnlGestorRecursos
     */
    public PnlGestorRecursos() {
        initComponents();
        instancia = this;        
        treeActivos.setCellRenderer(new ArbolEntidadRenderer());
        actualizarArbol();
    }

    public void actualizarArbol() {
        // actualizo el arbol
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode(new QArbolWrapper("Recursos", null));
        DefaultMutableTreeNode ent = new DefaultMutableTreeNode(new QArbolWrapper("Entidades", null));
        DefaultMutableTreeNode geom = new DefaultMutableTreeNode(new QArbolWrapper("Geometrías", null));
        DefaultMutableTreeNode mats = new DefaultMutableTreeNode(new QArbolWrapper("Materiales", null));
        DefaultMutableTreeNode text = new DefaultMutableTreeNode(new QArbolWrapper("Texturas", null));
        DefaultMutableTreeNode audi = new DefaultMutableTreeNode(new QArbolWrapper("Audios", null));

        raiz.add(ent);
        raiz.add(geom);
        raiz.add(mats);
        raiz.add(text);
        raiz.add(audi);
        for (Object objeto : QGestorRecursos.mapa.values()) {
            if (objeto instanceof QEntidad) {
                ent.add(new DefaultMutableTreeNode(new QArbolWrapper(((QEntidad) objeto).getNombre(), objeto)));
            } else if (objeto instanceof QGeometria) {
                geom.add(new DefaultMutableTreeNode(new QArbolWrapper(((QGeometria) objeto).nombre, objeto)));
            } else if (objeto instanceof QMaterialBas) {
                mats.add(new DefaultMutableTreeNode(new QArbolWrapper(((QMaterialBas) objeto).getNombre(), objeto)));
            } else if (objeto instanceof QTextura) {
                text.add(new DefaultMutableTreeNode(new QArbolWrapper("Textura", objeto)));
            } else if (objeto instanceof QBufferSonido) {
                audi.add(new DefaultMutableTreeNode(new QArbolWrapper("Buffer", objeto)));
            }
        }
        treeActivos.setModel(new DefaultTreeModel(raiz));
    }

    public class ArbolEntidadRenderer extends JLabel implements TreeCellRenderer {

        private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        private Color backgroundSelectionColor;

        private Color backgroundNonSelectionColor;

        public ArbolEntidadRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
            backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            try {
//                int selectedIndex = ((Integer) value).intValue();
                if (selected) {
                    setBackground(backgroundSelectionColor);
//                    setBackground(tree.getSelectionBackground());
//                    setForeground(tree.getSelectionForeground());
                } else {
                    setBackground(backgroundNonSelectionColor);
//                    setBackground(tree.getBackground());
//                    setForeground(tree.getForeground());
                }

//                setBackground(tree.getBackground());
//                setForeground(tree.getForeground());
                ImageIcon icon = Util.cargarIcono16("/cube.png");
//                ImageIcon icon;
//                if (leaf) {
//                    icon = (ImageIcon) defaultRenderer.getDefaultLeafIcon();
//                } else if (expanded) {
//                    icon = (ImageIcon) defaultRenderer.getDefaultOpenIcon();
//                } else {
//                    icon = (ImageIcon) defaultRenderer.getDefaultOpenIcon();
//                }
                String texto = "";
                Object valor = ((DefaultMutableTreeNode) value).getUserObject();
                if (valor instanceof QArbolWrapper) {
                    QArbolWrapper wraper = (QArbolWrapper) valor;                    
                    if (wraper.getObjeto() == null) {
//                        icon = Util.cargarIcono16("/cube.png");
                    } else if (wraper.getObjeto() instanceof QGeometria) {
                        icon = Util.cargarIcono16("/teapot_16.png");
                    } else if (wraper.getObjeto() instanceof QMaterialBas) {
                        icon = Util.cargarIcono16("/text_quad_16.png");
                    } else if (wraper.getObjeto() instanceof QEntidad) {
                        icon = Util.cargarIcono16("/cube.png");
                    } else if (wraper.getObjeto() instanceof QTextura) {
                        icon = Util.cargarIcono16("/text_16.png");
                    } else if (wraper.getObjeto() instanceof QBufferSonido) {
                        icon = Util.cargarIcono16("/sound_on_16.png");
                    } else {
//                        icon = Util.cargarIcono16("/teapot_16.png");
                    }
                    texto = wraper.getNombre();
                } else if (valor instanceof String) {
                    texto = (String) valor;
                } else {
                    texto = "N/A";
                }

                setIcon(icon);

                setText(texto);
//                setFont(list.getFont());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeActivos = new javax.swing.JTree();
        jButton1 = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("GESTOR RECURSOS");

        jScrollPane1.setViewportView(treeActivos);

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        actualizarArbol();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree treeActivos;
    // End of variables declaration//GEN-END:variables
}
