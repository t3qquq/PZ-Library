// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting;

import zombie.scripting.objects.Item;
import zombie.scripting.objects.Recipe;

public interface IScriptObjectStore {
    Item getItem(String name);

    Recipe getRecipe(String name);
}
