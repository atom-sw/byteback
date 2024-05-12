package byteback.syntax.scene.type.declaration.member.method.body.value;

import java.util.Arrays;
import java.util.List;

import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.util.NumberedString;

public class PreludeRef implements SootMethodRef {

	private final static SootClass PRELUDE_CLASS = new SootClass("Prelude");

	final String name;

	final Type returnType;

	final Type[] parameterTypes;

	public PreludeRef(final String name, final Type returnType, final Type[] argumentTypes) {
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = argumentTypes;
	}

	public String getName() {
		return name;
	}

	public String name() {
		return name;
	}

	public Type getReturnType() {
		return returnType;
	}

	public Type returnType() {
		return returnType;
	}

	public Type getParameterType(final int position) {
		return parameterTypes[position];
	}

	public Type parameterType(final int position) {
		return parameterTypes[position];
	}

	public List<Type> getParameterTypes() {
		return Arrays.asList(parameterTypes);
	}

	public List<Type> parameterTypes() {
		return Arrays.asList(parameterTypes);
	}

	public boolean isStatic() {
		return true;
	}

	public SootMethod tryResolve() {
		return null;
	}

	public SootMethod resolve() {
		throw new UnsupportedOperationException();
	}

	public String getSignature() {
		throw new UnsupportedOperationException();
	}

	public NumberedString getSubSignature() {
		throw new UnsupportedOperationException();
	}

	public SootClass getDeclaringClass() {
		return PRELUDE_CLASS;
	}

	public SootClass declaringClass() {
		return PRELUDE_CLASS;
	}

}
