package byteback.analysis.body.common.source;

import java.util.Optional;

public interface ClassSourceProvider {

    Optional<ClassSource> find(String name);
}
