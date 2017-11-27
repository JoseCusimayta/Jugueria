package yiwo.jugueria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Yiwo on 13/11/2017.
 */

public class DB_Controller extends SQLiteOpenHelper {

    private static final String DB_NAME = "DBJugueria";
    private static final int SCHEME_VERSION = 1;

    public DB_Controller(Context context) {
        super(context, DB_NAME, null, SCHEME_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(E_Productos.Table_Productos);
        sqLiteDatabase.execSQL(E_DetalleVentas.Table_DetalleVentas);
        sqLiteDatabase.execSQL(E_Ventas.Table_Ventas);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("Drop Table if Exists Productos");
        onCreate(sqLiteDatabase);
    }

    public boolean insert_productos(String producto, Double precio, Integer Stock) {
        if (buscar_productos(producto).getCount() < 1) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("Producto", producto);
                contentValues.put("Precio", precio);
                contentValues.put("Stock", Stock);
                contentValues.put("Disponible", 1);
                getWritableDatabase().insertOrThrow("Productos", "", contentValues);
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean delete_productos(String ID) {
        try {
            getWritableDatabase().delete("Productos", "id=" + ID + "", null);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public boolean cambiar_producto(String old_producto, String new_producto) {
        if (buscar_productos(old_producto).getCount() > 0) {
            try {
                getWritableDatabase().execSQL("Update Productos set Producto='" + new_producto + "' where Producto='" + old_producto + "'");
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean update_productos(String producto, Double precio, Integer Stock) {
        if (buscar_productos(producto).getCount() > 0) {
            try {
                getWritableDatabase().execSQL("Update Productos set precio=" + precio + ", Stock =" + Stock + " where producto='" + producto + "'");
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean reducir_stock(String producto, Integer cantidad) {
        if (buscar_productos(producto).getCount() > 0) {
            try {
                getWritableDatabase().execSQL("Update Productos set stock= stock -" + cantidad + " where producto='" + producto + "'");
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean cancelar_producto(String producto) {

        if (buscar_productos(producto).getCount() < 1) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("Producto", producto);
                contentValues.put("Disponible", 0);
                getWritableDatabase().update("Productos", contentValues, "Producto='" + producto + "'", null);
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void list_all_productos(TextView tv_nombre, TextView tv_precio, TextView tv_cantidad) {
        try {
            tv_nombre.setText("");
            tv_precio.setText("");
            tv_cantidad.setText("");
            Cursor cursor = this.getWritableDatabase().rawQuery("Select * from Productos where Disponible=1", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    tv_nombre.append(cursor.getString(1) + "\n");
                    tv_precio.append(cursor.getString(2) + "\n");
                    tv_cantidad.append(cursor.getString(3) + "\n");
                    //textView.append(cursor.getString(1) + " " + cursor.getDouble(2) + " " + cursor.getInt(3) + "\n");
                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            tv_nombre.setText(e.getMessage());
        }
    }



    public void list_all_productos(final Context context, LinearLayout lyv_producto, LinearLayout lyv_cantidad, LinearLayout lyv_precio
            , final TextView tv_codProducto, final EditText tv_producto, final EditText tv_precio, final EditText tv_cantidad, final Button bt_actualizar, final Button bt_eliminar){
        try {
            int tag=0;
            lyv_producto.removeAllViews();
            lyv_cantidad.removeAllViews();
            lyv_precio.removeAllViews();
            DecimalFormat form = new DecimalFormat("0.00");
            final Cursor cursor = this.getWritableDatabase().rawQuery("Select * from Productos where Disponible=1", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    final TextView textView = new TextView(context);
                    textView.setText(cursor.getString(1));
                    textView.setTag(tag);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String sql="Select * from Productos where Producto='"+textView.getText().toString()+"' ";
                            getProducto(sql, tv_codProducto,tv_producto,tv_precio,tv_cantidad, bt_actualizar,bt_eliminar);
                            //Toast.makeText(context,""+textView.getText().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    lyv_producto.addView(textView);


                    TextView textView2 = new TextView(context);
                    textView2.setText(cursor.getString(3));
                    lyv_cantidad.addView(textView2);


                    TextView textView3 = new TextView(context);
                    textView3.setText("S/. "+form.format(cursor.getDouble(2)));
                    lyv_precio.addView(textView3);
                    tag++;
                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            TextView textView = new TextView(context);
            textView.setText(e.getMessage());
            lyv_producto.addView(textView);
        }
    }
    public  void  getProducto(String sql, TextView tv_codProducto, EditText tv_producto, EditText tv_precio, EditText tv_cantidad, Button bt_actualizar, Button bt_eliminar){
        bt_actualizar.setEnabled(true);
        bt_eliminar.setEnabled(true);
        Cursor cursor= this.getWritableDatabase().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                tv_codProducto.setText(cursor.getString(0));
                tv_producto.setText(cursor.getString(1));
                tv_precio.setText(cursor.getString(2));
                tv_cantidad.setText(cursor.getString(3));
            }
        }
    }
    public Cursor buscar_productos(String producto) {
        try {
            return this.getWritableDatabase().rawQuery("Select * from Productos where Producto='" + producto + "' ", null);
        } catch (SQLiteException e) {
            return null;
        }
    }

    public Cursor list_all_productos() {

        try {
            return this.getWritableDatabase().rawQuery("Select * from Productos where Disponible=1 order by Producto asc", null);
        } catch (SQLiteException e) {
            return null;
        }
    }

    //--------------------------------------------Ventas---------------------------------------------------

    public boolean insert_ventas(Double Total) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("CodVenta", getMaxCodVenta() + 1);
            contentValues.put("Total", Total);
            getWritableDatabase().insertOrThrow("Ventas", "", contentValues);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public Integer getMaxCodVenta() {
        Integer maxCod = 0;
        try {
            Cursor cursor = this.getWritableDatabase().rawQuery("SELECT max(CodVenta) FROM Ventas", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    maxCod = cursor.getInt(0);
                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            maxCod = 0;
        }
        return maxCod;
    }


    public void list_all_ventas(final Context context, LinearLayout lyv_codigo, LinearLayout lyv_fecha, LinearLayout lyv_hora, LinearLayout lyv_subtotal, TextView tv_Total, TextView tv_Fecha_Busqueda, Integer Dia_Mes_Año
    , final LinearLayout lyv_detalle, final LinearLayout lyv_ticket, final LinearLayout lyv_producto, final LinearLayout lyv_cantidad, final LinearLayout lyv_totaldetalle) {
        try {

            lyv_ticket.setVisibility(View.VISIBLE);
            lyv_detalle.setVisibility(View.GONE);

            Double total = 0.00;
            lyv_codigo.removeAllViews();
            lyv_fecha.removeAllViews();
            lyv_hora.removeAllViews();
            lyv_subtotal.removeAllViews();
            DecimalFormat form = new DecimalFormat("0.00");

            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            String sql = "Select * from Ventas";
            if (Dia_Mes_Año == 0) {
                sql = "Select * from Ventas where date(Fecha)=date('" + tv_Fecha_Busqueda.getText() + "')";
            } else if (Dia_Mes_Año == 1) {
                sql = "Select * from Ventas where strftime('%Y-%m',Fecha)=strftime('%Y-%m','" + tv_Fecha_Busqueda.getText() + "')";
            } else if (Dia_Mes_Año == 2) {
                sql = "Select * from Ventas where strftime('%Y',Fecha)=strftime('%Y','" + tv_Fecha_Busqueda.getText() + "')";
            }


            Cursor cursor = this.getWritableDatabase().rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Date date = dateFormat.parse(cursor.getString(2), new ParsePosition(0));
                    String Fecha = sdfFecha.format(date);
                    String Hora = sdfHora.format(date);



                    final TextView tv_codVenta = new TextView(context);
                    tv_codVenta.setText(cursor.getString(1));
                    tv_codVenta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            list_all_detalle(tv_codVenta.getText().toString(), context, lyv_detalle,  lyv_ticket,  lyv_producto,  lyv_cantidad,  lyv_totaldetalle);
                            //Toast.makeText(context,""+tv_codVenta.getText().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    lyv_codigo.addView(tv_codVenta);


                    TextView tv_Fecha = new TextView(context);
                    tv_Fecha.setText(Fecha);
                    lyv_fecha.addView(tv_Fecha);

                    TextView tv_Hora = new TextView(context);
                    tv_Hora.setText(Hora);
                    lyv_hora.addView(tv_Hora);


                    TextView tv_SubTotal = new TextView(context);
                    tv_SubTotal.setText("S/. "+form.format(cursor.getDouble(3)));
                    lyv_subtotal.addView(tv_SubTotal);
                    total += cursor.getDouble(3);
                }
            }else{
                lyv_codigo.removeAllViews();
                lyv_fecha.removeAllViews();
                lyv_hora.removeAllViews();
                lyv_subtotal.removeAllViews();
                tv_Total.setText("");
            }
            cursor.close();
            tv_Total.setText("S/. " + form.format(total).toString());
        } catch (SQLiteException e) {
            TextView textView = new TextView(context);
            textView.setText(e.getMessage());
            lyv_codigo.addView(textView);
        }
    }

    public void list_all_ventas(final Context context, LinearLayout lyv_codigo, LinearLayout lyv_fecha, LinearLayout lyv_hora, LinearLayout lyv_subtotal, TextView tv_Total, TextView Fecha_Inicial, TextView Fecha_Final
            , final LinearLayout lyv_detalle, final LinearLayout lyv_ticket, final LinearLayout lyv_producto, final LinearLayout lyv_cantidad, final LinearLayout lyv_totaldetalle) {
        try {

            lyv_ticket.setVisibility(View.VISIBLE);
            lyv_detalle.setVisibility(View.GONE);

            Double total = 0.00;
            lyv_codigo.removeAllViews();
            lyv_fecha.removeAllViews();
            lyv_hora.removeAllViews();
            lyv_subtotal.removeAllViews();
            DecimalFormat form = new DecimalFormat("0.00");

            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Cursor cursor = this.getWritableDatabase().rawQuery("Select * from Ventas where date(Fecha) BETWEEN  date('" + Fecha_Inicial.getText().toString() + "') and date('" + Fecha_Final.getText().toString() + "')", null);


            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Date date = dateFormat.parse(cursor.getString(2), new ParsePosition(0));
                    String Fecha = sdfFecha.format(date);
                    String Hora = sdfHora.format(date);



                    final TextView tv_codVenta = new TextView(context);
                    tv_codVenta.setText(cursor.getString(1));
                    tv_codVenta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            list_all_detalle(tv_codVenta.getText().toString(), context, lyv_detalle,  lyv_ticket,  lyv_producto,  lyv_cantidad,  lyv_totaldetalle);
                            //getDetalle(context,tv_codVenta.getText().toString());
                            //Toast.makeText(context,""+tv_codVenta.getText().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    lyv_codigo.addView(tv_codVenta);


                    TextView tv_Fecha = new TextView(context);
                    tv_Fecha.setText(Fecha);
                    lyv_fecha.addView(tv_Fecha);

                    TextView tv_Hora = new TextView(context);
                    tv_Hora.setText(Hora);
                    lyv_hora.addView(tv_Hora);


                    TextView tv_SubTotal = new TextView(context);
                    tv_SubTotal.setText("S/. "+form.format(cursor.getDouble(3)));
                    lyv_subtotal.addView(tv_SubTotal);
                    total += cursor.getDouble(3);
                }
            }else{
                lyv_codigo.removeAllViews();
                lyv_fecha.removeAllViews();
                lyv_hora.removeAllViews();
                lyv_subtotal.removeAllViews();
                tv_Total.setText("");
            }
            cursor.close();
            tv_Total.setText("S/. " + form.format(total).toString());
        } catch (SQLiteException e) {
            TextView textView = new TextView(context);
            textView.setText(e.getMessage());
            lyv_codigo.addView(textView);
        }
    }



    public void list_all_ventas(TextView tv_codVenta, TextView tv_Fecha, TextView tv_Hora, TextView tv_SubTotal, TextView tv_Total) {
        Double total = 0.00;
        DecimalFormat form = new DecimalFormat("0.00");
        try {
            tv_codVenta.setText("");
            tv_Fecha.setText("");
            tv_Hora.setText("");
            tv_SubTotal.setText("");

            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Cursor cursor = this.getWritableDatabase().rawQuery("Select * from Ventas where Fecha>=date('now','localtime','start of day')", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Date date = dateFormat.parse(cursor.getString(2), new ParsePosition(0));
                    String Fecha = sdfFecha.format(date);
                    String Hora = sdfHora.format(date);
                    tv_codVenta.append(cursor.getString(1) + "\n");
                    tv_Fecha.append(Fecha + "\n");
                    tv_Hora.append(Hora + "\n");
                    tv_SubTotal.append("S/. " + form.format(cursor.getDouble(3)) + "\n");
                    total += cursor.getDouble(3);
                }
            } else {
                tv_codVenta.setText("");
                tv_Fecha.setText("");
                tv_Hora.setText("");
                tv_SubTotal.setText("");
            }
            cursor.close();
            tv_Total.setText("S/. " + form.format(total).toString());
        } catch (SQLiteException e) {
            tv_codVenta.setText(e.getMessage());
        }
    }

    public void list_all_ventas(TextView tv_codVenta, TextView tv_Fecha, TextView tv_Hora, TextView tv_SubTotal, TextView tv_Total, TextView tv_Fecha_Busqueda, Integer Dia_Mes_Año) {
        Double total = 0.00;
        DecimalFormat form = new DecimalFormat("0.00");
        try {
            tv_codVenta.setText("");
            tv_Fecha.setText("");
            tv_Hora.setText("");
            tv_SubTotal.setText("");

            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = sql = "Select * from Ventas";
            if (Dia_Mes_Año == 0) {
                sql = "Select * from Ventas where date(Fecha)=date('" + tv_Fecha_Busqueda.getText() + "')";
            } else if (Dia_Mes_Año == 1) {
                sql = "Select * from Ventas where strftime('%Y-%m',Fecha)=strftime('%Y-%m','" + tv_Fecha_Busqueda.getText() + "')";
            } else if (Dia_Mes_Año == 2) {
                sql = "Select * from Ventas where strftime('%Y',Fecha)=strftime('%Y','" + tv_Fecha_Busqueda.getText() + "')";
            }
            Cursor cursor = this.getWritableDatabase().rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Date date = dateFormat.parse(cursor.getString(2), new ParsePosition(0));
                    String Fecha = sdfFecha.format(date);
                    String Hora = sdfHora.format(date);
                    tv_codVenta.append(cursor.getString(1) + "\n");
                    tv_Fecha.append(Fecha + "\n");
                    tv_Hora.append(Hora + "\n");
                    tv_SubTotal.append("S/. " + form.format(cursor.getDouble(3)) + "\n");
                    total += cursor.getDouble(3);
                }
            } else {
                tv_codVenta.setText("");
                tv_Fecha.setText("");
                tv_Hora.setText("");
                tv_SubTotal.setText("");
            }
            cursor.close();
            tv_Total.setText("S/. " + form.format(total).toString());
        } catch (SQLiteException e) {
            tv_codVenta.setText(e.getMessage());
        }
    }

    public void list_all_ventas(TextView tv_codVenta, TextView tv_Fecha, TextView tv_Hora, TextView tv_SubTotal, TextView tv_Total, TextView Fecha_Inicial, TextView Fecha_Final) {
        Double total = 0.00;
        DecimalFormat form = new DecimalFormat("0.00");
        try {
            tv_codVenta.setText("");
            tv_Fecha.setText("");
            tv_Hora.setText("");
            tv_SubTotal.setText("");

            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Cursor cursor = this.getWritableDatabase().rawQuery("Select * from Ventas where date(Fecha) BETWEEN  date('" + Fecha_Inicial.getText().toString() + "') and date('" + Fecha_Final.getText().toString() + "')", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Date date = dateFormat.parse(cursor.getString(2), new ParsePosition(0));
                    String Fecha = sdfFecha.format(date);
                    String Hora = sdfHora.format(date);
                    tv_codVenta.append(cursor.getString(1) + "\n");
                    tv_Fecha.append(Fecha + "\n");
                    tv_Hora.append(Hora + "\n");
                    tv_SubTotal.append("S/. " + form.format(cursor.getDouble(3)) + "\n");
                    total += cursor.getDouble(3);
                }
            } else {
                tv_codVenta.setText("");
                tv_Fecha.setText("");
                tv_Hora.setText("");
                tv_SubTotal.setText("");
            }
            cursor.close();
            tv_Total.setText("S/. " + form.format(total).toString());
        } catch (SQLiteException e) {
            tv_codVenta.setText(e.getMessage());
        }
    }

    //--------------------------------------------Detalle de Ventas---------------------------------------------------

    public boolean insert_detalle_ventas_array(ArrayList<E_DetalleVentas> detalleVentas) {

        try {
            Double Importe=0.00;
            for(int i=0;i<detalleVentas.size();i++){
                E_DetalleVentas e_detalleVentas=new E_DetalleVentas();
                e_detalleVentas=detalleVentas.get(i);
                insertar_detalle_ventas(e_detalleVentas);
                Importe+= e_detalleVentas.getSubTotal();
                reducir_stock(e_detalleVentas.getProducto(),e_detalleVentas.getCantidad());
            }
            insert_ventas(Importe);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public void insertar_detalle_ventas(E_DetalleVentas e_detalleVentas){
        getWritableDatabase().insert(E_DetalleVentas.Table_Name,null,generarContentValues(e_detalleVentas));
    }
    private ContentValues generarContentValues(E_DetalleVentas e_detalleVentas){
        ContentValues contentValues=new ContentValues();
        contentValues.put(E_DetalleVentas.Field_CodVenta,e_detalleVentas.getCodVenta() );
        contentValues.put(E_DetalleVentas.Field_Producto,e_detalleVentas.getProducto() );
        contentValues.put(E_DetalleVentas.Field_Cantidad,e_detalleVentas.getCantidad() );
        contentValues.put(E_DetalleVentas.Field_SubTotal,e_detalleVentas.getSubTotal() );
        return contentValues;
    }
    public void getDetalle(Context context, String CodVenta){
        Integer contador=0;
        try{
            Cursor cursor = this.getWritableDatabase().rawQuery("Select * from DetalleVentas where CodVenta="+CodVenta+"", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contador++;
                    Toast.makeText(context,"Mensaje: "+contador+" - "+cursor.getString(2),Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context,"Fallido: "+contador,Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }catch (SQLiteException e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void list_all_detalle(String CodVenta, Context context, LinearLayout lyv_detalle,  LinearLayout lyv_ticket, LinearLayout lyv_producto,
                                 LinearLayout lyv_cantidad, LinearLayout lyv_totaldetalle){
        try {
            lyv_ticket.setVisibility(View.GONE);
            lyv_detalle.setVisibility(View.VISIBLE);
            Double total = 0.00;
            lyv_producto.removeAllViews();
            lyv_cantidad.removeAllViews();
            lyv_totaldetalle.removeAllViews();
            DecimalFormat form = new DecimalFormat("0.00");



            String sql = "Select * from DetalleVentas where CodVenta="+CodVenta;


            Cursor cursor = this.getWritableDatabase().rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    TextView textView1= new TextView(context);
                    textView1.setText(cursor.getString(2));
                    lyv_producto.addView(textView1);


                    TextView textView2= new TextView(context);
                    textView2.setText(cursor.getString(3));
                    lyv_cantidad.addView(textView2);


                    TextView textView3= new TextView(context);
                    textView3.setText("S/. " + form.format(cursor.getDouble(4)));
                    lyv_totaldetalle.addView(textView3);



                }
            }else{
                lyv_producto.removeAllViews();
                lyv_cantidad.removeAllViews();
                lyv_totaldetalle.removeAllViews();
            }
            cursor.close();
        } catch (SQLiteException e) {
            TextView textView = new TextView(context);
            textView.setText(e.getMessage());
            lyv_producto.addView(textView);
        }
    }

    public void list_all_detalle(Context context, LinearLayout lyv_detalle,  LinearLayout lyv_ticket, LinearLayout lyv_producto,
                                 LinearLayout lyv_cantidad, LinearLayout lyv_totaldetalle){
        try {
            lyv_ticket.setVisibility(View.GONE);
            lyv_detalle.setVisibility(View.VISIBLE);
            Double total = 0.00;
            lyv_producto.removeAllViews();
            lyv_cantidad.removeAllViews();
            lyv_totaldetalle.removeAllViews();
            DecimalFormat form = new DecimalFormat("0.00");



            String sql = "Select * from DetalleVentas group by Producto order by Producto";


            Cursor cursor = this.getWritableDatabase().rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    TextView textView1= new TextView(context);
                    textView1.setText(cursor.getString(2));
                    lyv_producto.addView(textView1);


                    TextView textView2= new TextView(context);
                    textView2.setText(cursor.getString(3));
                    lyv_cantidad.addView(textView2);


                    TextView textView3= new TextView(context);
                    textView3.setText("S/. " + form.format(cursor.getDouble(4)));
                    lyv_totaldetalle.addView(textView3);



                }
            }else{
                lyv_producto.removeAllViews();
                lyv_cantidad.removeAllViews();
                lyv_totaldetalle.removeAllViews();
            }
            cursor.close();
        } catch (SQLiteException e) {
            TextView textView = new TextView(context);
            textView.setText(e.getMessage());
            lyv_producto.addView(textView);
        }
    }

    public void list_all_detalle(Context context, LinearLayout lyv_detalle,  LinearLayout lyv_ticket, LinearLayout lyv_producto,
                                 LinearLayout lyv_cantidad, LinearLayout lyv_totaldetalle, TextView tv_Fecha_Busqueda, Integer Dia_Mes_Año, TextView tv_Total){
        try {
            lyv_ticket.setVisibility(View.GONE);
            lyv_detalle.setVisibility(View.VISIBLE);
            Double total = 0.00;
            lyv_producto.removeAllViews();
            lyv_cantidad.removeAllViews();
            lyv_totaldetalle.removeAllViews();
            DecimalFormat form = new DecimalFormat("0.00");

            String sql = sql = "Select DetalleVentas.ID, DetalleVentas.CodVenta, DetalleVentas.Producto, sum(DetalleVentas.Cantidad), sum(DetalleVentas.SubTotal)" +
                    " from Ventas, DetalleVentas where DetalleVentas.CodVenta=Ventas.CodVenta and ";
            if (Dia_Mes_Año == 0) {
                sql += " date(Fecha)=date('" + tv_Fecha_Busqueda.getText() + "')";
            } else if (Dia_Mes_Año == 1) {
                sql += " strftime('%Y-%m',Fecha)=strftime('%Y-%m','" + tv_Fecha_Busqueda.getText() + "')";
            } else if (Dia_Mes_Año == 2) {
                sql += " strftime('%Y',Fecha)=strftime('%Y','" + tv_Fecha_Busqueda.getText() + "')";
            }

             sql += " group by DetalleVentas.Producto order by DetalleVentas.SubTotal desc";


            Cursor cursor = this.getWritableDatabase().rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    TextView textView1= new TextView(context);
                    textView1.setText(cursor.getString(2));
                    lyv_producto.addView(textView1);


                    TextView textView2= new TextView(context);
                    textView2.setText(cursor.getString(3));
                    lyv_cantidad.addView(textView2);


                    TextView textView3= new TextView(context);
                    textView3.setText("S/. " + form.format(cursor.getDouble(4)));
                    lyv_totaldetalle.addView(textView3);


                    total+=cursor.getDouble(4);

                }
            }else{
                lyv_producto.removeAllViews();
                lyv_cantidad.removeAllViews();
                lyv_totaldetalle.removeAllViews();
            }
            cursor.close();
            tv_Total.setText("S/. " + form.format(total).toString());
        } catch (SQLiteException e) {
            TextView textView = new TextView(context);
            textView.setText(e.getMessage());
            lyv_producto.addView(textView);
        }
    }



    public void list_all_detalle(Context context, LinearLayout lyv_detalle,  LinearLayout lyv_ticket, LinearLayout lyv_producto,
                                 LinearLayout lyv_cantidad, LinearLayout lyv_totaldetalle, TextView Fecha_Inicial, TextView Fecha_Final, TextView tv_Total){
        try {
            lyv_ticket.setVisibility(View.GONE);
            lyv_detalle.setVisibility(View.VISIBLE);
            Double total = 0.00;
            lyv_producto.removeAllViews();
            lyv_cantidad.removeAllViews();
            lyv_totaldetalle.removeAllViews();
            DecimalFormat form = new DecimalFormat("0.00");


            String sql="Select DetalleVentas.ID, DetalleVentas.CodVenta, DetalleVentas.Producto, sum(DetalleVentas.Cantidad), sum(DetalleVentas.SubTotal)" +
                    " from Ventas, DetalleVentas where DetalleVentas.CodVenta=Ventas.CodVenta and date(Fecha) BETWEEN  date('" + Fecha_Inicial.getText().toString() + "') and date('" + Fecha_Final.getText().toString() + "')" +
                    " group by DetalleVentas.Producto order by DetalleVentas.SubTotal desc";
            //String sql = "Select * from DetalleVentas group by Producto order by Producto";


            Cursor cursor = this.getWritableDatabase().rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    TextView textView1= new TextView(context);
                    textView1.setText(cursor.getString(2));
                    lyv_producto.addView(textView1);


                    TextView textView2= new TextView(context);
                    textView2.setText(cursor.getString(3));
                    lyv_cantidad.addView(textView2);


                    TextView textView3= new TextView(context);
                    textView3.setText("S/. " + form.format(cursor.getDouble(4)));
                    lyv_totaldetalle.addView(textView3);

                    total+=cursor.getDouble(4);

                }
            }else{
                lyv_producto.removeAllViews();
                lyv_cantidad.removeAllViews();
                lyv_totaldetalle.removeAllViews();
            }
            cursor.close();
            tv_Total.setText("S/. " + form.format(total).toString());
        } catch (SQLiteException e) {
            TextView textView = new TextView(context);
            textView.setText(e.getMessage());
            lyv_producto.addView(textView);
        }
    }
}