package at.dalex.grape.script;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.graphics.Timer;

class LuaTimer {

	public static class LuaGetTime extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			return LuaValue.valueOf(GrapeEngine.getEngine().getDisplayManager().getTimer().getTime());
		}
	}

	public static class LuaGetDelta extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			Timer timer = GrapeEngine.getEngine().getDisplayManager().getTimer();
			return LuaValue.valueOf(timer.getDelta());
		}
	}
	
	public static class LuaGetFPS extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			Timer timer = GrapeEngine.getEngine().getDisplayManager().getTimer();
			return LuaValue.valueOf(timer.getFPS());
		}
	}
	
	public static class LuaGetUPS extends ZeroArgFunction {
		@Override
		public LuaValue call() {
			Timer timer = GrapeEngine.getEngine().getDisplayManager().getTimer();
			return LuaValue.valueOf(timer.getUPS());
		}
	}
}
