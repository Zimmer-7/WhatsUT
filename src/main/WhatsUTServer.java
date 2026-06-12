package main;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class WhatsUTServer {
	
    public static void main(String[] args) {
        try {
            IAuthService authService = new AuthService();

            Registry registry = LocateRegistry.createRegistry(1919);
            
            registry.rebind("AuthService", authService);

            System.out.println("Servidor WhatsUT online e pronto para autenticar");
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
