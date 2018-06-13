package fr.nectarlab.catchup.model;

/**
 * Modele de classe pour l'utilisateur
 */

public class Users {
    private String EMAIL;
    private String USERNAME;

    public Users (){}

    public Users (String Email, String Username){
        this.EMAIL = Email;
        this.USERNAME = Username;
    }

    public String getEMAIL() {

        return this.EMAIL;
    }

    public String getUSERNAME() {
        return this.USERNAME;
    }
}
