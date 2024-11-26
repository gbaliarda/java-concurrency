# Introducción a la Programación Paralela

## Archivos

- plot.ipynb: crea los gráficos utilizados para el analísis.
- actividad2.jar: JAR compilado con el código Java para generar los resultados.
- src/main/java/org/example/*Multiply: implementación de la multiplicación de matrices.
- src/main/java/org/example/App: código que ejecuta el JAR.

## Requisitos
- Java 21
- Maven
- Python
	- Seaborn
	- Matplotlib
	- Pandas

## Compilación

Para construir el JAR hacemos uso de maven:
```sh
mvn clean package
```

## Ejecución

Para ejecutar el JAR ejecutamos el comando:
```sh
java -jar ruta/del/archivo/compilado/java.jar
```
Por defecto en la carpeta root existe un JAR compilado llamado actividad2.

## Autores

- Gonzalo Baliarda 61490