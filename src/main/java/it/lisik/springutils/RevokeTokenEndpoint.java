package it.lisik.springutils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
public class RevokeTokenEndpoint {
    private final DefaultTokenServices tokenServices;
    private final TokenStore tokenStore;

    @Autowired
    public RevokeTokenEndpoint(DefaultTokenServices tokenServices, TokenStore tokenStore) {
        this.tokenServices = tokenServices;
        this.tokenStore = tokenStore;
    }

    @RequestMapping(value = "/oauth/revoke", method = RequestMethod.POST)
    public ResponseEntity postRevokeAccessToken(Principal principal, @RequestParam Map<String, String> parameters) {
        final Optional<String> tokenTypeHint = Optional.ofNullable(parameters.get("token_type_hint"));
        final String token = parameters.get("token");

        final TokenType tokenType = tokenTypeHint.map(type -> TokenType.valueOf(type.toUpperCase()))
                .orElseGet(() -> TokenType.ACCESS_TOKEN);

        switch (tokenType) {
            default:
            case ACCESS_TOKEN:
                final DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(token);
                tokenStore.removeAccessToken(accessToken);
                break;

            case REFRESH_TOKEN:
                final DefaultOAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken(token);
                tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
        }


        return new ResponseEntity(HttpStatus.OK);
    }
}
