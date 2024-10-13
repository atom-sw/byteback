package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.mutable.HashMap$")
abstract class HashMapSpec[K, V] {

  @Return
  def empty(): HashMap[K, V]

}
