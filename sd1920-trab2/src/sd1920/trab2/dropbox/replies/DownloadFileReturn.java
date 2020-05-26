package sd1920.trab2.dropbox.replies;

import java.security.Timestamp;

public class DownloadFileReturn {
	
	String name, id, rev;
	Timestamp client_modified, server_modified;
	long size;
	boolean is_downloadable;

	public DownloadFileReturn(String name, String rev, Timestamp client_modified,
			Timestamp server_modified, long size, boolean is_downloadable) {
		this.name = name;
		this.rev = rev;
		this.client_modified = client_modified;
		this.server_modified = server_modified;
		this.size = size;
		this.is_downloadable = is_downloadable;
	}

}
