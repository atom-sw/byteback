package byteback.specification.plugin;

import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Return;

@Plugin.Attach("java.io.FileReader")
public abstract class FileReaderSpec {

	@Return
	public FileReaderSpec() {
	}

	@Behavior
	public abstract boolean is_closed();

	@Ensure("is_closed")
	public void close() {
	}

}
