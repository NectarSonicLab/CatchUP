package fr.nectarlab.catchup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TabHost;

/**
 * Created by ThomasBene on 5/7/2018.
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

    }
    @Override
    public void onResume(){
        super.onResume();

    }


    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.i(TAG, "Year picked: "+year);
        Log.i(TAG, "Month picked: "+monthOfYear);
        Log.i (TAG, "Day picked: "+dayOfMonth);
    }

    public void update (View v){
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
