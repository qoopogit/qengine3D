package net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.colladaLoader;
//package net.qoopo.motor3d.core.carga.impl.collada.colladaLoader;

import java.util.ArrayList;
import java.util.List;

import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.SkinningData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.VertexSkinData;
import net.qoopo.engine3d.core.util.xmlParser.XmlNode;

public class SkinLoader {

    private XmlNode skinningData;
    private int maxWeights;

    public SkinLoader(XmlNode controllersNode, int maxWeights) {
        try {
            this.skinningData = controllersNode.getChild("controller").getChild("skin");
            this.maxWeights = maxWeights;
        } catch (Exception e) {
            skinningData = null;
            maxWeights = 0;
        }
    }

    public SkinningData extractSkinData() {
        try {
            List<String> jointsList = loadJointsList();
            float[] weights = loadWeights();
            XmlNode weightsDataNode = skinningData.getChild("vertex_weights");
            int[] effectorJointCounts = getEffectiveJointsCounts(weightsDataNode);
            List<VertexSkinData> vertexWeights = getSkinData(weightsDataNode, effectorJointCounts, weights);
            return new SkinningData(jointsList, vertexWeights);
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> loadJointsList() {
        XmlNode inputNode = skinningData.getChild("vertex_weights");
        String jointDataId = inputNode.getChildWithAttribute("input", "semantic", "JOINT").getAttribute("source")
                .substring(1);
        XmlNode jointsNode = skinningData.getChildWithAttribute("source", "id", jointDataId).getChild("Name_array");
        String[] names = jointsNode.getData().split(" ");
        List<String> jointsList = new ArrayList<String>();
        for (String name : names) {
            jointsList.add(name);
        }
        return jointsList;
    }

    private float[] loadWeights() {
        XmlNode inputNode = skinningData.getChild("vertex_weights");
        String weightsDataId = inputNode.getChildWithAttribute("input", "semantic", "WEIGHT").getAttribute("source")
                .substring(1);
        XmlNode weightsNode = skinningData.getChildWithAttribute("source", "id", weightsDataId).getChild("float_array");
        String[] rawData = weightsNode.getData().split(" ");
        float[] weights = new float[rawData.length];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Float.parseFloat(rawData[i]);
        }
        return weights;
    }

    private int[] getEffectiveJointsCounts(XmlNode weightsDataNode) {
        String[] rawData = weightsDataNode.getChild("vcount").getData().split(" ");
        int[] counts = new int[rawData.length];
        for (int i = 0; i < rawData.length; i++) {
            counts[i] = Integer.parseInt(rawData[i]);
        }
        return counts;
    }

    private List<VertexSkinData> getSkinData(XmlNode weightsDataNode, int[] counts, float[] weights) {
        String[] rawData = weightsDataNode.getChild("v").getData().split(" ");
        List<VertexSkinData> skinningData = new ArrayList<VertexSkinData>();
        int pointer = 0;
        for (int count : counts) {
            VertexSkinData skinData = new VertexSkinData();
            for (int i = 0; i < count; i++) {
                int jointId = Integer.parseInt(rawData[pointer++]);
                int weightId = Integer.parseInt(rawData[pointer++]);
                skinData.addJointEffect(jointId, weights[weightId]);
            }
            skinData.limitJointNumber(maxWeights);
            skinningData.add(skinData);
        }
        return skinningData;
    }

}
