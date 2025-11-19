package service;

import model.Traspaso;

import java.sql.*;

public class TraspasoCtrl {
    private String URL;

    public TraspasoCtrl(String URL) {
        this.URL = URL;
    }

    public void insertar(Traspaso traspaso){
        String sql ="INSERT INTO `traspasos` (`matricula_coche`, `id_vendedor`, `id_comprador`, `monto_economico`) VALUES (?, ?, ?, ?);";
        String sqlCar ="UPDATE `coches` SET `id_propietario`=? WHERE `matricula` = ?";
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);

            try(PreparedStatement pstmt = conn.prepareStatement(sql);
            PreparedStatement pstmtCar = conn.prepareStatement(sqlCar);){

                pstmt.setString(1, traspaso.getMatricula_coche());
                pstmt.setInt(2, traspaso.getId_vendedor());
                pstmt.setInt(3, traspaso.getId_comprador());
                pstmt.setDouble(4, traspaso.getMonto_economico());
                pstmt.executeUpdate();

                pstmtCar.setInt(1, traspaso.getId_comprador());
                pstmtCar.setString(2, traspaso.getMatricula_coche());
                pstmtCar.executeUpdate();

                System.out.println("Operación realizada con éxito");
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try{
                    conn.rollback();
                } catch (SQLException ex) {
                    e.printStackTrace();
                }
            }else{
                e.printStackTrace();
            }
        }finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
