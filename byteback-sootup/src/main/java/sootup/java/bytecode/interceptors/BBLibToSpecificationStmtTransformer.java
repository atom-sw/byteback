package sootup.java.bytecode.interceptors;

import java.util.List;

import sootup.core.graph.MutableStmtGraph;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.stmt.JInvokeStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.jimple.expr.byteback.JLogicExpr;
import sootup.core.jimple.stmt.byteback.JAssertStmt;
import sootup.core.jimple.stmt.byteback.JInvariantStmt;
import sootup.core.model.Body;
import sootup.core.signatures.MethodSignature;
import sootup.core.transform.BodyInterceptor;
import sootup.core.types.ClassType;
import sootup.core.views.View;
import sootup.java.bytecode.util.BytebackNamespace;

import javax.annotation.Nonnull;

public class BBLibToSpecificationStmtTransformer implements BodyInterceptor {

	public void interceptBody(@Nonnull final Body.BodyBuilder builder, @Nonnull final View<?> view) {
		final MutableStmtGraph graph = builder.getStmtGraph();
		final List<Stmt> stmts = graph.getStmts();
		for (final Stmt stmt : stmts) {
			if (stmt instanceof JInvokeStmt invokeStmt) {
				final AbstractInvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
				final MethodSignature methodSignature = invokeExpr.getMethodSignature();
				final ClassType classType = methodSignature.getDeclClassType();
				final String className = classType.getFullyQualifiedName();
				if (className.equals(BytebackNamespace.CONTRACT_CLASS_NAME)) {
					final String methodName = methodSignature.getName();
					final Stmt newStmt;
					final JLogicExpr<?> argument = new JLogicExpr<>(invokeExpr.getArg(0));
					final StmtPositionInfo stmtPositionInfo = invokeStmt.getPositionInfo();
					if (methodName.equals(BytebackNamespace.INVARIANT_METHOD_NAME)) {
						newStmt = new JInvariantStmt(argument, stmtPositionInfo);
					} else if (methodName.equals(BytebackNamespace.ASSERTION_METHOD_NAME)) {
						newStmt = new JAssertStmt(argument, stmtPositionInfo);
					} else {
						throw new IllegalStateException("Unknown specification method: " + methodName);
					}
					graph.replaceNode(stmt, newStmt);
				}
			}
		}
	}

}
