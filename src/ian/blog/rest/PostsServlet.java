package ian.blog.rest;

import ian.blog.UserUtil;
import ian.blog.dao.PostDao;
import ian.blog.entity.PostObject;

import java.io.IOException;
import java.util.Collections;
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
 * Restful API to serve and create blog posts
 * @author ianm
 */
public class PostsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {        
        PostDao postDao = new PostDao();
        List<PostObject> objects = null;
        
        String filter = req.getParameter("filter");
        if (filter == null || filter.equals("all")) {
            objects = postDao.getAll();
        }
        else if (filter.equals("mine")) {
            objects = postDao.getByAuthor(UserUtil.getAuthor());
        }
        else if (filter.equals("single")) {
            try {
                String postKey = req.getParameter("id");
                objects = Collections.singletonList(new PostObject(KeyFactory.stringToKey(postKey)));
            }
            catch (EntityNotFoundException enfe) {
                res.sendError(404, "Post not found");
                return;
            }
        }
        
        res.setContentType("application/json");
        JsonWriter jw = new JsonWriter(res.getWriter());
        
        try {
            jw.beginArray();
                if (objects != null) {
                    for (PostObject object : objects) {
                        jw.beginObject();
                            jw.name("id").value(KeyFactory.keyToString(object.getKey()));
                            jw.name("author").value(object.getAuthor().getEmail());
                            jw.name("timestamp").value(object.getTimestamp().getTime());
                            jw.name("title").value(object.getTitle());
                            jw.name("content").value(object.getContent());
                            jw.name("comments").value(0);
                        jw.endObject();
                    }
                }
            jw.endArray();
        }
        finally {
            jw.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        
        PostObject post = new PostObject(UserUtil.getAuthor());
        
        post.setTimestamp(new Date());
        post.setTitle(title);
        post.setComment(content);
        
        post.save();
        
        res.setContentType("application/json");
        JsonWriter jw = new JsonWriter(res.getWriter());
        
        try {
            jw.beginArray();
                jw.beginObject();
                    jw.name("id").value(KeyFactory.keyToString(post.getKey()));
                    jw.name("author").value(post.getAuthor().getEmail());
                    jw.name("timestamp").value(post.getTimestamp().getTime());
                    jw.name("title").value(post.getTitle());
                    jw.name("content").value(post.getContent());
                    jw.name("comments").value(0);
                jw.endObject();
            jw.endArray();
        }
        finally {
            jw.close();
        }
    }
}
