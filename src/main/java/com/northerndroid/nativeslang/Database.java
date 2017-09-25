package com.northerndroid.nativeslang;

import com.northerndroid.nativeslang.model.Comment;
import com.northerndroid.nativeslang.model.Post;
import com.northerndroid.nativeslang.model.User;
import com.northerndroid.nativeslang.sql.Table;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.northerndroid.nativeslang.sql.SQLBuilder.*;

public class Database {
	public static final String USERNAME_PATTERN = "[a-zA-Z0-9]+";
	private final RowMapper<Comment> commentMapper = (resultSet, rowNum) -> new Comment(
			resultSet.getLong("id"),
			getPost(resultSet.getLong("post_id")),
			getUser(resultSet.getLong("user_id")),
			unescape(resultSet.getString("text")));
	private final RowMapper<Post> postMapper = (resultSet, rowNum) -> new Post(
			resultSet.getLong("id"),
			getUser(resultSet.getLong("poster")),
			resultSet.getString("language"),
			unescape(resultSet.getString("title")),
			unescape(resultSet.getString("description")));
	private final RowMapper<User> userMapper = (rs, rowNum) -> new User(
			rs.getLong("id"),
			rs.getString("username"),
			rs.getString("password"));

	private final JdbcTemplate template;
	private final Table comment,
			hiddenComment,
			hiddenPost,
			post,
			superUser,
			user,
			userDescription;

	private Database(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);

		comment = table("comment",
				bigint("id").primaryKey(),
				bigint("post_id"),
				bigint("user_id"),
				varchar("text", 1024 * 5));
		hiddenComment = table("hidden_comment",
				bigint("comment_id"));
		hiddenPost = table("hidden_post",
				bigint("post_id"));
		post = table("post",
				bigint("id").primaryKey(),
				bigint("poster"),
				varchar("language", 128),
				varchar("title", 1024),
				varchar("description", 1024 * 10));
		superUser = table("super_user",
				bigint("user_id"));
		user = table("user",
				bigint("id").primaryKey(),
				varchar("username", 256),
				varchar("normalized_username", 256),
				varchar("password", 256));
		userDescription = table("user_description",
				bigint("id").primaryKey(),
				bigint("user_id"),
				varchar("description", 1024 * 16));

		template.update(comment.create());
		template.update(hiddenComment.create());
		template.update(hiddenPost.create());
		template.update(post.create());
		template.update(superUser.create());
		template.update(user.create());
		template.update(userDescription.create());

		createAdmins();
	}

	private boolean comparePassword(String hash, String password) {
		try {
			return PasswordStorage.verifyPassword(password, hash);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createAdmins() {
		try {
			String source = IOUtils.toString(getClass()
					.getClassLoader()
					.getResourceAsStream("Admins.csv"), Charset.forName("UTF-8"));
			String[] lines = source.split("\n");
			for (String line : lines) {
				String[] parts = line.split(",");
				String username = parts[0];
				String password = parts[1];
				if (!hasUser(username)) {
					createUser(username, password);
				}
				if (!isSuperUser(username)) {
					createSuperUser(username);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createComment(Post post,
			User user,
			String text) {
		String sanitizedText = sanitize(text);
		if (!sanitizedText.isEmpty()) {
			template.update(comment.insert(
					into("post_id", post.getId()),
					into("user_id", user.getId()),
					into("text", sanitizedText)));
		}
	}

	public void createHiddenComment(long commentId) {
		template.update(hiddenComment.insert(into("comment_id", commentId)));
	}

	public void createHiddenPost(long postId) {
		template.update(hiddenPost.insert(into("post_id", postId)));
	}

	public void createPost(String language,
			String username,
			String title,
			String description) {
		long posterId = template.queryForObject(
				user.select("id").where(isEqual("username", username)),
				Long.class);
		String sanitizedTitle = sanitize(title);
		String sanitizedDescription = sanitize(description);
		if (!sanitizedDescription.isEmpty() && !sanitizedTitle.isEmpty()) {
			if (!hasPost(posterId, language, sanitizedTitle, sanitizedDescription)) {
				template.update(post.insert(
						into("poster", posterId),
						into("language", language),
						into("title", sanitizedTitle),
						into("description", sanitizedDescription)));
			}
		}
	}

	public void createSuperUser(String username) {
		User user = getUser(username);
		template.update(superUser.insert(into("user_id", user.getId())));
	}

	public void createUser(String username, String password) {
		if (username.matches(USERNAME_PATTERN)) {
			template.update(user.insert(
					into("username", username),
					into("normalized_username", User.normalize(username)),
					into("password", encryptPassword(password))));
		}
	}

	public void createUserDescription(long userId, String description) {
		template.update(userDescription.insert(
				into("user_id", userId),
				into("description", description)));
	}

	private String encryptPassword(String password) {
		try {
			return PasswordStorage.createHash(password);
		} catch (PasswordStorage.CannotPerformOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public int getCommentCount(long postId) {
		return getComments(postId).size();
	}

	public List<Comment> getComments(long postId) {
		String query = comment.selectAll().where(isEqual("post_id", postId));
		List<Comment> results = template.query(query, commentMapper);
		results.removeIf(a -> isCommentHidden(a.getId()));
		return results;
	}

	public Post getPost(String language, long id) {
		String query = post
				.selectAll()
				.where(isEqual("language", language), isEqual("id", id));
		List<Post> result = template.query(query, postMapper);
		if (result.size() == 0) {
			throw new UnsupportedOperationException();
		}
		return result.get(0);
	}

	public Post getPost(long id) {
		String query = post.selectAll().where(isEqual("id", id));
		return template.queryForObject(query, postMapper);
	}

	public List<Post> getPostsByLanguage(String language) {
		String query = post
				.selectAll()
				.orderBy(descending("id"))
				.where(isEqual("language", language));
		List<Post> result = template.query(query, postMapper);
		result.removeIf(a -> isPostHidden(a.getId()));
		return result;
	}

	public User getUser(long id) {
		String query = user.selectAll().where(isEqual("id", id));
		return template.queryForObject(query, userMapper);
	}

	public User getUser(String username) {
		String query = user.selectAll().where(
				isEqual("normalized_username", User.normalize(username)));
		return template.queryForObject(query, userMapper);
	}

	public String getUserDescription(long userId) {
		String query = userDescription.selectTop(1, "description")
				.orderBy(descending("id"))
				.where(isEqual("user_id", userId));
		return template.queryForObject(query,
				(results, row) -> results.getString("description"));
	}

	public List<User> getUserList() {
		return template.query("select * from user", userMapper);
	}

	private boolean has(String sql) {
		return template.queryForObject(sql, Integer.class) > 0;
	}

	public boolean hasComment(long id) {
		return has(comment.selectCountWhere(isEqual("id", id)));
	}

	public boolean hasPost(long id) {
		return has(post.selectCountWhere(isEqual("id", id)));
	}

	public boolean hasPost(String language, long id) {
		return has(post.selectCountWhere(
				isEqual("language", language),
				isEqual("id", id)));
	}

	public boolean hasPost(long poster, String language, String title, String desc) {
		return has(post.selectCountWhere(
				isEqual("poster", poster),
				isEqual("language", language),
				isEqual("title", title),
				isEqual("description", desc)));
	}

	public boolean hasUser(String username) {
		return has(user.selectCountWhere(
				isEqual("normalized_username", User.normalize(username))));
	}

	public boolean hasUserDescription(long userId) {
		return has(userDescription.selectCountWhere(
				isEqual("user_id", userId)));
	}

	public boolean isCommentHidden(long comment) {
		return has(hiddenComment.selectCountWhere(isEqual("comment_id", comment)));
	}

	public boolean isLoginValid(String username, String password) {
		if (hasUser(username)) {
			User user = getUser(username);
			return comparePassword(user.getPassword(), password);
		} else {
			return false;
		}
	}

	public boolean isPostHidden(long post) {
		return has(hiddenPost.selectCountWhere(isEqual("post_id", post)));
	}

	public boolean isSuperUser(String username) {
		User user = getUser(username);
		return has(superUser.selectCountWhere(
				isEqual("user_id", user.getId())));
	}

	private String sanitize(String input) {
		return Jsoup.clean(input, Whitelist.basic())
				.replace("'", "&apos;")
				.replace("\"", "&quot;");
	}

	private String unescape(String input) {
		return StringEscapeUtils.unescapeXml(input);
	}

	public static Database newInFile(String file) {
		String url = "jdbc:hsqldb:file:" + file;
		DriverManagerDataSource dataSource = new DriverManagerDataSource(url);
		return new Database(dataSource);
	}

	public static Database newInMemory() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase database = builder
				.generateUniqueName(true)
				.setType(EmbeddedDatabaseType.HSQL)
				.build();
		return new Database(database);
	}
}
