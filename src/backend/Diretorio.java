package backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um diretório no sistema.
 * Pode conter outros arquivos ou diretórios (lista de filhos).
 */
public class Diretorio extends FSNode {
    private List<FSNode> filhos;

    public Diretorio(String nome, Diretorio pai) {
        super(nome, pai);
        this.filhos = new ArrayList<>();
    }

    /**
     * Adiciona um nó (arquivo ou diretório) à lista de filhos.
     */
    public void adicionarFilho(FSNode no) {
        this.filhos.add(no);
    }

    /**
     * Remove um nó da lista de filhos pelo nome.
     */
    public boolean removerFilho(String nome) {
        FSNode noParaRemover = buscarFilho(nome);
        if (noParaRemover != null) {
            filhos.remove(noParaRemover);
            return true;
        }
        return false;
    }

    /**
     * Busca um filho (arquivo ou diretório) pelo nome.
     */
    public FSNode buscarFilho(String nome) {
        for (FSNode no : filhos) {
            if (no.getNome().equals(nome)) {
                return no;
            }
        }
        return null; // Não encontrado
    }

    /**
     * Retorna a lista de filhos para listagem.
     */
    public List<FSNode> getFilhos() {
        return filhos;
    }

    @Override
    public int getTamanho() {
        // O tamanho de um diretório é a soma do tamanho de seus filhos
        int total = 0;
        for (FSNode filho : filhos) {
            total += filho.getTamanho();
        }
        return total;
    }
}