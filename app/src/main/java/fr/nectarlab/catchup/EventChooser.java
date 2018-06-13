package fr.nectarlab.catchup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * EventChooser
 * Activite repondant a un Intent envoye par EventSetup.java
 * Doit renvoyer le type d'evenement choisi (Resto, Expo, Concert...)
 */

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
     * methode pour recuperer l'info selon le bouton coche
     * @param v
     *
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

    /**
     * Setter pour le champ "choice"
     * @param event le texte correspondant au RadioButton choisi
     */
    private void setChoice(String event){
        Log.i(TAG, "setChoice: "+event);
        this.choice = event;
    }

    /**
     * Getter pour le champ "choice"
     * @return le champ "choice"
     */
    private String getChoice (){
        return this.choice;
    }



    /**
     *  Retour a l'activite appelante avec le choix final
     * @param v La View correspondant au bouton "Sauvegarder"
     */
    public void saveChoice (View v){
        String savedChoice = getChoice();
        Intent i = new Intent();
        i.putExtra("eventChoosen", savedChoice);
        setResult(RESULT_OK, i);
        finish();
    }


    /**
     * L'utilisateur annule son choix
     * @param v La View correspondant au bouton "Annuler"
     */
    public void cancelChoice(View v){
        finish();
    }
}
