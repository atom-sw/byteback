/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$ -c byteback.test.conversion.JavaConvertersSpec -c %{ghost}ListSpec -c %{ghost}ArrayListSpec -c byteback.test.conversion.AsScalaSpec -o %t.bpl
 */
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

}

@Attach("scala.collection.JavaConverters$AsScala")
abstract class AsScalaSpec {

  @Return
  def asScala[A](): Buffer[A]

}

object JavaToScalaCollection {

  @Behavior
  def l_is_mutable[A](l: java.util.List[A]): Boolean = {
    return Ghost.of(classOf[ListSpec[A]], l).is_mutable();
  }

  @Require("l_is_mutable")
  @Return
  def Convert_JavaList_To_Buffer[T](l: java.util.List[T]): Buffer[T] = {
    return l.asScala
  }

  @Return
  def Indirect_Convert_JavaList_To_Buffer(): Unit = {
    val javaList = new java.util.ArrayList[Object]
    javaList.add(new Object)
    javaList.add(new Object)
    
    val scalaBuffer = Convert_JavaList_To_Buffer(javaList)
  }

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 8 verified, 0 errors
 */
