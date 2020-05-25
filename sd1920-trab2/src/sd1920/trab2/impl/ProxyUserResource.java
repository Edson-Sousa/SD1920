package sd1920.trab2.impl;

import java.net.URI;

import sd1920.trab1.api.User;
import sd1920.trab1.clients.ClientFactory;
import sd1920.trab1.clients.MessagesEmailClient;
import sd1920.trab2.api.ProxyUserService;

public class ProxyUserResource implements ProxyUserService {
	
	String domain;

    MessagesEmailClient localMessageClient;
    
    //TODO: substituir a ED 'users'

	public ProxyUserResource(String domain, URI selfURI) {
		// TODO Auto-generated constructor stub
		System.out.println("Constructed UserResource in domain " + domain);
        this.domain = domain;
        localMessageClient = ClientFactory.getMessagesClient(selfURI, 2, 1000);
	}

	@Override
	public String postUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUser(String name, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User updateUser(String name, String pwd, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User deleteUser(String name, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

}
