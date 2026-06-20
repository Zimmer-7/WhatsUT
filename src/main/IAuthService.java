package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IAuthService extends Remote {
    boolean registrarUsuario(String username, String password) throws RemoteException;
    boolean login(String username, String password) throws RemoteException;
    List<String> getUsuariosOnline() throws RemoteException;
}