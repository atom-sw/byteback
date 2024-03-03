package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Predicate;
import byteback.specification.Contract.Function;
import byteback.specification.Contract.Return;
import java.io.FileReader;

@Attach(FileReader.class)
public abstract class FileReaderSpec {

	@Return
	public FileReaderSpec() {
	}

	@Function
	public abstract boolean isClosed();

	@Predicate
	public boolean is_closed() {
		return isClosed();
	}

	@Ensure("is_closed")
	public void close() {
	}

}
