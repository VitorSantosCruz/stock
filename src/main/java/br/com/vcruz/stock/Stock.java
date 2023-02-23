package br.com.vcruz.stock;

import br.com.vcruz.stock.configuration.LiquibaseLoaderConfig;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.view.LoginView;
import javax.swing.JOptionPane;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Stock {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            LiquibaseLoaderConfig.load();
            
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException | InternalException e) {
            log.error("[main] - {}", e.getMessage());
            
            JOptionPane.showMessageDialog(null, "[Erro interno] - Não foi possível iniciar o aplicativo!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
