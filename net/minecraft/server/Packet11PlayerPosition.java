package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet11PlayerPosition extends Packet10Flying {

    public Packet11PlayerPosition() {
        this.h = true;
    }

    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - add throws declaration
        this.x = datainputstream.readDouble();
        this.y = datainputstream.readDouble();
        this.stance = datainputstream.readDouble();
        this.z = datainputstream.readDouble();
        super.a(datainputstream);
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - add throws declaration
        dataoutputstream.writeDouble(this.x);
        dataoutputstream.writeDouble(this.y);
        dataoutputstream.writeDouble(this.stance);
        dataoutputstream.writeDouble(this.z);
        super.a(dataoutputstream);
    }

    public int a() {
        return 33;
    }
}
