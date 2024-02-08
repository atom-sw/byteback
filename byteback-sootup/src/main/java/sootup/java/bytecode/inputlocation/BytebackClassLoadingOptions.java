package sootup.java.bytecode.inputlocation;

import java.util.ArrayList;
import java.util.List;

import sootup.core.inputlocation.ClassLoadingOptions;
import sootup.core.transform.BodyInterceptor;
import sootup.java.bytecode.interceptors.Aggregator;
import sootup.java.bytecode.interceptors.BBLibToSpecificationStmtTransformer;
import sootup.java.bytecode.interceptors.SpecificationStmtAggregator;
import sootup.java.bytecode.interceptors.TypeAssigner;
import sootup.java.bytecode.interceptors.UnusedLocalEliminator;

public enum BytebackClassLoadingOptions implements ClassLoadingOptions {
  Default {

    @Override
    public List<BodyInterceptor> getBodyInterceptors() {
			final List<BodyInterceptor> bytebackInterceptors = new ArrayList<>();
			bytebackInterceptors.add(new TypeAssigner());
			bytebackInterceptors.add(new Aggregator(true));
			bytebackInterceptors.add(new BBLibToSpecificationStmtTransformer());
			bytebackInterceptors.add(new SpecificationStmtAggregator());
			bytebackInterceptors.add(new UnusedLocalEliminator());
      return bytebackInterceptors;
    }

  }

}
