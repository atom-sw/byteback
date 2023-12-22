/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.operation;

import byteback.annotations.Contract.Pure;

public class VirtualCall {

	@Pure
	public VirtualCall getThis() {
		return this;
	}

	@Pure
	public VirtualCall getThat(VirtualCall that) {
		return that.getThis();
	}

	public VirtualCall proceduralGetThis() {
		return this;
	}

	public VirtualCall proceduralGetThat(VirtualCall that) {
		return that.getThis();
	}

	public VirtualCall callsPure() {
		return getThis().getThat(this);
	}

	public VirtualCall callsProcedural() {
		return getThis().getThat(this);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
