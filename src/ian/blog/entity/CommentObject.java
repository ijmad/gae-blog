package ian.blog.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

public class CommentObject extends AbstractObject {
    public static final String MY_KIND = "Comment";
    
    public static Key key(long id) {
        return KeyFactory.createKey(MY_KIND, id);
    }
    
    public CommentObject() {
        super(MY_KIND);
    }
    
    public CommentObject(Key key) throws EntityNotFoundException {
        super(key);
    }
    
    public CommentObject(Entity entity) {
        super(entity);
    }
    
    public AuthorObject getAuthor() {
        try {
            Key key = (Key)this.entity.getProperty("author");
            return (key != null) ? new AuthorObject(key) : null;
        }
        catch (EntityNotFoundException enfe) {
            return null;
        }
    }
    
    public void setAuthor(AuthorObject author) {
        this.entity.setProperty("author", author.entity.getKey());
    }
    
    public PostObject getPost() {
        try {
            Key key = (Key)this.entity.getProperty("post");
            return (key != null) ? new PostObject(key) : null;
        }
        catch (EntityNotFoundException enfe) {
            return null;
        }
    }
    
    public void setPost(PostObject post) {
        this.entity.setProperty("post", post.entity.getKey());
    }
    
    public Date getTimestamp() {
        return (Date)this.entity.getProperty("timestamp");
    }
    
    public void setTimestamp(Date date) {
        this.entity.setProperty("timestamp", date);
    }
    
    public String getComment() {
        Text text = (Text)this.entity.getProperty("comment");
        return (text != null) ? text.getValue() : null;
    }
    
    public void setComment(String comment) {
        // store as a long text object
        this.entity.setProperty("comment", new Text(comment));
    }
}
