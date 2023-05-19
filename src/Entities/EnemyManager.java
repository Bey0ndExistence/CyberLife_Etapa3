package Entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.BufferedReader;

import Utils.GameException;
import gamestates.Playing;
import Utils.LoadSave;
import gamestates.Playing2;
import gamestates.Playing3;
import levels.Camera;

import static Utils.Constants.EnemyConstants.*;

public class EnemyManager implements Observer{
    private Playing playing;
    private Playing2 playing2;

    private Playing3 playing3;
    private BufferedImage[][] cop,turret,boss_one,boss_two;
    private BufferedImage portal;
    private ArrayList<Enemy> enemies = new ArrayList<>();

    private Camera camera;

    private Player player;

    private Portal portalLvl2;

    protected int enemiesKilled = 0;
    protected int score  = 0;
    protected int previusSize;

    private ScoreSubject subject;


    private boolean bossScoreUpdated = false;

    public EnemyManager(ScoreSubject subject, Playing playing,Camera camera,Player player,String filePath) throws GameException {
        this.playing = playing;
        this.camera =camera;
        this.player= player;
        loadCop();
        loadTurret();
        loadBossLevelOne();
        loadBossLevelTwo();
        loadPortal();
        addEnemiesFromFile(filePath);
        previusSize = enemies.size();
        this.subject = subject;

    }

    public EnemyManager(ScoreSubject subject,Playing2 playing2,Camera camera,Player player,String filePath, int score) throws GameException {
        this.playing2 = playing2;
        this.camera =camera;
        this.player= player;
        this.score = score;
        loadCop();
        loadTurret();
        loadBossLevelOne();
        loadBossLevelTwo();
        loadPortal();
        addEnemiesFromFile(filePath);
        previusSize = enemies.size();
        this.subject = subject;
        portalLvl2 = new Portal();
    }

    public EnemyManager(ScoreSubject subject,Playing3 playing3,Camera camera,Player player,String filePath, int score) throws GameException {
        this.playing3 = playing3;
        this.camera =camera;
        this.player= player;
        this.score = score;
        loadCop();
        loadTurret();
        loadBossLevelOne();
        loadBossLevelTwo();
        loadPortal();
        addEnemiesFromFile(filePath);
        previusSize = enemies.size();
        this.subject = subject;
    }


    public void addEnemiesFromFile(String filePath) throws GameException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parameters = line.split(" ");
                int damage = Integer.parseInt(parameters[0]);
                int lives =  Integer.parseInt(parameters[1]);
                int x = Integer.parseInt(parameters[2]);
                int y = Integer.parseInt(parameters[3]);
                int width = Integer.parseInt(parameters[4]);
                int height = Integer.parseInt(parameters[5]);
                String enemyType = parameters[6];


                switch (enemyType) {
                    case "COP":
                        enemies.add(new Cop(x, y, width, height, COP, cop, player,lives));
                        break;
                    case "TURRET":
                        enemies.add(new Turret(x, y, width, height, TURRET, turret,lives,damage));
                        break;
                    case "FINALBOSS":
                        enemies.add(new FinalBoss(x , y, width, height, BOSS_1, boss_one, player,lives,damage));
                        break;
                    case "FINALBOSS2":
                        enemies.add(new FinalBoss2(x, y, width, height, BOSS_2, boss_two, player, lives, damage));
                        default:
                        //throw new GameException();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void update(ArrayList<TilesHitBox> walls) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy instanceof Turret) {
                ((Turret) enemy).update(camera, player, walls);
            } else if(enemy instanceof FinalBoss){
                ((FinalBoss)enemy).update(camera,player,walls);
                if(!((FinalBoss) enemy).isBossAlive() && !bossScoreUpdated){
                    System.out.println("You just Killed the First Boss!");
                    subject.setScore(score+20);
                    System.out.println("Your score is: " + score + " XP");
                    bossScoreUpdated = true;
                }
            }else if(enemy instanceof Cop){
                ((Cop)enemy).update(camera,player,walls);
            }
            else if(enemy instanceof FinalBoss2){
                ((FinalBoss2)enemy).update(camera,player,walls);
            }
            else
            {
                enemy.update(camera, player, walls);
            }
        }
        if(enemies.size() < previusSize ){
            System.out.println("You killed an Enemy!");
            subject.setScore(score+10);
            System.out.println("Your score is: " + score + " XP");
            previusSize = enemies.size();
        }
    }


    public void drawEnemies(Graphics g) {
        for (Enemy c : enemies) {
            if (c instanceof Cop)
                g.drawImage(cop[c.getEnemyState()][c.getAniIndex()], (int)c.getHitbox().x - 50 , (int) c.getHitbox().y, c.getWidth(), c.getHeight(), null);
            else if ( c instanceof Turret) {

                g.drawImage(turret[c.getEnemyState()][c.getAniIndex()], (int) c.getHitbox().x  , (int) c.getHitbox().y, c.getWidth(), c.getHeight(), null);
                ((Turret) c).drawBullets(g);
            }
            else if( c instanceof  FinalBoss){
               // System.out.println(((FinalBoss) c).isBossAlive());
                if(((FinalBoss) c).isBossAlive()) {
                    g.drawImage(boss_one[c.getEnemyState()][c.getAniIndex()], (int) c.getHitbox().x - 100, (int) c.getHitbox().y, c.getWidth(), c.getHeight(), null);
                    ((FinalBoss) c).drawBullets(g);
                }

                else{
                    g.drawImage(portal,c.getHitbox().x-50 ,c.getHitbox().y,176,196,null);
                }
            }
            else if(c instanceof FinalBoss2){
                if(((FinalBoss2) c).isBossAlive())
                    g.drawImage(boss_two[c.getEnemyState()][c.getAniIndex()], (int) c.getHitbox().x -50 , (int) c.getHitbox().y  , c.getWidth()  , c.getHeight(), null);
                else{
                    portalLvl2.drawPortal(g,c.getHitbox_X()- 50, c.getHitbox().y,c.getWidth()*2,c.getHeight());
                    portalLvl2.updateAnimation();
                }
            }


          c.drawHitbox(g);
        }
    }

    private void loadCop() throws GameException {
        cop = new BufferedImage[3][10];
        BufferedImage temp = LoadSave.getInstance().getAtlas(LoadSave.COP);
        for (int j = 0; j < cop.length; j++)
            for (int i = 0; i < cop[j].length; i++)
                cop[j][i] = temp.getSubimage(i * 183, j * 192, 183, 192);
    }

    private void loadTurret() throws GameException {
        turret = new BufferedImage[2][4];
        BufferedImage temp = LoadSave.getInstance().getAtlas(LoadSave.TURRET);
        for (int j = 0; j < turret.length; j++)
            for (int i = 0; i < turret[j].length; i++)
                turret[j][i] = temp.getSubimage(i * 132, j * 186, 132, 186);
    }

    private void loadBossLevelOne() throws GameException {
        boss_one = new BufferedImage[4][9];
        BufferedImage temp = LoadSave.getInstance().getAtlas(LoadSave.BOSS_LVL1);
        for (int j = 0; j < boss_one.length; j++)
            for (int i = 0; i < boss_one[j].length; i++)
                boss_one[j][i] = temp.getSubimage(i * 412, j * 264, 412, 264);
    }

    private void loadBossLevelTwo() throws GameException {
        boss_two = new BufferedImage[6][10];
        BufferedImage temp = LoadSave.getInstance().getAtlas(LoadSave.BOSS_LVL2);
        for (int j = 0; j < boss_two.length; j++)
            for (int i = 0; i < boss_two[j].length; i++)
               boss_two[j][i] = temp.getSubimage(i * 97, j * 141, 97, 141);
    }

    private void loadPortal() throws GameException{
        portal = LoadSave.getInstance().getAtlas(LoadSave.PORTAL);

    }

    public int getScore(){
        return score;
    }

    public ArrayList<Enemy> getEnemies(){
        return enemies;
    }

    @Override
    public void onScoreUpdate(int score) {
        this.score = score;
        // Perform any necessary actions when the score updates
        System.out.println("Score updated: " + score + " XP");    }
}
