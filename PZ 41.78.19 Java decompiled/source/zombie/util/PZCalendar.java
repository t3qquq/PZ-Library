// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public final class PZCalendar {
    private final Calendar calendar;

    public static PZCalendar getInstance() {
        return new PZCalendar(Calendar.getInstance());
    }

    public PZCalendar(Calendar _calendar) {
        Objects.requireNonNull(_calendar);
        this.calendar = _calendar;
    }

    public void set(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this.calendar.set(year, month, dayOfMonth, hourOfDay, minute);
    }

    public void setTimeInMillis(long millis) {
        this.calendar.setTimeInMillis(millis);
    }

    public int get(int field) {
        return this.calendar.get(field);
    }

    public final Date getTime() {
        return this.calendar.getTime();
    }

    public long getTimeInMillis() {
        return this.calendar.getTimeInMillis();
    }

    public boolean isLeapYear(int year) {
        return ((GregorianCalendar)this.calendar).isLeapYear(year);
    }
}
