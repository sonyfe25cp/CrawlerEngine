package bit.crawl.extractor.xpath;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ConfigureFileNotFoundException extends RuntimeException {
	String _file;
	final String stackTrace;

	public ConfigureFileNotFoundException(String file) {
		_file = file;
		StringWriter sw = new StringWriter();
		super.printStackTrace(new PrintWriter(sw));
		stackTrace = sw.toString();
	}

	public void printStackTrace(PrintStream pw) {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.dir")).append(File.separator)
				.append(_file).append(" for configuration not found. \n");
		synchronized (pw) {
			pw.print("\n" + sb.toString());
			pw.print(stackTrace);
		}
	}

	public void printStackTrace() {
		printStackTrace(System.out);
	}
}
