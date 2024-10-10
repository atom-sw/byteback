/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$ -c %{ghost}MapSpec -c %{ghost}HashMapSpec -c byteback.test.conversion.ScalaMapSpec -c byteback.test.conversion.JavaMapConvertersSpec -c byteback.test.conversion.AsScalaMapSpec -c byteback.test.conversion.AsJavaMapSpec -c byteback.test.conversion.BufferSpec -c byteback.test.conversion.HashMapSpec -o %t.bpl
  */
package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._


@Attach("scala.collection.JavaConverters$")
abstract class JavaMapConvertersSpec {

  @Behavior
  def m_is_mutable[K, V](m: java.util.Map[K, V]): Boolean = {
    return Ghost.of(classOf[MapSpec[K, V]], m).is_mutable();
  }

  @Require("m_is_mutable")
  @Return
  def mapAsScalaMapConverter[K, V](m: java.util.Map[K, V]): AsScala[Map[K, V]]

  @Return
  def mutableMapAsJavaMapConverter[K, V](m: Map[K, V]): AsJava[java.util.Map[K, V]]

}

@Attach("scala.collection.JavaConverters$AsScala")
abstract class AsScalaMapSpec {

  @Return
  def asScala[K, V](): Map[K, V]

}

@Attach("scala.collection.JavaConverters$AsJava")
abstract class AsJavaMapSpec {

  @Behavior
  def returns_mutable[K, V](r: java.util.Map[K, V]): Boolean = {
    return Ghost.of(classOf[MapSpec[K, V]], r).is_mutable();
  }

  @Ensure("returns_mutable")
  @Return
  def asJava[K, V](): java.util.Map[K, V]

}

@Attach("scala.collection.mutable.Map")
abstract class ScalaMapSpec[K, V] {

  @Return
  def put(k: K, v: V): Option[V]

}

@Attach("scala.collection.mutable.HashMap$")
abstract class HashMapSpec[K, V] {

  @Return
  def empty(): HashMap[K, V]

}

object JavaMapToScala {

  @Behavior
  def m_is_mutable[K, V](m: java.util.Map[K, V]): Boolean = {
    return Ghost.of(classOf[MapSpec[K, V]], m).is_mutable();
  }

  @Require("m_is_mutable")
  @Return
  def Convert_JavaMap_To_HashMap[K, V](m: java.util.Map[K, V]): Map[K, V] = {
    return m.asScala
  }

  @Return
  def Indirect_Convert_JavaMap_To_HashMap(): Map[Object, Object]  = {
    val m = new java.util.HashMap[Object, Object]
    m.put(new Object, new Object)
    m.put(new Object, new Object)
    
    Convert_JavaMap_To_HashMap(m)
  }

  @Return
  def Convert_HashMap_To_JavaMap[K, V](m: Map[K, V]): java.util.Map[K, V] = {
    return m.asJava
  }

  @Return
  def Indirect_Convert_HashMap_To_JavaMap(): java.util.Map[Object, Object] = {
    val m = HashMap.empty[Object, Object]
    m.put(new Object, new Object)
    m.put(new Object, new Object)
    
    Convert_HashMap_To_JavaMap(m)
  }

  @Return
  def Convert_HashMap_To_JavaMap_And_Back[K, V](m: Map[K, V]): Map[K, V] = {
    return m.asJava.asScala
  }

}

/**
  * RUN: %{verify} %t.bpl | filecheck %s
  * CHECK: Boogie program verifier finished with 18 verified, 0 errors
  */
