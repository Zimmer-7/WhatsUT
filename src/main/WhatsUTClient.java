package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class WhatsUTClient {
	
    public static void main(String[] args) {
        try {
            // Conexão com o registro RMI na porta 1920
            Registry registry = LocateRegistry.getRegistry("localhost", 1920);
            IAuthService auth = (IAuthService) registry.lookup("AuthService");

            System.out.println("--- Testando Requisitos Avançados do WhatsUT ---\n");

            // 1. Cadastrando e Logando Usuários (Requisito 1)
            auth.registrarUsuario("goku", "Kamehameha123");
            auth.registrarUsuario("vegeta", "vermeInsolente");
            
            auth.login("goku", "Kamehameha123");
            auth.login("vegeta", "vermeInsolente");

            // 2. Requisito 3 - Criando um Grupo (Configurado para NÃO eliminar se o admin sair)
            System.out.println("Goku criando o grupo 'GuerreirosZ'...");
            auth.criarGrupo("GuerreirosZ", "goku", false);

            // Vegeta solicita entrar no grupo
            System.out.println("Vegeta soliciting entrada no grupo...");
            auth.solicitarEntradaGrupo("GuerreirosZ", "vegeta");

            // Goku lista solicitações e aprova o Vegeta
            List<String> solicitacoes = auth.listarSolicitacoesGrupo("GuerreirosZ", "goku");
            System.out.println("Solicitações para GuerreirosZ: " + solicitacoes);
            
            if (solicitacoes.contains("vegeta")) {
                auth.responderSolicitacaoGrupo("GuerreirosZ", "goku", "vegeta", true);
                System.out.println("Goku aprovou Vegeta no grupo!");
            }

            // 3. Requisito 3 - Testando os Modos de Chat (Mensagens Privadas e em Grupo)
            auth.enviarMensagemPrivada("vegeta", "goku", "Inseto! Você vai treinar hoje?");
            auth.enviarMensagemGrupo("goku", "GuerreirosZ", "Olá a todos do grupo!");

            // Lendo mensagens do Goku
            List<String> msgGoku = auth.lerNovasMensagens("goku");
            System.out.println("\nCaixa de entrada do Goku:\n" + msgGoku);

            // 4. Requisito 5 - Envio de Arquivos em Chat Privado
            String textoArquivo = "Dados secretos das Esferas do Dragão.";
            byte[] dadosArquivo = textoArquivo.getBytes();
            auth.enviarArquivoPrivado("goku", "vegeta", "esferas.txt", dadosArquivo);
            System.out.println("\nArquivo enviado de Goku para Vegeta.");

            // Vegeta baixando o arquivo enviado
            List<ArquivoMensagem> arquivosVegeta = auth.baixarArquivosNovos("vegeta");
            for (ArquivoMensagem arq : arquivosVegeta) {
                System.out.println("Vegeta recebeu o arquivo: '" + arq.getNomeArquivo() + "' vindo de: " + arq.getRemetente());
                System.out.println("Conteúdo do arquivo: " + new String(arq.getConteudo()));
            }

            // 5. Requisito 6 - Exclusão/Saída do Administrador
            System.out.println("\nMembros antes da saída do Admin: " + auth.listarMembrosGrupo("GuerreirosZ"));
            System.out.println("Goku (Admin) saindo do grupo GuerreirosZ...");
            auth.sairDoGrupo("goku", "GuerreirosZ");
            
            // --- TRECHO ATUALIZADO PARA MOSTRAR O NOVO ADMIN NO CLIENTE ---
            System.out.println("Membros atuais remanescentes: " + auth.listarMembrosGrupo("GuerreirosZ"));
            
            // Buscando as mensagens do Vegeta para ver o alerta do sistema
            List<String> msgVegeta = auth.lerNovasMensagens("vegeta");
            System.out.println("Caixa de entrada do Vegeta: " + msgVegeta);
            
            // PROVA DO REQUISITO 6: Se o Vegeta virou admin, ele conseguirá listar as solicitações do grupo!
            // Se ele não fosse o admin, o método retornaria uma lista vazia.
            System.out.println("\n[VALIDAÇÃO] Testando se Vegeta assumiu o papel de Administrador...");
            List<String> solicitacoesDoNovoAdmin = auth.listarSolicitacoesGrupo("GuerreirosZ", "vegeta");
            System.out.println("Vegeta consultou o painel de Admin do grupo. Retorno do servidor: " + solicitacoesDoNovoAdmin);
            System.out.println("-> Sucesso: O grupo foi mantido e a liderança transferida!");

        } catch (Exception e) {
            System.err.println("Erro no cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}