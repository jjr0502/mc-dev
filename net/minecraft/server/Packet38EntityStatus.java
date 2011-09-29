package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet38EntityStatus extends Packet {

    public int a;
    public byte b;

    public Packet38EntityStatus() {}

    public Packet38EntityStatus(int i, byte b0) {
        this.a = i;
        this.b = b0;
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
