import Exeptions.CarNotFoundException;
import Exeptions.PropietarioNotFoundException;
import model.Car;
import model.Propietario;
import model.Traspaso;
import service.CarCtrl;
import service.PropietarioCtrl;
import service.TraspasoCtrl;
import util.ConecctionManager;
import util.ConfigLoader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ConecctionManager cm = new ConecctionManager(1);
    private static ConfigLoader config = new ConfigLoader();

    public static void main(String[] args) {

        connectionManager();

        int option;
        do {
            showMenu();
            try {
                option = scanner.nextInt();
                scanner.nextLine();
                handleOption(option);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("Error: Por favor, introduce un número válido.");
                option = -1;
            }
        } while (option != 0);
    }

    /**
     * Mostrar menu con todas sus opciones
     */
    private static void showMenu() {
        boolean useSQLite = cm.getBdSqlite();
        System.out.println("\n--- GESTIÓN CONCESIONARIO ---");
        System.out.println("1. Crear base de datos");
        System.out.println("2. Insertar nuevo propietario");
        System.out.println("3. Insertar nuevo coche");
        System.out.println("4. Mostrar coches del concesionario");
        System.out.println("5. Mostrar coches Segun propietario");
        System.out.println("6. Modificar coche");
        System.out.println("7. Eliminar coche");
        System.out.println("8. Traspaso");
        System.out.println("9. Importar CSV");
        System.out.println("10. Crear informe");
        System.out.println("-1. Cambiar modo DB");

        if(useSQLite){
            System.out.println("11. Ejecutar procedimiento almacenado");
        }

        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    /**
     * Controlar opciones del menu
     * @param option Int Opcion del menu por el usuario
     */
    private static void handleOption(int option) {
        try{
            switch (option) {
                case 1->createDatabase();
                case 2->createPropietario();
                case 3->createCar();
                case 4-> buscarConcesionario();
                case 5 -> buscarPropietario();
                case 6 -> modificarCoche();
                case 7 -> eliminarCoche();
                case 8 -> traspaso();
                case 9 -> importarcsv();
                case 10 -> crearInforme();
                case 11 -> ejecutarProcedimiento();
                case -1 ->connectionManager();
                case 0 -> {
                    System.exit(0);
                }
                default-> {
                    System.out.println("Opción no válida. Inténtalo de nuevo." + option);
                }
            }
        } catch (CarNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void ejecutarProcedimiento() {

    }

    /**
     * Crear informe en un fichero .txt
     */
    private static void crearInforme() {

        Path ruta = Paths.get("informe_concesionario.txt");

        CarCtrl carCtrl = new CarCtrl(cm.getUrl());
        List<Car> coches = carCtrl.getInforme();

        StringBuilder stringInforme = new StringBuilder();

        try {
            if (coches == null || coches.isEmpty()) {
                throw new CarNotFoundException("No se encontraron coches en la base de datos.");
            }

            // calculos
            Map<String, List<Car>> cochesPorMarca = coches.stream()
                    .collect(Collectors.groupingBy(Car::getMarca));

            String extraMasRepetido = getExtraMasRepetido(coches);

            // contenido del informe
            int totalVehiculos = coches.size();

            stringInforme.append("==================================================\n");
            stringInforme.append("        INFORME RESUMEN DEL CONCESIONARIO         \n");
            stringInforme.append("==================================================\n\n");

            stringInforme.append("🚗 TOTAL DE VEHÍCULOS: ").append(totalVehiculos).append("\n\n");
            stringInforme.append("--------------------------------------------------\n");

            stringInforme.append("✨ EXTRA MÁS REPETIDO EN TODOS LOS COCHES:\n");
            stringInforme.append(extraMasRepetido).append("\n\n");
            stringInforme.append("--------------------------------------------------\n");


            stringInforme.append("📋 LISTADO DE COCHES AGRUPADOS POR MARCA:\n\n");
            cochesPorMarca.forEach((marca, listaCoches) -> {
                stringInforme.append("  > MARCA: ").append(marca).append(" (Total: ").append(listaCoches.size()).append(")\n");
                listaCoches.forEach(coche -> stringInforme.append("    - ").append(coche.toString()).append("\n"));
                stringInforme.append("\n");
            });


//            Escritura del informe
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta.toFile()))) {
                writer.write(stringInforme.toString());
                System.out.println("✅ Informe generado con éxito en: " + ruta.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error al escribir el fichero: " + e.getMessage());
            }


        } catch (CarNotFoundException e) {
            System.err.println("Advertencia: " + e.getMessage());
            // No es necesario lanzar un RuntimeException si solo es una advertencia.
        }
    }

    /**
     * Consegir el extra mas repedito
     * @param coches
     * @return
     */
    private static String getExtraMasRepetido(List<Car> coches) {
        Map<String, Long> frecuenciaExtras = new HashMap<>();

        // 1. Contar la frecuencia de cada extra
        coches.stream()
                .flatMap(car -> car.getExtras().stream()) // Combina todas las listas de extras en un solo stream
                .filter(extra -> extra != null && !extra.trim().isEmpty()) // Ignora extras nulos o vacíos
                .map(String::trim) // Limpia espacios en blanco
                .collect(Collectors.groupingBy(
                        extra -> extra,
                        Collectors.counting()
                ))
                .forEach(frecuenciaExtras::put); // Transfiere el resultado al mapa

        if (frecuenciaExtras.isEmpty()) {
            return "No hay extras registrados.";
        }

        // 2. Encontrar el extra con la máxima frecuencia
        Map.Entry<String, Long> maxEntry = Collections.max(
                frecuenciaExtras.entrySet(),
                Map.Entry.comparingByValue()
        );

        return maxEntry.getKey() + " (Aparece " + maxEntry.getValue() + " veces)";
    }

    /**
     * Cargar csv
     */
    private static void importarcsv() {
        CarCtrl carCtrl = new CarCtrl(cm.getUrl());
        String csv = config.getProperty("CSVFileDefault");

        List<Car> cars = new ArrayList<>();
        List<Car> cochesError = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] cocheArr = line.split(";");

                String[] extrasArray = cocheArr[3].split("\\|");
                List<String> extrasList = Arrays.asList(extrasArray);

                Float precioFloat = Float.parseFloat(cocheArr[4]);

                Car coche = new Car(cocheArr[0], cocheArr[1], cocheArr[2], extrasList, precioFloat);
                cars.add(coche);
            }

            for (Car car : cars) {
                try{
                    if (carCtrl.insert(car)){
                        System.out.println(car);
                    }else{
                        cochesError.add(car);
                        throw new CarNotFoundException("Error al insertar el coche");
                    }
                } catch (CarNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!cochesError.isEmpty()){
            System.out.println("Error al insertar estos vehiculos");
            for (Car car : cochesError) {
                System.out.println(car);
            }
        }

    }

    /**
     * Realizar traspaso entre usuarios
     * @throws CarNotFoundException
     */
    private static void traspaso() throws CarNotFoundException {
        PropietarioCtrl propietarioCtrl = new PropietarioCtrl(cm.getUrl());
        CarCtrl carCtrl = new CarCtrl(cm.getUrl());

        try{
            System.out.print("Introduzca el dni del vendedor: ");
            Propietario vendedor = propietarioCtrl.search(scanner.nextLine());

            List<Car> coches = carCtrl.searchPropietario(vendedor.getDni());

            if(!coches.isEmpty()){
                System.out.println("Coches del propietario: " + vendedor);
                for (Car car : coches) {
                    System.out.print(car);
                }

                System.out.println("\n¿Que coche quieres traspasar?");
                System.out.println("Escribe la matricula del coche que quieras traspasar");
                String matricula = scanner.nextLine();
                Car cocheTraspaso = carCtrl.search(matricula);

                if (Objects.equals(cocheTraspaso.getPropietario().getId(), vendedor.getId())) {
                    System.out.println("¿Quien es el comprador?");
                    System.out.print("Introduzca el dni del comprador: ");
                    Propietario comprador = propietarioCtrl.search(scanner.nextLine());

                    if (Objects.equals(comprador.getDni(), vendedor.getDni())) {
                        throw new PropietarioNotFoundException("Comprador no encontrado");
                    }

                    System.out.print("Introduzca el monto de la transaccion: ");
                    int monto = scanner.nextInt();
                    Traspaso traspaso = new Traspaso(matricula, Integer.parseInt(vendedor.getId()), Integer.parseInt(comprador.getId()), monto);

                    TraspasoCtrl traspasoCtrl = new TraspasoCtrl(cm.getUrl());

                    traspasoCtrl.insertar(traspaso);


                }else{
                    throw new CarNotFoundException("Matricula desconocida");
                }
            }else{
                throw new CarNotFoundException("No se encontraron coches para este vendedor");
            }

        }catch (PropietarioNotFoundException e){
            e.printStackTrace();
        }catch (CarNotFoundException e){
            e.printStackTrace();
        }


    }

    /**
     * Eliminar un vehiculo
     * @throws CarNotFoundException
     */
    private static void eliminarCoche() throws CarNotFoundException {
        CarCtrl carCtrl = new CarCtrl(cm.getUrl());
        System.out.println("Introduce la matricula del coche que quieres modificar:");

        Car coche = carCtrl.search(scanner.nextLine());

        if (coche== null){
            throw new CarNotFoundException("No se encontro el coche.");
        }

        if (carCtrl.delete(coche)){
            System.out.println("Coche eliminado correctamente.");
        }
        else{
            System.out.println("Error al eliminar el coche");
        }
    }

    /**
     * Metodo para modificar un vehiculo
     */
    private static void modificarCoche() {
        CarCtrl carCtrl = new CarCtrl(cm.getUrl());

        System.out.println("Introduce la matricula del coche que quieres modificar:");

        Car coche = carCtrl.search(scanner.nextLine());

        if (coche != null) {
            System.out.println(coche);

            System.out.println("\n --- Actualizar Coche --- ");
            System.out.print("Marca: ");
            String marca = scanner.nextLine();
            coche.setMarca(marca);
            System.out.print("Modelo: ");
            String modelo = scanner.nextLine();
            coche.setModelo(modelo);
            System.out.print("Precio: ");
            Float precio = Float.valueOf(scanner.nextLine());
            coche.setPrecio(precio);

            List<String> extras = extrasToList();
            coche.setExtras(extras);

            if (carCtrl.update(coche)!=0){
                System.out.println("Coche actualizado");
            }else{
                System.out.println("Error al actualizar");
            }

        }else{
            throw new CarNotFoundException("Coche no encontrado");
        }
    }

    /**
     * Buscar todos los coches con propietario igual al dni insertado por el usuario
     */
    private static void buscarPropietario() {
        CarCtrl carCtrl = new CarCtrl(cm.getUrl());

        System.out.println("Introduce el DNI del propietario que quiere buscar:");
        List<Car> coches = carCtrl.searchPropietario(scanner.nextLine());

        if (coches != null) {
            for (Car car : coches) {
                System.out.println(car);
            }
        }else{
            throw new CarNotFoundException("No se encontraron vehiculos asociados al propietario");
        }
    }

    /**
     * Metodo para buscar todos los coches del concesionario
     */
    private static void buscarConcesionario() {
        CarCtrl carCtrl = new CarCtrl(cm.getUrl());

        List<Car> coches = carCtrl.search(1);

        if (coches != null) {
            for (Car car : coches) {
                System.out.println(car);
            }
        }else{
            throw new CarNotFoundException("No se encontraron vehiculos asociados al propietario");
        }
    }

    /**
     * Crear nuevo coche
     */
    private static void createCar() {
        System.out.println("\n --- Insertar Nuevo Coche --- ");
        System.out.print("Matricula: ");
        String matricula = scanner.nextLine();
        System.out.print("Marca: ");
        String marca = scanner.nextLine();
        System.out.print("Modelo: ");
        String modelo = scanner.nextLine();
        System.out.print("Precio: ");
        Float precio = Float.valueOf(scanner.nextLine());

        List<String> extras = extrasToList();

        Car car = new Car(matricula,marca,modelo, extras, precio);

        CarCtrl carCtrl = new CarCtrl(cm.getUrl());


        if (carCtrl.insert(car)){
            System.out.println("Vehiculo agregado correctamente");
        }else{
            System.out.println("Hubo un erro al crear el vehiculo");
        }

    }

    /**
     * Metodo para preparar la lista de extras
     * @return
     */
    public static List<String> extrasToList(){
        Scanner s = new Scanner(System.in);

        System.out.println("Escribe todos los extras separados por coma:");
        System.out.println("Si no tiene ningun extra deja vacio");

        String extra = s.nextLine();

        return List.of(extra.split(","));
    }


    /**
     * Crear nuevo propietario
     */
    private static void createPropietario(){

        System.out.println("\n --- Insertar Nuevo Propietario --- ");
        System.out.print("Dni: ");
        String dni = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Telefono: ");
        String telefono = scanner.nextLine();

        Propietario propietario = new Propietario();
        propietario.setDni(dni);
        propietario.setNombre(nombre);
        propietario.setApellido(apellido);
        propietario.setTelefono(telefono);

        PropietarioCtrl propietarioCtrl = new PropietarioCtrl(cm.getUrl());


        if (propietarioCtrl.insert(propietario)!=0){
            System.out.println("Propietario agregado correctamente");
        }else{
            System.out.println("Hubo un erro al crear el propietario");
        }
    }

    /**
     * Crear base de datos
     */
    private static void createDatabase() {
        final String URL = cm.getUrl();
        boolean useSQLite = cm.getBdSqlite();
        try (Connection connection = DriverManager.getConnection(URL);
             Statement stmt = connection.createStatement()) {

            // SQLite necesita activar las foreign keys manualmente
            if (useSQLite) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            // Drops (iguales para ambos)
            stmt.executeUpdate("DROP TABLE IF EXISTS traspasos");
            stmt.executeUpdate("DROP TABLE IF EXISTS coches");
            stmt.executeUpdate("DROP TABLE IF EXISTS propietarios");

            // =========================================
            // PROPIETARIOS
            // =========================================
            String sqlCreatePropietarios = useSQLite
                    ? """
                  CREATE TABLE propietarios (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      dni TEXT NOT NULL UNIQUE,
                      nombre TEXT NOT NULL,
                      apellidos TEXT NOT NULL,
                      telefono TEXT NOT NULL
                  )
                  """
                    : """
                  CREATE TABLE propietarios (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      dni VARCHAR(100) NOT NULL UNIQUE,
                      nombre VARCHAR(100) NOT NULL,
                      apellidos VARCHAR(150) NOT NULL,
                      telefono VARCHAR(15) NOT NULL
                  )
                  """;
            String insertDefault = "INSERT INTO propietarios (dni, nombre, apellidos, telefono) VALUES ('00000000X', 'Concesionario', 'Concesionario', '666777888');";
            stmt.executeUpdate(sqlCreatePropietarios);
            stmt.executeUpdate(insertDefault);

            // =========================================
            // COCHES
            // =========================================
            String sqlCreateCoches = useSQLite
                    ? """
                  CREATE TABLE coches (
                      matricula TEXT PRIMARY KEY,
                      marca TEXT NOT NULL,
                      modelo TEXT NOT NULL,
                      extras TEXT NOT NULL,
                      precio REAL NOT NULL,
                      id_propietario INTEGER NOT NULL,
                      FOREIGN KEY (id_propietario) REFERENCES propietarios(id)
                  )
                  """
                    : """
                  CREATE TABLE coches (
                      matricula VARCHAR(10) PRIMARY KEY,
                      marca VARCHAR(50) NOT NULL,
                      modelo VARCHAR(50) NOT NULL,
                      extras VARCHAR(255) NOT NULL,
                      precio DECIMAL(10,2) NOT NULL,
                      id_propietario INT NOT NULL,
                      FOREIGN KEY (id_propietario) REFERENCES propietarios(id)
                  )
                  """;
            stmt.executeUpdate(sqlCreateCoches);

            // =========================================
            // TRASPASOS
            // =========================================
            String sqlCreateTraspasos = useSQLite
                    ? """
                  CREATE TABLE traspasos (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      matricula_coche TEXT NOT NULL,
                      id_vendedor INTEGER,
                      id_comprador INTEGER NOT NULL,
                      monto_economico REAL NOT NULL,
                      FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),
                      FOREIGN KEY (id_vendedor) REFERENCES propietarios(id),
                      FOREIGN KEY (id_comprador) REFERENCES propietarios(id)
                  )
                  """
                    : """
                  CREATE TABLE traspasos (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      matricula_coche VARCHAR(10) NOT NULL,
                      id_vendedor INT,
                      id_comprador INT NOT NULL,
                      monto_economico DECIMAL(10,2) NOT NULL,
                      FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),
                      FOREIGN KEY (id_vendedor) REFERENCES propietarios(id),
                      FOREIGN KEY (id_comprador) REFERENCES propietarios(id)
                  )
                  """;
            stmt.executeUpdate(sqlCreateTraspasos);


            if (!useSQLite) {

            }

            System.out.println("✅ Tablas creadas correctamente para " + (useSQLite ? "SQLite" : "MySQL"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Controlar tipo de conexion que se va a utilizar
     */
    private static void connectionManager(){
        int cod;
        do {
            System.out.println("¿Que tipo de Base de datos quieres utilizar?");
            System.out.println("1. MySql");
            System.out.println("2. Sqlite");
            System.out.print("Ingrese opcion: ");
            cod = scanner.nextInt();

        }while (cod!= 1 && cod != 2);
        cm.changeConection(cod);
    }
}
