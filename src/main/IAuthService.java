package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IAuthService extends Remote {
    // Autenticação e Usuários (Já existentes)
    boolean registrarUsuario(String username, String password) throws RemoteException;
    boolean login(String username, String password) throws RemoteException;
    List<String> getUsuariosOnline() throws RemoteException;

    // --- REQUISITO 3: Gerenciamento de Grupos ---
    boolean criarGrupo(String nomeGrupo, String criador, boolean eliminarSeAdminSair) throws RemoteException;
    List<String> listarGrupos() throws RemoteException;
    boolean solicitarEntradaGrupo(String nomeGrupo, String username) throws RemoteException;
    List<String> listarSolicitacoesGrupo(String nomeGrupo, String usernameAdmin) throws RemoteException;
    boolean responderSolicitacaoGrupo(String nomeGrupo, String usernameAdmin, String usuarioSolicitante, boolean aprovar) throws RemoteException;
    List<String> listarMembrosGrupo(String nomeGrupo) throws RemoteException;

    // --- REQUISITO 3 e 5: Comunicação (Mensagens e Arquivos) ---
    void enviarMensagemPrivada(String de, String para, String msg) throws RemoteException;
    void enviarMensagemGrupo(String de, String grupo, String msg) throws RemoteException;
    List<String> lerNovasMensagens(String username) throws RemoteException; // Polling simplificado de chat
    
    boolean enviarArquivoPrivado(String de, String para, String nomeArquivo, byte[] conteudo) throws RemoteException;
    List<ArquivoMensagem> baixarArquivosNovos(String username) throws RemoteException;

    // --- REQUISITO 6: Moderação e Exclusão ---
    boolean requisitarBanimentoAplicacao(String solicitante, String usuarioAserBanido) throws RemoteException;
    boolean banirUsuarioDoGrupo(String admin, String nomeGrupo, String usuarioAserBanido) throws RemoteException;
    boolean sairDoGrupo(String username, String nomeGrupo) throws RemoteException;
}