package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class PreludeTag implements Tag {

	public static String NAME = "PreludeTag";

	private final String definitionSymbol;

	public PreludeTag(final String definitionSymbol) {
		this.definitionSymbol = definitionSymbol;
	}

	public String getDefinitionSymbol() {
		return definitionSymbol;
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
