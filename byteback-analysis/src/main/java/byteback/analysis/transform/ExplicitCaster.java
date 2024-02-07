package byteback.analysis.transform;

import java.util.List;
import sootup.core.graph.StmtGraph;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.core.transform.BodyInterceptor;
import sootup.core.views.View;

public class ExplicitCaster implements BodyInterceptor {

	public void interceptBody(Body.BodyBuilder builder, View<?> view) {
		final StmtGraph<?> graph = builder.getStmtGraph();
		final List<Stmt> stmts = graph.getStmts();
	}

}
