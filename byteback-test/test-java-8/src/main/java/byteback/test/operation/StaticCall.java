/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.operation;

import byteback.specification.Contract.Behavior;

public class StaticCall {

	@Behavior
	public static int getConstant() {
		return 1;
	}

	@Behavior
	public static int increment(int a) {
		return a + 1;
	}

	public static int proceduralIncrement(int a) {
		return a + 1;
	}

	@Behavior
	public static int main() {
		return increment(getConstant());
	}

	public static int callsPure() {
		return increment(getConstant());
	}

	public static int callsProcedural() {
		return increment(proceduralIncrement(getConstant()));
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
