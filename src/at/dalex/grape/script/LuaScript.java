package at.dalex.grape.script;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.toolbox.Dialog;
import at.dalex.grape.toolbox.FileUtil;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;

public class LuaScript {

    private String  scriptFile;
    private Globals globals;

    public LuaScript(String scriptFile) {
        this.scriptFile = scriptFile;
        globals = JsePlatform.standardGlobals();
        scriptFile = "" + scriptFile;

        if (FileUtil.isFile(new File(scriptFile))) {
            //Read Lua File
            globals.get("dofile").call(LuaValue.valueOf(scriptFile));

            //Load engine's interfaces for these globals
            GrapeEngine.getEngine().getLuaManager().setupAPI(globals);
        }
        else {
            Dialog.error("Error", "Unable to find entity script '" + scriptFile + "'!\n"
                    + "The entity will not function correctly.");
        }
    }

    /**
     * Returns a reference to a method which is located in this script.
     * If the Method could no be found, an {@link LuaError} is thrown.
     *
     * @param identifier The Method's name
     * @return The reference of the Method
     */
    public LuaValue getMethod(String identifier) {
        LuaValue method = null;
        try {
            //Try to find the method in this script.
            method = globals.get(identifier);
        } catch (LuaError e) {
            e.printStackTrace();
            Dialog.error("Error", "Method identified by name '" + identifier + "' could not be found\n"
                    + "in script '" + scriptFile + "'!");
        }
        return method;
    }

    public Globals getGlobals() {
        return this.globals;
    }
}
