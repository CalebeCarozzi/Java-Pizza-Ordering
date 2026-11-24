package dao;

import dao.sql.ClienteDAOSql;
import dao.sql.SaborDAOSql;
import dao.sql.PrecoDAOSql;
import dao.sql.PedidoDAOSql;

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

    public static PedidoDAO getPedidoDAO() {
        return new PedidoDAOSql();
    }
}