package frontend;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Responsável pelo Journaling (Log de Operações).
 * Grava em um arquivo .txt real todas as operações realizadas para garantir integridade.
 */
public class Journal {
    private static final String ARQUIVO_LOG = "journal.log";

    public void registrar(String operacao, String alvo) {
        // Formato da data: [30/11/2025 14:00:00]
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dataHora = sdf.format(new Date());

        // Monta a linha do log
        String logEntry = String.format("[%s] OP: %s | ALVO: %s", dataHora, operacao, alvo);

        // Escreve no arquivo real (append = true para não apagar o histórico)
        try (FileWriter fw = new FileWriter(ARQUIVO_LOG, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(logEntry);

        } catch (IOException e) {
            System.err.println("CRÍTICO: Falha ao escrever no Journal: " + e.getMessage());
        }
    }
}