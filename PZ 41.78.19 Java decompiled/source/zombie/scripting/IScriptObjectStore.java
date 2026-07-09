// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting;

import zombie.scripting.objects.Item;
import zombie.scripting.objects.Recipe;

public interface IScriptObjectStore {
    Item getItem(String name);

    Recipe getRecipe(String name);
}
