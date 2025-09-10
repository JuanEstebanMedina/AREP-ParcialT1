# AREP-ParcialT1
Calculadora Web para estimar la media y la desviación estándar de un conjunto de números

## Inicializando

### Prerrequisitos
Asegúrate de tener instalado lo siguiente:
- Java21
- Maven

### Instalación
1. Clona este repositorio:
    ```bash
    git clone https://github.com/JuanEstebanMedina/AREP-ParcialT1
    ```

2. Navegar al directorio:
    ```bash
    cd AREP-ParcialT1
    ```

3. Compilar con maven:
    ```bash
    mvn package
    ```

### Ejecución
1. Inicializar backend:
    ```bash
    java -cp target/classes co.edu.arep.arep.parcialt1.BackendHost
    ```
    Inicializa en "http://localhost:35000"

2. Inicializar fachada:
    ```bash
    java -cp target/classes co.edu.arep.arep.parcialt1.FacadeHost
    ```
    Inicializa en "http://localhost:36000"

### Video de prueba ejecución

Se puede ver el video de demostración en el archivo [AREP-ParcialT1-video.mp4](./AREP-ParcialT1-video.mp4).

## Authors

* **Juan Esteban Medina Rivas** - Escuela Colombiana de Ingeniería Julio Garavito
