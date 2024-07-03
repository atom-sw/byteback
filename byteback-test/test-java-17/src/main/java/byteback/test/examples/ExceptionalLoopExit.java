/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class} -o %t.bpl
 */

package byteback.test.examples;

import static byteback.specification.Contract.assertion;
import static byteback.specification.Operators.*;

import byteback.specification.Contract.Return;

public class ExceptionalLoopExit {

	@Return
	public void m(int i) {
		try {
			while (i != 10) {
				i++;
				if (i == 0)
					throw new Exception();
				if (i == 7)
					break;
			}
		} catch (Exception e) {
			assertion(eq(i, 0));
			return;
		}

		assertion(eq(i, 10) | eq(i, 7));
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified
 */
