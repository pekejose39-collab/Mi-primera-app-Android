package es.medac.josesoto.gachatrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * Pantalla 1: lista de juegos y resumen del gasto del mes.
 * Su interfaz está en res/layout/activity_main.xml (móvil) y
 * res/layout-sw600dp/activity_main.xml (tablet).
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GachaTrack-Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        // Une esta Activity con su layout XML
        setContentView(R.layout.activity_main);

        mostrarNumeroDeJuegos();
        mostrarGastoDelMes();
        configurarTarjetasDeJuegos();

        Button btnNuevaSesion = findViewById(R.id.btn_new_session);
        btnNuevaSesion.setOnClickListener(v ->
                Toast.makeText(this, R.string.toast_coming_soon, Toast.LENGTH_SHORT).show());
    }

    /** Usa un recurso plurals: "1 juego incluido" / "4 juegos incluidos". */
    private void mostrarNumeroDeJuegos() {
        int numJuegos = getResources().getStringArray(R.array.game_names).length;
        TextView tvCount = findViewById(R.id.tv_games_count);
        tvCount.setText(getResources().getQuantityString(R.plurals.games_count, numJuegos, numJuegos));
    }

    /** Datos de ejemplo guardados en res/values/integers.xml (solo interfaz). */
    private void mostrarGastoDelMes() {
        int gastadoCent = getResources().getInteger(R.integer.demo_spent_cents);
        int limiteCent = getResources().getInteger(R.integer.demo_limit_cents);
        int porcentaje = gastadoCent * 100 / limiteCent;

        String gastado = String.format(Locale.getDefault(), "%.2f", gastadoCent / 100.0);
        String limite = String.format(Locale.getDefault(), "%.2f", limiteCent / 100.0);

        TextView tvAmount = findViewById(R.id.tv_spending_amount);
        TextView tvHint = findViewById(R.id.tv_spending_hint);
        ProgressBar pb = findViewById(R.id.pb_spending);

        tvAmount.setText(getString(R.string.spending_amount, gastado, limite));
        tvHint.setText(getString(R.string.spending_hint, porcentaje));
        pb.setProgress(porcentaje);
    }

    /** Cada tarjeta abre la ficha del juego pasando su posición en los arrays. */
    private void configurarTarjetasDeJuegos() {
        int[] idsTarjetas = {R.id.card_genshin, R.id.card_hsr, R.id.card_wuwa, R.id.card_zzz};
        for (int i = 0; i < idsTarjetas.length; i++) {
            final int indice = i;
            View tarjeta = findViewById(idsTarjetas[i]);
            tarjeta.setOnClickListener(v -> {
                Intent intent = new Intent(this, GameDetailActivity.class);
                intent.putExtra(GameDetailActivity.EXTRA_GAME_INDEX, indice);
                startActivity(intent);
            });
        }
    }

    // ----- Ciclo de vida: se ve en Logcat filtrando por "GachaTrack" (Parte A.3) -----
    @Override protected void onStart()   { super.onStart();   Log.d(TAG, "onStart"); }
    @Override protected void onResume()  { super.onResume();  Log.d(TAG, "onResume"); }
    @Override protected void onPause()   { super.onPause();   Log.d(TAG, "onPause"); }
    @Override protected void onStop()    { super.onStop();    Log.d(TAG, "onStop"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy"); }
}
