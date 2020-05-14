import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubmissionReference;
import net.dean.jraw.references.SubredditReference;
import net.dean.jraw.tree.CommentNode;

import javax.lang.model.util.Elements;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

	static long refreshTime;
	static List<String> buyFilters;

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		//config init
		FileDialog fileChooser = new FileDialog((Frame)null, "Select Config File");
		fileChooser.setMode(FileDialog.LOAD);
		fileChooser.setVisible(true);
		String filePath = fileChooser.getDirectory()+fileChooser.getFile();
		if(filePath.equals("nullnull")){
			System.exit(-2);
		}

		//scan configs
		buyFilters = new ArrayList<>();
		Scanner scanner = new Scanner(new File(filePath));
		String username = scanner.nextLine();
		String password = scanner.nextLine();
		String botID = scanner.nextLine();
		String botSecret = scanner.nextLine();
		refreshTime = Long.parseLong(scanner.nextLine());
		String regionReq = scanner.nextLine();

		while(scanner.hasNextLine()){
			String nextline = scanner.nextLine();
			if(nextline!=null&&nextline.length()>0)
			buyFilters.add(nextline.toLowerCase());
		}
		System.out.println("buyFilters = " + buyFilters);

		//graphics init
		GComponentInterface graphics = new GUI();
		graphics.init();
		graphics.setText("Init with Keywords: \n"+buyFilters.toString());

	    //acquire authorization from webapp in reddit client (not in this file)
	    UserAgent userAgent;
		Credentials credentials;
		NetworkAdapter adapter;
		RedditClient redditClient = null;
		try {
			userAgent = new UserAgent("bot", "notNecessary", "v0.0", "notNecessary");
			credentials = Credentials.script(username, password,
					botID, botSecret);
			adapter = new OkHttpNetworkAdapter(userAgent);
			redditClient = OAuthHelper.automatic(adapter, credentials);
		} catch (Exception e){
			System.exit(-1);
		}

	    SubredditReference mechMarket = redditClient.subreddit("mechmarket");
	    boolean firstTime = true;
	    while(true){
	    	if(firstTime){
	    		firstTime = false;
		    }else{
			    TimeUnit.SECONDS.sleep(refreshTime);
		    }

		    DefaultPaginator paginator = mechMarket.posts()
				    .limit(1)
				    .sorting(SubredditSort.NEW)
				    .timePeriod(TimePeriod.MONTH)
				    .build();

		    String topComment = paginator.next().toString();
		    String permaLinkHead = topComment.substring(
				    topComment.indexOf("permalink")+(String.valueOf("permalink=/r/mechmarket/comments/")).length());
		    String submissionID = permaLinkHead.split("/")[0];
		    String submissionShortName = permaLinkHead.split("/")[1];
		    String submissionURL = "https://www.reddit.com/r/mechmarket/comments/"+submissionID+"/"+submissionShortName+"/";
		    SubmissionReference submission = redditClient.submission(submissionID);
		    String submissionTitle = submission.inspect().getTitle();

		    if(graphics.getItemList()!=null&&graphics.getItemList().contains(submissionTitle)){
			    System.out.println("Same Context, Pass...");
			    continue;
		    }

		    String[] segments = submissionTitle.split("\\[H]|\\[W]");
		    if(segments.length<3){
		    	continue;//GB or IC
		    }
		    String origins = segments[0];
		    String haves = segments[1];
		    String wants = segments[2];

		    System.out.println(submissionTitle);
		    for(String thisKeyword: buyFilters){
		    	if(regionReq.length()!=0){
		    		//using region tag
				    if(origins.toLowerCase().contains(regionReq.toLowerCase())
						    && haves.toLowerCase().contains(thisKeyword)){
				    	//region tag matches and have tag matches
					    graphics.appendText(submissionTitle+"\n"+submissionURL);
					    graphics.popupLink(thisKeyword, submissionTitle, submissionURL);
					    continue;
				    }
			    }else if(haves.toLowerCase().contains(thisKeyword)){
				    //region tag matches and have tag matches
				    graphics.appendText(submissionTitle+"\n"+submissionURL);
				    graphics.popupLink(thisKeyword, submissionTitle, submissionURL);
				    continue;
		    	}

		    }
	    }



    }

}
