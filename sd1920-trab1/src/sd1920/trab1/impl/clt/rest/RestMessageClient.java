package sd1920.trab1.impl.clt.rest;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;

import sd1920.trab1.api.rest.MessageService;

public class RestMessageClient {
	
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

}
