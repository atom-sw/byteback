package byteback.encoder.boogie;

import byteback.analysis.Scheduler;
import byteback.encoder.Context;
import java.io.PrintWriter;
import sootup.core.views.View;

public class BplContext extends Context {

	public BplContext(final View<?> view, final Scheduler scheduler, final PrintWriter writer) {
		super(view, scheduler, writer);
	}

}
