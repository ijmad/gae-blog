package ian.blog.rest;

import ian.blog.UserUtil;
import ian.blog.dao.CommentDao;
import ian.blog.entity.CommentObject;
import ian.blog.entity.PostObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.stream.JsonWriter;

/**
 * Restful API to serve and create blog comments
 * @author ianm
 */
public class CommentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String postKey = req.getParameter("id");
        PostObject post = null;
        
        try {
            post = new PostObject(KeyFactory.stringToKey(postKey));
        }
        catch (EntityNotFoundException enfe) {
            res.sendError(404, "Post not found");
            return;
        }
        
        List<CommentObject> comments = new CommentDao().getCommentsForPost(post);
        
        res.setContentType("application/json");
        JsonWriter jw = new JsonWriter(res.getWriter());
        
        try {
            jw.beginArray();
            
            for (CommentObject comment : comments) {
                jw.beginObject();
                    jw.name("id").value(KeyFactory.keyToString(comment.getKey()));
                    jw.name("author").value(comment.getAuthor().getEmail());
                    jw.name("timestamp").value(comment.getTimestamp().getTime());
                    jw.name("comment").value(comment.getComment());
                    jw.name("comments").value(0);
                jw.endObject();
            }
            
            jw.endArray();
        }
        finally {
            jw.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String postKey = req.getParameter("id");
        String text = req.getParameter("comment");
        
        CommentObject comment = new CommentObject();
        
        try {
            comment.setPost(new PostObject(KeyFactory.stringToKey(postKey)));
        }
        catch (EntityNotFoundException enfe) {
            res.sendError(404, "Post not found");
            return;
        }
        
        comment.setAuthor(UserUtil.getAuthor());
        comment.setTimestamp(new Date());
        comment.setComment(text);
        
        comment.save();
        
        res.setContentType("application/json");
        JsonWriter jw = new JsonWriter(res.getWriter());
        
        try {
            jw.beginArray();
                jw.beginObject();
                    jw.name("id").value(KeyFactory.keyToString(comment.getKey()));
                    jw.name("author").value(comment.getAuthor().getEmail());
                    jw.name("timestamp").value(comment.getTimestamp().getTime());
                    jw.name("comment").value(comment.getComment());
                    jw.name("comments").value(0);
                jw.endObject();
            jw.endArray();
        }
        finally {
            jw.close();
        }
    }
}
