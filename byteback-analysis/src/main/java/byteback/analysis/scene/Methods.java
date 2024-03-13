package byteback.analysis.scene;

import byteback.common.function.Lazy;
import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.model.syntax.MethodModel;
import byteback.analysis.body.jimple.syntax.internal.JimpleLocal;

import java.util.ArrayList;
import java.util.List;

public class Methods {

    private static final Lazy<Methods> instance = Lazy.from(Methods::new);

    public static Methods v() {
        return instance.get();
    }

    private Methods() {
    }

    public List<Local> makeFakeParameterLocals(final MethodModel method) {
        final List<Local> parameterLocals = new ArrayList<>();

        if (!method.isStatic()) {
            parameterLocals.add(new JimpleLocal("this", method.getDeclaringClass().getClassType()));
        }

        for (int i = 0; i < method.getParameterCount(); ++i) {
            final String name = "p" + i;
            parameterLocals.add(new JimpleLocal(name, method.getParameterType(i)));
        }

        return parameterLocals;
    }

}
