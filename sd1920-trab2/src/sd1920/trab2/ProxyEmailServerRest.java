package sd1920.trab2;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd1920.trab1.Discovery;
import sd1920.trab1.impl.MessageResource;
import sd1920.trab1.impl.UserResource;
import sd1920.trab1.util.InsecureHostnameVerifier;
import sd1920.trab2.dropbox.CreateDirectory;
import sd1920.trab2.impl.ProxyMessageResource;
import sd1920.trab2.impl.ProxyUserResource;

public class ProxyEmailServerRest {

    private static Logger Log = Logger.getLogger(ProxyEmailServerRest.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
    }
    
    public static final int PORT = 8080;

	public static void main(String[] args) throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
        String ip = localHost.getHostAddress();
        String domain = localHost.getHostName();
        
		HttpsURLConnection.setDefaultHostnameVerifier(new InsecureHostnameVerifier());

        URI serverURI = URI.create(String.format("https://%s:%s/rest", ip, PORT));

        ResourceConfig config = new ResourceConfig();
        config.register(new ProxyMessageResource(domain, serverURI, ByteBuffer.wrap(localHost.getAddress()).getInt()));
        config.register(new ProxyUserResource(domain, serverURI));
        
        try {
        	JdkHttpServerFactory.createHttpServer(serverURI, config, SSLContext.getDefault());
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Invalid SSLL/TLS configuration.");
			e.printStackTrace();
			System.exit(1);
		}
        
        Discovery.startAnnounce(domain, serverURI);
        Discovery.startDiscovery();
        
        //Criar uma pasta na dropbox com o mesmo nome do dominio do servidor
        String directory = "/SD2020/"+domain;
        CreateDirectory nd = new CreateDirectory();
        boolean success = nd.execute(directory);
        if(success)
			System.out.println("Directory '" + directory + "' created successfuly.");
		else
			System.out.println("Failed to create directory '" + directory + "'");
        
        //Criar pasta na Dropbox com o nome do dominio se nao existir
        Log.info(String.format("%s REST Server ready @ %s\n", domain, serverURI));
	}

}
