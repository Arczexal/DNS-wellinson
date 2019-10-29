package cliente;

import interf.*;
import servidor.ChatServidor;
import java.io.StringWriter;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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

public class Chat extends UnicastRemoteObject implements InterfaceCliente{

    InterfaceCliente cliente;
    InterfaceServidor servidor;

    private JChatF chat;
    
    private String nick;
    private String hostName = "localhost";
    private String clienteServicoNome;

    protected boolean conexao = false;

    public Chat(JChatF Chat, String nick) throws RemoteException {
        super();
        this.chat = Chat;
        this.nick = nick;
        this.clienteServicoNome = "ClientService_" + nick;

    }
    public Chat() throws RemoteException {
        super();
    }

    public void startClient() throws RemoteException, AlreadyBoundException, MalformedURLException, NotBoundException {

        String[] argumentos = {nick, hostName, clienteServicoNome};

        try {
            //Criaçao do servidor do cliente
            ChatServidor chatServidor = new ChatServidor();
            String servidorName = chatServidor.startServidor(nick);
            
            //registro e bind do cliente
            InterfaceCliente cliente = new Chat();
            
            Registry registryCliente = LocateRegistry.createRegistry(ConstantChat.RMI_PORT2);
            registryCliente.rebind(ConstantChat.RMI_ID + nick, this);
            
            
            //lookup Servidor do Cliente
            Registry registry = LocateRegistry.getRegistry(hostName, ConstantServer.RMI_PORT2);
            servidor = (InterfaceServidor) registry.lookup(servidorName);
            

        } catch (ConnectException e) {
            conexao = true;
            e.printStackTrace();
        }
        if (!conexao) {

            servidor.registrarUsuario(argumentos);
            
        }

        System.out.println("Cliente " + nick + " conectado ao Chat\n");
    }
    
    public void conectarServerAmigo(String nickAmigo) throws RemoteException, NotBoundException, MalformedURLException {
        
        chat.setNickAmigo(nickAmigo);
        
        String ipAmigo = servidor.obterIpAmigo(nickAmigo);
        String hostName = ipAmigo;
        String clienteServicoNome = "ClientService_" + nickAmigo;
        
        //Conectando no Server Do Amigo
        String servidorName = ConstantServer.RMI_ID + nickAmigo;
        
        Registry registry = LocateRegistry.getRegistry(hostName, ConstantServer.RMI_PORT);
        InterfaceServidor servidorAmigo= (InterfaceServidor) registry.lookup(servidorName);
        
        String[] AmigoArgumentos = {nickAmigo,hostName,clienteServicoNome};
        servidor.registrarUsuarioAmigo(nick,AmigoArgumentos);
        
    }

//IMPLEMENTAÇAO Metodos Do Cliente
    public void mensagemDoServidor(String mensagem) throws RemoteException {
        System.out.println(mensagem);

         if(getChat() == null)
        {
            System.out.println("chat e nulo[mensagemDoServidor]");
        }
        getChat().textArea.append(mensagem);
        getChat().textArea.setCaretPosition(getChat().textArea.getDocument().getLength());

    }

    public void AtualizarListaUsuarios(String[] usuariosAtuais) throws RemoteException {
        if (usuariosAtuais.length < 2) {
            chat.buttonEnviar.setEnabled(false);
        }
    }
    
    public JChatF getChat(){
        return this.chat;
    }
}
