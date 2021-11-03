package net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.colladaLoader;

import java.util.List;

import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.AnimationData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.JointTransformData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.KeyFrameData;
import net.qoopo.engine3d.core.util.xmlParser.XmlNode;
import org.joml.Matrix4f;

public class AnimationLoader {

//    private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
    private XmlNode animationData;
    private XmlNode jointHierarchy;

    public AnimationLoader(XmlNode animationData, XmlNode jointHierarchy) {
        this.animationData = animationData;
        this.jointHierarchy = jointHierarchy;
    }

    public AnimationData extractAnimation() {
        String rootNode = findRootJointName();
        float[] times = getKeyTimes();
        float duration = times[times.length - 1];
        KeyFrameData[] keyFrames = initKeyFrames(times);
        List<XmlNode> animationNodes = animationData.getChildren("animation");
        for (XmlNode jointNode : animationNodes) {
            loadJointTransforms(keyFrames, jointNode, rootNode);
        }
        return new AnimationData(duration, keyFrames);
    }

    private float[] getKeyTimes() {
        XmlNode timeData = animationData.getChild("animation").getChild("source").getChild("float_array");
        String[] rawTimes = timeData.getData().split(" ");
        float[] times = new float[rawTimes.length];
        for (int i = 0; i < times.length; i++) {
            times[i] = Float.parseFloat(rawTimes[i]);
        }
        return times;
    }

    private KeyFrameData[] initKeyFrames(float[] times) {
        KeyFrameData[] frames = new KeyFrameData[times.length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = new KeyFrameData(times[i]);
        }
        return frames;
    }

    private void loadJointTransforms(KeyFrameData[] frames, XmlNode jointData, String rootNodeId) {
        String jointNameId = getJointName(jointData);
        String dataId = getDataId(jointData);
        XmlNode transformData = jointData.getChildWithAttribute("source", "id", dataId);
        String[] rawData = transformData.getChild("float_array").getData().split(" ");
        processTransforms(jointNameId, rawData, frames, jointNameId.equals(rootNodeId));
    }

    private String getDataId(XmlNode jointData) {
        XmlNode node = jointData.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT");
        return node.getAttribute("source").substring(1);
    }

    private String getJointName(XmlNode jointData) {
        XmlNode channelNode = jointData.getChild("channel");
        String data = channelNode.getAttribute("target");
        return data.split("/")[0];
    }

    private void processTransforms(String jointName, String[] rawData, KeyFrameData[] keyFrames, boolean root) {
//        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        float[] matrixData = new float[16];
        for (int i = 0; i < keyFrames.length; i++) {
            int c = 15;
            for (int j = 0; j < 16; j++) {
                matrixData[j] = Float.parseFloat(rawData[i * 16 + j]);
//                matrixData[c] = Float.parseFloat(rawData[i * 16 + j]);
                c--;
            }
//            buffer.clear();
//            buffer.put(matrixData);
//            buffer.flip();
            Matrix4f transform = new Matrix4f();
//            transform.load(buffer);
//            transform.set(buffer.array());
            transform.set(matrixData);
//            transform.transpose();

            if (root) {
                //because up axis in Blender is different to up axis in game
//                Matrix4f.mul(CORRECTION, transform, transform);
            }

            keyFrames[i].addJointTransform(new JointTransformData(jointName, transform));
        }
    }

    private String findRootJointName() {
        //ejemplo thinkMatrix
        XmlNode skeleton = jointHierarchy.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
//ejemplod e ala araña
//        XmlNode skeleton = jointHierarchy.getChild("visual_scene").getChildWithAttribute("node", "id", "Spider_Armature");
        return skeleton.getChild("node").getAttribute("id");
    }

}
