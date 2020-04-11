package sd1920.trab1.impl.srv.rest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;


import sd1920.trab1.api.User;
import sd1920.trab1.api.rest.UserService;

public class UserRestResource implements UserService{

	private final ConcurrentMap<String, User> allUsers = new ConcurrentHashMap<String, User>();
	private static Logger Log = Logger.getLogger(UserRestResource.class.getName());

	@Override
	public String postUser(User user) {
		Log.info("Request to register a new user: "+user.getName());
		if (user.getName() == null || user.getPwd() == null || user.getDomain() == null) {
			Log.info("User parameter is null.");
			throw new WebApplicationException( Status.CONFLICT );
		}

		//TODO: verificar o dominio onde se encontra o user
//		if (user.getDomain() != dominio corrente)
		
		synchronized (this) {
			User res = allUsers.putIfAbsent(user.getName(), user);
			if (res != null) {
				Log.info("User already exists.");
				throw new WebApplicationException( Status.CONFLICT );
			}
		}
		Log.info("User " + user.getName() + " successfully registered.");
		return Status.OK + ": " + user.getName() + "@" + user.getDomain();
	}

	@Override
	public User getUser(String name, String pwd) {
		Log.info("Received request for user: " + name);
		User u = null;
		
		synchronized (this) {
			u = allUsers.get(name);
		}
		if (u == null) {
			Log.info("Requested user does not exist.");
			throw new WebApplicationException( Status.CONFLICT );
		} else if (u.getPwd() != pwd) {
			Log.info("Wrong password for requested user.");
			throw new WebApplicationException( Status.CONFLICT );
		}
		
		Log.info("Returning user " + name);
		return u;
	}

	@Override
	public User updateUser(String name, String pwd, User user) {
		Log.info("Received request to update user: " + name);
		User u = null;
		
		synchronized (this) {
			u = allUsers.get(name);
		}
		if (u == null) {
			Log.info("Requested user does not exist.");
			throw new WebApplicationException( Status.CONFLICT );
		} else if (u.getPwd() != pwd) {
			Log.info("Wrong password for requested user.");
			throw new WebApplicationException( Status.CONFLICT );
		}
		
		Log.info("User: "+ name + " successfully updated.");
		return allUsers.replace(name, u);
	}

	@Override
	public User deleteUser(String name, String pwd) {
		Log.info("Received request to delete user: " + name);
		User u = null;
		
		synchronized (this) {
			u = allUsers.get(name);
		}
		if (u == null) {
			Log.info("Requested user does not exist.");
			throw new WebApplicationException( Status.CONFLICT );
		} else if (u.getPwd() != pwd) {
			Log.info("Wrong password for requested user.");
			throw new WebApplicationException( Status.CONFLICT );
		}
		
		Log.info("User "+ name +" successfully deleted.");
		return allUsers.remove(name);
	}

}
