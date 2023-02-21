package br.com.vcruz.stock.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class ConnectionConfig {

    public static Connection getConnection() throws IOException, SQLException {
        try (InputStream inputStream = new ConnectionConfig().getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String url = properties.getProperty("url");
            String user = properties.getProperty("username");
            String password = properties.getProperty("password");

            return DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLException e) {
            log.error("[getConnection] - {}", e.getMessage());

            throw e;
        }
    }
}
