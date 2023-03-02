package br.com.vcruz.stock.view.internal;

import br.com.vcruz.stock.enumerator.PaymentMethod;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.model.Product;
import br.com.vcruz.stock.model.ProductInfo;
import br.com.vcruz.stock.model.Stock;
import br.com.vcruz.stock.service.SaleService;
import br.com.vcruz.stock.service.StockService;
import br.com.vcruz.stock.utils.PageableUtils;
import br.com.vcruz.stock.view.DashboardView;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class CartView extends javax.swing.JInternalFrame {

    private final StockService stockService;
    private final SaleService saleService;
    private final int CODE_COLUMN_POSITION;
    private final int SIZE_COLUMN_POSITION;
    private final int COLOR_COLUMN_POSITION;
    private final int QUANTITY_COLUMN_POSITION;
    private List<Map<String, String>> cart;
    private int currentPage;
    private boolean isLookingFor;
    private int selectedRow;
    private BigDecimal price;
    private BigDecimal discount;

    /**
     * Creates new form CartView
     *
     * @param cart
     */
    public CartView(List<Map<String, String>> cart) {
        this.CODE_COLUMN_POSITION = 1;
        this.SIZE_COLUMN_POSITION = 2;
        this.COLOR_COLUMN_POSITION = 3;
        this.QUANTITY_COLUMN_POSITION = 8;
        this.stockService = new StockService();
        this.saleService = new SaleService();
        this.cart = cart;
        this.selectedRow = -1;
        this.discount = BigDecimal.ZERO;

        initComponents();

        int pageQuantity = this.stockService.pageQuantityOnCart(cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        this.fillCombobok(pageQuantity);

        this.setPrice();
    }

    private void setPrice() {
        this.price = cart.stream()
                .map(product -> new BigDecimal(product.get("unitPrice")).multiply(new BigDecimal(product.get("quantity"))))
                .reduce((accumulator, combiner) -> accumulator.add(combiner))
                .orElse(BigDecimal.ZERO);

        BigDecimal discountPrice = this.price.subtract(this.price.multiply(this.discount.divide(new BigDecimal("100")))).setScale(2, RoundingMode.HALF_UP);

        this.priceLabel.setText("Valor total: R$ " + discountPrice + "  ");
    }

    private void setDiscount() {
        this.discount = new BigDecimal(this.discountTextField.getText().isBlank() ? "0" : this.discountTextField.getText());
        this.setPrice();
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

        int pageQuantity = 0;

        if (featureMap.get("size") == null
                && featureMap.get("color") == null
                && featureMap.get("product_code") == null
                && featureMap.get("name") == null
                && featureMap.get("model") == null
                && featureMap.get("brand") == null) {
            this.isLookingFor = false;

            pageQuantity = this.stockService.pageQuantityOnCart(cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        } else {
            this.isLookingFor = true;
            pageQuantity = this.stockService.pageQuantityOnCart(cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, featureMap);
        }

        this.fillCombobok(pageQuantity);

        if (isLookingFor) {
            if (this.cart.isEmpty()) {
                this.loadStockList(new ArrayList<>());
            } else {
                this.loadStockList(this.stockService.findByOnCart(cart, featureMap, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, PageableUtils.FIRST_PAGE));
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

    private void delete() {
        String quantityString = JOptionPane.showInputDialog(this, "Quantos produtos iguais a esse você deseja remover do carrinho?", "Quantidade a ser excluída", JOptionPane.INFORMATION_MESSAGE);
        int quantityToBeRemove;

        try {
            if (quantityString == null || quantityString.isBlank()) {
                return;
            }

            int productQuantity = (Integer) this.stockTable.getValueAt(this.selectedRow, this.QUANTITY_COLUMN_POSITION);

            quantityToBeRemove = Integer.parseInt(quantityString);

            if (quantityToBeRemove > productQuantity) {
                JOptionPane.showMessageDialog(this, "A quantidade a ser removida não pode ser mair que a quantidade disponível!", "Erro", JOptionPane.ERROR_MESSAGE);
                this.delete();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "É necessário que um número interiro seja informado!", "Erro", JOptionPane.ERROR_MESSAGE);
            this.delete();
            return;
        }

        String productCode = (String) this.stockTable.getValueAt(this.selectedRow, this.CODE_COLUMN_POSITION);
        String productSize = (String) this.stockTable.getValueAt(this.selectedRow, this.SIZE_COLUMN_POSITION);
        String productColor = (String) this.stockTable.getValueAt(this.selectedRow, this.COLOR_COLUMN_POSITION);

        this.cart = this.cart.stream().map(cartProduct -> {
            if (cartProduct.get("productCode").equals(productCode)
                    && cartProduct.get("size").equals(productSize)
                    && cartProduct.get("color").equals(productColor)) {
                int cartProductQuantity = Integer.parseInt(cartProduct.get("quantity"));

                cartProduct.put("quantity", String.valueOf(cartProductQuantity - quantityToBeRemove));
            }

            return cartProduct;
        }).filter(cartProduct -> Integer.parseInt(cartProduct.get("quantity")) > 0).collect(Collectors.toCollection(ArrayList::new));

        this.setPrice();

        int pageQuantity;

        if (isLookingFor) {
            Map<String, String> featureMap = this.getFeatureMap();
            pageQuantity = this.stockService.pageQuantityOnCart(cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, featureMap);

        } else {
            pageQuantity = this.stockService.pageQuantityOnCart(cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE);
        }

        if (this.cart.isEmpty()) {
            DashboardView.openInternalFrame(new SaleView());
        }
        
        if (pageQuantity == 0) {
            this.loadStockList(new ArrayList<>());
        }

        this.fillCombobok(pageQuantity);
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

    private void sale() {
        PaymentMethod[] paymentMethodValues = PaymentMethod.values();
        String[] choices = new String[paymentMethodValues.length];
        Map<String, String> choicesMap = new HashMap<>();

        for (int i = 0; i < paymentMethodValues.length; i++) {
            choices[i] = paymentMethodValues[i].getValue();
            choicesMap.put(paymentMethodValues[i].getValue(), paymentMethodValues[i].toString());
        }

        String choice = (String) JOptionPane.showInputDialog(null, "Escolha o método de pagamento",
                "Método de pagamento", JOptionPane.INFORMATION_MESSAGE, null,
                choices,
                choices[1]);

        if (choice == null) {
            return;
        }

        String paymentMethod = choicesMap.get(choice);

        try {
            this.saleService.save(cart, this.price, PaymentMethod.valueOf(paymentMethod), this.discount, DashboardView.loggedUser.getId());

            DashboardView.openInternalFrame(new SaleView());
        } catch (InternalException e) {
            JOptionPane.showMessageDialog(this, "[Erro interno] - Não foi possível finalizar a venda!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processDiscountInput(KeyEvent evt) {
        switch (evt.getKeyChar()) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {

                if (!(this.discountTextField.getText() + String.valueOf(evt.getKeyChar())).isBlank() && Integer.parseInt(this.discountTextField.getText() + String.valueOf(evt.getKeyChar())) > 100) {
                    evt.consume();
                }
                break;
            }
            default ->
                evt.consume();
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
        saleAndPricePanel = new javax.swing.JPanel();
        pricePanel = new javax.swing.JPanel();
        discountPanel = new javax.swing.JPanel();
        discountTextField = new javax.swing.JTextField();
        discountLabel = new javax.swing.JLabel();
        percentLabel = new javax.swing.JLabel();
        priceLabel = new javax.swing.JLabel();
        salePanel = new javax.swing.JPanel();
        deleteButton = new javax.swing.JButton();
        saleButton = new javax.swing.JButton();

        setClosable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

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
        stockTable.setToolTipText("");
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

        saleAndPricePanel.setLayout(new java.awt.BorderLayout());

        pricePanel.setLayout(new java.awt.BorderLayout());

        discountPanel.setLayout(new java.awt.BorderLayout());

        discountTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                discountTextFieldFocusLost(evt);
            }
        });
        discountTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                discountTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                discountTextFieldKeyTyped(evt);
            }
        });
        discountPanel.add(discountTextField, java.awt.BorderLayout.CENTER);

        discountLabel.setText("Desconto   ");
        discountPanel.add(discountLabel, java.awt.BorderLayout.LINE_START);

        percentLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        percentLabel.setText("%");
        discountPanel.add(percentLabel, java.awt.BorderLayout.LINE_END);

        pricePanel.add(discountPanel, java.awt.BorderLayout.PAGE_START);

        priceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        priceLabel.setText("Valor total: R$ 0  ");
        pricePanel.add(priceLabel, java.awt.BorderLayout.CENTER);

        saleAndPricePanel.add(pricePanel, java.awt.BorderLayout.PAGE_START);

        salePanel.setLayout(new java.awt.GridLayout(1, 0));

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
        salePanel.add(deleteButton);

        saleButton.setText("Vender");
        saleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saleButtonActionPerformed(evt);
            }
        });
        saleButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                saleButtonKeyPressed(evt);
            }
        });
        salePanel.add(saleButton);

        saleAndPricePanel.add(salePanel, java.awt.BorderLayout.CENTER);

        utilsPanel.add(saleAndPricePanel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(utilsPanel, java.awt.BorderLayout.PAGE_END);

        setBounds(0, 0, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        DashboardView.openInternalFrame(new SaleView(this.cart));
    }//GEN-LAST:event_formInternalFrameClosing

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
            this.deleteButton.setEnabled(false);
        } else {
            this.selectedRow = this.stockTable.getSelectedRow();
            this.deleteButton.setEnabled(true && DashboardView.loggedUser.isRoot());
        }
    }//GEN-LAST:event_stockTableMouseReleased

    private void pageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageComboBoxActionPerformed
        this.selectedRow = -1;
        this.deleteButton.setEnabled(false);
        this.currentPage = 0;

        if (this.pageComboBox.getItemCount() > 0) {
            this.currentPage = Integer.parseInt(String.valueOf(this.pageComboBox.getSelectedItem())) - 1;
        }

        if (!isLookingFor) {
            if (this.cart.isEmpty()) {
                this.loadStockList(new ArrayList<>());
            } else {
                this.loadStockList(this.stockService.findAllOnCart(this.cart, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, this.currentPage));
            }
        }
    }//GEN-LAST:event_pageComboBoxActionPerformed

    private void saleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saleButtonActionPerformed
        this.sale();
    }//GEN-LAST:event_saleButtonActionPerformed

    private void saleButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_saleButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.sale();
        }
    }//GEN-LAST:event_saleButtonKeyPressed

    private void deleteButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_deleteButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.delete();
        }
    }//GEN-LAST:event_deleteButtonKeyPressed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        this.delete();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void discountTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_discountTextFieldKeyTyped
        this.processDiscountInput(evt);
    }//GEN-LAST:event_discountTextFieldKeyTyped

    private void discountTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_discountTextFieldFocusLost
        this.setDiscount();
    }//GEN-LAST:event_discountTextFieldFocusLost

    private void discountTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_discountTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.setDiscount();
        }
    }//GEN-LAST:event_discountTextFieldKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel brandLabel;
    private javax.swing.JTextField brandTextField;
    private javax.swing.JLabel codeLabel;
    private javax.swing.JTextField codeTextField;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JTextField colorTextField;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel discountLabel;
    private javax.swing.JPanel discountPanel;
    private javax.swing.JTextField discountTextField;
    private javax.swing.JLabel modelLabel;
    private javax.swing.JTextField modelTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox<String> pageComboBox;
    private javax.swing.JLabel percentLabel;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JPanel pricePanel;
    private javax.swing.JPanel saleAndPricePanel;
    private javax.swing.JButton saleButton;
    private javax.swing.JPanel salePanel;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextField sizeTextField;
    private javax.swing.JScrollPane stockScrollPane;
    private javax.swing.JTable stockTable;
    private javax.swing.JPanel utilsPanel;
    // End of variables declaration//GEN-END:variables
}
