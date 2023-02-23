package br.com.vcruz.stock.view.internal;

import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.model.User;
import br.com.vcruz.stock.service.UserService;
import br.com.vcruz.stock.utils.PageableUtils;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author vcruz
 */
public class UserListView extends javax.swing.JInternalFrame {

    private final UserService userService;
    private final int ID_COLUMN_POSITION;
    private int currentPage;
    private boolean isLookingFor;
    private int selectedRow;

    /**
     * Creates new form UserListView
     */
    public UserListView() {
        this.ID_COLUMN_POSITION = 0;
        this.userService = new UserService();
        this.selectedRow = -1;

        initComponents();

        int pageQuantity = this.userService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        this.fillCombobok(pageQuantity);
    }

    private void fillCombobok(int pageQuantity) {
        this.pageComboBox.removeAllItems();
        this.pageComboBox.setVisible(pageQuantity > 1);
        for (int i = 0; i < pageQuantity; i++) {
            this.pageComboBox.addItem(String.valueOf(i + 1));
        }
    }

    private void search() {
        String feature = this.searchTextField.getText();
        int pageQuantity;

        if (feature.isBlank()) {
            this.isLookingFor = false;
            pageQuantity = this.userService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        } else {
            this.isLookingFor = true;
            pageQuantity = this.userService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, feature);
        }

        this.fillCombobok(pageQuantity);

        if (isLookingFor) {
            this.loadUserList(this.userService.findBy(feature, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, PageableUtils.FIRST_PAGE));
        }
    }

    private void delete() {
        Object[] options = {"Sim", "Não"};
        int delete = JOptionPane.showOptionDialog(this, "Tem certeza que deseja apagar esse usuário?", "Apagar", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (JOptionPane.YES_OPTION == delete) {
            if (this.selectedRow >= 0) {
                Long id = (Long) this.userTable.getValueAt(this.selectedRow, this.ID_COLUMN_POSITION);

                try {
                    this.userService.deleteById(id);
                    int pageQuantity = this.userService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);

                    if (pageQuantity == 0) {
                        this.loadUserList(this.userService.findAll(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, PageableUtils.FIRST_PAGE));
                    }

                    this.fillCombobok(pageQuantity);
                    JOptionPane.showMessageDialog(this, "Usuário apagado!", "Apagar usuário", JOptionPane.INFORMATION_MESSAGE);
                } catch (InternalException e) {
                    JOptionPane.showMessageDialog(this, "[Erro interno] - Não foi possível apagar o usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void loadUserList(List<User> users) {
        try {
            this.fillUserTable(users);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "[Erro interno] - Não foi possível carregar a lista de usuários!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillUserTable(List<User> users) {
        this.clearTable();

        DefaultTableModel defaultTableModel = (DefaultTableModel) this.userTable.getModel();

        users.forEach(user -> {
            defaultTableModel.addRow(new Object[]{user.getId(), user.getName(), user.getLogin(), "********", user.isRoot()});
        });
    }

    private void clearTable() {
        DefaultTableModel defaultTableModel = (DefaultTableModel) this.userTable.getModel();
        defaultTableModel.setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchPanel = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        userScrollPane = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        utilsPanel = new javax.swing.JPanel();
        pageComboBox = new javax.swing.JComboBox<>();
        deleteButton = new javax.swing.JButton();

        setClosable(true);
        setTitle("Listagem de usuários");

        searchPanel.setLayout(new java.awt.BorderLayout());

        searchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchTextFieldKeyPressed(evt);
            }
        });
        searchPanel.add(searchTextField, java.awt.BorderLayout.CENTER);

        searchButton.setText("Procurar");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        searchButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchButtonKeyPressed(evt);
            }
        });
        searchPanel.add(searchButton, java.awt.BorderLayout.LINE_END);

        getContentPane().add(searchPanel, java.awt.BorderLayout.PAGE_START);

        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "login", "Senha", "Root"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                userTableMouseReleased(evt);
            }
        });
        userScrollPane.setViewportView(userTable);
        if (userTable.getColumnModel().getColumnCount() > 0) {
            userTable.getColumnModel().getColumn(0).setResizable(false);
            userTable.getColumnModel().getColumn(1).setResizable(false);
            userTable.getColumnModel().getColumn(2).setResizable(false);
            userTable.getColumnModel().getColumn(3).setResizable(false);
            userTable.getColumnModel().getColumn(4).setResizable(false);
        }

        getContentPane().add(userScrollPane, java.awt.BorderLayout.CENTER);

        utilsPanel.setLayout(new java.awt.BorderLayout());

        pageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageComboBoxActionPerformed(evt);
            }
        });
        utilsPanel.add(pageComboBox, java.awt.BorderLayout.CENTER);

        deleteButton.setText("Apagar");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        deleteButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                deleteButtonKeyPressed(evt);
            }
        });
        utilsPanel.add(deleteButton, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(utilsPanel, java.awt.BorderLayout.PAGE_END);

        setBounds(0, 0, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void searchTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_searchTextFieldKeyPressed

    private void searchButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_searchButtonKeyPressed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        this.search();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void pageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageComboBoxActionPerformed
        this.selectedRow = -1;
        this.deleteButton.setEnabled(false);

        if (this.pageComboBox.getItemCount() > 0 && !isLookingFor) {
            this.currentPage = Integer.parseInt(String.valueOf(this.pageComboBox.getSelectedItem())) - 1;
            this.loadUserList(this.userService.findAll(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, this.currentPage));
        }
    }//GEN-LAST:event_pageComboBoxActionPerformed

    private void userTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTableMouseReleased
        if (this.selectedRow == this.userTable.getSelectedRow()) {
            this.userTable.clearSelection();
            this.selectedRow = -1;
            this.deleteButton.setEnabled(false);
        } else {
            this.selectedRow = this.userTable.getSelectedRow();
            this.deleteButton.setEnabled(true);
        }
    }//GEN-LAST:event_userTableMouseReleased

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        this.delete();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void deleteButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_deleteButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.delete();
        }
    }//GEN-LAST:event_deleteButtonKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JComboBox<String> pageComboBox;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JScrollPane userScrollPane;
    private javax.swing.JTable userTable;
    private javax.swing.JPanel utilsPanel;
    // End of variables declaration//GEN-END:variables
}