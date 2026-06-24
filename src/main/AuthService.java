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
    private final List<String> usuariosOnline;

    private final Map<String, Grupo> bancoGrupos;
    private final Map<String, List<String>> caixasDeMensagem;
    private final Map<String, List<ArquivoMensagem>> caixasDeArquivos;

    public AuthService() throws RemoteException {
        super();
        this.bancoUsuarios = new HashMap<>();
        this.usuariosOnline = new ArrayList<>();
        this.bancoGrupos = new HashMap<>();
        this.caixasDeMensagem = new HashMap<>();
        this.caixasDeArquivos = new HashMap<>();
    }

    // --- MÉTODOS JÁ EXISTENTES REVISADOS ---

    @Override
    public synchronized boolean registrarUsuario(String username, String password) throws RemoteException {
        if (bancoUsuarios.containsKey(username)) return false;
        String senhaCriptografada = SecurityUtils.hashPassword(password);
        bancoUsuarios.put(username, senhaCriptografada);
        caixasDeMensagem.put(username, new ArrayList<>());
        caixasDeArquivos.put(username, new ArrayList<>());
        return true;
    }

    @Override
    public synchronized boolean login(String username, String password) throws RemoteException {
        if (!bancoUsuarios.containsKey(username)) return false;
        String senhaFornecidaHash = SecurityUtils.hashPassword(password);
        if (!bancoUsuarios.get(username).equals(senhaFornecidaHash)) return false;
        if (!usuariosOnline.contains(username)) usuariosOnline.add(username);
        return true;
    }

    @Override
    public synchronized List<String> getUsuariosOnline() throws RemoteException {
        return new ArrayList<>(usuariosOnline);
    }

    // --- REQUISITO 3: LOGICA DE GRUPOS ---

    @Override
    public synchronized boolean criarGrupo(String nomeGrupo, String criador, boolean eliminarSeAdminSair) throws RemoteException {
        if (bancoGrupos.containsKey(nomeGrupo)) return false;
        bancoGrupos.put(nomeGrupo, new Grupo(nomeGrupo, criador, eliminarSeAdminSair));
        return true;
    }

    @Override
    public synchronized List<String> listarGrupos() throws RemoteException {
        return new ArrayList<>(bancoGrupos.keySet());
    }

    @Override
    public synchronized boolean solicitarEntradaGrupo(String nomeGrupo, String username) throws RemoteException {
        Grupo grupo = bancoGrupos.get(nomeGrupo);
        if (grupo == null || grupo.getMembros().contains(username)) return false;
        if (!grupo.getSolicitacoesPendentes().contains(username)) {
            grupo.getSolicitacoesPendentes().add(username);
        }
        return true;
    }

    @Override
    public synchronized List<String> listarSolicitacoesGrupo(String nomeGrupo, String usernameAdmin) throws RemoteException {
        Grupo grupo = bancoGrupos.get(nomeGrupo);
        if (grupo != null && grupo.getCriador().equals(usernameAdmin)) {
            return new ArrayList<>(grupo.getSolicitacoesPendentes());
        }
        return new ArrayList<>();
    }

    @Override
    public synchronized boolean responderSolicitacaoGrupo(String nomeGrupo, String usernameAdmin, String usuarioSolicitante, boolean aprovar) throws RemoteException {
        Grupo grupo = bancoGrupos.get(nomeGrupo);
        if (grupo == null || !grupo.getCriador().equals(usernameAdmin)) return false;
        
        if (grupo.getSolicitacoesPendentes().remove(usuarioSolicitante)) {
            if (aprovar) {
                grupo.getMembros().add(usuarioSolicitante);
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized List<String> listarMembrosGrupo(String nomeGrupo) throws RemoteException {
        Grupo grupo = bancoGrupos.get(nomeGrupo);
        return (grupo != null) ? new ArrayList<>(grupo.getMembros()) : new ArrayList<>();
    }

    // --- REQUISITOS 3 E 5: CHAT, MENSAGENS E ARQUIVOS ---

    @Override
    public synchronized void enviarMensagemPrivada(String de, String para, String msg) throws RemoteException {
        if (caixasDeMensagem.containsKey(para)) {
            // Formato estruturado: PV ; Remetente ; Conteúdo
            caixasDeMensagem.get(para).add("PV;" + de + ";" + msg);
        }
    }

    @Override
    public synchronized void enviarMensagemGrupo(String de, String nomeGrupo, String msg) throws RemoteException {
        Grupo grupo = bancoGrupos.get(nomeGrupo);
        if (grupo != null && grupo.getMembros().contains(de)) {
            for (String membro : grupo.getMembros()) {
                if (!membro.equals(de) && caixasDeMensagem.containsKey(membro)) {
                    // Formato estruturado: GR ; NomeDoGrupo ; Remetente ; Conteúdo
                    caixasDeMensagem.get(membro).add("GR;" + nomeGrupo + ";" + de + ";" + msg);
                }
            }
            
        }
    }

    @Override
    public synchronized List<String> lerNovasMensagens(String username) throws RemoteException {
        List<String> novas = new ArrayList<>(caixasDeMensagem.getOrDefault(username, new ArrayList<>()));
        caixasDeMensagem.get(username).clear(); // Consome as mensagens lidas
        return novas;
    }

    @Override
    public synchronized boolean enviarArquivoPrivado(String de, String para, String nomeArquivo, byte[] conteudo) throws RemoteException {
        if (caixasDeArquivos.containsKey(para)) {
            caixasDeArquivos.get(para).add(new ArquivoMensagem(nomeArquivo, conteudo, de));
            enviarMensagemPrivada(de, para, "Te enviou um arquivo chamado: " + nomeArquivo);
            return true;
        }
        return false;
    }

    @Override
    public synchronized List<ArquivoMensagem> baixarArquivosNovos(String username) throws RemoteException {
        List<ArquivoMensagem> arquivos = new ArrayList<>(caixasDeArquivos.getOrDefault(username, new ArrayList<>()));
        caixasDeArquivos.get(username).clear();
        return arquivos;
    }

    // --- REQUISITO 6: EXCLUSÃO E MODERAÇÃO ---

    @Override
    public synchronized boolean requisitarBanimentoAplicacao(String solicitante, String usuarioAserBanido) throws RemoteException {
       
        if (bancoUsuarios.containsKey(usuarioAserBanido)) {
            bancoUsuarios.remove(usuarioAserBanido);
            usuariosOnline.remove(usuarioAserBanido);
            caixasDeMensagem.remove(usuarioAserBanido);
            caixasDeArquivos.remove(usuarioAserBanido);
            
            for (Grupo g : bancoGrupos.values()) {
                g.getMembros().remove(usuarioAserBanido);
                g.getSolicitacoesPendentes().remove(usuarioAserBanido);
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean banirUsuarioDoGrupo(String admin, String nomeGrupo, String usuarioAserBanido) throws RemoteException {
        Grupo grupo = bancoGrupos.get(nomeGrupo);
        if (grupo != null && grupo.getCriador().equals(admin)) {
            return grupo.getMembros().remove(usuarioAserBanido);
        }
        return false;
    }

    @Override
    public synchronized boolean sairDoGrupo(String username, String nomeGrupo) throws RemoteException {
        Grupo grupo = bancoGrupos.get(nomeGrupo);
        if (grupo == null || !grupo.getMembros().contains(username)) return false;

        grupo.getMembros().remove(username);

        // Se quem saiu foi o Administrador (Criador)
        if (grupo.getCriador().equals(username)) {
            if (grupo.isEliminarSeAdminSair() || grupo.getMembros().isEmpty()) {
                bancoGrupos.remove(nomeGrupo);
                System.out.println("Grupo " + nomeGrupo + " foi eliminado porque o administrador saiu.");
            } else {
                // Nova liderança: O aplicativo decide pegar o primeiro membro restante
                String novoAdmin = grupo.getMembros().get(0);
                grupo.setCriador(novoAdmin);
                System.out.println("O usuário " + novoAdmin + " é o novo administrador do grupo " + nomeGrupo);
            }
        }
        return true;
    }
}