package br.com.vcruz.stock.view.internal;

import br.com.vcruz.stock.model.Sale;
import br.com.vcruz.stock.service.SaleService;
import br.com.vcruz.stock.utils.PageableUtils;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author vcruz
 */
public class SaleListView extends javax.swing.JInternalFrame {

    private final SaleService saleService;
    private int currentPage;
    private final DateTimeFormatter formatter;
    private LocalDate currentStartDate;
    private LocalDate currentEndDate;

    /**
     * Creates new form SaleListView
     */
    public SaleListView() {
        this.saleService = new SaleService();
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.currentStartDate = LocalDate.now().minusWeeks(1);
        this.currentEndDate = LocalDate.now();

        initComponents();

        this.startDateFormattedTextField.setText(this.currentStartDate.format(this.formatter));
        this.endDateFormattedTextField.setText(this.currentEndDate.format(this.formatter));

        int pageQuantity = this.saleService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, this.currentStartDate, this.currentEndDate);
        this.fillCombobok(pageQuantity);
    }

    private void fillCombobok(int pageQuantity) {
        this.pageComboBox.removeAllItems();
        this.pageComboBox.setVisible(pageQuantity > 1);
        for (int i = 0; i < pageQuantity; i++) {
            this.pageComboBox.addItem(String.valueOf(i + 1));
        }
    }

    private void filter() {
        int pageQuantity = this.saleService.pageQuantity(PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, this.currentStartDate, this.currentEndDate);
        this.fillCombobok(pageQuantity);

        if (pageQuantity == 0) {
            this.clearTable();
        }
    }

    private void loadSaleList(List<Sale> sales) {
        try {
            this.fillSaleTable(sales);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "[Erro interno] - Não foi possível carregar a lista de vendas!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillSaleTable(List<Sale> sales) {
        this.clearTable();

        DefaultTableModel defaultTableModel = (DefaultTableModel) this.saleTable.getModel();

        sales.forEach(sale -> {
            defaultTableModel.addRow(new Object[]{sale.getId(), sale.getCreatedDate().format(this.formatter), sale.getPrice(), sale.getFormOfPayment().getValue(), sale.getDiscount()});
        });
    }

    private void clearTable() {
        DefaultTableModel defaultTableModel = (DefaultTableModel) this.saleTable.getModel();
        defaultTableModel.setRowCount(0);
    }

    private void setStartDate() {
        String startDateString = this.startDateFormattedTextField.getText();

        if (startDateString.matches("^([0-9]{2})/([0-9]{2})/([0-9]{4})$")) {
            LocalDate startDate = LocalDate.parse(startDateString, this.formatter);
            LocalDate endDate = LocalDate.parse(this.endDateFormattedTextField.getText(), this.formatter);
            boolean acceptNewStartDate = !(startDate.isAfter(LocalDate.now()) || startDate.isAfter(endDate));

            if (startDate.isAfter(LocalDate.now())) {
                startDate = LocalDate.now();
                this.currentStartDate = startDate;
                this.startDateFormattedTextField.setText(this.currentStartDate.format(this.formatter));
            }

            if (startDate.isAfter(endDate)) {
                this.currentStartDate = endDate;
                this.currentEndDate = startDate;

                this.startDateFormattedTextField.setText(this.currentStartDate.format(this.formatter));
                this.endDateFormattedTextField.setText(this.currentEndDate.format(this.formatter));
            }

            if (acceptNewStartDate) {
                this.currentStartDate = startDate;
            }
        } else {
            this.startDateFormattedTextField.setText(this.currentStartDate.format(this.formatter));
            this.endDateFormattedTextField.setText(this.currentEndDate.format(this.formatter));
        }
    }

    private void setEndDate() {
        String endDateString = this.endDateFormattedTextField.getText();

        if (endDateString.matches("^([0-9]{2})/([0-9]{2})/([0-9]{4})$")) {
            LocalDate startDate = LocalDate.parse(this.startDateFormattedTextField.getText(), this.formatter);
            LocalDate endDate = LocalDate.parse(endDateString, this.formatter);
            boolean acceptNewEndDate = !(endDate.isAfter(LocalDate.now()) || endDate.isBefore(startDate));

            if (endDate.isAfter(LocalDate.now())) {
                endDate = LocalDate.now();
                this.currentEndDate = endDate;
                this.endDateFormattedTextField.setText(this.currentEndDate.format(this.formatter));
            }

            if (endDate.isBefore(startDate)) {
                this.currentStartDate = endDate;
                this.currentEndDate = startDate;

                this.startDateFormattedTextField.setText(this.currentStartDate.format(this.formatter));
                this.endDateFormattedTextField.setText(this.currentEndDate.format(this.formatter));
            }

            if (acceptNewEndDate) {
                this.currentEndDate = endDate;
            }
        } else {
            this.startDateFormattedTextField.setText(this.currentStartDate.format(this.formatter));
            this.endDateFormattedTextField.setText(this.currentEndDate.format(this.formatter));
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
        endDateLabel = new javax.swing.JLabel();
        startDateLabel = new javax.swing.JLabel();
        filterButton = new javax.swing.JButton();
        startDateFormattedTextField = new javax.swing.JFormattedTextField();
        endDateFormattedTextField = new javax.swing.JFormattedTextField();
        saleScrollPane = new javax.swing.JScrollPane();
        saleTable = new javax.swing.JTable();
        utilsPanel = new javax.swing.JPanel();
        pageComboBox = new javax.swing.JComboBox<>();
        priceLabel = new javax.swing.JLabel();

        setClosable(true);

        searchPanel.setPreferredSize(new java.awt.Dimension(787, 120));

        endDateLabel.setText("Até");

        startDateLabel.setText("De");

        filterButton.setText("Filtrar");
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });
        filterButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filterButtonKeyPressed(evt);
            }
        });

        startDateFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        startDateFormattedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                startDateFormattedTextFieldFocusLost(evt);
            }
        });
        startDateFormattedTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                startDateFormattedTextFieldKeyPressed(evt);
            }
        });

        endDateFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        endDateFormattedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                endDateFormattedTextFieldFocusLost(evt);
            }
        });
        endDateFormattedTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                endDateFormattedTextFieldKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(filterButton))
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(endDateLabel)
                            .addComponent(startDateLabel))
                        .addGap(18, 18, 18)
                        .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startDateFormattedTextField)
                            .addComponent(endDateFormattedTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE))))
                .addContainerGap())
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDateLabel)
                    .addComponent(startDateFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endDateLabel)
                    .addComponent(endDateFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(searchPanel, java.awt.BorderLayout.PAGE_START);

        saleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Data", "Preço", "Forma de pagamento", "Desconto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class
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
        saleTable.setToolTipText("");
        saleTable.setRowSelectionAllowed(false);
        saleTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        saleTable.getTableHeader().setReorderingAllowed(false);
        saleScrollPane.setViewportView(saleTable);
        if (saleTable.getColumnModel().getColumnCount() > 0) {
            saleTable.getColumnModel().getColumn(0).setResizable(false);
            saleTable.getColumnModel().getColumn(1).setResizable(false);
            saleTable.getColumnModel().getColumn(2).setResizable(false);
            saleTable.getColumnModel().getColumn(3).setResizable(false);
            saleTable.getColumnModel().getColumn(4).setResizable(false);
        }

        getContentPane().add(saleScrollPane, java.awt.BorderLayout.CENTER);

        utilsPanel.setLayout(new java.awt.BorderLayout());

        pageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageComboBoxActionPerformed(evt);
            }
        });
        utilsPanel.add(pageComboBox, java.awt.BorderLayout.PAGE_START);

        priceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        priceLabel.setText("Valor total: R$ 0  ");
        utilsPanel.add(priceLabel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(utilsPanel, java.awt.BorderLayout.PAGE_END);

        setBounds(0, 0, 800, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        this.filter();
    }//GEN-LAST:event_filterButtonActionPerformed

    private void filterButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.filter();
        }
    }//GEN-LAST:event_filterButtonKeyPressed

    private void startDateFormattedTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_startDateFormattedTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.setStartDate();
            this.filter();
        }
    }//GEN-LAST:event_startDateFormattedTextFieldKeyPressed

    private void endDateFormattedTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_endDateFormattedTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.setEndDate();
            this.filter();
        }
    }//GEN-LAST:event_endDateFormattedTextFieldKeyPressed

    private void startDateFormattedTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_startDateFormattedTextFieldFocusLost
        this.setStartDate();
    }//GEN-LAST:event_startDateFormattedTextFieldFocusLost

    private void endDateFormattedTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_endDateFormattedTextFieldFocusLost
        this.setEndDate();
    }//GEN-LAST:event_endDateFormattedTextFieldFocusLost

    private void pageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageComboBoxActionPerformed
        this.currentPage = 0;

        if (this.pageComboBox.getItemCount() > 0) {
            this.currentPage = Integer.parseInt(String.valueOf(this.pageComboBox.getSelectedItem())) - 1;
        }

        List<Sale> saleList = this.saleService.findAll(this.currentStartDate, this.currentEndDate, PageableUtils.MAX_OF_REGISTERS_IN_A_PAGE, PageableUtils.FIRST_PAGE);
        BigDecimal totalValue = saleList.stream().map(sale -> sale.getPrice()).reduce((accumulator, combiner) -> accumulator.add(combiner)).orElse(BigDecimal.ZERO);
        this.priceLabel.setText("Valor total: R$ " + totalValue + "  ");
        this.loadSaleList(this.saleService.findAll(this.currentStartDate, this.currentEndDate, PageableUtils.MAX_QUANTITY_OF_ITENS_IN_THE_PAGE, this.currentPage));
    }//GEN-LAST:event_pageComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField endDateFormattedTextField;
    private javax.swing.JLabel endDateLabel;
    private javax.swing.JButton filterButton;
    private javax.swing.JComboBox<String> pageComboBox;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JScrollPane saleScrollPane;
    private javax.swing.JTable saleTable;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JFormattedTextField startDateFormattedTextField;
    private javax.swing.JLabel startDateLabel;
    private javax.swing.JPanel utilsPanel;
    // End of variables declaration//GEN-END:variables
}
