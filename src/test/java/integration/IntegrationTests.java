package integration;

import bach.project.apis.APIConnector;
import bach.project.bean.form.ChangeDataForm;
import bach.project.bean.form.SignUpForm;
import bach.project.bean.model.Comment;
import bach.project.bean.model.Link;
import bach.project.bean.model.User;
import bach.project.configuration.Application;
import bach.project.configuration.IntegrationTestingAdditionalConfiguration;
import bach.project.service.CommentService;
import bach.project.service.PredictionService;
import bach.project.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ContextHierarchy({
        @ContextConfiguration(classes = IntegrationTestingAdditionalConfiguration.class),
        @ContextConfiguration(classes = Application.class)
})
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = {"integration-testing"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
public class IntegrationTests {
    private final String USER_NAME = "jakub@integration-testing.com";
    @Inject
    private UserService userService;
    @Inject
    private PredictionService predictionService;
    @Inject
    private APIConnector apiConnector;
    @Inject
    private CommentService commentService;

    @BeforeEach
    void setUpUser() {
        Assertions.assertFalse(userService.existsUserByUserName(USER_NAME));

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setFirstName("firstName");
        signUpForm.setLastName("lastName");
        signUpForm.seteMail(USER_NAME);
        signUpForm.setPassword1("abc1");
        signUpForm.setPassword2("abc1");
        userService.registerUser(signUpForm);
        Assertions.assertTrue(userService.existsUserByUserName(USER_NAME));
    }

    @AfterEach
    void deleteUser() {
        userService.removeUser(USER_NAME);
    }

    @Test
    void predictSingleCommentTest() {
        User user = userService.getUserByUserName(USER_NAME).get();
        String content = "Single comment Test";
        for (int i = 0; i < 5; i++) {
            predictionService.predictSingleComment(user, Optional.empty(), content + Integer.toString(i));
        }
        List<Link> linkList = userService.getUserByObjectId(user.getId()).getLinkSearchHistory();
        Assertions.assertEquals(5, linkList.size());
        List<Comment> commentList = linkList.stream().flatMap(l -> commentService.findCommentsByLinkId(l.getLinkId()).stream()).sorted(Comparator.comparing(Comment::getBody)).collect(Collectors.toList());

        for (int i = 0; i < 5; i++) {
            Comment comment = commentList.get(i);
            Assertions.assertEquals(content + Integer.toString(i), comment.getBody());
            Assertions.assertTrue(comment.getScore() > 0);
        }
    }

    Stream<Arguments> predictWholeLinkTest() {
        return Stream.of(Arguments.arguments(new int[]{10}, new int[]{20}), Arguments.arguments(new int[]{50, 100, 20}, new int[]{100, 200, 500}), Arguments.arguments(new int[]{1, 1}, new int[]{100, 5}));
    }

    @ParameterizedTest
    @MethodSource("predictWholeLinkTest")
    void predictWholeLinkTest(int[] percentages, int[] amounts) {
        final int length = Math.min(percentages.length, amounts.length);
        User user = userService.getUserByUserName(USER_NAME).get();
        for (int i = 0; i < length; i++) {
            predictionService.predictWholeLink(user, "LINK_TEST" + (i + 1), percentages[i], amounts[i]);
        }
        List<Link> linkSearchHistory = userService.getUserByObjectId(user.getId()).getLinkSearchHistory();
        Assertions.assertEquals(length, linkSearchHistory.size(), () -> "Prediction from link wasnt saved.");

        linkSearchHistory.sort(Comparator.comparing(Link::getSearchDateTime));
        for (int i = 0; i < length; i++) {
            Link generatedPrediction = linkSearchHistory.get(i);
            Assertions.assertEquals(Integer.valueOf(percentages[i]), generatedPrediction.getPercentage(), () -> "Wrongly assigned percentage.");
            Assertions.assertEquals(Integer.valueOf(amounts[i]), generatedPrediction.getAmmount(), () -> "Wrongly assigned amount of comments");

            List<Comment> commentsByLinkId = commentService.findCommentsByLinkId(generatedPrediction.getLinkId());
            int expectedSize = Math.min(percentages[i] * amounts[i] / 100 + 1, amounts[i]);
            Assertions.assertEquals(expectedSize, commentsByLinkId.size(), () -> "Too much/not enough saved comments");

            Stream<Executable> executableStream = commentsByLinkId.stream().map(comment -> (Executable) () -> {
                Assertions.assertNotNull(comment.getCommentId(), "CommentId isn't filled.");
                Assertions.assertNotNull(comment.getLinkId(), "LinkId isnt filled.");
                Assertions.assertEquals(generatedPrediction.getLinkId(), comment.getLinkId(), () -> "Wrong linkId");
                Assertions.assertTrue(comment.getScore() > 0);
            });
            Assertions.assertAll("Test of commentsByLinkId failed.", executableStream);
        }
    }

    @Test
    void changePasswordTest() {
        User user = userService.getUserByUserName(USER_NAME).get();
        char[] oldHashedPassword = user.getPassword().toCharArray();
        userService.changePassword(user, "new_password_!1!1");
        char[] newPassword = userService.getUserByObjectId(user.getId()).getPassword().toCharArray();
        Assertions.assertFalse(Arrays.equals(oldHashedPassword, newPassword));
    }

    @Test
    void changePersonalDataTest() {
        User user = userService.getUserByUserName(USER_NAME).get();

        ChangeDataForm changeDataForm = new ChangeDataForm();
        changeDataForm.seteMail("new.email@xyz.abc");
        changeDataForm.setFirstName("first_new_name");
        changeDataForm.setLastName("last new name");
        userService.changePersonalData(user, changeDataForm);

        user = userService.getUserByObjectId(user.getId());
        Assertions.assertEquals(changeDataForm.geteMail(), user.getUserName());
        Assertions.assertEquals(changeDataForm.getFirstName(), user.getFirstName());
        Assertions.assertEquals(changeDataForm.getLastName(), user.getLastName());
    }
}
