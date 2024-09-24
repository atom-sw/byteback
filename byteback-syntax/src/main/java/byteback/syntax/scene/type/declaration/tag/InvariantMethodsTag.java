package byteback.syntax.scene.type.declaration.tag;

import soot.SootMethod;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvariantMethodsTag implements Tag {

	public static final String NAME = "InvariantMethodsTag";

	private final List<SootMethod> invariantMethods;

	public InvariantMethodsTag() {
		this.invariantMethods = new ArrayList<>();
	}

	public List<SootMethod> getInvariantMethods() {
		return Collections.unmodifiableList(invariantMethods);
	}

	public void addInvariantMethod(final SootMethod invariantMethod) {
		invariantMethods.add(invariantMethod);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		return new byte[0];
	}

}
