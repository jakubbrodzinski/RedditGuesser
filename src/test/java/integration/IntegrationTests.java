package integration;

import bach.project.bean.form.SignUpForm;
import bach.project.configuration.Application;
import bach.project.configuration.IntegrationTestingAdditionalConfiguration;
import bach.project.service.UserService;
import net.dean.jraw.RedditClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextHierarchy({
        @ContextConfiguration(classes = IntegrationTestingAdditionalConfiguration.class),
        @ContextConfiguration(classes = Application.class)
})
@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = {"integration-testing"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
public class IntegrationTests {
    private final RedditClient redditClient;
    private final UserService userService;

    @Autowired
    public IntegrationTests(RedditClient redditClient,UserService userService){
        this.redditClient=redditClient;
        this.userService=userService;
    }

    @Test
    public void test(){
        Assertions.assertEquals(1,1);
//        Assertions.assertFalse(userService.existsUserByUserName("email1"));
        SignUpForm signUpForm=new SignUpForm();
        signUpForm.setFirstName("firstName");
        signUpForm.setLastName("lastName");
        signUpForm.seteMail("email1");
        signUpForm.setPassword1("abc1");
        signUpForm.setPassword2("abc1");
        userService.registerUser(signUpForm);
        Assertions.assertTrue(userService.existsUserByUserName("email1"));
//        Assertions.assertFalse(userService.existsUserByUserName("jakubby@gmail.com"));
    }
}
