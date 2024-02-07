package byteback.cli;

import byteback.verifier.boogie.BoogieVerifier;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	public static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(final String[] args) {
		final Configuration configuration = Configuration.v();
		final BoogieVerifier verifier = BoogieVerifier.v();
		final long totalStart = System.currentTimeMillis();
		try {
			configuration.parse(args);
			if (configuration.getHelp()) {
				configuration.getJCommander().usage();
			} else {
				final long conversionStart = System.currentTimeMillis();
				log.info("Converting classes");
				final long endTime = System.currentTimeMillis();
				verifier.verify(configuration);
				final long totalTime = endTime - totalStart;
				final long conversionTime = endTime - conversionStart;
				log.info("Verification completed in {}ms, total time {}ms", conversionTime, totalTime);
			}
		} catch (final ParameterException exception) {
			log.error("Error while parsing program arguments: {}", exception.getMessage());
			exception.getJCommander().usage();
		}
	}

}
