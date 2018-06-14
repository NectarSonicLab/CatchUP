package fr.nectarlab.catchup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TabHost;

/**
 * DateChooser
 * Activite repondant a un Intent lance par EventSetup.java
 * Elle doit renvoyer sous la forme de String le jour, le mois et l'annee choisie pour l'evenement
 */

public class DateChooser extends AppCompatActivity implements DatePicker.OnDateChangedListener {
    private TabHost mTabHost;
    private DatePicker mDatePicker;
    private int dayOfMonth;
    private int year;
    private int month;
    private final String TAG = "DateChooser";

    @Override
    public void onCreate(Bundle b){
        Log.i(TAG, "onCreate: start");
        super.onCreate(b);
        setContentView(R.layout.date_chooser);

        mDatePicker = findViewById(R.id.datePicker);

        mTabHost = findViewById(R.id.dateChooser_main_TabHost);
        mTabHost.setup();
        TabHost.TabSpec tab1 = mTabHost.newTabSpec(getString(R.string.Tab1));
        TabHost.TabSpec tab2 = mTabHost.newTabSpec(getString(R.string.Tab2));
        tab1.setIndicator(getString(R.string.Tab1));
        tab1.setContent(R.id.tab1);

        tab2.setIndicator(getString(R.string.Tab2));
        tab2.setContent(R.id.tab2);

        mTabHost.addTab(tab1);
        mTabHost.addTab(tab2);
        Log.i(TAG, "onCreate: end");
    }
    @Override
    public void onResume(){
        Log.i(TAG, "onResume: start");
        super.onResume();
        Log.i(TAG, "onResume: end");
    }


    /**
     * Methode de l'interface OnDateChangeListener
     * Rien de particulier ici
     */
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.i(TAG, "onDateChanged()");
        Log.i(TAG, "Year picked: "+year);
        Log.i(TAG, "Month picked: "+monthOfYear);
        Log.i (TAG, "Day picked: "+dayOfMonth);
    }

    /*
     * Retour a l'activite appelante avec la date choisie
     */

    /**
     * Renvoie a EventSetup.java le choix de l'utilisateur
     * @param v La View (bouton "sauvegarder")
     */
    public void update (View v){
        Log.i(TAG, "update()");
        year = mDatePicker.getYear();
        month = mDatePicker.getMonth();
        dayOfMonth = mDatePicker.getDayOfMonth();
        mDatePicker.updateDate(year, month, dayOfMonth);
        onDateChanged(mDatePicker, year, month, dayOfMonth);
        Intent intent = new Intent();
        intent.putExtra("year", year);
        intent.putExtra("month", month+1);
        intent.putExtra("day", dayOfMonth);
        setResult(RESULT_OK, intent);
        finish();
    }
}
