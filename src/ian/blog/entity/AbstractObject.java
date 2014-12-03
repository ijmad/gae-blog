package ian.blog.entity;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

abstract class AbstractObject {
    protected final Entity entity;
    
    AbstractObject(Key key) throws EntityNotFoundException {
        this.entity = DatastoreServiceFactory.getDatastoreService().get(key);
    }
    
    /**
     * Create a new entity with an incomplete key and parent
     */
    AbstractObject(Key parent, String kind) {
        this.entity = new Entity(kind, parent);
    }
    
    /**
     * Create a new entity with an incomplete key and no parent
     */
    AbstractObject(String kind) {
        this.entity = new Entity(kind);
    }
    
    /**
     * Create a new entity with a key comprising a kind & keyName.
     */
    AbstractObject(String kind, String keyName) {
        this.entity = new Entity(kind, keyName);
    }
    
    /**
     * Wrap an {@link Entity}.
     */
    AbstractObject(Entity entity) {
        this.entity = entity;
    }
    
    public Key getKey() {
        return this.entity.getKey();
    }
    
    public void save() {
        DatastoreServiceFactory.getDatastoreService().put(entity);
    }
}
