package byteback.cli;

import byteback.analysis.body.grimp.transformer.VimpUnitBodyTransformer;
import byteback.analysis.body.grimp.transformer.VimpValueBodyTransformer;
import byteback.analysis.body.vimp.transformer.VimpExprFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Pack;
import soot.PackManager;
import soot.Transform;
import soot.options.Options;

public class Main {

    public static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_none);

        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("gc.cha", "apponly:true");

        final Pack japPack = PackManager.v().getPack("jap");

        japPack.add(new Transform("jap.vut", VimpUnitBodyTransformer.v()));
        japPack.add(new Transform("jap.vvt", VimpValueBodyTransformer.v()));
        japPack.add(new Transform("jap.vgg", VimpExprFolder.v()));

        soot.Main.main(args);
    }

}
