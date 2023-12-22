/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

import byteback.annotations.Contract.Pure;

public class FieldAccess {

	public static int staticField;

	public int field;

	@Pure
	public int staticFieldReference() {
		return staticField;
	}

	@Pure
	public int staticFieldSum() {
		return staticField + 2;
	}

	@Pure
	public int fieldReference() {
		return this.field;
	}

	@Pure
	public int fieldSum() {
		return this.field + 2;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 1 verified, 0 errors
 */
