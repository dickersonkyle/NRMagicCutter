package cutter;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.LoopTask;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Players;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.Item;
import org.rev317.min.api.wrappers.SceneObject;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
/**
 * Created by KyleHD on 5/1/2016.
 */

@ScriptManifest(

        author = "KyleHD",
        name = "NRMagicCutter",
        category = Category.WOODCUTTING,
        version = 1.2,
        description = "A basic woodcutting script for Near Reality. It cuts Magic trees for XP and 150k for each log, then drops when full. Start at Magic Trees training teleport.",
        servers = {"Near Reality"})

public class Cutter extends Script implements LoopTask, Paintable{

    private final int [] TREE_IDS = {1306, 8396, 8397, 8398, 8399, 8400, 8401, 8402, 8403, 8404, 8405, 8406, 8407, 8408, 8409,};
    private final int [] LOG_IDS = {1513, 1514};
    public long startTime;
    public int XP = 0;

    public int getHourlyRate(int variable) {
        return (int) (((double) (variable - 0) * 3600000D) / (double) (System
                .currentTimeMillis() - startTime));
    }

    public Image getImage(String url) { // Adds image.
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    public final Image img1 = getImage("http://i.imgur.com/TbdOWlj.png");

    public void paint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        g.drawImage(img1, -1, 0, null);
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        g.drawString(runTime(startTime), 160, 388);
        int XP2 = Skill.getCurrentExperience(Skill.WOODCUTTING.getIndex())
                - XP;
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        g.drawString((new StringBuilder("").append(XP2)).toString(), 404,
                387);
        g.drawString((new StringBuilder("")).append(getHourlyRate(XP2))
                .toString(), 341, 419);

    }

    @Override
    public int loop() { // Check for trees, check if inventory has space. If both are true, do option CHOP_DOWN on tree with TREE_IDS.
        SceneObject tree = tree();
        if(tree != null) {
            if(!Inventory.isFull()) {
                if(Players.getMyPlayer().getAnimation() == -1){
                    tree.interact(SceneObjects.Option.CHOP_DOWN);
                    Time.sleep(new SleepCondition(){
                        @Override
                        public boolean isValid(){
                            return Players.getMyPlayer().getAnimation() != -1;
                        }
                    }, 3000);
                }
            }else{
                for(Item log : Inventory.getItems(LOG_IDS)){ // Checks for inventory space. If no space, drop logs with LOG_IDS.
                    if(log != null){
                        log.drop();
                        Time.sleep(1000);
                    }
                }
            }
        }
        return 200;
    }

    @Override
    public boolean onExecute() {

        startTime = System.currentTimeMillis();
        XP = Skill.WOODCUTTING.getExperience();

        System.out.println("NRMagicCutter has started. Enjoy!"); // Script is starting.
        return true;
    }

    public static String runTime(long i) {
        DecimalFormat nf = new DecimalFormat("00");
        long millis = System.currentTimeMillis() - i;
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;
        return nf.format(hours) + ":" + nf.format(minutes) + ":"
                + nf.format(seconds);
    }

    @Override
    public void onFinish() {
        System.out.println("NRMagicCutter has ended. Goodbye!");  // Scrtipt is finished.
    }

    private SceneObject tree(){
        for(SceneObject tree : SceneObjects.getNearest(TREE_IDS)){ //Tree Shtuff.
            if(tree !=null){
                return tree;
            }
        }
        return null;
    }
}