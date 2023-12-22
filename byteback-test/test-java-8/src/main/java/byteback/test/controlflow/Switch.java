/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.controlflow;

public class Switch {

	public static int intSwitch(final int a) {
		int b;

		switch (a) {
			case 1 :
				b = 1;
			case 2 :
				b = 2;
				break;
			default :
				b = 0;
		}

		return b;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
