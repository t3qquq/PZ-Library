// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.itemConfig.generators;

import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.entity.GameEntity;
import zombie.entity.components.attributes.AttributeInstance;
import zombie.entity.components.attributes.AttributeType;
import zombie.entity.components.attributes.AttributeValueType;
import zombie.scripting.itemConfig.RandomGenerator;

public class GeneratorEnumStringSetAttribute extends RandomGenerator<GeneratorEnumStringSetAttribute> {
    private final AttributeType.EnumStringSet attributeType;
    private final String[] enumsValues;
    private final String[] stringValues;
    private final GeneratorEnumStringSetAttribute.Mode mode;

    public GeneratorEnumStringSetAttribute(AttributeType attributeType, GeneratorEnumStringSetAttribute.Mode mode, String[] enums, String[] strings) {
        this(attributeType, mode, 1.0F, enums, strings);
    }

    public GeneratorEnumStringSetAttribute(
        AttributeType attributeType, GeneratorEnumStringSetAttribute.Mode mode, float chance, String[] enums, String[] strings
    ) {
        if (chance < 0.0F) {
            throw new IllegalArgumentException("Chance may not be <= 0.");
        }

        if (attributeType instanceof AttributeType.EnumStringSet enumStringSet) {
            this.attributeType = enumStringSet;
            this.setChance(chance);
            this.enumsValues = enums;
            this.stringValues = strings;
            this.mode = mode;
        } else {
            throw new IllegalArgumentException("AttributeType valueType should be EnumStringSet.");
        }
    }

    @Override
    public boolean execute(GameEntity entity) {
        if (entity.getAttributes() == null || this.attributeType.getValueType() != AttributeValueType.EnumSet) {
            return false;
        }

        if (entity.getAttributes().contains(this.attributeType)) {
            try {
                AttributeInstance.EnumStringSet enumStringSet = (AttributeInstance.EnumStringSet)entity.getAttributes().getAttribute(this.attributeType);
                if (this.mode == GeneratorEnumStringSetAttribute.Mode.Set) {
                    enumStringSet.clear();
                }

                if (this.mode == GeneratorEnumStringSetAttribute.Mode.Remove) {
                    if (this.enumsValues != null) {
                        for (String s : this.enumsValues) {
                            if (!enumStringSet.removeEnumValueFromString(s)) {
                                DebugType.General.error("Unable to remove value '" + s + "'");
                            }
                        }
                    }

                    if (this.stringValues != null) {
                        for (String s : this.stringValues) {
                            if (!enumStringSet.removeStringValue(s)) {
                                DebugType.General.error("Unable to remove value '" + s + "'");
                            }
                        }
                    }
                } else {
                    if (this.enumsValues != null) {
                        for (String s : this.enumsValues) {
                            enumStringSet.addEnumValueFromString(s);
                        }
                    }

                    if (this.stringValues != null) {
                        for (String s : this.stringValues) {
                            enumStringSet.addStringValue(s);
                        }
                    }
                }
            } catch (Exception e) {
                DebugType.General.printException(e, LogSeverity.Error);
            }

            return true;
        } else {
            return false;
        }
    }

    public GeneratorEnumStringSetAttribute copy() {
        return new GeneratorEnumStringSetAttribute(this.attributeType, this.mode, this.getChance(), this.enumsValues, this.stringValues);
    }

    public enum Mode {
        Set,
        Add,
        Remove;
    }
}
