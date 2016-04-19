package it.lisik.springutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
    public static final List<SimpleGrantedAuthority> AUTHORITIES = Collections.singletonList(new SimpleGrantedAuthority("USER"));
    public static final InMemoryUserDetailsManager USER_DETAILS_MANAGER = new InMemoryUserDetailsManager(Arrays.asList(
            new User("matt", "matt", AUTHORITIES),
            new User("Matt2", "12345678", AUTHORITIES),
            new User("Matt3", "12345678", AUTHORITIES)
    ));
    public static final InMemoryTokenStore TOKEN_STORE = new InMemoryTokenStore();
    public static final InMemoryClientDetailsService CLIENT_DETAILS_SERVICE = new InMemoryClientDetailsService();

    @Autowired
    public AuthenticationManager authenticationManager;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("example")
                .secret("example")
                .scopes("test")
                .authorities("APP")
                .authorizedGrantTypes("password", "client_credentials", "refresh_token");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore());
        endpoints.tokenServices(defaultTokenServices());
        endpoints.authenticationManager(authenticationManager);
    }


    @Bean
    @Primary
    public TokenStore tokenStore() {
        return TOKEN_STORE;
    }

    @Bean
    @Primary
    public DefaultTokenServices defaultTokenServices() {
        final DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        return tokenServices;
    }



}
