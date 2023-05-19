package Entities;

import Utils.GameException;
import Utils.LoadSave;
import gamestates.Gamestate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static Utils.Constants.PlayerConstants.*;


/**
 * The Player class extends the Entity class and contains methods for updating and rendering the
 * player's animations and position.
 */
public class Player extends Entity{

    private BufferedImage[][] animationSheet;
    private BufferedImage[] bulletSheet;
    private BufferedImage img,bullet_img;

    private static int playerLives = 3;
    private int aniTick=0,aniIndex=0;
    private final int aniSpeed=27;
    private int playerAction = IDLE;
    private boolean moving =false;
    private boolean duck = false;

    private boolean shooting = false;
    private static boolean isAlive = true;

    private long cooldownStart = 0;
    private boolean cooldown = false;

    private boolean left, up, right ;

    private boolean inAir = false;

    private boolean goingLeft = false, goingRight = false;

    private float xspeed, yspeed;

    private Rectangle cameraHitbox;

    private boolean secondGun=false;

    private int LastCameraX,cameraXDiff;

   // private ArrayList<Bullet> bullets;

    private boolean okShoot= false;

    Bullet bullet;

    // The above code is defining a Java class called "Player" which extends another class called
    // "Entity". It defines the constructor for the Player class, which takes in x and y coordinates,
    // width, and height as parameters, and initializes the hitbox and cameraHitbox variables. It also
    // defines methods for updating and rendering the player, loading animations, updating the
    // animation based on player actions, and setting player actions such as ducking, shooting, and
    // moving left or right. Additionally, it includes methods for handling collisions with walls and
    // resetting the player's position if they fall off the screen.

    public Player(float x, float y,int width, int height) throws IOException, GameException {
        super(x, y,width,height);
        loadAnimations();

        initHitbox(x+50 ,y  , width/2, height);
        cameraHitbox = new Rectangle(1200,200,200,200);

        loadBulletAnimation();

        bullet = new Bullet(this.x + 200, this.y+200,60,15, bulletSheet,1,BULLET_PLAYER );
        bullet.initHitbox(this.x + 200,this.y +200,60,15);
    }

    public Rectangle getCameraHitbox(){
        return cameraHitbox;
    }

    public void update(ArrayList<TilesHitBox> wall, ArrayList<Enemy> enemies) {
        updatePos(wall);
        UpdateAnimation();
        setAnimation();

        bullet.update();

        if (bullet.isActive()) {
            if (bullet.getHitbox().getX() < 1920) {
                Iterator<Enemy> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                    if (bullet.getHitbox().intersects(enemy.getHitbox())) {
                        // Handle bullet-enemy collision
                        // ...

                        // Remove the enemy from the list
                        if (enemy instanceof Turret) {
                            if (!((Turret) enemy).enemy_got_damaged())
                                enemyIterator.remove();
                        } else if (enemy instanceof Cop) {
                            if (!((Cop) enemy).enemy_got_damaged())
                                enemyIterator.remove();
                        }
                        else if (enemy instanceof FinalBoss) {
                         if(((FinalBoss) enemy).isBossAlive())
                                ((FinalBoss) enemy).enemy_got_damaged();
                        }
                        else if(enemy instanceof FinalBoss2){
                            if(((FinalBoss2) enemy).isBossAlive()){
                                ((FinalBoss2) enemy).enemy_got_damaged();
                            }
                        }

                        bullet.setActive(false);
                        bullet.setPos(this.x + 200, this.y + 100);
                    }
                }
            } else {
                bullet.setActive(false);

               // System.out.println("Suntem in update "+this.y + " " + bullet.y + " " + bullet.isActive());
                bullet.setPos(this.x + 200, this.y + 100);
              //  System.out.println(this.y + " " + bullet.y + " " + bullet.isActive());
            //    System.out.println();
            }
        }

    }

    public void render(Graphics g){
        g.drawImage(this.animationSheet[playerAction][this.aniIndex],(int)x,(int)y,width, height, null);
        //drawHitbox(g);
        //g.drawRect(hitbox.x,hitbox.y,width,height);
        if(bullet.isActive())
            bullet.drawBullet(g);

    }



    public void loadAnimations() throws  GameException {

        this.img =LoadSave.getInstance().getAtlas(LoadSave.PLAYER_ATLAS);

        this.animationSheet= new BufferedImage[14][8];
        for(int i=0; i<animationSheet.length;i++)
            for(int j=0; j< animationSheet[i].length;j++) {
                this.animationSheet[i][j] = this.img.getSubimage(j * 240, i * 240, 240, 240);

            }
    }
    public void loadBulletAnimation() throws  GameException {

        this.bullet_img =LoadSave.getInstance().getAtlas(LoadSave.BULLETS);
        this.bulletSheet= new BufferedImage[4];
        for(int i=0; i< bulletSheet.length;i++) {
            this.bulletSheet[i] = this.bullet_img.getSubimage(i * 60, 0, 60,15);

        }
    }

    public void shoot() {
        if (!bullet.isActive() ) {
            bullet.setActive(true);
        }
    }

    public static void damage(int damage) {
        playerLives-=damage;
        System.out.println("You got only " + playerLives + " left");
        if (playerLives <= 0) {
            Gamestate.state = Gamestate.MENU;
            isAlive = false;
        }
    }

    public static boolean getIsAlive(){
        return isAlive;
    }


    public void UpdateAnimation() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            if (!duck) {
                aniIndex++;
                if (aniIndex >= GetSpriteAmount(playerAction)) {
                    aniIndex = 0;
                }
            } else {
                if (cooldown) {
                    // pause the animation on the last frame

                    if (aniIndex >= GetSpriteAmount(DUCK) - 1) {
                        aniIndex = GetSpriteAmount(DUCK) - 1;
                    }
                    else{
                        aniIndex++;
                    }
                    // check if cooldown is over
                    if (System.currentTimeMillis() - cooldownStart >= 500) {
                        cooldown = false;
                    }
                } else {
                    aniIndex++;
                    if (aniIndex >= GetSpriteAmount(DUCK)) {
                        aniIndex = 0;
                        this.duck = false;
                    }
                }
            }
        }
    }


    public void setDucking(){
        if (!cooldown) {
            this.duck = true;
            this.shooting = false;
            this.moving=false;
            this.cooldown = true;
            this.cooldownStart = System.currentTimeMillis();
        }
    }

    public void setShooting(){
        this.shooting =true;
    }

    public void setNonShooting(){
        this.shooting =false;
    }

    private void updatePos(ArrayList<TilesHitBox> walls){
        this.moving = false;
        TilesHitBox wall= null;

        if(left && right || !left && !right) xspeed *= 0.8;
        else if (left && !right && !inAir) {
            this.moving = true;
            xspeed--;
        }
        else if (right && !left && !inAir) {
            this.moving = true;
            xspeed++;

        }

        if(xspeed > 0 && xspeed < 0.25) xspeed =0;
        if(xspeed < 0 && xspeed > -0.25) xspeed =0;

        if(xspeed > 1) xspeed = 1;
        if(xspeed < -1) xspeed = -1;


        Iterator<TilesHitBox> iter;
        if(up){
            this.moving = false;
            this.inAir = true;
            hitbox.y++;
            iter = walls.iterator();
            while (iter.hasNext()) {
                wall = iter.next();
                if (wall.getHitbox().intersects(getHitbox())) {
                    yspeed = -3;

                }
            }
            hitbox.y--;
        }

        yspeed += 0.035;

        LastCameraX = cameraHitbox.x;
        // Horizontal Collision

        boolean ok = true;
        if (xspeed > 0) { // move to the right
            iter = walls.iterator();
            while (iter.hasNext()) {
                wall = iter.next();
                if (hitbox.x + hitbox.width + xspeed >= wall.getHitbox().x &&
                        hitbox.x + hitbox.width <= wall.getHitbox().x &&
                        hitbox.y < wall.getHitbox().y + wall.getHitbox().height &&
                        hitbox.y + hitbox.height > wall.getHitbox().y) {
                    if (wall instanceof Guns) {
                        ((Guns) wall).isVisible = false;
                        ((Guns) wall).nrGunsTook();
                        secondGun = true;
                        iter.remove(); // remove element using iterator
                    } else {
                        ok = false;
                        break;
                    }
                }
            }
            if (ok) {
                cameraHitbox.x += xspeed;
            }
        } else if (xspeed < 0) { // move to the left
            iter= walls.iterator();
            while (iter.hasNext()) {
                wall = iter.next();
                if (hitbox.x + xspeed <= wall.getHitbox().x + wall.getHitbox().width &&
                        hitbox.x >= wall.getHitbox().x + wall.getHitbox().width &&
                        hitbox.y < wall.getHitbox().y + wall.getHitbox().height &&
                        hitbox.y + hitbox.height > wall.getHitbox().y)  {
                    if (wall instanceof Guns) {
                        ((Guns) wall).isVisible = false;
                        ((Guns) wall).nrGunsTook();
                        secondGun = true;
                        iter.remove(); // remove element using iterator
                    } else {
                        ok = false;
                        break;
                    }
                }
            }
            if (ok) {
                cameraHitbox.x += xspeed;
            }
        }

        hitbox.y += yspeed;
        iter = walls.iterator();
        while (iter.hasNext()){
            wall = iter.next();
            if (getHitbox().intersects(wall.getHitbox())) {
                if(wall instanceof Guns ){
                    ((Guns) wall).isVisible = false;
                    ((Guns) wall).nrGunsTook();
                    secondGun =true;
                    iter.remove();
                }
                else{
                    hitbox.y -= yspeed;
                    while (!wall.getHitbox().intersects(getHitbox()))
                        hitbox.y += Math.signum(yspeed);
                    hitbox.y -= Math.signum(yspeed);
                    okShoot = true;
                    yspeed = 0;
                    inAir = false;
                }
            }
            else{
                okShoot= false;
            }
            y = hitbox.y;

        }

        y += yspeed;
        hitbox.y = (int) y ;

      //  System.out.println("y din updatePos "+ y);
        if(y>800){
            playerReset();
        }
    }

    private void playerReset(){
        cameraHitbox.x = 1200;
        hitbox.y = 0;
        y = hitbox.y;
        xspeed=0;
        yspeed=0;

    }
    public boolean isSecondGun(){
        return secondGun;
    }

    public void setRight(boolean right) {
        this.right = right;
    }


    public void setUp(boolean up) {
        this.up = up;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }


    public void setGoingLeft(){
        goingLeft= true;
        goingRight =false;
    }

    public void setGoingRight(){
        goingRight = true;
        goingLeft = false;
    }

//    public void bulletAdd(){
//        if()
//
//        bullets.add(bullet);
//    }
//    public void bulletCheckCollision(){
//
//    }
//

    private void setAnimation() {
        int previousPlayerAction = this.playerAction;
        if(moving) {
            if(goingRight)
                this.playerAction = RUN;
            else
                this.playerAction = RUN_LEFT;
        }
        else if(this.duck) {
            this.playerAction = DUCK;
        }
        else if(this.inAir){
            if((cameraHitbox.x - LastCameraX) > 0)
                this.playerAction = JUMP;
            else if((cameraHitbox.x - LastCameraX) <0)
                this.playerAction = JUMP_LEFT;
            else
                this.playerAction =JUMP;
        }
        else if(this.shooting){
            shoot();
            if(!secondGun)
                this.playerAction = SHOOT1;
            else
                this.playerAction = SHOOTcombuster;
        }
        else if(this.isAlive== false){
            this.playerAction = HURT;
            y -= 2;
            hitbox.y = (int) y ;

            this.isAlive= true;
        }
        else {
            this.playerAction = IDLE;
        }

        if(previousPlayerAction != this.playerAction) {
            this.aniIndex = 0;
            this.aniTick =  0;
        }


    }

//    public void setSecondGun(){
//        secondGun= true;
//    }

      public void LvlSpawn(int value){
        cameraHitbox.x = value;
      }

    void setAlive(boolean alive){
        this.isAlive = alive;
    }
}
