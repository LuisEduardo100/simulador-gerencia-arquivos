package backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Mantém o estado da memória (diretório raiz e diretório atual).
 * Aqui contém os métodos para ser chamados na interface
 */
public class FSSimulador {
    private Diretorio raiz;
    private Diretorio diretorioAtual;
    private final String ARQUIVO_DADOS = "filesystem.dat";

    public FSSimulador() {
        if (!carregarEstado()) {
            System.out.println("Nenhum dado anterior encontrado. Iniciando novo sistema de arquivos.");
            this.raiz = new Diretorio("root", null);
            this.diretorioAtual = this.raiz;
        } else {
            System.out.println("Sistema de arquivos restaurado com sucesso de '" + ARQUIVO_DADOS + "'.");
        }
    }

    public String lerArquivo(String nome) {
        FSNode no = diretorioAtual.buscarFilho(nome);
        
        if (no == null) {
            return "Erro: Arquivo '" + nome + "' não existe.";
        }
        
        if (no instanceof Diretorio) {
            return "Erro: '" + nome + "' é um diretório, não um arquivo de texto.";
        }
        
        Arquivo arq = (Arquivo) no;
        return arq.getConteudo();
    }

    /**
     * Salva o objeto Raiz (e consequentemente todos os filhos) no arquivo físico.
     */
    public void salvarEstado() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            oos.writeObject(this.raiz);
            System.out.println("Estado do sistema salvo em '" + ARQUIVO_DADOS + "'.");
        } catch (IOException e) {
            System.err.println("Erro ao salvar o estado do sistema: " + e.getMessage());
        }
    }

    /**
     * Tenta ler o arquivo .dat e reconstruir a árvore de diretórios.
     */
    private boolean carregarEstado() {
        File arq = new File(ARQUIVO_DADOS);
        if (!arq.exists()) return false;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arq))) {
            this.raiz = (Diretorio) ois.readObject();
            this.diretorioAtual = this.raiz; // Reseta o ponteiro para a raiz ao carregar
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna o diretório onde o usuário está "logado" no momento.
     */
    public Diretorio getDiretorioAtual() {
        return diretorioAtual;
    }

    /**
     * Navega para um subdiretório (comando cd nome).
     * Para subir de nível, use ".."
     */
    public String mudarDiretorio(String nome) {
        if (nome.equals("..")) {
            if (diretorioAtual.getPai() != null) {
                diretorioAtual = diretorioAtual.getPai();
                return "Mudou para: " + diretorioAtual.getNome();
            } else {
                return "Já está na raiz.";
            }
        }
        
        FSNode no = diretorioAtual.buscarFilho(nome);
        if (no instanceof Diretorio) {
            diretorioAtual = (Diretorio) no;
            return "Mudou para: " + diretorioAtual.getNome();
        } else if (no instanceof Arquivo) {
            return "Erro: '" + nome + "' é um arquivo, não um diretório.";
        } else {
            return "Diretório não encontrado.";
        }
    }

    // --- OPERAÇÕES DO TRABALHO ---

    /**
     * Criar Diretório (mkdir)
     */
    public String criarDiretorio(String nome) {
        if (diretorioAtual.buscarFilho(nome) != null) {
            return "Erro: Já existe um arquivo ou diretório com este nome.";
        }
        Diretorio novoDir = new Diretorio(nome, diretorioAtual);
        diretorioAtual.adicionarFilho(novoDir);
        return "Diretório '" + nome + "' criado com sucesso.";
    }

    /**
     * Criar Arquivo (simulação de touch ou editor)
     */
    public String criarArquivo(String nome, String conteudo) {
        if (diretorioAtual.buscarFilho(nome) != null) {
            return "Erro: Já existe um arquivo ou diretório com este nome.";
        }
        Arquivo novoArq = new Arquivo(nome, diretorioAtual, conteudo);
        diretorioAtual.adicionarFilho(novoArq);
        return "Arquivo '" + nome + "' criado com sucesso.";
    }

    /**
     * Listar Conteúdo (ls)
     */
    public String listarConteudo() {
        List<FSNode> lista = diretorioAtual.getFilhos();
        if (lista.isEmpty()) {
            return "(Diretório vazio)";
        }
        StringBuilder sb = new StringBuilder();
        for (FSNode no : lista) {
            String tipo = (no instanceof Diretorio) ? "[DIR] " : "[ARQ] ";
            sb.append(tipo).append(no.getNome())
              .append(" (").append(no.getTamanho()).append(" bytes)\n");
        }
        return sb.toString();
    }

    /**
     * Apagar Arquivo ou Diretório (rm / rmdir)
     */
    public String apagar(String nome) {
        boolean removido = diretorioAtual.removerFilho(nome);
        if (removido) {
            return "Item '" + nome + "' removido.";
        }
        return "Erro: Item não encontrado.";
    }

    /**
     * Renomear (mv nomeAntigo nomeNovo - apenas renomeação no mesmo local)
     */
    public String renomear(String nomeAntigo, String nomeNovo) {
        FSNode no = diretorioAtual.buscarFilho(nomeAntigo);
        if (no == null) {
            return "Erro: '" + nomeAntigo + "' não encontrado.";
        }
        if (diretorioAtual.buscarFilho(nomeNovo) != null) {
            return "Erro: Já existe um item com o nome '" + nomeNovo + "'.";
        }
        no.setNome(nomeNovo);
        return "Renomeado de '" + nomeAntigo + "' para '" + nomeNovo + "'.";
    }

    /**
     * Copiar Arquivo (cp origem destino)
     * Simplificação: Copia um arquivo do diretório atual com um novo nome.
     */
    public String copiarArquivo(String nomeOrigem, String novoNome) {
        FSNode noOrigem = diretorioAtual.buscarFilho(nomeOrigem);
        
        if (noOrigem == null) return "Erro: Origem não encontrada.";
        if (!(noOrigem instanceof Arquivo)) return "Erro: Copiar diretórios não suportado nesta versão simples.";
        if (diretorioAtual.buscarFilho(novoNome) != null) return "Erro: Destino já existe.";

        Arquivo arqOriginal = (Arquivo) noOrigem;
        // Cria um novo objeto (cópia profunda do conteúdo)
        Arquivo copia = new Arquivo(novoNome, diretorioAtual, arqOriginal.getConteudo());
        diretorioAtual.adicionarFilho(copia);
        
        return "Arquivo copiado com sucesso para '" + novoNome + "'.";
    }
}