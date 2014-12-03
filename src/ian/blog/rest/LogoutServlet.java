package ian.blog.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This servlet logs the user out, and returns them to the home page.
 */
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService userService = UserServiceFactory.getUserService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect(userService.createLoginURL("/"));
    }
}
