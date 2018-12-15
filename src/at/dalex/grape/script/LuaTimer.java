package at.dalex.grape.script;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.renderer.Timer;

class LuaTimer {

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
