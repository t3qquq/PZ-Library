// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.text.templating;

import zombie.characters.IsoGameCharacter;

/**
 * TurboTuTone.  Example of ReplaceProvider that registers firstname and lastname keys for the supplied character.
 */
public class ReplaceProviderCharacter extends ReplaceProvider {
    public ReplaceProviderCharacter(final IsoGameCharacter character) {
        this.addReplacer(
            "firstname",
            new IReplace() {
                @Override
                public String getString() {
                    return character != null && character.getDescriptor() != null && character.getDescriptor().getForename() != null
                        ? character.getDescriptor().getForename()
                        : "Bob";
                }
            }
        );
        this.addReplacer(
            "lastname",
            new IReplace() {
                @Override
                public String getString() {
                    return character != null && character.getDescriptor() != null && character.getDescriptor().getSurname() != null
                        ? character.getDescriptor().getSurname()
                        : "Smith";
                }
            }
        );
    }
}
