package yiwo.jugueria;

/**
 * Created by Yiwo on 23/11/2017.
 */

public class E_Productos {
    public static final String Table_Productos="Create Table Productos(" +
            "ID Integer primary key autoincrement, " +
            "Producto text unique, " +
            "Precio decimal, " +
            "Stock integer, " +
            "Disponible integer)";

    private String Producto;
    private Double Precio;
    private int Stock;
    private int Disponible;


    public E_Productos(String producto, Double precio, int stock, int disponible) {
        Producto = producto;
        Precio = precio;
        Stock = stock;
        Disponible = disponible;
    }

    public E_Productos(){

    }
    public String getProducto() {
        return Producto;
    }

    public void setProducto(String producto) {
        Producto = producto;
    }

    public Double getPrecio() {
        return Precio;
    }

    public void setPrecio(Double precio) {
        Precio = precio;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public int getDisponible() {
        return Disponible;
    }

    public void setDisponible(int disponible) {
        Disponible = disponible;
    }
}
