package com.yash.coursera.integration.helper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.yash.coursera.integration.model.EmailContents;

@Component
public class EmailBodyUtil {

	public String settingEmailBody(EmailContents emailContents, Map<String, String> userRefWithError) {

		String html = "<!DOCTYPE html>" + "<html>" + "<head>" + "<title>" + "Dear Admin, \n\n"
				+ emailContents.getMailSubject() + "</title>" + "</head>" + "<body>" + "<h4>Dear Admin, </h4>"
				+ "<p>Please find SFTP User Invitation Details</p>"
				+ "<h4>Details of submitted users for each programs :</h4>" 
				+ "<table border='1'>" + "<tr>"
				+ "<th>Date</th>" + "<th>Total Invitation</th>"
				+ "<th>Successfull Invitation</th>" + "<th>Failed Invitation</th>" + "</tr>" + "<tr>"
				+ "<td align='center'>" + emailContents.getCurrentDate() + "</td>"
				+ "<td align='center'>"+emailContents.getTotalInvitationCount() +"</td>" 
				+ "<td align='center'>"+ emailContents.getSuccessInvitationCount() + "</td>" 
				+ "<td align='center'>"+ emailContents.getFailedInvitationCount() + "</td>" + "</tr>" + "</table>";
				if (!userRefWithError.isEmpty()) {
					html += "<h4>Invalid Submission User Details :</h4>" + "<table border='1'>" + "<tr>"
							+ "<th>Employee Id</th>" + "<th>Error for failure</th>";
		
					for (Map.Entry<String, String> entry : userRefWithError.entrySet()) {
						html += "<tr><td align='center'>" + entry.getKey() + "</td >";
						html += "<td align='center'>" + entry.getValue() + "</td ></tr>";
					}
					html += "</table>";
				}
				html += "</body>" + "</html>";
		return html;
	}
}
