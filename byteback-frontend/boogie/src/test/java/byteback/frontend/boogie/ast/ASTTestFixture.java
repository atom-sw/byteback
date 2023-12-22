package byteback.frontend.boogie.ast;

import beaver.Scanner;
import byteback.frontend.boogie.ResourcesUtil;
import byteback.frontend.boogie.parser.BoogieParser;
import byteback.frontend.boogie.scanner.BoogieLexer;
import java.util.Collection;

public class ASTTestFixture {

	public Program getProgram(String programName) {
		try {
			final BoogieParser parser = new BoogieParser();
			final Scanner scanner = new BoogieLexer(ResourcesUtil.getBoogieReader(programName));
			return (Program) parser.parse(scanner);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Function getFunction(final String programName, final String functionName) {
		final Program program = getProgram(programName);
		final Function function = program.lookupFunction(functionName)
				.orElseThrow(() -> new RuntimeException("No such function: " + functionName));

		return function;
	}

	public Procedure getProcedure(final String programName, final String procedureName) {
		final Program program = getProgram(programName);
		final Procedure procedure = program.lookupProcedure(procedureName)
				.orElseThrow(() -> new RuntimeException("No such procedure: " + procedureName));

		return procedure;
	}

	public Collection<Implementation> getImplementations(final String programName, final String implementationName) {
		final Program program = getProgram(programName);
		final Collection<Implementation> implementations = program.lookupImplementations(implementationName);

		return implementations;
	}

	public Variable getVariable(final String programName, final String variableName) {
		final Program program = getProgram(programName);
		final Variable variable = program.lookupLocalVariable(variableName)
				.orElseThrow(() -> new RuntimeException("No such variable: " + variableName));

		return variable;
	}

	public TypeDefinition getTypeDefinition(final String programName, final String typeName) {
		final Program program = getProgram(programName);
		final TypeDefinition typeDefinition = program.lookupTypeDefinition(typeName)
				.orElseThrow(() -> new RuntimeException("No such type: " + typeName));

		return typeDefinition;
	}

}
