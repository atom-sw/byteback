package byteback.annotations;

import static byteback.annotations.Operator.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OperatorTest {

	@Test
	public void Implies_TrueTrue_ReturnsTrue() {
		assertTrue(implies(true, true));
	}

	public void Implies_FalseTrue_ReturnsTrue() {
		assertTrue(implies(false, true));
	}

	@Test
	public void Implies_TrueFalse_ReturnsFalse() {
		assertFalse(implies(true, false));
	}

	@Test
	public void Implies_FalseFalse_ReturnsTrue() {
		assertTrue(implies(false, false));
	}

	@Test
	public void Iff_TrueTrue_ReturnsTrue() {
		assertTrue(iff(true, true));
	}

	@Test
	public void Iff_FalseTrue_ReturnsFalse() {
		assertFalse(iff(false, true));
	}

	@Test
	public void Iff_TrueFalse_ReturnsFalse() {
		assertFalse(iff(true, false));
	}

	@Test
	public void Iff_FalseFalse_ReturnsTrue() {
		assertTrue(iff(false, false));
	}

}
