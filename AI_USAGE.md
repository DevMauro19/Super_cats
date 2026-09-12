# AI_USAGE.md — Super Cats (The Feline Graph Chronicles)

Este documento describe honestamente el uso de IA (Claude, modelo Sonnet)
durante el desarrollo del proyecto, siguiendo lo pedido en la seccion 8.1
del enunciado. Cubre principalmente la construccion de la Mision 1
(modelo, parser, algoritmo y formateo de salida), que fue la parte
trabajada en conjunto con la IA en esta sesion.

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

- Las Misiones 2, 3 y 4 (Dijkstra, Floyd-Warshall/Bellman-Ford, Kruskal +
  Union-Find), la GUI y la visualizacion **no se trabajaron con Claude en
  esta sesion** — se avanzaron por fuera. Si el equipo uso IA para esas
  partes en otras sesiones, documentenlo aparte aqui.

## 2. Prompts decisivos

1. **"Dame las clases del modelo"** — definio la arquitectura compartida
   entre las 4 misiones: un `Graph` generico (dirigido/no dirigido, pesos
   `long`) reutilizado por Dijkstra, Floyd-Warshall/Bellman-Ford y Kruskal,
   y una `Grid`/`Point` separada solo para la Mision 1. Sin este prompt cada
   mision hubiera terminado con su propia representacion de grafo,
   duplicando codigo.

2. **"Tengo los codigos de algoritmos como BFS, ¿podria usar este o deberia
   hacer algunos cambios? y porque"** (compartiendo una version generica y
   recursiva de BFS/DFS) — fue el prompt mas decisivo de la sesion. Forzo
   una revision completa contra los requisitos especificos del enunciado y
   revelo que el codigo original no serviria: DFS recursivo (revienta el
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

## 3. Casos donde la salida de la IA fue incorrecta o subóptima

1. **Nombres de clases y metodos en ingles que no coincidian con el
   proyecto real.** La primera version de `BFSDFSSolver`/`PathResult` uso
   `model.Point` con `getRow()`/`getCol()` y `Grid.isBomb()`/`isWalkable()`/
   `setBomb()`. Quan el equipo mostro sus clases reales (`Punto` con
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

## 4. Que aprendio cada integrante

> [Placeholder — cada integrante debe completar esto con sus propias
> palabras antes de entregar. Algunos temas que surgieron durante la
> sesion y que pueden servir de base:]

- **[Nombre 1]:** _(por ejemplo: por que un DFS recursivo revienta el
  stack con grillas grandes, y como simular manualmente el "call stack"
  con una pila explicita para reproducir exactamente el mismo orden de
  visita que la recursion — sin invertir el orden arriba/abajo/izquierda/
  derecha al usar una estructura LIFO)._
- **[Nombre 2]:** _(por ejemplo: la diferencia entre excepciones checked
  y unchecked, y por que conviene usar unchecked para invariantes internos
  del modelo (`ENumeroNegativo`, `EFueraRango`) y checked para errores de
  formato de entrada que la GUI debe manejar explicitamente
  (`EEntradaInvalida`)._
- **[Nombre 3]:** _(por ejemplo: por que usar arreglos primitivos
  (`boolean[]`, `int[]`) en vez de `HashSet`/`HashMap` de objetos para
  estructuras internas de un algoritmo que recorre grillas de hasta 10^6
  celdas, evitando el costo de boxing y de miles de objetos creados
  innecesariamente)._

---

*Nota: este documento fue redactado con ayuda de Claude a partir del
historial real de la sesion de trabajo. El equipo debe revisarlo,
completar los aprendizajes personales, y verificar que coincida con lo
que efectivamente pueden defender en la sustentacion oral (seccion 8.2):
un reporte que no coincida con lo que pasa en la defensa se califica como
deshonestidad academica.*