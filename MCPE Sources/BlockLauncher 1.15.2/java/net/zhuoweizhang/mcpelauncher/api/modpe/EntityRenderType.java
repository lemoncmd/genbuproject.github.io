package net.zhuoweizhang.mcpelauncher.api.modpe;

import java.util.Arrays;

public final class EntityRenderType {
    private static final int[] alltypes = new int[]{1, tnt, human, item, chicken, cow, mushroomCow, pig, sheep, bat, wolf, villager, zombie, zombiePigman, lavaSlime, ghast, blaze, skeleton, spider, silverfish, creeper, slime, enderman, arrow, fishHook, player, egg, snowball, unknownItem, thrownPotion, painting, fallingTile, minecart, boat, squid, fireball, smallFireball, villagerZombie, experienceOrb, lightningBolt, ironGolem, ocelot, snowGolem, expPotion, rabbit, witch, camera, map};
    public static final int arrow = 25;
    public static final int bat = 10;
    public static final int blaze = 18;
    public static final int boat = 35;
    public static final int camera = 48;
    public static final int chicken = 5;
    public static final int cow = 6;
    public static final int creeper = 22;
    public static final int egg = 28;
    public static final int enderman = 24;
    public static final int expPotion = 45;
    public static final int experienceOrb = 40;
    public static final int fallingTile = 33;
    public static final int fireball = 37;
    public static final int fishHook = 26;
    public static final int ghast = 17;
    public static final int human = 3;
    public static final int ironGolem = 42;
    public static final int item = 4;
    public static final int lavaSlime = 16;
    public static final int lightningBolt = 41;
    public static final int map = 50;
    public static final int minecart = 34;
    public static final int mushroomCow = 7;
    public static final int ocelot = 43;
    public static final int painting = 32;
    public static final int pig = 8;
    public static final int player = 27;
    public static final int rabbit = 46;
    public static final int sheep = 9;
    public static final int silverfish = 21;
    public static final int skeleton = 19;
    public static final int slime = 23;
    public static final int smallFireball = 38;
    public static final int snowGolem = 44;
    public static final int snowball = 29;
    public static final int spider = 20;
    public static final int squid = 36;
    public static final int thrownPotion = 31;
    public static final int tnt = 2;
    public static final int unknownItem = 30;
    public static final int villager = 12;
    public static final int villagerZombie = 39;
    public static final int witch = 47;
    public static final int wolf = 11;
    public static final int zombie = 14;
    public static final int zombiePigman = 15;

    private EntityRenderType() {
    }

    public static boolean isValidRenderType(int type) {
        return Arrays.binarySearch(alltypes, type) >= 0;
    }
}
