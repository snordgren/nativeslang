package com.northerndroid.nativeslang;

import java.util.Scanner;

public class Application {
	public static final String[] languages = {
			"english",
			"french",
			"german",
			"italian",
			"spanish",
			"portuguese",
			"russian",
			"swedish",
			"chinese",
			"japanese",
			"korean",
	};

	private final Database database;

	public Application(Database database) {
		this.database = database;
	}

	public void run() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Command listener initialized.");
		while (scanner.hasNext()) {
			String line = scanner.nextLine().trim().replace('\n', ' ').replace('\t', ' ');
			while (line.contains("  ")) {
				line = line.replace("  ", " ");
			}
			String[] parts = line.split(" ");
			switch (parts[0]) {
				case "createHiddenComment":
					if (parts.length >= 2) {
						long comment = Long.parseLong(parts[1]);
						if (database.isCommentHidden(comment)) {
							System.out.println("That comment is already hidden.");
						} else {
							database.createHiddenComment(comment);
							System.out.println("Comment " + comment + " was hidden.");
						}
					} else {
						System.out.println("createHiddenComment [commentId]");
					}
					break;
				case "createHiddenPost":
					if (parts.length >= 2) {
						long post = Long.parseLong(parts[1]);
						if (database.isPostHidden(post)) {
							System.out.println("That post is already hidden.");
						} else {
							database.createHiddenPost(post);
							System.out.println("Post " + post + " was hidden.");
						}
					} else {
						System.out.println("createHiddenPost [postId]");
					}
					break;
				case "createSuperUser":
					if (parts.length >= 2) {
						String user = parts[1];
						if (database.isSuperUser(user)) {
							System.out.println(user + " is already a super user.");
						} else if (database.hasUser(user)) {
							database.createSuperUser(user);
							System.out.println("Created super user " + user + ".");
						} else {
							System.out.println("User " + user + " does not exist.");
						}
					} else {
						System.out.println("createSuperUser [username]");
					}
					break;
			}
		}
	}
}
