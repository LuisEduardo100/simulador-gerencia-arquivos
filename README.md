# Relatório Técnico - Simulador de Sistema de Arquivos

## Parte 1: Introdução ao Sistema de Arquivos

**Um Sistema de Arquivos (File System)** é o componente do sistema operacional responsável por controlar como os dados são armazenados e recuperados. Sem um sistema de arquivos, as informações colocadas em um meio de armazenamento seriam um grande corpo de dados sem saber onde começa ou termina cada pedaço de informação.

Neste simulador, focamos na representação lógica dessa organização. O sistema baseia-se em uma hierarquia, **permitindo o aninhamento de diretórios e a segregação de arquivos**, simulando a experiência de navegação e manipulação encontrada em sistemas como **EXT4 (Linux)** ou **NTFS (Windows).**

**Journaling e Integridade de Dados:**
Para mitigar riscos de inconsistência, o sistema implementa um mecanismo de **Journaling**. Esta técnica, amplamente utilizada em sistemas modernos (como EXT4 e XFS), consiste em manter um registro sequencial (log) das operações antes que elas sejam efetivadas na estrutura principal. No contexto deste simulador, adotamos a abordagem de **Write-Ahead Logging (WAL)**: a intenção da mudança é gravada em um arquivo físico (`journal.log`) antes que a manipulação do objeto em memória ocorra. Isso garante um rastro auditável de todas as ações realizadas pelo usuário.

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados

Para representar a hierarquia do sistema de arquivos, utilizamos uma estrutura de dados baseada em Árvore N-ária (N-ary Tree). Nesta estrutura:

- O **Nó Raiz** representa o diretório principal ( `/` ).
- Os **Nós Intermediários** (galhos) representam os diretórios, que podem conter N filhos.
- Os **Nós Folha** representam os arquivos, que guardam dados e não possuem filhos.

Computacionalmente, aplicamos uma simplificação do **padrão de projeto Composite.** Isso permite tratar tanto arquivos quanto diretórios de maneira polimórfica, ou seja, ambos são considerados **"Nós do Sistema de Arquivos" (FSNode)**, compartilhando características como nome e diretório pai, mas comportando-se de maneira diferente quanto ao conteúdo (um guarda texto, o outro guarda uma lista de nós).

### Mecanismo de Journaling (Logs)

Diferente da estrutura de diretórios que reside na memória volátil (RAM), o Journaling é o componente de **persistência** do simulador.

- **Formato de Armazenamento:** Arquivo de texto plano (`journal.log`), acessível externamente ao simulador.
- **Estrutura do Registro:** Cada linha do log segue um padrão rígido para facilitar a leitura futura: `[TIMESTAMP] OP: <TIPO_OPERAÇÃO> | ALVO: <NOME_DO_ARQUIVO>`.
- **Operação de Append:** O sistema utiliza streams de escrita em modo *append* (acréscimo), garantindo que o histórico anterior nunca seja sobrescrito, preservando a linha do tempo das modificações.

## Parte 3: Implementação em Java

### Estrutura de Classes

Para representar o sistema de arquivos em memória, adotou-se uma estrutura hierárquica baseada no conceito de **árvore n-ária**, onde diretórios são nós que podem conter N filhos (arquivos ou subdiretórios). A implementação segue os princípios da Programação Orientada a Objetos, utilizando herança e polimorfismo para simplificar o gerenciamento dos elementos.

#### 1. Classe Abstrata FSNode:
- Representa a generalização de qualquer elemento do sistema de arquivos.
- Contém atributos comuns como nome, pai (referência ao diretório superior) e `dataCriacao`.
- Define o contrato para subclasses, como o método abstrato `getTamanho()`.

#### 2. Classe Arquivo (Folha):
- Estende `FSNode`. Representa a unidade básica de armazenamento.
- Possui o atributo `conteudo` (String) simulando os dados do arquivo.
- Seu tamanho é calculado com base no comprimento do conteúdo.

#### 3. Classe Diretorio (Nó Composto):
- Estende `FSNode`. Representa um contêiner lógico.
- Possui uma lista `List<FSNode> filhos`, permitindo armazenar tanto arquivos quanto outros diretórios.
- Implementa métodos de manipulação da árvore: **adicionarFilho**, **removerFilho** e **buscarFilho**.

#### 4. Classe FileSystemSimulator (Controlador Lógico):
- Atua como o motor do sistema, mantendo o estado da sessão do usuário.
- **Atributos Principais:**
    - `root`: O diretório raiz do sistema.
    - `diretorioAtual`: Um ponteiro que indica onde as operações do usuário serão executadas.
- **Funcionamento:** Encapsula a lógica de negócio das operações (criar, deletar, renomear). Por exemplo, ao solicitar a criação de um arquivo, esta classe verifica duplicidade de nomes antes de instanciar a classe `Arquivo` e adicioná-la à lista do `diretorioAtual`.

#### 5. Classe Journal (Persistência):
- Responsável pela camada de I/O (Input/Output) com o sistema operacional hospedeiro.
- Utiliza as classes `FileWriter` e `PrintWriter` do Java para manipular o arquivo físico de log.
- Garante que, independentemente do sucesso da operação em memória, a *tentativa* de operação seja registrada com data e hora precisas.

#### 6. Classe Main (Interface Shell/CLI):
- Implementa a interface com o usuário via terminal (**Command Line Interface**).
- Contém o loop principal (`while(true)`) que aguarda, interpreta e roteia os comandos.
- **Parser de Comandos:** Realiza o tratamento das Strings de entrada, separando o comando principal (ex: `nova-pasta`) de seus argumentos.
- **Adaptação de Usabilidade:** Para tornar o sistema mais intuitivo, os comandos tradicionais do Unix (`mkdir`, `rm`, `ls`) foram abstraídos para verbos em português (`nova-pasta`, `apagar`, `listar`), facilitando a curva de aprendizado.

### Lógica de Manipulação

As operações são realizadas diretamente na estrutura de dados em memória (RAM). A navegação entre diretórios (`entrar`) atualiza a referência do ponteiro `diretorioAtual`. As operações de listagem (`listar`) percorrem a lista de filhos do nó atual, utilizando polimorfismo para exibir diretórios e arquivos de forma distinta.

## Parte 4: Instalação e Funcionamento

Para garantir a execução correta do simulador, siga as instruções abaixo. O projeto não requer bibliotecas externas, utilizando apenas o JDK padrão.

### Requisitos
- **Java JDK 8** ou superior instalado.
- Terminal/Console do sistema operacional (CMD, PowerShell ou Bash).

### Passo a Passo de Execução

1. **Compilação:** Navegue até a pasta raiz do projeto (`src`) e compile todas as classes:
   ```bash
   javac -encoding UTF-8 frontend/*.java backend/*.java
2. **Execução:** Inicie a classe principal localizada no pacote frontend:
   ```bash
   java -cp . frontend.Main
3. **Verificação do Journaling:** Após executar alguns comandos no simulador, verifique se o arquivo `journal.log` foi criado. Você pode visualizar o conteúdo dele diretamente pelo terminal:
   ```bash
   # No Windows (CMD/PowerShell):
   type journal.log

   # No Linux ou Mac:
   cat journal.log
### Guia de Comandos

O simulador utiliza uma interface textual baseada em comandos Unix. Abaixo, a lista de operações disponíveis:

| Comando | Argumento | Descrição | Exemplo de Uso |
| :--- | :--- | :--- | :--- |
| **mkdir** | `<nome>` | Cria um novo diretório na pasta atual. | `mkdir documentos` |
| **touch** | `<nome>` | Cria um arquivo (solicitará o conteúdo em seguida). | `touch notas.txt` |
| **ls** | - | Exibe o conteúdo do diretório atual. | `ls` |
| **cd** | `<nome>` | Navega para uma subpasta. Use `..` para voltar. | `cd documentos` |
| **rm** | `<nome>` | Remove um arquivo ou diretório. | `rm notas.txt` |
| **mv** | `<antigo> <novo>` | Altera o nome de um item (Renomear). | `mv doc.txt tcc.txt` |
| **cp** | `<origem> <destino>` | Cria uma cópia do arquivo com novo nome. | `cp tcc.txt tcc_bkp.txt` |
| **exit** | - | Encerra o simulador. | `exit` |