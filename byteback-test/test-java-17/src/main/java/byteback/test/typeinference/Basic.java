/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.typeinference;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;

public class Basic {

	public static void inferPrimitives() {
		var a = 1;
		var b = 1L;
		var c = 1.0;
		var d = 1.0F;
		var e = 0b1;

		assertion(eq(a, 1));
		assertion(eq(b, 1L));
		assertion(eq(c, 1.0));
		assertion(eq(d, 1.0F));
		assertion(eq(e, 0b1));
	}

	public static void inferReftypes() {
		var a = new Object();
		var b = new int[10];

		assertion(neq(a, null));
		assertion(neq(b, null));
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
