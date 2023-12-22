/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.switchexpression;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;

public class Basic {

	public static int state = 1;

	public static void switchInteger() {
		state = 1;
		int a = switch(state) { case 1 -> 1; case 2 -> 2; default -> 3; };

		assertion(eq(a, 1));
	}

	public static void switchYieldInteger() {
		state = 1;
		int a = switch(state) { case 1 -> { yield(1); } case 2 -> { yield(2); } default -> { yield(3); } };

		assertion(eq(a, 1));
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
