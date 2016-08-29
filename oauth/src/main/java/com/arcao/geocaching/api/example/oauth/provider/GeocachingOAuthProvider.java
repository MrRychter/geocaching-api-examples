package com.arcao.geocaching.api.example.oauth.provider;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.OAuth1AccessTokenExtractor;
import com.github.scribejava.core.extractors.OAuth1RequestTokenExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.utils.OAuthEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeocachingOAuthProvider extends DefaultApi10a {
    private static final Pattern ERROR_MESSAGE_REGEX = Pattern.compile("oauth_error_message=([^&]*)");
    private static final String OAUTH_URL = "https://www.geocaching.com/oauth/mobileoauth.ashx";

    @Override
    public TokenExtractor<OAuth1RequestToken> getRequestTokenExtractor() {
        return new GeocachingRequestTokenExtractor();
    }

    @Override
    public TokenExtractor<OAuth1AccessToken> getAccessTokenExtractor() {
        return new GeocachingAccessTokenExtractor();
    }

    protected String getOAuthUrl() {
        return OAUTH_URL;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return getOAuthUrl();
    }

    @Override
    public String getAccessTokenEndpoint() {
        return getOAuthUrl();
    }

    @Override
    public String getAuthorizationUrl(OAuth1RequestToken requestToken) {
        return getOAuthUrl() + "?oauth_token=" + OAuthEncoder.encode(requestToken.getToken());
    }

    static void checkError(CharSequence response) {
        Matcher matcher = ERROR_MESSAGE_REGEX.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1) {
            throw new OAuthException(OAuthEncoder.decode(matcher.group(1)));
        }
    }

    public static class Staging extends GeocachingOAuthProvider {
        private static final String OAUTH_URL = "https://staging.geocaching.com/oauth/mobileoauth.ashx";

        @Override
        protected String getOAuthUrl() {
            return OAUTH_URL;
        }
    }

    private static class GeocachingRequestTokenExtractor extends OAuth1RequestTokenExtractor {
        GeocachingRequestTokenExtractor() {
        }

        @Override
        public OAuth1RequestToken extract(String response) {
            if (response != null)
                checkError(response);
            return super.extract(response);
        }
    }

    private static class GeocachingAccessTokenExtractor extends OAuth1AccessTokenExtractor {
        GeocachingAccessTokenExtractor() {
        }

        @Override
        public OAuth1AccessToken extract(String response) {
            if (response != null)
                checkError(response);
            return super.extract(response);
        }
    }
}
