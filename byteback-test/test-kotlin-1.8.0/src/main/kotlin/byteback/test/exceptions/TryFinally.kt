/**
 * 	RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.exceptions

import byteback.specification.Contract.*
import byteback.specification.Operators.*

class TryFinally {

		@Behavior
		fun always(): Boolean {
				return true
		}

		@Raise(`exception` = Exception1::class, `when` = "always")
		fun alwaysThrows1(): Unit {
				throw Exception1()
		}

		fun emptyFinallyBlock(): Unit {
				try {} finally {}
		}

		fun catchFinallyBlock(): Unit {
				try {} catch (e: Throwable) {
						assertion(false)
				} finally {}
		}

		fun finallyIsExecuted(): Unit {
				try {
						alwaysThrows1()
						assertion(false)
				} finally {}

				assertion(false)
		}

		fun finallyIsExecutedAfterThrowInCatch(): Unit {
				try {
						alwaysThrows1()
						assertion(false)
				} catch (e: Exception1) {
						alwaysThrows1()
						assertion(false)
				} finally {}

				assertion(false)
		}

		fun unreachableCatch(): Unit {
				try {
						alwaysThrows1()
						assertion(false)
				} catch (e: Exception2) {
						assertion(false)
				} finally {}
				assertion(false)
		}

		@Behavior
		fun `returns 2`(returns: Int): Boolean {
				return eq(returns, 2)
		}

		@Ensure("returns 2")
		fun finallyOverridesReturn(): Int {
				try {
						return 1
				} finally {
						return 2
				}
		}

		@Ensure("returns 2")
		fun finallyOverridesThrows(): Int {
				try {
						throw Exception1()
				} finally {
						return 2
				}
		}

		@Ensure("returns 2")
		fun finallyOverrides1NestedFinally(): Int {
				try {
						try {
								throw Exception1()
						} finally {
								return 1
						}
				} finally {
						return 2
				}
		}

		@Ensure("returns 2")
		fun finallyOverrides2NestedFinally(): Int {
				try {
						try {
								try {
										throw Exception1()
								} finally {
										return 3
								}
						} finally {
								return 1
						}
				} finally {
						return 2
				}
		}
}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 11 verified, 0 errors
 */
