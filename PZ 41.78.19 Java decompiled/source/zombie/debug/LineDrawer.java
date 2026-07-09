// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.PlayerCamera;
import zombie.iso.Vector2;

public final class LineDrawer {
    private static final long serialVersionUID = -8792265397633463907L;
    public static int red = 0;
    public static int green = 255;
    public static int blue = 0;
    public static int alpha = 255;
    static int idLayer = -1;
    static final ArrayList<LineDrawer.DrawableLine> lines = new ArrayList<>();
    static final ArrayDeque<LineDrawer.DrawableLine> pool = new ArrayDeque<>();
    private static int layer;
    static final Vector2 tempo = new Vector2();
    static final Vector2 tempo2 = new Vector2();

    static void DrawTexturedRect(Texture texture, float float0, float float1, float float2, float float3, int int0, float float6, float float7, float float8) {
        float0 = (int)float0;
        float1 = (int)float1;
        Vector2 vector0 = new Vector2(float0, float1);
        Vector2 vector1 = new Vector2(float0 + float2, float1);
        Vector2 vector2 = new Vector2(float0 + float2, float1 + float3);
        Vector2 vector3 = new Vector2(float0, float1 + float3);
        Vector2 vector4 = new Vector2(IsoUtils.XToScreen(vector0.x, vector0.y, int0, 0), IsoUtils.YToScreen(vector0.x, vector0.y, int0, 0));
        Vector2 vector5 = new Vector2(IsoUtils.XToScreen(vector1.x, vector1.y, int0, 0), IsoUtils.YToScreen(vector1.x, vector1.y, int0, 0));
        Vector2 vector6 = new Vector2(IsoUtils.XToScreen(vector2.x, vector2.y, int0, 0), IsoUtils.YToScreen(vector2.x, vector2.y, int0, 0));
        Vector2 vector7 = new Vector2(IsoUtils.XToScreen(vector3.x, vector3.y, int0, 0), IsoUtils.YToScreen(vector3.x, vector3.y, int0, 0));
        PlayerCamera playerCamera = IsoCamera.cameras[IsoPlayer.getPlayerIndex()];
        vector4.x = vector4.x - playerCamera.OffX;
        vector5.x = vector5.x - playerCamera.OffX;
        vector6.x = vector6.x - playerCamera.OffX;
        vector7.x = vector7.x - playerCamera.OffX;
        vector4.y = vector4.y - playerCamera.OffY;
        vector5.y = vector5.y - playerCamera.OffY;
        vector6.y = vector6.y - playerCamera.OffY;
        vector7.y = vector7.y - playerCamera.OffY;
        float float4 = -240.0F;
        float4 -= 128.0F;
        float float5 = -32.0F;
        vector4.y -= float4;
        vector5.y -= float4;
        vector6.y -= float4;
        vector7.y -= float4;
        vector4.x -= float5;
        vector5.x -= float5;
        vector6.x -= float5;
        vector7.x -= float5;
        SpriteRenderer.instance
            .renderdebug(
                texture,
                vector4.x,
                vector4.y,
                vector5.x,
                vector5.y,
                vector6.x,
                vector6.y,
                vector7.x,
                vector7.y,
                float6,
                float7,
                float8,
                1.0F,
                float6,
                float7,
                float8,
                1.0F,
                float6,
                float7,
                float8,
                1.0F,
                float6,
                float7,
                float8,
                1.0F,
                null
            );
    }

    static void DrawIsoLine(float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7, int int0) {
        tempo.set(float0, float1);
        tempo2.set(float2, float3);
        Vector2 vector0 = new Vector2(IsoUtils.XToScreen(tempo.x, tempo.y, 0.0F, 0), IsoUtils.YToScreen(tempo.x, tempo.y, 0.0F, 0));
        Vector2 vector1 = new Vector2(IsoUtils.XToScreen(tempo2.x, tempo2.y, 0.0F, 0), IsoUtils.YToScreen(tempo2.x, tempo2.y, 0.0F, 0));
        vector0.x = vector0.x - IsoCamera.getOffX();
        vector1.x = vector1.x - IsoCamera.getOffX();
        vector0.y = vector0.y - IsoCamera.getOffY();
        vector1.y = vector1.y - IsoCamera.getOffY();
        drawLine(vector0.x, vector0.y, vector1.x, vector1.y, float4, float5, float6, float7, int0);
    }

    public static void DrawIsoRect(float float0, float float1, float float2, float float3, int int0, float float4, float float5, float float6) {
        DrawIsoRect(float0, float1, float2, float3, int0, 0, float4, float5, float6);
    }

    public static void DrawIsoRect(float float1, float float3, float float0, float float2, int int0, int int1, float float13, float float14, float float15) {
        if (float0 < 0.0F) {
            float0 = -float0;
            float1 -= float0;
        }

        if (float2 < 0.0F) {
            float2 = -float2;
            float3 -= float2;
        }

        float float4 = IsoUtils.XToScreenExact(float1, float3, int0, 0);
        float float5 = IsoUtils.YToScreenExact(float1, float3, int0, 0);
        float float6 = IsoUtils.XToScreenExact(float1 + float0, float3, int0, 0);
        float float7 = IsoUtils.YToScreenExact(float1 + float0, float3, int0, 0);
        float float8 = IsoUtils.XToScreenExact(float1 + float0, float3 + float2, int0, 0);
        float float9 = IsoUtils.YToScreenExact(float1 + float0, float3 + float2, int0, 0);
        float float10 = IsoUtils.XToScreenExact(float1, float3 + float2, int0, 0);
        float float11 = IsoUtils.YToScreenExact(float1, float3 + float2, int0, 0);
        float float12 = -int1 * Core.TileScale;
        float5 += float12;
        float7 += float12;
        float9 += float12;
        float11 += float12;
        drawLine(float4, float5, float6, float7, float13, float14, float15);
        drawLine(float6, float7, float8, float9, float13, float14, float15);
        drawLine(float8, float9, float10, float11, float13, float14, float15);
        drawLine(float10, float11, float4, float5, float13, float14, float15);
    }

    public static void DrawIsoRectRotated(
        float float4, float float6, float float17, float float2, float float1, float float0, float float18, float float19, float float20, float float21
    ) {
        Vector2 vector0 = tempo.setLengthAndDirection(float0, 1.0F);
        Vector2 vector1 = tempo2.set(vector0);
        vector1.tangent();
        vector0.x *= float1 / 2.0F;
        vector0.y *= float1 / 2.0F;
        vector1.x *= float2 / 2.0F;
        vector1.y *= float2 / 2.0F;
        float float3 = float4 + vector0.x;
        float float5 = float6 + vector0.y;
        float float7 = float4 - vector0.x;
        float float8 = float6 - vector0.y;
        float float9 = float3 - vector1.x;
        float float10 = float5 - vector1.y;
        float float11 = float3 + vector1.x;
        float float12 = float5 + vector1.y;
        float float13 = float7 - vector1.x;
        float float14 = float8 - vector1.y;
        float float15 = float7 + vector1.x;
        float float16 = float8 + vector1.y;
        byte byte0 = 1;
        DrawIsoLine(float9, float10, float17, float11, float12, float17, float18, float19, float20, float21, byte0);
        DrawIsoLine(float9, float10, float17, float13, float14, float17, float18, float19, float20, float21, byte0);
        DrawIsoLine(float11, float12, float17, float15, float16, float17, float18, float19, float20, float21, byte0);
        DrawIsoLine(float13, float14, float17, float15, float16, float17, float18, float19, float20, float21, byte0);
    }

    public static void DrawIsoLine(
        float float1,
        float float2,
        float float3,
        float float6,
        float float7,
        float float8,
        float float10,
        float float11,
        float float12,
        float float13,
        int int0
    ) {
        float float0 = IsoUtils.XToScreenExact(float1, float2, float3, 0);
        float float4 = IsoUtils.YToScreenExact(float1, float2, float3, 0);
        float float5 = IsoUtils.XToScreenExact(float6, float7, float8, 0);
        float float9 = IsoUtils.YToScreenExact(float6, float7, float8, 0);
        drawLine(float0, float4, float5, float9, float10, float11, float12, float13, int0);
    }

    public static void DrawIsoTransform(
        float float0,
        float float1,
        float float2,
        float float9,
        float float8,
        float float3,
        int int0,
        float float4,
        float float5,
        float float6,
        float float7,
        int int1
    ) {
        DrawIsoCircle(float0, float1, float2, float3, int0, float4, float5, float6, float7);
        DrawIsoLine(float0, float1, float2, float0 + float9 + float3 / 2.0F, float1 + float8 + float3 / 2.0F, float2, float4, float5, float6, float7, int1);
    }

    public static void DrawIsoCircle(float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
        byte byte0 = 16;
        DrawIsoCircle(float0, float1, float2, float3, byte0, float4, float5, float6, float7);
    }

    public static void DrawIsoCircle(float float1, float float2, float float3, float float0, int int0, float float4, float float5, float float6, float float7) {
        double double0 = float1 + float0 * Math.cos(Math.toRadians(0.0F / int0));
        double double1 = float2 + float0 * Math.sin(Math.toRadians(0.0F / int0));

        for (int int1 = 1; int1 <= int0; int1++) {
            double double2 = float1 + float0 * Math.cos(Math.toRadians(int1 * 360.0F / int0));
            double double3 = float2 + float0 * Math.sin(Math.toRadians(int1 * 360.0F / int0));
            addLine((float)double0, (float)double1, float3, (float)double2, (float)double3, float3, float4, float5, float6, float7);
            double0 = double2;
            double1 = double3;
        }
    }

    static void drawLine(float float6, float float5, float float4, float float3, float float0, float float1, float float2) {
        SpriteRenderer.instance.renderline(null, (int)float6, (int)float5, (int)float4, (int)float3, float0, float1, float2, 1.0F);
    }

    public static void drawLine(float float7, float float6, float float5, float float4, float float0, float float1, float float2, float float3, int var8) {
        SpriteRenderer.instance.renderline(null, (int)float7, (int)float6, (int)float5, (int)float4, float0, float1, float2, float3);
    }

    public static void drawRect(float float0, float float6, float float7, float float5, float float1, float float2, float float3, float float4, int int0) {
        SpriteRenderer.instance.render(null, float0, float6 + int0, int0, float5 - int0 * 2, float1, float2, float3, float4, null);
        SpriteRenderer.instance.render(null, float0, float6, float7, int0, float1, float2, float3, float4, null);
        SpriteRenderer.instance.render(null, float0 + float7 - int0, float6 + int0, 1.0F, float5 - int0 * 2, float1, float2, float3, float4, null);
        SpriteRenderer.instance.render(null, float0, float6 + float5 - int0, float7, int0, float1, float2, float3, float4, null);
    }

    public static void drawArc(
        float float5,
        float float8,
        float float12,
        float float6,
        float float1,
        float float2,
        int int1,
        float float13,
        float float14,
        float float15,
        float float16
    ) {
        float float0 = float1 + (float)Math.acos(float2);
        float float3 = float1 - (float)Math.acos(float2);
        float float4 = float5 + (float)Math.cos(float0) * float6;
        float float7 = float8 + (float)Math.sin(float0) * float6;

        for (int int0 = 1; int0 <= int1; int0++) {
            float float9 = float0 + (float3 - float0) * int0 / int1;
            float float10 = float5 + (float)Math.cos(float9) * float6;
            float float11 = float8 + (float)Math.sin(float9) * float6;
            DrawIsoLine(float4, float7, float12, float10, float11, float12, float13, float14, float15, float16, 1);
            float4 = float10;
            float7 = float11;
        }
    }

    public static void drawCircle(float float1, float float2, float float0, int int0, float float3, float float4, float float5) {
        double double0 = float1 + float0 * Math.cos(Math.toRadians(0.0F / int0));
        double double1 = float2 + float0 * Math.sin(Math.toRadians(0.0F / int0));

        for (int int1 = 1; int1 <= int0; int1++) {
            double double2 = float1 + float0 * Math.cos(Math.toRadians(int1 * 360.0F / int0));
            double double3 = float2 + float0 * Math.sin(Math.toRadians(int1 * 360.0F / int0));
            drawLine((float)double0, (float)double1, (float)double2, (float)double3, float3, float4, float5, 1.0F, 1);
            double0 = double2;
            double1 = double3;
        }
    }

    public static void drawDirectionLine(
        float float1, float float5, float float6, float float2, float float3, float float7, float float8, float float9, float float10, int int0
    ) {
        float float0 = float1 + (float)Math.cos(float3) * float2;
        float float4 = float5 + (float)Math.sin(float3) * float2;
        DrawIsoLine(float1, float5, float6, float0, float4, float6, float7, float8, float9, float10, int0);
    }

    public static void drawDotLines(
        float float0, float float1, float float2, float float3, float float8, float float9, float float4, float float5, float float6, float float7, int int0
    ) {
        drawDirectionLine(float0, float1, float2, float3, float8 + (float)Math.acos(float9), float4, float5, float6, float7, int0);
        drawDirectionLine(float0, float1, float2, float3, float8 - (float)Math.acos(float9), float4, float5, float6, float7, int0);
    }

    public static void addLine(
        float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9
    ) {
        LineDrawer.DrawableLine drawableLine = pool.isEmpty() ? new LineDrawer.DrawableLine() : pool.pop();
        lines.add(drawableLine.init(float0, float1, float2, float3, float4, float5, float6, float7, float8, float9));
    }

    public static void addLine(float float0, float float1, float float2, float float3, float float4, float float5, int int2, int int1, int int0, String string) {
        addLine(float0, float1, float2, float3, float4, float5, int2, int1, int0, string, true);
    }

    public static void addLine(
        float float0,
        float float1,
        float float2,
        float float3,
        float float4,
        float float5,
        float float6,
        float float7,
        float float8,
        String string,
        boolean boolean0
    ) {
        LineDrawer.DrawableLine drawableLine = pool.isEmpty() ? new LineDrawer.DrawableLine() : pool.pop();
        lines.add(drawableLine.init(float0, float1, float2, float3, float4, float5, float6, float7, float8, string, boolean0));
    }

    public static void addRect(float float0, float float1, float float2, float float7, float float6, float float3, float float4, float float5) {
        LineDrawer.DrawableLine drawableLine = pool.isEmpty() ? new LineDrawer.DrawableLine() : pool.pop();
        lines.add(drawableLine.init(float0, float1, float2, float0 + float7, float1 + float6, float2, float3, float4, float5, null, false));
    }

    public static void addRectYOffset(float float0, float float1, float float2, float float7, float float6, int int0, float float3, float float4, float float5) {
        LineDrawer.DrawableLine drawableLine = pool.isEmpty() ? new LineDrawer.DrawableLine() : pool.pop();
        lines.add(drawableLine.init(float0, float1, float2, float0 + float7, float1 + float6, float2, float3, float4, float5, null, false));
        drawableLine.yPixelOffset = int0;
    }

    public static void clear() {
        if (!lines.isEmpty()) {
            for (int int0 = 0; int0 < lines.size(); int0++) {
                pool.push(lines.get(int0));
            }

            lines.clear();
        }
    }

    public void removeLine(String string) {
        for (int int0 = 0; int0 < lines.size(); int0++) {
            if (lines.get(int0).name.equals(string)) {
                lines.remove(lines.get(int0));
                int0--;
            }
        }
    }

    public static void render() {
        for (int int0 = 0; int0 < lines.size(); int0++) {
            LineDrawer.DrawableLine drawableLine = lines.get(int0);
            if (!drawableLine.bLine) {
                DrawIsoRect(
                    drawableLine.xstart,
                    drawableLine.ystart,
                    drawableLine.xend - drawableLine.xstart,
                    drawableLine.yend - drawableLine.ystart,
                    (int)drawableLine.zstart,
                    drawableLine.yPixelOffset,
                    drawableLine.red,
                    drawableLine.green,
                    drawableLine.blue
                );
            } else {
                DrawIsoLine(
                    drawableLine.xstart,
                    drawableLine.ystart,
                    drawableLine.zstart,
                    drawableLine.xend,
                    drawableLine.yend,
                    drawableLine.zend,
                    drawableLine.red,
                    drawableLine.green,
                    drawableLine.blue,
                    drawableLine.alpha,
                    1
                );
            }
        }
    }

    public static void drawLines() {
        clear();
    }

    static class DrawableLine {
        public boolean bLine = false;
        String name;
        float red;
        float green;
        float blue;
        float alpha;
        float xstart;
        float ystart;
        float zstart;
        float xend;
        float yend;
        float zend;
        int yPixelOffset;

        public LineDrawer.DrawableLine init(
            float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, String string
        ) {
            this.xstart = float0;
            this.ystart = float1;
            this.zstart = float2;
            this.xend = float3;
            this.yend = float4;
            this.zend = float5;
            this.red = float6;
            this.green = float7;
            this.blue = float8;
            this.alpha = 1.0F;
            this.name = string;
            this.yPixelOffset = 0;
            return this;
        }

        public LineDrawer.DrawableLine init(
            float float0,
            float float1,
            float float2,
            float float3,
            float float4,
            float float5,
            float float6,
            float float7,
            float float8,
            String string,
            boolean boolean0
        ) {
            this.xstart = float0;
            this.ystart = float1;
            this.zstart = float2;
            this.xend = float3;
            this.yend = float4;
            this.zend = float5;
            this.red = float6;
            this.green = float7;
            this.blue = float8;
            this.alpha = 1.0F;
            this.name = string;
            this.bLine = boolean0;
            this.yPixelOffset = 0;
            return this;
        }

        public LineDrawer.DrawableLine init(
            float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9
        ) {
            this.xstart = float0;
            this.ystart = float1;
            this.zstart = float2;
            this.xend = float3;
            this.yend = float4;
            this.zend = float5;
            this.red = float6;
            this.green = float7;
            this.blue = float8;
            this.alpha = float9;
            this.name = null;
            this.bLine = true;
            this.yPixelOffset = 0;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof LineDrawer.DrawableLine ? ((LineDrawer.DrawableLine)object).name.equals(this.name) : object.equals(this);
        }
    }
}
