package service;

import model.Propietario;

import java.sql.*;

public class PropietarioCtrl {
    private String URL;

    public PropietarioCtrl(String URL) {
        this.URL = URL;
    }

    /**
     * Creacion de propietario
     * @param propietario
     * @return int
     */
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

    public Propietario search(int id){
        String sql = "SELECT * FROM `propietarios` WHERE `id` = ?;";

        try(Connection connection = DriverManager.getConnection(URL);
            PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){

                String ident = rs.getString("id");
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellidos");
                String telefono = rs.getString("telefono");

                Propietario propietario = new  Propietario(ident,dni,nombre,apellido,telefono);

                return propietario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
