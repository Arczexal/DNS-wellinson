package servidor;

import interf.ConstantDNS;
import interf.InterfaceDNS;
import model.TabelaUsuarios;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Vector;

public class DNS implements InterfaceDNS {
	
	TabelaUsuarios tabelaUsuarios = new TabelaUsuarios();

        
	/**
	 * Construtor
	 */
	public DNS() {

	}

	/**
	 * Metodo main
	 * @param args
	 * @throws RemoteException
	 */
	public static void main(String args[]) throws RemoteException {

		try {
			
			DNS dns = new DNS();
			InterfaceDNS interfDNS = (InterfaceDNS) UnicastRemoteObject.exportObject(dns, 0);

			Registry registry = LocateRegistry.createRegistry(ConstantDNS.RMI_PORT); 
			
			registry.bind(ConstantDNS.RMI_ID, interfDNS);

			System.out.println("Servidor DNS rodando!");
			
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	
	public boolean autentica(String nick, String ip) throws RemoteException {
		tabelaUsuarios.adicionarUsuario(nick, ip);
		System.out.println("Tabela de usuarios online: " +tabelaUsuarios.toString());
		return true;
	}

	public ArrayList<String> obterListaUsuariosOnline() throws RemoteException {
                
		return tabelaUsuarios.listaUsuariosOnline();
	}

	public String obterIP(String nick) throws RemoteException {
		return tabelaUsuarios.ipUsuario(nick);
	}
}