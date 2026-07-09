// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import zombie.scripting.ScriptManager;

public final class VehicleTemplate extends BaseScriptObject {
    public String name;
    public String body;
    public VehicleScript script;

    public VehicleTemplate(ScriptModule module, String _name, String _body) {
        ScriptManager scriptManager = ScriptManager.instance;
        if (!scriptManager.scriptsWithVehicleTemplates.contains(scriptManager.currentFileName)) {
            scriptManager.scriptsWithVehicleTemplates.add(scriptManager.currentFileName);
        }

        this.module = module;
        this.name = _name;
        this.body = _body;
    }

    public VehicleScript getScript() {
        if (this.script == null) {
            this.script = new VehicleScript();
            this.script.module = this.getModule();
            this.script.Load(this.name, this.body);
        }

        return this.script;
    }
}
