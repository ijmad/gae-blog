package ian.blog.rest;

import ian.blog.UserUtil;
import ian.blog.entity.AuthorObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.stream.JsonWriter;

/**
 * Returns authentication information - to allow the front end to determine
 * if the user is logged in or not, then present login options.
 */
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        AuthorObject author = UserUtil.getAuthor();
        
        res.setContentType("application/json");
        JsonWriter jw = new JsonWriter(res.getWriter());
        
        try {
            if (author != null) {
                jw.beginObject();
                jw.name("auth").value(true);
                jw.name("user").value(author.getEmail());
                jw.endObject();
            }
            else {
                jw.beginObject();
                jw.name("auth").value(false);
                jw.endObject();
            }
        }
        finally {
            jw.close();
        }
    }
}
