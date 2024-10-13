/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$ -c %{ghost}ListSpec -c %{ghost}ArrayListSpec -c byteback.test.conversion.JavaConvertersSpec -c byteback.test.conversion.AsScalaBufferSpec -c byteback.test.conversion.AsJavaListSpec -c byteback.test.conversion.BufferSpec -c byteback.test.conversion.ArrayBufferSpec -o %t.bpl
  */
package byteback.test.conversion;

import byteback.specification.Contract._
import byteback.specification.ghost._;
import byteback.specification.ghost.Ghost._

import collection.JavaConverters._
import collection.mutable._

object JavaListToScala {

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
  def Indirect_Convert_JavaList_To_Buffer(): Buffer[Object]  = {
    val javaList = new java.util.ArrayList[Object]
    javaList.add(new Object)
    javaList.add(new Object)
    
    Convert_JavaList_To_Buffer(javaList)
  }

  @Return
  def Convert_Buffer_To_JavaList[T](b: Buffer[T]): java.util.List[T] = {
    return b.asJava
  }

  @Return
  def Indirect_Convert_Buffer_To_JavaList(): java.util.List[Object] = {
    val b = ArrayBuffer.empty[Object]
    b += new Object;
    b += new Object;
    
    Convert_Buffer_To_JavaList(b)
  }

  @Return
  def Convert_Buffer_To_JavaList_And_Back[T](b: Buffer[T]): Buffer[T] = {
    return b.asJava.asScala
  }

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 17 verified, 0 errors
 */
