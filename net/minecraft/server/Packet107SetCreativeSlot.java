package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet107SetCreativeSlot extends Packet {

    public int a;
    public int b;
    public int c;
    public int d;

    public Packet107SetCreativeSlot() {}

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - add throws declaration
        this.a = datainputstream.readShort();
        this.b = datainputstream.readShort();
        this.c = datainputstream.readShort();
        this.d = datainputstream.readShort();
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - add throws declaration
        dataoutputstream.writeShort(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeShort(this.c);
        dataoutputstream.writeShort(this.d);
    }

    public int a() {
        return 8;
    }
}
