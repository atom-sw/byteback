/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.controlflow;

import byteback.specification.Contract._;
import byteback.specification.Special._;
import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};

class Basic {

  def empty(): Unit = {}

  def doubleAssignment(a: Int): Int = {
    var b: Int = 0;
    b = a + 42
    b = b + 42

    return b;
  }

  def emptyWhile(a: Boolean): Unit = {
    while (a) {
    }
  }

  def emptyDoWhile(a: Boolean): Unit = {
    do {
    } while (a);
  }

  def emptyIf(a: Boolean): Unit = {
    if (a) {
    }
  }

  def returnsNull(): Object = {
    return null;
  }

  def realEmptyCondition(r: Double): Unit = {
    if (r < 2.72) {
    }
  }

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 8 verified, 0 errors
 */
