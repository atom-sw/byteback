package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.RefType;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author paganma
 */
public class InstanceChecksTag implements Tag {

	public static final String NAME = "InstanceChecksTag";

	private final List<RefType> checkedTypes;

	public InstanceChecksTag(final List<RefType> checkedTypes) {
		this.checkedTypes = checkedTypes.stream()
			.collect(Collectors.toList());
	}

	public InstanceChecksTag() {
		this(new ArrayList<>());
	}

	public List<RefType> getCheckedTypes() {
		return checkedTypes;
	}

	public void addCheckedTypes(final RefType checkedType) {
		checkedTypes.add(checkedType);
	}

	@Override
	public byte[] getValue() {
		return new byte[0];
	}

	public String getName() {
		return NAME;
	}

}
