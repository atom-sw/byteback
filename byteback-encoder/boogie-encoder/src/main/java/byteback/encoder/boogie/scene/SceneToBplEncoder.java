package byteback.encoder.boogie.scene;

import byteback.encoder.boogie.type.HierarchyToBplEncoder;
import byteback.encoder.common.scene.SceneEncoder;
import soot.Scene;
import soot.SootClass;

import java.io.*;
import java.util.Iterator;

public class SceneToBplEncoder extends SceneEncoder {

    public SceneToBplEncoder(final PrintWriter writer) {
        super(writer);
    }

    @Override
    public void transformScene(final Scene scene) {
        try (final InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("boogie/ByteBackPrelude.bpl")) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            }
            final Iterator<SootClass> classIterator = scene.getClasses().snapshotIterator();

            while (classIterator.hasNext()) {
                final SootClass sootClass = classIterator.next();
                new HierarchyToBplEncoder(writer).transformClass(scene, sootClass);
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
