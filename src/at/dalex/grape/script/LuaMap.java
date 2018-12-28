package at.dalex.grape.script;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import at.dalex.grape.gamestatemanager.PlayState;
import at.dalex.grape.graphics.Tileset;
import at.dalex.grape.map.MapReader;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;
import at.dalex.grape.toolbox.Dialog;

class LuaMap {

	public static class ChangeMap extends ThreeArgFunction {

		@Override
		public LuaValue call(LuaValue arg0, LuaValue arg1, LuaValue arg2) {
			String mapPath = arg0.checkjstring();
			String tilesetPath = arg1.checkjstring();
			int tileSize = arg2.checkint();
			Tileset tileset = new Tileset(ImageUtils.loadBufferedImage(tilesetPath), tileSize);
			if (mapPath != null && !mapPath.equals("") && !mapPath.equals(" ")) {
				PlayState.current_map = MapReader.parseMap(mapPath, tileset);
			} else Dialog.error("Map-Parse-Error", "The path '" + mapPath + "'\ndoes not lead to a correct mapfile in XML Format.");
			return NIL;
		}
	}
	
	public static class GetScale extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			if (PlayState.current_map == null)
				Dialog.error("Lua-Error", 	"Unable to get the scale of the current map,\n" +
											"no map has been loaded yet!");

			return LuaValue.valueOf(PlayState.current_map.getScale());
		}
	}
	
	public static class SetScale extends OneArgFunction {

		@Override
		public LuaValue call(LuaValue arg0) {
			float scale = (float) arg0.checkdouble();

			if (PlayState.current_map == null)
				Dialog.error("Lua-Error", 	"Unable to set the scale of the current map,\n" +
											"no map has been loaded yet!");

			PlayState.current_map.setScale(scale);
			return NIL;
		}
	}
	
	public static class SetPosition extends TwoArgFunction {

		@Override
		public LuaValue call(LuaValue arg0, LuaValue arg1) {
			int x = arg0.checkint();
			int y = arg1.checkint();
			
			PlayState.current_map.setPosition(x, y);

			if (PlayState.current_map == null)
				Dialog.error("Lua-Error", 	"Unable to get the position of the current map,\n" +
											"no map has been loaded yet!");
			
			return NIL;
		}
	}
	
	public static class GetX extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			if (PlayState.current_map == null)
				Dialog.error("Lua-Error", 	"Unable to get x-position of the current map,\n" +
											"no map has been loaded yet!");

			return LuaValue.valueOf(PlayState.current_map.getX());
		}
	}
	
	public static class GetY extends ZeroArgFunction {

		@Override
		public LuaValue call() {
			if (PlayState.current_map == null)
				Dialog.error("Lua-Error",	"Unable to get the y-position of the current map,\n" +
											"no map has been loaded yet!");

			return LuaValue.valueOf(PlayState.current_map.getY());
		}
	}
}
