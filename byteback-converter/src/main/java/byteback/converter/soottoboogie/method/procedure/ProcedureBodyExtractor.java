package byteback.converter.soottoboogie.method.procedure;

import byteback.analysis.JimpleStmtSwitch;
import byteback.converter.soottoboogie.Convention;
import byteback.converter.soottoboogie.type.TypeAccessExtractor;
import byteback.frontend.boogie.ast.Body;
import byteback.frontend.boogie.ast.Label;
import byteback.frontend.boogie.ast.LabelStatement;
import byteback.frontend.boogie.ast.Statement;
import byteback.frontend.boogie.ast.TypeAccess;
import byteback.frontend.boogie.ast.ValueReference;
import byteback.frontend.boogie.ast.VariableDeclaration;
import soot.Type;
import soot.Unit;

public class ProcedureBodyExtractor extends JimpleStmtSwitch<Body> {

	public class BodyReferenceProvider implements ReferenceProvider {

		private int variableCounter;

		public BodyReferenceProvider() {
			variableCounter = 0;
		}

		public ValueReference get(final Type type) {
			final TypeAccess bTypeAccess = new TypeAccessExtractor().visit(type);
			final ValueReference bReference = Convention.makeValueReference(++variableCounter);
			final VariableDeclaration bDeclaration = bReference.makeVariableDeclaration(bTypeAccess);
			body.addLocalDeclaration(bDeclaration);

			return bReference;
		}

	}

	private final Body body;

	private final ReferenceProvider variableProvider;

	private final LabelCollector labelCollector;

	public ProcedureBodyExtractor() {
		this.body = new Body();
		this.variableProvider = new BodyReferenceProvider();
		this.labelCollector = new LabelCollector();
	}

	public Body visit(final soot.Body body) {
		labelCollector.collect(body);

		return super.visit(body);
	}

	public void addStatement(final Statement statement) {
		body.addStatement(statement);
	}

	public void addLocalDeclaration(final VariableDeclaration declaration) {
		body.addLocalDeclaration(declaration);
	}

	public Body getBody() {
		return body;
	}

	public LabelCollector getLabelCollector() {
		return labelCollector;
	}

	public ReferenceProvider getReferenceProvider() {
		return variableProvider;
	}

	@Override
	public void caseDefault(final Unit unit) {
		if (labelCollector.hasLabel(unit)) {
			final Label label = labelCollector.fetchLabel(unit);
			addStatement(new LabelStatement(label));
		}

		final var extractor = new ProcedureStatementExtractor(this);
		extractor.visit(unit);
	}

	@Override
	public Body result() {
		return body;
	}

}
