package main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class AuthService extends UnicastRemoteObject implements IAuthService {
    
	private static final long serialVersionUID = 1L;
	private final Map<String, String> bancoUsuarios;

    public AuthService() throws RemoteException {
        super();
        this.bancoUsuarios = new HashMap<>();
    }

    @Override
    public synchronized boolean registrarUsuario(String username, String password) throws RemoteException {
        if (bancoUsuarios.containsKey(username)) {
            System.out.println("Tentativa de cadastro com usuário já existente: " + username);
            return false;
        }
        
        String senhaCriptografada = SecurityUtils.hashPassword(password);
        bancoUsuarios.put(username, senhaCriptografada);
        System.out.println("Usuário registrado com sucesso: " + username);
        return true;
    }

    @Override
    public synchronized boolean login(String username, String password) throws RemoteException {
        if (!bancoUsuarios.containsKey(username)) {
            System.out.println("Tentativa de login: Usuário não encontrado -> " + username);
            return false;
        }

        String senhaFornecidaHash = SecurityUtils.hashPassword(password);
        String senhaArmazenadaHash = bancoUsuarios.get(username);

        boolean autenticado = senhaArmazenadaHash.equals(senhaFornecidaHash);
        System.out.println("Tentativa de login para [" + username + "]: " + (autenticado ? "SUCESSO" : "FALHOU"));
        
        return autenticado;
    }
}
