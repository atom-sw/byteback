package byteback.analysis.body.common;

import java.util.*;

import byteback.common.function.Lazy;
import soot.Body;
import soot.Local;
import soot.util.Chain;
import soot.util.HashChain;

/**
 * Additional functions that operate on Soot bodies.
 * @author paganma
 */
public class Bodies {

	private static final Lazy<Bodies> instance = Lazy.from(Bodies::new);

	private Bodies() {
	}

	public static Bodies v() {
		return instance.get();
	}

	public Chain<Local> getBodyLocals(final Body body) {
		final var bodyLocals = new HashChain<Local>();
		final var parameterLocals = getParameterLocals(body);

		for (final Local local : body.getLocals()) {
			if (!parameterLocals.contains(local)) {
				bodyLocals.add(local);
			}
		}

		return bodyLocals;
	}

	public Chain<Local> getParameterLocals(final Body body) {
		final var locals = new HashChain<Local>();
		locals.addAll(body.getParameterLocals());
		getThisLocal(body).ifPresent(locals::addFirst);

		return locals;
	}

	public Optional<Local> getThisLocal(final Body body) {
		try {
			return Optional.of(body.getThisLocal());
		} catch (final RuntimeException exception) {
			return Optional.empty();
		}
	}

}
