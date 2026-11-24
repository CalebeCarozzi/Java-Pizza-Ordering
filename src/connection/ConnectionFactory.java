package connection;

import exception.PersistenciaException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final String ARQUIVO_CONFIG =
            "config/db.properties";

    public static Connection getConnection() {

        Properties propriedades = new Properties();

        try (FileInputStream arquivo =
                     new FileInputStream(ARQUIVO_CONFIG)) {

            propriedades.load(arquivo);

        } catch (IOException e) {

            throw new PersistenciaException(
                    "Não foi possível carregar as configurações do banco.",
                    e
            );
        }

        String url = propriedades.getProperty("db.url");
        String usuario = propriedades.getProperty("db.user");
        String senha = propriedades.getProperty("db.password");

        if (url == null || url.isBlank()
                || usuario == null || usuario.isBlank()
                || senha == null) {

            throw new PersistenciaException(
                    "Configurações do banco de dados inválidas."
            );
        }

        try {

            return DriverManager.getConnection(
                    url,
                    usuario,
                    senha
            );

        } catch (SQLException e) {

            throw new PersistenciaException(
                    "Não foi possível conectar ao banco de dados.",
                    e
            );
        }
    }
}