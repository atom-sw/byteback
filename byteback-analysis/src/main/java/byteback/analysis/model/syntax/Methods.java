package byteback.analysis.model.syntax;

import byteback.common.function.Lazy;

public class Methods {

    private static final Lazy<Methods> instance = Lazy.from(Methods::new);

    public static Methods v() {
        return instance.get();
    }

    private Methods() {
    }

}
