package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class WhatsUTClient {
	
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1919);
            
            IAuthService auth = (IAuthService) registry.lookup("AuthService");

            System.out.println("Autenticação WhatsUT");

            boolean reg1 = auth.registrarUsuario("goku", "Kamehameha123");
            System.out.println("Registro do goku: " + (reg1 ? "Sucesso" : "Falhou"));

            boolean reg2 = auth.registrarUsuario("goku", "outraSenha");
            System.out.println("Registro duplicado do goku: " + (reg2 ? "Sucesso (Erro!)" : "Falhou (Correto!)"));

            boolean loginIncorreto = auth.login("goku", "SenhaErrada");
            System.out.println("Login com senha errada: " + (loginIncorreto ? "Permitido (Erro!)" : "Negado (Correto!)"));

            boolean loginCorreto = auth.login("goku", "Kamehameha123");
            System.out.println("Login com senha correta: " + (loginCorreto ? "Sucesso! Entrou." : "Falhou (Erro!)"));

        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}