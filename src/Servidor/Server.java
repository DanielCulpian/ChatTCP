package Servidor;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {
    private List<hiloCliente> clientes;

    public Server() {
        clientes = new ArrayList<>();

        startServer();
    }

    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                hiloCliente clientHandler = new hiloCliente(clientSocket);
                clientes.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class hiloCliente implements Runnable {
        private Socket clientS;
        private BufferedReader reader;
        private PrintWriter writer;

        public hiloCliente(Socket socket) {
            this.clientS = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
                writer = new PrintWriter(clientS.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msg = reader.readLine();
                    if (msg == null) {
                        break;
                    }
                    difundirMensaje(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                    writer.close();
                    clientS.close();
                    clientes.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void mandarMensaje(String msg) {
            writer.println(msg);
        }
    }

    private void difundirMensaje(String msg) {

        for (hiloCliente c : clientes) {
            c.mandarMensaje(msg);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
