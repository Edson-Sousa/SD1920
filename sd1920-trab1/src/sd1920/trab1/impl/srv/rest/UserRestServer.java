package sd1920.trab1.impl.srv.rest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd1920.trab1.discovery.Discovery;

public class UserRestServer {

		private static Logger Log = Logger.getLogger(UserRestServer.class.getName());

		static {
			System.setProperty("java.net.preferIPv4Stack", "true");
			System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
		}

		public static final int PORT = 8080;
		static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
		
		public static Discovery discovery;

		public static void main(String[] args) throws UnknownHostException, IOException{
				String ip = InetAddress.getLocalHost().getHostAddress();
				String domain = InetAddress.getLocalHost().getHostName();
				
				String serverURI = String.format("http://%s:%s/rest", ip, PORT);
				
				// More code can be executed here...
				Discovery discovery = new Discovery(domain, serverURI);
				
				UserRestResource users = new UserRestResource(serverURI);
				MessageRestResource messages = new MessageRestResource(serverURI, discovery);
				
				ResourceConfig config = new ResourceConfig();
				config.register(users);
				config.register(messages);
				
				JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);
				
				Log.info(String.format("%s Server ready @ %s\n", domain, serverURI));
				
				discovery.start();
	}

}