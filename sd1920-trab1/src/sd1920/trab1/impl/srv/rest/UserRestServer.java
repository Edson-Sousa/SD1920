package sd1920.trab1.impl.srv.rest;

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

		public static final int PORT = 7070;
		public static final String SERVICE = "UserService";
		static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
		
		public static Discovery discovery;

		public static void main(String[] args) throws UnknownHostException {
			String ip = InetAddress.getLocalHost().getHostAddress();

			ResourceConfig config = new ResourceConfig();
			config.register(UserRestResource.class);

			String serverURI = String.format("http://%s:%s/rest", ip, PORT);
			JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

			Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));

			// More code can be executed here...
			Discovery discovery = new Discovery(DISCOVERY_ADDR, SERVICE, serverURI);
			//TODO: ?
			discovery.start();
	}

}
