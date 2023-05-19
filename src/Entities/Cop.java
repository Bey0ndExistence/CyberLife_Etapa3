package Entities;

import levels.Camera;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utils.Constants.EnemyConstants.*;

public class Cop extends  Enemy{
    BufferedImage[][] image;

    Player player;
    private int lives = 2;
    public Cop(float x, float y, int width, int height,  int enemyType,BufferedImage[][] image,Player player, int lives) {
        super(x, y, width, height, enemyType);
        this.image = image;
        this.player = player;
        this.lives = lives;
    }

    public boolean enemy_got_damaged(){
        if(player.isSecondGun())
            lives-=2;
        else
            lives--;

        System.out.println("Enemy has only " + lives +"remaining");
        return (lives > 0);
    }

    @Override
    public void update(Camera camera,Player player,ArrayList<TilesHitBox> walls){
        updateAnimationTick();
        updateEnemyPos(player,walls,camera);
    }

    @Override
    public void updateEnemyPos(Player player, ArrayList<TilesHitBox> walls, Camera camera){
        super.updateEnemyPos(player,walls,camera);
        float playerDistance = Math.abs(player.getX() - getHitbox_X());

        if (!playerDetected && playerDistance < detectionDistance) {
            playerDetected = true;
        }

        if (playerDistance < pickUpGun) {
            enemyState = IDLE_GUN;
        } else {
            enemyState = IDLE;
        }

        if (playerDetected) {
            movingLeft = true;
            enemyState = RUNNING;
        }
        else {
            movingLeft = false;
        }

        if (movingLeft) {
            xspeed = -2f;
        } else {
            if (!playerDetected) {
                xspeed = 0;
                update(camera);
            }
        }
    }


}
