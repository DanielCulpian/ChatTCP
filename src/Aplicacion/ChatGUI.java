package Aplicacion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private final Lock USERLOCK = new ReentrantLock();
    private String user;
    private PrintWriter salida;
    private BufferedReader entrada;

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private boolean usuarioVerificado = false;
    private List<String> conectedUsers = new LinkedList<>();

    private Vector<String> ve;

    /**
     *
     * @param user El nombre del usuario propietario del chat<br/><br/>
     *
     * <b>Explicacion: <b/> El formulario recibe como parametro el nombre del usuario. Una vez se construye el formulario,
     *             este, enviara su nombre al servidor indicando asi que se ha conectado exitosamente.<br/>Recibe como String
     *             tanto los mensajes de chat como las actualizaciones de conexion y desconexion de usuarios (ver el filtro usado
     *             tanto en el servidor como aqui).<br/><br/>
     *
     * <b>Finalizacion: </b> Manda un mensaje de desconexion al servidor indicando el nombre de usuario, el servidor se encargara
     *             de notificar a todos los clientes vivos.
     */


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
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            conectarThis("c");


            new Thread(() -> {
                    try {
                        String linea;
                        while ((linea = entrada.readLine()) != null) {
                            if (linea.contains("cu|")) {
                                linea = linea.substring(3);
                                conectedUsers = List.of(linea.split(":"));

                                if(!usuarioVerificado){
                                    if(!conectedUsers.get(conectedUsers.size()-1).equals(this.user))
                                        setUser(conectedUsers.get(conectedUsers.size()-1));

                                    this.usuarioVerificado = true;
                                }

                                ve = new Vector<>(conectedUsers);
                                userList.setListData(ve);
                            } else {
                                chatArea.append(linea + "\n");
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

            salida.println("-" + user + ": " + msg);
            this.textBox.setText("");
        }
    }

    private void conectarThis(String tipo) {
        if (!user.isEmpty() && !user.isBlank()) {
            salida.println(tipo+"user:"+user);
        }
    }

    @Override
    public void dispose(){
        conectarThis("d");

        super.dispose();
        System.exit(0);
    }

    private synchronized void setUser(String newName){
        USERLOCK.lock();
        try {
            this.user = newName;
            setTitle("Chat: " + newName);
        } finally {
            USERLOCK.unlock();
        }
    }
}
