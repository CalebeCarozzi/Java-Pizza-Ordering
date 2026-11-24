package dao;

import dao.sql.ClienteDAOSql;

public class DaoFactory {

    private DaoFactory() {
    }

    public static ClienteDAO getClienteDAO() {
        return new ClienteDAOSql();
    }
}