package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.mutable.Buffer")
abstract class BufferSpec[A] {

  @Return
  def +=(elem: A): Growable[A]

}
