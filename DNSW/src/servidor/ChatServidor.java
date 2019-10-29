package servidor;

import interf.*;
import java.io.StringWriter;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ChatServidor extends RemoteObject implements InterfaceServidor {

    String line = "---------------------------------------------\n";
    private Vector<Usuario> usuarios;

    InterfaceDNS remote;
    private String nick;
    private static final long serialVersionUID = 1L;

    //Constructor
    public ChatServidor() throws RemoteException {
        super();
        usuarios = new Vector<Usuario>(10, 1);
    }

    public String startServidor(String nick) throws RemoteException {

        String hostName = "localhost";
        String servidorName = ConstantServer.RMI_ID + nick;

        try {
            
            ChatServidor servidor = new ChatServidor();
            InterfaceServidor interfServidor = (InterfaceServidor) UnicastRemoteObject.exportObject(servidor, 0);
            Registry registry = LocateRegistry.createRegistry(ConstantServer.RMI_PORT2);
            registry.bind(servidorName, interfServidor);

            System.out.println("Chat RMI Servidor iniciado...");
        } catch (Exception e) {
            System.out.println("Servidor teve um problema para Iniciar  "+ e);
        }

        this.nick = nick;

        return servidorName;
    }
//METODOS DNS

    public void lookupDNS(String argumento) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", ConstantDNS.RMI_PORT);
            remote = (InterfaceDNS) registry.lookup(ConstantDNS.RMI_ID);

            String ip = obtemMeuIP();
            remote.autentica(argumento, ip);
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public String obtemMeuIP() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        String hostname = ip.getHostAddress();
        return hostname;
    }

    public String obterIpAmigo(String nickAmigo) throws RemoteException {
        String ipAmigo = remote.obterIP(nickAmigo);
        return ipAmigo;
    }

//END SERVER DNS}
    public void atualizarChat(String name, String proximaPostagem) throws RemoteException {
        String mensagem = name + " : " + proximaPostagem + "\n";
        enviarMensagemTodos(mensagem);
    }

    public void registrarUsuario(String[] argumentos) throws RemoteException {
        try {
            System.out.println("\n" + argumentos[0] + " Se juntou ao Chat");
            System.out.println(argumentos[0] + "'s hostname : " + argumentos[1]);
            System.out.println(argumentos[0] + "'sRMI servico : " + argumentos[2]);
            System.out.println(new Date(System.currentTimeMillis()));

            lookupDNS(argumentos[0]);
            lookUpUsuario(argumentos);
        } catch (NotBoundException ex) {
            Logger.getLogger(ChatServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void lookUpUsuario(String[] argumentos) throws AccessException, NotBoundException {
        try {
            Registry registry = LocateRegistry.getRegistry(argumentos[1], ConstantChat.RMI_PORT2);
            InterfaceCliente proximoUsuario = (InterfaceCliente) registry.lookup(ConstantChat.RMI_ID + argumentos[0]);

            usuarios.addElement(new Usuario(argumentos[0], proximoUsuario));
            enviarMensagemTodos("[Servidor] : " + argumentos[0] + " entrou no Chat.\n");
            atualizarListaUsuario();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void registrarUsuarioAmigo(String nick, String[] argumentos) throws RemoteException {
        System.out.println("\n" + argumentos[0] + " Se juntou ao Chat");
        System.out.println(argumentos[0] + "'s hostname : " + argumentos[1]);
        System.out.println(argumentos[0] + "'sRMI servico : " + argumentos[2]);
        System.out.println(new Date(System.currentTimeMillis()));

        lookUpUsuarioAmigo(nick, argumentos);
    }

    public void lookUpUsuarioAmigo(String nick, String[] argumentos) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(argumentos[1], ConstantChat.RMI_PORT);
            InterfaceCliente proximoUsuario = (InterfaceCliente) registry.lookup(ConstantChat.RMI_ID + argumentos[0]);

            usuarios.addElement(new Usuario(argumentos[0], proximoUsuario));
            
            String[] nicks = {argumentos[0], nick};
            enviarMensagem(nicks, "[Servidor] : " + nick + " entrou no Chat.\n");
            atualizarListaUsuario();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void atualizarListaUsuario() {
        String[] getUsuarios = getListaUsuario();
        for (Usuario c : usuarios) {
            try {
                c.getUsuario().AtualizarListaUsuarios(getUsuarios);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private String[] getListaUsuario() {
        String[] listaUsuarios = new String[usuarios.size()];
        for (int i = 0; i < listaUsuarios.length; i++) {
            listaUsuarios[i] = usuarios.elementAt(i).getNick();
        }
        return listaUsuarios;
    }

    public void enviarMensagemTodos(String mensagem) {
        for (Usuario usuario : usuarios) {
            try {
                usuario.getUsuario().mensagemDoServidor(mensagem);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sairChat(String nick) throws RemoteException {

        for (Usuario c : usuarios) {
            if (c.getNick().equals(nick)) {
                System.out.println(line + nick + " saiu do chat");
                System.out.println(new Date(System.currentTimeMillis()));
                usuarios.remove(c);
                break;
            }
        }
        if (!usuarios.isEmpty()) {
            atualizarListaUsuario();
        }
    }

    public void enviarMensagem(String[] nicks, String mensagem) throws RemoteException {
        int i = 0;
        for (Usuario usuario : usuarios) {
            if (usuario.getNick().equals(nicks[0])) {
                usuario = usuarios.get(i);
                usuario.getUsuario().mensagemDoServidor(mensagem);
            }
            i++;
        }
    }
}
