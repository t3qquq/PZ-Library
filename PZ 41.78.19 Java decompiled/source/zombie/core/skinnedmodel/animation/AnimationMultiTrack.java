// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEMMYMAIN on 28/02/2015.
 */
public final class AnimationMultiTrack {
    private final ArrayList<AnimationTrack> m_tracks = new ArrayList<>();
    private static final ArrayList<AnimationTrack> tempTracks = new ArrayList<>();

    public AnimationTrack findTrack(String trackName) {
        int int0 = 0;

        for (int int1 = this.m_tracks.size(); int0 < int1; int0++) {
            AnimationTrack animationTrack = this.m_tracks.get(int0);
            if (animationTrack.name.equals(trackName)) {
                return animationTrack;
            }
        }

        return null;
    }

    public void addTrack(AnimationTrack track) {
        this.m_tracks.add(track);
    }

    public void removeTrack(AnimationTrack track) {
        int int0 = this.getIndexOfTrack(track);
        if (int0 > -1) {
            this.removeTrackAt(int0);
        }
    }

    public void removeTracks(List<AnimationTrack> tracks) {
        tempTracks.clear();
        tempTracks.addAll(tracks);

        for (int int0 = 0; int0 < tempTracks.size(); int0++) {
            this.removeTrack(tempTracks.get(int0));
        }
    }

    public void removeTrackAt(int indexOf) {
        this.m_tracks.remove(indexOf).release();
    }

    public int getIndexOfTrack(AnimationTrack track) {
        if (track == null) {
            return -1;
        } else {
            int int0 = -1;

            for (int int1 = 0; int1 < this.m_tracks.size(); int1++) {
                AnimationTrack animationTrack = this.m_tracks.get(int1);
                if (animationTrack == track) {
                    int0 = int1;
                    break;
                }
            }

            return int0;
        }
    }

    public void Update(float time) {
        for (int int0 = 0; int0 < this.m_tracks.size(); int0++) {
            AnimationTrack animationTrack = this.m_tracks.get(int0);
            animationTrack.Update(time);
            if (animationTrack.CurrentClip == null) {
                this.removeTrackAt(int0);
                int0--;
            }
        }
    }

    public float getDuration() {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.m_tracks.size(); int0++) {
            AnimationTrack animationTrack = this.m_tracks.get(int0);
            float float1 = animationTrack.getDuration();
            if (animationTrack.CurrentClip != null && float1 > float0) {
                float0 = float1;
            }
        }

        return float0;
    }

    public void reset() {
        int int0 = 0;

        for (int int1 = this.m_tracks.size(); int0 < int1; int0++) {
            AnimationTrack animationTrack = this.m_tracks.get(int0);
            animationTrack.reset();
        }

        AnimationPlayer.releaseTracks(this.m_tracks);
        this.m_tracks.clear();
    }

    public List<AnimationTrack> getTracks() {
        return this.m_tracks;
    }

    public int getTrackCount() {
        return this.m_tracks.size();
    }

    public AnimationTrack getTrackAt(int i) {
        return this.m_tracks.get(i);
    }
}
