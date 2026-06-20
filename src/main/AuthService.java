package main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService extends UnicastRemoteObject implements IAuthService {
    
	private static final long serialVersionUID = 1L;
	private final Map<String, String> bancoUsuarios;
	private List<String> usuariosOnline;

    public AuthService() throws RemoteException {
        super();
        this.bancoUsuarios = new HashMap<>();
        this.usuariosOnline = new ArrayList<>();
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

        if (!senhaArmazenadaHash.equals(senhaFornecidaHash)) {
            System.out.println("Senha invalida");
            return false;
        }
        
        if (!usuariosOnline.contains(username)) {
            usuariosOnline.add(username);
        }
        
        return true;
    }

    @Override
    public synchronized List<String> getUsuariosOnline() throws RemoteException {
        // Retorna uma cópia para evitar concorrência
        return new ArrayList<>(usuariosOnline);
    }
}
