package byteback.analysis;

import byteback.util.ListHashMap;
import soot.Unit;
import soot.Value;

public class UseDefineChain {

	final ListHashMap<Value, Unit> definitions;

	final ListHashMap<Unit, Unit> uses;

	public UseDefineChain() {
		this.definitions = new ListHashMap<>();
		this.uses = new ListHashMap<>();
	}

}
