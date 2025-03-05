import byteback.specification.*;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Ensure;
import static byteback.specification.Operators.*;

public class Main {

	@Behavior
	public boolean positive_arguments(int a, int b) {
		return gte(a, 0) & gte(b, 0);
	}

	@Behavior
	public boolean positive_return(int a, int b, int returns) {
    return gte(returns, 0);
	}

	@Require("positive_arguments")   // a >= 0 && b >= 0
	@Ensure("positive_return")       // returns >= 0
	public int positive_sum(int a, int b) {
    return a + b;
	}

}
