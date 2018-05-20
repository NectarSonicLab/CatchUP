package fr.nectarlab.catchup.ExecutionThreads;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Tentative de creation d'un pool de Thread...
 * Non implante, inutile
 */

public class AppExecutors {
    public Executor getmDiskIO() {
        return mDiskIO;
    }

    public Executor getmNetworkIO() {
        return mNetworkIO;
    }

    public Executor getmMainThread() {
        return mMainThread;
    }

    private final Executor mDiskIO;
    private final Executor mNetworkIO;
    private final Executor mMainThread;

    private AppExecutors (Executor diskIO, Executor networkIO,Executor mainThread){
        this.mDiskIO=diskIO;
        this.mNetworkIO=networkIO;
        this.mMainThread=mainThread;
    }

    public AppExecutors(){
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), new MainThreadExecutor());
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler (Looper.getMainLooper());
        @Override
        public void execute (@NonNull Runnable command){
            mainThreadHandler.post(command);
        }
    }
}
