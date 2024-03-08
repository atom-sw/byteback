package byteback.tool;

import byteback.analysis.body.jimple.transformer.CallExprTransformer;
import byteback.analysis.body.jimple.transformer.OldExprTransformer;
import byteback.analysis.body.jimple.transformer.VimpUnitBodyTransformer;
import byteback.analysis.body.jimple.transformer.VimpValueBodyTransformer;
import byteback.analysis.body.vimp.transformer.*;
import byteback.analysis.scene.properties.PostconditionsProperty;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

import java.util.Map;

public class Main {

    public static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        Arguments.v().parse(args);

        try {
            if (Arguments.v().getHelp()) {
                Arguments.v().getJCommander().usage();
                return;
            }
        } catch (final ParameterException exception) {
            log.error("Error while parsing program arguments: {}", exception.getMessage());
            exception.getJCommander().usage();
        }

        Options.v().set_unfriendly_mode(true);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(Arguments.v().formatClassPaths());
        Options.v().classes().addAll(Arguments.v().getStartingClasses());

        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("jtp", "enabled:true");
        Options.v().setPhaseOption("wjtp", "enabled:true");

        final Pack jtpPack = PackManager.v().getPack("jtp");

        jtpPack.add(new Transform("jtp.ii", InvokeIgnorer.v()));
        jtpPack.add(new Transform("jtp.dir", DynamicInvokeResolver.v()));
        jtpPack.add(new Transform("jtp.vut", VimpUnitBodyTransformer.v()));
        jtpPack.add(new Transform("jtp.vvt", VimpValueBodyTransformer.v()));
        jtpPack.add(new Transform("jtp.cet", CallExprTransformer.v()));
        jtpPack.add(new Transform("jtp.oet", OldExprTransformer.v()));
        jtpPack.add(new Transform("jtp.qft", QuantifierValueTransformer.v()));
        jtpPack.add(new Transform("jtp.vgg", SpecificationExprFolder.v()));
        jtpPack.add(new Transform("jtp.eit", ExceptionInvariantTransformer.v()));
        jtpPack.add(new Transform("jtp.ict", IndexCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.nct", NullCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.cct", CallCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.tat", ThisAssumptionTransformer.v()));
        jtpPack.add(new Transform("jtp.gt", GuardTransformer.v()));
        jtpPack.add(new Transform("jtp.ie", InvariantExpander.v()));
        jtpPack.add(new Transform("jtp.ule", UnusedLocalEliminator.v()));
        jtpPack.add(new Transform("jtp.test", new BodyTransformer() {
            @Override
            protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
                PostconditionsProperty.v().of(b.getMethod());
            }

        }));

        soot.Main.main(new String[]{});
    }

}
