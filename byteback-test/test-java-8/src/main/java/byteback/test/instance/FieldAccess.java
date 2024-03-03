/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

import byteback.specification.Contract.Function;

public class FieldAccess {

	public static int staticField;

	public int field;

	@Function
	public int staticFieldReference() {
		return staticField;
	}

	@Function
	public int staticFieldSum() {
		return staticField + 2;
	}

	@Function
	public int fieldReference() {
		return this.field;
	}

	@Function
	public int fieldSum() {
		return this.field + 2;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 1 verified, 0 errors
 */
