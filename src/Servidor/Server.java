package Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <b>Explicacion: </b> Tenemos el codigo del servidor y del hilo que va a asignar
 * por cada cliente que acepte. <br/><br/>
 *
 * <b>Finalizacion: </b> Tener en cuenta, que el servidor se debe cerrar manualmente.
 */

public class Server{
    private List<hiloCliente> clientes;
    private List<String> conectedUsers = new LinkedList<>();

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
                new Thread(hiloCliente).start();
                clientes.add(hiloCliente);
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
                    }else{
                        if(msg.contains("user")){
                            boolean accion = false;
                            String[] partes = msg.split(":");
                            if(partes[0].equals("duser")){
                                conectedUsers.remove(partes[1]);
                                accion= true;
                            }else if(partes[0].equals("cuser")){
                                conectedUsers.add(setNombre(partes[1]));
                                accion= true;
                            }

                            if(accion){
                                msg = "cu|";
                                for(String u : conectedUsers){
                                    System.out.println(u);
                                    msg += u + ":";
                                }
                                difundirMensaje(msg);
                            }
                        }
                        difundirMensaje(msg);
                        System.out.println(msg);
                    }

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

        System.out.println(clientes.size());
        for (hiloCliente c : clientes) {
            c.mandarMensaje(msg);
        }
    }

    private String setNombre(String nombreIntento){
        int numUsrMismoNombre = 0;
        for(String n:conectedUsers){
            if(n.toLowerCase().contains(nombreIntento.toLowerCase())){
                numUsrMismoNombre++;
            }
        }


        if(numUsrMismoNombre == 0){
            return nombreIntento;
        }else{
            return nombreIntento + "_" + numUsrMismoNombre;
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
