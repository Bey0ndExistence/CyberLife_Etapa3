package Game;

import Entities.Player;
import Entities.ScoreSubject;
import Utils.GameException;
import gamestates.*;
import gamestates.Menu;
import levels.LevelManager;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * This is a Java class that represents a game and handles the game loop, updating game objects, and
 * rendering graphics.
 */

public class Game implements Runnable {
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Menu menu;
    private  Playing playing;
    private  Playing2 playing2;
    private  Playing3 playing3;

    private Thread gameThread;

    private final int FPS_SET =144;

    private long lastCheck=0;
    private final int UPS_SET = 200;

    private Player player;

    ScoreSubject subject;

    public Game() throws GameException, IOException, SQLException {
        initClasses();
        gamePanel = new GamePanel(this);
        this.gameWindow= new GameWindow(this.gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        System.out.println("Game running");
        startGameLoop();
    }

    private void initClasses() throws IOException, GameException, SQLException {
        menu = new Menu(this);
        subject = new ScoreSubject();
        player = new Player(1000,200,240,240);
        playing = new Playing(this,player,menu,subject);
        playing2 = new Playing2(this,player,menu,subject);
        playing3 = new Playing3(this,player,menu,subject);
        
    }

    private void startGameLoop(){
        this.gameThread= new Thread(this);
        gameThread.start();
    }

    public void update() {

        switch(Gamestate.state) {
            case MENU:
                menu.update();
                break;
            case PLAYING:
                playing.update();
                break;
            case PLAYING2:
                playing2.update();
                break;
            case PLAYING3:
                playing3.update();
                break;
            case OPTIONS:
                System.exit(0);
                break;
            case QUIT:
                System.exit(0);
                break;
            default:
                break;
        }
    }

    public void render(Graphics g, Graphics2D bg){
        switch(Gamestate.state) {
            case MENU:
                menu.draw(g,bg);
                break;
            case PLAYING:
                playing.draw(g,bg);
                break;
            case PLAYING2:
                playing2.draw(g,bg);
                break;
            case PLAYING3:
                playing3.draw(g,bg);
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {

            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            if(deltaF >= 1){
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                //System.out.println("FPS: " + frames + "| UPS: " + updates);
                frames = 0;
                updates =0;

            }
        }
    }

    public void resetGame() throws SQLException, IOException, GameException {
        player = new Player(1000,200,240,240);
        subject.removeObserver(playing.getEnemyManager());
        subject.removeObserver(playing2.getEnemyManager());
        subject.removeObserver(playing3.getEnemyManager());
        playing =  null;
        playing2 = null;
        playing3=  null;

        playing = new Playing(this,player,menu,subject);
        playing2 = new Playing2(this,player,menu,subject);
        playing3 = new Playing3(this,player,menu,subject);

    }
    public void windowsFocusLost(){
        if(Gamestate.state == Gamestate.PLAYING);

    }
    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }

    public Playing2 getPlaying2(){
        return  playing2;
    }
    public Playing3 getPlaying3(){
        return  playing3;
    }



}
