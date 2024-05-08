/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract._;
import byteback.specification.Special._;
import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};

import scala.annotation.meta._;

class Basic(@(Behavior @getter) val f: Int) {

  def tryCatchBlock(): Unit = {
    try {
      throw new Exception();
    } catch {
      case _: Exception =>
    }
  }

  @Return
  def neverThrows(): Unit = {
  }

  def neverCatches(): Unit = {
    try {
      neverThrows();
    } catch {
      case _: Exception => assertion(false);
    }
  }

  @Behavior
  def always_throws(): Boolean = {
    return true;
  }

  @Raise(exception = classOf[Exception], when = "always_throws")
  def alwaysThrows(): Unit = {
    throw new Exception();
  }

  def alwaysCatches(): Unit = {
    try {
      alwaysThrows();
      assertion(false);
    } catch {
      case _: Throwable =>
    }
  }

  @Raise(exception = classOf[Exception], when = "always_throws")
  def callsAlwaysThrows(): Unit = {
    alwaysThrows();
  }

  @Behavior
  def argument_is_even(n: Int): Boolean = {
    return equal(n % 2, 0);
  }

  @Raise(exception = classOf[Exception], when = "argument_is_even")
  def throwsIfEven(n: Int): Unit = {
    if (n % 2 == 0) {
      throw new Exception();
    }
  }

  def catchesIfEven(): Unit = {
    try {
      throwsIfEven(2);
      assertion(false);
    } catch {
      case e: Exception => assertion(true);
    }
  }

  @Behavior
  def f_is_1(): Boolean = {
    return equal(f, 1);
  }

  @Behavior
  def f_is_2(): Boolean = {
    return equal(f, 2);
  }

  @Behavior
  def f_is_3(): Boolean = {
    return equal(f, 3);
  }

  @Behavior
  def f_is_4(): Boolean = {
    return equal(f, 4);
  }

  @Behavior
  def f_is_gt_4(): Boolean = {
    return gt(f, 4);
  }

  @Raise(exception = classOf[Exception1], when = "f_is_1")
  @Raise(exception = classOf[Exception2], when = "f_is_2")
  @Raise(exception = classOf[Exception3], when = "f_is_3")
  @Raise(exception = classOf[Exception4], when = "f_is_4")
  @Return(when = "f_is_gt_4")
  def throwsMultiple(): Unit = {
    f match {
      case 1 => throw new Exception1()
      case 2 => throw new Exception2()
      case 3 => throw new Exception3()
      case 4 => throw new Exception4()
      case _ =>
    }
  }

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 10 verified, 0 errors
 */
