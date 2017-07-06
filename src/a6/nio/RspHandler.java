package a6.nio;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import StringProcessors.HalloweenCommandProcessor;
import a6.ResponseHandler;
import port.trace.nio.RemoteCommandExecuted;

public class RspHandler implements Runnable, ResponseHandler{
	HalloweenCommandProcessor hcp;
	BlockingQueue<String> queue;
	String cache;
	
	public RspHandler(HalloweenCommandProcessor hcp){
		this.hcp=hcp;
		queue = new ArrayBlockingQueue<>(1024);
		cache=null;
	}
	
	public synchronized boolean handleResponse(byte[] rsp) {
		String input = new String(rsp);
		if(!queue.offer(input)){
			System.out.println("Queue full");
		}
		return false;
	}	

	public void run() {
		while(true) {
			try{
				String command = queue.take();
				hcp.processCommand(command);
				RemoteCommandExecuted.newCase(this,
						command);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleResponse(String command) {}
}


