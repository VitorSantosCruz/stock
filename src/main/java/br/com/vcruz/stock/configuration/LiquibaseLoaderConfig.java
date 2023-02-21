package br.com.vcruz.stock.configuration;

import br.com.vcruz.stock.exception.InternalException;
import java.io.InputStream;
import java.util.Properties;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LiquibaseLoaderConfig {

    public static void load() {
        try (InputStream inputStream = new LiquibaseLoaderConfig().getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String changeLogFile = properties.getProperty("changeLogFile");

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(ConnectionConfig.getConnection()));

            try (Liquibase liquibase = new liquibase.Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(new Contexts(), new LabelExpression());
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            log.error("[load] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }
}
