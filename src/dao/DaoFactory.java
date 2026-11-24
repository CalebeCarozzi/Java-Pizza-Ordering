package dao;

import dao.sql.ClienteDAOSql;
import dao.sql.SaborDAOSql;
import dao.sql.PrecoDAOSql;

public class DaoFactory {

    private DaoFactory() {
    }

    public static ClienteDAO getClienteDAO() {
        return new ClienteDAOSql();
    }

    public static SaborDAO getSaborDAO() {
        return new SaborDAOSql();
    }

    public static PrecoDAO getPrecoDAO() {
        return new PrecoDAOSql();
    }
}