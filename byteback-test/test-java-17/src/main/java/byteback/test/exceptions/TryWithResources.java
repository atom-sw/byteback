/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.exceptions;


import static byteback.annotations.Contract.*;

// Java 9 Feature:
//
// If the resource is referenced by a final variable, then there is no
// need to declare a new variable in the declaration block.
public class TryWithResources {

	public class Resource implements AutoCloseable {
		private boolean closed;

		@Return
		public Resource() {
			closed = false;
		}

		@Ensure("isClosed")
		public void close() {
			closed = true;
		}

		@Pure
		@Predicate
		public boolean isClosed() {
			return closed;
		}

	}

	public void tryWithResourcesOnExistingResourceClosesResource() {
		Resource resource = new Resource();

		try (resource) {
		}

		assertion(resource.isClosed());
	}

	public void tryWithResourcesFinallyOnExistingResourceClosesResource() {
		Resource resource = new Resource();

		try (resource) {
		} finally {
			assertion(resource.isClosed());
		}
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
