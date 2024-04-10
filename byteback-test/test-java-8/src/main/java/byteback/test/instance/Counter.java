/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

import byteback.specification.Contract.Return;
import static byteback.specification.Operator.*;
import static byteback.specification.Contract.*;
import static byteback.specification.Special.*;

public class Counter {

	public static void main() {
		final Counter counter = new Counter();
		counter.increment();
		counter.countTo10();
		counter.countTo10Indirectly();
	}

	int count;

	@Behavior
	public boolean increments_count_by_1() {
    return eq(count, old(count) + 1);
	}

	@Return
	public Counter() {
		this.count = 0;
	}

  @Ensure("increments_count_by_1")
	@Return
	public void increment() {
		count++;
	}

	@Return
	public void countTo10() {
		for (int i = 0; i < 10; ++i) {
			invariant(lte(0, i) & lte(i, 10));
			count++;
		}
	}

	@Return
	public void countTo10Indirectly() {
		for (int i = 0; i < 10; ++i) {
			invariant(lte(0, i) & lte(i, 10));
      invariant(eq(count, old(count) + i));
			increment();
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
