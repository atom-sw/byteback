package byteback.analysis.body.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import soot.Body;
import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class SootBodies {

	public static Collection<Local> getLocals(final Body body) {
		final Collection<Local> parameterLocals = getParameterLocals(body);

		return body.getLocals().stream()
				.filter((local) -> !parameterLocals.contains(local))
				.collect(Collectors.toList());
	}

	public static Collection<Local> getParameterLocals(final Body body) {
		final List<Local> locals = new ArrayList<>(body.getParameterLocals());
		getThisLocal(body)
				.ifPresent((thisLocal) ->
						locals.add(0, thisLocal));

		return locals;
	}

	public static Optional<Local> getThisLocal(final Body body) {
		try {
			return Optional.of(body.getThisLocal());
		} catch (final RuntimeException exception) {
			return Optional.empty();
		}
	}

	public static BlockGraph makeBlockGraph(final Body body) {
		return new BriefBlockGraph(body);
	}

	public static UnitGraph makeUnitGraph(final Body body) {
		return new BriefUnitGraph(body);
	}

}
