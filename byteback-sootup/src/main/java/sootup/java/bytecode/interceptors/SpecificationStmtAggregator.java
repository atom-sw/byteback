package sootup.java.bytecode.interceptors;

import java.util.List;

import sootup.core.graph.BasicBlock;
import sootup.core.graph.MutableStmtGraph;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.expr.AbstractInstanceInvokeExpr;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.expr.JStaticInvokeExpr;
import sootup.core.jimple.common.stmt.AbstractDefinitionStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.jimple.expr.byteback.JLogicExpr;
import sootup.core.jimple.stmt.byteback.SpecificationStmt;
import sootup.core.model.Body;
import sootup.core.transform.BodyInterceptor;
import sootup.core.views.View;
import sootup.java.core.JavaSootMethod;

import javax.annotation.Nonnull;

public class SpecificationStmtAggregator implements BodyInterceptor {

	public JLogicExpr<?> aggregateAt(final JLogicExpr<?> startExpr, final List<Stmt> stmts) {
		final Value wrappedExpr = startExpr.getInner();

		if (wrappedExpr instanceof Local local) {
			final List<AbstractDefinitionStmt<Local, Value>> localDefs = local.getDefsOfLocal(stmts);
			if (localDefs.size() == 1) {
				final AbstractDefinitionStmt<Local, Value> localDef = localDefs.get(0);
				final Value substitute = localDef.getRightOp();
				if (substitute instanceof AbstractInvokeExpr invokeExpr) {
					if (invokeExpr instanceof AbstractInstanceInvokeExpr instanceInvokeExpr) {
						System.out.println(instanceInvokeExpr);
					} else if (invokeExpr instanceof JStaticInvokeExpr staticInvokeExpr) {
						System.out.println(staticInvokeExpr);
					}
				}
			}
		}

		return startExpr;
	}

	public void interceptBody(@Nonnull final Body.BodyBuilder builder, @Nonnull final View<?> view) {
		final MutableStmtGraph graph = builder.getStmtGraph();
		for (final BasicBlock<?> block : graph.getBlocks()) {
			final List<Stmt> stmts = block.getStmts();
			for (final Stmt stmt : stmts) {
				if (stmt instanceof SpecificationStmt specificationStmt) {
					final JLogicExpr<?> specification = aggregateAt(specificationStmt.getCondition(), stmts);
					graph.replaceNode(specificationStmt, specificationStmt.withCondition(specification));
				}
			}
		}
	}

}
