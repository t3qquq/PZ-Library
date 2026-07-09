// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.secure;

import org.junit.Assert;
import org.junit.Test;

public class PZCryptTest extends Assert {
    @Test
    public void hash() {
        String string = PZcrypt.hash("123456");
        assertEquals("$2a$12$O/BFHoDFPrfFaNPAACmWpuPkOtwkznuRQ7saS6/ouHjTT9KuVcKfq", string);
    }

    @Test
    public void hashSalt() {
        String string0 = PZcrypt.hashSalt("1234567");
        String string1 = PZcrypt.hashSalt("1234567");
        assertNotEquals(string0, string1);
        boolean boolean0 = PZcrypt.checkHashSalt(string0, "1234567");
        assertEquals(true, boolean0);
        boolean0 = PZcrypt.checkHashSalt(string0, "1238567");
        assertEquals(false, boolean0);
        boolean0 = PZcrypt.checkHashSalt(string1, "1234567");
        assertEquals(true, boolean0);
        boolean0 = PZcrypt.checkHashSalt(string1, "dnfgdf;godf;ogdogi;");
        assertEquals(false, boolean0);
    }
}
