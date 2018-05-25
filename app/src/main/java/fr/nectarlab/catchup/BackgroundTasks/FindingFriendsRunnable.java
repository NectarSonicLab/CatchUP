package fr.nectarlab.catchup.BackgroundTasks;


import android.os.Process;

/**
 * Non implante
 * Created by ThomasPiaczinski on 03/04/18.
 */

public class FindingFriendsRunnable implements Runnable {
    //Utiliser un Handler pour pouvoir communiquer avec l'UI Thread
    //ex (dans un PoolManager) mHandler = new Handler (Looper.getMainLooper())

    //peut avoir un constructeur
    //FindingFriendsRunnable (La classe qui effectue la tache);

    //Definir les champs ici
    Thread mThread = new Thread();


    @Override
    public void run(){
    /*
    * Definir le code à executer ici
     */
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }

    public void send(){
        FindingFriendsRunnable runnable = new FindingFriendsRunnable();
        Thread test = new Thread (runnable);
        test.start();
    }
}
