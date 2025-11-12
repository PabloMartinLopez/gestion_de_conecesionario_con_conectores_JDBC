import util.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class App {
    private static ConfigLoader config = new ConfigLoader();
    private static final String URLMSQ = config.getProperty("db.URL.MYSQL");
    private static final String URLSQL = config.getProperty("db.URL.SQLITE");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        int option;
        do {
            showMenu();
            try {
                option = Integer.parseInt(scanner.nextLine());
                handleOption(option);
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor, introduce un número válido.");
                option = -1;
            }
        } while (option != 0);
    }

    private static void showMenu() {
        System.out.println("\n--- GESTIÓN CONCESIONARIO ---");
        System.out.println("1. Crear base de datos");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }
    private static void handleOption(int option) {
        switch (option) {
            case 1:
                createDatabase();
                break;


            case 0: break;
            default:
                System.out.println("Opción no válida. Inténtalo de nuevo.");
        }
    }

    private static void createDatabase() {
        final String URL =  URLSQL;// => jdbc:sqlite:basedatos.db
        try(Connection connection = DriverManager.getConnection(URL);
            Statement stmt = connection.createStatement()) {

            // DROPS
            String sqlDropTraspasos = "DROP TABLE IF EXISTS traspasos";
            String sqlDropCoches = "DROP TABLE IF EXISTS coches";
            String sqlDropPropietarios = "DROP TABLE IF EXISTS propietarios";

            stmt.executeUpdate(sqlDropTraspasos);
            stmt.executeUpdate(sqlDropCoches);
            stmt.executeUpdate(sqlDropPropietarios);



            //  Creacion Tabla de propietarios
            String sqlCreatePropietarios = "CREATE TABLE propietarios (" +
                    " id INT AUTO_INCREMENT PRIMARY KEY," +
                    " dni VARCHAR(100) NOT NULL," +
                    " nombre VARCHAR(100) NOT NULL UNIQUE," +
                    " apellidos VARCHAR(150) NOT NULL," +
                    " telefono VARCHAR(15) NOT NULL)";
            stmt.executeUpdate(sqlCreatePropietarios);

            //  Creacion tabla de coches
            String sqlCreateCoches = "CREATE TABLE coches (" +
                    "matricula VARCHAR(10) PRIMARY KEY," +
                    "marca VARCHAR(50) NOT NULL," +
                    "modelo VARCHAR(50) NOT NULL," +
                    "extras VARCHAR(255) NOT NULL," +
                    "precio DECIMAL(10,2) NOT NULL," +
                    "id_propietario INT NOT NULL," +
                    "FOREIGN KEY (id_propietario) REFERENCES propietarios(id))";

            stmt.executeUpdate(sqlCreateCoches);

            //  Creacion tabla de traspasos
            String sqlCreateTraspasos = "CREATE TABLE traspasos(" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "matricula_coche VARCHAR(10) NOT NULL," +
                    "id_vendedor INT," +
                    "id_comprador INT NOT NULL," +
                    "monto_economico DECIMAL(10,2) NOT NULL)";

            stmt.executeUpdate(sqlCreateTraspasos);

            System.out.println("Tabla 'cuentas' creada y poblada.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
