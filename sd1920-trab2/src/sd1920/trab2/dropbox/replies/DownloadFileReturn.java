package sd1920.trab2.dropbox.replies;

import java.sql.Timestamp;

//import java.security.Timestamp;

public class DownloadFileReturn {
	
	private String name, id, rev;
	private Timestamp client_modified, server_modified;
	private long size;
	private boolean is_downloadable;

	/*
	public DownloadFileReturn(String name, String rev, Timestamp client_modified,
			Timestamp server_modified, long size, boolean is_downloadable) {
		this.name = name;
		this.rev = rev;
		this.client_modified = client_modified;
		this.server_modified = server_modified;
		this.size = size;
		this.is_downloadable = is_downloadable;
	}
	*/
	
	public DownloadFileReturn() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRev() {
		return rev;
	}
	
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	public Timestamp getClient_modified() {
		return client_modified;
	}
	
	public void setClient_modifified(Timestamp client_modified) {
		this.client_modified = client_modified;
	}
	
	public Timestamp getServer_modified() {
		return server_modified;
	}
	
	public void setServer_modified(Timestamp server_modified) {
		this.server_modified = server_modified;
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public boolean getDownloadable() {
		return is_downloadable;
	}

	public void setDownloadable(boolean isDownloadable) {
		this.is_downloadable = isDownloadable;
	}
	
	//Testing purpose only
	public String toString() {
		return "'name': " + name + ",\n" + "'id': " + id ;
		
	}

}
