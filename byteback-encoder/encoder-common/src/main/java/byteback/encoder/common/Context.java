package byteback.encoder.common;

import java.io.PrintWriter;
import sootup.core.views.View;

public abstract class Context {

	private final View<?> view;

	private final Scheduler scheduler;

	private final PrintWriter writer;

	public Context(final View<?> view, final Scheduler scheduler, final PrintWriter writer) {
		this.scheduler = scheduler;
		this.view = view;
		this.writer = writer;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public View<?> getView() {
		return view;
	}

	public PrintWriter getWriter() {
		return writer;
	}

}
