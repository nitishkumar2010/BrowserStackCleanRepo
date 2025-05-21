
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Class to read result file and prepare mail body
 * 
 * @author nikumar
 *
 */
public class Testing1 {


	public static void main(String[] args) {

		String buildNum = "1";
		String path = "C://Users///nikumar//.jenkins//jobs//BRPExecution//workspace//BRPAutomation" + File.separator + "test-output" + File.separator
				+ "jenkins-BRPExecution-" + buildNum.trim() + File.separator + "html" + File.separator;
		System.out.println(path);
		StringBuilder contentBuilder = new StringBuilder();
		try {
			System.out.println(path + "suites.html");
			BufferedReader in = new BufferedReader(new FileReader(path + "suites.html"));
			String str;
			while ((str = in.readLine()) != null) {
				contentBuilder.append(str);
			}
			in.close();
		} catch (IOException e) {
		}

		String content = contentBuilder.toString();

		Document doc = Jsoup.parse(content);
		Elements p = doc.select("td a");

		System.out.println("--------------> " + "Getting all the suites link");
		for (int i = 0; i < p.size(); i++) {
			System.out.println("Link attr ---> " + p.get(i).attr("href"));
			StringBuilder contentBuilder_suite = new StringBuilder();
			try {
				System.out.println(p.get(i).attr("href"));
				BufferedReader in = new BufferedReader(new FileReader(path + p.get(i).attr("href")));
				String str;
				while ((str = in.readLine()) != null) {
					contentBuilder_suite.append(str);
				}
				in.close();
			} catch (IOException e) {
			}
		}
	}

}
