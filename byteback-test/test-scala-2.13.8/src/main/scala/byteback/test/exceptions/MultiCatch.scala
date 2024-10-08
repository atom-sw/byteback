/**
  * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
  */
package byteback.test.exceptions;

import byteback.specification.Contract._;
import byteback.specification.Special._;
import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};

import scala.annotation.meta._;

class MultiCatch {

  @Exceptional
  @Behavior
  def always_throws_exception1_exception2(): Boolean = {
    return thrown().isInstanceOf[Exception1] | thrown().isInstanceOf[Exception2]
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
  * CHECK: Boogie program verifier finished with 4 verified, 0 errors
  */
