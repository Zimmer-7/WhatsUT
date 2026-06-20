package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class WhatsUTClient {
	
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1920);
            IAuthService auth = (IAuthService) registry.lookup("AuthService");

            System.out.println("Autenticação WhatsUT\n");

            // Cadastro inicial para testes
            auth.registrarUsuario("goku", "Kamehameha123");
            // Simulando outro usuário já online no sistema para teste visual
            auth.registrarUsuario("vegeta", "vermeInsolente");
            auth.login("vegeta", "vermeInsolente"); 

            // Fluxo de login do usuário atual
            String usuarioAtual = "goku";
            boolean loginCorreto = auth.login(usuarioAtual, "Kamehameha123");
            System.out.println("Login com senha correta: " + (loginCorreto ? "Sucesso! Entrou." : "Falhou"));

            // Se o login foi bem sucedido, exibe a lista caracterizando o usuário
            if (loginCorreto) {
                System.out.println("\n=================================");
                System.out.println("||     USUÁRIOS DISPONÍVEIS    ||");
                System.out.println("=================================");
                
                // Busca a lista atualizada do servidor
                List<String> listaOnline = auth.getUsuariosOnline();
                
                for (String user : listaOnline) {
                    if (user.equals(usuarioAtual)) {
                        // Caracteriza o usuário atualmente logado
                        System.out.println("[Disponível] " + user + " (Você) ");
                    } else {
                        // Outros usuários disponíveis para chat
                        System.out.println("[Disponível] " + user);
                    }
                }
                System.out.println("=================================\n");
            }

        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}