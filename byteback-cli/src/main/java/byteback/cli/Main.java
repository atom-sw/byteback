package byteback.cli;

import byteback.analysis.body.jimple.transformer.VimpUnitBodyTransformer;
import byteback.analysis.body.jimple.transformer.VimpValueBodyTransformer;
import byteback.analysis.body.vimp.transformer.*;
import byteback.encoder.boogie.BplEncoder;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

import java.util.Map;

public class Main {

    public static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        Configuration.v().parse(args);

        try {
            if (Configuration.v().getHelp()) {
                Configuration.v().getJCommander().usage();
                return;
            }
        } catch (final ParameterException exception) {
            log.error("Error while parsing program arguments: {}", exception.getMessage());
            exception.getJCommander().usage();
        }

        Options.v().set_unfriendly_mode(true);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_grimp);
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(Configuration.v().formatClassPaths());
        Options.v().set_no_bodies_for_excluded(true);

        Options.v().setPhaseOption("jb", "use-original-names:true");
        
        Options.v().setPhaseOption("gc.cha", "apponly:true");
        Options.v().setPhaseOption("gop.ict", "enabled:true");
        Options.v().setPhaseOption("gop.nct", "enabled:true");
        Options.v().setPhaseOption("gb.a1", "enabled:false");
        Options.v().setPhaseOption("gb.cf", "enabled:false");
        Options.v().setPhaseOption("gb.a2", "enabled:false");
        
        Options.v().setPhaseOption("gop", "enabled:true");

        final Pack gopPack = PackManager.v().getPack("gop");

        gopPack.add(new Transform("gop.ii", InvokeIgnorer.v()));
        gopPack.add(new Transform("gop.d2s", DynamicToStaticTransformer.v()));
        gopPack.add(new Transform("gop.vut", VimpUnitBodyTransformer.v()));
        gopPack.add(new Transform("gop.vvt", VimpValueBodyTransformer.v()));
        gopPack.add(new Transform("gop.vgg", VimpExprFolder.v()));
        gopPack.add(new Transform("gop.qf", QuantifierValueTransformer.v()));
        gopPack.add(new Transform("gop.ei", ExceptionInvariantTransformer.v()));
        gopPack.add(new Transform("gop.ict", IndexCheckTransformer.v()));
        gopPack.add(new Transform("gop.nct", NullCheckTransformer.v()));
        gopPack.add(new Transform("gop.cct", CallCheckTransformer.v()));
        gopPack.add(new Transform("gop.gt", GuardTransformer.v()));
        gopPack.add(new Transform("gop.ie", InvariantExpander.v()));
        gopPack.add(new Transform("gop.bpl", new BplEncoder()));

        final String[] startingClasses = Configuration.v().getStartingClasses().toArray(new String[]{});

        soot.Main.main(startingClasses);
    }

}
