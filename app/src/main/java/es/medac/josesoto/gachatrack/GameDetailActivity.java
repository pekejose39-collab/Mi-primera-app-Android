package es.medac.josesoto.gachatrack;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * Pantalla 2: ficha del juego y calculadora de pity.
 * Todos los datos salen de res/values/arrays.xml usando la posición recibida.
 */
public class GameDetailActivity extends AppCompatActivity {

    public static final String EXTRA_GAME_INDEX = "es.medac.josesoto.gachatrack.GAME_INDEX";
    private static final String TAG = "GachaTrack-Detail";

    // Claves para guardar el resultado al girar el móvil
    private static final String KEY_RESULT = "result";
    private static final String KEY_RESULT_COST = "result_cost";
    private static final String KEY_WORST = "worst";

    private int hardPity;
    private int pullCost;
    private String moneda;

    private EditText etPity, etCurrency;
    private CheckBox cbGuaranteed;
    private TextView tvResult, tvResultCost, tvWorstCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_game_detail);

        int indice = getIntent().getIntExtra(EXTRA_GAME_INDEX, 0);
        cargarDatosDelJuego(indice);

        etPity = findViewById(R.id.et_pity);
        etCurrency = findViewById(R.id.et_currency);
        cbGuaranteed = findViewById(R.id.cb_guaranteed);
        tvResult = findViewById(R.id.tv_result);
        tvResultCost = findViewById(R.id.tv_result_cost);
        tvWorstCase = findViewById(R.id.tv_worst_case);
        Button btnCalcular = findViewById(R.id.btn_calculate);
        btnCalcular.setOnClickListener(v -> calcular());

        // Si venimos de un giro de pantalla, recuperamos el resultado
        // (los EditText y el CheckBox ya guardan su estado solos porque tienen id)
        if (savedInstanceState != null) {
            tvResult.setText(savedInstanceState.getCharSequence(KEY_RESULT));
            tvResultCost.setText(savedInstanceState.getCharSequence(KEY_RESULT_COST));
            tvWorstCase.setText(savedInstanceState.getCharSequence(KEY_WORST));
        }
    }

    private void cargarDatosDelJuego(int i) {
        Resources res = getResources();
        String nombre = res.getStringArray(R.array.game_names)[i];
        moneda = res.getStringArray(R.array.game_currencies)[i];
        String rate5 = res.getStringArray(R.array.game_rate_5)[i];
        String rate4 = res.getStringArray(R.array.game_rate_4)[i];
        int softPity = res.getIntArray(R.array.game_soft_pity)[i];
        hardPity = res.getIntArray(R.array.game_hard_pity)[i];
        pullCost = res.getInteger(R.integer.pull_cost);

        TypedArray colores = res.obtainTypedArray(R.array.game_colors);
        int color = colores.getColor(i, ContextCompat.getColor(this, R.color.brand_primary));
        colores.recycle();

        LinearLayout header = findViewById(R.id.header_detail);
        header.setBackgroundTintList(ColorStateList.valueOf(color));
        getWindow().setStatusBarColor(color);

        ((TextView) findViewById(R.id.tv_game_name)).setText(nombre);
        ((TextView) findViewById(R.id.tv_game_description))
                .setText(res.getStringArray(R.array.game_descriptions)[i]);
        ((TextView) findViewById(R.id.tv_badge_5))
                .setText(getString(R.string.badge_rate, getString(R.string.badge_5star), rate5));
        ((TextView) findViewById(R.id.tv_badge_4))
                .setText(getString(R.string.badge_rate, getString(R.string.badge_4star), rate4));
        ((TextView) findViewById(R.id.tv_rate_5)).setText(rate5);
        ((TextView) findViewById(R.id.tv_rate_4)).setText(rate4);
        ((TextView) findViewById(R.id.tv_soft_pity)).setText(getString(R.string.value_pull_number, softPity));
        ((TextView) findViewById(R.id.tv_hard_pity)).setText(getString(R.string.value_pull_number, hardPity));
        ((TextView) findViewById(R.id.tv_cost)).setText(getString(R.string.value_cost, pullCost, moneda));

        setTitle(nombre);
    }

    /** CU-02: calcula tiradas y moneda hasta el 5★ asegurado. */
    private void calcular() {
        String textoPity = etPity.getText().toString().trim();
        int maxPity = hardPity - 1;

        // Flujo alternativo 3a: valor vacío o fuera de rango
        if (TextUtils.isEmpty(textoPity) || Integer.parseInt(textoPity) > maxPity) {
            etPity.setError(getString(R.string.error_pity_range, maxPity));
            tvResult.setText(R.string.result_placeholder);
            tvResultCost.setText(null);
            tvWorstCase.setText(null);
            return;
        }

        int pity = Integer.parseInt(textoPity);
        String textoMoneda = etCurrency.getText().toString().trim();
        int monedaActual = TextUtils.isEmpty(textoMoneda) ? 0 : Integer.parseInt(textoMoneda);

        int tiradas = hardPity - pity;
        int coste = tiradas * pullCost;
        int falta = Math.max(0, coste - monedaActual);

        tvResult.setText(getResources().getQuantityString(R.plurals.result_pulls_left, tiradas, tiradas));
        tvResult.setTextColor(ContextCompat.getColor(this, R.color.brand_primary));

        if (falta == 0) {
            tvResultCost.setText(getString(R.string.result_cost_ok, coste, moneda));
            tvResultCost.setTextColor(ContextCompat.getColor(this, R.color.text_success));
        } else {
            tvResultCost.setText(getString(R.string.result_cost, coste, moneda, falta));
            tvResultCost.setTextColor(ContextCompat.getColor(this, R.color.text_error));
        }

        // Sin garantía, el 5★ puede no ser el del banner: peor caso = otro pity completo
        if (cbGuaranteed.isChecked()) {
            tvWorstCase.setText(null);
        } else {
            tvWorstCase.setText(getString(R.string.result_worst_case, tiradas + hardPity));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putCharSequence(KEY_RESULT, tvResult.getText());
        outState.putCharSequence(KEY_RESULT_COST, tvResultCost.getText());
        outState.putCharSequence(KEY_WORST, tvWorstCase.getText());
    }

    // ----- Ciclo de vida (Parte A.3) -----
    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}
