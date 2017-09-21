package com.northerndroid.nativeslang.util;

import spark.utils.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class DesignUtil {
	public static String loadHtml(String name) {
		try {
			String path = "resources/public/html/" + name + ".html";
			return IOUtils.toString(new FileInputStream(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
