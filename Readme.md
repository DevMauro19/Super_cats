# Super Cats — The Feline Graph Chronicles

Proyecto final del curso Lenguajes y Compiladores — Universidad EIA.
Cuatro misiones basadas en grafos (BFS/DFS, Dijkstra, Floyd-Warshall + Bellman-Ford y Kruskal) resueltas en Java, con una GUI temática inspirada en Pola, Minerva y el villano Limon.

## Integrantes del grupo


- [Carlos Mauricio Velasco Chavarro]
- [Santiago Perez]
- [Pablo Escudero]

## Cómo compilar y ejecutar

> Para compilar el proyecto debe hacerse de la siguiente manera, primero clonaremos el proyecto y después ejecutaremos el mainFrame.java

## Estructura del proyecto 

```text
src/
├── algorithms/
│   ├── mission1/       BFSDFSSolver.java, PathResult.java
│   ├── mission2/       Dijkstra.java
│   ├── mission3/       BellmanFord.java, FloydWarshall.java
│   └── mission4/       Kruskal.java, Union.java
├── Exceptions/
│   ├── EEntradaInvalida.java   (checked — errores de formato de entrada)
│   ├── EFueraRango.java        (unchecked — invariante interno del modelo)
│   └── ENumeroNegativo.java    (checked — invariante interno del modelo)
├── gui/
│   └── MainFrame.java
├── io/
│   ├── Input.java          Parser compartido: un método leerMisionN por misión
│   ├── MisionUnoCaso.java  Contenedor (Grid, inicio, destino) para un caso de Misión 1
│   └── Output.java         Formateador de salida: un método formatearMisionN por misión
├── model/
│   ├── Edge.java           Arista dirigida dentro de la lista de adyacencia de Graph
│   ├── Graph.java          Grafo genérico (dirigido o no), reutilizado en Misiones 2, 3 y 4
│   ├── Grid.java           Grilla con bombas, usada solo en Misión 1
│   ├── Punto.java          Coordenada (fila, columna) inmutable, usada en Misión 1
│   └── WeightedEdge.java   Arista plana (from, to, weight), para Kruskal y Bellman-Ford
└── test/
    ├── BFS_DFS_TEST.java
    ├── DIJKSTRA_TEST.java
    ├── FLOYDWARSHALL_BELLMANFORD_TEST.java
    └── KRUSKAL_TEST.java
```

## Clases más importantes y qué hacen

### `model.Graph`
Es el corazón del proyecto para las misiones 2, 3 y 4. Mantiene la estructura del grafo y sincroniza dos representaciones:

- lista de adyacencia para recorridos por nodo (`Edge`)
- lista plana de aristas para algoritmos globales (`WeightedEdge`)

Esto permite reutilizar el mismo modelo para Dijkstra, Floyd-Warshall, Bellman-Ford y Kruskal sin crear grafos distintos para cada algoritmo.

### `model.Grid`
Representa la grilla de la misión 1. Guarda las dimensiones y el conjunto de celdas con bombas. Permite validar si una posición es válida y si es transitable. Es un modelo orientado a la geometría del mapa, no al perfil de un algoritmo de caminos.

### `model.Punto`
Encapsula una coordenada `(fila, columna)` para la misión 1. Sirve para representar ubicaciones de inicio, destino y cualquier celda accesible en la grilla.

### `model.Edge` y `model.WeightedEdge`
Son las aristas del grafo. `Edge` describe conexiones de vecinos para recorridos por lista de adyacencia, mientras que `WeightedEdge` conserva `(from, to, weight)` como una estructura más útil para ordenamiento y relajación de costos.

### `algorithms.mission1.BFSDFSSolver`
Resuelve la misión de rescate de Nina. Implementa BFS y DFS sobre una grilla sin construir una lista de adyacencia explícita, calculando los vecinos al vuelo para evitar gastar memoria con millones de nodos. Es la clase central de la misión 1.

### `algorithms.mission2.Dijkstra`
Calcula el camino mínimo con pesos no negativos. Se apoya en la representación del `Graph` y en la estructura de adyacencia para relajación de costos.

### `algorithms.mission3.FloydWarshall` y `BellmanFord`
Son las soluciones para rutas entre todos los pares y para detectar ciclos negativos. Cada una usa la misma abstracción del grafo, pero con diferentes enfoques. `FloydWarshall` trabaja con matriz de distancias; `BellmanFord` relaja todas las aristas repetidas veces.

### `algorithms.mission4.Kruskal` y `Union`
Se encargan del árbol de expansión mínima. `Kruskal` ordena todas las aristas y selecciona las mejores que no formen ciclos; `Union` aporta la lógica de unión de componentes para verificar si dos nodos están conectados.

### `io.Input`
Es el punto de entrada para interpretar texto plano del usuario o de pruebas. Lee tokens, valida el formato y construye los casos de prueba de cada misión. Es la clase encargada de convertir texto en objetos del dominio.

### `io.Output`
Formatea la salida final para cada misión. En lugar de duplicar la lógica en cada algoritmo, centraliza la representación textual de los resultados y mantiene la estructura consistente para la GUI o la consola.

### `io.MisionUnoCaso`, `MisionDosCaso`, `MisionTresCaso`
Son contenedores de cada caso específico. Guardan la entrada ya transformada a objetos reutilizables para cada misión (grillas, grafos, nodos de origen y destino).

### `exceptions.*`
Encapsulan todas las fallas de validación del programa. Se separan por tipo de error:

- `EEntradaInvalida`: texto mal formado, tokens faltantes o fuera de rango durante parsing.
- `ENumeroNegativo`: se lanza cuando un valor numérico no puede ser negativo por invariante del modelo.
- `EFueraRango`: protege índices o nodos no válidos dentro de una estructura como `Graph` o `Grid`.

### `gui.MainFrame`
Es la ventana principal de la interfaz gráfica. Aquí se debería conectar la entrada del usuario, la selección de misión, el procesamiento de datos y la visualización de resultados.

## Decisiones de diseño tomadas

- `Graph` único y genérico para las Misiones 2, 3 y 4.

  Se construye como `Graph(int nodeCount, boolean directed)`, en vez de crear un grafo por misión. Mantiene dos vistas internas sincronizadas:

  - lista de adyacencia para recorridos por nodo
  - lista plana de aristas para algoritmos que iteren sobre todas las aristas

- `Grid` y `Punto` separados de `Graph` para la Misión 1.

  La grilla puede llegar a 10^6 celdas, así que los vecinos se calculan al vuelo a partir de `(fila, columna)` en vez de materializar una lista de adyacencia explícita, ahorrando memoria.

- DFS no recursivo en `BFSDFSSolver`.

  Simula el call stack manualmente para reproducir el orden de visita requerido por el enunciado y evitar `StackOverflowError` en grillas grandes.

- Estructuras internas con arreglos primitivos.

  En lugar de `HashSet<Punto>` o `HashMap<Punto, Punto>`, se usan `boolean[]` y `int[]` indexados con `fila * columnas + columna`, según convenga, para reducir el costo de boxing y mejorar el rendimiento.

- Excepciones en dos capas.

  - `ENumeroNegativo` y `EFueraRango` protegen invariantes del modelo.
  - `EEntradaInvalida` protege el texto entrante desde la GUI y debe ser capturada explícitamente para mostrar un mensaje legible.

- Un solo `Input` y un solo `Output` para todas las misiones.

  Cada misión agrega su método estático (`leerMisionN` / `formatearMisionN`) reutilizando el mismo tokenizador. Esto mantiene el parser centralizado y evita duplicación.

## Al menos un test por algoritmo

En `src/test` se encuentran pruebas de referencia para los algoritmos principales. Actualmente `BFS_DFS_TEST.java` cubre BFS y DFS de la misión 1 contra el ejemplo del enunciado (`BFS 18 DFS 32`). Los demás archivos (`DIJKSTRA_TEST`, `FLOYDWARSHALL_BELLMANFORD_TEST`, `KRUSKAL_TEST`) cuentan con casos reales y verificables.

![Rescue Cat](src/images/rescuecat.webp)