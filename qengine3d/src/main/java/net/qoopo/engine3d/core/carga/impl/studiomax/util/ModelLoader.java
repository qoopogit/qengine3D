package net.qoopo.engine3d.core.carga.impl.studiomax.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Facade for loading a 3ds model in different ways.
 *
 * @author Kjetil �ster�s
 */
public class ModelLoader {

    public static Model3DS load3dModel(File file) throws ParserException {
        try {
            FileInputStream stream = new FileInputStream(file);
            return load3dModel(stream);
        } catch (FileNotFoundException e) {
            throw new ParserException("Unable to find file " + file.getAbsolutePath(), e);
        }
    }

    public static Model3DS load3dModel(FileInputStream stream) throws ParserException {
        FileChannel channel = null;
        try {
            channel = stream.getChannel();
            MapReader reader = new MapReader(channel);
            Parser parser = new Parser(reader);
            return parser.parseFile();
        } catch (IOException e) {
            throw new ParserException(e);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    // ignore this
                }
            }
        }
    }

    public static Model3DS load3dModel(InputStream stream) throws ParserException {
        try {
            StreamReader reader = new StreamReader(stream);
            Parser parser = new Parser(reader);
            return parser.parseFile();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // ignore this
            }
        }
    }
}
