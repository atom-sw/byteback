package byteback.converter.boogie.scene;

import byteback.converter.boogie.type.ClassToBplEncoder;
import byteback.converter.common.scene.SceneEncoder;
import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.type.declaration.context.ClassContext;
import soot.Scene;
import soot.SootClass;

import java.io.*;
import java.util.Iterator;

public class SceneToBplEncoder extends SceneEncoder {

    public SceneToBplEncoder(final PrintWriter writer) {
        super(writer);
    }

    @Override
    public void walkScene(final SceneContext context) {
        final Scene scene = context.getScene();

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
                new ClassToBplEncoder(writer).transformClass(new ClassContext(scene, sootClass));
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
