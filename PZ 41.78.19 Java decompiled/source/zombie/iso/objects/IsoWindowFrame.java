// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.network.GameServer;

public class IsoWindowFrame {
    private static IsoWindowFrame.Direction getDirection(IsoObject object) {
        if (!(object instanceof IsoWindow) && !(object instanceof IsoThumpable)) {
            if (object == null || object.getProperties() == null || object.getObjectIndex() == -1) {
                return IsoWindowFrame.Direction.INVALID;
            } else if (object.getProperties().Is(IsoFlagType.WindowN)) {
                return IsoWindowFrame.Direction.NORTH;
            } else {
                return object.getProperties().Is(IsoFlagType.WindowW) ? IsoWindowFrame.Direction.WEST : IsoWindowFrame.Direction.INVALID;
            }
        } else {
            return IsoWindowFrame.Direction.INVALID;
        }
    }

    public static boolean isWindowFrame(IsoObject o) {
        return getDirection(o).isValid();
    }

    public static boolean isWindowFrame(IsoObject o, boolean north) {
        IsoWindowFrame.Direction direction = getDirection(o);
        return north && direction == IsoWindowFrame.Direction.NORTH || !north && direction == IsoWindowFrame.Direction.WEST;
    }

    public static int countAddSheetRope(IsoObject o) {
        IsoWindowFrame.Direction direction = getDirection(o);
        return direction.isValid() ? IsoWindow.countAddSheetRope(o.getSquare(), direction == IsoWindowFrame.Direction.NORTH) : 0;
    }

    public static boolean canAddSheetRope(IsoObject o) {
        IsoWindowFrame.Direction direction = getDirection(o);
        return direction.isValid() && IsoWindow.canAddSheetRope(o.getSquare(), direction == IsoWindowFrame.Direction.NORTH);
    }

    public static boolean haveSheetRope(IsoObject o) {
        IsoWindowFrame.Direction direction = getDirection(o);
        return direction.isValid() && IsoWindow.isTopOfSheetRopeHere(o.getSquare(), direction == IsoWindowFrame.Direction.NORTH);
    }

    public static boolean addSheetRope(IsoObject o, IsoPlayer player, String itemType) {
        return !canAddSheetRope(o) ? false : IsoWindow.addSheetRope(player, o.getSquare(), getDirection(o) == IsoWindowFrame.Direction.NORTH, itemType);
    }

    public static boolean removeSheetRope(IsoObject o, IsoPlayer player) {
        return !haveSheetRope(o) ? false : IsoWindow.removeSheetRope(player, o.getSquare(), getDirection(o) == IsoWindowFrame.Direction.NORTH);
    }

    public static IsoGridSquare getOppositeSquare(IsoObject o) {
        IsoWindowFrame.Direction direction = getDirection(o);
        if (!direction.isValid()) {
            return null;
        } else {
            boolean boolean0 = direction == IsoWindowFrame.Direction.NORTH;
            return o.getSquare().getAdjacentSquare(boolean0 ? IsoDirections.N : IsoDirections.W);
        }
    }

    public static IsoGridSquare getIndoorSquare(IsoObject o) {
        IsoWindowFrame.Direction direction = getDirection(o);
        if (!direction.isValid()) {
            return null;
        } else {
            IsoGridSquare square0 = o.getSquare();
            if (square0.getRoom() != null) {
                return square0;
            } else {
                IsoGridSquare square1 = getOppositeSquare(o);
                return square1 != null && square1.getRoom() != null ? square1 : null;
            }
        }
    }

    public static IsoCurtain getCurtain(IsoObject o) {
        IsoWindowFrame.Direction direction = getDirection(o);
        if (!direction.isValid()) {
            return null;
        } else {
            boolean boolean0 = direction == IsoWindowFrame.Direction.NORTH;
            IsoCurtain curtain = o.getSquare().getCurtain(boolean0 ? IsoObjectType.curtainN : IsoObjectType.curtainW);
            if (curtain != null) {
                return curtain;
            } else {
                IsoGridSquare square = getOppositeSquare(o);
                return square == null ? null : square.getCurtain(boolean0 ? IsoObjectType.curtainS : IsoObjectType.curtainE);
            }
        }
    }

    public static IsoGridSquare getAddSheetSquare(IsoObject o, IsoGameCharacter chr) {
        IsoWindowFrame.Direction direction = getDirection(o);
        if (!direction.isValid()) {
            return null;
        } else {
            boolean boolean0 = direction == IsoWindowFrame.Direction.NORTH;
            if (chr != null && chr.getCurrentSquare() != null) {
                IsoGridSquare square0 = chr.getCurrentSquare();
                IsoGridSquare square1 = o.getSquare();
                if (boolean0) {
                    if (square0.getY() < square1.getY()) {
                        return square1.getAdjacentSquare(IsoDirections.N);
                    }
                } else if (square0.getX() < square1.getX()) {
                    return square1.getAdjacentSquare(IsoDirections.W);
                }

                return square1;
            } else {
                return null;
            }
        }
    }

    public static void addSheet(IsoObject o, IsoGameCharacter chr) {
        IsoWindowFrame.Direction direction = getDirection(o);
        if (direction.isValid()) {
            boolean boolean0 = direction == IsoWindowFrame.Direction.NORTH;
            IsoGridSquare square = getIndoorSquare(o);
            if (square == null) {
                square = o.getSquare();
            }

            if (chr != null) {
                square = getAddSheetSquare(o, chr);
            }

            if (square != null) {
                IsoObjectType objectType;
                if (square == o.getSquare()) {
                    objectType = boolean0 ? IsoObjectType.curtainN : IsoObjectType.curtainW;
                } else {
                    objectType = boolean0 ? IsoObjectType.curtainS : IsoObjectType.curtainE;
                }

                if (square.getCurtain(objectType) == null) {
                    int int0 = 16;
                    if (objectType == IsoObjectType.curtainE) {
                        int0++;
                    }

                    if (objectType == IsoObjectType.curtainS) {
                        int0 += 3;
                    }

                    if (objectType == IsoObjectType.curtainN) {
                        int0 += 2;
                    }

                    int0 += 4;
                    IsoCurtain curtain = new IsoCurtain(o.getCell(), square, "fixtures_windows_curtains_01_" + int0, boolean0);
                    square.AddSpecialTileObject(curtain);
                    if (GameServer.bServer) {
                        curtain.transmitCompleteItemToClients();
                        if (chr != null) {
                            chr.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
                        }
                    } else if (chr != null) {
                        chr.getInventory().RemoveOneOf("Sheet");
                    }
                }
            }
        }
    }

    public static boolean canClimbThrough(IsoObject o, IsoGameCharacter chr) {
        IsoWindowFrame.Direction direction = getDirection(o);
        if (!direction.isValid()) {
            return false;
        } else if (o.getSquare() == null) {
            return false;
        } else {
            IsoWindow window = o.getSquare().getWindow(direction == IsoWindowFrame.Direction.NORTH);
            if (window != null && window.isBarricaded()) {
                return false;
            } else {
                if (chr != null) {
                    IsoGridSquare square = direction == IsoWindowFrame.Direction.NORTH
                        ? o.getSquare().nav[IsoDirections.N.index()]
                        : o.getSquare().nav[IsoDirections.W.index()];
                    if (!IsoWindow.canClimbThroughHelper(chr, o.getSquare(), square, direction == IsoWindowFrame.Direction.NORTH)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private static enum Direction {
        INVALID,
        NORTH,
        WEST;

        public boolean isValid() {
            return this != INVALID;
        }
    }
}
