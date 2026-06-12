package main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuthService extends Remote {
    boolean registrarUsuario(String username, String password) throws RemoteException;
    boolean login(String username, String password) throws RemoteException;
}