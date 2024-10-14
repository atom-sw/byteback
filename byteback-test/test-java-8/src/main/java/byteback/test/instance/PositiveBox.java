/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

import static byteback.specification.Operators.gte;
import static byteback.specification.Operators.eq;
import static byteback.specification.Special.old;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Return;
import byteback.specification.Contract.TwoState;

@Invariant("content_is_positive")
public class PositiveBox {

	public int content;

	@Behavior
	public boolean content_is_positive() {
		return gte(content, 0);
	}

	public PositiveBox() {
		this.content = 1;
	}

	@Behavior
	public boolean content_is_positive(final int content) {
		return gte(content, 0);
	}

	@Return
	@Require("content_is_positive")
	public void setContent(int content) {
		this.content = content;
	}

	@Return
	public int getContent() {
		return content;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
