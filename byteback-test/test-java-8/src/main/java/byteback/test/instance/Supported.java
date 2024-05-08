/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c byteback.test.instance.Unit -o %t.bpl
 */
package byteback.test.instance;

public class Supported {

	final Unit support;

	public Supported() {
		this.support = new Unit();
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
