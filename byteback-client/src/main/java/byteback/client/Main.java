package byteback.client;

import byteback.verifier.boogie.BoogieVerifier;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	public static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(final String[] args) {
		final Configuration configuration = Configuration.getInstance();
		final BoogieVerifier verifier = BoogieVerifier.getInstance();
		final long totalStart = System.currentTimeMillis();
		try {
			configuration.parse(args);
			if (configuration.getHelp()) {
				configuration.getJCommander().usage();
			} else {
				log.info("Converting classes");
				verifier.verify(configuration);
				final long totalTime = System.currentTimeMillis() - totalStart;
				log.info("Verification completed in {}ms", totalTime);
			}
		} catch (final ParameterException exception) {
			log.error("Error while parsing program arguments: {}", exception.getMessage());
			exception.getJCommander().usage();
		}
	}

}
