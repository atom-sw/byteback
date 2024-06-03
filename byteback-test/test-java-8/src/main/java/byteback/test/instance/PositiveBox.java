/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

import static byteback.specification.Operators.gte;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Require;

@Invariant("content_is_positive")
public class PositiveBox {

	public int content;

	@Behavior
	public boolean content_is_positive() {
		return gte(content, 0);
	}

	@Behavior
	public boolean content_is_positive(final int content) {
		return gte(content, 0);
	}

	@Require("content_is_positive")
	public void setContent(int content) {
		this.content = content;
	}

	public int getContent() {
		return content;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
