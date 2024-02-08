package byteback.verifier.boogie;

import byteback.verifier.common.Configuration;
import java.io.InputStream;
import java.io.OutputStream;

public interface BoogieConfiguration extends Configuration {

	public String getOutputPath();

	public OutputStream getOutputStream();

	public InputStream getPreludeInputStream();

}
