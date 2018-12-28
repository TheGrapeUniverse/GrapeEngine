package at.dalex.grape.script;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.graphics.DisplayManager;

class LuaDisplay {

	public static class LuaGetWidth extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			return LuaValue.valueOf(DisplayManager.windowWidth);
		}
	}
	
	public static class LuaGetHeight extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			return LuaValue.valueOf(DisplayManager.windowHeight);
		}
	}
	
	public static class LuaSetVSync extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue vsync) {
			boolean enableVSync = vsync.checkboolean(1);
			GrapeEngine.getEngine().getDisplayManager().enableVsync(enableVSync);
			return NIL;
		}
	}
}
