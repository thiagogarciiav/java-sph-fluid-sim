<h1>
    Java SPH Fluid Simulator (Dam Break)
    <img
        src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/java/java-original.svg"
        alt="Java"
        title="Java"
        width="40px"
        style="padding-right:10px;"
        align="center"
    />
</h1>

Uma simulação de fluidos em tempo real escrita em **Java puro**, utilizando o método de **Hidrodinâmica de Partículas Suavizadas (SPH)** para resolver numericamente as equações de Navier-Stokes. O projeto apresenta uma implementação eficiente com **Spatial Hashing** e visualização dinâmica via **JavaFX**.

---

## ✨ Funcionalidades

- **Física Realista:** Simula o rompimento de uma barragem (*Dam Break*) com comportamento de fluido incompressível.
- **Otimização Avançada:** Utiliza **Spatial Hashing** (Grid Uniforme) para reduzir a complexidade da busca de vizinhos de \( O(n^2) \) para \( O(n) \), garantindo performance mesmo com centenas de partículas.
- **Mapeamento de Cores Dinâmico:** As partículas mudam de cor (de azul para vermelho) conforme a pressão interna aumenta, permitindo visualizar as ondas de choque do fluido.
- **Código Auto-Contido:** Atualmente implementado em um único arquivo `.java` para facilitar a execução e entendimento.
- **Pronto para Paralelismo:** Estrutura preparada para evoluir para processamento paralelo com `Fork/Join` ou GPU via OpenCL.

---

## 🧠 A Matemática por Trás do Código

O projeto resolve as equações de Navier-Stokes para fluidos débeis (fracamente) compressíveis usando o método SPH. Aqui estão os pilares matemáticos implementados:

### 1. Equação de Estado (Tait)
Para calcular a pressão a partir da densidade:

<a>
   <img
      alt="Image1"
      width="40px"
      style="padding-right:10px;"
      align="center"
      src="https://github.com/user-attachments/assets/cc684800-f6cc-483b-89e5-e7eff46cac8c" 
   />
</a>

 
### 2. Funções Kernel (Suavização)
- **Kernel Poly6** (para densidade): Garante suavidade e evita singularidades.
- **Kernel Spiky** (para gradiente de pressão): Utilizado no cálculo das forças para melhor estabilidade numérica.

### 3. Cálculo da Densidade

<a>
   <img
      alt="Image2"
      width="40px"
      style="padding-right:10px;"
      align="center"
      src="https://github.com/user-attachments/assets/cc684800-f6cc-483b-89e5-e7eff46cac8c](https://github.com/user-attachments/assets/248611ee-c7d7-4dff-9254-ba3ee0babc75" 
   />
</a>

### 4. Forças de Pressão e Viscosidade
A aceleração de cada partícula é calculada somando as contribuições dos vizinhos:

<a>
   <img
      alt="Image3"
      width="40px"
      style="padding-right:10px;"
      align="center"
      src="https://github.com/user-attachments/assets/cc684800-f6cc-483b-89e5-e7eff46cac8c](https://github.com/user-attachments/assets/c6181b35-7b33-4c11-a740-d84128c0f772" 
   />
</a>

---

## ⚙️ Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- **JDK 17+** (Recomendo Eclipse Adoptium ou Oracle OpenJDK).
- **JavaFX SDK 17 ou 21** (Baixe em [Gluon HQ](https://gluonhq.com/products/javafx/)).

---

## 🚀 Como Executar (Passo a Passo)

### Opção 1: Usando o Terminal (Sem IDE)

1. **Clone o repositório** (ou copie o arquivo `SPHSimulator.java` para uma pasta).
   ```bash
   git clone https://github.com/seuusuario/java-sph-simulator.git
   cd java-sph-simulator

### Opção 2: Usando IntelliJ IDEA (Recomendado para desenvolvimento)

1. Abra o projeto e crie uma nova classe SPHSimulator.

2. Cole o código completo.

3. Vá em Run → Edit Configurations.

4. Em VM options, adicione:
    ```bash
    --module-path /caminho/para/javafx-sdk-21/lib --add-modules javafx.controls

### Opção 3: Usando Maven (Mais prático)

1. Se preferir, crie um `pom.xml` com a dependência do JavaFX e execute:
    ```bash
    mvn clean compile exec:java

## 🎮 Como Interagir com a Simulação
Atualmente, a simulação roda automaticamente mostrando o efeito Dam Break:

Água represada no lado esquerdo que se rompe e escorre pelo chão.

A cor das partículas indica a pressão (azul = baixa, vermelho = alta).

O canto superior esquerdo exibe o número de partículas e a pressão média do sistema.

(Sinta-se à vontade para modificar o código e adicionar interação com o mouse, como adicionar partículas ao clicar!)