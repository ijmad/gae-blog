package ian.blog.dao;

import ian.blog.entity.AuthorObject;
import ian.blog.entity.PostObject;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class PostDao {
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    
    private final Function<Entity, PostObject> transformer = new Function<Entity, PostObject>() {
        @Override public PostObject apply(Entity entity) { return new PostObject(entity); }
    };
    
    public List<PostObject> getAll() {
        Query query = new Query(PostObject.MY_KIND)
            .addSort("timestamp", SortDirection.DESCENDING)
        ;
        
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        return Lists.transform(results, transformer);
    }
    
    public List<PostObject> getByAuthor(AuthorObject author) {
        Query query = new Query(PostObject.MY_KIND)
            .setAncestor(author.getKey())
            .addSort("timestamp", SortDirection.DESCENDING)
        ;
        
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withLimit(100));
        return Lists.transform(results, transformer);
    }
}
