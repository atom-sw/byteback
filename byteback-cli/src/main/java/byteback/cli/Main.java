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
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(Configuration.v().formatClassPaths());
        Options.v().set_no_bodies_for_excluded(true);

        Options.v().setPhaseOption("jb", "use-original-names:true");
        
        Options.v().setPhaseOption("gc.cha", "apponly:true");

        Options.v().setPhaseOption("jtp", "enabled:true");
        Options.v().setPhaseOption("jtp.ict", "enabled:true");
        Options.v().setPhaseOption("jtp.nct", "enabled:true");

        final Pack jtpPack = PackManager.v().getPack("jtp");

        jtpPack.add(new Transform("jtp.ii", InvokeIgnorer.v()));
        jtpPack.add(new Transform("jtp.d2s", DynamicToStaticTransformer.v()));
        jtpPack.add(new Transform("jtp.vut", VimpUnitBodyTransformer.v()));
        jtpPack.add(new Transform("jtp.vvt", VimpValueBodyTransformer.v()));
        jtpPack.add(new Transform("jtp.vgg", VimpExprFolder.v()));
        jtpPack.add(new Transform("jtp.qf", QuantifierValueTransformer.v()));
        jtpPack.add(new Transform("jtp.ei", ExceptionInvariantTransformer.v()));
        jtpPack.add(new Transform("jtp.ict", IndexCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.nct", NullCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.cct", CallCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.gt", GuardTransformer.v()));
        jtpPack.add(new Transform("jtp.ie", InvariantExpander.v()));
        jtpPack.add(new Transform("jtp.bpl", new BplEncoder()));

        final String[] startingClasses = Configuration.v().getStartingClasses().toArray(new String[]{});

        soot.Main.main(startingClasses);
    }

}
