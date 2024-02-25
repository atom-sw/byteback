package byteback.converter.soottoboogie.method.procedure;

import byteback.converter.soottoboogie.Convention;
import byteback.converter.soottoboogie.ConversionException;
import byteback.converter.soottoboogie.Prelude;
import byteback.converter.soottoboogie.method.function.FunctionManager;
import byteback.frontend.boogie.ast.Condition;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.ValueReference;
import soot.*;
import soot.tagkit.AnnotationTag;

public abstract class ConditionConverter {

    final SootMethod target;

    public ConditionConverter(final SootMethod target) {
        this.target = target;
    }

    public List<Expression> makeSourceArguments(final SootMethod method) {
        final List<Expression> references = new List<>();
        references.add(Prelude.v().getHeapVariable().makeValueReference());

        for (final Local local : ProcedureConverter.getParameterLocals(method)) {
            references.add(ValueReference.of(ProcedureConverter.parameterName(local)));
        }

        if (method.getReturnType() != VoidType.v()) {
            references.add(Convention.makeReturnReference());
        }

        references.add(Convention.makeExceptionReference());

        return references;
    }

    public Expression makeConditionExpression(final SootMethod source) {
        final List<Expression> arguments = makeSourceArguments(target);

        return FunctionManager.v().convert(source).getFunction().inline(arguments);
    }

    public Expression convertSource(final String sourceName, final java.util.List<java.util.List<Type>> signatures) {
        final SootClass clazz = target.getDeclaringClass();
        SootMethod source = null;

        for (java.util.List<Type> signature : signatures) {
            source = clazz.getMethodUnsafe(sourceName, signature, BooleanType.v());

            if (source != null)
                break;
        }

        if (source == null) {
            throw new ConversionException("Unable to find predicate " + sourceName + " in class " + clazz.getName()
                    + ", matching method" + target.getName());
        }

        if (source.isStatic() != target.isStatic()) {
            throw new ConversionException("Incompatible target type for condition method " + source.getName());
        }

        RootResolver.v().ensureResolved(source);

        return makeConditionExpression(source);
    }

    public abstract Condition convert(final AnnotationTag tag);
}
