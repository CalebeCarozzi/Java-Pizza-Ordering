package dao;

import dao.sql.ClienteDAOSql;
import dao.sql.SaborDAOSql;

public class DaoFactory {

    private DaoFactory() {
    }

    public static ClienteDAO getClienteDAO() {
        return new ClienteDAOSql();
    }

    public static SaborDAO getSaborDAO() {
        return new SaborDAOSql();
    }
}