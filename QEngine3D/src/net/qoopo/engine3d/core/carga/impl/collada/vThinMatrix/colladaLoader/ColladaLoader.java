package net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.colladaLoader;

import java.io.File;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.AnimatedModelData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.AnimationData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.MeshData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.SkeletonData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.SkinningData;
import net.qoopo.engine3d.core.util.xmlParser.XmlNode;
import net.qoopo.engine3d.core.util.xmlParser.XmlParser;

public class ColladaLoader {

    public static AnimatedModelData loadColladaModel(File colladaFile, int maxWeights) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);
        GeometryLoader g = null;
        MeshData meshData = null;
        SkeletonData jointsData = null;
        SkeletonLoader jointsLoader = null;
        SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
        SkinningData skinningData = skinLoader.extractSkinData();
        if (skinningData != null) {
            jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
            jointsData = jointsLoader.extractBoneData();
            g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
            meshData = g.extractModelData();
        } else {
            g = new GeometryLoader(node.getChild("library_geometries"),null);
            meshData = g.extractModelData();
        }

        return new AnimatedModelData(meshData, jointsData);
    }

    public static AnimationData loadColladaAnimation(File colladaFile) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);
        XmlNode animNode = node.getChild("library_animations");
        XmlNode jointsNode = node.getChild("library_visual_scenes");
        AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
        AnimationData animData = loader.extractAnimation();
        return animData;
    }

}
