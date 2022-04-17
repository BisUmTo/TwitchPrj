package com.github.gnottero;


import carpet.script.CarpetContext;
import carpet.script.Expression;
import carpet.script.exception.InternalExpressionException;
import carpet.script.value.EntityValue;
import carpet.script.value.Value;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;

public class TwitchProjectScarpet {
    public static void apply(Expression expression) {
        expression.addContextFunction("apply_shader", -1, (c, t, lv) ->
        {
            if (lv.size() < 2) throw new InternalExpressionException("'apply_shader' requires at least two arguments");
            Value playerValue = lv.get(0);
            ServerPlayerEntity player = EntityValue.getPlayerByValue(((CarpetContext) c).s.getServer(), playerValue);
            if (player == null)
                throw new InternalExpressionException("'apply_shader' requires a valid online player as the first argument.");
            Identifier shaderIdentifier = null;
            if (lv.size() == 2) {
                shaderIdentifier = new Identifier("shaders/post/" + lv.get(1).getString() + ".json");
            } else if (lv.size() == 3) {
                shaderIdentifier = new Identifier(lv.get(1).getString(), "shaders/post/" + lv.get(2).getString() + ".json");
            }
            player.emitGameEvent(GameEvent.ENTITY_SHAKE);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeIdentifier(shaderIdentifier);

            ServerPlayNetworking.send(player, new Identifier("carpet", "shaders"), buf);
            return Value.TRUE;
        });
    }
}
