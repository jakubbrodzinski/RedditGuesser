package bach.project.configuration;

import bach.project.configuration.handlers.CustomLoginFailureHandler;
import bach.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/sign_in").usernameParameter("username").passwordParameter("password")
                .failureHandler(authenticationFailureHandler()).successHandler(new SimpleUrlAuthenticationSuccessHandler("/account/panel"))
                .and()
                .logout().logoutUrl("/account/logout").logoutSuccessUrl("/sign_in?status=5")
                .and()
                .authorizeRequests().antMatchers("/account/**").hasRole("USER")
                .and()
                .exceptionHandling().accessDeniedPage("/denied");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,UserService userService) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(pbkdf2PasswordEncoder());

    }

    @Bean
    public AuthenticationTrustResolver getAuthenticationTrustResolver() {
        return new AuthenticationTrustResolverImpl();
    }

    @Bean
    public Pbkdf2PasswordEncoder pbkdf2PasswordEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomLoginFailureHandler();
    }

}
