/* $Id: SecurityConfig.java 1198 2017-05-17 19:57:40Z zis $ */

package ch.claninfo.clanng.web.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

	@Override
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService pUserDetailsService, PasswordEncoder pPasswordEncoder) {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(pUserDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(pPasswordEncoder);
		return daoAuthenticationProvider;
	}

	@Inject
	public void doConfig(AuthenticationManagerBuilder auth, AuthenticationProvider pAuthenticationProvider) throws Exception {
		auth.authenticationProvider(pAuthenticationProvider);
	}
}
