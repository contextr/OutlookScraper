package io.contextr.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class FileUtils {

	private static final Logger logger = Logger.getLogger(FileUtils.class);

	public List<String> readFile(String filePath) {
		List<String> lines = new ArrayList<>();
		File file = new File(filePath);
		if (file.exists()) {
			try {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					lines.add(sc.nextLine());
				}
				sc.close();
				return lines;
			} catch (FileNotFoundException e) {
				logger.error("File \"" + filePath + "\" failed to open!");
			}
		}
		return null;
	}

	public void writeToFile(String filePath, String contents) {
		File file = new File(filePath);

		FileWriter fw;
		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(contents);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}