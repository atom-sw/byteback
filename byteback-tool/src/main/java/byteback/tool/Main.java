package byteback.tool;

import byteback.analysis.body.jimple.transformer.CallExprTransformer;
import byteback.analysis.body.jimple.transformer.OldExprTransformer;
import byteback.analysis.body.jimple.transformer.VimpUnitBodyTransformer;
import byteback.analysis.body.jimple.transformer.VimpValueBodyTransformer;
import byteback.analysis.body.vimp.transformer.*;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.JastAddJ.Opt;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

import javax.swing.text.html.Option;
import java.io.File;
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
        Options.v().set_whole_program(true);
        Options.v().set_drop_bodies_after_load(true);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(Arguments.v().formatClassPaths());
        Options.v().set_throw_analysis(Options.throw_analysis_pedantic);
        Options.v().set_check_init_throw_analysis(Options.check_init_throw_analysis_pedantic);
        Options.v().classes().addAll(Arguments.v().getStartingClasses());

        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("cg", "enabled:false");
        Options.v().setPhaseOption("wjtp", "enabled:true");
        Options.v().setPhaseOption("jtp", "enabled:true");

        final Pack jtpPack = PackManager.v().getPack("jtp");

        jtpPack.add(new Transform("jtp.ii", InvokeFilter.v()));
        jtpPack.add(new Transform("jtp.dir", DynamicInvokeResolver.v()));
        jtpPack.add(new Transform("jtp.vut", VimpUnitBodyTransformer.v()));
        jtpPack.add(new Transform("jtp.vvt", VimpValueBodyTransformer.v()));
        jtpPack.add(new Transform("jtp.cet", CallExprTransformer.v()));
        jtpPack.add(new Transform("jtp.oet", OldExprTransformer.v()));
        jtpPack.add(new Transform("jtp.qft", QuantifierValueTransformer.v()));
        jtpPack.add(new Transform("jtp.vgg", SpecificationExprFolder.v()));
        jtpPack.add(new Transform("jtp.eit", NormalLoopExitSpecifier.v()));
        jtpPack.add(new Transform("jtp.ict", IndexCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.nct", NullCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.cct", InvokeCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.tai", ThisAssumptionInserter.v()));
        jtpPack.add(new Transform("jtp.cai", CaughtAssumptionInserter.v()));
        jtpPack.add(new Transform("jtp.gt", GuardTransformer.v()));
        jtpPack.add(new Transform("jtp.ie", InvariantExpander.v()));
        jtpPack.add(new Transform("jtp.ule", UnusedLocalEliminator.v()));
        jtpPack.add(new Transform("jtp.print", new BodyTransformer() {
            @Override
            protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
                System.out.println(b);
            }
        }));

        soot.Main.v().run(new String[]{});
    }

}
