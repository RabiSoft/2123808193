package RabiSoft.android;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeLength implements Parcelable {

    public static final String m_actionPick = "RabiSoft.intent.action.PICK_TIME_LENGTH";

    public static final String m_typeSecond = "TimeLength/second";
    public static final String m_typeMinute = "TimeLength/minute";

    public static final String m_keyExtra_Current = "current";

    long m_time;

    public static final Creator<TimeLength> CREATOR = new Creator<TimeLength>() {

        public TimeLength createFromParcel(Parcel in) {
            return new TimeLength(in);
        }

        public TimeLength[] newArray(int size) {
            return new TimeLength[size];
        }

    };

    public TimeLength() {
        // do nothing.
    }

    protected TimeLength(Parcel in) {
        m_time = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_time);
    }

    public long toSeconds() {
        long value = m_time / 1000L;
        return value;
    }

    public long toMinutes() {
        long value = m_time / ( 60L * 1000L );
        return value;
    }

    public static TimeLength fromSeconds(long seconds) {
        long milliseconds = seconds * 1000L;
        TimeLength length = new TimeLength();
        length.m_time = milliseconds;
        return length;
    }

    public static TimeLength fromMinutes(long minutes) {
        long milliseconds = minutes * 60L * 1000L;
        TimeLength length = new TimeLength();
        length.m_time = milliseconds;
        return length;
    }

}
