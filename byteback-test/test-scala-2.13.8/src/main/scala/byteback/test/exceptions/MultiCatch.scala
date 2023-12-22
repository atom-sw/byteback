/**
  * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
  */
package byteback.test.exceptions;

import byteback.annotations.Contract._;
import byteback.annotations.Special._;
import byteback.annotations.Operator._;
import byteback.annotations.Operator.{eq => equal};

import scala.annotation.meta._;

class MultiCatch {

  @Predicate
  def always_throws_exception1_exception2(e: Throwable): Boolean = {
    return e.isInstanceOf[Exception1] | e.isInstanceOf[Exception2]
  }

  @Ensure("always_throws_exception1_exception2")
  def alwaysThrowsMultiple(): Unit = {
    throw new Exception1();
  }

  def emptyMulticatch(): Unit = {
    try {
      alwaysThrowsMultiple();
    } catch {
      case e @ (_: Exception1 | _: Exception2) =>
    }
  }

  def multiCatchUnionAssertion(): Unit = {
    try {
      alwaysThrowsMultiple();
      assertion(false);
    } catch {
      case e @ (_: Exception1 | _: Exception2) =>
        assertion(e.isInstanceOf[Exception1] | e.isInstanceOf[Exception2]);
    }
  }

}
/**
  * RUN: %{verify} %t.bpl | filecheck %s
  * CHECK: Boogie program verifier finished with 6 verified, 0 errors
  */
