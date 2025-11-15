package service;

import com.mysql.cj.jdbc.ConnectionImpl;
import model.Car;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CarCtrl {
    private String URL;

    public CarCtrl(String URL) {
        this.URL = URL;
    }

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
}
