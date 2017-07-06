package a6;

import port.trace.objects.SerializerTakenFromPool;
import util.trace.TraceableInfo;
import util.trace.TraceableListener;

public class ATraceableListener implements TraceableListener {
	long lastTime;
	TraceableInfo lastTraceable;
	boolean traceAll=false;
	int numCalls=0;
	int previousNumCalls=0;
	@Override
	public void newEvent(Exception arg0) {
		if(arg0 instanceof TraceableInfo){
			if(arg0.getClass().getPackage().getName().startsWith("port.trace")){
				numCalls++;
				if(numCalls-previousNumCalls>=1000){
					System.out.println("Number of Calls: "+numCalls);
					previousNumCalls=numCalls;
				}
			}
			TraceableInfo trace = (TraceableInfo) arg0;
			long time = trace.getTimeStamp();
			if(lastTime!=0){
				long timeInterval = time-lastTime;
				if( timeInterval > 50|| traceAll){
					if(trace.getClass().getPackage().getName().startsWith("port.trace")){
						System.out.println("last: "+ lastTraceable.getClass().getSimpleName() +" current: "+ trace.getClass().getSimpleName()+ " time interval: "+ timeInterval);
					}
					if(trace instanceof SerializerTakenFromPool){
						//System.out.println("Queue Size: " + ((SerializerTakenFromPool)trace).getQueue().size() );
					}
				}
				
			}
		
			lastTime=time;
			lastTraceable = trace;
		}
	}
	
}
