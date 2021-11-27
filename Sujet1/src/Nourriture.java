import java.util.*;
import java.util.TimerTask;
import java.util.concurrent.locks.*;

class Nourriture {
    public int ValidityTime;
    public boolean Fraiche = true;
    private final Lock Locker = new ReentrantLock();
    public int PeremptionTimer = 0;


    public Nourriture(int ValidityTime) {
        this.ValidityTime = ValidityTime;
        Timer timer = new Timer();
        //Ici on incremente le timer de peremption de 1 pour plus tard trier les nourritures par fraicheur
        //Si on depasse la date de validité la nourriture est périmée.
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run() {

                try {
                    Locker.lock();
                    if (PeremptionTimer > ValidityTime) {
                        Fraiche = false;
                    }
                    PeremptionTimer++;
                    Locker.unlock();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },1,1);
    }



    @Override
    public String toString() {
        return "Nourriture{" +
                "validity=" + ValidityTime +
                ", fraiche=" + Fraiche +
                ", PeremptionTimer=" + PeremptionTimer+
                '}';
    }


}









