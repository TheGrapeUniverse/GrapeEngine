package at.dalex.grape.script;

import java.awt.Color;
import java.io.File;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.renderer.graphicsutil.Graphics;
import at.dalex.grape.renderer.graphicsutil.Image;
import at.dalex.grape.renderer.graphicsutil.ImageUtils;

class LuaGraphics {

	public static class LuaFillRectangle extends VarArgFunction {
		@Override
		public Varargs onInvoke(Varargs args) {
			int x = args.checkint(1);
			int y = args.checkint(2);
			int width = args.checkint(3);
			int height = args.checkint(4);
			
			Graphics.fillRectangle(x, y, width, height, Color.WHITE, GrapeEngine.getEngine().getCamera().getProjectionAndViewMatrix());
			return NIL;
		}
	}
	
	public static class LuaLoadImage extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue arg0) {
			String path = arg0.checkjstring(1);
			if (path != null) {
				Image loadedImage = ImageUtils.loadImage(new File(path));
				return CoerceJavaToLua.coerce(loadedImage);
			}
			return NIL;
		}
	}
	
	public static class LuaDrawImage extends VarArgFunction {
		@Override
		public Varargs onInvoke(Varargs args) {
			LuaValue imageValue = args.checknotnil(1);
			int x = args.checkint(2);
			int y = args.checkint(3);
			int width = args.checkint(4);
			int height = args.checkint(5);
			
			Image image = (Image) CoerceLuaToJava.coerce(imageValue, Image.class);
			Graphics.drawImage(image, x, y, width, height, GrapeEngine.getEngine().getCamera().getProjectionAndViewMatrix());
			
			return NIL;
		}
	}
}
