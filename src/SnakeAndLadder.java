import java.util.ArrayList;
import java.util.Scanner;

public class SnakeAndLadder {
    //states
    private ArrayList<Player> players;
    private ArrayList<Snake> snakes;
    private ArrayList<Ladder> ladders;
    private int boardSize;
    //0 = the game isnt started yet
    //1 = the game is started
    //2 = the game is over
    private int status;
    private int playerInTurn;

    public SnakeAndLadder(int boardSize) {
        this.boardSize = boardSize;
        this.players = new ArrayList<Player>();
        this.snakes = new ArrayList<Snake>();
        this.ladders = new ArrayList<Ladder>();
        this.status = 0;
    }

    public void initiateGame() {
        //set the ladders
        int[][] ladders = {
                {2, 23},
                {8, 34},
                {20, 77},
                {32, 68},
                {41, 79},
                {74, 88},
                {82, 100},
                {85, 95}
        };
        addLadders(scalePositions(ladders));
        //set the snakes
        int[][] snakes = {
                {29, 9},
                {38, 15},
                {47, 5},
                {53, 33},
                {62, 37},
                {86, 54},
                {92, 70},
                {97, 25}
        };
        addSnakes(scalePositions(snakes));
    }

    private int[][] scalePositions(int[][] positions) {
        double scale = boardSize / 100.0;
        int[][] scaledPositions = new int[positions.length][2];
        for (int i = 0; i < positions.length; i++) {
            scaledPositions[i][0] = Math.min((int) (positions[i][0] * scale), boardSize);
            scaledPositions[i][1] = Math.min((int) (positions[i][1] * scale), boardSize);
        }
        return scaledPositions;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPlayerInTurn(int p) {
        this.playerInTurn = p;
    }

    public void addPlayer(Player p) {
        this.players.add(p);
    }

    public void addSnake(Snake s) {
        this.snakes.add(s);
    }

    public void addSnakes(int[][] s) {
        for (int i = 0; i < s.length; i++) {
            Snake snake = new Snake(s[i][0], s[i][1]);
            this.snakes.add(snake);
        }
    }

    public void addLadder(Ladder l) {
        this.ladders.add(l);
    }

    public void addLadders(int[][] l) {
        for (int m = 0; m < l.length; m++) {
            Ladder ladder = new Ladder(l[m][0], l[m][1]);
            this.ladders.add(ladder);
        }
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getStatus() {
        return status;
    }

    public int getPlayerInTurn() {
        return playerInTurn;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Snake> getSnakes() {
        return snakes;
    }

    public ArrayList<Ladder> getLadders() {
        return ladders;
    }

    public int getTurn() {
        if (this.status == 0) {
            return 0; // Start with the first player initially
        } else {
            // Rotate turns among players
            return (playerInTurn + 1) % players.size();
        }
    }

    public void movePlayer(Player p, int x) {
        p.moveAround(x, this.boardSize);

        //checking the ladder
        for (int i = 0; i < this.ladders.size(); i++) {
            Ladder l = this.ladders.get(i);
            if (p.getPosition() == l.getFromPosition()) {
                p.setPosition(l.getToPosition());
                System.out.println(p.getUserName() + " got ladder from " + l.getFromPosition() + " climb to " + l.getToPosition());
            }
        }

        //checking the snake
        for (int i = 0; i < this.snakes.size(); i++) {
            Snake s = this.snakes.get(i);
            if (p.getPosition() == s.getFromPosition()) {
                p.setPosition(s.getToPosition());
                System.out.println(p.getUserName() + " got snake from " + s.getFromPosition() + " slide down to " + s.getToPosition());
            }
        }

        // Check if two players are in the same position
        checkAndHandleSamePosition();

        System.out.println(p.getUserName() + " new position is " + p.getPosition());
        if (p.getPosition() == this.boardSize) {
            this.status = 2;
            System.out.println("The winner is: " + p.getUserName());
        }
    }

    private void checkAndHandleSamePosition() {
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                if (players.get(i).getPosition() == players.get(j).getPosition()) {
                    players.get(i).setPosition(players.get(i).getPosition() - 1);
                    System.out.println(players.get(i).getUserName() + " was pushed back to " + players.get(i).getPosition() + " due to same position with " + players.get(j).getUserName());
                }
            }
        }
    }

    public void play() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the number of players (2-5):");
        int numPlayers = sc.nextInt();
        sc.nextLine();

        while (numPlayers < 2 || numPlayers > 5) {
            System.out.println("Invalid number of players. Please enter a number between 2 and 5:");
            numPlayers = sc.nextInt();
            sc.nextLine();
        }

        for (int i = 1; i <= numPlayers; i++) {
            System.out.println("Enter player " + i + " name:");
            String playerName = sc.nextLine();
            Player player = new Player(playerName);
            addPlayer(player);
        }

        initiateGame();

        setStatus(1);

        do {
            //Determine the first turn
            int t1 = getTurn();
            setPlayerInTurn(t1);

            Player playerInTurn = getPlayers().get(t1);
            System.out.println("---------------------------------");
            System.out.println("Player in turn is " + playerInTurn.getUserName());

            boolean bonusTurn;
            do {
                bonusTurn = false;
                //player in turn roll dice
                System.out.println(playerInTurn.getUserName() + " it's your turn, please press enter to roll dice");
                String input = sc.nextLine();
                int x = 0;
                if (input.isEmpty()) {
                    x = playerInTurn.rollDice();
                }

                System.out.println("Dice number: " + x);
                movePlayer(playerInTurn, x);

                // Check for bonus roll
                if (x == 6) {
                    bonusTurn = true;
                    System.out.println(playerInTurn.getUserName() + " rolled a 6 and gets a bonus turn!");
                }
            } while (bonusTurn && getStatus() != 2);

        } while (getStatus() != 2);
    }
}
