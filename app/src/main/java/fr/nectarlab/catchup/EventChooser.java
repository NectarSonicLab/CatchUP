package fr.nectarlab.catchup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class EventChooser extends AppCompatActivity {
    private final String TAG = "EventChooser";
    private RadioGroup mRadioGroup;
    private String choice;

    @Override
    public void onCreate (Bundle b){
        super.onCreate(b);
        Log.i(TAG, "OnCreate: Debut");
        setContentView(R.layout.event_chooser);
        mRadioGroup = findViewById(R.id.eventChooser_main_RG);
        mRadioGroup.getCheckedRadioButtonId();
    }
    /**
     * Bouton pour sauvegarder lequel des RB est choisi et le renvoyer a l'activite appelante
     */

    public void onRadioButtonClicked(View v){
        switch (v.getId()){
            case R.id.ec_resto_rb:
                RadioButton restoButton = findViewById(R.id.ec_resto_rb);
                String resto = (String) restoButton.getText();
                Log.i(TAG, "onRadioButtonClicked: "+resto);
                setChoice(resto);
                break;

            case R.id.ec_expo_rb:
                RadioButton expoButton = findViewById(R.id.ec_expo_rb);
                String expo = (String) expoButton.getText();
                Log.i(TAG, "onRadioButtonClicked: "+expo);
                setChoice(expo);
                break;

            case R.id.ec_apero_rb:
                RadioButton aperoButton = findViewById(R.id.ec_apero_rb);
                String apero = (String) aperoButton.getText();
                Log.i(TAG, "onRadioButtonClicked: "+apero);
                setChoice(apero);
                break;

            case R.id.ec_diner_rb:
                RadioButton dinerButton = findViewById(R.id.ec_diner_rb);
                String diner = (String) dinerButton.getText();
                Log.i(TAG, "onRadioButtonClicked: "+diner);
                setChoice(diner);
                break;

            case R.id.ec_sport_rb:
                RadioButton sportButton = findViewById(R.id.ec_sport_rb);
                String sport = (String) sportButton.getText();
                Log.i(TAG, "onRadioButtonClicked: "+sport);
                setChoice(sport);
                break;

            case R.id.ec_cinema_rb:
                RadioButton cinemaButton = findViewById(R.id.ec_cinema_rb);
                String cinema = (String) cinemaButton.getText();
                Log.i(TAG, "onRadioButtonClicked: "+cinema);
                setChoice(cinema);
                break;

            case R.id.ec_concert_rb:
                RadioButton concertButton = findViewById(R.id.ec_concert_rb);
                String concert = (String) concertButton.getText();
                Log.i(TAG, "onRadioButtonClicked: "+concert);
                setChoice(concert);
                break;

            case R.id.ec_surprise_rb:
                RadioButton surpriseButton = findViewById(R.id.ec_surprise_rb);
                String surprise = (String) surpriseButton.getText();
                Log.i(TAG, "onRadioButtonClicked: "+surprise);
                setChoice(surprise);
                break;
        }
    }
    private void setChoice(String event){
        Log.i(TAG, "setChoice: "+event);
        this.choice = event;
    }

    private String getChoice (){
        return this.choice;
    }

    public void saveChoice (View v){
        String savedChoice = getChoice();
        Intent i = new Intent();
        i.putExtra("eventChoosen", savedChoice);
        setResult(RESULT_OK, i);
        finish();
    }

    public void cancelChoice(View v){
        finish();
    }
}
