package byteback.test.conversion

import byteback.specification.Contract._
import byteback.specification.ghost._
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.mutable.Set")
abstract class SetSpec[A] {

  @Return
  def +=(elem: A): Growable[A]

}
