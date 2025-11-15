import model.Car;
import model.Propietario;
import service.CarCtrl;
import service.PropietarioCtrl;
import util.ConecctionManager;
import util.ConfigLoader;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class App {
    private static ConfigLoader config = new ConfigLoader();
    private static Scanner scanner = new Scanner(System.in);
    private static ConecctionManager cm = new ConecctionManager(1);

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
                System.out.println("Error: Por favor, introduce un número válido.");
                option = -1;
            }
        } while (option != 0);
    }

    /**
     * Mostrar menu con todas sus opciones
     */
    private static void showMenu() {
        System.out.println("\n--- GESTIÓN CONCESIONARIO ---");
        System.out.println("1. Crear base de datos");
        System.out.println("2. Insertar nuevo propietario");
        System.out.println("3. Insertar nuevo coche");
        System.out.println("4. Mostrar coches del concesionario");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    /**
     * Controlar opciones del menu
     * @param option Int Opcion del menu por el usuario
     */
    private static void handleOption(int option) {
        switch (option) {
            case 1:
                createDatabase();
                break;
            case 2:
                createPropietario();
                break;
            case 3:
                createCar();
                break;
            case 4:
                buscarConcesionario();
            case 0: break;
            default:
                System.out.println("Opción no válida. Inténtalo de nuevo." + option);
        }
    }

    /**
     * Metodo para buscar todos los coches del concesionario
     */
    private static void buscarConcesionario() {
        CarCtrl carCtrl = new CarCtrl(cm.getUrl());

        List<Car> coches = carCtrl.search(1);

        for (Car car : coches) {
            System.out.println(car);
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
        carCtrl.insert(car);

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
        propietarioCtrl.insert(propietario);

        System.out.println("Registro guardado");

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
                      dni TEXT NOT NULL,
                      nombre TEXT NOT NULL UNIQUE,
                      apellidos TEXT NOT NULL,
                      telefono TEXT NOT NULL
                  )
                  """
                    : """
                  CREATE TABLE propietarios (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      dni VARCHAR(100) NOT NULL,
                      nombre VARCHAR(100) NOT NULL UNIQUE,
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
