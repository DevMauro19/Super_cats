# 🐈 Super Cats — The Feline Graph Chronicles

> 🐾 **Proyecto final del curso de Lenguajes y Compiladores — Universidad EIA.**

Una aventura felina a través de **grafos, caminos mínimos y algoritmos de optimización**.  
La aplicación combina algoritmos clásicos con una interfaz gráfica interactiva inspirada en los protagonistas de nuestra historia: **Pola, Minerva y Limon**. 🐱🐱🐱

---

## 👥 Integrantes del grupo

| 🐱 Integrantes                     |
|------------------------------------|
| Carlos Mauricio Velasco Chavarro🦖 |
| Santiago Perez                     |
| Pablo Escudero                     |

---

## 🎯 Estado actual del proyecto

La aplicación cuenta con una **interfaz gráfica funcional en `src/gui/MainFrame.java`** y permite ejecutar y visualizar las cuatro misiones del proyecto.

### ✨ Funcionalidades principales

- 🗂️ **Pestañas** para las 4 misiones.
- 📥 **Carga de ejemplos** por misión.
- ✏️ **Entrada personalizada** de casos de prueba.
- ▶️ **Ejecución de algoritmos** desde la interfaz.
- 📊 **Presentación visual de resultados**.
- 🗺️ **Vista previa de grafos y grillas**.
- 🎬 **Mini pantalla de evolución** para observar los algoritmos paso a paso.
- 🌙 **Tema oscuro** inspirado en tonos negros, naranjas y blancos.
- 🐈 **Estética temática de gatos** relacionada con la historia del proyecto.
- 🧩 **Componentes gráficos personalizados** para mantener una apariencia consistente.

> 🐾 La GUI transforma el proyecto de una aplicación únicamente orientada a consola en una herramienta visual para **probar, analizar y comprender los algoritmos**.

---

# 🚀 Cómo compilar y ejecutar

La forma recomendada de ejecutar el proyecto es mediante **IntelliJ IDEA**.

### 💻 Desde IntelliJ IDEA

Abrir el proyecto y ejecutar:

```text
gui.MainFrame
```

### 🖥️ Desde la terminal

Desde la raíz del repositorio se puede compilar el proyecto Java completo:

```bash
javac -d out $(find src -name "*.java")
```

Y posteriormente ejecutar:

```bash
java -cp out gui.MainFrame
```

> ⚠️ En Windows, la opción más fiable es ejecutar el proyecto desde IntelliJ IDEA o utilizar una terminal con Java correctamente configurado.

---

# 📁 Estructura del proyecto

```text
src/
├── 🧠 algorithms/
│   ├── mission1/       BFSDFSSolver.java, PathResult.java
│   ├── mission2/       Dijkstra.java, DijkstraResult.java
│   ├── mission3/       BellmanFord.java, FloydWarshall.java,
│   │                    Mission3Solver.java
│   └── mission4/       Kruskal.java, Union.java
│
├── ⚠️ Exceptions/
│   ├── EEntradaInvalida.java
│   ├── EFueraRango.java
│   └── ENumeroNegativo.java
│
├── 🖥️ gui/
│   └── MainFrame.java
│
├── 📥 io/
│   ├── Input.java
│   ├── MisionUnoCaso.java
│   ├── MisionDosCaso.java
│   ├── MisionTresCaso.java
│   ├── Output.java
│   └── ...
│
├── 🧩 model/
│   ├── Edge.java
│   ├── Graph.java
│   ├── Grid.java
│   ├── Punto.java
│   └── WeightedEdge.java
│
├── 🧪 test/
│   ├── BFS_DFS_TEST.java
│   ├── DIJKSTRA_TEST.java
│   ├── FLOYDWARSHALL_BELLMANFORD_TEST.java
│   └── KRUSKAL_TEST.java
│
└── 🖼️ images/
    └── rescuecat.jpg
```

---

# 🗺️ Misiones

La aplicación está dividida en **cuatro misiones**, cada una enfocada en diferentes algoritmos y estructuras de grafos.

| Misión | 🤖 Algoritmos | 🎯 Concepto |
|---|---|---|
| 💣 **Misión 1** | BFS / DFS | Recorridos sobre grillas |
| 🧭 **Misión 2** | Dijkstra | Caminos mínimos |
| 🔄 **Misión 3** | Floyd-Warshall / Bellman-Ford | Caminos mínimos y ciclos negativos |
| 🔗 **Misión 4** | Kruskal / Union-Find | Árbol de expansión mínima |

---

# 💣 Misión 1 — BFS / DFS sobre grilla

La primera misión se resuelve mediante `BFSDFSSolver` y `PathResult`.

### 🔍 Características

- 🌊 Implementa **Búsqueda en Amplitud (BFS)**.
- 🕳️ Implementa **Búsqueda en Profundidad (DFS)**.
- 💣 Maneja una grilla con bombas.
- 🧱 Evita la recursión profunda en DFS para soportar grillas grandes sin `StackOverflowError`.
- 🔢 Mantiene un orden fijo de vecinos para garantizar resultados deterministas.
- 📥 Utiliza `io.Input` para procesar los casos.
- 📤 Utiliza `io.Output` para formatear los resultados.
- 🗺️ Permite visualizar la exploración directamente desde la GUI.

---

# 🧭 Misión 2 — Dijkstra

La segunda misión calcula **rutas mínimas con pesos no negativos**.

### ⚙️ Características

- 🕸️ Utiliza la estructura genérica `Graph`.
- 📏 Calcula el costo mínimo entre nodos.
- 🧭 `DijkstraResult` almacena:
  - el camino encontrado,
  - el costo total.
- 🎬 La GUI permite visualizar el proceso de Dijkstra.
- 🟠 El camino final puede visualizarse directamente sobre el grafo.

---

# 🔄 Misión 3 — Floyd-Warshall y Bellman-Ford

La tercera misión combina dos algoritmos clásicos para resolver problemas de caminos mínimos.

### 🌐 Floyd-Warshall

`FloydWarshall` calcula las **distancias mínimas entre cualquier par de nodos**.

### ⚡ Bellman-Ford

`BellmanFord` permite:

- 🔍 Relajar caminos.
- ♻️ Detectar ciclos negativos.
- ⚠️ Identificar situaciones donde las distancias no están acotadas.

### 🧠 `Mission3Solver`

`Mission3Solver` centraliza la resolución de los casos de esta misión según la entrada proporcionada.

### 🎬 Visualización

La interfaz muestra las **rondas de relajación** de Bellman-Ford para observar cómo evoluciona el cálculo.

---

# 🔗 Misión 4 — Kruskal

La cuarta misión implementa un **Árbol de Expansión Mínima (MST)** mediante:

- 🌳 `Kruskal`
- 🔗 `Union` / Union-Find

### ⚙️ Funcionamiento

1. 📊 Ordena las aristas por peso.
2. 🔍 Evalúa cada arista.
3. ✅ Selecciona las aristas que no forman ciclos.
4. ❌ Rechaza las aristas que generarían un ciclo.
5. 🌳 Construye el árbol de expansión mínima.
6. 📤 Presenta el resultado final.

La GUI permite observar el proceso de evaluación de las aristas **paso a paso**. 🎬

---

# 🧩 Componentes clave del proyecto

## 🕸️ `model.Graph`

Es el modelo común utilizado principalmente por las misiones **2, 3 y 4**.

Mantiene:

- 📋 Lista de adyacencia para recorridos por nodos.
- 🔗 Estructura de aristas planas para algoritmos globales.
- ↔️ Soporte para grafos dirigidos y no dirigidos.

---

## 🗺️ `model.Grid` y `model.Punto`

Representan la grilla utilizada en la **Misión 1**.

### `Grid`

- 📐 Guarda las dimensiones.
- 💣 Representa las celdas con bombas.

### `Punto`

- 📍 Encapsula las coordenadas `(fila, columna)`.
- 🧭 Permite representar inicio, destino y recorridos.

Esto evita duplicar lógica relacionada con las coordenadas dentro de los algoritmos.

---

## 📥 `io.Input` y `io.Output`

Son los componentes encargados de la comunicación de entrada y salida.

### 📥 `Input`

- Lee el texto introducido.
- 🔍 Valida el formato.
- 🧩 Genera los casos de prueba.

### 📤 `Output`

- Formatea los resultados.
- 🖥️ Permite reutilizar la salida tanto en consola como en la GUI.

La estructura se reutiliza entre las diferentes misiones para mantener consistencia.

---

# 🖥️ `gui.MainFrame`

Es el componente principal de la interfaz gráfica.

Incluye:

- 🐈 4 pestañas de misión.
- 📥 Panel de entrada.
- 📤 Panel de resultados.
- 🗺️ Visualización de mapas y grafos.
- 🎬 Animaciones de los algoritmos.
- ▶️ Botones de ejecución.
- 🔄 Controles para las animaciones.
- 🌙 Tema visual oscuro.
- 🟠 Esquema de colores negro, naranja y blanco.
- 🐾 Identidad visual inspirada en gatos.

---

# 🧪 Tests del proyecto

En `src/test` se encuentran pruebas de referencia para los algoritmos principales.

### 🔬 Pruebas disponibles

- 🧪 `BFS_DFS_TEST.java`
- 🧪 `DIJKSTRA_TEST.java`
- 🧪 `FLOYDWARSHALL_BELLMANFORD_TEST.java`
- 🧪 `KRUSKAL_TEST.java`

Estos tests permiten validar el comportamiento de los algoritmos y sirven como referencia durante el desarrollo y la evaluación del proyecto.

---

# ✨ Cambios recientes incorporados

- 🎨 Actualización completa de la interfaz a un diseño oscuro.
- 🟠 Incorporación de una paleta basada en negro, naranja y blanco.
- 🐈 Integración de la temática de gatos.
- ▶️ Botones para **Cargar ejemplo**, **Ejecutar** y **Limpiar**.
- ✏️ Soporte para casos personalizados.
- 🗺️ Visualización de mapas y grafos.
- 🎬 Mini pantalla de evolución.
- ⏯️ Controles de reproducción y velocidad de las animaciones.
- 🧠 Visualización del avance de los algoritmos.
- 🖥️ Integración completa de los algoritmos con la GUI.
- 🧩 Componentes gráficos personalizados para mantener la identidad visual.
- 🐾 La aplicación dejó de ser únicamente una herramienta de consola y ahora funciona como una interfaz gráfica completa para probar los algoritmos.

---

# 🎮 Recomendación de uso

Para probar una misión:

```text
       🐈
        │
        ▼
  1️⃣ Seleccionar misión
        │
        ▼
  2️⃣ Cargar ejemplo
        │
        ▼
  3️⃣ Ejecutar ▶️
        │
        ├───────────────┐
        ▼               ▼
   📤 Resultado      🗺️ Visualización
        │               │
        └───────┬───────┘
                ▼
        🎬 Evolución
```

### 🐾 Flujo recomendado

1. 🗂️ **Seleccionar una misión.**
2. 📥 **Cargar el ejemplo** del sistema o introducir un caso personalizado.
3. ▶️ **Ejecutar la misión.**
4. 📤 Revisar el **resultado**.
5. 🗺️ Observar el **mapa o grafo** generado.
6. 🎬 Utilizar la **mini pantalla de evolución** para comprender cómo avanza el algoritmo.

---

# 🐱 Nuestros protagonistas

La historia de **Super Cats** está acompañada por nuestros tres personajes principales:

> 🐈 **Pola**  
> 🐈 **Minerva**  
> 🐈 **Limon**

Ellos acompañan las diferentes misiones mientras los algoritmos recorren grafos, buscan caminos y construyen soluciones. 🧭🕸️

---

# 🖼️ Referencia de Carlitos :)

<p align="center">
  <img src="src/images/rescuecat.jpg" alt="Rescue Cat" width="400">
</p>

🐾 *Porque ningún proyecto de Super Cats estaría completo sin un gato de referencia.*

---

## 🐈‍⬛ Super Cats

```text
     /\_/\\
    ( o.o )
     > ^ <

  THE FELINE GRAPH
      CHRONICLES
```

### 🧠 Algorithms + 🐈 Cats = ❤️

---

<p align="center">

**🐾 Super Cats — The Feline Graph Chronicles 🐾**

*Universidad EIA · Lenguajes y Compiladores*

</p>
