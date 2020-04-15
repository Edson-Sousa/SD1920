package sd1920.trab1.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * <p>A class to perform service discovery, based on periodic service contact endpoint 
 * announcements over multicast communication.</p>
 * 
 * <p>Servers announce their *name* and contact *uri* at regular intervals. The server actively
 * collects received announcements.</p>
 * 
 * <p>Service announcements have the following format:</p>
 * 
 * <p>&lt;service-name-string&gt;&lt;delimiter-char&gt;&lt;service-uri-string&gt;</p>
 */
public class Discovery {
	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		// addresses some multicast issues on some TCP/IP stacks
		System.setProperty("java.net.preferIPv4Stack", "true");
		// summarizes the logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}


	// The pre-aggreed multicast endpoint assigned to perform discovery. 
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 5000;

	// Used separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	private InetSocketAddress addr;
	private String serviceName;
	private String serviceURI;
	//	static Map;
	private static ConcurrentMap<String, ConcurrentMap<URI,Long>> domainURIs;

	/**
	 * @param  serviceName the name of the service to announce
	 * @param  serviceURI an uri string - representing the contact endpoint of the service being announced
	 * @throws UnknownHostException 
	 */
	public Discovery(String serviceName, String serviceURI) throws UnknownHostException {
		domainURIs = new ConcurrentHashMap<String, ConcurrentMap<URI,Long>>();
		this.addr = DISCOVERY_ADDR;
		this.serviceName = serviceName;
		this.serviceURI  = serviceURI;
//		start();
	}

	/**
	 * Starts sending service announcements at regular intervals... 
	 */
	public void start() {
Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", addr, serviceName, serviceURI));
		
		byte[] announceBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();
		DatagramPacket announcePkt = new DatagramPacket(announceBytes, announceBytes.length, addr);

		try {
			MulticastSocket ms = new MulticastSocket( addr.getPort());
			ms.joinGroup(addr.getAddress());
			// start thread to send periodic announcements
			new Thread(() -> {
				for (;;) {
					try {
						ms.send(announcePkt);
						Thread.sleep(DISCOVERY_PERIOD);
					} catch (Exception e) {
						e.printStackTrace();
						// do nothing
					}
				}
			}).start();

			// start thread to reply to clients and to collect
			new Thread(() -> {
				DatagramPacket pkt = new DatagramPacket(new byte[1024], 1024);
				for (;;) {
					try {
						pkt.setLength(1024);
						ms.receive(pkt);
						String msg = new String( pkt.getData(), 0, pkt.getLength());
						String[] msgElems = msg.split(DELIMITER);
						long startTime = 0L;
						if( msgElems.length == 2) {	//periodic announcement
//							System.out.printf( "FROM %s (%s) : %s\n", pkt.getAddress().getCanonicalHostName(), 
//									pkt.getAddress().getHostAddress(), msg);

							// to complete by recording the received information
							startTime = System.currentTimeMillis();
							String service = msgElems[0];
							URI uri = URI.create(msgElems[1]);
//							System.out.println("My output > Service: "+ msgElems[0] +" URI:"+ uri +" Time received: "+startTime);
							if(domainURIs.containsKey(service))
								domainURIs.get(service).put(uri, startTime);
							else{
								ConcurrentMap<URI,Long> map = new ConcurrentHashMap<URI,Long>();
								map.put(uri, startTime);
								domainURIs.put(service, map);
							}
							ms.setSoTimeout(DISCOVERY_TIMEOUT);
							startTime = System.currentTimeMillis();
//							System.out.printf( "FROM %s (%s) : %s\n", pkt.getAddress().getCanonicalHostName(), 
//									pkt.getAddress().getHostAddress(), msg);		
						} else {
							ms.setSoTimeout((int) (DISCOVERY_TIMEOUT - (System.currentTimeMillis()-startTime))); 
						}
					} catch (IOException e) {
						// do nothing
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the known services.
	 * 
	 * @param  serviceName the name of the service being discovered
	 * @return an array of URI with the service instances discovered. 
	 * 
	 */
	public URI[] knownUrisOf(String serviceName) {
		Set<URI> keySet = domainURIs.get(serviceName).keySet();
		URI[] uris = keySet.toArray(new URI[keySet.size()]);
		return uris;
	}	

}