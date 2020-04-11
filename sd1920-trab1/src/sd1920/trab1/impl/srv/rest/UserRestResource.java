package sd1920.trab1.impl.srv.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import sd1920.trab1.api.User;
import sd1920.trab1.api.rest.UserService;

public class UserRestResource implements UserService{

	protected final ConcurrentMap<String, User> allUsers;
	protected final String domain;

	private static Logger Log = Logger.getLogger(UserRestResource.class.getName());


	public UserRestResource() throws UnknownHostException {
		this.allUsers =  new ConcurrentHashMap<String, User>();
		this.domain = InetAddress.getLocalHost().getHostName();
		Log.info("Setting up new User server at " + domain);
	}

	@Override
	public String postUser(User user) {

		Log.info("Received request to register user: " + user.getName());
		if(!user.getName().equals(null)		&&		   
				!user.getDomain().equals(null)	&&
				!user.getPwd().equals(null))		{

			if(!user.getDomain().equals(domain)){

				synchronized(this){
					if(!allUsers.containsKey(user.getName())){

						Log.warning("User " + user.getName() + "successfully created");
						allUsers.put(user.getName(), user);
						return (Status.OK + ": " + user.getName() + "@" + user.getDomain());

					} else {
						Log.warning("User already exists.");
						throw new WebApplicationException( Status.CONFLICT );
					}
				}

			} else {
				Log.warning("The domain in the user does not match the domain of the server");
				throw new WebApplicationException( Status.FORBIDDEN );
			}

		} else {
			Log.warning("Either name, password, or domain are null");
			throw new WebApplicationException( Status.CONFLICT );
		}
	}

	@Override
	public User getUser(String name, String pwd) {

		Log.info("Received request to return user: " + name + "and password: " + pwd);
		if(!name.equals(null) && !pwd.equals(null)){

			synchronized(this) {

				User user = allUsers.get(name);
				if(user != null){

					if(user.getPwd().equals(pwd)){

						Log.info("Operation Successfull. Returning user: " + user.getName());
						return user;

					} else {
						Log.warning("Password is incorrect");
						throw new WebApplicationException( Status.CONFLICT );
					}

				} else {
					Log.warning("User does not exist.");
					throw new WebApplicationException( Status.CONFLICT );
				}
			}

		} else {
			Log.warning("Either name, password are null.");
			throw new WebApplicationException( Status.CONFLICT );
		}
	}

	@Override
	public User updateUser(String name, String pwd, User user) {

		Log.info("Received request to update user: " + name + "and password: " + pwd);
		if(!name.equals(null) && !pwd.equals(null) && user!= null){

			synchronized(this){

				User update = allUsers.get(name);
				if(update != null){

					if(update.getPwd().equals(pwd)){

						if(user.getName().equals(null) && user.getDomain().equals(null)){

							if(!user.getPwd().equals(null)){
								update.setPwd(user.getPwd());
							}

							if(!user.getDisplayName().equals(null)){
								update.setDisplayName(user.getDisplayName());
							}

							Log.info("Operation Successfull. Updated user: " + user.getName());
							return update;

						} else {
							Log.warning("Users cannot change name or domain");
							throw new WebApplicationException( Status.CONFLICT );
						}

					} else {
						Log.warning("Password is incorrect");
						throw new WebApplicationException( Status.CONFLICT );
					}

				} else {
					Log.warning("User does not exist.");
					throw new WebApplicationException( Status.CONFLICT );
				}
			}

		} else {
			Log.warning("Either name, password or user data are null.");
			throw new WebApplicationException( Status.CONFLICT );
		}
	}

	@Override
	public User deleteUser(String name, String pwd) {

		Log.info("Received request to delete user: " + name);
		if(!name.equals(null) && !pwd.equals(null)){

			synchronized (this){

				User test = allUsers.get(name);
				if(test != null){

					if(!pwd.equals(test.getPwd())){

						Log.warning("User " + name + "successfully removed");
						return allUsers.remove(name);

					} else {
						Log.warning("Password is incorrect");
						throw new WebApplicationException( Status.CONFLICT );
					}
					//
				} else {
					Log.warning("User does not exist.");
					throw new WebApplicationException( Status.CONFLICT );
				}
			}

		} else {
			Log.warning("Either name or password are null");
			throw new WebApplicationException( Status.CONFLICT );
		}
	}

}