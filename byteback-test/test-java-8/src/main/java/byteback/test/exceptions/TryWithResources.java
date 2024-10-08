/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$Resource -o %t.bpl
 */
package byteback.test.exceptions;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Special.*;
import static byteback.specification.Quantifiers.*;

import byteback.specification.Bindings;

public class TryWithResources {

	public static class Resource implements AutoCloseable {
		private boolean closed;

		@Return
		@Ensure("isOpen")
		@Ensure("otherResourcesAreClosed")
		public Resource() {
			closed = false;
		}

		@TwoState
		@Behavior
		public boolean otherResourcesAreClosed() {
			final Resource r = Bindings.reference(Resource.class);

			return forall(r, implies(neq(r, this), eq(old(r.closed), r.closed)));
		}

		@Return
		@Ensure("isClosed")
		@Ensure("otherResourcesAreClosed")
		public void close() {
			closed = true;
		}

		@Behavior
		public boolean isClosed() {
			return closed;
		}

		@Behavior
		public boolean isOpen() {
			return not(closed);
		}

	}

	@Return
	public void emptyTryWithResources() {
		try (Resource resource = new Resource()) {
		}
	}

	@Return
	public void tryWithResourcesClosesResource() {
		Resource r = new Resource();

		try (Resource resource = r) {
			assertion(resource.isOpen());
		}

		assertion(r.isClosed());
	}

	@Return
	public void emptyTryWithResourcesFinally() {
		try (Resource resource = new Resource()) {
			assertion(resource.isOpen());
		} finally {
		}

	}

	@Return
	public void tryWithResourcesFinallyClosesResource() {
		Resource r = new Resource();

		try (Resource resource = r) {
			assertion(resource.isOpen());
		} finally {
			assertion(r.isClosed());
		}

	}

	public void throwingTryWithResourcesClosesResource() {
		Resource r = new Resource();

		try (Resource resource = r) {
			assertion(resource.isOpen());
			throw new RuntimeException();
		} finally {
			assertion(r.isClosed());
		}
	}

	@Return
	public void tryWithResourcesAliases() {
		Resource a = new Resource();
		Resource b = a;
		Resource c = b;
		Resource d = c;
		Resource e = d;
		Resource f = e;
		Resource g = f;
		Resource h = g;

		try (Resource i = h) {
			assertion(i.isOpen());
		} finally {
			assertion(a.isClosed());
			assertion(b.isClosed());
			assertion(c.isClosed());
			assertion(d.isClosed());
			assertion(e.isClosed());
			assertion(f.isClosed());
			assertion(g.isClosed());
			assertion(h.isClosed());
		}
	}

	@Return
	public void nested2TryWithResourcesOnSingleResource() {
		Resource r = new Resource();

		try (Resource r1 = r) {
			try (Resource r2 = r1) {
			} finally {
				assertion(r.isClosed());
			}
		} finally {
			assertion(r.isClosed());
		}
	}

	@Return
	public void nested3TryWithResourcesOnSingleResource() {
		Resource r = new Resource();

		try (Resource r1 = r) {
			try (Resource r2 = r1) {
				try (Resource r3 = r2) {
				} finally {
					assertion(r.isClosed());
				}
			} finally {
				assertion(r.isClosed());
			}
		} finally {
			assertion(r.isClosed());
		}
	}

	@Return
	public void nested2TryWithResourcesOn2Resources() {
		Resource a = new Resource();
		Resource b = new Resource();

		try (Resource r1 = a) {
			try (Resource r2 = b) {
			} finally {
				assertion(b.isClosed());
			}
		} finally {
			assertion(a.isClosed());
		}
	}

	@Return
	public void nested3TryWithResourcesOn3Resources() {
		Resource a = new Resource();
		Resource b = new Resource();
		Resource c = new Resource();

		try (Resource r1 = a) {
			try (Resource r2 = b) {
				try (Resource r3 = c) {
				} finally {
					assertion(c.isClosed());
				}
			} finally {
				assertion(b.isClosed());
				assertion(c.isClosed());
			}
		} finally {
			assertion(a.isClosed());
			assertion(b.isClosed());
			assertion(c.isClosed());
		}
	}

	@Return
	public void tryWithResourcesOn2Resources() {
		Resource a = new Resource();
		Resource b = new Resource();

		try (Resource r1 = a;
				 Resource r2 = b) {
			assertion(a.isOpen());
			assertion(b.isOpen());
		} finally {
			assertion(a.isClosed());
			assertion(b.isClosed());
		}
	}

	@Return
	public void tryWithResourcesOn3Resources() {
		Resource a = new Resource();
		Resource b = new Resource();
		Resource c = new Resource();

		try (Resource r1 = a;
				 Resource r2 = b;
				 Resource r3 = c) {
			assertion(a.isOpen());
			assertion(b.isOpen());
			assertion(c.isOpen());
		} finally {
			assertion(a.isClosed());
			assertion(b.isClosed());
			assertion(c.isClosed());
		}
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 15 verified, 0 errors
 */
