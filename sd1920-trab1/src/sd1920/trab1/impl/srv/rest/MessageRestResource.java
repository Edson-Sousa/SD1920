package sd1920.trab1.impl.srv.rest;

import java.net.URI;
import java.util.HashSet;
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
	
	private final ConcurrentMap<Long, Message> allMessages = new ConcurrentHashMap<Long, Message>();
	private final ConcurrentMap<String, Set<Long>> userInboxs = new ConcurrentHashMap<String, Set<Long>>();

	private static Logger Log = Logger.getLogger(MessageRestResource.class.getName());

	public MessageRestResource() {
		this.randomNumberGenerator = new Random(System.currentTimeMillis());
	}

	@Override
	public long postMessage(String pwd, Message msg) {
		Log.info("Received request to register a new message (Sender: " + msg.getSender() + "; Subject: "+msg.getSubject()+")");
		if(msg.getSender() == null || msg.getDestination() == null || msg.getDestination().size() == 0) {
			Log.info("Message was rejected due to lack of recepients.");
			throw new WebApplicationException( Status.CONFLICT );
		}
		
		//TODO: verifica se user e password sao validos
		URI[] srvURI = Discovery.knownUrisOf("UserService");
		RestUserClient ruc = new RestUserClient(srvURI[0]);
		if(ruc.getUser(msg.getSender(), pwd) == null) {
			Log.info("user does not exist or if the pwd is not correct.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		long newID = 0;
		synchronized (this) {
			newID = Math.abs(randomNumberGenerator.nextLong());
			while(allMessages.containsKey(newID)) {
				newID = Math.abs(randomNumberGenerator.nextLong());
			}
			allMessages.put(newID, msg);
		}

		Log.info("Created new message with id: " + newID);
		synchronized (this) {
			for(String recipient: msg.getDestination()) {
				if(!userInboxs.containsKey(recipient))
					userInboxs.put(recipient, new HashSet<Long>());
				userInboxs.get(recipient).add(newID);
			}
		}
			
		Log.info("Recorded message with identifier: " + newID);
		return newID;
	}

	@Override
	public Message getMessage(String user, long mid, String pwd) {
		Log.info("Received request for message with id: "+ mid);
		
		URI[] srvURI = Discovery.knownUrisOf("UserService");
		RestUserClient ruc = new RestUserClient(srvURI[0]);
		if (ruc.getUser(user, pwd) == null) {
			Log.info("user does not exist or if the pwd is not correct.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}
		
		Message m = null;
		//TODO: verificar mensagem no inbox do user
		synchronized (this) {
			 if (userInboxs.containsValue(mid))
				 m = allMessages.get(mid);
		}
		if (m == null) {
			Log.info("Requested message does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		Log.info("Returning requested message to user.");
		return m;
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
