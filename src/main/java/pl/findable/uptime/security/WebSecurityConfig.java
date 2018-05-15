package pl.findable.uptime.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationEntryPoint authEntryPoint;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeRequests()
				.antMatchers("/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**").permitAll()
				.anyRequest().authenticated()
			.and().httpBasic()
			.authenticationEntryPoint(authEntryPoint);
	}

	@Bean
  @Override
  public UserDetailsService userDetailsService() {
    UserDetails admin = User.withUsername("rafalZapiszSeGdziesHaslo")
    		.password("Bog0n3r4d4w1$lA55")
    		.authorities("ADMIN", "ACTUATOR")
    		.build();

    return new InMemoryUserDetailsManager(Arrays.asList(admin));
  }
}
