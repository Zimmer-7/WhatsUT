package main;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginFrame extends JFrame {

    private IAuthService auth;
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnRegistrar, btnLogin;

    public LoginFrame() {
        setTitle("WhatsUT - Autenticação");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel de campos
        JPanel painelCampos = new JPanel(new GridLayout(2, 2, 5, 5));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        txtUsuario = new JTextField();
        txtSenha = new JPasswordField();

        painelCampos.add(new JLabel("Usuário:"));
        painelCampos.add(txtUsuario);
        painelCampos.add(new JLabel("Senha:"));
        painelCampos.add(txtSenha);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnRegistrar = new JButton("Registrar");
        btnLogin = new JButton("Login");
        
        painelBotoes.add(btnRegistrar);
        painelBotoes.add(btnLogin);

        add(painelCampos, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        configurarEventos();
        conectarRMI();
    }

    private void conectarRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1919);
            auth = (IAuthService) registry.lookup("AuthService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao servidor RMI: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarEventos() {
        // Botão Registrar
        btnRegistrar.addActionListener(e -> {
            try {
                if (auth == null) return;
                boolean sucesso = auth.registrarUsuario(txtUsuario.getText(), new String(txtSenha.getPassword()));
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Usuário '" + txtUsuario.getText() + "' registrado com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Usuário já existe.", "Erro", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Falha ao registrar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Botão Login
        btnLogin.addActionListener(e -> {
            try {
                if (auth == null) return;
                String usuario = txtUsuario.getText();
                boolean sucesso = auth.login(usuario, new String(txtSenha.getPassword()));
                
                if (sucesso) {
                    // Abre a tela principal passando a conexão RMI e o usuário logado
                    SwingUtilities.invokeLater(() -> {
                        new MainChatFrame(auth, usuario).setVisible(true);
                    });
                    // Fecha a tela de login
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Falha no Login", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Falha no login: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
