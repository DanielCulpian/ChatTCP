package Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server{
    private List<hiloCliente> clientes;

    public Server() {
        clientes = new ArrayList<>();
        startServer();
    }

    private void startServer() {
        try {
            ServerSocket serverS = new ServerSocket(12345);

            while (true) {
                Socket clienteS = serverS.accept();
                hiloCliente hiloCliente = new hiloCliente(clienteS);
                clientes.add(hiloCliente);
                new Thread(hiloCliente).start();
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
                    System.out.println(msg);
                    if (msg == null) {
                        break;
                    }
                    else
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
