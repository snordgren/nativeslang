package com.northerndroid.nativeslang;

import com.northerndroid.nativeslang.model.Comment;
import com.northerndroid.nativeslang.model.Post;
import com.northerndroid.nativeslang.model.User;
import com.northerndroid.nativeslang.sql.Table;
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
	private final Table comment, post, user;

	private Database(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);

		comment = table("comment",
				bigint("id").primaryKey(),
				bigint("post_id"),
				bigint("user_id"),
				varchar("text", 1024 * 5));
		post = table("post",
				bigint("id").primaryKey(),
				bigint("poster"),
				varchar("language", 128),
				varchar("title", 1024),
				varchar("description", 1024 * 10));
		user = table("user",
				bigint("id").primaryKey(),
				varchar("username", 256),
				varchar("password", 256));

		template.update(comment.create());
		template.update(post.create());
		template.update(user.create());
	}

	private boolean comparePassword(String hash, String password) {
		try {
			return PasswordStorage.verifyPassword(password, hash);
		} catch (Exception e) {
			throw new RuntimeException(e);
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
			template.update(post.insert(
					into("poster", posterId),
					into("language", language),
					into("title", sanitizedTitle),
					into("description", sanitizedDescription)));
		}
	}

	public void createUser(String username, String password) {
		if (username.matches(USERNAME_PATTERN)) {
			template.update(user.insert(
					into("username", username),
					into("password", encryptPassword(password))));
		}
	}

	private String encryptPassword(String password) {
		try {
			return PasswordStorage.createHash(password);
		} catch (PasswordStorage.CannotPerformOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Comment> getComments(Post post) {
		String query = comment.selectAll().where(isEqual("post_id", post.getId()));
		return template.query(query, commentMapper);
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
		return template.query(query, postMapper);
	}

	public User getUser(long id) {
		String query = user.selectAll().where(isEqual("id", id));
		return template.queryForObject(query, userMapper);
	}

	public User getUser(String username) {
		String query = user.selectAll().where(isEqual("username", username));
		return template.queryForObject(query, userMapper);
	}

	public boolean hasUser(String username) {
		return template.queryForObject(
				user.selectCountWhere(isEqual("username", username)),
				Integer.class) > 0;
	}

	public boolean hasPost(long id) {
		return template.queryForObject(
				post.selectCountWhere(isEqual("id", id)),
				Integer.class) > 0;
	}

	public boolean hasPost(String language, long id) {
		return template.queryForObject(
				post.selectCountWhere(
						isEqual("language", language),
						isEqual("id", id)),
				Integer.class) > 0;
	}

	public boolean isLoginValid(String username, String password) {
		if (hasUser(username)) {
			User user = getUser(username);
			return comparePassword(user.getPassword(), password);
		} else {
			return false;
		}
	}

	public boolean isUsernameAvailable(String username) {
		return template.queryForObject(user.selectCountWhere(
				isEqual("username", username)),
				Integer.class) < 1;
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
