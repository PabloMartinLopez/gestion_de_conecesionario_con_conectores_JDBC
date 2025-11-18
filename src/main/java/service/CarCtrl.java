package service;

import com.mysql.cj.jdbc.ConnectionImpl;
import model.Car;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarCtrl {
    private String URL;

    public CarCtrl(String URL) {
        this.URL = URL;
    }

    /**
     * Insertar un nuevo vehiculo en la base de datos
     * @param car
     * @return
     */
    public int insert(Car car) {
        String sql = "INSERT INTO `coches` (`matricula`, `marca`, `modelo`, `extras`, `precio`, `id_propietario`)" +
                " VALUES (?, ?, ?, ?, ?, 1);";

        try(Connection connection = DriverManager.getConnection(URL);
            PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, car.getMatricula());
            pstmt.setString(2, car.getMarca());
            pstmt.setString(3, car.getModelo());
            pstmt.setString(4, car.getExtrasString());
            pstmt.setString(5, car.getPrecio().toString());

            int filas = pstmt.executeUpdate();
            return filas;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Buscar un coche basado en el id del propietario
     * @param id del propietario
     * @return
     */
    public List<Car> search(int id){
        String sql = "Select * FROM `coches` WHERE `id_propietario` = ?;";

        List<Car> coches = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(id));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String matricula = rs.getString("matricula");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                List<String> extras = List.of(rs.getString("extras").split(", "));
                Float precio = Float.valueOf(rs.getString("precio"));

                Car coche = new Car(matricula, marca, modelo,extras, precio);

                coches.add(coche);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return coches;
    }

    /**
     * Buscar coches de un propietario basandonos en el dni del mismo
     * @param dni del propietario
     * @return
     */
    public List<Car> searchPropietario(String dni){
        String sql ="SELECT * FROM coches c JOIN propietarios p ON p.id = c.id_propietario WHERE p.dni LIKE ?";

        List<Car> coches = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%"+dni+"%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String matricula = rs.getString("matricula");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                List<String> extras = List.of(rs.getString("extras").split(", "));
                Float precio = Float.valueOf(rs.getString("precio"));

                coches.add(new Car(matricula, marca, modelo,extras, precio));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coches;
    }

    /**
     * Buscar coche por matricula
     * @param matriculaBusq matricula del vehiculo
     * @return
     */
    public Car search(String matriculaBusq) {
        String sql = "SELECT * FROM `coches` WHERE matricula = ?;";

        try(Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matriculaBusq);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {

                String matricula = rs.getString("matricula");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                List<String> extras = List.of(rs.getString("extras").split(", "));
                Float precio = Float.valueOf(rs.getString("precio"));
                return  new Car(matricula, marca, modelo,extras, precio);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
