/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

import static byteback.specification.Operators.lte;
import static byteback.specification.Operators.gt;
import static byteback.specification.Operators.implies;
import static byteback.specification.Operators.eq;
import static byteback.specification.Special.isVoid;
import static byteback.specification.Contract.thrown;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Exceptional;

public class NegativeBox {
	public int x = 0;

	@Ensure("x_eq_y")
	@Ensure("x_pos")
	@Ensure("y_neg")
	public void m(int y) {
		x = y;
		if (x > 0) throw new RuntimeException();
	}

	@Exceptional @Behavior public boolean y_neg(int y)
	{ return implies(lte(y, 0), isVoid(thrown())); }

		@Exceptional @Behavior public boolean x_pos(int y)
	{ return implies(gt(x, 0), thrown() instanceof RuntimeException); }

		@Behavior public boolean x_eq_y(int y)
	{ return eq(x, y); }

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
