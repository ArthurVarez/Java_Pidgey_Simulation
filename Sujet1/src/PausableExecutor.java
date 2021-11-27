import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class PausableExecutor extends ThreadPoolExecutor {
    //custom excutor pour mettre en pause les thread lorsque l'on dessine l'interface graphique
    private boolean isPaused = false;
    private final Lock pauseLock = new ReentrantLock();
    private final Condition unpaused = pauseLock.newCondition();

    public PausableExecutor() {
        super(Constants.NombrePigeon, Constants.NombrePigeon, Integer.MAX_VALUE, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    }


    public void pause() {
        //mettre en pause les thread
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        //relancer les thread
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signal();
        } finally {
            pauseLock.unlock();
        }
    }
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) {
                unpaused.await();
            }
        } catch (InterruptedException e) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }
}