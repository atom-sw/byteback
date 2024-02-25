package byteback.converter.soottoboogie.program;

import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.analysis.model.SootHosts;
import byteback.common.Lazy;
import byteback.converter.soottoboogie.field.FieldConverter;
import byteback.converter.soottoboogie.method.function.FunctionManager;
import byteback.converter.soottoboogie.method.procedure.ProcedureConverter;
import byteback.converter.soottoboogie.type.ClassHierarchyConverter;
import byteback.converter.soottoboogie.type.ReferenceTypeConverter;
import byteback.frontend.boogie.ast.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class ProgramConverter {

    private static final Lazy<ProgramConverter> instance = Lazy.from(ProgramConverter::new);
    public static Logger log = LoggerFactory.getLogger(ProgramConverter.class);

    private ProgramConverter() {
    }

    public static ProgramConverter v() {
        return instance.get();
    }

    public void convertFields(final Program program, final RootResolver resolver) {
        for (final SootField field : resolver.getUsedFields()) {
            program.addDeclarations(FieldConverter.instance().convert(field));
        }
    }

    public void convertMethods(final Program program, final RootResolver resolver) {
        for (final SootMethod method : resolver.getUsedMethods()) {
            log.info("Converting method {}", method.getSignature());

            if (SootHosts.hasAnnotation(method, BBLibNamespace.PRELUDE_ANNOTATION)) {
                continue;
            }

            if (SootHosts.hasAnnotation(method, BBLibNamespace.PURE_ANNOTATION)) {
                program.addDeclaration(FunctionManager.v().convert(method));
            } else if (!SootHosts.hasAnnotation(method, BBLibNamespace.PREDICATE_ANNOTATION)) {
                program.addDeclaration(ProcedureConverter.v().convert(method));
            }
        }
    }

    public void convertClasses(final Program program, final RootResolver resolver) {
        for (final SootClass clazz : resolver.getUsedClasses()) {
            program.addDeclaration(ReferenceTypeConverter.v().convert(clazz));
            program.addDeclarations(ClassHierarchyConverter.v().convert(clazz, resolver));
        }
    }

    public Program convert(final RootResolver resolver) {
        final var program = new Program();

        convertClasses(program, resolver);
        convertFields(program, resolver);
        convertMethods(program, resolver);

        return program;
    }

}
