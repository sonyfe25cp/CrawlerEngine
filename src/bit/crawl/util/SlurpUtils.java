package bit.crawl.util;

import java.io.*;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class SlurpUtils {
	private static final int DEFAULT_BUFFER_SIZE = 10240 * 4;

	public static void copyLargeWithLimit(InputStream input,
			OutputStream output, int limit) {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int n = 0;
		int remain = limit;
		int toRead;
		try {
			while (remain > 0) {
				toRead = remain > DEFAULT_BUFFER_SIZE ? DEFAULT_BUFFER_SIZE : remain;
				n = input.read(buffer, 0, toRead);
				if (n == -1) {
					break;
				}
				output.write(buffer, 0, n);
				remain -= n;
			}
		} catch (IOException e) {
			
		}
	}

	public static byte[] toByteArrayWithLimit(InputStream input, int limit) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copyLargeWithLimit(input, output, limit);
		return output.toByteArray();
	}

}
