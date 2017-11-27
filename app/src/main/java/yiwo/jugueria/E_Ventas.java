package yiwo.jugueria;

/**
 * Created by Yiwo on 23/11/2017.
 */

public class E_Ventas {
    public static final String Table_Ventas="Create Table Ventas (" +
            "ID Integer primary key autoincrement, " +
            "CodVenta Integer, " +
            "Fecha DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) , " +
            "Total decimal" +
            ")";
    private int CodVenta;
    private double Total;

    public int getCodVenta() {
        return CodVenta;
    }

    public void setCodVenta(int codVenta) {
        CodVenta = codVenta;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }
}
