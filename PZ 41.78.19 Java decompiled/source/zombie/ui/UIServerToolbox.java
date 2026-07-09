// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.util.ArrayList;
import java.util.Objects;
import zombie.core.Translator;
import zombie.core.znet.SteamUtils;
import zombie.network.CoopMaster;
import zombie.network.ICoopServerMessageListener;

public final class UIServerToolbox extends NewWindow implements ICoopServerMessageListener, UIEventHandler {
    public static UIServerToolbox instance;
    ScrollBar ScrollBarV;
    UITextBox2 OutputLog;
    private final ArrayList<String> incomingConnections = new ArrayList<>();
    DialogButton buttonAccept;
    DialogButton buttonReject;
    private String externalAddress = null;
    private String steamID = null;
    public boolean autoAccept = false;

    public UIServerToolbox(int int0, int int1) {
        super(int0, int1, 10, 10, true);
        this.ResizeToFitY = false;
        this.visible = true;
        if (instance != null) {
            instance.shutdown();
        }

        instance = this;
        this.width = 340.0F;
        this.height = 325.0F;
        byte byte0 = 6;
        byte byte1 = 37;
        this.OutputLog = new UITextBox2(UIFont.Small, 5, 33, 330, 260, Translator.getText("IGUI_ServerToolBox_Status"), true);
        this.OutputLog.multipleLine = true;
        this.ScrollBarV = new ScrollBar(
            "ServerToolboxScrollbar",
            this,
            (int)(this.OutputLog.getX() + this.OutputLog.getWidth()) - 14,
            this.OutputLog.getY().intValue() + 4,
            this.OutputLog.getHeight().intValue() - 8,
            true
        );
        this.ScrollBarV.SetParentTextBox(this.OutputLog);
        this.AddChild(this.OutputLog);
        this.AddChild(this.ScrollBarV);
        this.buttonAccept = new DialogButton(this, 30, 225, Translator.getText("IGUI_ServerToolBox_acccept"), "accept");
        this.buttonReject = new DialogButton(this, 80, 225, Translator.getText("IGUI_ServerToolBox_reject"), "reject");
        this.AddChild(this.buttonAccept);
        this.AddChild(this.buttonReject);
        this.buttonAccept.setVisible(false);
        this.buttonReject.setVisible(false);
        this.PrintLine("\n");
        if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
            CoopMaster.instance.addListener(this);
            CoopMaster.instance
                .invokeServer(
                    "get-parameter",
                    "external-ip",
                    new ICoopServerMessageListener() {
                        @Override
                        public void OnCoopServerMessage(String var1, String var2, String string0) {
                            UIServerToolbox.this.externalAddress = string0;
                            String string1 = "null".equals(UIServerToolbox.this.externalAddress)
                                ? Translator.getText("IGUI_ServerToolBox_IPUnknown")
                                : UIServerToolbox.this.externalAddress;
                            UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_ServerAddress", string1));
                            UIServerToolbox.this.PrintLine("");
                        }
                    }
                );
            if (SteamUtils.isSteamModeEnabled()) {
                CoopMaster.instance.invokeServer("get-parameter", "steam-id", new ICoopServerMessageListener() {
                    @Override
                    public void OnCoopServerMessage(String var1, String var2, String string) {
                        UIServerToolbox.this.steamID = string;
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_SteamID", UIServerToolbox.this.steamID));
                        UIServerToolbox.this.PrintLine("");
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite1"));
                        UIServerToolbox.this.PrintLine("");
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite2"));
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite3"));
                        UIServerToolbox.this.PrintLine("");
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite4"));
                        UIServerToolbox.this.PrintLine("");
                        UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite5"));
                    }
                });
            }
        }
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            super.render();
            this.DrawTextCentre(Translator.getText("IGUI_ServerToolBox_Title"), this.getWidth() / 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
            String string0 = "null".equals(this.externalAddress) ? Translator.getText("IGUI_ServerToolBox_IPUnknown") : this.externalAddress;
            this.DrawText(Translator.getText("IGUI_ServerToolBox_ExternalIP", string0), 7.0, 19.0, 0.7F, 0.7F, 1.0, 1.0);
            if (!this.incomingConnections.isEmpty()) {
                String string1 = this.incomingConnections.get(0);
                if (string1 != null) {
                    this.DrawText(Translator.getText("IGUI_ServerToolBox_UserConnecting", string1), 10.0, 205.0, 0.7F, 0.7F, 1.0, 1.0);
                }
            }
        }
    }

    @Override
    public void update() {
        if (this.isVisible()) {
            if (this.incomingConnections.isEmpty()) {
                this.buttonReject.setVisible(false);
                this.buttonAccept.setVisible(false);
            } else {
                this.buttonReject.setVisible(true);
                this.buttonAccept.setVisible(true);
            }

            super.update();
        }
    }

    void UpdateViewPos() {
        this.OutputLog.TopLineIndex = this.OutputLog.Lines.size() - this.OutputLog.NumVisibleLines;
        if (this.OutputLog.TopLineIndex < 0) {
            this.OutputLog.TopLineIndex = 0;
        }

        this.ScrollBarV.scrollToBottom();
    }

    @Override
    public synchronized void OnCoopServerMessage(String string0, String var2, String string1) {
        if (Objects.equals(string0, "login-attempt")) {
            this.PrintLine(string1 + " is connecting");
            if (this.autoAccept) {
                this.PrintLine("Accepted connection from " + string1);
                CoopMaster.instance.sendMessage("approve-login-attempt", string1);
            } else {
                this.incomingConnections.add(string1);
                this.setVisible(true);
            }
        }
    }

    void PrintLine(String string) {
        this.OutputLog.SetText(this.OutputLog.Text + string + "\n");
        this.UpdateViewPos();
    }

    public void shutdown() {
        if (CoopMaster.instance != null) {
            CoopMaster.instance.removeListener(this);
        }
    }

    @Override
    public void DoubleClick(String var1, int var2, int var3) {
    }

    @Override
    public void ModalClick(String var1, String var2) {
    }

    @Override
    public void Selected(String string0, int var2, int var3) {
        if (Objects.equals(string0, "accept")) {
            String string1 = this.incomingConnections.get(0);
            this.incomingConnections.remove(0);
            this.PrintLine("Accepted connection from " + string1);
            CoopMaster.instance.sendMessage("approve-login-attempt", string1);
        }

        if (Objects.equals(string0, "reject")) {
            String string2 = this.incomingConnections.get(0);
            this.incomingConnections.remove(0);
            this.PrintLine("Rejected connection from " + string2);
            CoopMaster.instance.sendMessage("reject-login-attempt", string2);
        }
    }
}
