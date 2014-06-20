

package server.world.house;

import java.io.File;
import server.world.entity.player.Player;
import server.world.map.Palette;

/**
 *
 * @author Carnewal
 * 
 *  All credits go to Graham/Women
 *  http://www.rune-server.org/runescape-development/rs2-server/tutorials/518679-pi-real-construction-using-construct-map-region-packet.html
 * 
 */

public class House {
    
	public static final Palette.PaletteTile DEFAULT = new Palette.PaletteTile(1864, 5056, 0, 0);
    public static final Palette.PaletteTile GARDEN = new Palette.PaletteTile(1859, 5066, 0, 0);
    public static final Palette.PaletteTile THRONE = new Palette.PaletteTile(1904, 5096, 0, 0);
    public static final Palette.PaletteTile GAME = new Palette.PaletteTile(1864, 5104, 0, 0);
    public static final Palette.PaletteTile FLOOR2 = new Palette.PaletteTile(1903, 5095, 0, 0);
	public static final Palette.PaletteTile PARLOUR = new Palette.PaletteTile(1856, 5112, 0, 0);
	public static final Palette.PaletteTile KITCHEN = new Palette.PaletteTile(1872, 5112, 0, 0);
	public static final Palette.PaletteTile DINING = new Palette.PaletteTile(1890, 5112, 0, 0);
	public static final Palette.PaletteTile WORKSHOP = new Palette.PaletteTile(1856, 5096, 0, 0);
	public static final Palette.PaletteTile BEDROOM = new Palette.PaletteTile(1904, 5112, 0, 0);
	public static final Palette.PaletteTile SKILLHALL = new Palette.PaletteTile(1880, 5104, 0, 0);
	public static final Palette.PaletteTile COMBAT = new Palette.PaletteTile(1880, 5088, 0, 0);
	public static final Palette.PaletteTile QUEST_HALL = new Palette.PaletteTile(1912, 5104, 0, 0);
	public static final Palette.PaletteTile STUDY = new Palette.PaletteTile(1888, 5096, 0, 0);
	public static final Palette.PaletteTile COSTUME_ROOM = new Palette.PaletteTile(1904, 5064, 0, 0);
	public static final Palette.PaletteTile CHAPEL = new Palette.PaletteTile(1872, 5096, 0, 0);
	public static final Palette.PaletteTile PORTAL_CHAMBER = new Palette.PaletteTile(1864, 5088, 0, 0);
	public static final Palette.PaletteTile FORMAL_GARDEN = new Palette.PaletteTile(1872, 5064, 0, 0);
	public static final Palette.PaletteTile THRONE_ROOM = new Palette.PaletteTile(1904, 5080, 0, 0);
	public static final Palette.PaletteTile OUBILIETTE = new Palette.PaletteTile(1904, 5080, 0, 0);
	public static final Palette.PaletteTile CORRIDOR_DUNGEON = new Palette.PaletteTile(1888, 5080, 0, 0);
	public static final Palette.PaletteTile JUNCTION_DUNGEON = new Palette.PaletteTile(1856, 5080, 0, 0);
	public static final Palette.PaletteTile STAIRS_DUNGEON = new Palette.PaletteTile(1872, 5080, 0, 0);
	public static final Palette.PaletteTile TREASURE_ROOM = new Palette.PaletteTile(1912, 5088, 0, 0);

    
    
    public static boolean hasHouse(Player player) {
     //   File file = new File(houseLocation);
//	return file.exists();
        return true;
    }
    
    public static void showHouse(Player owner, Player visitor) {
        Palette p = new Palette();
        
        
        visitor.getPacketBuilder().sendCustomMapRegion(p);
        
        
    }
    
    
}
