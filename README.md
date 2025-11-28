## Metodologia e Arquitetura

### Estrutura de Classes

Para representar o sistema de arquivos em memória, adotou-se uma estrutura hierárquica baseada no conceito de árvore n-ária, onde diretórios são nós que podem conter N filhos (arquivos ou subdiretórios). A implementação segue os princípios da Programação Orientada a Objetos, utilizando herança e polimorfismo para simplificar o gerenciamento dos elementos.

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
