/**
  * RUN: %{byteback} -cp %{jar} -c %{class} --npe -o %t.bpl
  */

package byteback.test.exceptions;

import byteback.specification.Contract._;
import byteback.specification.Special._;
import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};

class A @Return () {

  @Return
  def method(): Unit = {}

}

class PotentialNullDereference {

  @Return
  def freshTarget(): Unit = {
    var a: A = new A()
    a.method()
  }

  @Return
  def passedTarget(a: A): Unit = {
    if (a != null) {
      a.method()
    }
  }

  @Return
  def passedArrayTarget(a: Array[Int]): Unit = {
    if (a != null) {
      val a_l: Int = a.length;
    }
  }

  @Behavior
  def a_is_not_null(a: Array[Int]): Boolean = {
    return neq(a, null);
  }

  @Return(when = "a_is_not_null")
  def constrainedPassedArrayTarget(a: Array[Int]): Unit = {
    val a_1: Int = a(1);
  }

}

/**
  * RUN: %{verify} %t.bpl | filecheck %s
  * CHECK: Boogie program verifier finished with 7 verified, 0 errors
  */
