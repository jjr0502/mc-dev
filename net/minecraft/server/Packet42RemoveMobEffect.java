package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet42RemoveMobEffect extends Packet {

    public int a;
    public byte b;

    public Packet42RemoveMobEffect() {}

    public Packet42RemoveMobEffect(int i, MobEffect mobeffect) {
        this.a = i;
        this.b = (byte) (mobeffect.getEffectId() & 255);
    }

    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - add throws declaration
        this.a = datainputstream.readInt();
        this.b = datainputstream.readByte();
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - add throws declaration
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeByte(this.b);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 5;
    }
}
