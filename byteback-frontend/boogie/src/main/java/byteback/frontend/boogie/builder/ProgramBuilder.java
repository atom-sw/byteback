package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class ProgramBuilder {

	private final Program program;

	public ProgramBuilder() {
		this.program = new Program();
	}

	public ProgramBuilder addDeclaration(final Declaration declaration) {
		program.addDeclaration(declaration);

		return this;
	}

	public Program build() {
		return program;
	}

}
