package net.qoopo.engine3d.core.carga.impl.assimp.imp;

//import org.joml.Matrix4f;
import net.qoopo.engine3d.core.math.QMatriz4;
import org.lwjgl.assimp.AIBone;

public class Bone {

    private final int boneId;
    private int boneParentId;
    private final String boneName;
    private QMatriz4 offsetMatrix;
    private AIBone aiBone;
    
    //cadena utilizada para ordenar la lista desde los nodos mas cercanos a la raiz hasta los nodos que estan mas debajo en la jerarquia
    private String rutaJerarquia;

    public Bone(int boneId, String boneName, QMatriz4 offsetMatrix, AIBone aiBone) {
        this.boneId = boneId;
        this.boneName = boneName;
        this.offsetMatrix = offsetMatrix;
        this.aiBone = aiBone;
    }

    public int getBoneParentId() {
        return boneParentId;
    }

    public void setBoneParentId(int boneParentId) {
        this.boneParentId = boneParentId;
    }

    public int getBoneId() {
        return boneId;
    }

    public String getBoneName() {
        return boneName;
    }

    public QMatriz4 getOffsetMatrix() {
        return offsetMatrix;
    }

    public AIBone getAiBone() {
        return aiBone;
    }

    public void setAiBone(AIBone aiBone) {
        this.aiBone = aiBone;
    }

    public String getRutaJerarquia() {
        return rutaJerarquia;
    }

    public void setRutaJerarquia(String rutaJerarquia) {
        this.rutaJerarquia = rutaJerarquia;
    }
    
    

}
