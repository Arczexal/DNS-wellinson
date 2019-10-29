/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Wellinson
 */
public interface InterfaceCliente extends Remote {
    
        //Metodos Do Cliente
    public void mensagemDoServidor(String mensagem) throws RemoteException;
    public void AtualizarListaUsuarios(String[] usuariosAtuais) throws RemoteException;
}
