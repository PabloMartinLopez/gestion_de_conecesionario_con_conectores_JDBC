package service;

import model.Propietario;

import java.sql.*;

public class PropietarioCtrl {
    private String URL;

    public PropietarioCtrl(String URL) {
        this.URL = URL;
    }

    public int insert(Propietario propietario){

        String sql = "INSERT INTO `propietarios` (`id`, `dni`, `nombre`, `apellidos`, `telefono`) VALUES (?, ?, ?, ?, ?);";

        try(Connection connection = DriverManager.getConnection(URL);
            PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, propietario.getId());
            pstmt.setString(2, propietario.getDni());
            pstmt.setString(3, propietario.getNombre());
            pstmt.setString(4, propietario.getApellido());
            pstmt.setString(5, propietario.getTelefono());

            int filas = pstmt.executeUpdate();
            return filas;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
