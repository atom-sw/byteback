package byteback.analysis.transformer;

import static org.junit.Assert.fail;

import byteback.analysis.vimp.LogicStmt;
import soot.Unit;

public class UnitTransformerFixture {

	public static void assertLogicUnitEquiv(final Unit a, final Unit b) {
		if (a instanceof LogicStmt al) {
			if (b instanceof LogicStmt bl) {
				if (al.getClass() != bl.getClass()) {
					fail("Comparing different types of logic statements " + a.getClass() + " and " + b.getClass());
				}
				if (!al.getCondition().equivTo(bl.getCondition())) {
					fail("Conditions " + al.getCondition() + " and " + bl.getCondition() + " are not equivalent");
				}
			} else {
				fail("Second argument must be a logic statement");
			}
		} else {
			fail("First argument must be a logic statement");
		}
	}

}
