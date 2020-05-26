package sd1920.trab2.dropbox.arguments;


public class UploadFileArgs {
	
	String filename;
	String mode;
	boolean autorename;
	boolean mute;
	boolean strict_conflict;

	public UploadFileArgs(String filename, String mode, boolean autorename, boolean mute, boolean strict_conflict) {
		this.filename = filename;
		this.mode = mode;
		this.autorename = autorename;
		this.mute = mute;
		this.strict_conflict = strict_conflict;
	}

}
