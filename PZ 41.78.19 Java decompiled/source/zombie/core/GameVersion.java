// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.core.math.PZMath;

public final class GameVersion {
    private final int m_major;
    private final int m_minor;
    private final String m_suffix;
    private final String m_string;

    public GameVersion(int major, int minor, String suffix) {
        if (major < 0) {
            throw new IllegalArgumentException("major version must be greater than zero");
        } else if (minor >= 0 && minor <= 999) {
            this.m_major = major;
            this.m_minor = minor;
            this.m_suffix = suffix;
            this.m_string = String.format(Locale.ENGLISH, "%d.%d%s", this.m_major, this.m_minor, this.m_suffix == null ? "" : this.m_suffix);
        } else {
            throw new IllegalArgumentException("minor version must be from 0 to 999");
        }
    }

    public int getMajor() {
        return this.m_major;
    }

    public int getMinor() {
        return this.m_minor;
    }

    public String getSuffix() {
        return this.m_suffix;
    }

    public int getInt() {
        return this.m_major * 1000 + this.m_minor;
    }

    public boolean isGreaterThan(GameVersion rhs) {
        return this.getInt() > rhs.getInt();
    }

    public boolean isGreaterThanOrEqualTo(GameVersion rhs) {
        return this.getInt() >= rhs.getInt();
    }

    public boolean isLessThan(GameVersion rhs) {
        return this.getInt() < rhs.getInt();
    }

    public boolean isLessThanOrEqualTo(GameVersion rhs) {
        return this.getInt() <= rhs.getInt();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return !(obj instanceof GameVersion gameVersion) ? false : gameVersion.m_major == this.m_major && gameVersion.m_minor == this.m_minor;
        }
    }

    @Override
    public String toString() {
        return this.m_string;
    }

    public static GameVersion parse(String str) {
        Matcher matcher = Pattern.compile("([0-9]+)\\.([0-9]+)(.*)").matcher(str);
        if (matcher.matches()) {
            int int0 = PZMath.tryParseInt(matcher.group(1), 0);
            int int1 = PZMath.tryParseInt(matcher.group(2), 0);
            String string = matcher.group(3);
            return new GameVersion(int0, int1, string);
        } else {
            throw new IllegalArgumentException("invalid game version \"" + str + "\"");
        }
    }
}
