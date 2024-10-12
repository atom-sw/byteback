package byteback.syntax.scene.type.declaration.member.method.transformer;

import java.util.ArrayList;
import java.util.List;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterLocalFinder;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterRefFinder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.tag.PostassumptionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostassumptionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreassumptionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreassumptionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.tag.InvariantMethodsTagAccessor;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;

public class InvariantInstantiator extends MethodTransformer {

	private static final Lazy<InvariantInstantiator> INSTANCE = Lazy.from(InvariantInstantiator::new);

	public static InvariantInstantiator v() {
		return INSTANCE.get();
	}

	private InvariantInstantiator() {
	}

	private List<Value> instantiateInvariants(final Value value) {
		final var invariantValues = new ArrayList<Value>();

		if (value.getType() instanceof final RefType refType) {
			if (refType.hasSootClass()) {
				final SootClass sootClass = refType.getSootClass();
				InvariantMethodsTagAccessor.v().get(sootClass).ifPresent((invariantMethodsTag) -> {
					final List<SootMethod> invariantMethods = invariantMethodsTag.getInvariantMethods();

					for (final SootMethod invariantMethod : invariantMethods) {
						invariantValues.add(Vimp.v().newCallExpr(invariantMethod.makeRef(), Vimp.v().nest(value)));
					}
				});
			}
		}

		return invariantValues;
	}

	@Override
	public void transformMethod(final SootMethod sootMethod) {
		final PreassumptionsTag preassumptionsTag = PreassumptionsTagAccessor.v().putIfAbsent(sootMethod,
				PreassumptionsTag::new);
		final PostassumptionsTag postassumptionsTag = PostassumptionsTagAccessor.v().putIfAbsent(sootMethod,
				PostassumptionsTag::new);

		for (int i = 0; i < sootMethod.getParameterCount(); ++i) {
			final ParameterRef parameterRef = Jimple.v().newParameterRef(sootMethod.getParameterType(i), i);
			final List<Value> parameterInvariantValues = instantiateInvariants(parameterRef);
			preassumptionsTag.addConditions(parameterInvariantValues);
		}

		final List<Value> returnInvariantValues = instantiateInvariants(
				Vimp.v().newReturnRef(sootMethod.getReturnType()));
		postassumptionsTag.addConditions(returnInvariantValues);
	}

}
