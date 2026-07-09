// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.network.packets.INetworkPacket;

public class PlayerItem extends Instance implements INetworkPacket {
    protected int itemId;
    protected InventoryItem item;

    public void set(InventoryItem itemx) {
        super.set(itemx.getRegistry_id());
        this.item = itemx;
        this.itemId = this.item.getID();
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        boolean boolean0 = byteBuffer.get() == 1;
        if (boolean0) {
            this.ID = byteBuffer.getShort();
            byteBuffer.get();

            try {
                this.item = InventoryItemFactory.CreateItem(this.ID);
                if (this.item != null) {
                    this.item.load(byteBuffer, 195);
                }
            } catch (BufferUnderflowException | IOException iOException) {
                DebugLog.Multiplayer.printException(iOException, "Item load error", LogSeverity.Error);
                this.item = null;
            }
        } else {
            this.item = null;
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        if (this.item == null) {
            byteBufferWriter.putByte((byte)0);
        } else {
            byteBufferWriter.putByte((byte)1);

            try {
                this.item.save(byteBufferWriter.bb, false);
            } catch (IOException iOException) {
                DebugLog.Multiplayer.printException(iOException, "Item write error", LogSeverity.Error);
            }
        }
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.item != null;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n\tItem [ Item=" + (this.item == null ? "?" : "\"" + this.item.getDisplayName() + "\"") + " ]";
    }

    public InventoryItem getItem() {
        return this.item;
    }
}
