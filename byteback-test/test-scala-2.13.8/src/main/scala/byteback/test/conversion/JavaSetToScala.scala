/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$ -c %{ghost}SetSpec -c byteback.test.conversion.JavaSetConvertersSpec -c byteback.test.conversion.AsScalaSetSpec -c byteback.test.conversion.AsJavaSetSpec -c byteback.test.conversion.ScalaSetSpec -c %{ghost}HashSetSpec -c byteback.test.conversion.HashSetSpec -o %t.bpl
 */
package byteback.test.conversion

import byteback.specification.Contract._
import byteback.specification.ghost._
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

@Attach("scala.collection.JavaConverters$")
abstract class JavaSetConvertersSpec {

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

@Attach("scala.collection.JavaConverters$AsScala")
abstract class AsScalaSetSpec {

  @Return
  def asScala[A](): Set[A]
}

@Attach("scala.collection.JavaConverters$AsJava")
abstract class AsJavaSetSpec {

  @Behavior
  def returns_mutable[A](r: java.util.Set[A]): Boolean = {
    return Ghost.of(classOf[SetSpec[A]], r).is_mutable();
  }

  @Ensure("returns_mutable")
  @Return
  def asJava[A](): java.util.Set[A]
}

@Attach("scala.collection.mutable.Set")
abstract class ScalaSetSpec[A] {

  @Return
  def +=(elem: A): Growable[A]
}

@Attach("scala.collection.mutable.HashSet$")
abstract class HashSetSpec[A] {

  @Return
  def empty[A](): HashSet[A]

}

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
