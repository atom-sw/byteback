/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

public class StaticField {

	static boolean booleanField;

	static byte byteField;

	static short shortField;

	static int intField;

	static long longField;

	static float floatField;

	static double doubleField;

	static Object referenceField;

	public static boolean initialized() {
		return booleanField == false && byteField == 1 && shortField == 1 && intField == 1 && longField == 1
				&& floatField == 1.0f && doubleField == 1.0 && referenceField == null;
	}

	public static void initialize() {
		booleanField = false;
		byteField = 1;
		shortField = 1;
		intField = 1;
		longField = 1;
		floatField = 1.0f;
		doubleField = 1.0;
		referenceField = null;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
