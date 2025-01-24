package com.example.demo.config;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.springframework.security.WebAuthnAuthenticationProvider;
import com.webauthn4j.springframework.security.config.configurers.WebAuthnLoginConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

//@Profile("digital")
//@Configuration
//@EnableWebSecurity
public class SecurityConfigDigital {

    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager) throws Exception {

        http.authenticationManager(authenticationManager);
        // WebAuthn Login
        http.with(WebAuthnLoginConfigurer.webAuthnLogin(), (customizer) ->{
            customizer
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .credentialIdParameter("credentialId")
                    .clientDataJSONParameter("clientDataJSON")
                    .authenticatorDataParameter("authenticatorData")
                    .signatureParameter("signature")
                    .clientExtensionsJSONParameter("clientExtensionsJSON")
                    .loginProcessingUrl("/login")
                    .attestationOptionsEndpoint()
                    .rp()
                    .name("WebAuthn4J Spring Security Sample")
                    .and()
                    .pubKeyCredParams(
                            new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256), // Windows Hello
                            new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256) // FIDO U2F Key, etc
                    )
                    .extensions()
                    .credProps(true)
                    .and()
                    .assertionOptionsEndpoint()
                    .and()
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler);
        });
        return http.build();
    }

    @Bean
    public WebAuthnAuthenticationProvider webAuthnAuthenticationProvider(
            WebAuthnAuthenticatorService authenticatorService,
            WebAuthnManager webAuthnManager){
        return new WebAuthnAuthenticationProvider(authenticatorService, webAuthnManager);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers){
        return new ProviderManager(providers);
    }*/
}
