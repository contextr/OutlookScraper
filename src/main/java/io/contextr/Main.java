package io.contextr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;

import io.contextr.model.PersistModel;
import io.contextr.utils.FileUtils;
import io.contextr.utils.HTTPUtils;

@SpringBootApplication
public class Main implements CommandLineRunner {

	HTTPUtils httpUtils = new HTTPUtils();
	FileUtils fileUtils = new FileUtils();

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args).close();
	}

	public void run(String... args) throws Exception {

//		args = new String[] { "barry_tycholiz_000_1_1.pst", "Enron", "output" };

		if (args.length != 3) {
			helpAndQuit();
		} else {

			String inputPath = args[0];
			String profile = args[1];
			String outputPath = args[2];

			List<PersistModel> list = new ArrayList<>();

			try {
				PSTFile pstFile = new PSTFile(inputPath);
				System.out.println(pstFile.getMessageStore().getDisplayName());
				processFolder(pstFile.getRootFolder(), list, profile);
			} catch (Exception err) {
				err.printStackTrace();
			}

			fileUtils.writeToFile(outputPath, new ObjectMapper().writeValueAsString(list));
		}
	}

	int depth = -1;

	String sentFolder = "sent";
	String emailTag = "\r\n\r\n***********\r\nEDRM Enron Email Data Set has been produced in EML, PST and NSF format by ZL Technologies, Inc. This Data Set is licensed under a Creative Commons Attribution 3.0 United States License <http://creativecommons.org/licenses/by/3.0/us/> . To provide attribution, please cite to \"ZL Technologies, Inc. (http://www.zlti.com).\"\r\n***********\r\n\u0000";
	String origMessage = "-----Original Message-----";
	String fowardedBy = "---------------------- Forwarded by";

	public void processFolder(PSTFolder folder, List<PersistModel> list, String profileName)
			throws PSTException, java.io.IOException {
		depth++;
		// the root folder doesn't have a display name
		if (depth > 0) {
			System.out.println(folder.getDisplayName());
		}

		// go through the folders...
		if (folder.hasSubfolders()) {
			Vector<PSTFolder> childFolders = folder.getSubFolders();

			for (PSTFolder childFolder : childFolders) {
				processFolder(childFolder, list, profileName);
			}
		}

		// and now the emails for this folder
		if (folder.getContentCount() > 0 && folder.getDisplayName().toLowerCase().contains(sentFolder)) {
			depth++;
			PSTMessage email = (PSTMessage) folder.getNextChild();
			while (email != null) {
				String body = email.getBody();
				body = body.replace(emailTag, "");
				body = body.replaceAll("\\v", "\n");
				if (body.contains(origMessage)) {
					body = body.substring(0, body.indexOf(origMessage));
				}
				if (body.contains(fowardedBy)) {
					body = body.substring(0, body.indexOf(fowardedBy));
				}
				
				while(body.contains("\n\n"))
				{
					body = body.replace("\n\n", "\n");
				}

				if (!body.isEmpty()) {
					PersistModel p = new PersistModel(profileName, body);
					if (!list.contains(p)) {
						list.add(p);
					}
				}

				email = (PSTMessage) folder.getNextChild();
			}
			depth--;
		}
		depth--;
	}

	private static void helpAndQuit() {
		System.out.println("Invalid arguments!\n" + "Arguments: <input filepath> <profile> <output filepath>\n\n"
				+ "<input filepath> \t- path to file of the stored emails\n"
				+ "<profile> \t- profile under which to file the sources\n"
				+ "<output filepath> \t- path to file to write to\n");
		System.exit(-1);
	}

}
