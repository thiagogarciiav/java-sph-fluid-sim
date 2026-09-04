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
      width="250px"
      style="padding-right:10px;"
      align="center"
      src="https://github.com/user-attachments/assets/cc684800-f6cc-483b-89e5-e7eff46cac8c" 
   />
</a>

<br/>
<br/>

* **Legenda:** Se a densidade (***p***) da partícula aumenta, a pressão (***P***) dispara de forma exponencial. ***B*** é a rigidez.
 
### 2. Funções Kernel (Suavização)
- **Kernel Poly6** (para densidade): Garante suavidade e evita singularidades.
- **Kernel Spiky** (para gradiente de pressão): Utilizado no cálculo das forças para melhor estabilidade numérica.

### 3. Cálculo da Densidade
A densidade de um partícula é a soma das massas de todas as partículas vizinhas, ponderada por uma função de suavização (Kernel):

<a>
   <img
      alt="Image2"
      width="250px"
      style="padding-right:10px;"
      align="center"
      src="https://github.com/user-attachments/assets/27fd84f0-9a24-4053-8127-ba11cfef0389" 
   />
</a>

<br/>
<br/>

* **Legenda:** Para a Partícula ***i***, somamos a contribuição de massa (***mj***) de cada vizinho ***j***. Quanto mais próximos, maior o peso definido pelo Kernel ***W***.

### 4. Força de Pressão
Essa é a força que impede o fluido de se comprimir infinitamente e gera as ondas:

<a>
   <img
      alt="Image3"
      width="280px"
      style="padding-right:10px;"
      align="center"
      src="https://github.com/user-attachments/assets/44d45613-1f41-4f8d-8094-b4f5c64910ce" 
   />
</a>

<br/>
<br/>

* **Legenda:** A aceleração (***ai***) causada pela pressão é o gradiente (a "inclinação") do Kernel. Se a pressão da partícula ***i*** é maior que a do vizinho ***j***, ela será empurrada para longe dele.

### 5. Força de Viscosidade
Essa força imita o atrito interno da água, fazendo as partículas com velocidades diferentes se igualarem:

<a>
   <img
      alt="Image4"
      width="280px"
      style="padding-right:10px;"
      align="center"
      src="https://github.com/user-attachments/assets/4e364df5-dedc-45f4-8fa9-1d2b39b355e1" 
   />
</a>

<br/>
<br/>

* **Legenda:** Se o vizinho está mais rápido que você, ele puxa você para frente. Se está mais lento, ele freia você. O fator ***v*** é a constante de viscosidade.
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
