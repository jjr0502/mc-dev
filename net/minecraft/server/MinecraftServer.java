package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinecraftServer implements Runnable, ICommandListener, IMinecraftServer {

    public static Logger log = Logger.getLogger("Minecraft");
    public static HashMap trackerList = new HashMap();
    private String s;
    private int t;
    public NetworkListenThread networkListenThread;
    public PropertyManager propertyManager;
    public WorldServer[] worldServer;
    public long[] f = new long[100];
    public long[][] g;
    public ServerConfigurationManager serverConfigurationManager;
    private ConsoleCommandHandler consoleCommandHandler;
    private boolean isRunning = true;
    public boolean isStopped = false;
    int ticks = 0;
    public String k;
    public int l;
    private List w = new ArrayList();
    private List x = Collections.synchronizedList(new ArrayList());
    public EntityTracker[] tracker = new EntityTracker[3];
    public boolean onlineMode;
    public boolean spawnAnimals;
    public boolean pvpMode;
    public boolean allowFlight;
    public String r;
    private RemoteStatusListener y;
    private RemoteControlListener z;

    public MinecraftServer() {
        new ThreadSleepForever(this);
    }

    private boolean init() {
        this.consoleCommandHandler = new ConsoleCommandHandler(this);
        ThreadCommandReader threadcommandreader = new ThreadCommandReader(this);

        threadcommandreader.setDaemon(true);
        threadcommandreader.start();
        ConsoleLogManager.init();
        log.info("Starting minecraft server version 1.0.1");
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            log.warning("**** NOT ENOUGH RAM!");
            log.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        log.info("Loading properties");
        this.propertyManager = new PropertyManager(new File("server.properties"));
        this.s = this.propertyManager.getString("server-ip", "");
        this.onlineMode = this.propertyManager.getBoolean("online-mode", true);
        this.spawnAnimals = this.propertyManager.getBoolean("spawn-animals", true);
        this.pvpMode = this.propertyManager.getBoolean("pvp", true);
        this.allowFlight = this.propertyManager.getBoolean("allow-flight", false);
        this.r = this.propertyManager.getString("motd", "A Minecraft Server");
        this.r.replace('\u00a7', '$');
        InetAddress inetaddress = null;

        if (this.s.length() > 0) {
            inetaddress = InetAddress.getByName(this.s);
        }

        this.t = this.propertyManager.getInt("server-port", 25565);
        log.info("Starting Minecraft server on " + (this.s.length() == 0 ? "*" : this.s) + ":" + this.t);

        try {
            this.networkListenThread = new NetworkListenThread(this, inetaddress, this.t);
        } catch (IOException ioexception) {
            log.warning("**** FAILED TO BIND TO PORT!");
            log.log(Level.WARNING, "The exception was: " + ioexception.toString());
            log.warning("Perhaps a server is already running on that port?");
            return false;
        }

        if (!this.onlineMode) {
            log.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            log.warning("The server will make no attempt to authenticate usernames. Beware.");
            log.warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            log.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
        }

        this.serverConfigurationManager = new ServerConfigurationManager(this);
        this.tracker[0] = new EntityTracker(this, 0);
        this.tracker[1] = new EntityTracker(this, -1);
        this.tracker[2] = new EntityTracker(this, 1);
        long i = System.nanoTime();
        String s = this.propertyManager.getString("level-name", "world");
        String s1 = this.propertyManager.getString("level-seed", "");
        long j = (new Random()).nextLong();

        if (s1.length() > 0) {
            try {
                long k = Long.parseLong(s1);

                if (k != 0L) {
                    j = k;
                }
            } catch (NumberFormatException numberformatexception) {
                j = (long) s1.hashCode();
            }
        }

        log.info("Preparing level \"" + s + "\"");
        this.a(new WorldLoaderServer(new File(".")), s, j);
        log.info("Done (" + (System.nanoTime() - i) + "ns)! For help, type \"help\" or \"?\"");
        if (this.propertyManager.getBoolean("enable-query", false)) {
            log.info("Starting GS4 status listener");
            this.y = new RemoteStatusListener(this);
            this.y.a();
        }

        if (this.propertyManager.getBoolean("enable-rcon", false)) {
            log.info("Starting remote control listener");
            this.z = new RemoteControlListener(this);
            this.z.a();
        }

        return true;
    }

    private void a(Convertable convertable, String s, long i) {
        if (convertable.isConvertable(s)) {
            log.info("Converting map!");
            convertable.convert(s, new ConvertProgressUpdater(this));
        }

        this.worldServer = new WorldServer[3];
        this.g = new long[this.worldServer.length][100];
        int j = this.propertyManager.getInt("gamemode", 0);

        j = WorldSettings.a(j);
        log.info("Default game type: " + j);
        WorldSettings worldsettings = new WorldSettings(i, j, true, false);
        ServerNBTManager servernbtmanager = new ServerNBTManager(new File("."), s, true);

        for (int k = 0; k < this.worldServer.length; ++k) {
            byte b0 = 0;

            if (k == 1) {
                b0 = -1;
            }

            if (k == 2) {
                b0 = 1;
            }

            if (k == 0) {
                this.worldServer[k] = new WorldServer(this, servernbtmanager, s, b0, worldsettings);
            } else {
                this.worldServer[k] = new SecondaryWorldServer(this, servernbtmanager, s, b0, worldsettings, this.worldServer[0]);
            }

            this.worldServer[k].addIWorldAccess(new WorldManager(this, this.worldServer[k]));
            this.worldServer[k].difficulty = this.propertyManager.getInt("difficulty", 1);
            this.worldServer[k].setSpawnFlags(this.propertyManager.getBoolean("spawn-monsters", true), this.spawnAnimals);
            this.worldServer[k].getWorldData().setGameType(j);
            this.serverConfigurationManager.setPlayerFileData(this.worldServer);
        }

        short short1 = 196;
        long l = System.currentTimeMillis();

        for (int i1 = 0; i1 < 1; ++i1) {
            log.info("Preparing start region for level " + i1);
            if (i1 == 0 || this.propertyManager.getBoolean("allow-nether", true)) {
                WorldServer worldserver = this.worldServer[i1];
                ChunkCoordinates chunkcoordinates = worldserver.getSpawn();

                for (int j1 = -short1; j1 <= short1 && this.isRunning; j1 += 16) {
                    for (int k1 = -short1; k1 <= short1 && this.isRunning; k1 += 16) {
                        long l1 = System.currentTimeMillis();

                        if (l1 < l) {
                            l = l1;
                        }

                        if (l1 > l + 1000L) {
                            int i2 = (short1 * 2 + 1) * (short1 * 2 + 1);
                            int j2 = (j1 + short1) * (short1 * 2 + 1) + k1 + 1;

                            this.b("Preparing spawn area", j2 * 100 / i2);
                            l = l1;
                        }

                        worldserver.chunkProviderServer.getChunkAt(chunkcoordinates.x + j1 >> 4, chunkcoordinates.z + k1 >> 4);

                        while (worldserver.updateLights() && this.isRunning) {
                            ;
                        }
                    }
                }
            }
        }

        this.t();
    }

    private void b(String s, int i) {
        this.k = s;
        this.l = i;
        log.info(s + ": " + i + "%");
    }

    private void t() {
        this.k = null;
        this.l = 0;
    }

    private void saveChunks() {
        log.info("Saving chunks");

        for (int i = 0; i < this.worldServer.length; ++i) {
            WorldServer worldserver = this.worldServer[i];

            worldserver.save(true, (IProgressUpdate) null);
            worldserver.saveLevel();
        }
    }

    private void stop() {
        log.info("Stopping server");
        if (this.serverConfigurationManager != null) {
            this.serverConfigurationManager.savePlayers();
        }

        for (int i = 0; i < this.worldServer.length; ++i) {
            WorldServer worldserver = this.worldServer[i];

            if (worldserver != null) {
                this.saveChunks();
            }
        }
    }

    public void safeShutdown() {
        this.isRunning = false;
    }

    public void run() {
        try {
            if (this.init()) {
                long i = System.currentTimeMillis();

                for (long j = 0L; this.isRunning; Thread.sleep(1L)) {
                    long k = System.currentTimeMillis();
                    long l = k - i;

                    if (l > 2000L) {
                        log.warning("Can\'t keep up! Did the system time change, or is the server overloaded?");
                        l = 2000L;
                    }

                    if (l < 0L) {
                        log.warning("Time ran backwards! Did the system time change?");
                        l = 0L;
                    }

                    j += l;
                    i = k;
                    if (this.worldServer[0].everyoneDeeplySleeping()) {
                        this.w();
                        j = 0L;
                    } else {
                        while (j > 50L) {
                            j -= 50L;
                            this.w();
                        }
                    }
                }
            } else {
                while (this.isRunning) {
                    this.b();

                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException interruptedexception) {
                        interruptedexception.printStackTrace();
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.log(Level.SEVERE, "Unexpected exception", throwable);

            while (this.isRunning) {
                this.b();

                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedexception1) {
                    interruptedexception1.printStackTrace();
                }
            }
        } finally {
            try {
                this.stop();
                this.isStopped = true;
            } catch (Throwable throwable1) {
                throwable1.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
    }

    private void w() {
        long i = System.nanoTime();
        ArrayList arraylist = new ArrayList();
        Iterator iterator = trackerList.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            int j = ((Integer) trackerList.get(s)).intValue();

            if (j > 0) {
                trackerList.put(s, Integer.valueOf(j - 1));
            } else {
                arraylist.add(s);
            }
        }

        int k;

        for (k = 0; k < arraylist.size(); ++k) {
            trackerList.remove(arraylist.get(k));
        }

        AxisAlignedBB.a();
        Vec3D.a();
        ++this.ticks;

        for (k = 0; k < this.worldServer.length; ++k) {
            long l = System.nanoTime();

            if (k == 0 || this.propertyManager.getBoolean("allow-nether", true)) {
                WorldServer worldserver = this.worldServer[k];

                if (this.ticks % 20 == 0) {
                    this.serverConfigurationManager.a(new Packet4UpdateTime(worldserver.getTime()), worldserver.worldProvider.dimension);
                }

                worldserver.doTick();

                while (true) {
                    if (!worldserver.updateLights()) {
                        worldserver.tickEntities();
                        break;
                    }
                }
            }

            this.g[k][this.ticks % 100] = System.nanoTime() - l;
        }

        this.networkListenThread.a();
        this.serverConfigurationManager.tick();

        for (k = 0; k < this.tracker.length; ++k) {
            this.tracker[k].updatePlayers();
        }

        for (k = 0; k < this.w.size(); ++k) {
            ((IUpdatePlayerListBox) this.w.get(k)).a();
        }

        try {
            this.b();
        } catch (Exception exception) {
            log.log(Level.WARNING, "Unexpected exception while parsing console command", exception);
        }

        this.f[this.ticks % 100] = System.nanoTime() - i;
    }

    public void issueCommand(String s, ICommandListener icommandlistener) {
        this.x.add(new ServerCommand(s, icommandlistener));
    }

    public void b() {
        while (this.x.size() > 0) {
            ServerCommand servercommand = (ServerCommand) this.x.remove(0);

            this.consoleCommandHandler.handle(servercommand);
        }
    }

    public void a(IUpdatePlayerListBox iupdateplayerlistbox) {
        this.w.add(iupdateplayerlistbox);
    }

    public static void main(String[] astring) {
        StatisticList.a();

        try {
            MinecraftServer minecraftserver = new MinecraftServer();

            if (!GraphicsEnvironment.isHeadless() && (astring.length <= 0 || !astring[0].equals("nogui"))) {
                ServerGUI.a(minecraftserver);
            }

            (new ThreadServerApplication("Server thread", minecraftserver)).start();
        } catch (Exception exception) {
            log.log(Level.SEVERE, "Failed to start the minecraft server", exception);
        }
    }

    public File a(String s) {
        return new File(s);
    }

    public void sendMessage(String s) {
        log.info(s);
    }

    public void warning(String s) {
        log.warning(s);
    }

    public String getName() {
        return "CONSOLE";
    }

    public WorldServer getWorldServer(int i) {
        return i == -1 ? this.worldServer[1] : (i == 1 ? this.worldServer[2] : this.worldServer[0]);
    }

    public EntityTracker getTracker(int i) {
        return i == -1 ? this.tracker[1] : (i == 1 ? this.tracker[2] : this.tracker[0]);
    }

    public int getProperty(String s, int i) {
        return this.propertyManager.getInt(s, i);
    }

    public String a(String s, String s1) {
        return this.propertyManager.getString(s, s1);
    }

    public void a(String s, Object object) {
        this.propertyManager.a(s, object);
    }

    public void c() {
        this.propertyManager.savePropertiesFile();
    }

    public String getPropertiesFile() {
        File file1 = this.propertyManager.c();

        return file1 != null ? file1.getAbsolutePath() : "No settings file";
    }

    public String getMotd() {
        return this.s;
    }

    public int getPort() {
        return this.t;
    }

    public String getServerAddress() {
        return this.r;
    }

    public String getVersion() {
        return "1.0.1";
    }

    public int getPlayerCount() {
        return this.serverConfigurationManager.getPlayerCount();
    }

    public int getMaxPlayers() {
        return this.serverConfigurationManager.getMaxPlayers();
    }

    public String[] getPlayers() {
        return this.serverConfigurationManager.d();
    }

    public String getWorldName() {
        return this.propertyManager.getString("level-name", "world");
    }

    public String getPlugins() {
        return "";
    }

    public void o() {}

    public String d(String s) {
        RemoteControlCommandListener.a.a();
        this.consoleCommandHandler.handle(new ServerCommand(s, RemoteControlCommandListener.a));
        return RemoteControlCommandListener.a.b();
    }

    public boolean isDebugging() {
        return false;
    }

    public void severe(String s) {
        log.log(Level.SEVERE, s);
    }

    public void debug(String s) {
        if (this.isDebugging()) {
            log.log(Level.INFO, s);
        }
    }

    public String[] q() {
        return (String[]) this.serverConfigurationManager.getBannedAddresses().toArray(new String[0]);
    }

    public String[] r() {
        return (String[]) this.serverConfigurationManager.getBannedPlayers().toArray(new String[0]);
    }

    public static boolean isRunning(MinecraftServer minecraftserver) {
        return minecraftserver.isRunning;
    }
}
