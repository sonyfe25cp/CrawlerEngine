package bit.crawl.util;

import java.util.Formatter;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * 定制后的logger
 * @author ChenJie
 *
 */
public class Logger {

	private org.apache.log4j.Logger logger = null;

	public Logger() {
		logger = LogManager.getLogger(new Exception().getStackTrace()[1].getClassName());
	}

	public void info(String args) {

		if (!logger.isInfoEnabled())
			return;

		logger.info(args);
	}

	public void info(String format, Object... args) {

		if (!logger.isInfoEnabled())
			return;

		logger.info(sprintf(format, args));
	}

	public void info(String format, Throwable t, Object... args) {

		if (!logger.isInfoEnabled())
			return;

		logger.info(sprintf(format, args), t);
	}

	public void debug(String args) {

		if (!logger.isDebugEnabled())
			return;

		logger.debug(args);
	}

	public void debug(String format, Object... args) {

		if (!logger.isDebugEnabled())
			return;

		logger.debug(sprintf(format, args));
	}

	public void debug(String format, Throwable t, Object... args) {

		if (!logger.isDebugEnabled())
			return;

		logger.debug(sprintf(format, args), t);
	}

	public void error(String args) {

		if (!logger.isEnabledFor(Level.ERROR))
			return;

		logger.error(args);
	}

	public void error(String format, Object... args) {

		if (!logger.isEnabledFor(Level.ERROR))
			return;

		logger.error(sprintf(format, args));
	}

	public void error(String format, Throwable t, Object... args) {

		if (!logger.isEnabledFor(Level.ERROR))
			return;

		logger.error(sprintf(format, args), t);
	}

	public void fatal(String args) {

		if (!logger.isEnabledFor(Level.FATAL))
			return;

		logger.fatal(args);
	}

	public void fatal(String format, Object... args) {

		if (!logger.isEnabledFor(Level.FATAL))
			return;

		logger.fatal(sprintf(format, args));
	}

	public void fatal(String format, Throwable t, Object... args) {

		if (!logger.isEnabledFor(Level.FATAL))
			return;

		logger.fatal(sprintf(format, args), t);
	}

	public void warn(String args) {

		if (!logger.isEnabledFor(Level.WARN))
			return;

		logger.warn(args);
	}

	public void warn(String format, Object... args) {

		if (!logger.isEnabledFor(Level.WARN))
			return;

		logger.warn(sprintf(format, args));
	}

	public void warn(String format, Throwable t, Object... args) {

		if (!logger.isEnabledFor(Level.WARN))
			return;

		logger.warn(sprintf(format, args), t);
	}

	/**
	 * Clone of C sprintf support. log.error( "This thing broke: %s due to bar:
	 * %s on this thing: %s", foo, bar, car );
	 * 
	 * @see Formatter
	 */
	public static String sprintf(String format, Object... args) {

		Formatter f = getFormatter();
		f.format(format, args);

		StringBuilder sb = (StringBuilder) f.out();
		String message = sb.toString();
		sb.setLength(0);

		return message;

	}

	/**
	 * Interface to cached formatters.
	 */
	private static Formatter getFormatter() {
		return formatterCache.get();
	}

	private static ThreadLocal<Formatter> formatterCache = new ThreadLocal<Formatter>() {

		protected synchronized Formatter initialValue() {
			return new Formatter();
		}

	};

}
