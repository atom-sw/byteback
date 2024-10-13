package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.JavaConverters$AsScala")
abstract class AsScalaMapSpec {

  @Return
  def asScala[K, V](): Map[K, V]

}
