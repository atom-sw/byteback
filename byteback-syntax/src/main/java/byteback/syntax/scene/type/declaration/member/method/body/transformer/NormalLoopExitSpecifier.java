package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.AssertStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.tag.ThrowTargetTagMarker;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.UnitConstant;
import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.BriefUnitGraph;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Specifies non-exceptional loop exits as such, by adding
 * `assume @caughtexception == @void` at every non-exceptional
 * loop exit.
 *
 * @author paganma
 */
public class NormalLoopExitSpecifier extends BodyTransformer {

	private static final Lazy<NormalLoopExitSpecifier> INSTANCE = Lazy.from(NormalLoopExitSpecifier::new);

	private NormalLoopExitSpecifier() {
	}

	public static NormalLoopExitSpecifier v() {
		return INSTANCE.get();
	}

	@Override
	public void transformBody(final BodyContext bodyContext) {
		final Body body = bodyContext.getBody();
		final PatchingChain<Unit> units = body.getUnits();
		final var loopFinder = new LoopFinder();
		final var unitGraph = new BriefUnitGraph(body);
		final Set<Loop> loops = loopFinder.getLoops(unitGraph);

		for (final Loop loop : loops) {
			final ThrownRef exceptionRef = Vimp.v().newThrownRef();
			final UnitConstant unitConstant = UnitConstant.v();
			final Value behaviorValue = Jimple.v().newEqExpr(
					Vimp.v().nest(exceptionRef),
					unitConstant);
			final Supplier<AssertStmt> assertStmtSupplier = () -> Vimp.v().newAssertStmt(Vimp.v().nest(behaviorValue));

			units.insertBefore(assertStmtSupplier.get(), loop.getHead());
			units.insertBefore(assertStmtSupplier.get(), loop.getBackJumpStmt());

			for (final Unit loopExit : loop.getLoopExits()) {
				final Unit target;

				// The `getTarget()` method for IfStmt is different to that of GotoStmt, hence
				// the two if-matches.
				if (loopExit instanceof final IfStmt ifExit) {
					target = ifExit.getTarget();
				} else if (loopExit instanceof final GotoStmt gotoStmt) {
					target = gotoStmt.getTarget();
				} else {
					continue;
				}

				// Insert the assumption only if exit target is not an exceptional target
				// (as tagged by GuardTransformer).
				if (ThrowTargetTagMarker.v().hasTag(target)) {
					ThrowTargetTagMarker.v().flag(target);
					units.insertBefore(assertStmtSupplier.get(), loopExit);
					final AssertStmt targetAssertStmt = assertStmtSupplier.get();
					units.insertBefore(targetAssertStmt, target);
					target.redirectJumpsToThisTo(targetAssertStmt);
				}
			}
		}
	}

}
