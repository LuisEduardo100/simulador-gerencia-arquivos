package frontend;

import backend.FSSimulador; // Importando a classe do seu amigo
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Inicializa o Backend (Memória)
        FSSimulador simulador = new FSSimulador();

        // 2. Inicializa o Sistema de Journaling (Logs)
        Journal journal = new Journal();

        System.out.println("=== SIMULADOR DE SISTEMA DE ARQUIVOS (v1.0) ===");
        System.out.println("Digite 'help' para ver os comandos.");

        // Loop Infinito (Shell)
        while (true) {
            // Mostra o diretório atual no prompt, estilo Linux (ex: /home/user > )
            String caminhoAtual = simulador.getDiretorioAtual().getCaminhoCompleto();
            System.out.print(caminhoAtual + " $ ");

            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) continue;

            // Divide o comando dos argumentos (ex: "mkdir pasta" -> ["mkdir", "pasta"])
            String[] partes = entrada.split(" ");
            String comando = partes[0];
            String argumento = partes.length > 1 ? partes[1] : "";

            // Lógica de Comandos
            switch (comando) {
                case "exit":
                    System.out.println("Encerrando simulador...");
                    scanner.close();
                    System.exit(0);
                    break;

                case "help":
                    mostrarAjuda();
                    break;

                case "ls":
                    // Listar não precisa necessariamente de journal, mas é bom para debug
                    System.out.println(simulador.listarConteudo());
                    break;

                case "cd":
                    // Navegação
                    System.out.println(simulador.mudarDiretorio(argumento));
                    break;

                case "mkdir":
                    if (verificarArg(argumento)) {
                        // 1. Grava no Journal (Write-Ahead Logging)
                        journal.registrar("CRIAR_DIR", argumento);
                        // 2. Executa na memória
                        System.out.println(simulador.criarDiretorio(argumento));
                    }
                    break;

                case "rm":
                    if (verificarArg(argumento)) {
                        journal.registrar("APAGAR", argumento);
                        System.out.println(simulador.apagar(argumento));
                    }
                    break;

                case "touch": // Criar arquivo
                    if (verificarArg(argumento)) {
                        System.out.print("Digite o conteúdo do arquivo: ");
                        String conteudo = scanner.nextLine();

                        journal.registrar("CRIAR_ARQ", argumento + " (Size: " + conteudo.length() + ")");
                        System.out.println(simulador.criarArquivo(argumento, conteudo));
                    }
                    break;

                case "mv": // Renomear
                    if (partes.length < 3) {
                        System.out.println("Uso: mv <nomeAntigo> <nomeNovo>");
                    } else {
                        String novoNome = partes[2];
                        journal.registrar("RENOMEAR", argumento + " -> " + novoNome);
                        System.out.println(simulador.renomear(argumento, novoNome));
                    }
                    break;

                case "cp": // Copiar
                    if (partes.length < 3) {
                        System.out.println("Uso: cp <origem> <destino>");
                    } else {
                        String destino = partes[2];
                        journal.registrar("COPIAR", argumento + " -> " + destino);
                        System.out.println(simulador.copiarArquivo(argumento, destino));
                    }
                    break;

                default:
                    System.out.println("Comando não reconhecido: " + comando);
            }
        }
    }

    // Função auxiliar para verificar se o usuário digitou o nome do arquivo/pasta
    private static boolean verificarArg(String arg) {
        if (arg.isEmpty()) {
            System.out.println("Erro: Faltando argumento (nome do arquivo/diretório).");
            return false;
        }
        return true;
    }

    private static void mostrarAjuda() {
        System.out.println("\n--- Comandos Disponíveis ---");
        System.out.println("mkdir <nome>       : Cria um diretório");
        System.out.println("touch <nome>       : Cria um arquivo (pede conteúdo depois)");
        System.out.println("rm <nome>          : Apaga arquivo ou diretório");
        System.out.println("cd <nome>          : Entra no diretório (ou 'cd ..' para voltar)");
        System.out.println("ls                 : Lista o conteúdo atual");
        System.out.println("mv <antigo> <novo> : Renomeia um arquivo/pasta");
        System.out.println("cp <origem> <novo> : Copia um arquivo");
        System.out.println("exit               : Sai do simulador");
        System.out.println("----------------------------\n");
    }
}