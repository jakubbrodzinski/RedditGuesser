package bach.project.configuration;

import bach.project.apis.CommentAPIConnector;
import bach.project.apis.RedditAPIConnector;
import bach.project.configuration.handlers.SignedInInterceptor;
import bach.project.apis.APIConnector;
import bach.project.apis.APIConnectorImpl;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

@Configuration
@EnableTransactionManagement
public class WebConfiguration implements WebMvcConfigurer, ApplicationContextAware {
    private static final String UTF8 = "UTF-8";
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/").setCachePeriod(86400);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(86400);
        registry.addResourceHandler("/images/**").addResourceLocations("/images/").setCachePeriod(86400);
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(86400);
        registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/").setCachePeriod(86400);
        registry.addResourceHandler("/pdf/**").addResourceLocations("/pdf/").setCachePeriod(86400);
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        return localValidatorFactoryBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(signedInInterceptor());
    }

    @Bean
    public SignedInInterceptor signedInInterceptor() {
        return new SignedInInterceptor();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    @Profile("!integration-testing")
    public RedditClient redditClient(Environment environment) {
        UserAgent userAgent = new UserAgent("botGuesser", "bach.project.utils", "1.0.0-SNAPSHOT", environment.getProperty("reddit.username"));
        Credentials credentials = Credentials.script(environment.getProperty("reddit.username"), environment.getProperty("reddit.password"), environment.getProperty("reddit.clientId"), environment.getProperty("reddit.clientSecret"));
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

        RedditClient redditClient= OAuthHelper.automatic(adapter, credentials);
        redditClient.setAutoRenew(true);

        return redditClient;
    }

    @Bean
    @Profile("!integration-testing")
    public APIConnector apiConnector(){
        return new APIConnectorImpl();
    }

    @Bean
    @Profile("!integration-testing")
    public CommentAPIConnector commentAPIConnector(RedditClient redditClient){
        return new RedditAPIConnector(redditClient);
    }
}
