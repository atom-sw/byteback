package byteback.analysis;

import soot.Body;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.jimple.StmtBody;

public class VimpBody extends StmtBody {

	VimpBody(final SootMethod method) {
		this(method.getActiveBody());
	}

	@Override
	public Body clone() {
		Body b = Vimp.v().newBody(getMethodUnsafe());
		b.importBodyContentsFrom(this);

		return b;
	}

	VimpBody(final Body body) {
		super(body.getMethod());
	}

	public final void construct(final JimpleBody body) {
	}

}
