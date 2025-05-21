package Reporting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Utils.Helper;
import Utils.ReadResultFolderAndPrepareMailContent;

/**
 * Class for sending email after suite gets completed
 * 
 * @author nikumar
 *
 */
public class SendReportEx2 {

	private static double passPerTotalGen;

	public static enum EmailContentType {
		HTML, NormalText, NormalTextWithAttachment
	};

	public static void main(String[] args) {
		
		String folder = ".jenkins";

		String htmlContent = "", htmlTableContent = "";

		InetAddress IP = null;
		try {
			IP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		htmlContent += "<html><body>";
		htmlContent += "Report Generated from machine <b>" + IP.getHostName() + "/" + IP.getHostAddress() + "</b>";

		String str = null;
		File file = new File(System.getProperty("user.home") + File.separator + folder + File.separator + "jobs"
				+ File.separator + "BrowserStack" + File.separator + "nextBuildNumber");
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];

			fis.read(data);
			fis.close();
			str = new String(data, "UTF-8");

		} catch (IOException e) {
			str = "2";
		}

		int buildNumber = Integer.parseInt(str.trim());
		str = String.valueOf(buildNumber - 1);
		String remoteAddress = "";
		try {
			Properties configProperty = new Properties();
			File configfile = new File(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator
					+ "Config.properties");
			FileInputStream fileIn = new FileInputStream(configfile);
			configProperty.load(fileIn);
			remoteAddress = configProperty.getProperty("RemoteAddress");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String path = "http://" + remoteAddress + ":8084/test-output/" + "jenkins-BrowserStack-" + str.trim()
		+ "/html/";
		htmlContent += "<h2><b><a href='" + path + "'>" + "Full Report" + "</a></td></table></b></h2>";

		htmlTableContent += "<p></p><table> <tr> <th>  </th> <th> TotalCases </th> <th> PassedCases </th> <th> FailedCases  </th> <th> SkippedCases </th> <th> Pass% </th> </tr>";
		System.out.println("Calling ReadResultFolderAndPrepareMailContent read file");
		htmlTableContent += ReadResultFolderAndPrepareMailContent.readFiles(str);
		System.out.println(htmlTableContent);

		htmlTableContent += getDataFromOverviewHTMLFile(str);

		htmlContent += htmlTableContent + "<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr></table>";

		FileInputStream fileIn = null;
		try {
			Properties configProperty = new Properties();
			File configfile = new File(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator
					+ "Config.properties");
			fileIn = new FileInputStream(configfile);
			configProperty.load(fileIn);
			String to = configProperty.getProperty("EmailId"), replyTo = "nikumar@ex2india.com",
					subject = passPerTotalGen + "% - Automation Report - " + configProperty.getProperty("AreaName") + " - " + configProperty.getProperty("Environment");

			System.out.println(htmlContent);
			SendMail(to, replyTo, subject, htmlContent, EmailContentType.HTML, configProperty);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method to get data from overview html file
	 * 
	 * @param buildId
	 * @return
	 */
	private static String getDataFromOverviewHTMLFile(String buildId) {
		String path = System.getProperty("user.dir") + File.separator + "test-output" + File.separator
				+ "jenkins-BrowserStack-" + buildId.trim() + File.separator + "html" + File.separator;
		System.out.println(path);
		StringBuilder contentBuilder = new StringBuilder();
		try {
			System.out.println(path + "overview.html");
			BufferedReader in = new BufferedReader(new FileReader(path + "overview.html"));
			String str;
			while ((str = in.readLine()) != null) {
				contentBuilder.append(str);
			}
			in.close();
		} catch (IOException e) {
		}

		String content = contentBuilder.toString();
		Document doc = Jsoup.parse(content);
		Elements tableElements = doc.select("table");
		Elements tableRowElements = tableElements.select(":not(thead) tr");

		Element row = tableRowElements.get(tableRowElements.size() - 1);
		Elements rowItems = row.select("td");
		String passCount = rowItems.get(1).text();
		String skipCount = rowItems.get(2).text();
		String failCount = rowItems.get(3).text();
		int total = Integer.parseInt(passCount) + Integer.parseInt(skipCount) + Integer.parseInt(failCount);

		passPerTotalGen = (Integer.parseInt(passCount) * 100.00) / total;
		DecimalFormat df = new DecimalFormat("###.##");

		try {
			passPerTotalGen = Double.parseDouble(df.format(passPerTotalGen));
		} catch (NumberFormatException e) {
			// Set default value of 0.00 in case of Number format exception
			passPerTotalGen = 0.00;
		}

		String htmlText = "<tr><td> Total </td>" + "<td align='center'>" + total + " </td>" + "<td align='center'>"
				+ passCount + " </td>" + "<td align='center'>" + failCount + " </td>" + "<td align='center'>"
				+ skipCount + " </td>" + "<td align='center'>" + passPerTotalGen + " %" + " </td></tr>";

		return htmlText;
	}

	/**
	 * This Method is used to Send Email
	 * 
	 * @param to
	 * @param replyTo
	 * @param subject
	 * @param messageContent
	 * @param emailContentType
	 * @param configProperty
	 * @return : True/False (Based on Mail has been send or not)
	 */
	public static boolean SendMail(String to, String replyTo, String subject, String messageContent,
			EmailContentType emailContentType, Properties configProperty) {

		final String username = "testautomationex2@gmail.com";
		//final String password = "ex2app@12345";
		final String password = "rfzdliqmnkckpgmr";

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(username, password);
			}
		});

		try {
			String fileName = "";
			String reportFrom = "Automation Report";
			String attachmentFilePath = System.getProperty("user.dir") + File.separator + "test-output" + File.separator
					+ "ExtentReport" + File.separator + "ExtentReport.html";
			Message message = new MimeMessage(session); // email message
			message.setFrom(new InternetAddress("ex2apptesting@gmail.com", reportFrom));
			message.setReplyTo(InternetAddress.parse(replyTo));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			switch (emailContentType) {
			case HTML:
				MimeMultipart multipart = new MimeMultipart("related");
				MimeBodyPart messageAttachment = new MimeBodyPart();
				BodyPart messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attachmentFilePath);

				messageAttachment.setDataHandler(new DataHandler(source));
				messageAttachment.setFileName("ExtentReport.html");
				messageBodyPart.setContent(messageContent, "text/html; charset=UTF-8");
				multipart.addBodyPart(messageBodyPart);
				multipart.addBodyPart(messageAttachment);

				String environment = configProperty.getProperty("Environment");
				System.out.println(environment);
				if(environment.contains("Performace")) {
					try {
						Properties environmentConfigProperty = new Properties();
						File configfile = new File(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator
								+ environment + ".properties");
						FileInputStream fileIn = new FileInputStream(configfile);
						environmentConfigProperty.load(fileIn);
						fileName = environmentConfigProperty.getProperty("FileName");
						System.out.println(fileName);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					String attachmentXLSXFile = System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + fileName;
					MimeBodyPart messageAttachmentXLSX = new MimeBodyPart();
					messageAttachmentXLSX.setFileName(fileName);
					DataSource sources = new FileDataSource(attachmentXLSXFile);
					messageAttachmentXLSX.setDataHandler(new DataHandler(sources));
					multipart.addBodyPart(messageAttachmentXLSX);

					boolean exist = checkWhetherFileIsThereOrNot(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "PerformanceStats_" + Helper.getCurrentDate("dd-MM-yyyy") + ".txt");

					if(exist && environment.contains("Performace")){
						MimeBodyPart messageSecondAttachment = new MimeBodyPart();  
						source = new FileDataSource(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "PerformanceStats_" + Helper.getCurrentDate("dd-MM-yyyy") + ".txt");
						messageSecondAttachment.setDataHandler(new DataHandler(source));  
						messageSecondAttachment.setFileName("PerformanceStats_" + Helper.getCurrentDate("dd-MM-yyyy") + ".txt");  
						multipart.addBodyPart(messageSecondAttachment);					
					}

				}

				if(environment.contains("Performance")) {
					try {
						Properties environmentConfigProperty = new Properties();
						File configfile = new File(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator
								+ environment + ".properties");
						FileInputStream fileIn = new FileInputStream(configfile);
						environmentConfigProperty.load(fileIn);
						fileName = environmentConfigProperty.getProperty("FileName");
						System.out.println(fileName);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					String attachmentXLSXFile = System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + fileName;
					MimeBodyPart messageAttachmentXLSX = new MimeBodyPart();
					messageAttachmentXLSX.setFileName(fileName);
					DataSource sources = new FileDataSource(attachmentXLSXFile);
					messageAttachmentXLSX.setDataHandler(new DataHandler(sources));
					multipart.addBodyPart(messageAttachmentXLSX);

					boolean exist = checkWhetherFileIsThereOrNot(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "PerformanceStatsDrees_" + Helper.getCurrentDate("dd-MM-yyyy") + ".txt");

					if(exist && environment.contains("Performance")){
						MimeBodyPart messageSecondAttachment = new MimeBodyPart();  
						source = new FileDataSource(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "PerformanceStatsDrees_" + Helper.getCurrentDate("dd-MM-yyyy") + ".txt");
						messageSecondAttachment.setDataHandler(new DataHandler(source));  
						messageSecondAttachment.setFileName("PerformanceStatsDrees_" + Helper.getCurrentDate("dd-MM-yyyy") + ".txt");  
						multipart.addBodyPart(messageSecondAttachment);	
					}
				}

				boolean leadFileExist = checkWhetherFileIsThereOrNot(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "Leads_" + Helper.getCurrentDate("dd-MM-yyyy") + ".xls");

				String areaName = configProperty.getProperty("AreaName");
				System.out.println(areaName);
				if(leadFileExist && areaName.contains("Leads")){
					MimeBodyPart messageSecondAttachment = new MimeBodyPart();  
					source = new FileDataSource(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "Leads_" + Helper.getCurrentDate("dd-MM-yyyy") + ".xls");
					messageSecondAttachment.setDataHandler(new DataHandler(source));  
					messageSecondAttachment.setFileName("Leads_" + Helper.getCurrentDate("dd-MM-yyyy") + ".xls");  
					multipart.addBodyPart(messageSecondAttachment);					
				}

				message.setContent(multipart);
				break;
			case NormalText:
				message.setContent(messageContent, "text/html; charset=UTF-8");
				break;
			default:
				System.out.println("Email Content type not defined");
			}


			try {
				Transport.send(message);
				System.out.println("Mail sent successfully.");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("********** This is the Message we were trying to send via Email **********\n");
				System.out.println("Subject : " + message.getSubject());
				try {
					System.out.println("Message : " + message.getContent().toString());
				} catch (IOException e1) {
				}
				return false;
			}
			
			boolean exist = checkWhetherFileIsThereOrNot(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "PerformanceStatsDrees_" + Helper.getCurrentDate("dd-MM-yyyy") + ".txt");

			if(exist){
				File file = new File(System.getProperty("user.dir") + File.separator + "Parameters" + File.separator + "PerformanceStatsDrees_" + Helper.getCurrentDate("dd-MM-yyyy") + ".txt");
				if (file.delete()) {
					System.out.println("File deleted successfully");
				}
				else {
					System.out.println("Failed to delete the file");
				}
			}

			return true;
		} catch (MessagingException | UnsupportedEncodingException mex) {
			mex.printStackTrace();
			return false;
		}
	}

	/**
	 * Method to verify sheet is already present or not
	 * 
	 * @param string
	 */
	private static boolean checkWhetherFileIsThereOrNot(String fileName) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
