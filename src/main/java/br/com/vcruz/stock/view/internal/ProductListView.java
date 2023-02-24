package br.com.vcruz.stock.view.internal;

import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.model.Product;
import br.com.vcruz.stock.service.ProductService;
import br.com.vcruz.stock.utils.PageableUtils;
import br.com.vcruz.stock.view.DashboardView;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author vcruz
 */
public class ProductListView extends javax.swing.JInternalFrame {

    private final ProductService productService;
    private final int ID_COLUMN_POSITION;
    private final int CODE_COLUMN_POSITION;
    private int currentPage;
    private boolean isLookingFor;
    private int selectedRow;

    /**
     * Creates new form ProductListView
     */
    public ProductListView() {
        this.ID_COLUMN_POSITION = 0;
        this.CODE_COLUMN_POSITION = 1;
        this.productService = new ProductService();
        this.selectedRow = -1;

        initComponents();

        int pageQuantity = this.productService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
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
        Map<String, String> featureMap = this.getFeatureMap();

        int pageQuantity;

        if (featureMap.get("product_code") == null
                && featureMap.get("name") == null
                && featureMap.get("model") == null
                && featureMap.get("brand") == null) {
            this.isLookingFor = false;
            pageQuantity = this.productService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        } else {
            this.isLookingFor = true;
            pageQuantity = this.productService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, featureMap);
        }

        this.fillCombobok(pageQuantity);

        if (isLookingFor) {
            this.loadProductList(this.productService.findBy(featureMap, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, PageableUtils.FIRST_PAGE));
        }
    }

    private Map<String, String> getFeatureMap() {
        Map<String, String> featureMap = new HashMap<>();
        String code = this.codeTextField.getText();
        String name = this.nameTextField.getText();
        String model = this.modelTextField.getText();
        String brand = this.brandTextField.getText();

        if (!code.isBlank()) {
            featureMap.put("product_code", this.codeTextField.getText());
        }

        if (!name.isBlank()) {
            featureMap.put("name", this.nameTextField.getText());
        }

        if (!model.isBlank()) {
            featureMap.put("model", this.modelTextField.getText());
        }

        if (!brand.isBlank()) {
            featureMap.put("brand", this.brandTextField.getText());
        }

        return featureMap;
    }

    private void delete() {
        Object[] options = {"Sim", "Não"};
        int delete = JOptionPane.showOptionDialog(this, "Tem certeza que deseja apagar esse produto?", "Apagar", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (JOptionPane.YES_OPTION == delete) {
            if (this.selectedRow >= 0) {
                Long id = (Long) this.productTable.getValueAt(this.selectedRow, this.ID_COLUMN_POSITION);

                try {
                    this.productService.deleteById(id);
                    int pageQuantity = this.productService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);

                    if (pageQuantity == 0) {
                        this.loadProductList(this.productService.findAll(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, PageableUtils.FIRST_PAGE));
                    }

                    this.fillCombobok(pageQuantity);
                    JOptionPane.showMessageDialog(this, "Produto apagado!", "Apagar produto", JOptionPane.INFORMATION_MESSAGE);
                } catch (InternalException e) {
                    JOptionPane.showMessageDialog(this, "[Erro interno] - Não foi possível apagar o produto!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void loadProductList(List<Product> products) {
        try {
            this.fillProductTable(products);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "[Erro interno] - Não foi possível carregar a lista de produtos!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillProductTable(List<Product> products) {
        this.clearTable();

        DefaultTableModel defaultTableModel = (DefaultTableModel) this.productTable.getModel();

        products.forEach(product -> {
            defaultTableModel.addRow(new Object[]{product.getId(), product.getCode(), product.getName(), product.getModel(), product.getBrand(), product.getPrice()});
        });
    }

    private void clearTable() {
        DefaultTableModel defaultTableModel = (DefaultTableModel) this.productTable.getModel();
        defaultTableModel.setRowCount(0);
    }

    private void edit() {
        try {
            String code = (String) this.productTable.getValueAt(this.selectedRow, this.CODE_COLUMN_POSITION);
            Product product = this.productService.findByCode(code);
            DashboardView.openInternalFrame(new ProductRegisterView(product, true));
        } catch (InternalException e) {
            JOptionPane.showMessageDialog(this, "[Erro interno] - Não foi possível carregar o produto!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchPanel = new javax.swing.JPanel();
        codeLabel = new javax.swing.JLabel();
        codeTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        modelLabel = new javax.swing.JLabel();
        modelTextField = new javax.swing.JTextField();
        brandLabel = new javax.swing.JLabel();
        brandTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        productScrollPane = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        utilsPanel = new javax.swing.JPanel();
        pageComboBox = new javax.swing.JComboBox<>();
        editAndDeletePanel = new javax.swing.JPanel();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setClosable(true);

        codeLabel.setText("Código");

        codeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                codeTextFieldKeyPressed(evt);
            }
        });

        nameLabel.setText("Nome");

        nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nameTextFieldKeyPressed(evt);
            }
        });

        modelLabel.setText("Modelo");

        modelTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                modelTextFieldKeyPressed(evt);
            }
        });

        brandLabel.setText("Marca");

        brandTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                brandTextFieldKeyPressed(evt);
            }
        });

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

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(codeLabel)
                            .addComponent(nameLabel)
                            .addComponent(modelLabel)
                            .addComponent(brandLabel))
                        .addGap(18, 18, 18)
                        .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(modelTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
                            .addComponent(codeTextField)
                            .addComponent(nameTextField)
                            .addComponent(brandTextField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(searchButton)))
                .addContainerGap())
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(codeLabel)
                    .addComponent(codeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modelLabel)
                    .addComponent(modelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(brandLabel)
                    .addComponent(brandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(searchButton)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        getContentPane().add(searchPanel, java.awt.BorderLayout.PAGE_START);

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Código", "Nome", "Modelo", "Marca", "Preço"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                productTableMouseReleased(evt);
            }
        });
        productScrollPane.setViewportView(productTable);
        if (productTable.getColumnModel().getColumnCount() > 0) {
            productTable.getColumnModel().getColumn(0).setResizable(false);
            productTable.getColumnModel().getColumn(1).setResizable(false);
            productTable.getColumnModel().getColumn(2).setResizable(false);
            productTable.getColumnModel().getColumn(3).setResizable(false);
            productTable.getColumnModel().getColumn(4).setResizable(false);
            productTable.getColumnModel().getColumn(5).setResizable(false);
        }

        getContentPane().add(productScrollPane, java.awt.BorderLayout.CENTER);

        utilsPanel.setLayout(new java.awt.BorderLayout());

        pageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageComboBoxActionPerformed(evt);
            }
        });
        utilsPanel.add(pageComboBox, java.awt.BorderLayout.CENTER);

        editAndDeletePanel.setLayout(new java.awt.GridLayout(1, 0));

        editButton.setText("Editar");
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        editButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                editButtonKeyPressed(evt);
            }
        });
        editAndDeletePanel.add(editButton);

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
        editAndDeletePanel.add(deleteButton);

        utilsPanel.add(editAndDeletePanel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(utilsPanel, java.awt.BorderLayout.PAGE_END);

        setBounds(0, 0, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void codeTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_codeTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_codeTextFieldKeyPressed

    private void nameTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_nameTextFieldKeyPressed

    private void modelTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_modelTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_modelTextFieldKeyPressed

    private void brandTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_brandTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_brandTextFieldKeyPressed

    private void searchButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_searchButtonKeyPressed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        this.search();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void deleteButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_deleteButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.delete();
        }
    }//GEN-LAST:event_deleteButtonKeyPressed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        this.delete();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void pageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageComboBoxActionPerformed
        this.selectedRow = -1;
        this.deleteButton.setEnabled(false);
        this.editButton.setEnabled(false);

        if (this.pageComboBox.getItemCount() > 0 && !isLookingFor) {
            this.currentPage = Integer.parseInt(String.valueOf(this.pageComboBox.getSelectedItem())) - 1;
            this.loadProductList(this.productService.findAll(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, this.currentPage));
        }
    }//GEN-LAST:event_pageComboBoxActionPerformed

    private void productTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productTableMouseReleased
        if (this.selectedRow == this.productTable.getSelectedRow()) {
            this.productTable.clearSelection();
            this.selectedRow = -1;
            this.deleteButton.setEnabled(false);
            this.editButton.setEnabled(false);
        } else {
            this.selectedRow = this.productTable.getSelectedRow();
            this.deleteButton.setEnabled(true && DashboardView.loggedUser.isRoot());
            this.editButton.setEnabled(true);
        }
    }//GEN-LAST:event_productTableMouseReleased

    private void editButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_editButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.edit();
        }
    }//GEN-LAST:event_editButtonKeyPressed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        this.edit();
    }//GEN-LAST:event_editButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel brandLabel;
    private javax.swing.JTextField brandTextField;
    private javax.swing.JLabel codeLabel;
    private javax.swing.JTextField codeTextField;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel editAndDeletePanel;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel modelLabel;
    private javax.swing.JTextField modelTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox<String> pageComboBox;
    private javax.swing.JScrollPane productScrollPane;
    private javax.swing.JTable productTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel utilsPanel;
    // End of variables declaration//GEN-END:variables
}
