/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.casting;

public class IntToReal {

	public static double implicit() {
		int a = 1;
		float f = a + 3.14f;
		double d = f + 3.14d;

		return d;
	}

	public static double explicit() {
		int a = 1;
		double f;

		f = a;

		if (a == 1) {
			f = a;
			double d = f;
			f = f + d;
		}

		return f;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
