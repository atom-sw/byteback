/**
  * RUN: %{byteback} -cp %{jar} -c %{class} --nct -o %t.bpl
  */

package byteback.test.exceptions;

import byteback.annotations.Contract._;
import byteback.annotations.Special._;
import byteback.annotations.Operator._;
import byteback.annotations.Operator.{eq => equal};

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

  @Predicate
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
