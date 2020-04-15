package sd1920.trab1.impl.clt.rest.user;

import java.io.IOException;
//import java.net.URI;
import java.util.Scanner;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import sd1920.trab1.api.User;
import sd1920.trab1.api.rest.UserService;

public class DeleteUserClient {

	public final static int MAX_RETRIES = 3;
	public final static long RETRY_PERIOD = 1000;
	public final static int CONNECTION_TIMEOUT = 1000;
	public final static int REPLY_TIMEOUT = 600;

	public static void main(String[] args) throws IOException {

		Scanner sc = new Scanner(System.in);

		//You should replace this by the discovery class developed last week
		System.out.println("Provide the server url:");
		String serverUrl = sc.nextLine();

		System.out.println("Provide user name:");
		String username = sc.nextLine();

		System.out.println("Provide user password:");
		String password = sc.nextLine();

		sc.close();

		System.out.println("Sending request to server.");

		ClientConfig config = new ClientConfig();
		//How much time until timeout on opening the TCP connection to the server
		config.property(ClientProperties.CONNECT_TIMEOUT, CONNECTION_TIMEOUT);
		//How much time to wait for the reply of the server after sending the request
		config.property(ClientProperties.READ_TIMEOUT, REPLY_TIMEOUT);
		Client client = ClientBuilder.newClient(config);

		WebTarget target = client.target( serverUrl ).path( UserService.PATH );

		short retries = 0;
		boolean success = false;

		while(!success && retries < MAX_RETRIES) {
			try {

				Response r = target.path(username).queryParam("pwd", password).
						request().accept(MediaType.APPLICATION_JSON)
						.delete();

				if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
					System.out.println(r.getStatus() + ": " + r.readEntity(User.class));
				else
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
