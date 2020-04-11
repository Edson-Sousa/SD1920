package sd1920.trab1.impl.clt.rest;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientConfig;

import sd1920.trab1.api.User;
import sd1920.trab1.api.rest.MessageService;
import sd1920.trab1.api.rest.UserService;

public class RestUserClient implements UserService {

	protected URI uri;
	protected Client client;
	protected WebTarget target;
	protected ClientConfig config;

	public RestUserClient(URI serverUri) {
		uri = serverUri;
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		target = this.client.target(uri).path(MessageService.PATH);
	}

	@Override
	public String postUser(User user) {
		Response r = target.request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));

		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
			return r.getStatus() + ": " + user.getName() + "@" + user.getDomain();//r.getStatus() + user.getName() + user.getDomain();
		else
			return Integer.toString(r.getStatus());
	}

	@Override
	public User getUser(String name, String pwd) {
		Response r = target.path(name).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
			//return utilizador.
			return null;
		else
			//return codigo de erro.
			return null;
	}

	@Override
	public User updateUser(String name, String pwd, User user) {
		Response r = target.path(name).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));
		//return utilizador ou codigo de erro.
		return null;
	}

	@Override
	public User deleteUser(String name, String pwd) {
		Response r = target.path(name).request()
				.accept(MediaType.APPLICATION_JSON)
				.delete();
		if (r.getStatus() == Status.NO_CONTENT.getStatusCode() )
			//Devolve utilizador.
			return null;
		else
			//Devolve codigo de erro
			return null;
	}

}
