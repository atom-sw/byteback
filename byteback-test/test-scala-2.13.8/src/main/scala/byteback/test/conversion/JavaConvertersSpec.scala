package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.JavaConverters$")
abstract class JavaConvertersSpec {

  @Behavior
  def l_is_mutable[A](l: java.util.List[A]): Boolean = {
    return Ghost.of(classOf[ListSpec[A]], l).is_mutable();
  }

  @Require("l_is_mutable")
  @Return
  def asScalaBufferConverter[A](l: java.util.List[A]): AsScala[Buffer[A]]

  @Return
  def bufferAsJavaListConverter[A](l: Buffer[A]): AsJava[java.util.List[A]]

  @Behavior
  def m_is_mutable[K, V](m: java.util.Map[K, V]): Boolean = {
    return Ghost.of(classOf[MapSpec[K, V]], m).is_mutable();
  }

  @Require("m_is_mutable")
  @Return
  def mapAsScalaMapConverter[K, V](m: java.util.Map[K, V]): AsScala[Map[K, V]]

  @Return
  def mutableMapAsJavaMapConverter[K, V](m: Map[K, V]): AsJava[java.util.Map[K, V]]

  @Behavior
  def s_is_mutable[A](s: java.util.Set[A]): Boolean = {
    return Ghost.of(classOf[SetSpec[A]], s).is_mutable();
  }

  @Require("s_is_mutable")
  @Return
  def asScalaSetConverter[A](s: java.util.Set[A]): AsScala[Set[A]]

  @Return
  def mutableSetAsJavaSetConverter[A](s: Set[A]): AsJava[java.util.Set[A]]

}
