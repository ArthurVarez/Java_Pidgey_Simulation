import java.util.*;
import java.util.concurrent.locks.*;

public class Case {
    public int X;
    public  int Y;
    public List<Nourriture> Nourritures = new ArrayList<>();
    //liste de nourriture dans la case
    private final Lock Locker = new ReentrantLock();

    public Case(int x,int y)
    {
        this.X = x;
        this.Y=y;
    }


    @Override
    public String toString() {
        return "Case{" +
                "X=" + X +
                ", Y=" + Y +
                ", Nourritures=" + Nourritures.size() +
                '}';
    }

    public String toStringNourriture(){
        return "Nourriture : " + Nourritures.size() ;
    }

    public void DeleteNotFreshedNourriture()
    {
        for (int i = 0; i < Nourritures.size(); i++) {
            if (!Nourritures.get(i).Fraiche)
            {
                Locker.lock();
                try {
                    Nourritures.remove(i);
                }
                catch (Exception exception) {
                    exception.getStackTrace();
                }
                Locker.unlock();
            }
        }
    }

    public boolean DeleteNourriture(Nourriture n )
    {
        boolean eaten = false;
        try {
            Locker.lock();
            if (Objects.isNull(n) || Nourritures.isEmpty()) {
                return false;
            }
            Nourritures.remove(n);
            eaten = true;
            Locker.unlock();
        }
        catch (Exception e) {
            e.getStackTrace();
        }
        return eaten;
    }

    public void AddNourriture(){
        Locker.lock();
        Nourritures.add(new Nourriture(Constants.ValidityTime));
        Locker.unlock();

    }

    public boolean HasNourriture() { return !Nourritures.isEmpty();}

    public Nourriture GetFresherNourriture()
    {
        Nourriture res = null;
        if(Nourritures.isEmpty()) {
            return null;
        }
        try {
            res =  Nourritures.stream().min(Comparator.comparingInt(n -> n.PeremptionTimer)).orElse(null) ;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
