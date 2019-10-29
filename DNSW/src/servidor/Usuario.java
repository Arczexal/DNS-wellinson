/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import interf.InterfaceCliente;

/**
 *
 * @author Wellinson
 */
class Usuario {

    public String nick;

    public InterfaceCliente cliente;

    //constructor
    public Usuario(String nick, InterfaceCliente client) {
        this.nick = nick;
        this.cliente = client;
    }

    //getters and setters
    public String getNick() {
        return nick;
    }

    public InterfaceCliente getUsuario() {
        return cliente;
    }

}
