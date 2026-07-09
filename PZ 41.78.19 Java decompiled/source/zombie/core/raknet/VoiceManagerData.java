// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.raknet;

import java.util.ArrayList;
import zombie.radio.devices.DeviceData;

public class VoiceManagerData {
    public static ArrayList<VoiceManagerData> data = new ArrayList<>();
    public long userplaychannel;
    public long userplaysound;
    public boolean userplaymute;
    public long voicetimeout;
    public final ArrayList<VoiceManagerData.RadioData> radioData = new ArrayList<>();
    public boolean isCanHearAll;
    short index;

    public VoiceManagerData(short short0) {
        this.userplaymute = false;
        this.userplaychannel = 0L;
        this.userplaysound = 0L;
        this.voicetimeout = 0L;
        this.index = short0;
    }

    public static VoiceManagerData get(short short0) {
        if (data.size() <= short0) {
            for (short short1 = (short)data.size(); short1 <= short0; short1++) {
                VoiceManagerData voiceManagerData0 = new VoiceManagerData(short1);
                data.add(voiceManagerData0);
            }
        }

        VoiceManagerData voiceManagerData1 = data.get(short0);
        if (voiceManagerData1 == null) {
            voiceManagerData1 = new VoiceManagerData(short0);
            data.set(short0, voiceManagerData1);
        }

        return voiceManagerData1;
    }

    public static class RadioData {
        DeviceData deviceData;
        public int freq;
        public float distance;
        public short x;
        public short y;
        float lastReceiveDistance;

        public RadioData(float float0, float float1, float float2) {
            this(null, 0, float0, float1, float2);
        }

        public RadioData(int int0, float float0, float float1, float float2) {
            this(null, int0, float0, float1, float2);
        }

        public RadioData(DeviceData deviceDatax, float float0, float float1) {
            this(deviceDatax, deviceDatax.getChannel(), deviceDatax.getMicIsMuted() ? 0.0F : deviceDatax.getTransmitRange(), float0, float1);
        }

        private RadioData(DeviceData deviceDatax, int int0, float float0, float float1, float float2) {
            this.deviceData = deviceDatax;
            this.freq = int0;
            this.distance = float0;
            this.x = (short)float1;
            this.y = (short)float2;
        }

        public boolean isTransmissionAvailable() {
            return this.freq != 0
                && this.deviceData != null
                && this.deviceData.getIsTurnedOn()
                && this.deviceData.getIsTwoWay()
                && !this.deviceData.isNoTransmit()
                && !this.deviceData.getMicIsMuted();
        }

        public boolean isReceivingAvailable(int int0) {
            return this.freq != 0
                && this.deviceData != null
                && this.deviceData.getIsTurnedOn()
                && this.deviceData.getChannel() == int0
                && this.deviceData.getDeviceVolume() != 0.0F
                && !this.deviceData.isPlayingMedia();
        }

        public DeviceData getDeviceData() {
            return this.deviceData;
        }
    }

    public static enum VoiceDataSource {
        Unknown,
        Voice,
        Radio,
        Cheat;
    }
}
