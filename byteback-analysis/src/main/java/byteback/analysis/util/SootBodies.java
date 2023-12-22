package byteback.analysis.util;

import byteback.analysis.SwappableUnitBox;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import soot.util.HashChain;

public class SootBodies {

	public static Collection<Local> getLocals(final Body body) {
		final Collection<Local> parameterLocals = getParameterLocals(body);

		return body.getLocals().stream().filter((local) -> !parameterLocals.contains(local))
				.collect(Collectors.toList());
	}

	public static Collection<Local> getParameterLocals(final Body body) {
		final List<Local> locals = new ArrayList<>(body.getParameterLocals());
		getThisLocal(body).ifPresent((thisLocal) -> locals.add(0, thisLocal));

		return locals;
	}

	public static Optional<Local> getThisLocal(final Body body) {
		try {
			return Optional.of(body.getThisLocal());
		} catch (final RuntimeException exception) {
			return Optional.empty();
		}
	}

	public static Collection<Loop> getLoops(final Body body) {
		final LoopFinder loopFinder = new LoopFinder();
		loopFinder.transform(body);

		return loopFinder.getLoops(body);
	}

	public static BlockGraph getBlockGraph(final Body body) {
		return new BriefBlockGraph(body);
	}

	public static UnitGraph getUnitGraph(final Body body) {
		return new BriefUnitGraph(body);
	}

	public static Chain<UnitBox> getUnitBoxes(final Body body) {
		final var unitBoxes = new HashChain<UnitBox>();

		for (final Unit unit : body.getUnits()) {
			unitBoxes.add(new SwappableUnitBox(unit, body));
		}

		return unitBoxes;
	}

	public static class ValidationException extends RuntimeException {

		public ValidationException(final String message) {
			super(message);
		}

		public ValidationException(final Exception exception) {
			super(exception);
		}

	}

	public static void validateCalls(final Body body) {
		for (final ValueBox valueBox : body.getUseAndDefBoxes()) {
			final Value value = valueBox.getValue();

			if (value instanceof InvokeExpr invokeExpr) {
				try {
					invokeExpr.getMethod();
				} catch (Exception e) {
					throw new ValidationException(e);
				}
			}
		}
	}

	public static boolean isDynamic(final Body body) {
		for (final ValueBox valueBox : body.getUseAndDefBoxes()) {
			final Value value = valueBox.getValue();

			if (value instanceof DynamicInvokeExpr) {
				return true;
			}
		}

		return false;
	}

}
