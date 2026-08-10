// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.text.templating;

/**
 * TurboTuTone.  Interface that can provide IReplace for ITemplateBuilder.  Any keys present in a IReplaceProvider take priority when replacing keys in a template.   NOTE: When checking the key String, this should be equaling a lowercase version or equalsIgnoreCase
 */
public interface IReplaceProvider {
    boolean hasReplacer(String key);

    IReplace getReplacer(String key);
}
