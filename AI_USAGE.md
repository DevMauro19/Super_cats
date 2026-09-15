# AI_USAGE.md — Super Cats (The Feline Graph Chronicles)

Este documento describe honestamente el uso de IA (Claude, modelo Sonnet)
durante el desarrollo del proyecto, siguiendo lo pedido en la seccion 8.1
del enunciado. Cubre dos sesiones de trabajo distintas: la construccion de
la Mision 1 (modelo, parser, algoritmo y formateo de salida) y, por
separado, la construccion completa de la Mision 3 (Floyd-Warshall y
Bellman-Ford).

## 1. Herramientas usadas y para que parte del proyecto

- **Claude (Anthropic)**, usado como asistente de diseno y generacion de
  codigo para:
    - Las clases del modelo compartidas entre misiones: `Point`/`Punto`,
      `Grid`, `Edge`, `WeightedEdge`, `Graph`.
    - El algoritmo de la Mision 1: `BFSDFSSolver` (BFS iterativo + DFS no
      recursivo con orden fijo arriba/abajo/izquierda/derecha) y `PathResult`.
    - El parser compartido: metodo `Input.leerMision1` y la clase contenedora
      `MisionUnoCaso`, integrados al `Input.java` que el equipo ya tenia
      empezado con `leerMission4` (Kruskal).
    - El formateador de salida `Output.java` para la Mision 1.
    - Revision de codigo ya escrito por el equipo (una version inicial de
      BFS/DFS generica, y las clases reales `Grid`/`Punto`/`Input`), senalando
      bugs y falta de cumplimiento de requisitos del enunciado.
    - **La Mision 3 completa**: `FloydWarshall.java` y `FloydWarshallResult.java`
      (maximo churun entre todos los pares, con deteccion de pares no
      acotados por ciclos de ganancia positiva); `BellmanFord.java` y
      `BellmanFordResult.java` (maximo churun desde un origen S, con
      deteccion y propagacion de ciclos de ganancia positiva); y
      `Mission3Solver.java` (orquestador que corre ambos algoritmos por
      separado y cruza sus resultados para el par S-D, exigencia explicita
      de la seccion 5 del enunciado). Tambien el parser (`Input.leerMision3`,
      `io/MisionTresCaso.java`) y el formateador (`Output.formatearMision3`),
      integrados al mismo `Input.java`/`Output.java` compartidos con las
      demas misiones. Y el test `FLOYDWARSHALL_BELLMANFORD_TEST.java`, que
      reproduce el ejemplo del enunciado y ademas ejercita explicitamente
      la deteccion de discrepancias entre los dos algoritmos.

- Las Misiones 2 y 4 (Dijkstra, Kruskal + Union-Find), la GUI y la
  visualizacion **no se trabajaron con Claude**. Si algun companero de
  equipo uso IA para esas partes, deben documentarlo ellos mismos en esta
  misma seccion.

## 2. Prompts decisivos

1. **"Dame las clases del modelo"** — definio la arquitectura compartida
   entre las 4 misiones: un `Graph` generico (dirigido/no dirigido, pesos
   `long`) reutilizado por Dijkstra, Floyd-Warshall/Bellman-Ford y Kruskal,
   y una `Grid`/`Point` separada solo para la Mision 1. Sin este prompt cada
   mision hubiera terminado con su propia representacion de grafo,
   duplicando codigo.

2. **"Tengo los codigos de algoritmos como BFS, ¿podria usar este o deberia
   hacer algunos cambios? y porque"** (compartiendo una version generica y
   recursiva de BFS/DFS) — fue el prompt mas decisivo de la sesion de
   Mision 1. Forzo una revision completa contra los requisitos especificos
   del enunciado y revelo que el codigo original no serviria: DFS recursivo
   (revienta el stack con grillas de hasta 10^6 celdas), sin orden fijo de
   vecinos, sin manejo de bombas, y que imprimia resultados en vez de
   retornarlos.

3. **Compartir el PDF completo del enunciado junto con un zip del
   repositorio tal como estaba** (para la sesion de Mision 3) — este
   prompt fue el punto de partida de toda la implementacion. Antes de
   escribir una sola linea de Floyd-Warshall o Bellman-Ford, se leyeron
   `Graph.java`, `WeightedEdge.java`, `Dijkstra.java` (como referencia de
   estilo), `Output.java` e `Input.java` reales del equipo. Esto evito
   repetir el error que ya habia pasado en Mision 1 (inventar nombres de
   clases/metodos en ingles que no coincidian con el proyecto) y aseguro
   que `Mission3Solver` usara exactamente `getNeightbors`, `addEdge`,
   `NodoValido` y el mismo estilo de excepciones checked que el resto del
   proyecto.

4. **"¿Cómo defenderías el diseño si el profesor pregunta X en la
   sustentacion?"** (por ejemplo, convertir la Mision 3 en un problema de
   minimizacion) — no genero codigo nuevo, pero obligo a verbalizar por
   que la deteccion de ciclos de ganancia positiva depende del SIGNO del
   ciclo y no de si se busca un maximo o un minimo, lo cual es justamente
   lo que la seccion 8.2 del enunciado exige poder explicar en vivo.

## 3. Casos donde la salida de la IA fue incorrecta o subóptima

1. **Nombres de clases y metodos en ingles que no coincidian con el
   proyecto real (Mision 1).** La primera version de `BFSDFSSolver`/
   `PathResult` uso `model.Point` con `getRow()`/`getCol()` y
   `Grid.isBomb()`/`isWalkable()`/`setBomb()`. Al mostrar las clases reales
   del equipo (`Punto`, `Grid` con nombres en espanol), esos archivos no
   compilaban. **Correccion:** se reescribieron ambos archivos completos
   usando los nombres reales del equipo.

2. **Se diseno un parser paralelo antes de conocer el del equipo
   (Mision 1).** Se crearon `TokenReader.java`, `Mission1Parser.java` y
   otros, sin saber que ya existia `Input.java` con `LectorTokens` y
   `EEntradaInvalida` (checked) funcionando. **Correccion:** se
   descartaron esos archivos y se integro `leerMision1` dentro del
   `Input.java` real.

3. **Import faltante en `Input.java` (Mision 1).** La version con
   `leerMision1` agregado no incluia `import model.Grid;`, pese a usar
   `new Grid(...)`. No compilaba. **Correccion:** se agrego el import tras
   revisar el arbol de archivos.

4. **Errores de tipeo en los comentarios de complejidad (Mision 3).** Los
   comentarios de `FloydWarshall.java` y `BellmanFord.java` decian "son
   come 10^6 operaciones" y "son come 5*10^5 operaciones" en vez de "son
   unas". No afectaba la compilacion, pero es un descuido de redaccion en
   codigo que se va a defender en publico. **Correccion:** se corrigieron
   ambos comentarios antes de la entrega.

5. **Reemplazar toda la carpeta `src` genero cambios falsos en Git
   (Mision 3).** El zip con el codigo de Mision 3 se genero en un entorno
   Linux (fin de linea LF), mientras que el repositorio del equipo usa
   CRLF (Windows). Al reemplazar `src/` completa, `git status` marco como
   "modified" 19 archivos que en realidad NO habian cambiado de contenido
   — solo de fin de linea. **Como se detecto y corrigio:** se comparo con
   `git diff --ignore-all-space` (sin diferencias reales) y se restauraron
   esos 19 archivos con `git checkout -- <archivos>` antes de comitear,
   para no ensuciar el historial del repositorio con cambios fantasma que
   hubieran generado conflictos innecesarios con el trabajo de otros
   companeros.

6. **La IA no pudo compilar ni ejecutar el codigo que genero.** El entorno
   donde corrio Claude durante la sesion de Mision 3 no tenia `javac`
   instalado (solo un JRE) ni acceso a internet para instalarlo. Todo el
   codigo se verifico primero mediante una revision manual exhaustiva
   (balance de llaves, imports, firmas de metodos) y una traza a mano de
   los 3 casos de ejemplo del enunciado, pero la **unica confirmacion real**
   de que compilaba y funcionaba fue la que hizo el estudiante en IntelliJ
   (`Rebuild Project` sin errores, y el test `FLOYDWARSHALL_BELLMANFORD_TEST`
   dando `PASS`). Esto es una limitacion a tener en cuenta: el codigo
   generado por IA en este flujo de trabajo no viene "verificado por la
   IA", viene verificado por quien lo compila despues.

## 4. Que aprendio cada integrante

> [Placeholder — cada integrante debe completar esto con sus propias
> palabras antes de entregar. Algunos temas que surgieron durante las
> sesiones y que pueden servir de base:]

- **[Nombre 1 — Mision 1]:** _(por ejemplo: por que un DFS recursivo
  revienta el stack con grillas grandes, y como simular manualmente el
  "call stack" con una pila explicita sin invertir el orden arriba/abajo/
  izquierda/derecha al usar una estructura LIFO)._
- **[Nombre 2]:** _(por ejemplo: la diferencia entre excepciones checked
  y unchecked, y por que conviene usar unchecked para invariantes internos
  del modelo y checked para errores de formato de entrada que la GUI debe
  manejar explicitamente)._
- **[Nombre 3 — Mision 3]:** _(por ejemplo: por que Floyd-Warshall
  necesita una pasada EXTRA despues del triple bucle clasico para
  distinguir "el mejor valor finito que alcanzo a encontrar" de
  "realmente no tiene limite"; por que la propagacion de un ciclo de
  ganancia positiva debe seguir unicamente las aristas SALIENTES desde el
  ciclo y no cualquier nodo alcanzable en cualquier direccion; o la
  diferencia entre una caminata (walk, que puede repetir nodos y aristas)
  y un camino simple, y por que maximizar sobre caminatas es un problema
  polinomial mientras que sobre caminos simples seria NP-duro)._

---

*Nota: este documento fue redactado con ayuda de Claude a partir del
historial real de dos sesiones de trabajo distintas. El equipo debe
revisarlo, completar los aprendizajes personales, y verificar que coincida
con lo que efectivamente pueden defender en la sustentacion oral (seccion
8.2): un reporte que no coincida con lo que pasa en la defensa se
califica como deshonestidad academica.*