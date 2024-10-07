package io.socket.client;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Url {

    /**
     * Expected format: "[id:password@]host[:port]"
     */
    private static Pattern PATTERN_AUTHORITY = Pattern.compile("^(.*@)?([^:]+)(:\\d+)?$");

    private Url() {}

    static class ParsedURI {
        public final URI uri;
        public final String id;

        public ParsedURI(URI uri, String id) {
            this.uri = uri;
            this.id = id;
        }
    }

    public static ParsedURI parse(URI uri) {
        String protocol = uri.getScheme();
        if (protocol == null || !protocol.matches("^https?|wss?$")) {
            protocol = "https";
        }

        String path = uri.getRawPath();
        if (path == null || path.length() == 0) {
            path = "/";
        }

        String userInfo = uri.getRawUserInfo();
        String query = uri.getRawQuery();
        String fragment = uri.getRawFragment();
        String _host = uri.getHost();
        if (_host == null) {
            // might happen on some of Samsung Devices such as S4.
            _host = extractHostFromAuthorityPart(uri.getRawAuthority());
        }
        URI completeUri = URI.create(protocol + "://"
                + (userInfo != null ? userInfo + "@" : "")
                + _host
                + path
                + (query != null ? "?" + query : "")
                + (fragment != null ? "#" + fragment : ""));
        String id = protocol + "://" + _host;

        return new ParsedURI(completeUri, id);
    }


    private static String extractHostFromAuthorityPart(String authority)
    {
        if (authority == null) {
            throw new RuntimeException("unable to parse the host from the authority");
        }

        Matcher matcher = PATTERN_AUTHORITY.matcher(authority);

        // If the authority part does not match the expected format.
        if (!matcher.matches()) {
            throw new RuntimeException("unable to parse the host from the authority");
        }

        // Return the host part.
        return matcher.group(2);
    }

}
