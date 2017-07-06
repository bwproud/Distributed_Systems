package a6;

import java.rmi.RemoteException;

public interface ResponseHandler {
	void handleResponse(String command) throws RemoteException;
}
