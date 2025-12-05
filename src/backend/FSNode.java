package backend;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe Abstrata que representa um nó genérico no sistema de arquivos.
 * Serve de base tanto para Arquivos quanto para Diretórios.
 */
public abstract class FSNode implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String nome;
    protected Diretorio pai; // Referência para o diretório onde este nó está
    protected Date dataCriacao;

    public FSNode(String nome, Diretorio pai) {
        this.nome = nome;
        this.pai = pai;
        this.dataCriacao = new Date();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Diretorio getPai() {
        return pai;
    }

    public void setPai(Diretorio pai) {
        this.pai = pai;
    }

    // Método abstrato para forçar as classes filhas a implementarem sua própria lógica de tamanho
    public abstract int getTamanho();
    
    // Caminho completo (ex: /home/usuario/docs)
    public String getCaminhoCompleto() {
        if (pai == null) return "/" + nome;
        if (pai.getPai() == null) return "/" + nome; // Caso o pai seja a raiz
        return pai.getCaminhoCompleto() + "/" + nome;
    }
}