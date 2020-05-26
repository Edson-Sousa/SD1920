package sd1920.trab2.dropbox;

import java.util.Scanner;

import org.pac4j.scribe.builder.api.DropboxApi20;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;

import sd1920.trab2.dropbox.arguments.DownloadFileArgs;
import sd1920.trab2.dropbox.replies.DownloadFileReturn;

public class DownloadFile {

	private static final String apiKey = "oanhgaq1rbczej5";
	private static final String apiSecret = "ppqs2hygopipw2i";
	private static final String accessTokenStr = "6NNhCP3syewAAAAAAAADPE6mm8b-AciraA4j23uulYCWRr61F74uhnWzWmzNGR2W";

	protected static final String OCTET_CONTENT_TYPE = "application/octet-stream; charset=utf-8";

	protected static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

	private static final String DOWNLOAD_FILE_URL = "https://content.dropboxapi.com/2/files/download";

	private OAuth20Service service;
	private OAuth2AccessToken accessToken;

	private Gson json;

	public DownloadFile() {
		service = new ServiceBuilder(apiKey).apiSecret(apiSecret).build(DropboxApi20.INSTANCE);
		accessToken = new OAuth2AccessToken(accessTokenStr);

		json = new Gson();
	}

	public String execute( String filename ) {
		String fileContent = null;
		
		OAuthRequest downloadFile = new OAuthRequest(Verb.POST, DOWNLOAD_FILE_URL);
		downloadFile.addHeader("Content-Type", OCTET_CONTENT_TYPE);
		downloadFile.addHeader("Dropbox-API-Arg", json.toJson(new DownloadFileArgs(filename)));

		service.signRequest(accessToken, downloadFile);

		Response r = null;

		try {
			r = service.execute(downloadFile);
			if(r.getCode() != 200) {
				System.err.println("Failed to download file '" + filename + "'.Status " + r.getCode() + ": " + r.getMessage());
				System.err.println(r.getBody());
				return null;
			}
			
			DownloadFileReturn reply = json.fromJson(r.getBody(), DownloadFileReturn.class);
			fileContent = reply.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return fileContent;
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		DownloadFile df = new DownloadFile();
		
		System.out.println("Provide the name of the file to be downloaded:");
		String nameOfFile = sc.nextLine().trim();
		sc.close();
		String success = df.execute(nameOfFile);
		if (success != null)
			System.out.println("File '" + nameOfFile + "' downloaded successfully.");
		else
			System.out.println("Failed to download file '" + nameOfFile + "'");
	}

}
