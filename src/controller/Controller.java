package controller;

import model.InvoiceHeader;
import model.InvoicesTableModel;
import model.InvoiceLine;
import model.InvoiceLinesTableModel;
import view.InvoiceHeaderDialog;
import view.MainFrame;
import view.InvoiceLineDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller implements ActionListener, ListSelectionListener {

    private MainFrame mainFrame;
    private InvoiceHeaderDialog invoiceHeaderDialog;
    private InvoiceLineDialog lineDialog;

    public Controller(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("Action: " + actionCommand);
        switch (actionCommand) {
            case "Load File":
                loadFile();
                break;
            case "Save File":
                saveFile();
                break;
            case "Create New Invoice":
                createNewInvoice();
                break;
            case "Delete Invoice":
                deleteInvoice();
                break;
            case "Create New Item":
                createNewItem();
                break;
            case "Delete Item":
                deleteItem();
                break;
            case "createInvoiceCancel":
                createInvoiceCancel();
                break;
            case "createInvoiceOK":
                createInvoiceOK();
                break;
            case "createLineOK":
                createLineOK();
                break;
            case "createLineCancel":
                createLineCancel();
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = mainFrame.getInvoiceTable().getSelectedRow();
        if (selectedIndex != -1) {
            System.out.println("You have selected row: " + selectedIndex);
            InvoiceHeader currentInvoice = mainFrame.getInvoices().get(selectedIndex);
            mainFrame.getInvoiceNumLabel().setText("" + currentInvoice.getInvoiceNum());
            mainFrame.getInvoiceDateLabel().setText(currentInvoice.getInvoicedate());
            mainFrame.getCustomerNameLabel().setText(currentInvoice.getCustomerName());
            mainFrame.getInvoiceTotalLabel().setText("" + currentInvoice.getInvoiceTotal());
            InvoiceLinesTableModel linesTableModel = new InvoiceLinesTableModel(currentInvoice.getInvoicelines());
            mainFrame.getLineTable().setModel(linesTableModel);
            linesTableModel.fireTableDataChanged();
        }
    }
 private void saveFile() {
        ArrayList<InvoiceHeader> invoices = mainFrame.getInvoices();
        String headers = "";
        String lines = "";
        for (InvoiceHeader invoice : invoices) {
            String invCSV = invoice.getCSV();
            headers += invCSV;
            headers += "\n";

            for (InvoiceLine line : invoice.getInvoicelines()) {
                String lineCSV = line.getCSV();
                lines += lineCSV;
                lines += "\n";
            }
        }
        System.out.println("Check point");
        try {
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(mainFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                FileWriter hfw = new FileWriter(headerFile);
                hfw.write(headers);
                hfw.flush();
                hfw.close();
                result = fc.showSaveDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    FileWriter lfw = new FileWriter(lineFile);
                    lfw.write(lines);
                    lfw.flush();
                    lfw.close();
                }
            }
        } catch (Exception ex) {

        }
    }

    private void loadFile() {
        JFileChooser fc = new JFileChooser();
        try {
            int result = fc.showOpenDialog(mainFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                Path headerPath = Paths.get(headerFile.getAbsolutePath());
                List<String> headerLines = Files.readAllLines(headerPath);
                System.out.println("Invoices have been read");
                // 1,22-11-2020,Ali
                // 2,13-10-2021,Saleh
                // 3,09-01-2019,Ibrahim
                ArrayList<InvoiceHeader> invoicesArray = new ArrayList<>();
                for (String headerLine : headerLines) {
                    String[] headerParts = headerLine.split(",");
                    int invoiceNum = Integer.parseInt(headerParts[0]);
                    String invoiceDate = headerParts[1];
                    String customerName = headerParts[2];

                    InvoiceHeader invoice = new InvoiceHeader(invoiceNum, invoiceDate, customerName);
                    invoicesArray.add(invoice);
                }
                System.out.println("Check point");
                result = fc.showOpenDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    Path linePath = Paths.get(lineFile.getAbsolutePath());
                    List<String> lineLines = Files.readAllLines(linePath);
                    System.out.println("Lines have been read");
                    for (String lineLine : lineLines) {
                        String lineParts[] = lineLine.split(",");
                        int invoiceNum = Integer.parseInt(lineParts[0]);
                        String itemName = lineParts[1];
                        double itemPrice = Double.parseDouble(lineParts[2]);
                        int count = Integer.parseInt(lineParts[3]);
                        InvoiceHeader inv = null;
                        for (InvoiceHeader invoice : invoicesArray) {
                            if (invoice.getInvoiceNum() == invoiceNum) {
                                inv = invoice;
                                break;
                            }
                        }

                        InvoiceLine line = new InvoiceLine(itemPrice, itemName, count);
                        inv.getInvoicelines().add(line);
                    }
                    System.out.println("Check point");
                }
                mainFrame.setInvoices(invoicesArray);
                InvoicesTableModel invoicesTableModel = new InvoicesTableModel(invoicesArray);
                mainFrame.setInvoicesTableModel(invoicesTableModel);
                mainFrame.getInvoiceTable().setModel(invoicesTableModel);
                mainFrame.getInvoicesTableModel().fireTableDataChanged();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

   
    private void createNewInvoice() {
        invoiceHeaderDialog = new InvoiceHeaderDialog(mainFrame);
        invoiceHeaderDialog.setVisible(true);
    }

    private void deleteInvoice() {
        int selectedRow = mainFrame.getInvoiceTable().getSelectedRow();
        if (selectedRow != -1) {
            mainFrame.getInvoices().remove(selectedRow);
            mainFrame.getInvoicesTableModel().fireTableDataChanged();
        }
    }

    private void createNewItem() {
        lineDialog = new InvoiceLineDialog(mainFrame);
        lineDialog.setVisible(true);
    }

    private void deleteItem() {
        int selectedRow = mainFrame.getLineTable().getSelectedRow();

        if (selectedRow != -1) {
            InvoiceLinesTableModel linesTableModel = (InvoiceLinesTableModel) mainFrame.getLineTable().getModel();
            linesTableModel.getLines().remove(selectedRow);
            linesTableModel.fireTableDataChanged();
            mainFrame.getInvoicesTableModel().fireTableDataChanged();
        }
    }

    private void createInvoiceCancel() {
        invoiceHeaderDialog.setVisible(false);
        invoiceHeaderDialog.dispose();
        invoiceHeaderDialog = null;
    }

    private void createInvoiceOK() {
        
        String date = invoiceHeaderDialog.getInvDateField().getText();
       
        int num = mainFrame.getNextInvoiceNum();

        InvoiceHeader invoice = new InvoiceHeader(num, date, date);
        mainFrame.getInvoices().add(invoice);
        mainFrame.getInvoicesTableModel().fireTableDataChanged();
        invoiceHeaderDialog.setVisible(false);
        invoiceHeaderDialog.dispose();
        invoiceHeaderDialog = null;
    }

    private void createLineOK() {
        String item = lineDialog.getItemNameField().getText();
        
        String priceStr = lineDialog.getItemPriceField().getText();
        String countStr = lineDialog.getItemCountField().getText();
        int count = Integer.parseInt(countStr);
        
        double price = Double.parseDouble(priceStr);
        int selectedInvoice = mainFrame.getInvoiceTable().getSelectedRow();
        if (selectedInvoice != -1) {
            InvoiceHeader invoice = mainFrame.getInvoices().get(selectedInvoice);
            InvoiceLine line = new InvoiceLine(price, item, count);
            invoice.getInvoicelines().add(line);
            InvoiceLinesTableModel linesTableModel = (InvoiceLinesTableModel) mainFrame.getLineTable().getModel();
            //linesTableModel.getLines().add(line);
            linesTableModel.fireTableDataChanged();
            mainFrame.getInvoicesTableModel().fireTableDataChanged();
        }
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

    private void createLineCancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

}
