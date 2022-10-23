
package model;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class InvoiceLinesTableModel extends AbstractTableModel {

    private ArrayList<InvoiceLine> lines;
    private String[] cols = {"No.", "Item Name", "Item Price", "Count", "Item Total"};

    public InvoiceLinesTableModel(ArrayList<InvoiceLine> lines) {
        this.lines = lines;
    }

    public ArrayList<InvoiceLine> getLines() {
        return lines;
    }
    
    
    @Override
    public int getRowCount() {
        return lines.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int cols1) {
        return cols[cols1];
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        InvoiceLine line = lines.get(row);
        
        switch(column) {
            case 0: return line.getInvoiceHeader().getInvoiceNum();
            case 1: return line.getItemName();
            case 2: return line.getItemPrice();
            case 3: return line.getCount();
            case 4: return line.getTotal();
            default : return "";
        }
    }
    
}
