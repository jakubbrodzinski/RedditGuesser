package bach.project.configuration.handlers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class SignedInInterceptor extends HandlerInterceptorAdapter {
    Collection<String> excludedPaths=Arrays.asList(new String[]{"/account","/activate_account","/css","/js","/images","/fonts"});
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String reqURI=request.getRequestURI();
        //has to be fixed!
        if(excludedPaths.stream().noneMatch(s->reqURI.startsWith(s)) &&  getPrincipal()!=null)
            response.sendRedirect("/account/panel");
        return true;
    }

    private String getPrincipal() {
        String userName = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication!=null && !(authentication instanceof AnonymousAuthenticationToken)) {
            userName = authentication.getName();
        }
        return userName;
    }
}
