/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.instance;

public class InstanceField {

	boolean booleanField;

	byte byteField;

	short shortField;

	int intField;

	long longField;

	float floatField;

	double doubleField;

	Object referenceField;

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 1 verified, 0 errors
 */
