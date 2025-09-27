# Proyecto 2 – Sistemas Operativos (2025-2)

*Grupo 14*
Integrantes:
* Alejandro Sepúlveda
* Juan José Gómez  
* Samuel Herrera

---

## Descripción del Proyecto

Este proyecto implementa una **simulación de tráfico concurrente** que modela el comportamiento de vehículos en un sistema de carreteras con intersecciones controladas. La simulación está desarrollada en **Java** utilizando la librería **Karel J. Robot** para la visualización y control de movimiento.

### Características principales:

- **56 robots** divididos en dos grupos (azul y verde) que representan vehículos
- **Sincronización mediante semáforos** para evitar colisiones y controlar el acceso a intersecciones críticas
- **Algoritmos de navegación** basados en matrices de movimiento predefinidas
- **Gestión de recursos compartidos** como estacionamientos y zonas de carga/descarga
- **Simulación de semáforos** y puntos de control de tráfico

### Problemática abordada:

El proyecto simula los desafíos típicos de la gestión de tráfico urbano:
- **Control de intersecciones**: Evitar colisiones entre vehículos
- **Gestión de estacionamientos**: Limitar el número de vehículos por zona
- **Semáforos inteligentes**: Controlar flujo de tráfico en puntos críticos
- **Intercambio de carriles**: Permitir cambios de grupo/ruta de manera segura

### Tecnologías utilizadas:

- **Java**: Lenguaje principal de desarrollo
- **Threads**: Para la concurrencia de múltiples robots
- **Semáforos**: Para sincronización y control de recursos
- **Karel J. Robot**: Librería para simulación y visualización

---

## Estructura del Código

- **`MyRobot`**: Clase que extiende Robot con movimientos personalizados
- **`MyThread`**: Implementa la lógica de navegación de cada vehículo
- **`Shared`**: Contiene recursos compartidos, semáforos y matrices de movimiento
- **`Main`**: Inicializa la simulación y crea los 56 robots

---

## Ejecución del Programa

### Requisitos previos:
- Java JDK 11 o superior instalado
- Archivo `KarelJRobot.jar` en el directorio del proyecto
- Archivo de mundo `Entrega2.kwld`

### Opción 1 – Usando los archivos .bat (Windows)

1. **Compilar**
   Ejecute el archivo:
   ```
   compilar.bat
   ```

2. **Ejecutar**
   Ejecute el archivo:
   ```
   run.bat
   ```

### Opción 2 – Usando comandos en consola (Linux/Unix)

1. **Compilar el proyecto**
   ```bash
   javac -d . -cp ".:KarelJRobot.jar" Main.java
   ```

2. **Ejecutar la simulación**
   ```bash
   java -cp ".:KarelJRobot.jar" Main
   ```

---

## Funcionamiento de la Simulación

1. **Inicialización**: Se crean 56 robots divididos en dos grupos (28 azules, 28 verdes)
2. **Navegación**: Cada robot sigue una matriz de movimientos específica para su grupo
3. **Sincronización**: Los semáforos controlan el acceso a intersecciones y recursos compartidos
4. **Intercambio**: Los robots pueden cambiar de grupo en puntos específicos del recorrido

### Controles de la simulación:
- La velocidad se puede ajustar modificando `World.setDelay(5)` en el código
- El mundo se puede cambiar modificando el archivo `.kwld` referenciado

---

## Observaciones Técnicas

- **Prevención de deadlocks**: Implementada mediante orden de adquisición de semáforos
- **Fairness**: Los semáforos utilizan política FIFO para garantizar equidad
- **Escalabilidad**: El diseño permite modificar fácilmente el número de robots
- **Mantenibilidad**: Código modular con separación clara de responsabilidades
