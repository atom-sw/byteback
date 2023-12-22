/**
  * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
  */
package byteback.test.exceptions;

import byteback.annotations.Contract._;
import byteback.annotations.Special._;
import byteback.annotations.Operator._;
import byteback.annotations.Operator.{eq => equal};

class TryFinally {

  @Predicate
  def always(): Boolean = {
    return true
  }

  @Raise(exception = classOf[Exception1], when = "always")
  def alwaysThrows1(): Unit = {
    throw new Exception1()
  }

  def finallyBlock(): Unit = {
    try {
    } finally {
    }
  }

  def catchFinallyBlock(): Unit = {
    try {
    } catch {
      case _: Throwable => {
        assertion(false);
      }
    }	finally {
    }
  }

  def finallyIsExecuted(): Unit = {
    try {
      alwaysThrows1()
      assertion(false);
    } finally {
    }

    assertion(false);
  }

  def finallyIsExecutedAfterThrowInCatch(): Unit = {
    try {
      alwaysThrows1()
    } catch {
      case _: Exception1 => alwaysThrows1()
    } finally {
    }

    assertion(false);
  }

  def unreachableCatch(): Unit = {
    try {
      alwaysThrows1();
      assertion(false);
    } catch {
      case e: Exception2 => assertion(false);
    } finally {
    }

    assertion(false);
  }

  @Predicate
  def returns_2(returns: Int): Boolean = {
    return equal(returns, 2);
  }

  @Ensure("returns_2")
  def finallyOverridesReturn(): Int = {
    try {
      return 1
    } finally {
      return 2
    }
  }

  @Ensure("returns_2")
  def finallyOverridesThrows(): Int = {
    try {
      throw new Exception1();
    } finally {
      return 2;
    }
  }

  @Ensure("returns_2")
  def finallyOverrides1NestedFinally(): Int = {
    try {
      try {
        throw new Exception1();
      } finally {
        return 1;
      }
    } finally {
      return 2;
    }
  }

  @Ensure("returns_2")
  def finallyOverrides2NestedFinally(): Int = {
    try {
      try {
        try {
          throw new Exception1();
        } finally {
          return 3;
        }
      } finally {
        return 1;
      }
    } finally {
      return 2;
    }
  }

}
/**
  * RUN: %{verify} %t.bpl | filecheck %s
  * CHECK: Boogie program verifier finished with 13 verified, 0 errors
  */
