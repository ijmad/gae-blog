package ian.blog;

import ian.blog.entity.AuthorObject;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;


public class UserUtil {
    /**
     * Get or create a user object for the current logged in user.
     */
    public static AuthorObject getAuthor() {
        AuthorObject author = null;
        
        User user = UserServiceFactory.getUserService().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            
            try {
                author = new AuthorObject(AuthorObject.key(email));
            }
            catch (EntityNotFoundException enfe) {
            }
            
            // ensure Author exists for this user
            if (author == null) {
                author = new AuthorObject(email);
                author.save();
            }
        }
        
        return author;
    }
}
