package bit.crawl.util;

import java.io.*;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class SlurpUtils {
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static long copyLargeWithLimit(InputStream input,
			OutputStream output, long limit) {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		long remain = limit;
		int toRead;
		try {
			while (remain > 0) {
				toRead = (int) (remain > DEFAULT_BUFFER_SIZE ? DEFAULT_BUFFER_SIZE
						: remain);
				n = input.read(buffer, 0, toRead);
				if (n == -1) {
					break;
				}
				output.write(buffer, 0, n);
				count += n;
				remain -= n;
			}
		} catch (IOException e) {
			// Ignore. Copy as much as possible.
		}
		return count;
	}

	public static byte[] toByteArrayWithLimit(InputStream input, long limit) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copyLargeWithLimit(input, output, limit);
		return output.toByteArray();
	}

}
