// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AltCommandArgs.class)
public @interface CommandArgs {
    String DEFAULT_OPTIONAL_ARGUMENT = "no value";

    String[] required() default {};

    String optional() default "no value";

    String argName() default "NO_ARGUMENT_NAME";

    boolean varArgs() default false;
}
