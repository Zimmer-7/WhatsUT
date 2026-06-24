package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Grupo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String criador; // Administrador atual
    private List<String> membros;
    private List<String> solicitacoesPendentes;
    private boolean eliminarSeAdminSair;

    public Grupo(String nome, String criador, boolean eliminarSeAdminSair) {
        this.nome = nome;
        this.criador = criador;
        this.eliminarSeAdminSair = eliminarSeAdminSair;
        this.membros = new ArrayList<>();
        this.solicitacoesPendentes = new ArrayList<>();
        this.membros.add(criador); // O criador entra automaticamente
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public String getCriador() { return criador; }
    public void setCriador(String criador) { this.criador = criador; }
    public List<String> getMembros() { return membros; }
    public List<String> getSolicitacoesPendentes() { return solicitacoesPendentes; }
    public boolean isEliminarSeAdminSair() { return eliminarSeAdminSair; }
}