package Aplicacion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
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
    private JScrollPane chatScrollPane;
    private JScrollPane chatScrollPanel;
    private String user;
    private PrintWriter out;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    private List<String> conectedUsers = new LinkedList<>();
    private Vector<String> ve;


    public ChatGUI(String user){

        this.user = user;

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

            conectarThis("c");


            new Thread(() -> {
                try {
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String linea;
                    if(entrada.ready()){
                        while ((linea = entrada.readLine()) != null) {
                            System.out.println(linea);
                            if(linea.contains("user")){
                                String[] partes = linea.split(":");
                                if(linea.contains("duser")){
                                    conectedUsers.remove(partes[1]);
                                }else if(linea.contains("cuser")){
                                    conectedUsers.add(partes[1]);
                                }else{
                                    chatArea.append(linea + "\n");
                                }
                                ve  = new Vector<>(conectedUsers);
                                userList.setListData(ve);
                            }else{
                                chatArea.append(linea + "\n");
                            }

                        }
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

    private void conectarThis(String tipo) {
        if (!user.isEmpty() && !user.isBlank()) {
            out.println(tipo+"user:"+user);
            System.out.println(tipo+"user:"+user);
        }
    }

    @Override
    public void dispose(){
        conectarThis("d");

        super.dispose();
        System.exit(0);
    }
}
