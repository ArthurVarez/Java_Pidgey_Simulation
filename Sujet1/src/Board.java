import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Board {
    public int Size;
    public List<List<Case>> Board = new ArrayList<>();
    private final Lock Locker = new ReentrantLock();
    // ici on a une plateau de taille NxN

    public Board(int size) {
        this.Size = size;
        //Creation de la grille
        for (int i = 0; i < Size; i++) {
            ArrayList<Case> temp = new ArrayList<>();
            for (int j = 0; j < Size; j++) {
                temp.add(new Case(i, j));
            }
            Board.add(temp);

        }
    }
    public void AddNourriture(int n) throws InterruptedException {
        // rien n'empeche d'avoir deux nourritures sur la meme case
        for (int k = 0; k <n ; k++) {
            AddNourriture();
            Thread.sleep(100);
        }

    }
    public void AddNourriture(){
        Random randomGenerator = new Random();
        int i = randomGenerator.nextInt(Size);
        int j = randomGenerator.nextInt(Size);
        AddNourriture(i,j);
    }

    public void AddNourriture(int i, int j){
        if(Board.stream().flatMap(Collection::stream).anyMatch(c-> c.X==i && c.Y==j))
            try {
                Locker.lock();
                GetCase(i,j).AddNourriture();
                Locker.unlock();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void DeleteUnfreshedNourriture()
    {
        Locker.lock();
        for (List<Case> row: Board) {
            for (Case aCase:row)
            {
                try {
                    aCase.DeleteNotFreshedNourriture();
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        }
        Locker.unlock();

    }

    public Case GetCase(int i, int j)
    {
        return Board
                .stream()
                .flatMap(Collection::stream)
                .filter(c -> c.X==i && c.Y==j)
                .findFirst()
                .orElse(null);
    }

    public void DisplayFood()
    {
        for(int i=0;i<Size;i++)
            for(int j=0;j<Size;j++)
                if(!GetCase(i,j).Nourritures.isEmpty()) {
                    for ( Nourriture n:GetCase(i,j).Nourritures)
                          {
                              System.out.printf("X = %s , Y = %s \n",i,j);
                              System.out.println(n);
                    }
                }
    }

    public boolean IsEmpty()
    {
        boolean isEmpty = true;
        for(List<Case> row : Board)
            for(Case element:row)
                if (element.HasNourriture()) {
                    isEmpty = false;
                    break;
                }
        return isEmpty;
    }


    public void DisplayBoard()
    {
        for(List<Case> partition : Board) {
            for (Case c : partition)
                System.out.print("| " + c.toString() + "|");
            System.out.println("\n");
        }
    }

    public void DisplayBoard(String Nourriture)
    {
        for(List<Case> partition : Board) {
            for (Case c : partition)
                System.out.print("| " + c.toStringNourriture() + "|");
            System.out.println("\n");
        }
    }


}
