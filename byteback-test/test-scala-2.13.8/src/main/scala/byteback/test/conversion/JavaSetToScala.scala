/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$ -c %{ghost}SetSpec -c byteback.test.conversion.JavaConvertersSpec -c byteback.test.conversion.AsScalaSetSpec -c byteback.test.conversion.AsJavaSetSpec -c byteback.test.conversion.SetSpec -c %{ghost}HashSetSpec -c byteback.test.conversion.HashSetSpec -o %t.bpl
 */
package byteback.test.conversion

import byteback.specification.Contract._
import byteback.specification.ghost._
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

object JavaSetToScala {

  @Behavior
  def s_is_mutable[A](s: java.util.Set[A]): Boolean = {
    return Ghost.of(classOf[SetSpec[A]], s).is_mutable();
  }

  @Require("s_is_mutable")
  @Return
  def Convert_JavaSet_To_Set[T](s: java.util.Set[T]): Set[T] = {
    return s.asScala
  }

  @Return
  def Indirect_Convert_JavaSet_To_Set(): Set[Object] = {
    val javaSet = new java.util.HashSet[Object]
    javaSet.add(new Object)
    javaSet.add(new Object)

    Convert_JavaSet_To_Set(javaSet)
  }

  @Return
  def Convert_Set_To_JavaSet[T](s: Set[T]): java.util.Set[T] = {
    return s.asJava
  }

  @Return
  def Indirect_Convert_Set_To_JavaSet(): java.util.Set[Object] = {
    val s = HashSet.empty[Object]
    s += new Object
    s += new Object

    Convert_Set_To_JavaSet(s)
  }

  @Return
  def Convert_Set_To_JavaSet_And_Back[T](s: Set[T]): Set[T] = {
    return s.asJava.asScala
  }

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 17 verified, 0 errors
 */
