package util;

import java.util.Scanner;

public class ConecctionManager {
    private Boolean bdMysql = false;
    private Boolean bdSqlite=false;
    private static ConfigLoader config = new ConfigLoader();

    private String url ="";

    /**
     * Constructor para iniciar la coneccion con la bd
     * @param codigo Tipo de conexcion que se va a emplear
     */
    public ConecctionManager(int codigo) {
        changeConection(codigo);
    }

    /**
     * Metodo para iniciar la coneccion con la bd
     * @param codigo Tipo de conexcion que se va a emplear
     */
    public void changeConection(int codigo){
        switch (codigo) {
            case 1:
                bdMysql = true;
                bdSqlite=false;
                break;
            case 2:
                bdMysql = false;
                bdSqlite=true;
                break;
            default:
                break;
        }
    }


    /**
     * metodo para devolver la url de la conexion con la bd
     * @return url de la conexion
     */
    public String getUrl(){

        if (bdMysql){
            url = config.getProperty("db.URL.MYSQL");
        } else if (bdSqlite) {
            url = config.getProperty("db.URL.SQLITE");
        }

        return url;
    }

    public Boolean getBdMysql() {
        return bdMysql;
    }

    public Boolean getBdSqlite() {
        return bdSqlite;
    }
}
