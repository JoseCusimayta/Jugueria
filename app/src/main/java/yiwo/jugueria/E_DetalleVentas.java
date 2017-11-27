package yiwo.jugueria;


/**
 * Created by Yiwo on 23/11/2017.
 */

public class E_DetalleVentas {
    public static final String Table_Name="DetalleVentas";
    public static final String Field_CodVenta="CodVenta";
    public static final String Field_Producto="Producto";
    public static final String Field_Cantidad="Cantidad";
    public static final String Field_SubTotal="SubTotal";
    public static final String Table_DetalleVentas = "Create Table "+Table_Name+" (" +
            "ID Integer primary key autoincrement," +
            Field_CodVenta+" Integer, " +
            Field_Producto+" text, " +
            Field_Cantidad+" integer, " +
            Field_SubTotal+" decimal " +
            ")";
    private int CodVenta;
    private String Producto;
    private int Cantidad;
    private Double SubTotal;

    public E_DetalleVentas(int codVenta, String producto, int cantidad, Double subTotal) {
        CodVenta = codVenta;
        Producto = producto;
        Cantidad = cantidad;
        SubTotal = subTotal;
    }

    public E_DetalleVentas(){
    }
    public int getCodVenta() {
        return CodVenta;
    }

    public void setCodVenta(int codVenta) {
        CodVenta = codVenta;
    }

    public String getProducto() {
        return Producto;
    }

    public void setProducto(String producto) {
        Producto = producto;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }

    public Double getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(Double subTotal) {
        SubTotal = subTotal;
    }
}