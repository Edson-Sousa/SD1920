package sd1920.trab1.impl.clt.rest_errado_paraUsoDoCode;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;

import sd1920.trab1.api.Message;
import sd1920.trab1.api.rest.MessageService;

public class RestMessageClient implements MessageService {
	
	protected URI uri;
	protected Client client;
	protected WebTarget target;
	protected ClientConfig config;

	public RestMessageClient(URI serverUri) {
		uri = serverUri;
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		target = this.client.target(uri).path(MessageService.PATH);
	}

	//Que tipo de return devem ter esses metodos...
	
	@Override
	public long postMessage(String pwd, Message msg) {
		return 0;
	}

	@Override
	public Message getMessage(String user, long mid, String pwd) {
		return null;
	}

	@Override
	public List<Long> getMessages(String user, String pwd) {
		return null;
	}

	@Override
	public void removeFromUserInbox(String user, long mid, String pwd) {
		
	}

	@Override
	public void deleteMessage(String user, long mid, String pwd) {
		
	}

}
