package Aplicacion;

import Utils.UsersControler;

import javax.swing.*;
import java.awt.*;
public class InicoGUI {
    private static String nombre;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mostrarVentanaEmergente();
        });
    }

    private static void mostrarVentanaEmergente() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel label = new JLabel("Introduzca su nombre:");
        panel.add(label);

        JTextField textField = new JTextField();
        panel.add(textField);

        int opcion = JOptionPane.showOptionDialog(
                null,
                panel,
                "Seleccion de usuario",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null
        );

        if (opcion == JOptionPane.OK_OPTION) {
            nombre = textField.getText();
            UsersControler us = new UsersControler();

            if(!nombre.isEmpty() && !nombre.isBlank()){
                if(us.esUsuarioValido(nombre)){
                    us.conectarUsuario(nombre);
                    new ChatGUI(nombre);
                }else{
                    int id = us.contarAnonimos();
                    JOptionPane.showMessageDialog(panel, "Ese nombre no es valido, se entrara como: 'Anonimo'");
                    String name = "Anonimo_" + (id + 1);
                    us.conectarUsuario(name);
                    new ChatGUI(name);
                }

            }else{
                int id = us.contarAnonimos();
                JOptionPane.showMessageDialog(panel, "No se ha introducido nombre, se entrara como: 'Anonimo'");
                String name = "Anonimo_" + id;
                us.conectarUsuario(name);
                new ChatGUI(name);
            }
        }
    }
}
