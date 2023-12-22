/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.casting;

public class RealToInt {

	public int explicit() {
		float f = 3.14f;
		double d = 3.14d;
		int i = (int)f;
		i = (int)d;

		return i;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
