package at.dalex.grape.script;

import java.awt.Font;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import at.dalex.grape.graphics.TrueTypeFont;

class LuaFont {

	public static class LuaCreateFont extends ThreeArgFunction {

		@Override
		public LuaValue call(LuaValue arg0, LuaValue arg1, LuaValue arg2) {
			String fontName = arg0.checkjstring();
			int fontSize = arg1.checkint();
			boolean antiAlias = arg2.checkboolean();
			TrueTypeFont font = new TrueTypeFont(new Font(fontName, Font.PLAIN, fontSize), antiAlias);
			return CoerceJavaToLua.coerce(font);
		}
	}
}
