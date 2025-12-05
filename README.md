# Relatório Técnico - Simulador de Sistema de Arquivos

## Parte 1: Introdução ao Sistema de Arquivos

Um Sistema de Arquivos (File System) é o componente do sistema
operacional responsável por controlar como os dados são armazenados e
recuperados. Sem ele, as informações seriam apenas um bloco contínuo de
dados.

Este simulador modela essa lógica interna usando uma **hierarquia de
diretórios e arquivos**, semelhante aos sistemas EXT4 (Linux) e NTFS
(Windows).

### Persistência e Integridade de Dados

O sistema utiliza duas camadas de persistência:

#### 1. **Journaling (Write-Ahead Logging -- WAL)**

Antes de qualquer operação ser aplicada na memória, sua intenção é
registrada no arquivo `journal.log`.\
Formato:

    [TIMESTAMP] OP: <TIPO> | ALVO: <NOME>

#### 2. **Serialização de Estado (Snapshot)**

Ao encerrar o simulador, todo o estado do sistema (árvore de diretórios
e arquivos) é serializado para `filesystem.dat`.\
Ao iniciar, o arquivo é carregado automaticamente, restaurando a sessão
anterior.

---

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados -- Árvore N-ária

- **Nó Raiz ( / )**: diretório principal\
- **Nós Intermediários**: diretórios\
- **Nós Folha**: arquivos

A arquitetura segue uma simplificação do padrão **Composite**, tratando
arquivos e diretórios como tipos de um mesmo conceito: **FSNode**.

### Mecanismo de Persistência Híbrida

- **journal.log** → texto simples, histórico permanente\
- **filesystem.dat** → binário, contém toda a estrutura do sistema

---

## Parte 3: Implementação em Java

### 1. Classe Abstrata `FSNode`

- Generaliza arquivos e diretórios\
- Atributos: `nome`, `pai`, `dataCriacao`\
- Implementa `Serializable`

### 2. Classe `Arquivo`

- Folha da árvore\
- Contém `conteudo` (String)

### 3. Classe `Diretorio`

- Contém lista `List<FSNode> filhos`\
- Permite adicionar, remover e buscar nós

### 4. Classe `FileSystemSimulator`

- Motor central do sistema\
- Gerencia:
  - `root`
  - `diretorioAtual`
  - salvamento / carregamento automático
- Implementa leitura com `lerArquivo()`

### 5. Classe `Journal`

- Registra operações em `journal.log` usando `FileWriter (append)`

### 6. Classe `Main`

- Interface do usuário (CLI)\
- Loop principal (`while(true)`)\
- Parser de comandos\
- Chama salvamento automático ao usar `exit`

---

## Parte 4: Instalação e Execução

### Requisitos

- Java **JDK 8+**
- Terminal (CMD/PowerShell/Bash)

### Método 1 (Recomendado) -- Execução Automática

#### Windows

    executar.bat

#### Linux/macOS

```bash
chmod +x executar.sh
./executar.sh
```

---

### Método 2 -- Compilação Manual

```bash
cd src
javac -cp . Main.java backend/*.java frontend/*.java
java -cp . Main
```

---

## Guia de Comandos

mkdir `<nome>` Cria um diretório

touch `<nome>` Cria um arquivo

cat `<nome>` Lê um arquivo

ls \- Lista conteúdo

cd `<nome>` Entra em diretório

rm `<nome>` Remove arquivo/pasta

mv `<antigo> <novo>` Renomeia

cp `<origem> <dest>` Copia arquivo

save \- Salva estado

exit \- Salva e encerra

---

Projeto desenvolvido para a disciplina de **Sistemas Operacionais**.
