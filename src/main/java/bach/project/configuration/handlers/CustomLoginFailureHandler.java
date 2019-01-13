package bach.project.configuration.handlers;

import bach.project.bean.enums.ErrorCode;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        int errorCode;
        if(exception instanceof BadCredentialsException){
            errorCode= ErrorCode.WRONG_PASSWORD.getStatus();
        }else if(exception instanceof AccountStatusException){
            errorCode= ErrorCode.NOT_ACTIVATED.getStatus();
        }else {
            errorCode=ErrorCode.WRONG_USERNAME.getStatus();
        }
        String username=request.getParameter("username");
        String url="/sign_in?error="+Integer.toString(errorCode);
        url+=username!=null? ("&user="+username) : "" ;
        getRedirectStrategy().sendRedirect(request,response,url);
    }
}
