package byteback.converter.soottoboogie.method.function;

import byteback.frontend.boogie.ast.FunctionDeclaration;
import byteback.common.Lazy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import soot.SootMethod;

public class FunctionManager {

    private static final Lazy<FunctionManager> instance = Lazy.from(FunctionManager::new);

    private final Map<SootMethod, FunctionDeclaration> cache;

    private FunctionManager() {
        this.cache = new ConcurrentHashMap<>();
    }

    public static FunctionManager v() {
        return instance.get();
    }

    public FunctionDeclaration convert(final SootMethod method) {
        return cache.computeIfAbsent(method, FunctionConverter.v()::convert);
    }

}
