import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int TILE_SIZE = 25;
    private final int GRID_WIDTH = 20;
    private final int GRID_HEIGHT = 20;
    private final int SCREEN_WIDTH = GRID_WIDTH * TILE_SIZE;
    private final int SCREEN_HEIGHT = GRID_HEIGHT * TILE_SIZE;
    private final int INITIAL_LENGTH = 5;
    private final int DELAY = 140;

    private int score;
    private ArrayList<Point> snake;
    private Point food;
    private char direction;
    private boolean running;
    private Timer timer;
    private boolean showRules = true;
    private boolean gameOver = false;

    public SnakeGame() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (showRules) {
                    showRules = false;
                    startGame();
                } else if (gameOver) {
                    // Reset the game if the game is over and any key is pressed
                    resetGame();
                } else {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (direction != 'D') direction = 'U';
                            break;
                        case KeyEvent.VK_DOWN:
                            if (direction != 'U') direction = 'D';
                            break;
                        case KeyEvent.VK_LEFT:
                            if (direction != 'R') direction = 'L';
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (direction != 'L') direction = 'R';
                            break;
                    }
                }
            }
        });
    }

    private void resetGame() {
        gameOver = false;
        showRules = false;
        startGame();
    }

    private void startGame() {
        snake = new ArrayList<>();
        for (int i = 0; i < INITIAL_LENGTH; i++) {
            snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2 + i));
        }
        direction = 'U';
        running = true;
        spawnFood();
        timer = new Timer(DELAY, this);
        timer.start();
        score = 0; // Initialize score to 0
    }

    private void spawnFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(GRID_WIDTH);
            y = rand.nextInt(GRID_HEIGHT);
        } while (snake.contains(new Point(x, y)));
        food = new Point(x, y);
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head);
        switch (direction) {
            case 'U':
                newHead.y -= 1;
                break;
            case 'D':
                newHead.y += 1;
                break;
            case 'L':
                newHead.x -= 1;
                break;
            case 'R':
                newHead.x += 1;
                break;
        }
        if (newHead.equals(food)) {
            snake.add(0, newHead);
            spawnFood();
            score++; // Increment the score when the snake eats the food
        } else {
            if (newHead.x < 0 || newHead.x >= GRID_WIDTH || newHead.y < 0 || newHead.y >= GRID_HEIGHT
                    || snake.contains(newHead)) {
                running = false;
                gameOver = true;
                timer.stop();
                return;
            }
            snake.add(0, newHead);
            snake.remove(snake.size() - 1);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showRules) {
            drawRules(g);
        } else if (running) {
            g.setColor(Color.RED);
            g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
        } else if (gameOver) {
            drawGameOver(g);
        }
    }

    private void drawRules(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Snake Game Rules and Controls", 10, 30);
        g.drawString("Use the arrow keys to control the snake's movement.", 10, 60);
        g.drawString("Eat the red food to grow the snake and score points.", 10, 90);
        g.drawString("Avoid hitting the walls or the snake's body.", 10, 120);
        g.drawString("Press any key to start the game.", 10, 150);
    }

    private void drawGameOver(Graphics g) {
        String message = "Game Over";
        Font font = new Font("Helvetica", Font.BOLD, 30);
        g.setColor(Color.WHITE);
        g.setFont(font);
        FontMetrics metrics = getFontMetrics(font);
        int x = (SCREEN_WIDTH - metrics.stringWidth(message)) / 2;
        int y = SCREEN_HEIGHT / 2;
        g.drawString(message, x, y);
        g.drawString("Press any key to restart", x, y + 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame gamePanel = new SnakeGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}