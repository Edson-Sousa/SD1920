package sd1920.trab1.impl.srv.rest;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import sd1920.trab1.api.Message;
import sd1920.trab1.api.User;
import sd1920.trab1.api.rest.MessageService;
import sd1920.trab1.api.rest.UserService;
import sd1920.trab1.discovery.Discovery;

@Singleton
public class MessageRestResource implements MessageService {

	public final static int MAX_RETRIES = 3;
	public final static long RETRY_PERIOD = 1000;
	public final static int CONNECTION_TIMEOUT = 1000;
	public final static int REPLY_TIMEOUT = 600;

	private Random randomNumberGenerator;

	private final ConcurrentMap<Long, Message> allMessages = new ConcurrentHashMap<Long, Message>();
	private final ConcurrentMap<String, Set<Long>> userInboxs = new ConcurrentHashMap<String, Set<Long>>();
	private Discovery discovery;
	protected final String domain, serverURI;

	private static Logger Log = Logger.getLogger(MessageRestResource.class.getName());

	public MessageRestResource(String serverURI, Discovery discovery) throws UnknownHostException {
		this.randomNumberGenerator = new Random(System.currentTimeMillis());
		this.discovery = discovery;
		this.domain = InetAddress.getLocalHost().getHostName();
		this.serverURI = serverURI;
	}

	@Override
	public long postMessage(String pwd, Message msg) {

		Log.info("Received request to register a new message (Sender: " + msg.getSender() + "; Subject: "+msg.getSubject()+")");
		if( msg.getDestination() == null|| msg.getDestination().size() == 0) {
			Log.info("Message was rejected due to lack of recepients.");
			throw new WebApplicationException( Status.CONFLICT );
		}

		long newID = 0;

		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.CONNECT_TIMEOUT, CONNECTION_TIMEOUT);
		config.property(ClientProperties.READ_TIMEOUT, REPLY_TIMEOUT);
		Client client = ClientBuilder.newClient(config);

		WebTarget target = client.target( serverURI ).path( UserService.PATH );

		short retries = 0;
		boolean success = false;

		while(!success && retries < MAX_RETRIES) {
			try {

				Response r = target.path(msg.getSender()).queryParam("pwd", pwd).
						request().accept(MediaType.APPLICATION_JSON)
						.get();

				if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {

					User u = r.readEntity(User.class);

					Log.info("Sending message with id: "+msg.getId());
					if ( msg.getSender().equals(null) || pwd.equals(null) ) {
						Log.info("Message sender or password are null.");
						throw new WebApplicationException(Status.FORBIDDEN);
					}

					if ( !u.getName().equals(msg.getSender()) 
							|| !u.getPwd().equals(pwd)) {
						Log.info("Incorrect username or password.");
						throw new WebApplicationException(Status.CONFLICT);
					}

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


				} else
					System.out.println("Error, HTTP error status: " + r.getStatus() );

				success = true;
			} catch ( ProcessingException pe ) { //Error in communication with server
				System.out.println("Timeout occurred.");
				pe.printStackTrace(); //Could be removed
				retries ++;
				try {
					Thread.sleep( RETRY_PERIOD ); //wait until attempting again.
				} catch (InterruptedException e) {
					//Nothing to be done here, if this happens we will just retry sooner.
				}
				System.out.println("Retrying to execute request.");
			}
		}
		Log.info("Recorded message with identifier: " + newID);
		return newID;

	}

	@Override
	public Message getMessage(String user, long mid, String pwd) {
		try {
			Log.info("Received request for message with id: "+ mid);



			ClientConfig config = new ClientConfig();
			config.property(ClientProperties.CONNECT_TIMEOUT, CONNECTION_TIMEOUT);
			config.property(ClientProperties.READ_TIMEOUT, REPLY_TIMEOUT);
			Client client = ClientBuilder.newClient(config);

			WebTarget target = client.target( serverURI ).path( UserService.PATH );

			short retries = 0;
			boolean success = false;

			Message m = null;
			while(!success && retries < MAX_RETRIES) {
				try {

					Response r = target.path(user).queryParam("pwd", pwd).
							request().accept(MediaType.APPLICATION_JSON)
							.get();

					if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {

						User u = r.readEntity(User.class);

						if (!u.getName().equals(user) || user.equals(null) 
								|| pwd.equals(null) || !u.getPwd().equals(pwd)) {
									Log.info("User or password null");
									throw new WebApplicationException("");
								}

								synchronized (this) {
									if (userInboxs.get(user).contains(mid))
										m = allMessages.get(mid);
								}
								if (m == null) {
									Log.info("Requested message does not exist.");
									throw new WebApplicationException( Status.NOT_FOUND );
								}
								Log.info("Returning requested message to user.");


					} else
						System.out.println("Error, HTTP error status: " + r.getStatus() );

					success = true;
				} catch ( ProcessingException pe ) { //Error in communication with server
					System.out.println("Timeout occurred.");
					pe.printStackTrace(); //Could be removed
					retries ++;
					try {
						Thread.sleep( RETRY_PERIOD ); //wait until attempting again.
					} catch (InterruptedException e) {
						//Nothing to be done here, if this happens we will just retry sooner.
					}
					System.out.println("Retrying to execute request.");
				}
			}
			return m;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<Long> getMessages(String user, String pwd) {
		Log.info("Received request for all the messages of user: " + user);

		List<Long> msgs = new ArrayList<Long>();
		synchronized (this) {
			msgs.addAll(userInboxs.get(user));
		}
		Log.info("Returnig a list of user " + user +"'s messages.");
		return msgs;
	}

	@Override
	public void removeFromUserInbox(String user, long mid, String pwd) {
		Log.info("Received request to delete message with id " + mid +" from user " + user + " inbox.");

		Set<Long> userMsgs = new HashSet<Long>();
		synchronized (this) {
			userMsgs = userInboxs.get(user);
		}
		if ( !allMessages.containsKey(mid) ) {
			Log.info("Message to delete does not exist in server.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		userMsgs.remove(mid);
	}

	@Override
	public void deleteMessage(String user, long mid, String pwd) {

		Message m = null;

		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.CONNECT_TIMEOUT, CONNECTION_TIMEOUT);
		config.property(ClientProperties.READ_TIMEOUT, REPLY_TIMEOUT);
		Client client = ClientBuilder.newClient(config);

		WebTarget target = client.target( serverURI ).path( UserService.PATH );

		short retries = 0;
		boolean success = false;

		while(!success && retries < MAX_RETRIES) {
			try {

				Response r = target.path(user).queryParam("pwd", pwd).
						request().accept(MediaType.APPLICATION_JSON)
						.get();

				if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {

					synchronized (this) {
						m = allMessages.get(mid);
					}
					if (m != null) {			
						if (m.getSender().equals(user)) {
							for (String u: userInboxs.keySet())
								userInboxs.get(u).remove(mid);
							allMessages.remove(mid);
						} else
							Log.info("The user " + user + " is not the sender of the message with id " + mid);
					} else
						Log.info("Message does not exist.");
				} else
					System.out.println("Error, HTTP error status: " + r.getStatus() );

				success = true;
			} catch ( ProcessingException pe ) { //Error in communication with server
				System.out.println("Timeout occurred.");
				pe.printStackTrace(); //Could be removed
				retries ++;
				try {
					Thread.sleep( RETRY_PERIOD ); //wait until attempting again.
				} catch (InterruptedException e) {
					//Nothing to be done here, if this happens we will just retry sooner.
				}
				System.out.println("Retrying to execute request.");
			}
		}
	}
}