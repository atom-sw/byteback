package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.JavaConverters$AsJava")
abstract class AsJavaListSpec {

  @Behavior
  def returns_mutable[A](r: java.util.List[A]): Boolean = {
    return Ghost.of(classOf[ListSpec[A]], r).is_mutable();
  }

  @Ensure("returns_mutable")
  @Return
  def asJava[A](): java.util.List[A]

}
