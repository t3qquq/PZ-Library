// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.AltCommandArgs;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclesDB2;

@CommandName(
    name = "addvehicle"
)
@AltCommandArgs({@CommandArgs(
        required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)"},
        argName = "Script Only"
    ), @CommandArgs(
        required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)", "(\\d+),(\\d+),(\\d+)"},
        argName = "Script And Coordinate"
    ), @CommandArgs(
        required = {"([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)", "(.+)"},
        argName = "Script And Player"
    )})
@CommandHelp(
    helpText = "UI_ServerOptionDesc_AddVehicle"
)
@RequiredRight(
    requiredRights = 60
)
public class AddVehicleCommand extends CommandBase {
    public static final String scriptOnly = "Script Only";
    public static final String scriptPlayer = "Script And Player";
    public static final String scriptCoordinate = "Script And Coordinate";

    public AddVehicleCommand(String string0, String string1, String string2, UdpConnection udpConnection) {
        super(string0, string1, string2, udpConnection);
    }

    @Override
    protected String Command() {
        String string0 = this.getCommandArg(0);
        VehicleScript vehicleScript = ScriptManager.instance.getVehicle(string0);
        if (vehicleScript == null) {
            return "Unknown vehicle script \"" + string0 + "\"";
        } else {
            string0 = vehicleScript.getModule().getName() + "." + vehicleScript.getName();
            int int0;
            int int1;
            int int2;
            if (this.argsName.equals("Script And Player")) {
                String string1 = this.getCommandArg(1);
                IsoPlayer player0 = GameServer.getPlayerByUserNameForCommand(string1);
                if (player0 == null) {
                    return "User \"" + string1 + "\" not found";
                }

                int0 = PZMath.fastfloor(player0.getX());
                int1 = PZMath.fastfloor(player0.getY());
                int2 = PZMath.fastfloor(player0.getZ());
            } else if (this.argsName.equals("Script And Coordinate")) {
                int0 = PZMath.fastfloor(Float.parseFloat(this.getCommandArg(1)));
                int1 = PZMath.fastfloor(Float.parseFloat(this.getCommandArg(2)));
                int2 = PZMath.fastfloor(Float.parseFloat(this.getCommandArg(3)));
            } else {
                if (this.connection == null) {
                    return "Pass a username or coordinate";
                }

                String string2 = this.getExecutorUsername();
                IsoPlayer player1 = GameServer.getPlayerByUserNameForCommand(string2);
                if (player1 == null) {
                    return "User \"" + string2 + "\" not found";
                }

                int0 = PZMath.fastfloor(player1.getX());
                int1 = PZMath.fastfloor(player1.getY());
                int2 = PZMath.fastfloor(player1.getZ());
            }

            if (int2 > 0) {
                return "Z coordinate must be 0 for now";
            } else {
                IsoGridSquare square = ServerMap.instance.getGridSquare(int0, int1, int2);
                if (square == null) {
                    return "Invalid location " + int0 + "," + int1 + "," + int2;
                } else {
                    BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
                    vehicle.setScriptName(string0);
                    vehicle.setX(int0 - 1.0F);
                    vehicle.setY(int1 - 0.1F);
                    vehicle.setZ(int2 + 0.2F);
                    if (IsoChunk.doSpawnedVehiclesInInvalidPosition(vehicle)) {
                        vehicle.setSquare(square);
                        vehicle.square.chunk.vehicles.add(vehicle);
                        vehicle.chunk = vehicle.square.chunk;
                        vehicle.addToWorld();
                        VehiclesDB2.instance.addVehicle(vehicle);
                        return "Vehicle spawned";
                    } else {
                        return "ERROR: I can not spawn the vehicle. Invalid position. Try to change position.";
                    }
                }
            }
        }
    }
}
