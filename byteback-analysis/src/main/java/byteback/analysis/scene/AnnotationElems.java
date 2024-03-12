package byteback.analysis.scene;

import byteback.analysis.scene.visitor.AbtractAnnotationElementSwitch;
import byteback.common.function.Lazy;
import soot.tag.AnnotationClassElement;
import soot.tag.AnnotationStringElement;

import java.util.Optional;

public class AnnotationElems {

    private static final Lazy<AnnotationElems> instance = Lazy.from(AnnotationElems::new);

    public static AnnotationElems v() {
        return instance.get();
    }

    private AnnotationElems() {
    }

    public class StringElementExtractor extends AbtractAnnotationElementSwitch<Optional<String>> {

        public String value;

        @Override
        public void caseAnnotationStringElem(final AnnotationStringElement element) {
            this.value = element.getValue();
        }

        @Override
        public Optional<String> getResult() {
            return Optional.ofNullable(value);
        }

    }

    public class ClassElementExtractor extends AbtractAnnotationElementSwitch<Optional<String>> {

        public String value;

        @Override
        public void caseAnnotationClassElem(final AnnotationClassElement element) {
            this.value = element.getDesc();
        }

        @Override
        public Optional<String> getResult() {
            return Optional.ofNullable(value);
        }

    }

}
