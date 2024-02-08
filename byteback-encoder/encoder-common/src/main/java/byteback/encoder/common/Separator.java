package byteback.encoder.common;

public class Separator {

	private volatile boolean separating;

	private final String separatorString;

	public Separator(final String separatorString) {
		this.separatorString = separatorString;
		this.separating = false;
	}

	public boolean isSeparating() {
		return separating;
	}

	public void setSeparating() {
		separating = true;
	}

	@Override
	public String toString() {
		return separatorString;
	}

}
