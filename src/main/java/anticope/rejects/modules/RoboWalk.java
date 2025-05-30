package anticope.rejects.modules;

import anticope.rejects.MeteorRejectsAddon;
import anticope.rejects.mixin.PlayerMoveC2SPacketAccessor;
import anticope.rejects.mixin.VehicleMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class RoboWalk extends Module {
    public RoboWalk() {
        super(MeteorRejectsAddon.CATEGORY, "robo-walk", "Bypasses LiveOverflow movement check.");
    }

    private double smooth(double d) {
        double temp = (double) Math.round(d * 100) / 100;
        return Math.nextAfter(temp, temp + Math.signum(d));
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket packet) {
            if (!packet.changesPosition()) return;

            double x = smooth(packet.getX(0));
            double z = smooth(packet.getZ(0));

            ((PlayerMoveC2SPacketAccessor) packet).setX(x);
            ((PlayerMoveC2SPacketAccessor) packet).setZ(z);
        } else if (event.packet instanceof VehicleMoveC2SPacket packet) {
            Vec3d pos = ((VehicleMoveC2SPacketAccessor) (Object) packet).getPosition();
            double x = smooth(pos.getX());
            double z = smooth(pos.getZ());

            event.packet = VehicleMoveC2SPacketAccessor.create(new Vec3d(x, pos.getY(), z), packet.yaw(), packet.pitch(), packet.onGround());
        }
    }
}
