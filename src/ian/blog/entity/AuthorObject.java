package ian.blog.entity;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Author Kind represents someone who can write posts or comment
 */
public class AuthorObject extends AbstractObject {
    public static final String MY_KIND = "Author";
    
    public static Key key(String email) {
        return KeyFactory.createKey(MY_KIND, email);
    }
    
    public AuthorObject(Key key) throws EntityNotFoundException {
        super(key);
    }
    
    /**
     * Create new author object from email
     */
    public AuthorObject(String email) {
        super(MY_KIND, email);
    }
    
    public String getEmail() {
        return entity.getKey().getName();
    }
}