package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class UsersControler {
    File users = new File("conectedUsers.dat");

    public List<String> leerUsuariosConectados(){
        try(RandomAccessFile raf = new RandomAccessFile(users, "rw")){
            List<String> conectedUsers = new ArrayList<>();

            while (raf.getFilePointer() < raf.length()) {
                String user = raf.readUTF();

                if(user != null && !user.isEmpty()){
                    conectedUsers.add(user);
                }
            }

            return conectedUsers;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void conectarUsuario(String username){
        try(RandomAccessFile raf = new RandomAccessFile(users, "rw")){
            raf.seek(users.length());
            raf.writeUTF(username);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void desconectarUsuario(String username){
        List<String> usuarios = leerUsuariosConectados();

        try{
            int cont = 0;
            for(String u : usuarios){
                if (u.equals(username))
                    usuarios.remove(cont);
                cont++;
            }
        }catch(Exception e){
            borrarListaUsuarios();
        }

        users.delete();

        try {
            users.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String toAdd : usuarios) {
            conectarUsuario(toAdd);
        }
    }

    public boolean esUsuarioValido(String userName){
        boolean valido = true;

        List<String> conectedUsers = leerUsuariosConectados();

        for(String u : conectedUsers){
            if(u.equalsIgnoreCase(userName))
                valido = false;
        }

        return valido;
    }
    public int contarAnonimos(){
        int numAnonimos = 0;

        List<String> conectedUsers = leerUsuariosConectados();

        for(String u : conectedUsers){
            if(u.contains("Anonimo"))
                numAnonimos++;
        }

        return numAnonimos;
    }

    public void borrarListaUsuarios(){
        users.delete();
    }
}
