package yiwo.jugueria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.opengl.Visibility;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class vistaImpresion extends AppCompatActivity {

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    Spinner sp_item;
    Button b_inv, b_hist;
    TextView tv_unidad,tv_total,tv_resto2;
    TextView tv_codVenta, tv_producto, tv_cantidad, tv_importe,tv_totalFinal;
    TextView tv_imporFactura;
    LinearLayout gridView_Balance, gridView_Factura;
    LinearLayout lyv_producto, lyv_cantidad, lyv_subtotal,lyv_eliminar;
    EditText et_cantidad;
    DB_Controller controller;
    ArrayList<E_DetalleVentas> eDetalleVentasList=new ArrayList<E_DetalleVentas>();
    int indice=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vista_impresion);
        //////////////////////////////////////////////////////

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        sp_item=  findViewById(R.id.sp_item);
        b_inv=findViewById(R.id.b_inv);
        b_hist=findViewById(R.id.b_hist);
        tv_unidad=findViewById(R.id.tv_unidad2);
        tv_total=findViewById(R.id.tv_total2);
        et_cantidad=findViewById(R.id.et_cantidad);
        tv_resto2=findViewById(R.id.tv_resto2);
        tv_codVenta=findViewById(R.id.tv_codVenta);
        tv_producto=findViewById(R.id.tv_prod);
        tv_cantidad=findViewById(R.id.tv_cant);
        tv_importe=findViewById(R.id.tv_importe);
        tv_totalFinal=findViewById(R.id.tv_totalFinal);
        lyv_producto=findViewById(R.id.lyv_producto);
        lyv_cantidad=findViewById(R.id.lyv_cantidad);
        lyv_subtotal=findViewById(R.id.lyv_subtotal);
        lyv_eliminar=findViewById(R.id.lyv_eliminar);
        tv_imporFactura=findViewById(R.id.tv_imporFactura);
        gridView_Balance=findViewById(R.id.gridView_Balance);
        gridView_Factura=findViewById(R.id.gridView_Factura);
        controller= new DB_Controller(this);
        //////////////////////////////////////////////////////

        controller.list_all_ventas( tv_codVenta,  tv_producto, tv_cantidad, tv_importe, tv_totalFinal);


        /////////////////////// BOTONES ///////////////////////////////
        // Set up the user interaction to manually show or hide the system UI.
        b_inv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vistaImpresion.this, Inventario.class);
                startActivity(i);
            }
        });
        b_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vistaImpresion.this, Historial.class);
                startActivity(i);
            }
        });
        //////////////////////////////////////////////////////

        //////////////////////// SPINNER //////////////////////////////
        RellenarSpinner();

        sp_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    Cursor cursor = controller.buscar_productos(selectedItemText);
                    if(null != cursor){
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                //texto+=cursor.getString(1) + " " + cursor.getDouble(2) + " " + cursor.getInt(3) + "\n";
                                Integer resto=cursor.getInt(3);
                                tv_unidad.setText(String.valueOf(cursor.getDouble(2)));

                                if(eDetalleVentasList.size()>0){
                                    for(int i=0;i<eDetalleVentasList.size();i++){
                                        E_DetalleVentas e_detalleVentas=new E_DetalleVentas();
                                        e_detalleVentas=eDetalleVentasList.get(i);
                                        if(sp_item.getSelectedItem().toString()==e_detalleVentas.getProducto()){
                                            resto-=e_detalleVentas.getCantidad();
                                            if(resto<0){
                                                resto=0;
                                            }
                                        }
                                    }

                                }
                                tv_resto2.setText(String.valueOf(resto));
                                if(!et_cantidad.getText().toString().trim().isEmpty())
                                {
                                    ObtenerTotal();
                                }
                            }
                        }
                        /*
                        // Notify the selected item text
                        Toast.makeText
                                (getApplicationContext(), "Selected : " + texto +" -- "+selectedItemText, Toast.LENGTH_SHORT)
                                .show();
                        */
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //////////////////////////////////////////////////////

        //////////////////////// Código ejecutado mientras se escribe //////////////////////////////
        et_cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()!=0){
                    ObtenerTotal();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //////////////////////////////////////////////////////
    }
    /////////////////////////////////////Boton imprimir///////////////////////////////////////////////
    public void Imprimir(View view) {
        if (!et_cantidad.getText().toString().trim().isEmpty() && sp_item.getSelectedItemId() != 0) {
            Integer cantidad = Integer.parseInt(et_cantidad.getText().toString());
            Integer stock = Integer.parseInt(tv_resto2.getText().toString());
            String producto = sp_item.getSelectedItem().toString();
            if (cantidad <= stock) {
                AgregarDetalle();
                if (controller.insert_detalle_ventas_array(eDetalleVentasList)) {
                    Toast.makeText(this, "Imprimiendo...", Toast.LENGTH_SHORT).show();
                    controller.list_all_ventas(tv_codVenta, tv_producto, tv_cantidad, tv_importe, tv_totalFinal);
                    eDetalleVentasList.clear();
                } else {
                    Toast.makeText(this, "No se pudo registrar la venta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No hay suficientes productos", Toast.LENGTH_SHORT).show();
            }
        }else if(eDetalleVentasList.size()>0){
            if (controller.insert_detalle_ventas_array(eDetalleVentasList)) {
                Toast.makeText(this, "Imprimiendo...", Toast.LENGTH_SHORT).show();
                controller.list_all_ventas(tv_codVenta, tv_producto, tv_cantidad, tv_importe, tv_totalFinal);
                et_cantidad.setText("");
                eDetalleVentasList.clear();
            } else {
                Toast.makeText(this, "No se pudo registrar la venta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Agregar(View view){

        if (!et_cantidad.getText().toString().trim().isEmpty() && sp_item.getSelectedItemId()!=0) {
            Integer cantidad = Integer.parseInt(et_cantidad.getText().toString());
            Integer stock = Integer.parseInt(tv_resto2.getText().toString());
            if (cantidad <= stock) {
                AgregarDetalle();
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////Funciones/////////////////////////////////////////////////////////////////
    public  void ObtenerTotal(){
        Double precio=Double.parseDouble(tv_unidad.getText().toString());
        Double cantidad=Double.parseDouble(et_cantidad.getText().toString());
        Double Total=precio*cantidad;
        DecimalFormat precision = new DecimalFormat("0.00");
        tv_total.setText(precision.format(Total));
    }
    public void AgregarDetalle() {
        Integer CodigoVentas = controller.getMaxCodVenta() + 1;
        String Producto = sp_item.getSelectedItem().toString();
        Integer Cantidad = Integer.parseInt(et_cantidad.getText().toString());
        Double Total = Double.parseDouble(tv_total.getText().toString());
        if (eDetalleVentasList.size() > 0) {
            if(existe()){
                E_DetalleVentas e_detalleVentas = new E_DetalleVentas();
                e_detalleVentas = eDetalleVentasList.get(getIndice());
                Cantidad += e_detalleVentas.getCantidad();
                Total += e_detalleVentas.getSubTotal();
                eDetalleVentasList.set(getIndice(), new E_DetalleVentas(CodigoVentas, Producto, Cantidad, Total));
            }else{
                eDetalleVentasList.add(new E_DetalleVentas(CodigoVentas, Producto, Cantidad, Total));
            }
        } else {
            eDetalleVentasList.add(new E_DetalleVentas(CodigoVentas, Producto, Cantidad, Total));
        }
        et_cantidad.setText("1");
        tv_resto2.setText("0");
        tv_unidad.setText("0");
        tv_total.setText("0");
        sp_item.setSelection(0);


        RellenarGridView();
    }
    public void RellenarGridView(){
         int eliminar_indice=0;
        Double Importe=0.00;
        DecimalFormat form = new DecimalFormat("0.00");
        lyv_producto.removeAllViews();
        lyv_cantidad.removeAllViews();
        lyv_subtotal.removeAllViews();
        lyv_eliminar.removeAllViews();
        for(int i=0;i<eDetalleVentasList.size();i++){
            E_DetalleVentas e_detalleVentas=new E_DetalleVentas();
            e_detalleVentas=eDetalleVentasList.get(i);
            String prdoucto=e_detalleVentas.getProducto();
            Integer cantidad=e_detalleVentas.getCantidad();
            String subTotal="S/. "+form.format(e_detalleVentas.getSubTotal());
            Importe+=e_detalleVentas.getSubTotal();

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(2, 2, 2, 2); // llp.setMargins(left, top, right, bottom);


            TextView textView1 = new TextView(this);
            textView1.setText(prdoucto);
            textView1.setTextSize(12);
            textView1.setMaxHeight(40);
            textView1.setLayoutParams(llp);
            lyv_producto.addView(textView1);


            TextView textView2 = new TextView(this);
            textView2.setText(cantidad.toString());
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setLayoutParams(llp);
            lyv_cantidad.addView(textView2);

            TextView textView3 = new TextView(this);
            textView3.setText(subTotal);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setLayoutParams(llp);
            lyv_subtotal.addView(textView3);


            final TextView textView4 = new TextView(this);
            textView4.setText("Eliminar");
            textView4.setBackgroundColor(Color.GRAY);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setLayoutParams(llp);
            textView4.setHint(String.valueOf(eliminar_indice));
            final int finalEliminar_indice = eliminar_indice;
            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eDetalleVentasList.remove(finalEliminar_indice);
                    ActualizarGridView();
                    //Toast.makeText(vistaImpresion.this, "Tag: "+textView4.getHint(), Toast.LENGTH_SHORT).show();
                }
            });
            lyv_eliminar.addView(textView4);
            eliminar_indice++;
        }
        tv_imporFactura.setText("S/. "+form.format(Importe));

    }



    public void ActualizarGridView(){
        int eliminar_indice=0;
        Double Importe=0.00;
        DecimalFormat form = new DecimalFormat("0.00");
        lyv_producto.removeAllViews();
        lyv_cantidad.removeAllViews();
        lyv_subtotal.removeAllViews();
        lyv_eliminar.removeAllViews();
        for(int i=0;i<eDetalleVentasList.size();i++){
            E_DetalleVentas e_detalleVentas=new E_DetalleVentas();
            e_detalleVentas=eDetalleVentasList.get(i);
            String prdoucto=e_detalleVentas.getProducto();
            Integer cantidad=e_detalleVentas.getCantidad();
            String subTotal="S/. "+form.format(e_detalleVentas.getSubTotal());
            Importe+=e_detalleVentas.getSubTotal();

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(2, 2, 2, 2); // llp.setMargins(left, top, right, bottom);


            TextView textView1 = new TextView(this);
            textView1.setText(prdoucto);
            textView1.setTextSize(12);
            textView1.setMaxHeight(40);
            textView1.setLayoutParams(llp);
            lyv_producto.addView(textView1);


            TextView textView2 = new TextView(this);
            textView2.setText(cantidad.toString());
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setLayoutParams(llp);
            lyv_cantidad.addView(textView2);

            TextView textView3 = new TextView(this);
            textView3.setText(subTotal);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setLayoutParams(llp);
            lyv_subtotal.addView(textView3);


            final TextView textView4 = new TextView(this);
            textView4.setText("Eliminar");
            textView4.setBackgroundColor(Color.GRAY);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setLayoutParams(llp);
            textView4.setHint(String.valueOf(eliminar_indice));
            final int finalEliminar_indice = eliminar_indice;
            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eDetalleVentasList.remove(finalEliminar_indice);
                    RellenarGridView();
                    Toast.makeText(vistaImpresion.this, "Tag: "+textView4.getHint(), Toast.LENGTH_SHORT).show();
                }
            });
            lyv_eliminar.addView(textView4);
            eliminar_indice++;
        }
        tv_imporFactura.setText("S/. "+form.format(Importe));

    }


    public boolean existe(){
        E_DetalleVentas e_detalleVentas = new E_DetalleVentas();
        for (int i = 0; i < eDetalleVentasList.size(); i++) {
            e_detalleVentas = eDetalleVentasList.get(i);
            if (sp_item.getSelectedItem().toString() == e_detalleVentas.getProducto()) {
                setIndice(i);
                return  true;
            }
        }
        return  false;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }


    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void RellenarSpinner() {
        try {
            List<String> values = new ArrayList<String>();
            Cursor cursor = controller.list_all_productos();
            values.add("Seleccione un producto...");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    values.add(cursor.getString(1));
                }
            } else {
                values.add("No hay productos");
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, values) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == 0) {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.WHITE);
                    }
                    return view;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_item.setAdapter(dataAdapter);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        RellenarSpinner();
        eDetalleVentasList.clear();
        //gridView_Balance.setVisibility(View.VISIBLE);
        //gridView_Factura.setVisibility(View.GONE);
        super.onResume();
    }
}
