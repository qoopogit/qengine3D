package net.qoopo.engine3d.componentes.iluminacion;

import net.qoopo.engine3d.core.math.QColor;

public class QIluminacion {

//    public float dR, dG, dB, sR, sG, sB;
    private QColor colorDifuso;
    private QColor colorEspecular;

    public QIluminacion() {
    }

//    public QIluminacion(float dR, float dG, float dB, float sR, float sG, float sB) {
//        this.dR = dR;
//        this.dG = dG;
//        this.dB = dB;
//        this.sR = sR;
//        this.sG = sG;
//        this.sB = sB;
//    }
//
//    void copyAttribute(QIluminacion other) {
//        this.dR = other.dR;
//        this.dG = other.dG;
//        this.dB = other.dB;
//        this.sR = other.sR;
//        this.sG = other.sG;
//        this.sB = other.sB;
//    }
//    @Override
//    public String toString() {
//        return dR + ", " + dG + ", " + dB + ", " + sR + ", " + sG + ", " + sB;
//    }

    public QIluminacion(QColor colorDifuso, QColor colorEspecular) {
        this.colorDifuso = colorDifuso;
        this.colorEspecular = colorEspecular;
    }

    public QColor getColorDifuso() {
        return colorDifuso;
    }

    public void setColorDifuso(QColor colorDifuso) {
        this.colorDifuso = colorDifuso;
    }

    public QColor getColorEspecular() {
        return colorEspecular;
    }

    public void setColorEspecular(QColor colorEspecular) {
        this.colorEspecular = colorEspecular;
    }

}
