package main;

import java.io.Serializable;

public class ArquivoMensagem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomeArquivo;
    private byte[] conteudo;
    private String remetente;

    public ArquivoMensagem(String nomeArquivo, byte[] conteudo, String remetente) {
        this.nomeArquivo = nomeArquivo;
        this.conteudo = conteudo;
        this.remetente = remetente;
    }

    public String getNomeArquivo() { return nomeArquivo; }
    public byte[] getConteudo() { return conteudo; }
    public String getRemetente() { return remetente; }
}