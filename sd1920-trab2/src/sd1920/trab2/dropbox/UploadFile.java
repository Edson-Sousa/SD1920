package sd1920.trab2.dropbox;

import java.io.IOException;

import org.pac4j.scribe.builder.api.DropboxApi20;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;

import sd1920.trab2.dropbox.arguments.UploadFileArgs;

public class UploadFile {

	private static final String apiKey = "oanhgaq1rbczej5";
	private static final String apiSecret = "ppqs2hygopipw2i";
	private static final String accessTokenStr = "6NNhCP3syewAAAAAAAADPE6mm8b-AciraA4j23uulYCWRr61F74uhnWzWmzNGR2W";

//	protected static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
	
	protected static final String OCTET_CONTENT_TYPE = "application/octet-stream";

	private static final String UPLOAD_FILE_URL = "https://content.dropboxapi.com/2/files/upload";

	private OAuth20Service service;
	private OAuth2AccessToken accessToken;

	private Gson json;

	public UploadFile() {
		service = new ServiceBuilder(apiKey).apiSecret(apiSecret).build(DropboxApi20.INSTANCE);
		accessToken = new OAuth2AccessToken(accessTokenStr);

		json = new Gson();
	}

	public boolean execute( String filename ) {
		OAuthRequest uploadFile = new OAuthRequest(Verb.POST,UPLOAD_FILE_URL);
		uploadFile.addHeader("Dropbox-API-Arg", new UploadFileArgs(filename, "add", false, true, false).toString());
		uploadFile.addHeader("Content-Type", OCTET_CONTENT_TYPE);

		uploadFile.setPayload( );

		service.signRequest(accessToken, uploadFile);

		Response r = null;

		try {
			r = service.execute(uploadFile);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (r.getCode() == 200) {
			return true;
		} else {
			System.err.println("HTTP Error Code: " + r.getCode() + ": " + r.getMessage());
			try {
				System.err.println(r.getBody());
			} catch (IOException e) {
				System.err.println("No body in the response");
			}
			return false;
		}

	}

}
