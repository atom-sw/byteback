package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.mutable.Map")
abstract class MapSpec[K, V] {

  @Return
  def put(k: K, v: V): Option[V]

}
