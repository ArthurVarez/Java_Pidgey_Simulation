import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.random.RandomGenerator;

public class Pigeon implements Runnable {
    public int Id;
    public int X;
    public int Y;
    public Board Board;
    private final Lock Locker = new ReentrantLock();
    private final RandomGenerator generator;
    public int FoodCount = 0;

    public Pigeon(int id,int x, int y,Board board )
    {
        this.Id=id;
        this.X = x;
        this.Y=y;
        this.Board = board;
        this.generator = new Random();
    }

    @Override
    public String toString() {
        return "Pigeon {" +
                "Id = "+ Id +
                ", x=" + X +
                ", y=" + Y +
                ", Nourriture Mangé : "+FoodCount+
                '}';
    }

    public Case SearchFood()
    {
         List<Case> cases = Board.Board
        .stream()
        .flatMap(Collection::stream)
        .filter(c->c.Nourritures.size()>0)
        .toList();

         Map<Case,Integer> temp = new HashMap<>();
         try {
             cases.forEach(aCase -> {
                 if (!Objects.isNull(aCase.GetFresherNourriture())) {
                     if (aCase.GetFresherNourriture() != null && aCase.GetFresherNourriture().Fraiche) {
                         temp.put(aCase, aCase.GetFresherNourriture().PeremptionTimer);
                     }
                 }
             });
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }

        return (temp.size()>0) ? temp.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey() : null ;
    }


    @Override
    public void run() {
        while (true) {
            Case target = SearchFood();
            if (target == null) {
                try {
                    System.out.printf("Waiting for %s milliseconds \n", Constants.PigeonWaitingTime);
                    Move();
                    Thread.sleep(Constants.PigeonWaitingTime);
                } catch (Exception e) {
                    System.out.println("Thread interrupted");
                }
            }
            else {
                System.out.printf(Id+", I'm on case %s, %s \n", X, Y);
                System.out.println("La case " + target + " est celle contenant la nourriture la plus fracihe");
                boolean moved = false;
                try {
                    if (target.X != X) {
                        X = (X - target.X) > 0 ? X - 1 : X + 1;
                        moved = true;
                        System.out.printf(Id+", Moving to %s,%s \n", X, Y);
                        Thread.sleep(Constants.PigeonSpeed);
                    }
                    if (target.Y != Y && !moved) {
                        Y = (Y - target.Y) > 0 ? Y - 1 : Y + 1;
                        System.out.printf(Id+", Moving to %s,%s \n", X, Y);
                        Thread.sleep(Constants.PigeonSpeed);
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (target.X == X && target.Y == Y)// on est sur la bonne case
                {
                    if (target.HasNourriture()) {
                        try {
                            Locker.lock();
                            if (!target.Nourritures.isEmpty()) {
                                if(target.Nourritures.get(0).Fraiche) {
                                    boolean eaten = target.DeleteNourriture(target.GetFresherNourriture());
                                    if (eaten) {
                                        FoodCount++;
                                    }
                                    System.out.printf("Id %s mangé la nourriture en %s %s \n", Id, X, Y);
                                }
                                else {
                                    System.out.printf("Id %s : Je ne mange pas la nourriture pas fracihe en %s %s \n", Id, X, Y);
                                }
                            }
                            Locker.unlock();

                        }
                        catch (Exception exception) {
                            System.out.println("Nourriture deja mangée");
                            }
                        }
                    }
                }
            }
        }

        public synchronized void Move()
        {
            double moveProbability = BigDecimal.valueOf(generator.nextDouble())
                                        .setScale(1, RoundingMode.HALF_UP)
                                        .doubleValue();
            if(moveProbability ==1) {
                Locker.lock();
                try {
                    X = generator.nextInt(Constants.BoardSize);
                    Y = generator.nextInt(Constants.BoardSize);
                    System.out.printf("I moved to %s,%s because I've been afraid \n",X,Y);
                }
                catch (Exception ex) {
                    ex.getStackTrace();
                }
                Locker.unlock();
            }
        }
    }

