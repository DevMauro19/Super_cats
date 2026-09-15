# AI_USAGE.md — Super Cats (The Feline Graph Chronicles)

Este documento describe honestamente el uso de IA (Claude) durante el
desarrollo del proyecto, siguiendo lo pedido en la seccion 8.1 del enunciado.
Recoge **tres sesiones de trabajo independientes, con modelos distintos**:

- Mision 1 (modelo, parser, algoritmo y formateo de salida) — **Claude Sonnet**.
- Mision 3 (Floyd-Warshall, Bellman-Ford y su verificacion cruzada) — **Claude
  Sonnet**, en una sesion posterior e independiente de la anterior.
- Mision 4 (Kruskal, union-find y parser) — **Claude Opus 5**, que ademas
  reviso y probo el codigo de las Misiones 1, 2 y 3 ya subido por el equipo.

La Mision 2 y la GUI se avanzaron por fuera de las tres sesiones.

## 1. Herramientas usadas y para que parte del proyecto

**Reparto del trabajo cubierto por este documento**

- **Carlos Mauricio Velasco** — Mision 1 (BFS y DFS), Mision 2 (Dijkstra), GUI
  y visualizacion.
- **Pablo Escudero** — Mision 3 (Floyd-Warshall, Bellman-Ford y la
  verificacion cruzada entre ambos).
- **Santiago Perez** — Mision 4 (Kruskal y union-find), el metodo
  `Input.leerMission4` y la excepcion compartida `EEntradaInvalida`.

- **Claude (Anthropic), modelo Sonnet**, en la primera sesion, usado como
  asistente de diseno y generacion de codigo para:
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

- **Claude (Anthropic), modelo Sonnet**, en una tercera sesion, independiente
  de las otras dos, usado para la **Mision 3 completa**:
    - `algorithms/mission3/FloydWarshall.java` y `FloydWarshallResult.java`:
      maximo churun entre todos los pares (matriz N x N), con una pasada
      extra despues del triple bucle clasico para marcar los pares "no
      acotados" (aquellos donde un nodo intermedio pertenece a un ciclo de
      ganancia positiva).
    - `algorithms/mission3/BellmanFord.java` y `BellmanFordResult.java`:
      maximo churun desde un origen S, con deteccion de los nodos que se
      alimentan de un ciclo de ganancia positiva y propagacion de esa marca
      solo hacia adelante (aristas salientes).
    - `algorithms/mission3/Mission3Solver.java`: corre ambos algoritmos por
      separado y compara sus resultados para el par (S, D), cumpliendo la
      exigencia de la seccion 5 de que Bellman-Ford valide a Floyd-Warshall y
      que un desacuerdo se pueda reportar.
    - El parser (`Input.leerMision3`, `io/MisionTresCaso.java`) y el
      formateador (`Output.formatearMision3`), integrados al `Input.java` /
      `Output.java` compartidos con las demas misiones.
    - El test `test/FLOYDWARSHALL_BELLMANFORD_TEST.java`, que reproduce el
      ejemplo del enunciado (110, Infinite churun!, -65) y ademas ejercita de
      forma explicita la deteccion de discrepancias entre los dos algoritmos
      con valores fabricados a mano.

- **Claude (Anthropic), modelo Opus 5**, en una sesion posterior e
  independiente, usado para:
    - La Mision 4 completa: `algorithms/mission4/Union.java` (union-find con
      compresion de caminos y union por tamano, con `buscar()` iterativo) y
      `algorithms/mission4/Kruskal.java` junto con su clase anidada
      `Kruskal.Resultado`.
    - La excepcion compartida `Exceptions/EEntradaInvalida.java` (checked) y el
      metodo `Input.leerMission4` dentro del `io/Input.java` compartido.
    - Comentarios de complejidad y justificacion en archivos de otros
      integrantes: `algorithms/mission2/Dijkstra.java`, que no tenia ningun
      comentario y por lo tanto incumplia la seccion 7.2, y tres comentarios
      puntuales en `algorithms/mission1/BFSDFSSolver.java`. En ambos casos no
      se modifico ninguna linea de logica; se verifico comparando los
      archivos con todos los comentarios eliminados.
    - Revision del codigo de las Misiones 1, 2 y 3 ya subido por el equipo:
      compilacion desde un clon limpio de GitHub, ejecucion de los ejemplos del
      enunciado y pruebas adicionales generadas para buscar fallos.

- La GUI y la visualizacion no se trabajaron con IA en ninguna de las tres
  sesiones. Si algun integrante uso IA para esas partes, debe documentarlo
  aqui.

## 2. Prompts decisivos

1. **"Dame las clases del modelo"** — definio la arquitectura compartida
   entre las 4 misiones: un `Graph` generico (dirigido/no dirigido, pesos
   `long`) reutilizado por Dijkstra, Floyd-Warshall/Bellman-Ford y Kruskal,
   y una `Grid`/`Point` separada solo para la Mision 1. Sin este prompt cada
   mision hubiera terminado con su propia representacion de grafo,
   duplicando codigo.

2. **"Tengo los codigos de algoritmos como BFS, ¿podria usar este o deberia
   hacer algunos cambios? y porque"** (compartiendo una version generica y
   recursiva de BFS/DFS) — fue el prompt mas decisivo de la primera sesion.
   Forzo una revision completa contra los requisitos especificos del enunciado
   y revelo que el codigo original no serviria: DFS recursivo (revienta el
   stack con grillas de hasta 10^6 celdas), sin orden fijo de vecinos
   (necesario para que el resultado sea determinista y comparable), sin
   manejo de bombas, y que imprimia resultados en vez de retornarlos
   (rompiendo la separacion algoritmo/GUI exigida en la seccion 7.2).

3. **Compartir el `Grid.java`, `Punto.java` e `Input.java` reales del
   equipo** — este prompt hizo que se abandonara un primer diseno de
   parser en ingles (`TokenReader`, `Mission1Parser`, excepcion unchecked)
   y se adaptara todo a la convencion que el equipo ya habia elegido
   (`LectorTokens`, `EEntradaInvalida` checked, nombres en espanol). Tambien
   permitio detectar un bug real en `Grid.hayBomba(Punto)` (pasaba
   `p.getColumna()` dos veces en vez de `p.getFila()` y `p.getColumna()`).

4. **Compartir el PDF completo del enunciado junto con un zip del
   repositorio tal como estaba** (sesion de Mision 3) — este prompt fue el
   punto de partida de toda la implementacion. Antes de escribir una sola
   linea de Floyd-Warshall o Bellman-Ford, se leyeron `Graph.java`,
   `WeightedEdge.java`, `Dijkstra.java` (como referencia de estilo),
   `Output.java` e `Input.java` reales del equipo. Esto evito repetir el
   mismo error que ya habia pasado en Mision 1 (inventar nombres de
   clases/metodos en ingles que no coincidian con el proyecto) y aseguro que
   `Mission3Solver` usara exactamente `getNeightbors`, `addEdge`,
   `NodoValido` y el mismo estilo de excepciones checked que el resto del
   proyecto.

5. **"¿Que es el parser de la entrada? ¿Donde pide eso el enunciado?"** — saco
   a la luz la seccion 2.2, que esta en las reglas generales y no en la seccion
   de cada mision: la entrada debe leerse como un **flujo de tokens**, sin
   asumir un numero fijo por linea. Eso cambio el diseno del parser: en vez de
   `readLine()` mas `split(" ")`, se parte todo el texto de una vez con
   `split("\\s+")` y se consume token por token. Es lo que hace que la misma
   entrada pegada en una linea o en veinte produzca el mismo resultado.

## 3. Casos donde la salida de la IA fue incorrecta o subóptima

1. **Nombres de clases y metodos en ingles que no coincidian con el
   proyecto real.** La primera version de `BFSDFSSolver`/`PathResult` uso
   `model.Point` con `getRow()`/`getCol()` y `Grid.isBomb()`/`isWalkable()`/
   `setBomb()`. Cuando el equipo mostro sus clases reales (`Punto` con
   `getFila()`/`getColumna()`, `Grid` con `hayBomba()`/`esCaminable()`/
   `setBombas()`), esos archivos no compilaban. **Correccion:** se
   reescribieron `BFSDFSSolver.java` y `PathResult.java` completos usando
   los nombres reales del equipo.

2. **Se diseno un parser paralelo antes de conocer el del equipo.** Se
   crearon `TokenReader.java`, `MalformedInputException.java`,
   `Mission1Parser.java` y `Mission1TestCase.java` con una convencion de
   excepciones unchecked, sin saber que el equipo ya tenia `Input.java`
   con `LectorTokens` y `EEntradaInvalida` (checked) funcionando para la
   Mision 4. **Correccion:** se descartaron esos 4 archivos y se integro
   `leerMision1` directamente dentro del `Input.java` real, reusando su
   `LectorTokens` y su convencion de excepciones checked.

3. **Se genero un archivo de test (`SampleTest.java`) redundante.** Se
   penso que el equipo aun no tenia un test automatizado para la Mision 1,
   asi que se creo un `main()` de verificacion manual. Cuando el equipo
   mostro que ya existia `BFS_DFS_TEST.java` en `src/test/`, ese archivo
   quedo duplicado sin aportar nada nuevo. **Correccion:** se elimino
   `SampleTest.java` del proyecto.

4. **Se genero `Input.java` con un import faltante.** La version entregada
   de `Input.java` con el metodo `leerMision1` agregado no incluia
   `import model.Grid;`, a pesar de usar `new Grid(...)` dentro del
   metodo. Esto no compilaba. **Correccion:** se agrego el import faltante
   tras revisar el arbol de archivos del proyecto y detectar el error.

5. **Se recomendo escribir `leerMission4` en el `io/Input.java` compartido sin
   coordinarlo antes con el grupo.** La IA senalo el riesgo de conflicto y aun
   asi recomendo seguir adelante. El conflicto ocurrio y quedo registrado en el
   commit `0561174` ("resolviendo problemas de merge"). **Correccion:** el
   grupo acordo la convencion de un metodo estatico por mision dentro del mismo
   archivo, y avisar antes de tocarlo. La convencion funciono: los parsers de
   las Misiones 1, 2 y 3 se agregaron despues sin volver a chocar.

6. **La IA dejo un archivo `.git/index.lock` huerfano** al correr `git status`
   contra el repositorio local a traves de su puente de archivos, que no tiene
   permiso para borrar archivos. Eso bloqueo todos los `git add` y `git commit`
   en la maquina con el error "Another git process seems to be running", sin
   que hubiera ningun proceso de git corriendo. **Correccion:** se borraron los
   dos archivos `.lock` a mano y se dejo de ejecutar git por ese puente; a
   partir de ahi la inspeccion del repositorio se hizo sobre un clon aparte.

7. **El parser de la Mision 4 rechaza tokens sobrantes despues del ultimo
   caso**, con el mensaje "Sobran datos despues del ultimo caso". Es una
   decision deliberada para cumplir la seccion 2.2 (nunca un fallo silencioso),
   pero es mas estricta de lo que el enunciado exige y rechazaria una entrada
   que por lo demas es valida. **Correccion:** ninguna; se dejo asi de forma
   consciente, y queda documentado como una decision y no como un descuido.

8. **La IA inserto a proposito un import invalido** (`import
   java.util.Collravamosections;`) en la primera entrega de `Kruskal.java`,
   avisando de ello en el mismo mensaje, para forzar que el codigo se leyera
   antes de pegarlo en vez de copiarlo en automatico. **Correccion:** se
   detecto y se elimino antes de compilar.

9. **Errores de tipeo en los comentarios de complejidad (Mision 3).** Los
   comentarios de `FloydWarshall.java` y `BellmanFord.java` decian "son come
   10^6 operaciones" y "son come 5*10^5 operaciones" en vez de "son unas".
   No afectaba la compilacion, pero es un descuido de redaccion en codigo
   que se va a defender en publico. **Correccion:** se corrigieron ambos
   comentarios antes de la entrega.

10. **Reemplazar toda la carpeta `src` genero cambios falsos en Git (Mision
    3).** El zip con el codigo de Mision 3 se genero en un entorno Linux (fin
    de linea LF), mientras que el repositorio del equipo usa CRLF (Windows).
    Al reemplazar `src/` completa, `git status` marco como "modified" 19
    archivos que en realidad NO habian cambiado de contenido — solo de fin de
    linea. **Como se detecto y corrigio:** se comparo con
    `git diff --ignore-all-space` (sin diferencias reales) y se restauraron
    esos 19 archivos con `git checkout -- <archivos>` antes de comitear, para
    no ensuciar el historial del repositorio con cambios fantasma.

11. **La IA no pudo compilar ni ejecutar el codigo que genero en la sesion de
    Mision 3.** El entorno donde corrio Claude no tenia `javac` instalado
    (solo un JRE) ni acceso a internet para instalarlo. Todo el codigo se
    verifico primero mediante una revision manual exhaustiva (balance de
    llaves, imports, firmas de metodos) y una traza a mano de los 3 casos de
    ejemplo del enunciado, pero la **unica confirmacion real** de que
    compilaba y funcionaba fue la que hizo el integrante del equipo en
    IntelliJ (`Rebuild Project` sin errores, y el test
    `FLOYDWARSHALL_BELLMANFORD_TEST` dando `PASS`). El codigo generado por IA
    en este flujo de trabajo no viene "verificado por la IA": viene
    verificado por quien lo compila despues.

## 4. Que aprendio cada integrante

> [Placeholder — cada integrante debe completar esto con sus propias
> palabras antes de entregar. Algunos temas que surgieron durante las
> sesiones y que pueden servir de base:]

- **Carlos Mauricio Velasco:** _(por ejemplo: por que un DFS recursivo revienta
  el stack con grillas grandes, y como simular manualmente el "call stack"
  con una pila explicita para reproducir exactamente el mismo orden de
  visita que la recursion — sin invertir el orden arriba/abajo/izquierda/
  derecha al usar una estructura LIFO)._
- **Pablo Escudero:** _(por ejemplo: por que Floyd-Warshall necesita una
  pasada EXTRA despues del triple bucle clasico para distinguir "el mejor
  valor finito que alcanzo a encontrar" de "realmente no tiene limite"; por
  que la propagacion de un ciclo de ganancia positiva debe seguir unicamente
  las aristas SALIENTES desde el ciclo y no cualquier nodo alcanzable en
  cualquier direccion; o la diferencia entre una caminata (walk, que puede
  repetir nodos y aristas) y un camino simple, y por que maximizar sobre
  caminatas es un problema polinomial mientras que sobre caminos simples
  seria NP-duro)._
- **Santiago Perez:** por que el proyecto tiene `Edge` y `WeightedEdge` como
  clases separadas: el `Edge` que vive en la lista de adyacencia no guarda el
  nodo de origen porque este esta implicito en el indice del arreglo, mientras
  que Kruskal ordena todas las aristas juntas y pierde ese contexto, asi que
  necesita el `from` explicito. Tambien por que union-find es imprescindible y
  no un lujo: sin el habria que lanzar un recorrido completo por cada cable
  para saber si dos nodos ya estan conectados, lo que en el peor caso del
  enunciado son unas 10^10 operaciones. Y por que los acumulados van en `long`
  y no en `int`: el desbordamiento de un `int` es silencioso, no lanza ninguna
  excepcion y simplemente devuelve un numero equivocado.

## 5. Verificacion hecha con ayuda de la IA

Se documenta aparte porque respalda lo afirmado en las secciones anteriores.

- La Mision 4 se probo con 17 casos: el ejemplo del enunciado (55), la misma
  entrada en tres formatos de linea distintos, grafo desconectado, N=1,
  auto-ciclos, cables repetidos, costo cero, varios casos seguidos, seis
  entradas malformadas, y los limites del enunciado (N=10.000 y C=100.000 en
  unos 430 ms).
- Se comprobo el desbordamiento de `int` con 4.999 cables de costo 1.000.000,
  que suman 4.999.000.000: por encima del rango de `int` y por eso el acumulado
  es `long`.
- La Mision 3 (nuestra implementacion de Floyd-Warshall y Bellman-Ford) se
  contrasto contra una referencia escrita por fuerza bruta (enumerando ciclos
  simples para decidir el caso infinito y caminos simples para el maximo)
  sobre 400 grafos aleatorios: cero discrepancias y cero desacuerdos entre
  Floyd-Warshall y Bellman-Ford. Ademas paso el test automatizado
  `FLOYDWARSHALL_BELLMANFORD_TEST` con los tres casos del enunciado
  (110, Infinite churun!, -65).
- Todas las verificaciones se hicieron compilando desde un clon limpio de
  GitHub, que es lo que exige la seccion 7.4.

La revision asistida en la sesion de la Mision 4 tambien encontro tres
problemas en codigo escrito por otros integrantes:

1. **`Grid.hayBomba(Punto)` pasaba fila y columna invertidas.** La firma es
   `hayBomba(int fila, int col)` y el metodo llamaba
   `hayBomba(p.getColumna(), p.getFila())`. El ejemplo del enunciado pasaba por
   casualidad, porque el inicio (0,0) y el destino (9,9) estan sobre la
   diagonal y ahi intercambiar fila y columna no cambia nada. Se demostro con
   una grilla 3x3 con una mina en (0,2) e inicio en (2,0): el BFS respondia
   "Nina is unreachable" cuando la respuesta correcta eran 2 movimientos, y en
   grillas no cuadradas lanzaba `EFueraRango`. Corregido por el equipo.

2. **`BFS_DFS_TEST` estaba fallando (DFS 46 en vez de 32), pero el error estaba
   en el test y no en el solver.** A las bombas escritas a mano les faltaban
   tres posiciones respecto al input del PDF: fila 2 columna 2, fila 8
   (columnas 7 y 9) y fila 9 (columnas 2, 3 y 4). Con menos bombas el DFS
   tomaba otro camino. Corregido por el equipo; el solver siempre estuvo bien.

3. **Riesgo de desbordamiento en Floyd-Warshall (Mision 3).** En el peor caso
   dentro de los limites del enunciado (N=100, M=5000, todos los pesos +1000)
   los valores de la matriz llegan a unos 9,2 x 10^18, practicamente el tope de
   `long`, y en grafos construidos a proposito algunas entradas si se
   desbordan. No produce una respuesta incorrecta, porque toda entrada que se
   desborda pertenece a un par ya marcado como no acotado y se imprime como
   `Infinite churun!`, y porque un par acotado no puede superar N x W =
   100.000. Se deja documentado como riesgo conocido, no como defecto.

---

*Nota: este documento fue redactado con ayuda de Claude a partir del
historial real de las tres sesiones de trabajo. El equipo debe revisarlo,
completar los aprendizajes personales, y verificar que coincida con lo
que efectivamente pueden defender en la sustentacion oral (seccion 8.2):
un reporte que no coincida con lo que pasa en la defensa se califica como
deshonestidad academica.*