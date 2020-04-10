package sd1920.trab1.impl.srv.rest;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import sd1920.trab1.api.Message;
import sd1920.trab1.api.rest.MessageService;
import sd1920.trab1.discovery.Discovery;
import sd1920.trab1.impl.clt.rest.RestUserClient;

@Singleton
public class MessageRestResource implements MessageService {

	private Random randomNumberGenerator;
	
	protected final ConcurrentMap<Long, Message> allMessages;
	protected final ConcurrentMap<String, Set<Long>> userInboxs;

	private static Logger Log = Logger.getLogger(MessageRestResource.class.getName());

	public MessageRestResource() {
		this.randomNumberGenerator = new Random(System.currentTimeMillis());
		this.allMessages = new ConcurrentHashMap<Long, Message>();
		this.userInboxs = new ConcurrentHashMap<String, Set<Long>>();
	}

	@Override
	public long postMessage(String pwd, Message msg) {
		Log.info("Received request to register a new message (Sender: " + msg.getSender() + "; Subject: "+msg.getSubject()+")");
		
		//Check if message is valid, if not return HTTP CONFLICT 409
		if (msg.getSender() == null || msg.getDestination() == null || msg.getDestination().size() == 0) {
			Log.info("Message was rejected due to lack of recepients.");
			throw new WebApplicationException( Status.CONFLICT );
		}

		//Check if user and password are valid
		URI[] srvURI = Discovery.knownUrisOf("UserService");
		RestUserClient ruc = new RestUserClient(srvURI[0]);
//		Set<String> users = ruc.
//		if (userInboxs.containsKey(msg.getSender()) || msg.getSender().)
			
		return 0;
	}

	@Override
	public Message getMessage(String user, long mid, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getMessages(String user, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFromUserInbox(String user, long mid, String pwd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteMessage(String user, long mid, String pwd) {
		// TODO Auto-generated method stub
		
	}

}
