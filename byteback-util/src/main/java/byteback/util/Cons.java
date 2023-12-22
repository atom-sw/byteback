package byteback.util;

public class Cons<T, D> {

	public final T car;

	public final D cdr;

	public Cons(final T car, final D cdr) {
		this.car = car;
		this.cdr = cdr;
	}

	@Override
	public boolean equals(final Object object) {
		return (object instanceof final Cons<?, ?> cons) && cons.car.equals(this.car) && cons.cdr.equals(this.cdr);
	}

	@Override
	public String toString() {
		return "(" + car + " " + cdr + ")";
	}

}
