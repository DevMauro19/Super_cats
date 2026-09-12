# Super Cats — The Feline Graph Chronicles

Proyecto final del curso Lenguajes y Compiladores — Universidad EIA.
Cuatro misiones basadas en grafos (BFS/DFS, Dijkstra, Floyd-Warshall +
Bellman-Ford, Kruskal) resueltas en Java, con una GUI tematica inspirada
en Pola, Minerva y el villano Limon.

## Integrantes del grupo

> [Placeholder — completar con nombres reales antes de entregar]

- [Nombre 1]
- [Nombre 2]
- [Nombre 3]

## Como compilar y ejecutar

> [Placeholder — el proyecto actualmente no muestra un `pom.xml` ni
> `build.gradle` en la estructura de carpetas. Si van a seguir sin Maven/
> Gradle, documenten aqui el comando exacto de `javac`/`java` que use
> todo el equipo. Ejemplo generico compilando todo el codigo fuente:]

```bash
# Desde la raiz del proyecto (donde esta la carpeta src/)
mkdir -p out
javac -d out $(find src -name "*.java")

# Ejecutar la GUI principal (ajustar el nombre de la clase principal real)
java -cp out gui.MainFrame
```

Si en algun momento migran a Maven o Gradle, reemplacen esta seccion por
el comando real (`mvn clean package && java -jar ...` o
`./gradlew run`), que es justo lo que pide la seccion 7.4: "compile from
a clean clone" con un solo comando documentado.

## Estructura del proyecto

```
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
│   ├── Input.java          Parser compartido: un metodo leerMisionN por mision
│   ├── MisionUnoCaso.java  Contenedor (Grid, inicio, destino) para un caso de Mision 1
│   └── Output.java         Formateador de salida: un metodo formatearMisionN por mision
├── model/
│   ├── Edge.java           Arista dirigida dentro de la lista de adyacencia de Graph
│   ├── Graph.java          Grafo generico (dirigido o no), reutilizado en Misiones 2, 3 y 4
│   ├── Grid.java           Grilla con bombas, usada solo en Mision 1
│   ├── Punto.java          Coordenada (fila, columna) inmutable, usada en Mision 1
│   └── WeightedEdge.java   Arista plana (from, to, weight), para Kruskal y Bellman-Ford
└── test/
    ├── BFS_DFS_TEST.java
    ├── DIJKSTRA_TEST.java
    ├── FLOYDWARSHALL_BELLMANFORD_TEST.java
    └── KRUSKAL_TEST.java
```

## Decisiones de diseño tomadas

- **`Graph` unico y generico** para las Misiones 2, 3 y 4 (constructor
  `Graph(int nodeCount, boolean directed)`), en vez de un grafo por
  mision. Mantiene en paralelo una lista de adyacencia (`Edge`, para
  Dijkstra/Floyd-Warshall que recorren por nodo) y una lista plana de
  aristas (`WeightedEdge`, para Kruskal que ordena todas las aristas y
  Bellman-Ford que las relaja todas en cada ronda).

- **`Grid`/`Punto` separados de `Graph`** para la Mision 1: la grilla
  puede llegar a 10^6 celdas, asi que los vecinos se calculan al vuelo a
  partir de `(fila, columna)` en vez de materializar una lista de
  adyacencia explicita (que desperdiciaria memoria con hasta 4×10^6
  aristas).

- **DFS no recursivo** en `BFSDFSSolver`: simula manualmente el call stack
  (cada "frame" guarda la celda y la proxima direccion a intentar) para
  reproducir exactamente el mismo orden de visita que una version
  recursiva con el orden fijo arriba/abajo/izquierda/derecha, sin arriesgar
  un `StackOverflowError` en grillas grandes.

- **Estructuras internas con arreglos primitivos**, no colecciones de
  objetos: `boolean[]`/`int[]` indexados por `fila*columnas + columna` en
  vez de `HashSet<Punto>`/`HashMap<Punto,Punto>`, para evitar el costo de
  boxing y de crear millones de objetos en el peor caso (grillas de hasta
  1000×1000).

- **Excepciones en dos capas:**
    - `ENumeroNegativo` y `EFureraRango` (esta ultima unchecked, ya que sus
      metodos no la declaran con `throws`) protegen invariantes internos del
      **modelo** (`Grid`, `Graph`, `Edge`, `Union`), sin importar quien los
      llame.
    - `EEntradaInvalida` (checked) protege contra **texto** mal formado que
      el usuario pega en la GUI (token faltante, fuera de rango, basura
      sobrante), y debe ser capturada explicitamente por la GUI para mostrar
      un mensaje legible en vez de un stack trace (seccion 2.2 del
      enunciado).

- **Un solo `Input.java` y un solo `Output.java`**, compartidos por las 4
  misiones: cada mision agrega su propio metodo estatico
  (`leerMisionN`/`formatearMisionN`) reutilizando el mismo tokenizador
  interno (`LectorTokens`), en vez de que cada mision tenga su propio
  parser/formateador independiente.

## Limitaciones conocidas / pendiente

> [Placeholder — actualizar a medida que avancen]

- Mision 2 (Dijkstra), y partes de Mision 3 (Bellman-Ford/Floyd-Warshall)
  y Mision 4 (Kruskal/Union) estan en distintos niveles de avance; validar
  que usen la misma convencion de excepciones y el mismo `Graph` antes de
  la entrega.
- La GUI (`MainFrame.java`) y la visualizacion de grafos/grilla estan
  pendientes — se dejaron para el final segun lo acordado por el equipo.
- `Output.java` solo tiene el formateador de la Mision 1
  (`formatearMision1`); falta agregar el de las Misiones 2, 3 y 4.
- Falta un test que ejercite `Input.leerMision1` directamente (hoy
  `BFS_DFS_TEST.java` arma el `Grid` a mano y no pasa por el parser).

## Al menos un test automatizado por algoritmo

Ver `src/test/`. Actualmente `BFS_DFS_TEST.java` cubre BFS y DFS de la
Mision 1 contra el ejemplo del enunciado (`BFS 18 DFS 32`); los demas
archivos de test (`DIJKSTRA_TEST`, `FLOYDWARSHALL_BELLMANFORD_TEST`,
`KRUSKAL_TEST`) estan pendientes de contenido real.