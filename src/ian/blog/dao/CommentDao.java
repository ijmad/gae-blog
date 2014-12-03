package ian.blog.dao;

import ian.blog.entity.CommentObject;
import ian.blog.entity.PostObject;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class CommentDao {
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    
    private final Function<Entity, CommentObject> transformer = new Function<Entity, CommentObject>() {
        @Override public CommentObject apply(Entity entity) { return new CommentObject(entity); }
    };
    
    public List<CommentObject> getCommentsForPost(PostObject post) {
        Filter filter = new FilterPredicate("post", Query.FilterOperator.EQUAL, post.getKey());
        
        Query query = new Query(CommentObject.MY_KIND)
            .setFilter(filter)
            .addSort("timestamp", SortDirection.DESCENDING)
        ;
        
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        return Lists.transform(results, transformer);
    }
}
