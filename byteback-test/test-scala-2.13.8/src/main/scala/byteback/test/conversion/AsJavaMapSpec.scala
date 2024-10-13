package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.JavaConverters$AsJava")
abstract class AsJavaMapSpec {

  @Behavior
  def returns_mutable[K, V](r: java.util.Map[K, V]): Boolean = {
    return Ghost.of(classOf[MapSpec[K, V]], r).is_mutable();
  }

  @Ensure("returns_mutable")
  @Return
  def asJava[K, V](): java.util.Map[K, V]

}
