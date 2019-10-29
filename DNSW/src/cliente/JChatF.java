/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

/**
 *
 * @author Wellinson
 */
class JChatF extends JFrame implements ActionListener, Serializable {

    private static final long serialVersionUID = 1L;

    private JPanel textPanel, inputPanel;
    private JTextField textField;
    private String nick, nickAmigo, mensagem;
    private Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);
    private Border blankBorder = BorderFactory.createEmptyBorder(10, 10, 20, 10);
    private Chat chatClient;
    private JList<String> listaUsuarios;
    private DefaultListModel<String> listModel;

    protected JTextArea textArea, userArea;
    protected JFrame frame;
    protected JButton buttonEnviar, buttonAutentica, buttonLookUp;
    protected JPanel clientPanel, userPanel;

    public static void main(String args[]) {
        new JChatF();
    }

    public JChatF() {

        frame = new JFrame("Console Cliente");
        frame.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (chatClient != null) {
                    try {
                        enviarMensagemTodos("Saiu do Chat");
                        chatClient.servidor.sairChat(nick);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }

        });

        iniciarComponentes();
    }

    public void iniciarComponentes() {

        Container c = getContentPane();
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(getInputPanel(), BorderLayout.CENTER);
        
        outerPanel.add(getTextPanel(), BorderLayout.NORTH);
        c.setLayout(new BorderLayout());
        c.add(outerPanel, BorderLayout.CENTER);
        c.add(getUsersPanel(), BorderLayout.WEST);

        frame.add(c);
        frame.pack();

        frame.setLocation(150, 150);
        textField.requestFocus();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public JPanel getTextPanel() {
        String bemVindo = "Bem vindo a esse prototipo de Chat\nDigite seu nickName e inicie o chat\n";
        textArea = new JTextArea(bemVindo, 14, 34);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setFont(meiryoFont);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        textPanel = new JPanel();
        textPanel.add(scrollPane);

        textPanel.setFont(new Font("Meiryo", Font.PLAIN, 14));
        return textPanel;
    }

    public JPanel getInputPanel() {
        inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        inputPanel.setBorder(blankBorder);
        textField = new JTextField();
        textField.setFont(meiryoFont);
        inputPanel.add(textField);

        textField.addKeyListener(new java.awt.event.KeyListener() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        mensagem = textField.getText();
                        if (mensagem.equals("")) {
                            System.out.println("mensagem vazia!\n");
                        } else {
                            textField.setText("");
                            textArea.append(nick + " : " + mensagem + "\n");
                            enviarMensagem(nickAmigo);
                        }

                    } catch (RemoteException remoteExc) {
                        remoteExc.printStackTrace();
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
        });

        return inputPanel;
    }

    public JPanel getUsersPanel() {

        userPanel = new JPanel(new BorderLayout());
        userPanel.add(setButtonPanel(), BorderLayout.SOUTH);
        userPanel.setBorder(blankBorder);
        return userPanel;

    }

    public void setClientPanel(String[] currClients) {
        clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();

        for (String s : currClients) {
            listModel.addElement(s);
        }
        if (currClients.length > 1) {
            buttonEnviar.setEnabled(true);
        }

        listaUsuarios = new JList<String>(listModel);
        listaUsuarios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaUsuarios.setVisibleRowCount(8);
        listaUsuarios.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(listaUsuarios);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }

    public JPanel setButtonPanel() {

        buttonLookUp = new JButton("lookUp Amigo");
        buttonLookUp.addActionListener(this);
        buttonLookUp.setEnabled(false);

        buttonEnviar = new JButton("Enviar");
        buttonEnviar.addActionListener(this);
        buttonEnviar.setEnabled(false);

        buttonAutentica = new JButton("Autentica ");
        buttonAutentica.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));

        buttonPanel.add(buttonLookUp);
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(buttonAutentica);
        buttonPanel.add(buttonEnviar);

        return buttonPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == buttonAutentica) {

                nick = textField.getText();

                if (nick.length() != 0) {
                    frame.setTitle(nick + "'s console ");
                    textField.setText("");
                    textArea.append("Servidor : " + nick + " conectanto ao Chat...\n");
                    try {
                        getConexao(nick);
                    } catch (AlreadyBoundException ex) {
                        Logger.getLogger(JChatF.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NotBoundException ex) {
                        Logger.getLogger(JChatF.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (!chatClient.conexao) {
                        buttonAutentica.setEnabled(false);
                        buttonEnviar.setEnabled(true);
                        buttonLookUp.setEnabled(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Digite seu nome Por favor");
                }
            }
            if (e.getSource() == buttonLookUp) {
                String nickAmigo = JOptionPane.showInputDialog("Digite o nome de seu amigo para iniciar o chat: ");
                chatClient.conectarServerAmigo(nickAmigo);
                buttonLookUp.setEnabled(false);

            }
            if (e.getSource() == buttonEnviar) {

                mensagem = textField.getText();
                if (mensagem.equals("")) {
                    System.out.println("mensagem vazia!\n");
                } else {
                    textField.setText("");
                    textArea.append(nick + " : " + mensagem + "\n");
                    enviarMensagem(nickAmigo);
                }
            }

        } catch (RemoteException remoteExc) {
            remoteExc.printStackTrace();
        } catch (MalformedURLException ex) {
            Logger.getLogger(JChatF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(JChatF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarMensagemTodos(String mensagem) throws RemoteException {
        String mensagemEnviada = "[" + nick + "] : " + mensagem + "\n";

        chatClient.servidor.atualizarChat(nick, mensagem);
    }

    private void enviarMensagem(String nickAmigo) throws RemoteException {
        String mensagemEnviada = "[" + nick + "] : " + mensagem + "\n";

        String[] nicks = {nickAmigo, nick};
        chatClient.servidor.enviarMensagem(nicks, mensagemEnviada);
    }

    private void getConexao(String nickName) throws RemoteException, MalformedURLException, AlreadyBoundException, NotBoundException {
        String Nick = nickName.replaceAll("\\s+", "_");
        Nick = nickName.replaceAll("\\W+", "_");
        try {
            chatClient = new Chat(this, Nick);
            chatClient.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setNickAmigo(String nickAmigo) {
        this.nickAmigo = nickAmigo;
    }

}
