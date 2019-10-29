/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

/**
 *
 * @author Wellinson
 */
public interface InterfaceServidor extends Remote {

    public void atualizarChat(String name, String proximaPostagem) throws RemoteException;

    public void registrarUsuario(String[] argumentos) throws RemoteException;

    public void registrarUsuarioAmigo(String nick,String[] argumentos) throws RemoteException;

    public void sairChat(String nick) throws RemoteException;

    public void enviarMensagem(String[] nicks, String mensagem) throws RemoteException;

    public String obterIpAmigo(String nickAmigo) throws RemoteException;

}
