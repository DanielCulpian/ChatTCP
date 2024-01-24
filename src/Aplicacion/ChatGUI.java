package Aplicacion;

import Utils.UsersControler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class ChatGUI extends JFrame {
    private JPanel mainPanel;
    private JPanel chatPanel;
    private JPanel userListPanel;
    private JPanel actionsPanel;
    private JLabel label01;
    private JTextField textBox;
    private JButton sendBtn;
    private JTextArea chatArea;
    private JList userList;
    private JScrollPane chatScrollPanel;
    private String user;
    private PrintWriter out;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;


    public ChatGUI(String user){

        this.user = user;
        actualizarListaUsuarios();

        setTitle("Chat: " + user);
        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setSize(650, 750);

        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mandarMensaje();
            }
        });

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String linea;
                    while ((linea = entrada.readLine()) != null) {
                        chatArea.append(linea + "\n");
                        actualizarListaUsuarios();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mandarMensaje() {
        String msg = this.textBox.getText();
        if (!msg.isEmpty() && !msg.isBlank()) {
            out.println("-" + user + ": " + msg);
            this.textBox.setText("");
        }
    }

    public void actualizarListaUsuarios(){
        UsersControler uc = new UsersControler();
        List<String> conectedUsers = uc.leerUsuariosConectados();
        Vector<String> ve = new Vector<>(conectedUsers);

        userList.setListData(ve);
    }

    @Override
    public void dispose(){
        UsersControler us = new UsersControler();
        us.desconectarUsuario(user);

        super.dispose();
    }
}
