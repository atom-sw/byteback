package byteback.analysis.tags;

import soot.tagkit.Tag;

public class PositionTag implements Tag {

	public final String file;

	public final int lineNumber;

	public PositionTag(final String file, final int lineNumber) {
		this.file = file;
		this.lineNumber = lineNumber;
	}

	@Override
	public String getName() {
		return "PositionTag";
	}

	@Override
	public byte[] getValue() {
		return (file + ":" + lineNumber).getBytes();
	}

}
