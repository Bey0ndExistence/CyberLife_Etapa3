package levels;

import Entities.Guns;
import Entities.TilesHitBox;
import Entities.TilesHitBoxAnimated;
import Game.Game;
import Utils.GameException;
import Utils.LoadSave;
import gamestates.Playing;
import gamestates.Playing2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static Utils.Constants.PlayerConstants.*;
import static Utils.Constants.PlayerConstants.BANNER_SUSHI;

public class LevelManager2 {

    private Game game;
    Playing2 playing2;
    private BufferedImage miami_car,miami_highway, palm_tree, car_bmw,car_tesla, BG_FAR, BG_MIDDLE, BG_CLOSE;
    private Camera camera;
    private BackgroundLayer[] backgroundLayers;

   // private BufferedImage[]

    private ArrayList<TilesHitBox> hitBoxes;


    LoadSave singleton;
    public LevelManager2(Playing2 playing2, Camera camera) throws FileNotFoundException, GameException {
        this.playing2= playing2;
        this.camera = camera;
        importTileSprites();
        LoadTileHitboxes();

    }

    public ArrayList<TilesHitBox> getHitBoxes(){
        return hitBoxes;

    }
    private void importTileSprites() throws GameException {
        miami_highway = LoadSave.getInstance().getAtlas(LoadSave.MIAMI_HIGHWAY);
        miami_car = LoadSave.getInstance().getAtlas(LoadSave.MASINA);
        miami_car = LoadSave.getInstance().getAtlas(LoadSave.MASINA);
        palm_tree = LoadSave.getInstance().getAtlas(LoadSave.PALMIER);
        car_bmw = LoadSave.getInstance().getAtlas(LoadSave.CAR_BMW);
        car_tesla = LoadSave.getInstance().getAtlas(LoadSave.CAR_TESLA);
        //Background layers
        BG_FAR = LoadSave.getInstance().getAtlas(LoadSave.BG_FAR_LVL2);
        BG_MIDDLE = LoadSave.getInstance().getAtlas(LoadSave.BG_MIDDLE_LVL2);
        BG_CLOSE = LoadSave.getInstance().getAtlas(LoadSave.BG_CLOSE_LVL2);

        //BANNER


        //TILES


        //GUNS



        //LEVEL_END



    }

    public void LoadTileHitboxes() throws FileNotFoundException {

        backgroundLayers = new BackgroundLayer[] {
                new BackgroundLayer(BG_FAR, 0.5,0,0),
                new BackgroundLayer(BG_MIDDLE, 0.7,0,0),
                new BackgroundLayer(BG_CLOSE, 0.9,0,0),
        };

        this.hitBoxes = new ArrayList<>();
        //incarcare imagini


//        hitBoxes.add(new TilesHitBox(0,0,5376,780,miami_highway));
//        hitBoxes.add(new TilesHitBox(2500,500,552,204,miami_car));
//        hitBoxes.add(new TilesHitBox(750,150,399,624,palm_tree));
    //    hitBoxes.add(new TilesHitBox(2800,600,350,275,car_bmw));

        File map2 = new File("src/MapLvl2");
        Scanner scanner = new Scanner(map2);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(",");
            int type = Integer.parseInt(values[0]);
            int x = Integer.parseInt(values[1]);
            int y = Integer.parseInt(values[2]);
            int width = Integer.parseInt(values[3]);
            int height = Integer.parseInt(values[4]);

            if (type == 0) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, miami_highway));
            } else if (type == 1) {
                hitBoxes.add(new TilesHitBox(x, y, width, height, miami_car));
            }
            else if(type == 2){
                hitBoxes.add(new TilesHitBox(x, y, width, height, palm_tree));
            }

        }


        File mapHitbox = new File("src/MapHitboxLvl2");
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
