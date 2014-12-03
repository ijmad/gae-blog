function BlogView() {
	var self = this;

	this.showLogin = function() {
		$('#login').show();
	};
	
	this.hideLogin = function() {
		$('#login').hide();
	};
	
	this.showUser = function(email) {
		$('header#user #email').html(email);
		$('header#user #email').show();
		$('header#user').show();
	};
	
	this.hideUser = function() {
		$('header#user').hide();
	};
	
	this.showPostBox = function() {
		$('nav').show();
		$('.showing').removeClass('showing');
		$('#postbox').show();
		$('#linkNewPost').addClass('showing');
	};
	
	this.showAllPosts = function(fetch) {
		$('nav').show();
		$('.showing').removeClass('showing');
		fetch(function(results) {
			self.showPosts(results);
			$('#linkAllPosts').addClass('showing');
		});
	};
	
	this.showMyPosts = function(fetch) {
		$('nav').show();
		$('.showing').removeClass('showing');
		fetch(function(results) {
			self.showPosts(results);
			$('#linkMyPosts').addClass('showing');
		});
	};
	
	this.showSinglePost = function(fetch) {
		$('nav').show();
		$('.showing').removeClass('showing');
		fetch(function(posts, comments) {
			self.showPosts(posts);
			self.showComments(comments);
		});
	};
	
	this.showPosts = function(results) {
		posts = $('section#posts');
		posts.empty();
		if (results.length > 0) {
			for (var i = 0; i < results.length; i++) {
				posts.append(self.renderPost(results[i]));
			}
		}
		else {
			posts.append('<article><header>No Posts Found</header>Why not make one?</article>')
		}
		posts.show();
	};
	
	this.renderPost = function(result) {
		// build article markup
		article = $('<article />');
		
		header = $('<header />');
		header.append(result.title);
		header.appendTo(article);
		
		article.append(result.content);
		
		footer = $('<footer />');
		footer.append(result.author + " &bull; " + self.renderDate(result.timestamp) +
		    " &bull; <a href=\"javascript:controller.openSinglePost('" + result.id + "');\">" + result.comments + " comments</a>");
		footer.appendTo(article);
		
		return article;
	};
	
	this.showComments = function(results) {
		comments = $('section#comments');
		container = comments.find('section#comments_container');
		container.empty();
		if (results.length > 0) {
			for (var i = 0; i < results.length; i++) {
				container.append(self.renderComment(results[i]));
			}
		}
		else {
			container.append('<article><header>No comments so far...</header>Why not write one?</article>');
		}
		comments.show();
	}
	
	this.renderComment = function(result) {
		// build article markup
		article = $('<article />');
		
		header = $('<header />');
		header.append(result.author + " commented at " + self.renderDate(result.timestamp) + ":");
		header.appendTo(article);
		
		article.append(result.comment);
		
		return article;
	};
	
	this.renderDate = function(epoch) {
		var pad = function(num, pad) {
			var str = ""+num;
			return pad.substring(0, pad.length - str.length) + str
		};
		
		var date = new Date(epoch);
		return date.getFullYear() + '-' + pad(date.getMonth() + 1, '00') + '-' + pad(date.getDate(), '00') + 
			' ' + pad(date.getHours(), '00') + ':' + pad(date.getMinutes(), '00');
	};
	
	this.hideBlog = function() {
		$('section#posts').hide();
		$('section#comments').hide();
		$('nav').hide();
		$('#posts').hide();
		$('#posts').empty();
		$('#postbox').hide();
	};
	
	this.showLoader = function() {
		$('#loader').show();
	};;
	
	this.hideLoader = function() {
		$('#loader').hide();
	};
}


function BlogModel() {
	var self = this;
	
	this.getUser = function(callback) {
		$.get('/api/v1/auth', function(result) {
			if (result.auth) {
				callback(result.user);
			}
			else {
				callback(null);
			}
		});
	};
	
	this.getPosts = function(options, callback) {
		$.get('/api/v1/posts', options, callback);
	};
	
	this.getComments = function(options, callback) {
		$.get('/api/v1/comments', options, callback);
	};
	
	this.submitPost = function(data, callback) {
		var posting = $.post('/api/v1/posts', data);
		posting.done(callback);
	}
	
	this.submitComment = function(data, callback) {
		var posting = $.post('/api/v1/comments', data);
		posting.done(callback);
	}
}


function BlogController(model, view) {
	var self = this;
	
	this.start = function() {
		model.getUser(function(user) {
			if (user) {
				view.showUser(user)
				self.openView(function() {
					view.hideLoader();
				});
			}
			else {
				view.showLogin();
				view.hideLoader();
			}
		});
	};
	
	this.reload = function() {
		view.showLoader();
		view.hideBlog();
		self.openView(function() {
			view.hideLoader();
		});
	};
	
	this.openView = function(callback) {
		var hash = document.location.hash;
		
		if (hash == '#postbox') {
			view.showPostBox();
			callback();
		}
		else if (hash.match(/^\#post\:/)) {
			view.showSinglePost(function(render) {
				id = hash.substring(6);
				model.getPosts({filter: 'single', id: id}, function(posts) {
					model.getComments({id: id}, function(comments) {
						render(posts, comments);
						callback();
					});
				});
			});
		}
		else if (hash == '#all') {
			view.showAllPosts(function(render) {
				model.getPosts({filter: 'all'}, function(results) {
					render(results);
					callback();
				});
			});
		}
		else /*if (hash == '#mine')*/ {
			view.showMyPosts(function(render) {
				model.getPosts({filter: 'mine'}, function(results) {
					render(results);
					callback();
				});
			});
		}
	}
	
	this.openMyPosts = function() {
		document.location.hash = "mine";
		self.reload();
	};
	
	this.openAllPosts = function() {
		document.location.hash = "all";
		self.reload();
	};
	
	this.openPostBox = function() {
		document.location.hash = "postbox";
		self.reload();
	};
	
	this.openSinglePost = function(id) {
		document.location.hash = "post:" + id;
		self.reload();
	}
	
	this.submitPost = function() {
		event.preventDefault();
		
	    var title = $('#postbox_form input#title').val();
	    var content = $('#postbox_form textarea#content').val();
	    
	    if (!title) {
	    	alert('Please enter a title!');
	    	return;
	    }
	    
	    if (!content) {
	    	alert('Please enter some content!');
	    	return;
	    }
	    
	    view.showLoader();
		model.submitPost({ title: title, content: content }, function(result) {
			self.openMyPosts();
		});
	};
	
	this.submitComment = function() {
		event.preventDefault();
		
	    var comment = $('#comment_form textarea#comment').val();
	    
	    if (!comment) {
	    	alert('Please enter a comment!');
	    	return;
	    }
	    
	    id = document.location.hash.substring(6);
	    
	    view.showLoader();
		model.submitComment({ id: id, comment: comment }, function(result) {
			model.getComments({ id: id }, function(comments) {
				view.showComments(comments);
				view.hideLoader();
			});
		});
	};
}
