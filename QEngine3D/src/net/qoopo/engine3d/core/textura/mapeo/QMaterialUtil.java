/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura.mapeo;

import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public class QMaterialUtil {

    public static QGeometria suavizar(QGeometria geometria, boolean suave) {
        for (QPrimitiva face : geometria.listaPrimitivas) {
            if (face instanceof QPoligono) {
                ((QPoligono)face).smooth = suave;
            }
        }
        return geometria;
    }

    /**
     * Aplica un material a todo el objeto
     *
     * @param objeto
     * @param material
     * @return
     */
    public static QGeometria aplicarMaterial(QGeometria objeto, QMaterial material) {
        for (QPrimitiva primitiva : objeto.listaPrimitivas) {
            primitiva.material = material;
        }
        if (objeto instanceof QForma) {
            ((QForma) objeto).setMaterial(material);
        }
        return objeto;
    }

    public static QGeometria aplicarColor(QGeometria objeto, float alpha, QColor colorDifuso, QColor colorEspecular, float factorEmisionLuz, int specularExponent) {
        QMaterialBas material = null;
        try {
            material = new QMaterialBas();
            material.setTransAlfa(alpha);
            material.setColorBase(colorDifuso);
//            material.setColorEspecular(colorEspecular);
            material.setFactorEmision(factorEmisionLuz);
            material.setSpecularExponent(specularExponent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aplicarMaterial(objeto, material);
    }

    public static QGeometria aplicarColor(QGeometria objeto, float alpha, float r, float g, float b, float rS, float gS, float bS, float factorEmisionLuz, int specularExponent) {
        QMaterialBas material = null;
        try {
            material = new QMaterialBas();
            material.setTransAlfa(alpha);
            material.setColorBase(new QColor(1, r, g, b));
//            material.setColorEspecular(new QColor(1, rS, gS, bS));
            material.setFactorEmision(factorEmisionLuz);
            material.setSpecularExponent(specularExponent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aplicarMaterial(objeto, material);
    }

    public static QGeometria aplicarColor(QGeometria objeto, float alpha, float r, float g, float b, float rS, float gS, float bS, int specularExponent) {
        QMaterialBas material = null;
        try {
            material = new QMaterialBas();
            material.setTransAlfa(alpha);
            material.setTransparencia(alpha < 1.0f);
            material.setColorBase(new QColor(1, r, g, b));
//            material.setColorEspecular(new QColor(1, rS, gS, bS));
            material.setSpecularExponent(specularExponent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aplicarMaterial(objeto, material);
    }

}
