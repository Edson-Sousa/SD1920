package sd1920.trab1;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import sd1920.trab1.util.InsecureHostnameVerifier;
import sd1920.trab1.impl.MessageResourceSoap;
import sd1920.trab1.impl.UserResourceSoap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.xml.ws.Endpoint;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@SuppressWarnings("restriction")
public class EmailServerSoap {

    private static Logger Log = Logger.getLogger(EmailServerSoap.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
    }

    public static final int PORT = 8080;

    public static final String SOAP_MESSAGES_PATH = "/soap/messages";
    public static final String SOAP_USERS_PATH = "/soap/users";

    public static void main(String[] args) throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();
        String ip = localHost.getHostAddress();
        String domain = localHost.getHostName();

        //
		HttpsURLConnection.setDefaultHostnameVerifier(new InsecureHostnameVerifier());

		//
		HttpsConfigurator configurator = new HttpsConfigurator(SSLContext.getDefault());
		
        URI serverURI = URI.create(String.format("https://%s:%s/soap", ip, PORT));

        //
		HttpsServer server = HttpsServer.create(new InetSocketAddress(ip, PORT), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        //
		server.setHttpsConfigurator(configurator);
        
        // Create a SOAP Endpoint (you need one for each service)
        Endpoint soapMessagesEndpoint = Endpoint.create(new MessageResourceSoap(domain, serverURI,
                ByteBuffer.wrap(localHost.getAddress()).getInt()));
        Endpoint soapUsersEndpoint = Endpoint.create(new UserResourceSoap(domain, serverURI));

        // Publish a SOAP webservice, under the "http://<ip>:<port>/soap"
        soapMessagesEndpoint.publish(server.createContext(SOAP_MESSAGES_PATH));
        soapUsersEndpoint.publish(server.createContext(SOAP_USERS_PATH));

        server.start();

        Discovery.startAnnounce(domain, serverURI);
        Discovery.startDiscovery();
        Log.info(String.format("%s SOAP Server ready @ %s\n", domain, serverURI));
    }

}
