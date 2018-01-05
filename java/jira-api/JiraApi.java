package 

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.apache.poi.util.IOUtils;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.kobie.tr1.util.Settings;

public class JiraApi {
	private static URI jiraServerUri = null;

	static {
		try {
			jiraServerUri = new URI(Settings.get("jira.url.base"));
		} catch (URISyntaxException e) {
			System.out.println("Could not understand the JIRA Base URL: " + e.getMessage());
		}
	}

	public static JiraRestClient getClient() {
		JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		return factory.createWithBasicHttpAuthentication(jiraServerUri, Settings.get("jira.username"),
				Settings.get("jira.password"));
	}

	public static Iterable<Issue> getMyOpenIssues() {
		SearchRestClient c = JiraApi.getClient().getSearchClient();
		Promise<SearchResult> p = c.searchJql("assignee=" + Settings.get("jira.username"));

		return p.claim().getIssues();
	}

	private static IssueRestClient getIssueClient() {
		return JiraApi.getClient().getIssueClient();
	}

	private static Issue getIssueInfo(String ticketNumber) {
		IssueRestClient issueClient = getIssueClient();
		return issueClient.getIssue(ticketNumber).claim();
	}

	public static void getAttachment(String ticketNumber, String attachmentName)
			throws IOException, InterruptedException, ExecutionException {
		IssueRestClient issueClient = getIssueClient();
		Issue issue = getIssueInfo(ticketNumber);
		ArrayList<Attachment> attachmentsWithCorrectName = new ArrayList<Attachment>();
		SortedMap<DateTime, Attachment> sortedAttachments = new TreeMap<DateTime, Attachment>(
				new Comparator<DateTime>() {

					@Override
					public int compare(DateTime d1, DateTime d2) {
						return d2.compareTo(d1);
					}
				});

		for (Attachment a : issue.getAttachments()) {
			if (a.getFilename().toUpperCase().contains(attachmentName.toUpperCase()))
				attachmentsWithCorrectName.add(a);
		}

		for (Attachment a : attachmentsWithCorrectName) {
			sortedAttachments.put(a.getCreationDate(), a);
		}

		if (sortedAttachments.isEmpty()) {
			return;
		}

		Path path = Paths.get(Settings.get("main.file.directory") + issue.getKey());
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File targetFile = new File(Settings.get("main.file.directory") + issue.getKey() + "/"
				+ sortedAttachments.get(sortedAttachments.firstKey()).getFilename());
		try (InputStream inputStream = issueClient
				.getAttachment(sortedAttachments.get(sortedAttachments.firstKey()).getContentUri()).claim();
				OutputStream outputStream = new FileOutputStream(targetFile)) {

			IOUtils.copy(inputStream, outputStream);
		}
	}

	public static void postCampaignAttachment(String ticketNumber, String fileType) {
		IssueRestClient issueClient = getIssueClient();
		Issue issue = getIssueInfo(ticketNumber);
		String extension;
		
		if (fileType == "createCampaign") {
			extension = ".sql";
		} else {
			extension = ".xls";
		}
		
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(Settings.get("main.file.directory") + ticketNumber + "/" + fileType
					+ ticketNumber.replaceAll("[^0-9]","") + extension);
			Promise<Void> promise = issueClient.addAttachment(issue.getAttachmentsUri(), inputStream,
					fileType + ticketNumber.replaceAll("[^0-9]","") + extension);
			promise.claim();

			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void postComment(String ticketNumber, String comment) {
		IssueRestClient issueClient = getIssueClient();
		Issue issue = getIssueInfo(ticketNumber);
		Promise<Void> promise = issueClient.addComment(issue.getCommentsUri(),
				new Comment(null, comment, null, null, null, null, null, null));
		promise.claim();
	}

	public static User getUser(String userName) {
		return JiraApi.getClient().getUserClient().getUser(userName).claim();
	}

	public static void assignTicket(String ticketNumber, String userName) {
		IssueRestClient issueClient = getIssueClient();
		FieldInput fieldInput = new IssueInputBuilder().setAssignee(getUser(userName)).build().getField("assignee");
		HashMap<String, FieldInput> map = new HashMap<>();
		map.put("assignee", fieldInput);
		IssueInput issueInput = new IssueInput(map);
		issueClient.updateIssue(ticketNumber, issueInput).claim();
	}

	private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName) {
		for (Transition transition : transitions) {
			if (transition.getName().equals(transitionName)) {
				return transition;
			}
		}
		return null;
	}
	
	public static void updateInstallInstructions(String issueKey, String installInstructions) {
		IssueInput input = IssueInput.createWithFields(new FieldInput("customfield_10010", installInstructions));
		IssueRestClient issueClient = getIssueClient();
		issueClient.updateIssue(issueKey, input);
	}

	public static void transitionSwimlanes(String ticketNumber, String swimLaneToSwitchTo) {
		IssueRestClient issueClient = getIssueClient();
		Issue issue = getIssueInfo(ticketNumber);
		Iterable<Transition> transitions = issueClient.getTransitions(issue.getTransitionsUri()).claim();
		Transition transition = getTransitionByName(transitions, swimLaneToSwitchTo);
		issueClient.transition(issue.getTransitionsUri(), new TransitionInput(transition.getId())).claim();
	}
}
