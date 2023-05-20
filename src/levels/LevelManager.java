package levels;



import Entities.Entity;
import Entities.Guns;
import Entities.TilesHitBox;
import Entities.TilesHitBoxAnimated;
import Game.Game;
import Utils.GameException;
import Utils.LoadSave;
import gamestates.Playing;


import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import static Utils.Constants.PlayerConstants.*;

/**
 * The LevelManager class manages the loading and drawing of tiles, background layers, and hitboxes for
 * a game level in Java.
 */
public class LevelManager {

    private Game game;
    Playing playing;
    private BufferedImage BG_FAR,BG_MIDDLE, BG_CLOSE,bridge,banner_start,banner_R,banner_flash, banner_life ,longBlock, smallBlock,banner_Hotel, banner_Japoneza_verde,banner_coke,banner_Japoneza_mov,banner_sushi;
    private Camera camera;
    private BackgroundLayer[] backgroundLayers;

    private BufferedImage[] bannerStart,bannerR,bannerFlash,bannerLife,bannerJaponezaverde,bannerCoke, bannerJaponezamov,bannerSushi;

    private BufferedImage secondGun;
    private BufferedImage level_end;
    private ArrayList<TilesHitBox> hitBoxes;


    LoadSave singleton;
    public LevelManager(Playing playing, Camera camera) throws FileNotFoundException, GameException, SQLException {
        this.playing= playing;
        this.camera = camera;
        importTileSprites();
        LoadBannerStart();
        LoadBannerR();
        LoadBannerFlash();
        LoadBannerLife();
        LoadBannerJaponezaVerde();
        LoadBannerCoke();
        LoadBannerJaponezaMov();
        LoadBannerSushi();
        LoadTileHitboxes();

    }

    public ArrayList<TilesHitBox> getHitBoxes(){
       return hitBoxes;

    }
    private void importTileSprites() throws GameException {
        singleton = LoadSave.getInstance();

        //incarcare imagini
        BufferedImage bridgeTemp = singleton.getAtlas(singleton.BRIDGE_TILE);
        BufferedImage tileset1 = singleton.getAtlas(singleton.LEVEL1_TILESET1);
        BufferedImage tileset2 = singleton.getAtlas(singleton.LEVEL1_TILESET2);

        //Background layers
        BG_FAR = singleton.getAtlas(singleton.BG_FAR);
        BG_MIDDLE = singleton.getAtlas(singleton.BG_MIDDLE);
        BG_CLOSE = singleton.getAtlas(singleton.BG_CLOSE);

        //BANNER
        banner_Hotel = singleton.getAtlas(singleton.BANNER_HOTEL);

        //TILES
        bridge = bridgeTemp.getSubimage(0,96,1670,288); // platforma 1
        smallBlock = tileset1.getSubimage(0,42,298,288); // platforma 3
        longBlock = tileset2.getSubimage(104,58,920,288); // platforma 2

        //GUNS

        secondGun = singleton.getAtlas(LoadSave.GUN_RED);

        //LEVEL_END

        level_end= singleton.getAtlas(LoadSave.LEVEL_END);
        
    }

    public void LoadTileHitboxes() throws FileNotFoundException, SQLException {
        Database db = Database.getInstance();
        Connection connection = db.getConnection();

        Statement statement = connection.createStatement();


        backgroundLayers = new BackgroundLayer[] {
                new BackgroundLayer(BG_FAR, 0.5,0,0),
                new BackgroundLayer(BG_MIDDLE, 0.7,0,0),
                new BackgroundLayer(BG_CLOSE, 0.9,0,0),
        };
        this.hitBoxes = new ArrayList<>();



        ResultSet mapResult = statement.executeQuery("SELECT * FROM Map");
        while (mapResult.next()) {
            int type = mapResult.getInt("type");
            int x = mapResult.getInt("x");
            int y = mapResult.getInt("y");
            int width = mapResult.getInt("width");
            int height = mapResult.getInt("height");

            if (type == 1) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, bridge));
            } else if (type == 2) {
                hitBoxes.add(new TilesHitBoxAnimated(x, y, width, height, bannerStart, BANNER_START));
            } else if (type == 3) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, longBlock));
            }
            else if( type == 4){
                hitBoxes.add(new TilesHitBoxAnimated(x, y, width, height, bannerR, BANNER_R));
            } else if (type == 5) {
                hitBoxes.add(new TilesHitBoxAnimated(x, y, width, height, bannerLife, BANNER_LIFE));
            } else if (type == 6) {
                hitBoxes.add(new TilesHitBoxAnimated(x, y, width, height, bannerFlash, BANNER_FLASH));
            } else if (type == 7) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, bridge));
            } else if (type == 8) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, banner_Hotel));
            } else if (type == 9) {
                hitBoxes.add(new TilesHitBoxAnimated(x, y, width, height, bannerJaponezaverde, BANNER_JAPONEZA_VERDE));
            } else if (type == 10) {
                hitBoxes.add(new TilesHitBoxAnimated(x, y, width, height, bannerCoke, BANNER_COKE));
            } else if (type == 11) {
                hitBoxes.add(new TilesHitBoxAnimated(x, y, width, height, bannerJaponezamov, BANNER_JAPONEZA_MOV));
            } else if (type == 12) {
                hitBoxes.add(new TilesHitBoxAnimated(x, y, width, height, bannerSushi, BANNER_SUSHI));
            } else if (type == 13) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, smallBlock));
            } else if (type == 14) {
                hitBoxes.add(new Guns(x, y, width, height, secondGun));
            } else if (type == 15) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, level_end));
            }
        }
        mapResult.close();

        File mapHitbox = new File("src/MapHitbox");
        Scanner scannerHitbox = new Scanner(mapHitbox);
        int index = 0;
        while (scannerHitbox.hasNextLine() && index < hitBoxes.size()) {
            String line = scannerHitbox.nextLine();
            String[] values = line.split(",");
            int x = Integer.parseInt(values[0]);
            int y = Integer.parseInt(values[1]);
            int width = Integer.parseInt(values[2]);
            int height = Integer.parseInt(values[3]);
            hitBoxes.get(index).initHitbox(x, y, width, height);
            index++;
        }
        // Close the statement (the connection will be closed when the instance of Database is destroyed)
        statement.close();

    }


    public  void drawTiles(Graphics g){
       for( TilesHitBox hitBox : hitBoxes){

           if (hitBox instanceof TilesHitBoxAnimated) {
               ((TilesHitBoxAnimated) hitBox).drawAnimated(g);

           }
           else if(hitBox instanceof Guns) {
               ((Guns) hitBox).drawGun(g);
           }
           else {
               hitBox.draw(g);
           }
        hitBox.drawHitbox(g);
       }
    }
    public void drawBG(Graphics2D g2d) {

        for (BackgroundLayer layer : backgroundLayers) {
            layer.draw(g2d);
        }

    }

    public void update(){
        camera.update();
        for (BackgroundLayer layer : backgroundLayers) {
            layer.update(camera);
        }
        for( TilesHitBox hitBox: hitBoxes) {
            hitBox.update(camera);
            if (hitBox instanceof TilesHitBoxAnimated) {
                ((TilesHitBoxAnimated) hitBox).updateAnimation();
            }
        }
    }

    public void LoadBannerStart() throws GameException {
        banner_start = singleton.getAtlas(singleton.BANNER_START);

        this.bannerStart= new BufferedImage[getSpriteAmountTiles(BANNER_START)];
        for(int i=0; i<bannerStart.length;i++)
             {
                this.bannerStart[i] = this.banner_start.getSubimage(i * 270, 0, 270, 180);
            }
    }

    public void LoadBannerR() throws GameException {
        banner_R = singleton.getAtlas(singleton.BANNER_R);

        this.bannerR= new BufferedImage[getSpriteAmountTiles(BANNER_R)];
        for(int i=0; i<bannerR.length;i++)
        {
            this.bannerR[i] = this.banner_R.getSubimage(i * 162, 0, 162, 195);
        }
    }

    public void LoadBannerFlash() throws GameException {
        banner_flash = singleton.getAtlas(singleton.BANNER_FLASH);

        this.bannerFlash= new BufferedImage[getSpriteAmountTiles(BANNER_FLASH)];
        for(int i=0; i<bannerFlash.length;i++)
        {
            this.bannerFlash[i] = this.banner_flash.getSubimage(i * 165, 0, 165, 204);
        }
    }

    public void LoadBannerLife() throws GameException {
        banner_life = singleton.getAtlas(singleton.BANNER_LIFE);

        this.bannerLife= new BufferedImage[getSpriteAmountTiles(BANNER_LIFE)];
        for(int i=0; i<bannerLife.length;i++)
        {
            this.bannerLife[i] = this.banner_life.getSubimage(i * 111, 0, 111, 180);
        }
    }
    public  void LoadBannerJaponezaVerde() throws GameException {
        banner_Japoneza_verde = singleton.getAtlas(singleton.BANNER_JAPONEZA_VERDE);

        this.bannerJaponezaverde = new BufferedImage[getSpriteAmountTiles(BANNER_JAPONEZA_VERDE)];
        for (int i = 0; i < bannerJaponezaverde.length; i++) {
            this.bannerJaponezaverde[i] = this.banner_Japoneza_verde.getSubimage(i * 105, 0, 105, 276);
        }
    }
    public  void LoadBannerCoke() throws GameException {
        banner_coke = singleton.getAtlas(singleton.BANNER_COKE);

        this.bannerCoke = new BufferedImage[getSpriteAmountTiles(BANNER_COKE)];
        for (int i = 0; i < bannerCoke.length; i++) {
            this.bannerCoke[i] = this.banner_coke.getSubimage(i * 54, 0, 54, 156);
        }
    }

    public  void LoadBannerJaponezaMov() throws GameException {
        banner_Japoneza_mov = singleton.getAtlas(singleton.BANNER_JAPONEZA_MOV);

        this.bannerJaponezamov = new BufferedImage[getSpriteAmountTiles(BANNER_JAPONEZA_MOV)];
        for (int i = 0; i < bannerJaponezamov.length; i++) {
            this.bannerJaponezamov[i] = this.banner_Japoneza_mov.getSubimage(i * 76, 0, 76, 192);
        }
    }

    public  void LoadBannerSushi() throws GameException {
        banner_sushi = singleton.getAtlas(singleton.BANNER_SUSHI);

        this.bannerSushi = new BufferedImage[getSpriteAmountTiles(BANNER_SUSHI)];
        for (int i = 0; i < bannerSushi.length; i++) {
            this.bannerSushi[i] = this.banner_sushi.getSubimage(i * 180, 0, 180, 65);
        }
    }



}
