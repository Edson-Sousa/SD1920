package sd1920.trab1.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

	private static InetSocketAddress addr;
	private static String serviceName;
	private static String serviceURI;
	//	static Set<URI> set;
	private static Map<String, Map<URI, Long>> chmap;
	private static Map<URI,Long> chmap2;

	/**
	 * @param  serviceName the name of the service to announce
	 * @param  serviceURI an uri string - representing the contact endpoint of the service being announced
	 */
	public Discovery( InetSocketAddress addr, String serviceName, String serviceURI) {
		this.addr = addr;
		this.serviceName = serviceName;
		this.serviceURI  = serviceURI;
	}

	/**
	 * Starts sending service announcements at regular intervals... 
	 */
	public static void start() {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s", addr, serviceName, serviceURI));

		byte[] announceBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();
		DatagramPacket announcePkt = new DatagramPacket(announceBytes, announceBytes.length, addr);

		try {
			@SuppressWarnings("resource")
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
				//				set = new HashSet<URI>();
				DatagramPacket pkt = new DatagramPacket(new byte[1024], 1024);
				for (;;) {
					try {
						pkt.setLength(1024);
						ms.receive(pkt);
						String msg = new String( pkt.getData(), 0, pkt.getLength());
						String[] msgElems = msg.split(DELIMITER);
						long startTime = 0L;
						if( msgElems.length == 2) {	//periodic announcement
							System.out.printf( "FROM %s (%s) : %s\n", pkt.getAddress().getCanonicalHostName(), 
									pkt.getAddress().getHostAddress(), msg);

							// to complete by recording the received information
							startTime = System.currentTimeMillis();
							URI uri = URI.create(msgElems[1]);
							System.out.println("My output > Service: "+ msgElems[0] +" URI:"+ uri +" Time received: "+startTime);
							chmap = new ConcurrentHashMap<String, Map<URI, Long>>();
							chmap2 = new ConcurrentHashMap<URI, Long>();
							chmap2.put(uri, startTime);
							chmap.putIfAbsent(msgElems[0], chmap2);
							ms.setSoTimeout(DISCOVERY_TIMEOUT);
							startTime = System.currentTimeMillis();
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
	public static URI[] knownUrisOf(String serviceName) {
		//URI[] uris = set.toArray(new URI[set.size()]);
		URI[] uris = chmap.get(serviceName).keySet().toArray( new URI[chmap.get(serviceName).values().size()] );
		return uris;
	}	

	// Main just for testing purposes
	public static void main( String[] args) throws Exception {
		//TODO: main do discovery
		Discovery discovery = new Discovery( DISCOVERY_ADDR, "MessageService", "http://" + InetAddress.getLocalHost().getHostAddress());
		Discovery.start();
	}
}
