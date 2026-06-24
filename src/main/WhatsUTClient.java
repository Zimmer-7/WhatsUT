package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;


public class WhatsUTClient extends JFrame {

    private IAuthService auth;
    private String usuarioLogado = "";

    private JTextField txtUsuario, txtSenha, txtDestinatario, txtGrupo, txtMensagem;
    private JTextArea areaConsole;
    private JButton btnRegistrar, btnLogin, btnCriarGrupo, btnSolicitarEntrada, btnAprovar, btnEnviarPrivado, btnEnviarGrupo, btnEnviarArquivo, btnSairGrupo, btnLerMensagens;

    public WhatsUTClient() {
        // 1. Configurações básicas da Janela
        setTitle("WhatsUT Client - interface Gráfica");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 2. Painel Superior: Autenticação
        JPanel painelAutenticacao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAutenticacao.setBorder(BorderFactory.createTitledBorder("1. Autenticação"));
        
        txtUsuario = new JTextField(8);
        txtSenha = new JPasswordField(8);
        btnRegistrar = new JButton("Registrar");
        btnLogin = new JButton("Login");

        painelAutenticacao.add(new JLabel("Usuário:"));
        painelAutenticacao.add(txtUsuario);
        painelAutenticacao.add(new JLabel("Senha:"));
        painelAutenticacao.add(txtSenha);
        painelAutenticacao.add(btnRegistrar);
        painelAutenticacao.add(btnLogin);

        // 3. Painel Central: Operações de Chat e Grupo
        JPanel painelCentral = new JPanel(new GridLayout(3, 1, 5, 5)); // <-- Declarado aqui!

        // Sub-painel: Grupos
        JPanel painelGrupos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelGrupos.setBorder(BorderFactory.createTitledBorder("2. Gerenciamento de Grupos"));
        txtGrupo = new JTextField(10);
        btnCriarGrupo = new JButton("Criar Grupo");
        btnSolicitarEntrada = new JButton("Solicitar Entrada");
        btnAprovar = new JButton("Aprovar Vegeta (Mock)");
        btnSairGrupo = new JButton("Sair do Grupo");

        painelGrupos.add(new JLabel("Nome do Grupo:"));
        painelGrupos.add(txtGrupo);
        painelGrupos.add(btnCriarGrupo);
        painelGrupos.add(btnSolicitarEntrada);
        painelGrupos.add(btnAprovar);
        painelGrupos.add(btnSairGrupo);

        // Sub-painel: Mensagens e Arquivos
        JPanel painelMensagens = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelMensagens.setBorder(BorderFactory.createTitledBorder("3. Envio de Mensagens / Arquivos"));
        txtDestinatario = new JTextField(8);
        txtMensagem = new JTextField(15);
        btnEnviarPrivado = new JButton("Msg Privada");
        btnEnviarGrupo = new JButton("Msg Grupo");
        btnEnviarArquivo = new JButton("Enviar Arquivo");

        painelMensagens.add(new JLabel("Para (User):"));
        painelMensagens.add(txtDestinatario);
        painelMensagens.add(new JLabel("Mensagem:"));
        painelMensagens.add(txtMensagem);
        painelMensagens.add(btnEnviarPrivado);
        painelMensagens.add(btnEnviarGrupo);
        painelMensagens.add(btnEnviarArquivo);

        // Sub-painel: Ações de Leitura
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAcoes.setBorder(BorderFactory.createTitledBorder("4. Atualizações"));
        btnLerMensagens = new JButton("Ler Novas Mensagens & Arquivos");
        painelAcoes.add(btnLerMensagens);

        // Colocando os sub-painéis dentro do painelCentral
        painelCentral.add(painelGrupos);
        painelCentral.add(painelMensagens);
        painelCentral.add(painelAcoes);

        // 4. Painel Inferior: Console de Saída (Log)
        JPanel painelConsole = new JPanel(new BorderLayout());
        painelConsole.setBorder(BorderFactory.createTitledBorder("Log do Sistema / Caixa de Entrada"));
        
        areaConsole = new JTextArea(); // <-- Inicializado ANTES do RMI chamar o log()
        areaConsole.setEditable(false);
        areaConsole.setBackground(Color.BLACK);
        areaConsole.setForeground(Color.GREEN);
        JScrollPane scroll = new JScrollPane(areaConsole);
        painelConsole.add(scroll, BorderLayout.CENTER);

        // 5. Adicionando os painéis principais no frame
        add(painelAutenticacao, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelConsole, BorderLayout.SOUTH);
        
        scroll.setPreferredSize(new Dimension(680, 250));

        // 6. Configuração dos Eventos (Listeners)
        configurarEventos();

        // 7. Agora sim! O RMI roda por último com toda a interface já criada
        conectarRMI();
    }

    private void conectarRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1919);
            auth = (IAuthService) registry.lookup("AuthService");
            log("[SISTEMA] Conectado ao servidor RMI com sucesso.");
        } catch (Exception e) {
            log("[ERRO RMI] Não foi possível conectar ao servidor: " + e.getMessage());
        }
    }

    private void log(String mensagem) {
        areaConsole.append(mensagem + "\n");
    }

    private void configurarEventos() {
        // Botão Registrar
        btnRegistrar.addActionListener(e -> {
            try {
                boolean sucesso = auth.registrarUsuario(txtUsuario.getText(), txtSenha.getText());
                if(sucesso)
                	log("[SUCESSO] Usuário '" + txtUsuario.getText() + "' registrado!");
                else
                	log("[FALHA] Usuário já existe");
            } catch (Exception ex) {
                log("[ERRO] Falha ao registrar: " + ex.getMessage());
            }
        });

        // Botão Login
        btnLogin.addActionListener(e -> {
            try {
                boolean sucesso = auth.login(txtUsuario.getText(), txtSenha.getText());
                if(sucesso) {
                	usuarioLogado = txtUsuario.getText();
                    log("[LOGIN] Logado com sucesso como: " + usuarioLogado);
                } else
                	log("[FALHA] Usuário ou senha inválidos");
                
            } catch (Exception ex) {
                log("[ERRO] Falha no login: " + ex.getMessage());
            }
        });

        // Botão Criar Grupo
        btnCriarGrupo.addActionListener(e -> {
            try {
                // Cria o grupo configurado para NÃO eliminar se o admin sair (false)
            	boolean sucesso = auth.criarGrupo(txtGrupo.getText(), usuarioLogado, false);
            	if(sucesso)
            		log("[GRUPO] Grupo '" + txtGrupo.getText() + "' criado por " + usuarioLogado);
            	else
            		log("[FALHA] Grupo já existe");
            } catch (Exception ex) {
                log("[ERRO] Falha ao criar grupo: " + ex.getMessage());
            }
        });

        // Botão Solicitar Entrada
        btnSolicitarEntrada.addActionListener(e -> {
            try {
            	boolean sucesso = auth.solicitarEntradaGrupo(txtGrupo.getText(), usuarioLogado);
            	if(sucesso)
            		log("[GRUPO] Entrada solicitada para o grupo: " + txtGrupo.getText());
            	else
            		log("[FALHA] Grupo não existe");
            } catch (Exception ex) {
                log("[ERRO] Falha ao solicitar entrada: " + ex.getMessage());
            }
        });

        // Botão Aprovação Automatizada
        btnAprovar.addActionListener(e -> {
            try {
                List<String> solicitacoes = auth.listarSolicitacoesGrupo(txtGrupo.getText(), usuarioLogado);
                log("[GRUPO] Solicitações pendentes: " + solicitacoes);
                if (solicitacoes.contains("vegeta")) {
                    auth.responderSolicitacaoGrupo(txtGrupo.getText(), usuarioLogado, "vegeta", true);
                    log("[GRUPO] Usuário 'vegeta' foi aprovado no grupo por " + usuarioLogado);
                } else {
                    log("[AVISO] Nenhuma solicitação do 'vegeta' encontrada.");
                }
            } catch (Exception ex) {
                log("[ERRO] Falha ao aprovar: " + ex.getMessage());
            }
        });

        // Botão Enviar Mensagem Privada
        btnEnviarPrivado.addActionListener(e -> {
            try {
                auth.enviarMensagemPrivada(usuarioLogado, txtDestinatario.getText(), txtMensagem.getText());
                log("[CHAT] Mensagem privada enviada para " + txtDestinatario.getText());
            } catch (Exception ex) {
                log("[ERRO] Falha ao enviar mensagem: " + ex.getMessage());
            }
        });

        // Botão Enviar Mensagem em Grupo
        btnEnviarGrupo.addActionListener(e -> {
            try {
                auth.enviarMensagemGrupo(usuarioLogado, txtGrupo.getText(), txtMensagem.getText());
                log("[CHAT] Mensagem enviada ao grupo " + txtGrupo.getText());
            } catch (Exception ex) {
                log("[ERRO] Falha ao enviar mensagem ao grupo: " + ex.getMessage());
            }
        });

        // Botão Enviar Arquivo
        btnEnviarArquivo.addActionListener(e -> {
            try {
                String textoArquivo = "Dados secretos das Esferas do Dragão.";
                byte[] dadosArquivo = textoArquivo.getBytes();
                auth.enviarArquivoPrivado(usuarioLogado, txtDestinatario.getText(), "esferas.txt", dadosArquivo);
                log("[ARQUIVO] Arquivo 'esferas.txt' enviado para " + txtDestinatario.getText());
            } catch (Exception ex) {
                log("[ERRO] Falha ao enviar arquivo: " + ex.getMessage());
            }
        });

        // Botão Sair do Grupo
        btnSairGrupo.addActionListener(e -> {
            try {
                auth.sairDoGrupo(usuarioLogado, txtGrupo.getText());
                log("[GRUPO] " + usuarioLogado + " saiu do grupo " + txtGrupo.getText());
            } catch (Exception ex) {
                log("[ERRO] Falha ao sair do grupo: " + ex.getMessage());
            }
        });

        // Botão Ler Mensagens e Arquivos (Atualiza o Console)
        btnLerMensagens.addActionListener(e -> {
            try {
                if (usuarioLogado.isEmpty()) {
                    log("[SISTEMA] Faça login primeiro.");
                    return;
                }
                // Lendo Mensagens de texto
                List<String> mensagens = auth.lerNovasMensagens(usuarioLogado);
                log("\n--- Novas Mensagens para " + usuarioLogado + " ---");
                if(mensagens.isEmpty()) log("(Nenhuma mensagem nova)");
                for (String msg : mensagens) {
                    log(msg);
                }

                // Baixando arquivos novos
                List<ArquivoMensagem> arquivos = auth.baixarArquivosNovos(usuarioLogado);
                for (ArquivoMensagem arq : arquivos) {
                    log("[ARQUIVO RECEBIDO] Arquivo: '" + arq.getNomeArquivo() + "' de: " + arq.getRemetente());
                    log("-> Conteúdo: " + new String(arq.getConteudo()));
                }
                
                // Validação de Admin (Se for o caso de testar a troca de liderança)
                log("\n[TESTE ADMIN] Verificando se " + usuarioLogado + " consegue ver o painel de admin...");
                List<String> listagemAdmin = auth.listarSolicitacoesGrupo(txtGrupo.getText(), usuarioLogado);
                log("-> Retorno do painel admin para " + usuarioLogado + ": " + listagemAdmin);

            } catch (Exception ex) {
                log("[ERRO] Falha ao atualizar dados: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        // Roda a interface na Thread correta do Swing/AWT
        SwingUtilities.invokeLater(() -> {
            new WhatsUTClient().setVisible(true);
        });
    }
}