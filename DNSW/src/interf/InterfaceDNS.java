package interf;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfaceDNS extends Remote {

	public boolean autentica(String nick, String IP) throws RemoteException;
	
	public ArrayList<String> obterListaUsuariosOnline() throws RemoteException;
	
	public String obterIP(String nick) throws RemoteException;

}