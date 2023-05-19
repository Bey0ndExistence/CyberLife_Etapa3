package gamestates;



import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import Entities.EnemyManager;
import Entities.Player;
import Entities.ScoreSubject;
import Utils.GameException;
import levels.Camera;
import levels.*;
import Game.Game;

public class Playing3 extends State implements Statemethods {
    private LevelManager3 levelManager3;

    private EnemyManager enemyManager3;
    private Player player;

    private Camera camera;

    private int score;

    private Menu menu;

    private ScoreSubject subject;
    public Playing3(Game game,Player player, Menu menu,ScoreSubject subject) throws IOException, GameException {
        super(game);
        this.player = player;
        this.menu = menu;
        this.subject = subject;
        initClasses();
    }

    private void initClasses() throws IOException, GameException {


        //   player.setSecondGun();
        camera = new Camera(150, 50, 1920, 768, player);
        levelManager3= new LevelManager3(this,camera);
        enemyManager3= new EnemyManager(subject,this,camera,player,"src/enemy_coordonates_lvl3", score);
        subject.addObserver(enemyManager3);
    }

    @Override
    public void update() {

        player.update(levelManager3.getHitBoxes(),enemyManager3.getEnemies());
        levelManager3.update();
        enemyManager3.update(levelManager3.getHitBoxes());

    }

    @Override
    public void draw(Graphics g, Graphics2D bg) {
        levelManager3.drawBG(bg);
        levelManager3.drawTiles(g);
        player.render(g);
        enemyManager3.drawEnemies(g);

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                player.setUp(true);
                break;
            case KeyEvent.VK_A:
                player.setLeft(true);
                player.setGoingLeft();
                break;
            case KeyEvent.VK_S:
                player.setDucking();
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                player.setGoingRight();
                break;
            case KeyEvent.VK_SPACE:
                player.setShooting();
                break;
            case KeyEvent.VK_BACK_SPACE:
                Gamestate.state= Gamestate.MENU;
                menu.setPrevious(Gamestate.PLAYING);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                player.setUp(false);
                break;
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_S:
                // gamePanel.getGame().getPlayer().setNonDucking();
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setNonShooting();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    public EnemyManager getEnemyManager(){
        return enemyManager3;
    }

    public Player getPlayer() {
        return player;
    }

}
