/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos.brazo;

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;

/*
  Mi clase para controlar los Tankes
 */
class Movimiento extends Behavior {

    final static int HOMBRO = 0;
    final static int CODO = 1;
    final static int MUNECA = 2;
    final static int PISO = 3;
    private TransformGroup piso, hombro, codo, muneca;
    private Transform3D tr;
    private Transform3D rox, roy, roz;
    private WakeupOnAWTEvent trigger;
    private double ahom, acod, amun;
    private double bhom, bcod, bmun;
    private double ypiso;
    private int place;
    private Appearance pint1, pint2, pint3, pint4;
    private Material mat1, mat2;

    public Movimiento(TransformGroup p, TransformGroup h, TransformGroup c, TransformGroup m,
            Appearance p1, Appearance p2, Appearance p3, Appearance p4, Material m1, Material m2) {
        piso = p;
        hombro = h;
        codo = c;
        muneca = m;
        pint1 = p1;
        pint2 = p2;
        pint3 = p3;
        pint4 = p4;
        mat1 = m1;
        mat2 = m2;
        tr = new Transform3D();
        rox = new Transform3D();
        roy = new Transform3D();
        roz = new Transform3D();
        trigger = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        place = PISO;
    }

    public void initialize() {
        this.wakeupOn(trigger);
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                AWTEvent[] arr = ((WakeupOnAWTEvent) (wakeup)).getAWTEvent();
                KeyEvent ke = (KeyEvent) arr[0];
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        switch (place) {
                            case PISO:
                                pint1.setMaterial(mat1);
                                pint2.setMaterial(mat2);
                                place = HOMBRO;
                                break;
                            case HOMBRO:
                                pint2.setMaterial(mat1);
                                pint3.setMaterial(mat2);
                                place = CODO;
                                break;
                            case CODO:
                                pint3.setMaterial(mat1);
                                pint4.setMaterial(mat2);
                                place = MUNECA;
                                break;
                            default:
                                pint4.setMaterial(mat1);
                                pint1.setMaterial(mat2);
                                place = PISO;
                                break;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (place == HOMBRO) {
                            if (bhom > 0) {
                                bhom -= Math.PI / 20.0;
                            }
                        } else if (place == CODO) {
                            if (bcod > 0) {
                                bcod -= Math.PI / 20.0;
                            }
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (place == HOMBRO) {
                            if (bhom < Math.PI / 2.0) {
                                bhom += Math.PI / 20.0;
                            }
                        } else if (place == CODO) {
                            if (bcod < Math.PI / 2.0) {
                                bcod += Math.PI / 20.0;
                            }
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        switch (place) {
                            case PISO:
                                if (ypiso > -2) {
                                    ypiso -= 0.02;
                                }
                                break;
                            case HOMBRO:
                                if (ahom > -Math.PI / 2.0) {
                                    ahom -= Math.PI / 20.0;
                                }
                                break;
                            case CODO:
                                if (acod > -Math.PI / 2.0) {
                                    acod -= Math.PI / 20.0;
                                }
                                break;
                            default:
                                if (amun > -Math.PI / 2.0) {
                                    amun -= Math.PI / 20.0;
                                }
                                break;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (place == PISO) {
                            if (ypiso < 2) {
                                ypiso += 0.02;
                            }
                        } else if (place == HOMBRO) {
                            if (ahom < Math.PI / 2.0) {
                                ahom += Math.PI / 20.0;
                            }
                        } else if (place == CODO) {
                            if (acod < Math.PI / 2.0) {
                                acod += Math.PI / 20.0;
                            }
                        } else {
                            if (amun < Math.PI / 2.0) {
                                amun += Math.PI / 20.0;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        //modificar los Tgroups
        tr.set(new Vector3f((float) ypiso, 0.0f, 0.0f));
        piso.setTransform(tr);
        rox.rotX(bhom);
        roz.rotZ(ahom);
        roz.mul(rox);
        hombro.setTransform(roz);
        rox.rotX(bcod);
        roz.rotZ(acod);
        roz.mul(rox);
        codo.setTransform(roz);
        roz.rotZ(amun);
        muneca.setTransform(roz);
        this.wakeupOn(trigger);
    }
}
