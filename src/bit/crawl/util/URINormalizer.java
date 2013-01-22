package bit.crawl.util;

import java.util.*;
import java.net.*;

import org.apache.commons.lang.StringUtils;

/**
 * The main purpose is to normalize potentially duplicated URLs/URIs found in
 * web pages, which may lead to duplicated crawling of the same resource.
 * 
 * @author Kunshan Wang
 * 
 */
public class URINormalizer {
	/**
	 * Normalize a URI.
	 * <p>
	 * The query part is reordered alphabetically. The fragment part is removed.
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param uri
	 *            The uri to normalize.
	 * @return The normalized uri.
	 * @throws URISyntaxException
	 *             Thrown if the uri is of bad syntax.
	 */
	public static URI normalize(URI uri) throws URISyntaxException {
		String query = uri.getQuery();
		String[] queryParts = StringUtils.split(query, '&');
		Arrays.sort(queryParts);
		String newQuery = StringUtils.join(queryParts, '&');

		URI newUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(),
				uri.getPort(), uri.getPath(), newQuery, null);
		return newUri;
	}

	public static String normalize(String uriString) throws URISyntaxException {
		URI uri = new URI(uriString);
		URI newUri = normalize(uri);
		return newUri.toString();
	}
}
