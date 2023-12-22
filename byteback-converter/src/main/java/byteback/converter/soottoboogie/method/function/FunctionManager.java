package byteback.converter.soottoboogie.method.function;

import byteback.frontend.boogie.ast.FunctionDeclaration;
import byteback.util.Lazy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import soot.SootMethod;

public class FunctionManager {

	private static final Lazy<FunctionManager> instance = Lazy.from(FunctionManager::new);

	private final Map<SootMethod, FunctionDeclaration> cache;

	public static FunctionManager v() {
		return instance.get();
	}

	private FunctionManager() {
		this.cache = new ConcurrentHashMap<>();
	}

	public FunctionDeclaration convert(final SootMethod method) {
		return cache.computeIfAbsent(method, FunctionConverter.v()::convert);
	}

}
