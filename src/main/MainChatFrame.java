package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainChatFrame extends JFrame {

    private final IAuthService auth;
    private final String usuarioLogado;

    private String chatAtivo = ""; 
    private final Map<String, List<String>> historicoConversas = new HashMap<>();

    private JTextField txtDestinatario, txtGrupo, txtMensagem;
    private JTextArea areaConsole;
    private JLabel lblChatAtual;
    private JButton btnCriarGrupo, btnSolicitarEntrada, btnVerSolicitacoes, btnEnviarPrivado, btnEnviarGrupo, btnEnviarArquivo, btnSairGrupo, btnLerMensagens;
    
    private JList<String> listaUsuariosOnline;
    private DefaultListModel<String> modeloUsuarios;
    private JList<String> listaGruposDisponiveis;
    private DefaultListModel<String> modeloGrupos;
    private JButton btnAtualizarListas;

    public MainChatFrame(IAuthService auth, String usuarioLogado) {
        this.auth = auth;
        this.usuarioLogado = usuarioLogado;

        setTitle("WhatsUT - Painel Principal [" + usuarioLogado + "]");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Painel Superior
        JPanel painelSuperior = new JPanel(new GridLayout(1, 2));
        painelSuperior.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblStatus = new JLabel(" Usuário: " + usuarioLogado);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblChatAtual = new JLabel("Nenhuma conversa selecionada", SwingConstants.RIGHT);
        lblChatAtual.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblChatAtual.setForeground(Color.BLUE);
        painelSuperior.add(lblStatus);
        painelSuperior.add(lblChatAtual);

        // 2. Painel Lateral Direito (Abas)
        JPanel painelLateral = new JPanel(new BorderLayout(5, 5));
        painelLateral.setBorder(BorderFactory.createTitledBorder("Navegação"));
        painelLateral.setPreferredSize(new Dimension(220, 0));

        JTabbedPane abasLaterais = new JTabbedPane();
        modeloUsuarios = new DefaultListModel<>();
        listaUsuariosOnline = new JList<>(modeloUsuarios);
        abasLaterais.addTab("Usuários Online", new JScrollPane(listaUsuariosOnline));
        
        modeloGrupos = new DefaultListModel<>();
        listaGruposDisponiveis = new JList<>(modeloGrupos);
        abasLaterais.addTab("Grupos", new JScrollPane(listaGruposDisponiveis));

        btnAtualizarListas = new JButton("Atualizar Contatos/Grupos");
        painelLateral.add(abasLaterais, BorderLayout.CENTER);
        painelLateral.add(btnAtualizarListas, BorderLayout.SOUTH);

        // 3. Painel Central
        JPanel painelCentral = new JPanel(new GridLayout(3, 1, 5, 5));

        // Sub-painel: Grupos
        JPanel painelGrupos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelGrupos.setBorder(BorderFactory.createTitledBorder("1. Gerenciamento de Grupos"));
        txtGrupo = new JTextField(10);
        btnCriarGrupo = new JButton("Criar Grupo");
        btnSolicitarEntrada = new JButton("Solicitar Entrada");
        btnVerSolicitacoes = new JButton("Ver Solicitações"); // <-- Botão real no lugar do Mock
        btnSairGrupo = new JButton("Sair do Grupo");

        painelGrupos.add(new JLabel("Nome do Grupo:"));
        painelGrupos.add(txtGrupo);
        painelGrupos.add(btnCriarGrupo);
        painelGrupos.add(btnSolicitarEntrada);
        painelGrupos.add(btnVerSolicitacoes);
        painelGrupos.add(btnSairGrupo);

        // Sub-painel: Mensagens e Arquivos
        JPanel painelMensagens = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelMensagens.setBorder(BorderFactory.createTitledBorder("2. Envio de Mensagens / Arquivos"));
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

        // Sub-painel: Sincronização
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAcoes.setBorder(BorderFactory.createTitledBorder("3. Sincronização"));
        btnLerMensagens = new JButton("Sincronizar e Buscar Novas Mensagens");
        painelAcoes.add(btnLerMensagens);

        painelCentral.add(painelGrupos);
        painelCentral.add(painelMensagens);
        painelCentral.add(painelAcoes);

        // 4. Painel Inferior: Console
        JPanel painelConsole = new JPanel(new BorderLayout());
        painelConsole.setBorder(BorderFactory.createTitledBorder("Visualização da Conversa Ativa"));
        areaConsole = new JTextArea();
        areaConsole.setEditable(false);
        areaConsole.setBackground(Color.BLACK);
        areaConsole.setForeground(Color.GREEN);
        JScrollPane scrollConsole = new JScrollPane(areaConsole);
        scrollConsole.setPreferredSize(new Dimension(930, 240));
        painelConsole.add(scrollConsole, BorderLayout.CENTER);

        add(painelSuperior, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelLateral, BorderLayout.EAST);
        add(painelConsole, BorderLayout.SOUTH);

        configurarEventos();
        sincronizarListasGerais();
    }

    private void atualizarTelaDeChat() {
        areaConsole.setText("");
        if (chatAtivo.isEmpty()) {
            areaConsole.append("[SISTEMA] Selecione um usuário ou grupo na barra lateral para conversar.\n");
            return;
        }
        List<String> historico = historicoConversas.getOrDefault(chatAtivo, new ArrayList<>());
        for (String msg : historico) {
            areaConsole.append(msg + "\n");
        }
    }

    private void adicionarMensagemAoHistorico(String chaveTarget, String mensagemFormatada) {
        historicoConversas.putIfAbsent(chaveTarget, new ArrayList<>());
        historicoConversas.get(chaveTarget).add(mensagemFormatada);
    }

    private void sincronizarListasGerais() {
        try {
            List<String> usuariosOnline = auth.getUsuariosOnline();
            modeloUsuarios.clear();
            for (String user : usuariosOnline) {
                modeloUsuarios.addElement(user.equals(usuarioLogado) ? user + " (Você)" : user);
            }

            List<String> grupos = auth.listarGrupos();
            modeloGrupos.clear();
            for (String g : grupos) {
                modeloGrupos.addElement(g);
            }
        } catch (Exception ex) {
            areaConsole.append("[ERRO] Erro ao sincronizar contatos/grupos: " + ex.getMessage() + "\n");
        }
    }
    
    private void log(String mensagem) {
        areaConsole.append(mensagem + "\n");
    }

    private void configurarEventos() {

        // Clique na lista de Usuários Online
        listaUsuariosOnline.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaUsuariosOnline.getSelectedValue() != null) {
                listaGruposDisponiveis.clearSelection();
                String selecionado = listaUsuariosOnline.getSelectedValue().replace(" (Você)", "").trim();
                chatAtivo = selecionado;
                txtDestinatario.setText(chatAtivo);
                lblChatAtual.setText("Chat Privado com: " + chatAtivo + " ");
                atualizarTelaDeChat();
            }
        });

        // Clique na lista de Grupos
        listaGruposDisponiveis.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaGruposDisponiveis.getSelectedValue() != null) {
                listaUsuariosOnline.clearSelection();
                chatAtivo = listaGruposDisponiveis.getSelectedValue();
                txtGrupo.setText(chatAtivo);
                txtDestinatario.setText(chatAtivo);
                lblChatAtual.setText("Chat do Grupo: " + chatAtivo + " ");
                atualizarTelaDeChat();
            }
        });

        btnAtualizarListas.addActionListener(e -> sincronizarListasGerais());

        // JANELA REAL DE SOLICITAÇÕES (Sem Mock)
        btnVerSolicitacoes.addActionListener(e -> {
            String grupoNome = txtGrupo.getText().trim();
            if (grupoNome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione ou digite o nome de um grupo para gerenciar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Busca as solicitações reais armazenadas no servidor
                List<String> solicitacoes = auth.listarSolicitacoesGrupo(grupoNome, usuarioLogado);
                
                if (solicitacoes.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nenhuma solicitação pendente para este grupo.\n(Nota: Apenas o administrador do grupo pode ver esta lista)", "Solicitações", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Cria uma janela popup dinamicamente
                JDialog janelaSolicitacoes = new JDialog(this, "Solicitações pendentes: " + grupoNome, true);
                janelaSolicitacoes.setSize(350, 300);
                janelaSolicitacoes.setLocationRelativeTo(this);
                janelaSolicitacoes.setLayout(new BorderLayout(10, 10));

                DefaultListModel<String> modeloSol = new DefaultListModel<>();
                solicitacoes.forEach(modeloSol::addElement);
                JList<String> listaSol = new JList<>(modeloSol);
                listaSol.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                // Painel de Ações Interno
                JPanel painelBotoesDlg = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                JButton btnAprovarUser = new JButton("Aprovar");
                JButton btnRecusarUser = new JButton("Recusar");
                painelBotoesDlg.add(btnAprovarUser);
                painelBotoesDlg.add(btnRecusarUser);

                // Evento: Aprovar Usuário Selecionado
                btnAprovarUser.addActionListener(ev -> {
                    String usuarioSelecionado = listaSol.getSelectedValue();
                    if (usuarioSelecionado != null) {
                        try {
                            boolean confirmado = auth.responderSolicitacaoGrupo(grupoNome, usuarioLogado, usuarioSelecionado, true);
                            if (confirmado) {
                                log("[SISTEMA] Usuário '" + usuarioSelecionado + "' foi aprovado no grupo " + grupoNome);
                                modeloSol.removeElement(usuarioSelecionado);
                                if (modeloSol.isEmpty()) janelaSolicitacoes.dispose();
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(janelaSolicitacoes, "Erro ao aprovar: " + ex.getMessage());
                        }
                    }
                });

                // Evento: Recusar Usuário Selecionado
                btnRecusarUser.addActionListener(ev -> {
                    String usuarioSelecionado = listaSol.getSelectedValue();
                    if (usuarioSelecionado != null) {
                        try {
                            boolean confirmado = auth.responderSolicitacaoGrupo(grupoNome, usuarioLogado, usuarioSelecionado, false);
                            if (confirmado) {
                                log("[SISTEMA] Solicitação de '" + usuarioSelecionado + "' para o grupo " + grupoNome + " foi recusada.");
                                modeloSol.removeElement(usuarioSelecionado);
                                if (modeloSol.isEmpty()) janelaSolicitacoes.dispose();
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(janelaSolicitacoes, "Erro ao recusar: " + ex.getMessage());
                        }
                    }
                });

                janelaSolicitacoes.add(new JLabel(" Selecione o usuário que deseja gerenciar:", SwingConstants.LEFT), BorderLayout.NORTH);
                janelaSolicitacoes.add(new JScrollPane(listaSol), BorderLayout.CENTER);
                janelaSolicitacoes.add(painelBotoesDlg, BorderLayout.SOUTH);
                
                janelaSolicitacoes.setVisible(true);

            } catch (Exception ex) {
                log("[ERRO RMI] Não foi possível obter solicitações: " + ex.getMessage());
            }
        });

        // Botão Enviar Mensagem Privada
        btnEnviarPrivado.addActionListener(e -> {
            try {
                String destino = txtDestinatario.getText();
                String msg = txtMensagem.getText();
                auth.enviarMensagemPrivada(usuarioLogado, destino, msg);
                adicionarMensagemAoHistorico(destino, "[Você]: " + msg);
                txtMensagem.setText("");
                atualizarTelaDeChat();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar PV: " + ex.getMessage());
            }
        });

        // Botão Enviar Mensagem em Grupo
        btnEnviarGrupo.addActionListener(e -> {
            try {
                String grupoDestino = txtGrupo.getText();
                String msg = txtMensagem.getText();
                auth.enviarMensagemGrupo(usuarioLogado, grupoDestino, msg);
                adicionarMensagemAoHistorico(grupoDestino, "[Você]: " + msg);
                txtMensagem.setText("");
                atualizarTelaDeChat();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar ao grupo: " + ex.getMessage());
            }
        });

        // Botão Sincronizar / Ler Mensagens
        btnLerMensagens.addActionListener(e -> {
            try {
                sincronizarListasGerais();
                List<String> brutas = auth.lerNovasMensagens(usuarioLogado);

                for (String raw : brutas) {
                    String[] tokens = raw.split(";", 4);
                    if (tokens[0].equals("PV")) {
                        String remetente = tokens[1];
                        String msg = tokens[2];
                        adicionarMensagemAoHistorico(remetente, "[" + remetente + "]: " + msg);
                    } else if (tokens[0].equals("GR")) {
                        String grupo = tokens[1];
                        String remetente = tokens[2];
                        String msg = tokens[3];
                        adicionarMensagemAoHistorico(grupo, "[" + remetente + "]: " + msg);
                    }
                }

                List<ArquivoMensagem> arquivos = auth.baixarArquivosNovos(usuarioLogado);
                for (ArquivoMensagem arq : arquivos) {
                    adicionarMensagemAoHistorico(arq.getRemetente(), "[ARQUIVO RECEBIDO]: " + arq.getNomeArquivo());
                }

                atualizarTelaDeChat();
            } catch (Exception ex) {
                System.err.println("Erro ao receber mensagens: " + ex.getMessage());
            }
        });

        // Botão Criar Grupo
        btnCriarGrupo.addActionListener(e -> {
            try {
                boolean sucesso = auth.criarGrupo(txtGrupo.getText(), usuarioLogado, false);
                if (sucesso) {
                    sincronizarListasGerais();
                    log("[GRUPO] Grupo '" + txtGrupo.getText() + "' criado com sucesso.");
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Botão Solicitar Entrada
        btnSolicitarEntrada.addActionListener(e -> {
            try {
                boolean enviado = auth.solicitarEntradaGrupo(txtGrupo.getText(), usuarioLogado);
                if(enviado) {
                    log("[SISTEMA] Solicitação de entrada enviada para o grupo: " + txtGrupo.getText());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Botão Enviar Arquivo
        btnEnviarArquivo.addActionListener(e -> {
            try {
                byte[] dadosArquivo = "Dados secretos das Esferas do Dragão.".getBytes();
                auth.enviarArquivoPrivado(usuarioLogado, txtDestinatario.getText(), "esferas.txt", dadosArquivo);
                adicionarMensagemAoHistorico(txtDestinatario.getText(), "[Você enviou o arquivo esferas.txt]");
                atualizarTelaDeChat();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Botão Sair do Grupo
        btnSairGrupo.addActionListener(e -> {
            try {
                auth.sairDoGrupo(usuarioLogado, txtGrupo.getText());
                sincronizarListasGerais();
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }
}