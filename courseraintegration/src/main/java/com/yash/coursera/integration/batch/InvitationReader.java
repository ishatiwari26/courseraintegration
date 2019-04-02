package com.yash.coursera.integration.batch;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yash.coursera.integration.model.ApiResponse;
import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.User;

@Component
public class InvitationReader implements ItemReader<Elements> {

	private String apiUrl;;
	private String requestMethod;
	private ApiResponse apiResponse;
	private Integer count = 0;

	public InvitationReader(String apiUrl, String requestMethod) {
		this.apiUrl = apiUrl;
		this.requestMethod = requestMethod;
	}

	public InvitationReader() {
	}

	@Override
	public Elements read() throws IOException {

		System.out.println("inside invitation reader");

		User invitedUser = null;

		if (!CollectionUtils.isEmpty(inviteUserList()) && count < inviteUserList().size()) {
			invitedUser = inviteUserList().get(count);
			count++;
		} else {
			return null;
		}

		StringBuffer response = new StringBuffer();

		HttpURLConnection con = callInviteAPI(invitedUser);

		if (con.getResponseCode() == 201) {
			Reader streamReader = new InputStreamReader(con.getInputStream());
			BufferedReader in = new BufferedReader(streamReader);
			String tempResponse = in.readLine();
			while (tempResponse != null) {
				response.append(tempResponse);
				tempResponse = in.readLine();
			}
			apiResponse = new ObjectMapper().readValue(response.toString(), ApiResponse.class);
			in.close();
		} else {
			throw new RuntimeException("user already exist. send invite to unique user");
		}

		Elements elem = null;
		try {
			List<Element> list = apiResponse.getElements();
			if (!CollectionUtils.isEmpty(list)) {
				elem = new Elements();
				elem.setElement(list);
			}

		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
		return elem;
	}

	public HttpURLConnection callInviteAPI(User user) throws IOException {

		URL url = new URL(apiUrl + "/" + getProgramId());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(requestMethod);

		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");

		ObjectMapper mapper = new ObjectMapper();

		DataOutputStream postUserInvite = new DataOutputStream(con.getOutputStream());
		postUserInvite.writeBytes(mapper.writeValueAsString(user));

		postUserInvite.flush();
		postUserInvite.close();

		return con;

	}

	public List<User> inviteUserList() {
		List<User> inviteUsersList = new ArrayList<>();

		User user1 = new User();
		user1.setExternalId("41386190311");
		user1.setFullName("user123");
		user1.setEmail("abc123@domain.com");

		User user2 = new User();
		user2.setExternalId("41386191156");
		user2.setFullName("user2456");
		user2.setEmail("xyz123@domain.com");

		inviteUsersList.add(user1);
		inviteUsersList.add(user2);

		return inviteUsersList;

	}

	public String getProgramId() {
		String programId = "Q0Wzd5osEei1PwqN7iH8Jg";
		return programId;
	}

}