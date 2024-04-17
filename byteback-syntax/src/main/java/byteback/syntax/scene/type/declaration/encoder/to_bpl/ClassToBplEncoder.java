package byteback.syntax.scene.type.declaration.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.encoder.ClassEncoder;
import byteback.syntax.scene.type.declaration.encoder.context.ClassEncoderContext;
import byteback.syntax.scene.type.declaration.tag.AxiomsProvider;
import byteback.syntax.scene.type.declaration.tag.AxiomsTag;
import soot.SootClass;

import java.util.Optional;

public class ClassToBplEncoder extends ClassEncoder {

    private static final Lazy<ClassToBplEncoder> INSTANCE = Lazy.from(ClassToBplEncoder::new);

    public static ClassToBplEncoder v() {
        return INSTANCE.get();
    }

    private ClassToBplEncoder() {
    }

    @Override
    public void encode(final ClassEncoderContext classEncoderContext) {
        final SootClass sootClass = classEncoderContext.getSootClass();
        final Optional<AxiomsTag> axiomsTagOptional = AxiomsProvider.v().get(sootClass);

        if (axiomsTagOptional.isPresent()) {
            final AxiomsTag axiomsTag = axiomsTagOptional.get();
            
        }
    }

}
