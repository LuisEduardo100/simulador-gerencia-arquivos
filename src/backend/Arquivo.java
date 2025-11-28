package backend;

/**
 * Representa um arquivo no sistema.
 * É uma "folha" na estrutura de árvore (não possui filhos).
 */
public class Arquivo extends FSNode {
    private String conteudo;

    public Arquivo(String nome, Diretorio pai, String conteudo) {
        super(nome, pai);
        this.conteudo = conteudo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public int getTamanho() {
        // O tamanho é o comprimento do conteúdo (simulação simples)
        return conteudo != null ? conteudo.length() : 0;
    }
}