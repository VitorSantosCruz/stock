package br.com.vcruz.stock.view.internal;

import br.com.vcruz.stock.model.Product;
import br.com.vcruz.stock.model.ProductInfo;
import br.com.vcruz.stock.model.Stock;
import br.com.vcruz.stock.service.StockService;
import br.com.vcruz.stock.utils.PageableUtils;
import br.com.vcruz.stock.view.DashboardView;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author vcruz
 */
public class SaleView extends javax.swing.JInternalFrame {

    private final StockService stockService;
    private final int CODE_COLUMN_POSITION;
    private final int SIZE_COLUMN_POSITION;
    private final int COLOR_COLUMN_POSITION;
    private final int PRICE_COLUMN_POSITION;
    private final int QUANTITY_COLUMN_POSITION;
    private List<Map<String, String>> cart;
    private int currentPage;
    private boolean isLookingFor;
    private int selectedRow;

    /**
     * Creates new form SaleView
     */
    public SaleView() {
        this.CODE_COLUMN_POSITION = 1;
        this.SIZE_COLUMN_POSITION = 2;
        this.COLOR_COLUMN_POSITION = 3;
        this.PRICE_COLUMN_POSITION = 7;
        this.QUANTITY_COLUMN_POSITION = 8;
        this.stockService = new StockService();
        this.cart = new ArrayList<>();
        this.selectedRow = -1;

        initComponents();

        int pageQuantity = this.stockService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        this.fillCombobok(pageQuantity);
    }

    /**
     * Creates new form SaleView
     *
     * @param cart
     */
    public SaleView(List<Map<String, String>> cart) {
        this.CODE_COLUMN_POSITION = 1;
        this.SIZE_COLUMN_POSITION = 2;
        this.COLOR_COLUMN_POSITION = 3;
        this.PRICE_COLUMN_POSITION = 7;
        this.QUANTITY_COLUMN_POSITION = 8;
        this.stockService = new StockService();
        this.cart = cart;
        this.selectedRow = -1;

        initComponents();
        int pageQuantity;

        if (cart.isEmpty()) {
            pageQuantity = this.stockService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        } else {
            this.goToCartButton.setEnabled(true);
            pageQuantity = this.stockService.pageQuantityExcept(cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        }

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

        if (featureMap.get("size") == null
                && featureMap.get("color") == null
                && featureMap.get("product_code") == null
                && featureMap.get("name") == null
                && featureMap.get("model") == null
                && featureMap.get("brand") == null) {
            this.isLookingFor = false;

            if (this.cart.isEmpty()) {
                pageQuantity = this.stockService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
            } else {
                pageQuantity = this.stockService.pageQuantityExcept(cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
            }
        } else {
            this.isLookingFor = true;
            if (this.cart.isEmpty()) {
                pageQuantity = this.stockService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, featureMap);
            } else {
                pageQuantity = this.stockService.pageQuantityExcept(cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, featureMap);
            }
        }

        this.fillCombobok(pageQuantity);

        if (isLookingFor) {
            if (this.cart.isEmpty()) {
                this.loadStockList(this.stockService.findBy(featureMap, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, PageableUtils.FIRST_PAGE));
            } else {
                this.loadStockList(this.stockService.findByExcept(cart, featureMap, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, PageableUtils.FIRST_PAGE));
            }
        }
    }

    private Map<String, String> getFeatureMap() {
        Map<String, String> featureMap = new HashMap<>();
        String size = this.sizeTextField.getText();
        String color = this.colorTextField.getText();
        String code = this.codeTextField.getText();
        String name = this.nameTextField.getText();
        String model = this.modelTextField.getText();
        String brand = this.brandTextField.getText();

        if (!size.isBlank()) {
            featureMap.put("size", size);
        }

        if (!color.isBlank()) {
            featureMap.put("color", color);
        }

        if (!code.isBlank()) {
            featureMap.put("product_code", code);
        }

        if (!name.isBlank()) {
            featureMap.put("name", name);
        }

        if (!model.isBlank()) {
            featureMap.put("model", model);
        }

        if (!brand.isBlank()) {
            featureMap.put("brand", brand);
        }

        return featureMap;
    }

    private void addToCart() {
        String quantityString = JOptionPane.showInputDialog(this, "Quantos produtos iguais a esse você deseja adicionar ao carrinho?", "Quantidade a ser adicionada", JOptionPane.INFORMATION_MESSAGE);
        int quantityToBeAdded;

        try {
            if (quantityString == null || quantityString.isBlank()) {
                return;
            }

            int productQuantity = (Integer) this.stockTable.getValueAt(this.selectedRow, this.QUANTITY_COLUMN_POSITION);

            quantityToBeAdded = Integer.parseInt(quantityString);

            if (quantityToBeAdded > productQuantity) {
                JOptionPane.showMessageDialog(this, "A quantidade a ser adicionada não pode ser mair que a quantidade disponível!", "Erro", JOptionPane.ERROR_MESSAGE);
                this.addToCart();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "É necessário que um número interiro seja informado!", "Erro", JOptionPane.ERROR_MESSAGE);
            this.addToCart();
            return;
        }

        String productCode = (String) this.stockTable.getValueAt(this.selectedRow, this.CODE_COLUMN_POSITION);
        String productSize = (String) this.stockTable.getValueAt(this.selectedRow, this.SIZE_COLUMN_POSITION);
        String productColor = (String) this.stockTable.getValueAt(this.selectedRow, this.COLOR_COLUMN_POSITION);
        BigDecimal productPrice = (BigDecimal) this.stockTable.getValueAt(this.selectedRow, this.PRICE_COLUMN_POSITION);

        Map<String, String> product = new HashMap<>();
        product.put("productCode", productCode);
        product.put("size", productSize);
        product.put("color", productColor);
        product.put("unitPrice", productPrice.toString());
        product.put("quantity", String.valueOf(quantityToBeAdded));

        Map<String, String> productFound = new HashMap<>();
        this.cart = this.cart.stream().map(cartProduct -> {
            if (cartProduct.get("productCode").equals(productCode)
                    && cartProduct.get("size").equals(productSize)
                    && cartProduct.get("color").equals(productColor)) {
                int cartProductQuantity = Integer.parseInt(cartProduct.get("quantity"));

                cartProduct.put("quantity", String.valueOf(cartProductQuantity + quantityToBeAdded));
                productFound.putAll(cartProduct);
            }

            return cartProduct;
        }).collect(Collectors.toCollection(ArrayList::new));

        if (this.cart.isEmpty() || productFound.isEmpty()) {
            this.cart.add(product);
        }

        int pageQuantity;

        if (isLookingFor) {
            Map<String, String> featureMap = this.getFeatureMap();
            pageQuantity = this.stockService.pageQuantityExcept(this.cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, featureMap);

        } else {
            pageQuantity = this.stockService.pageQuantityExcept(this.cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        }

        this.fillCombobok(pageQuantity);

        this.goToCartButton.setEnabled(true);
    }

    private void loadStockList(List<Stock> stocks) {
        try {
            this.fillStockTable(stocks);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "[Erro interno] - Não foi possível carregar a lista de produtos!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillStockTable(List<Stock> stocks) {
        this.clearTable();

        DefaultTableModel defaultTableModel = (DefaultTableModel) this.stockTable.getModel();

        stocks.forEach(stock -> {
            Product product = stock.getProduct();
            ProductInfo productInfo = stock.getProductInfo();

            defaultTableModel.addRow(new Object[]{stock.getId(), product.getCode(), productInfo.getSize(), productInfo.getColor(), product.getName(), product.getModel(), product.getBrand(), product.getPrice(), stock.getQuantity()});
        });
    }

    private void clearTable() {
        DefaultTableModel defaultTableModel = (DefaultTableModel) this.stockTable.getModel();
        defaultTableModel.setRowCount(0);
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
        sizeLabel = new javax.swing.JLabel();
        sizeTextField = new javax.swing.JTextField();
        colorLabel = new javax.swing.JLabel();
        colorTextField = new javax.swing.JTextField();
        codeLabel = new javax.swing.JLabel();
        codeTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        modelLabel = new javax.swing.JLabel();
        modelTextField = new javax.swing.JTextField();
        brandLabel = new javax.swing.JLabel();
        brandTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        stockScrollPane = new javax.swing.JScrollPane();
        stockTable = new javax.swing.JTable();
        utilsPanel = new javax.swing.JPanel();
        pageComboBox = new javax.swing.JComboBox<>();
        cartPanel = new javax.swing.JPanel();
        goToCartButton = new javax.swing.JButton();
        addToCartButton = new javax.swing.JButton();

        setClosable(true);

        searchPanel.setPreferredSize(new java.awt.Dimension(787, 250));

        sizeLabel.setText("Tamanho");

        sizeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sizeTextFieldKeyPressed(evt);
            }
        });

        colorLabel.setText("Cor");

        colorTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                colorTextFieldKeyPressed(evt);
            }
        });

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
                            .addComponent(nameLabel)
                            .addComponent(modelLabel)
                            .addComponent(sizeLabel)
                            .addComponent(brandLabel)
                            .addComponent(colorLabel))
                        .addGap(49, 49, 49)
                        .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(modelTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                            .addComponent(brandTextField)
                            .addComponent(nameTextField)
                            .addComponent(codeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(colorTextField)
                            .addComponent(sizeTextField, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addComponent(codeLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
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
                    .addComponent(sizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sizeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colorLabel))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        getContentPane().add(searchPanel, java.awt.BorderLayout.PAGE_START);

        stockTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Código", "Tamanho", "Cor", "Nome", "Modelo", "Marca", "Preço", "Quantidade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stockTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        stockTable.getTableHeader().setReorderingAllowed(false);
        stockTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                stockTableMouseReleased(evt);
            }
        });
        stockScrollPane.setViewportView(stockTable);

        getContentPane().add(stockScrollPane, java.awt.BorderLayout.CENTER);

        utilsPanel.setLayout(new java.awt.BorderLayout());

        pageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageComboBoxActionPerformed(evt);
            }
        });
        utilsPanel.add(pageComboBox, java.awt.BorderLayout.CENTER);

        cartPanel.setLayout(new java.awt.GridLayout());

        goToCartButton.setText("Ir para o carrinho");
        goToCartButton.setEnabled(false);
        goToCartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToCartButtonActionPerformed(evt);
            }
        });
        goToCartButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                goToCartButtonKeyPressed(evt);
            }
        });
        cartPanel.add(goToCartButton);

        addToCartButton.setText("Adicionar ao carrinho");
        addToCartButton.setEnabled(false);
        addToCartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToCartButtonActionPerformed(evt);
            }
        });
        addToCartButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                addToCartButtonKeyPressed(evt);
            }
        });
        cartPanel.add(addToCartButton);

        utilsPanel.add(cartPanel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(utilsPanel, java.awt.BorderLayout.PAGE_END);

        setBounds(0, 0, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void sizeTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sizeTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_sizeTextFieldKeyPressed

    private void colorTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_colorTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_colorTextFieldKeyPressed

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

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        this.search();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.search();
        }
    }//GEN-LAST:event_searchButtonKeyPressed

    private void stockTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stockTableMouseReleased
        if (this.selectedRow == this.stockTable.getSelectedRow()) {
            this.stockTable.clearSelection();
            this.selectedRow = -1;
            this.addToCartButton.setEnabled(false);
        } else {
            this.selectedRow = this.stockTable.getSelectedRow();
            this.addToCartButton.setEnabled(true && DashboardView.loggedUser.isRoot());
        }
    }//GEN-LAST:event_stockTableMouseReleased

    private void addToCartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToCartButtonActionPerformed
        this.addToCart();
    }//GEN-LAST:event_addToCartButtonActionPerformed

    private void addToCartButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addToCartButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.addToCart();
        }
    }//GEN-LAST:event_addToCartButtonKeyPressed

    private void pageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageComboBoxActionPerformed
        this.selectedRow = -1;
        this.addToCartButton.setEnabled(false);
        this.currentPage = 0;

        if (this.pageComboBox.getItemCount() > 0) {
            this.currentPage = Integer.parseInt(String.valueOf(this.pageComboBox.getSelectedItem())) - 1;
        }

        if (!isLookingFor) {
            if (this.cart.isEmpty()) {
                this.loadStockList(this.stockService.findAll(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, this.currentPage));
            } else {
                this.loadStockList(this.stockService.findAllExcept(this.cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, this.currentPage));
            }
        }
    }//GEN-LAST:event_pageComboBoxActionPerformed

    private void goToCartButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_goToCartButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            DashboardView.openInternalFrame(new CartView(this.cart));
        }
    }//GEN-LAST:event_goToCartButtonKeyPressed

    private void goToCartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToCartButtonActionPerformed
        DashboardView.openInternalFrame(new CartView(this.cart));
    }//GEN-LAST:event_goToCartButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToCartButton;
    private javax.swing.JLabel brandLabel;
    private javax.swing.JTextField brandTextField;
    private javax.swing.JPanel cartPanel;
    private javax.swing.JLabel codeLabel;
    private javax.swing.JTextField codeTextField;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JTextField colorTextField;
    private javax.swing.JButton goToCartButton;
    private javax.swing.JLabel modelLabel;
    private javax.swing.JTextField modelTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox<String> pageComboBox;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextField sizeTextField;
    private javax.swing.JScrollPane stockScrollPane;
    private javax.swing.JTable stockTable;
    private javax.swing.JPanel utilsPanel;
    // End of variables declaration//GEN-END:variables
}
