<h1 align="center" id="title">Gestion de concesionario con conectores JDBC</h1>

<p align="center"><img src="https://socialify.git.ci/PabloMartinLopez/gestion_de_conecesionario_con_conectores_JDBC/image?language=1&amp;owner=1&amp;name=1&amp;stargazers=1&amp;theme=Light" alt="project-image"></p>

![GitHub top language](https://img.shields.io/github/languages/top/pablomartinlopez/gestion_de_conecesionario_con_conectores_JDBC)

<p id="description">El objetivo de este proyecto era poner a prueba la creación de una aplicación de Java en la que haciendo uso de JDBC podamos conectarnos tanto a una base de datos MySQL como a una base de datos SQLite realizar operaciones básicas de CRUD y generar informes basándonos en los datos de la base de datos activa</p>

<p>
Para este proyecto he definido las tablas coches, propietarios y trnsacciones, las he relacionado entre si de forma que un propietario pudiera tener uno o mas coches a la vez que los coches deben de tener un propietario. Cuando inserto un coche en el sistema este siempre es del propio concesionario (Se crea junto con todas las tablas con el id asociado 1)
</p>

<p>
Las transacciones se tienen que hacer entre dos propietarios distintos y deben involucrar un coche y un importe. Tras realizar esta operacion el coche cambiara de propietario y aparecera en el listado de vehiculos del nuevo propietario</p>

<h2>🔬 Features</h2>

Principales Features de este proyecto:

- Conectar BBDD
- Crear Tablas
- Registrar Propietario
- Insertar Coche
- Importar Coches desde fichero CSV
- Listar Coches
- Listar Coches
- Modificar Coche
- Borrar Coche
- Realizar Traspaso
- Generar Informe Resumen

<h2>🛠️ Instalacion:</h2>

<p>1. Configuración de java y Maven</p>

<p>2. Configuración servidor MySQL (Para modo MySQL)</p>

```
CREATE DATABASE IF NOT EXISTS bd_coches;
```

<p>3. Configurar variables de entorno dentro del fichero src/main/resources/config.properties</p>

```
### URL
db.URL.MYSQL=jdbc:mysql://localhost:3306/[Nombre de la base de datos]?useSSL=false&serverTimezone=UTC&user=[usuario base de datos]&password=[contraseña base de datos]
db.URL.SQLITE=jdbc:sqlite:[nombre de la base]

### CSV
CSVFileDefault=./FicheroDeCarga/BBDD Coches.csv

### Informes
rutaInforme=./mi_informe.txt
```

<h2>💻 Creado con:</h2>

Tecnologías utilizadas en este proyecto:

- JAVA
- JDBC
- MySQL
- SQLite
