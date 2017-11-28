package yiwo.jugueria;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Historial extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //region Codigo de pantalla completa

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


    TextView tv_fecha, tv_final;
    TextView  tv_cTotal,tv_cTotaldetalle;
    RadioButton rb_fecha,rb_mes, rb_año, rb_dia;
    Button bt_atras;
    LinearLayout lyv_codigo,lyv_fecha,lyv_hora,lyv_subtotal;
    LinearLayout lyv_detalle, lyv_ticket;
    LinearLayout lyv_producto, lyv_cantidad, lyv_totaldetalle;
    int dia,mes,año;
    DB_Controller controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historial);

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
        tv_fecha = findViewById(R.id.tv_fecha);
        tv_final = findViewById(R.id.tv_final);
        rb_fecha=findViewById(R.id.rb_fecha);
        rb_año=findViewById(R.id.rb_año);
        rb_mes=findViewById(R.id.rb_mes);
        rb_dia=findViewById(R.id.rb_dia);
        tv_cTotal=findViewById(R.id.tv_cTotal);
        tv_cTotaldetalle=findViewById(R.id.tv_cTotaldetalle);
        lyv_codigo = findViewById(R.id.lyv_codigo);
        lyv_fecha=findViewById(R.id.lyv_fecha);
        lyv_hora=findViewById(R.id.lyv_hora);
        lyv_subtotal=findViewById(R.id.lyv_subtotal);
        bt_atras=findViewById(R.id.bt_atras);

        lyv_detalle=findViewById(R.id.lyv_detalle);
        lyv_ticket=findViewById(R.id.lyv_ticket);
        lyv_producto=findViewById(R.id.lyv_producto);
        lyv_cantidad=findViewById(R.id.lyv_cantidad);
        lyv_totaldetalle=findViewById(R.id.lyv_totaldetalle);
        controller= new DB_Controller(this);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        tv_fecha.setText(dateFormat.format(date));
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        tv_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c= Calendar.getInstance();
                año=c.get(Calendar.YEAR);
                mes=c.get(Calendar.MONTH);
                dia=c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(Historial.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String _mes;
                        String _dia;
                        if(String.valueOf(i1+1).length()==1){
                            _mes="0"+String.valueOf(i1+1);
                        }else{
                            _mes=String.valueOf(i1+1);
                        }
                        if(String.valueOf(i2).length()==1){
                            _dia="0"+String.valueOf(i2);
                        }else{
                            _dia=String.valueOf(i2);
                        }
                        String fecha = String.valueOf(i) +"-"+_mes+"-"+_dia;
                        tv_fecha.setText(fecha);
                    }
                },año, mes, dia);
                datePickerDialog.show();

            }
        });
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        tv_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c= Calendar.getInstance();
                año=c.get(Calendar.YEAR);
                mes=c.get(Calendar.MONTH);
                dia=c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(Historial.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String _mes;
                        String _dia;
                        if(String.valueOf(i1+1).length()==1){
                            _mes="0"+String.valueOf(i1+1);
                        }else{
                            _mes=String.valueOf(i1+1);
                        }
                        if(String.valueOf(i2).length()==1){
                            _dia="0"+String.valueOf(i2);
                        }else{
                            _dia=String.valueOf(i2);
                        }
                        String fecha = String.valueOf(i) +"-"+_mes+"-"+_dia;
                        tv_final.setText(fecha);
                    }
                },año, mes, dia);
                datePickerDialog.show();

            }
        });
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        rb_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tv_final.isEnabled()){
                    tv_final.setEnabled(true);
                }
            }
        });
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        rb_dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_final.isEnabled()){
                    tv_final.setEnabled(false);
                }
            }
        });
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        rb_mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_final.isEnabled()){
                    tv_final.setEnabled(false);
                }
            }
        });
        //////////////////////////////////////////////////////

        //////////////////////////////////////////////////////
        rb_año.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_final.isEnabled()){
                    tv_final.setEnabled(false);
                }
            }
        });
        //////////////////////////////////////////////////////

        bt_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void buscar(View view) {
        if (rb_dia.isChecked()) {
            controller.list_all_ventas(this, lyv_codigo,lyv_fecha,lyv_hora,lyv_subtotal,tv_cTotal,tv_fecha,0
            ,lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle,tv_cTotaldetalle);
            //controller.list_all_ventas(tv_cCodigo, tv_cFecha, tv_cHora, tv_cSubTotal, tv_cTotal, tv_fecha, 0);
            //Falta hacer que una sola venta, tenga varios productos, por lo que falta ahcer el
            // algoritmo para guardar todos los productos y hacer que al momento de imprmir, se guarden todos con un solo Codigo de venta
        } else if (rb_mes.isChecked()) {
            //controller.list_all_ventas(tv_cCodigo, tv_cFecha, tv_cHora, tv_cSubTotal, tv_cTotal, tv_fecha, 1);
            controller.list_all_ventas(this, lyv_codigo,lyv_fecha,lyv_hora,lyv_subtotal,tv_cTotal,tv_fecha,1
                    ,lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle,tv_cTotaldetalle);
        } else if (rb_año.isChecked()) {
            //controller.list_all_ventas(tv_cCodigo, tv_cFecha, tv_cHora, tv_cSubTotal, tv_cTotal, tv_fecha, 2);
            controller.list_all_ventas(this, lyv_codigo,lyv_fecha,lyv_hora,lyv_subtotal,tv_cTotal,tv_fecha,2
                    ,lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle,tv_cTotaldetalle);
        } else if (rb_fecha.isChecked()) {
            controller.list_all_ventas(this, lyv_codigo,lyv_fecha,lyv_hora,lyv_subtotal,tv_cTotal, tv_fecha, tv_final
                    ,lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle,tv_cTotaldetalle);
            //controller.list_all_ventas(tv_cCodigo, tv_cFecha, tv_cHora, tv_cSubTotal, tv_cTotal, tv_fecha, tv_final);
        }
    }

    public  void verProductosFacturas(View view){
        if (rb_dia.isChecked()) {
            controller.list_all_detalle(this, lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle,tv_fecha,0, tv_cTotaldetalle);
        } else if (rb_mes.isChecked()) {
            controller.list_all_detalle(this, lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle,tv_fecha,1, tv_cTotaldetalle);
        } else if (rb_año.isChecked()) {
            controller.list_all_detalle(this, lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle,tv_fecha,2, tv_cTotaldetalle);
        } else if (rb_fecha.isChecked()) {
            controller.list_all_detalle(this, lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle, tv_fecha, tv_final, tv_cTotaldetalle);
        }
        //controller.list_all_detalle(this, lyv_detalle,lyv_ticket,lyv_producto,lyv_cantidad,lyv_totaldetalle);
    }





    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
    }

    //region Codigo FullScreen
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
    //endregion
}
