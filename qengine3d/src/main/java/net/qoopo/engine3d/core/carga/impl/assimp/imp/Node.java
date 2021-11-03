package net.qoopo.engine3d.core.carga.impl.assimp.imp;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.core.math.QMatriz4;
import org.lwjgl.assimp.AINode;

public class Node {

    private AINode aiNode;
    private List<Node> children;
    private QMatriz4 transformacion;
    private String name;
    private Node parent;

    public Node(String name, Node parent, AINode aiNode) {
        this.name = name;
        this.parent = parent;
        this.aiNode = aiNode;
        //la transformacion que da es relativa a su padre.
        this.transformacion = AssimpLoader.toMatrix(aiNode.mTransformation());
        if (transformacion == null) {
            transformacion = new QMatriz4();
        }
        this.children = new ArrayList<>();
    }

    public QMatriz4 getTransformacion() {
        return transformacion;
    }

    public void setTransformacion(QMatriz4 transformacion) {
        this.transformacion = transformacion;
    }

    /**
     * Obtiene la trasnformacion acumulada, osea la transformacion en el mundo
     * real
     *
     * @return
     */
    public QMatriz4 getTransformacionAnidada() {
        if (parent == null) {
//            return new QMatriz4();
            return transformacion;
        } else {
//            QMatriz4 parentTransform = new QMatriz4(getParent().getTransformacionAnidada());
//            return parentTransform.mult(transformacion);            
            return getParent().getTransformacionAnidada().mult(transformacion);
        }
    }

    public String getRuta() {
        if (parent == null) {
//            return new QMatriz4();
            return name;
        } else {
//            QMatriz4 parentTransform = new QMatriz4(getParent().getTransformacionAnidada());
//            return parentTransform.mult(transformacion);            
            return getParent().getRuta() + "/" + name;
        }
    }

//
//    /**
//     * Obtiene la transformacion anidada
//     *
//     * @param node
//     * @param framePos
//     * @return
//     */
//    public static QMatriz4 getParentTransforms(Node node, int framePos) {
//        if (node == null) {
//            return new QMatriz4();
//        } else {
//            QMatriz4 parentTransform = new QMatriz4(getParentTransforms(node.getParent(), framePos));
//            List<QMatriz4> transformations = node.getTransformations();
//            QMatriz4 nodeTransform;
//            if (framePos < transformations.size()) {
//                nodeTransform = transformations.get(framePos);
//            } else {
//                nodeTransform = new QMatriz4();
//            }
////            return parentTransform.mul(nodeTransform);
//            return parentTransform.multLocal(nodeTransform);
//
//        }
//    }
    public void addChild(Node node) {
        this.children.add(node);
    }

//    public void addTransformation(QMatriz4 transformation) {
//        transformations.add(transformation);
//    }
    public Node findByName(String targetName) {
        Node result = null;
        if (this.name.equals(targetName)) {
            result = this;
        } else {
            for (Node child : children) {
                result = child.findByName(targetName);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

//    public int getAnimationFrames() {
//        int numFrames = this.transformations.size();
//        for (Node child : children) {
//            int childFrame = child.getAnimationFrames();
//            numFrames = Math.max(numFrames, childFrame);
//        }
//        return numFrames;
//    }
    public List<Node> getChildren() {
        return children;
    }

//    public List<QMatriz4> getTransformations() {
//        return transformations;
//    }
    public String getName() {
        return name;
    }

    public Node getParent() {
        return parent;
    }

    public AINode getAiNode() {
        return aiNode;
    }

    public void setAiNode(AINode aiNode) {
        this.aiNode = aiNode;
    }

}
