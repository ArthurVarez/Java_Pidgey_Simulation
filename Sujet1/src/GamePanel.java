import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {

    private final Board Board;
    private final List<Pigeon> Pigeons;
    private static final int PanelSize = Constants.DisplayerSize-300;
    private static final int GAME_UNITS = Constants.BoardSize;
    private static final int UNIT_SIZE = PanelSize/GAME_UNITS ;
    private static final int DELAY = 10;
    private static boolean Running = false;
    private PausableExecutor executor;

    GamePanel() throws InterruptedException {

        this.Board = CreateInitializeBoard();
        this.Pigeons=CreatePigeon(Board);
        System.out.println(Pigeons.size());
        JPanel panel = new JPanel();
        this.setPreferredSize(new Dimension(Constants.DisplayerSize,Constants.DisplayerSize));
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLUE);
        JButton add_nourriture = new JButton("Add Nourriture");
        JButton vaccum = new JButton("vaccum");
        add_nourriture.addActionListener(e->Board.AddNourriture());
        add_nourriture.setFocusable(false);
        vaccum.addActionListener(e-> Board.DeleteUnfreshedNourriture());
        vaccum.setFocusable(false);
        panel.add(add_nourriture);
        panel.add(vaccum);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.SOUTH);
        StartGame();
    }

    private void StartGame()
    {
        Running = true;
        Timer timer = new Timer(DELAY, this);
        timer.start();
        executor = new PausableExecutor();
        Pigeons.forEach(executor::execute);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g)
    {
        executor.pause();

        for(int i=0; i<=GAME_UNITS;i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE,PanelSize );
            g.drawLine(0, i * UNIT_SIZE,PanelSize,i * UNIT_SIZE );
        }

        DrawNourriture(g);

        g.setColor(new Color(100, 0, 100, 50));
        Pigeons.forEach(p->g.fillOval(
                (p.X)*(PanelSize/GAME_UNITS)+UNIT_SIZE/2,
                (p.Y)*(PanelSize/GAME_UNITS)+UNIT_SIZE/2,UNIT_SIZE/2,UNIT_SIZE/2));

        g.setColor(Color.BLACK);
        int count = PanelSize/Pigeons.size();
        for(Pigeon pigeon : Pigeons)
        {
            g.drawString( pigeon.toString(),PanelSize,count);
            count+= PanelSize/Pigeons.size();
        }
        executor.resume();
    }

    private void DrawNourriture(Graphics g)
    {
        for(List<Case> row : Board.Board){
            for(Case c : row)
                for(Nourriture n : c.Nourritures)
                {
                    if (n.Fraiche) {
                        g.setColor(new Color(0, 255, 0, 50));
                    } else {
                        g.setColor(new Color(255, 0, 0, 50));
                    }
                    g.fill3DRect(
                            (c.X)*(PanelSize/GAME_UNITS)+UNIT_SIZE/4,
                            (c.Y)*(PanelSize/GAME_UNITS)+UNIT_SIZE/4,UNIT_SIZE/2,UNIT_SIZE/2,false);
                }
        }

    }
    private static List<Pigeon> CreatePigeon(Board board)
    {
        List<Pigeon> res = new ArrayList<>();
        for(int i=0;i<Constants.NombrePigeon;i++)
        {
            Random generator = new Random();
            int k = generator.nextInt(Constants.BoardSize);
            int v = generator.nextInt(Constants.BoardSize);
            res.add(new Pigeon(i,k,v,board));
        }
        return  res;
    }

    private static Board CreateInitializeBoard() throws InterruptedException {
        Board board = new Board(Constants.BoardSize);
        board.AddNourriture(Constants.InitialNourriture);
        return board;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(Running)
        {
            repaint();
        }
    }

}

