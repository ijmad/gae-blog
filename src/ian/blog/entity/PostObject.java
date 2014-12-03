package ian.blog.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * Post Kind represents a post someone has written on the blog
 */
public class PostObject extends AbstractObject {
    public static final String MY_KIND = "Post";
    
    public static Key key(AuthorObject author, long id) {
        return KeyFactory.createKey(author.entity.getKey(), MY_KIND, id);
    }
    
    /**
     * Retrieve an existing post by key
     */
    public PostObject(Key key) throws EntityNotFoundException {
        super(key);
    }
    
    /**
     * Create a new Post object
     */
    public PostObject(AuthorObject author) {
        super(author.entity.getKey(), MY_KIND);
    }
    
    /**
     * Create around a retrieved {@link Entity}
     */
    public PostObject(Entity entity) {
        super(entity);
    }
    
    public AuthorObject getAuthor() {
        try {
            return new AuthorObject(entity.getKey().getParent());
        }
        catch (EntityNotFoundException enfe) {
            return null;
        }
    }
    
    public Date getTimestamp() {
        return (Date)this.entity.getProperty("timestamp");
    }
    
    public void setTimestamp(Date date) {
        this.entity.setProperty("timestamp", date);
    }
    
    public String getTitle() {
        return (String)this.entity.getProperty("title");
    }
    
    public void setTitle(String title) {
        this.entity.setProperty("title", title);
    }
    
    public String getContent() {
        Text text = (Text)this.entity.getProperty("content");
        return (text != null) ? text.getValue() : null;
    }
    
    public void setComment(String content) {
        // store as a long text object
        this.entity.setProperty("content", new Text(content));
    }
}
