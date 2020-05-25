package sd1920.trab2.impl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import sd1920.trab1.api.Message;
import sd1920.trab1.clients.ClientFactory;
import sd1920.trab1.clients.UsersEmailClient;
import sd1920.trab2.api.ProxyMessageService;

public class ProxyMessageResource implements ProxyMessageService {
	
	String domain;
    AtomicInteger midCounter;
    int midPrefix;

    UsersEmailClient localUserClient;
    
    //TODO: substituir as estruturas de dados 'inBoxes' e 'outBoxes'

	public ProxyMessageResource(String domain, URI selfURI, int midPrefix) {
        System.out.println("Constructed MessageResource in domain " + domain);
        System.out.println("Prefix: " + midPrefix);

        this.domain = domain;
        this.midCounter = new AtomicInteger(0);
        this.midPrefix = midPrefix;

        localUserClient = ClientFactory.getUsersClient(selfURI, 5, 1000);

	}
	
	public long nextMessageId() {
        //Message id is constructed using the (server-unique) prefix and a local counter
        int counter = midCounter.incrementAndGet();
        return ((long) counter << 32) | (midPrefix & 0xFFFFFFFFL);
    }
	
    //***************** INBOX OPERATIONS **************************

	@Override
	public Message getMessage(String user, long mid, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getMessages(String user, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFromUserInbox(String user, long mid, String pwd) {
		// TODO Auto-generated method stub
		
	}
	
    //***************** OUTBOX OPERATIONS **************************

	@Override
	public long postMessage(String pwd, Message msg) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void deleteMessage(String user, long mid, String pwd) {
		// TODO Auto-generated method stub
		
	}

}
