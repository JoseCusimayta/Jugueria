package yiwo.jugueria;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Inventario extends AppCompatActivity {

    //region DefaultCode
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
    private View mControlsView;
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


    //endregion


    Button bt_reg, bt_elim, bt_act, bt_cancelar;
    EditText et_precio, et_producto, et_stock;
    TextView tv_codProducto;
    DB_Controller controller;
    String producto;
    Double precio;
    Integer stock;
    LinearLayout lyv_producto,lyv_cantidad,lyv_precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventario);

        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        bt_reg = findViewById(R.id.bt_reg);
        bt_act = findViewById(R.id.bt_act);
        bt_cancelar = findViewById(R.id.bt_cancelar);
        bt_elim = findViewById(R.id.bt_elim);
        et_precio = findViewById(R.id.et_precio);
        et_producto = findViewById(R.id.et_producto);
        et_stock = findViewById(R.id.et_stock);
        tv_codProducto=findViewById(R.id.tv_codProducto);
        controller = new DB_Controller(this);

        lyv_producto=findViewById(R.id.lyv_producto);
        lyv_cantidad=findViewById(R.id.lyv_cantidad);
        lyv_precio=findViewById(R.id.lyv_precio);
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        RellenarGridView();
        //////////////////////////////////////////////////////
    }

    //////////////////////////////////////////////////////
    public void btn_clic(View view) {
        switch (view.getId()) {
            case R.id.bt_reg:
                Toast.makeText(this, Registrar(), Toast.LENGTH_SHORT).show();
                RellenarGridView();
                bt_act.setEnabled(false);
                break;
            case R.id.bt_act:
                Toast.makeText(this, Actualizar(), Toast.LENGTH_SHORT).show();
                RellenarGridView();
                break;
            case R.id.bt_elim:


                new AlertDialog.Builder(Inventario.this)
                        .setTitle("Eliminar")
                        .setMessage("¿Esstá seguro de eliminar este producto?")
                        .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                // do the acknowledged action, beware, this is run on UI thread


                                Toast.makeText(Inventario.this, Eliminar(), Toast.LENGTH_SHORT).show();
                                RellenarGridView();
                                bt_act.setEnabled(false);
                                bt_elim.setEnabled(false);
                            }
                        })
                        .create()
                        .show();

                break;
            case R.id.bt_cancelar:
                et_precio.setText("");
                et_stock.setText("");
                et_producto.setText("");
                et_producto.requestFocus();
                bt_act.setEnabled(false);
                bt_elim.setEnabled(false);
                break;
            case R.id.b_atras:
                finish();
        }
    }
    //////////////////////////////////////////////////////

    //////////////////////////////////////////////////////
    public Boolean ProductoVacio() {
        if (!et_producto.getText().toString().trim().isEmpty()) {
            setProducto(et_producto.getText().toString());
            try {
                setPrecio(et_precio.getText().toString());
                setStock(et_stock.getText().toString());
            } catch (Exception e) {
                setPrecio("0.00");
                setStock("0");
            }
            return true;
        } else
            return false;
    }

    public Boolean CamposVacios() {
        if (!et_producto.getText().toString().trim().isEmpty() &&
                !et_precio.getText().toString().trim().isEmpty() &&
                !et_stock.getText().toString().trim().isEmpty()) {
            setProducto(et_producto.getText().toString());
            setPrecio(et_precio.getText().toString());
            setStock(et_stock.getText().toString());
            return true;
        } else
            return false;
    }

    public String Registrar() {
        if (ProductoVacio()) {
            if (controller.insert_productos(getProducto(), getPrecio(), getStock())) {
                bt_act.setEnabled(false);
                bt_elim.setEnabled(false);
                return getProducto() + " agregado";
            } else {
                return "No se pudo agregar el producto: " + getProducto();
            }
        } else {
            return "No se puede dejar campos vacíos";
        }
    }

    public String Actualizar(){
        if (ProductoVacio()) {
            if (controller.update_productos(tv_codProducto.getText().toString(), getProducto(), getPrecio(), getStock())) {
                bt_act.setText("Actualizar");
                bt_act.setEnabled(false);
                bt_elim.setEnabled(false);
                return getProducto() + " actualizado";
            } else {
                return "No se pudo actualizar el producto: " + getProducto();
            }
        } else {
            return "No se puede dejar campos vacíos";
        }
    }
    public String Eliminar(){
        if (ProductoVacio()) {
            if (controller.baja_producto(tv_codProducto.getText().toString())) {
                bt_act.setEnabled(false);
                bt_elim.setEnabled(false);
                return getProducto() + " eliminado";
            } else {
                return "No se pudo eliminar el producto: " + getProducto();
            }
        } else {
            return "No se puede dejar campos vacíos";
        }
    }
    public void CambiarNombre() {
        if (ProductoVacio()) {
            try {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Inventario.this);
                dialog.setTitle("Ingrese nuevo nombre del producto");
                final EditText nuevoProducto = new EditText(this);
                dialog.setView(nuevoProducto);
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String value = nuevoProducto.getText().toString().trim();
                        if (!value.isEmpty()) {
                            if (controller.cambiar_producto(producto, value)) {
                                Toast.makeText(Inventario.this, "Producto: " + producto + " cambiado a " + value, Toast.LENGTH_SHORT).show();
                                et_producto.setText(value);
                            } else {
                                Toast.makeText(Inventario.this, "No se pudo cambiar de nombre", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Inventario.this, "El campo está vacío", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();

            } catch (SQLiteException e) {
                Toast.makeText(this, "No se pudo cambiar el nombre del producto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se debe dejar campos vacíos", Toast.LENGTH_SHORT).show();
        }
    }

    public void RellenarGridView(){
        controller.list_all_productos(this,lyv_producto,lyv_cantidad,lyv_precio,tv_codProducto,et_producto,et_precio,et_stock, bt_act, bt_elim);
    }
    //////////////////////////////////////////////////////

    //////////////////////////////////////////////////////

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = Double.parseDouble(precio);
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = Integer.parseInt(stock);
    }

    //////////////////////////////////////////////////////

    //////////////////////////////////////////////////////
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

    @Override
    protected void onResume() {
        RellenarGridView();
        super.onResume();
    }
}