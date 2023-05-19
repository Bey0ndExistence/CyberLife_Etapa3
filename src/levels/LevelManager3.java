package levels;



import Entities.Guns;
import Entities.TilesHitBox;
import Entities.TilesHitBoxAnimated;
import Game.Game;
import Utils.GameException;
import Utils.LoadSave;

import gamestates.Playing3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static Utils.Constants.PlayerConstants.*;
import static Utils.Constants.PlayerConstants.BANNER_SUSHI;

public class LevelManager3 {

    private Game game;
    Playing3 playing3;
    private BufferedImage map, big_computer,small_terminal,elevator,cryo_pod,server_cabinetes,  BG_CLOSE;
    private Camera camera;
    private BackgroundLayer[] backgroundLayers;

    // private BufferedImage[]

    private ArrayList<TilesHitBox> hitBoxes;


    LoadSave singleton;
    public LevelManager3(Playing3 playing3, Camera camera) throws FileNotFoundException, GameException {
        this.playing3= playing3;
        this.camera = camera;
        importTileSprites();
        LoadTileHitboxes();

    }

    public ArrayList<TilesHitBox> getHitBoxes(){
        return hitBoxes;

    }
    private void importTileSprites() throws GameException {
        map = LoadSave.getInstance().getAtlas(LoadSave.LVL3_MAP);

        big_computer = LoadSave.getInstance().getAtlas(LoadSave.LVL3_BIG_COMPUTER);
        small_terminal = LoadSave.getInstance().getAtlas(LoadSave.LVL3_TERMINAL);
        elevator = LoadSave.getInstance().getAtlas(LoadSave.LVL3_ELEVATOR);
        cryo_pod = LoadSave.getInstance().getAtlas(LoadSave.LVL3_CRYO_POD);
        server_cabinetes = LoadSave.getInstance().getAtlas(LoadSave.LVL3_CABINS);

        //Background layers

        BG_CLOSE = LoadSave.getInstance().getAtlas(LoadSave.LVL3_BG);

        //BANNER


        //TILES


        //GUNS



        //LEVEL_END



    }

    public void LoadTileHitboxes() throws FileNotFoundException {

        backgroundLayers = new BackgroundLayer[] {
                new BackgroundLayer(BG_CLOSE, 0.7,2000,0),
        };

        this.hitBoxes = new ArrayList<>();
        //incarcare imagini
//        hitBoxes.add(new TilesHitBox(0,-110,6240,880,map));
//        hitBoxes.add(new TilesHitBox(300,0,636,738,big_computer));
//        hitBoxes.add(new TilesHitBox(2000,500,165,185,small_terminal));
//        hitBoxes.add(new TilesHitBox(3000,400,268,356,elevator));
//        hitBoxes.add(new TilesHitBox(4000,400,236,348,cryo_pod));
//        hitBoxes.add(new TilesHitBox(5000,480,260,220,server_cabinetes));
//        hitBoxes.add(new TilesHitBox(4950,530,260,220,server_cabinetes));


        File map3 = new File("src/MapLvl3");
        Scanner scanner = new Scanner(map3);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(",");
            int type = Integer.parseInt(values[0]);
            int x = Integer.parseInt(values[1]);
            int y = Integer.parseInt(values[2]);
            int width = Integer.parseInt(values[3]);
            int height = Integer.parseInt(values[4]);

            if (type == 1) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, map));
            } else if (type == 2) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, big_computer));
            } else if (type == 3) {
                hitBoxes.add(new TilesHitBox(x, y, width, height,small_terminal ));
            } else if (type == 4) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, elevator));
            } else if (type == 5) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, cryo_pod));
            } else if (type == 6) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, server_cabinetes));
            }
        }


        File mapHitbox = new File("src/MapHitboxLvl3");
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
}
