/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.world.entity.player.house;

import java.util.HashMap;
import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.world.entity.player.Player;
import server.world.map.Palette;
import server.world.map.Position;
import server.world.object.WorldObject;

/**
 *
 * @author Brecht
 */
public class House {    
    
    private Room[][][] rooms;    
    private HashMap<WorldObject, Position> objects;
    private Player owner;
    
    
    
    public static void showHouse(Player owner, Player visitor) {
        
        House h = new House(owner);
        
        visitor.move(new Position(48,48,0));
        
        
        visitor.getPacketBuilder().sendCustomMapRegion(h.rooms);
        
        showObjects(h, visitor);
        
        
    }
    
    public House(Player player) {        
        rooms = new Room[13][13][4];      
        
	for(int x = 0; x < 11; x++) {
            for(int y = 0; y < 11; y++) {
                //for(int z = 0; z < 4; z++) {
                   // rooms[x][y][0] = h.rooms[x][y][0];
                    rooms[x][y][0] = Room.DEFAULT;
                    if(x == 0 || y == 0 || x == 1 || y == 1 || x == 11 || y == 11 || x == 10 || y == 10) {
                        rooms[x][y][0] = null;
                    }                    
              //  }

            }
        }
	rooms[6][6][0] = Room.GARDEN;
    }
    
    public static void showObjects(House house, Player visitor) {
        
        
    }
    
    
    
    
    
}
