# Relatório Técnico - Simulador de Sistema de Arquivos

## Parte 1: Introdução ao Sistema de Arquivos

**Um Sistema de Arquivos (File System)** é o componente do sistema operacional responsável por controlar como os dados são armazenados e recuperados. Sem um sistema de arquivos, as informações colocadas em um meio de armazenamento seriam um grande corpo de dados sem saber onde começa ou termina cada pedaço de informação.

Neste simulador, focamos na representação lógica dessa organização. O sistema baseia-se em uma hierarquia, **permitindo o aninhamento de diretórios e a segregação de arquivos**, simulando a experiência de navegação e manipulação encontrada em sistemas como **EXT4 (Linux)** ou **NTFS (Windows).**

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados

Para representar a hierarquia do sistema de arquivos, utilizamos uma estrutura de dados baseada em Árvore N-ária (N-ary Tree). Nesta estrutura:

- O Nó Raiz representa o diretório principal ( `/` ).

- Os Nós Intermediários (galhos) representam os diretórios, que podem conter N filhos.

- Os Nós Folha representam os arquivos, que guardam dados e não possuem filhos.

Computacionalmente, aplicamos uma simplificação do **padrão de projeto Composite.** Isso permite tratar tanto arquivos quanto diretórios de maneira polimórfica, ou seja, ambos são considerados **"Nós do Sistema de Arquivos" (FSNode)**, compartilhando características como nome e diretório pai, mas comportando-se de maneira diferente quanto ao conteúdo (um guarda texto, o outro guarda uma lista de nós).

(Obs: seção sobre Journaling)

## Parte 3: Implementação em java

### Estrutura de Classes

Para representar o sistema de arquivos em memória, adotou-se uma estrutura hierárquica baseada no conceito de **árvore n-ária**, onde diretórios são nós que podem conter N filhos (arquivos ou subdiretórios). A implementação segue os princípios da Programação Orientada a Objetos, utilizando herança e polimorfismo para simplificar o gerenciamento dos elementos.

#### 1. Classe Abstrata FSNode:

- Representa a generalização de qualquer elemento do sistema de arquivos.

- Contém atributos comuns como nome, pai (referência ao diretório superior) e dataCriacao.

- Define o contrato para subclasses, como o método abstrato getTamanho().

#### 2. Classe Arquivo (Folha):

- Estende FSNode. Representa a unidade básica de armazenamento.

- Possui o atributo conteudo (String) simulando os dados do arquivo.

- Seu tamanho é calculado com base no comprimento do conteúdo.

#### 3. Classe Diretorio (Nó Composto):

- Estende FSNode. Representa um contêiner lógico.

- Possui uma lista List<FSNode> filhos, permitindo armazenar tanto arquivos quanto outros diretórios.

- Implementa métodos de manipulação da árvore: **adicionarFilho**, **removerFilho** e **buscarFilho**.

#### 4. Classe FileSystemSimulator (Controlador Lógico):

- Atua como o motor do sistema, mantendo o estado da sessão do usuário.

##### Atributos Principais:

- **root:** O diretório raiz do sistema.

- **diretorioAtual:** Um ponteiro que indica onde as operações do usuário serão executadas.

- **Funcionamento**: Encapsula a lógica de negócio das operações (criar, deletar, renomear). Por exemplo, ao solicitar a criação de um arquivo, esta classe verifica duplicidade de nomes antes de instanciar a classe Arquivo e adicioná-la à lista do diretorioAtual.

### Lógica de Manipulação

As operações são realizadas diretamente na estrutura de dados em memória (RAM). A navegação entre diretórios (cd) atualiza a referência do ponteiro diretorioAtual. As operações de listagem (ls) percorrem a lista de filhos do nó atual, utilizando polimorfismo para exibir diretórios e arquivos de forma distinta.
