/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.exceptions;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import static byteback.annotations.Special.*;
import static byteback.annotations.Quantifier.*;

import byteback.annotations.Binding;

public class TryWithResources {

	public static class Resource implements AutoCloseable {
		private boolean closed;

		@Lemma
		@Return
		@Ensure("isOpen")
		@Ensure("otherResourcesAreClosed")
		public Resource() {
			closed = false;
		}

		@Predicate
		public boolean otherResourcesAreClosed() {
			final Resource r = (Resource) Binding.reference();

			return forall(r, implies(neq(r, this), eq(old(r.closed), r.closed)));
		}

		@Return
		@Ensure("isClosed")
		@Ensure("otherResourcesAreClosed")
		public void close() {
			closed = true;
		}

		@Pure
		@Predicate
		public boolean isClosed() {
			return closed;
		}

		@Pure
		@Predicate
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
 * CHECK: Boogie program verifier finished with 14 verified, 0 errors
 */
