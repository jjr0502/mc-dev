package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet19EntityAction extends Packet {

    public int a;
    public int animation;

    public Packet19EntityAction() {}

    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - add throws declaration
        this.a = datainputstream.readInt();
        this.animation = datainputstream.readByte();
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - add throws declaration
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeByte(this.animation);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 5;
    }
}
