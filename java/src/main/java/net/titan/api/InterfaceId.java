package net.titan.api;

public final class InterfaceId {
    public static final int MAKE_BUTTON = net.titan.gamevals.InterfaceID.Skillmulti.BOTTOM;
    public static final int AUTO_RETALIATE = net.titan.gamevals.InterfaceID.CombatInterface.RETALIATE;
    public static final int QUESTSCROLL_CLOSE = net.titan.gamevals.InterfaceID.Questscroll.CLOSE_BUTTON;
    public static final int QUESTSCROLL_CONTENT = net.titan.gamevals.InterfaceID.Questscroll.CONTENT;
    public static final int DIALOG_OPTIONS = net.titan.gamevals.InterfaceID.Chatmenu.OPTIONS;
    public static final int SPEC_ORB = net.titan.gamevals.InterfaceID.Orbs.ORB_SPECENERGY;
    public static final int RUN_ORB = net.titan.gamevals.InterfaceID.Orbs.RUNENERGY_BACKING;

    public static final int BANK_CLOSE = net.titan.gamevals.InterfaceID.Bankmain.FRAME;
    public static final int BANK_ITEM_CONTAINER = net.titan.gamevals.InterfaceID.Bankmain.ITEMS;
    public static final int BANK_NOTE_TOGGLE = net.titan.gamevals.InterfaceID.Bankmain.POTIONSTORE_CONTAINER;
    public static final int BANK_DEPOSIT_INVENTORY = net.titan.gamevals.InterfaceID.Bankmain.DEPOSITINV;
    public static final int BANK_DEPOSIT_EQUIPMENT = net.titan.gamevals.InterfaceID.Bankmain.DEPOSITWORN;
    public static final int BANK_INVENTORY_ITEM_CONTAINER = net.titan.gamevals.InterfaceID.Bankside.ITEMS;

    public static final int DEPOSIT_BOX_INVENTORY_ITEM_CONTAINER =
        net.titan.gamevals.InterfaceID.BankDepositbox.MENU_BUTTON;
    public static final int DEPOSIT_BOX_DEPOSIT_BUTTON = net.titan.gamevals.InterfaceID.BankDepositbox.WORN;
    public static final int GRAND_EXCHANGE_INVENTORY_ITEM_CONTAINER =
        net.titan.gamevals.InterfaceID.GeOffersSide.ITEMS;
    public static final int BANK_PIN_CONTAINER = net.titan.gamevals.InterfaceID.BankpinKeypad.UNIVERSE;
    public static final int BANK_PIN_INSTRUCTION = net.titan.gamevals.InterfaceID.BankpinKeypad.FRAME;

    private InterfaceId() {}

    public static int pack(int group, int child) {
        return (group << 16) | child;
    }

    public static int group(int packedId) {
        return (packedId >>> 16) & 0xffff;
    }

    public static int child(int packedId) {
        return packedId & 0xffff;
    }

    /// Packed widget ids for the standard/ancient/lunar/arceuus magic
    /// spellbook layer. Compatibility wrapper over
    /// {@code net.titan.gamevals.InterfaceID.MagicSpellbook}.
    public static final class MagicSpellbook {
        private MagicSpellbook() {}

        public static final int UNIVERSE = net.titan.gamevals.InterfaceID.MagicSpellbook.UNIVERSE;
        public static final int TOP = net.titan.gamevals.InterfaceID.MagicSpellbook.TOP;
        public static final int GLOW = net.titan.gamevals.InterfaceID.MagicSpellbook.GLOW;
        public static final int SPELLLAYER = net.titan.gamevals.InterfaceID.MagicSpellbook.SPELLLAYER;
        public static final int BACK_BUTTON = net.titan.gamevals.InterfaceID.MagicSpellbook.BACK_BUTTON;
        public static final int LEAGUE_HOME_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.LEAGUE_HOME_TELEPORT;
        public static final int TELEPORT_HOME_STANDARD = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_HOME_STANDARD;
        public static final int TELEPORT_MINIGAME_STANDARD = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_MINIGAME_STANDARD;
        public static final int TELEPORT_MINIGAME_ANCIENT = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_MINIGAME_ANCIENT;
        public static final int TELEPORT_MINIGAME_ARCEUUS = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_MINIGAME_ARCEUUS;
        public static final int TELEPORT_MINIGAME_LUNAR = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_MINIGAME_LUNAR;
        public static final int WIND_STRIKE = net.titan.gamevals.InterfaceID.MagicSpellbook.WIND_STRIKE;
        public static final int CONFUSE = net.titan.gamevals.InterfaceID.MagicSpellbook.CONFUSE;
        public static final int XBOWS_ENCHANT = net.titan.gamevals.InterfaceID.MagicSpellbook.XBOWS_ENCHANT;
        public static final int WATER_STRIKE = net.titan.gamevals.InterfaceID.MagicSpellbook.WATER_STRIKE;
        public static final int ENCHANT_JEWELLERY = net.titan.gamevals.InterfaceID.MagicSpellbook.ENCHANT_JEWELLERY;
        public static final int ENCHANT_1 = net.titan.gamevals.InterfaceID.MagicSpellbook.ENCHANT_1;
        public static final int EARTH_STRIKE = net.titan.gamevals.InterfaceID.MagicSpellbook.EARTH_STRIKE;
        public static final int WEAKEN = net.titan.gamevals.InterfaceID.MagicSpellbook.WEAKEN;
        public static final int FIRE_STRIKE = net.titan.gamevals.InterfaceID.MagicSpellbook.FIRE_STRIKE;
        public static final int BONES_BANANAS = net.titan.gamevals.InterfaceID.MagicSpellbook.BONES_BANANAS;
        public static final int WIND_BOLT = net.titan.gamevals.InterfaceID.MagicSpellbook.WIND_BOLT;
        public static final int CURSE = net.titan.gamevals.InterfaceID.MagicSpellbook.CURSE;
        public static final int BIND = net.titan.gamevals.InterfaceID.MagicSpellbook.BIND;
        public static final int LOW_ALCHEMY = net.titan.gamevals.InterfaceID.MagicSpellbook.LOW_ALCHEMY;
        public static final int WATER_BOLT = net.titan.gamevals.InterfaceID.MagicSpellbook.WATER_BOLT;
        public static final int VARROCK_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.VARROCK_TELEPORT;
        public static final int ENCHANT_2 = net.titan.gamevals.InterfaceID.MagicSpellbook.ENCHANT_2;
        public static final int EARTH_BOLT = net.titan.gamevals.InterfaceID.MagicSpellbook.EARTH_BOLT;
        public static final int LUMBRIDGE_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.LUMBRIDGE_TELEPORT;
        public static final int TELEGRAB = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEGRAB;
        public static final int FIRE_BOLT = net.titan.gamevals.InterfaceID.MagicSpellbook.FIRE_BOLT;
        public static final int FALADOR_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.FALADOR_TELEPORT;
        public static final int CRUMBLE_UNDEAD = net.titan.gamevals.InterfaceID.MagicSpellbook.CRUMBLE_UNDEAD;
        public static final int TELEPORT_TO_YOUR_HOUSE = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_TO_YOUR_HOUSE;
        public static final int WIND_BLAST = net.titan.gamevals.InterfaceID.MagicSpellbook.WIND_BLAST;
        public static final int SUPERHEAT = net.titan.gamevals.InterfaceID.MagicSpellbook.SUPERHEAT;
        public static final int CAMELOT_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.CAMELOT_TELEPORT;
        public static final int WATER_BLAST = net.titan.gamevals.InterfaceID.MagicSpellbook.WATER_BLAST;
        public static final int KOUREND_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.KOUREND_TELEPORT;
        public static final int ENCHANT_3 = net.titan.gamevals.InterfaceID.MagicSpellbook.ENCHANT_3;
        public static final int IBAN_BLAST = net.titan.gamevals.InterfaceID.MagicSpellbook.IBAN_BLAST;
        public static final int SNARE = net.titan.gamevals.InterfaceID.MagicSpellbook.SNARE;
        public static final int MAGIC_DART = net.titan.gamevals.InterfaceID.MagicSpellbook.MAGIC_DART;
        public static final int ARDOUGNE_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.ARDOUGNE_TELEPORT;
        public static final int EARTH_BLAST = net.titan.gamevals.InterfaceID.MagicSpellbook.EARTH_BLAST;
        public static final int FORTIS_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.FORTIS_TELEPORT;
        public static final int HIGH_ALCHEMY = net.titan.gamevals.InterfaceID.MagicSpellbook.HIGH_ALCHEMY;
        public static final int CHARGE_WATER_ORB = net.titan.gamevals.InterfaceID.MagicSpellbook.CHARGE_WATER_ORB;
        public static final int ENCHANT_4 = net.titan.gamevals.InterfaceID.MagicSpellbook.ENCHANT_4;
        public static final int WATCHTOWER_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.WATCHTOWER_TELEPORT;
        public static final int FIRE_BLAST = net.titan.gamevals.InterfaceID.MagicSpellbook.FIRE_BLAST;
        public static final int CHARGE_EARTH_ORB = net.titan.gamevals.InterfaceID.MagicSpellbook.CHARGE_EARTH_ORB;
        public static final int BONES_PEACHES = net.titan.gamevals.InterfaceID.MagicSpellbook.BONES_PEACHES;
        public static final int SARADOMIN_STRIKE = net.titan.gamevals.InterfaceID.MagicSpellbook.SARADOMIN_STRIKE;
        public static final int CLAWS_OF_GUTHIX = net.titan.gamevals.InterfaceID.MagicSpellbook.CLAWS_OF_GUTHIX;
        public static final int FLAMES_OF_ZAMORAK = net.titan.gamevals.InterfaceID.MagicSpellbook.FLAMES_OF_ZAMORAK;
        public static final int TROLLHEIM_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.TROLLHEIM_TELEPORT;
        public static final int WIND_WAVE = net.titan.gamevals.InterfaceID.MagicSpellbook.WIND_WAVE;
        public static final int CHARGE_FIRE_ORB = net.titan.gamevals.InterfaceID.MagicSpellbook.CHARGE_FIRE_ORB;
        public static final int APE_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.APE_TELEPORT;
        public static final int WATER_WAVE = net.titan.gamevals.InterfaceID.MagicSpellbook.WATER_WAVE;
        public static final int CHARGE_AIR_ORB = net.titan.gamevals.InterfaceID.MagicSpellbook.CHARGE_AIR_ORB;
        public static final int VULNERABILITY = net.titan.gamevals.InterfaceID.MagicSpellbook.VULNERABILITY;
        public static final int ENCHANT_5 = net.titan.gamevals.InterfaceID.MagicSpellbook.ENCHANT_5;
        public static final int EARTH_WAVE = net.titan.gamevals.InterfaceID.MagicSpellbook.EARTH_WAVE;
        public static final int ENFEEBLE = net.titan.gamevals.InterfaceID.MagicSpellbook.ENFEEBLE;
        public static final int TELEOTHER_LUMBRIDGE = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEOTHER_LUMBRIDGE;
        public static final int FIRE_WAVE = net.titan.gamevals.InterfaceID.MagicSpellbook.FIRE_WAVE;
        public static final int ENTANGLE = net.titan.gamevals.InterfaceID.MagicSpellbook.ENTANGLE;
        public static final int STUN = net.titan.gamevals.InterfaceID.MagicSpellbook.STUN;
        public static final int CHARGE = net.titan.gamevals.InterfaceID.MagicSpellbook.CHARGE;
        public static final int WIND_SURGE = net.titan.gamevals.InterfaceID.MagicSpellbook.WIND_SURGE;
        public static final int TELEOTHER_FALADOR = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEOTHER_FALADOR;
        public static final int WATER_SURGE = net.titan.gamevals.InterfaceID.MagicSpellbook.WATER_SURGE;
        public static final int TELEPORT_BLOCK = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_BLOCK;
        public static final int BOUNTY_TARGET = net.titan.gamevals.InterfaceID.MagicSpellbook.BOUNTY_TARGET;
        public static final int ENCHANT_6 = net.titan.gamevals.InterfaceID.MagicSpellbook.ENCHANT_6;
        public static final int TELEOTHER_CAMELOT = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEOTHER_CAMELOT;
        public static final int EARTH_SURGE = net.titan.gamevals.InterfaceID.MagicSpellbook.EARTH_SURGE;
        public static final int ENCHANT_7 = net.titan.gamevals.InterfaceID.MagicSpellbook.ENCHANT_7;
        public static final int FIRE_SURGE = net.titan.gamevals.InterfaceID.MagicSpellbook.FIRE_SURGE;
        public static final int TELEPORT_BOAT_TO_ME = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_BOAT_TO_ME;
        public static final int TELEPORT_ME_TO_BOAT = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_ME_TO_BOAT;
        public static final int ICE_RUSH = net.titan.gamevals.InterfaceID.MagicSpellbook.ICE_RUSH;
        public static final int ICE_BLITZ = net.titan.gamevals.InterfaceID.MagicSpellbook.ICE_BLITZ;
        public static final int ICE_BURST = net.titan.gamevals.InterfaceID.MagicSpellbook.ICE_BURST;
        public static final int ICE_BARRAGE = net.titan.gamevals.InterfaceID.MagicSpellbook.ICE_BARRAGE;
        public static final int BLOOD_RUSH = net.titan.gamevals.InterfaceID.MagicSpellbook.BLOOD_RUSH;
        public static final int BLOOD_BLITZ = net.titan.gamevals.InterfaceID.MagicSpellbook.BLOOD_BLITZ;
        public static final int BLOOD_BURST = net.titan.gamevals.InterfaceID.MagicSpellbook.BLOOD_BURST;
        public static final int BLOOD_BARRAGE = net.titan.gamevals.InterfaceID.MagicSpellbook.BLOOD_BARRAGE;
        public static final int SMOKE_RUSH = net.titan.gamevals.InterfaceID.MagicSpellbook.SMOKE_RUSH;
        public static final int SMOKE_BLITZ = net.titan.gamevals.InterfaceID.MagicSpellbook.SMOKE_BLITZ;
        public static final int SMOKE_BURST = net.titan.gamevals.InterfaceID.MagicSpellbook.SMOKE_BURST;
        public static final int SMOKE_BARRAGE = net.titan.gamevals.InterfaceID.MagicSpellbook.SMOKE_BARRAGE;
        public static final int SHADOW_RUSH = net.titan.gamevals.InterfaceID.MagicSpellbook.SHADOW_RUSH;
        public static final int SHADOW_BLITZ = net.titan.gamevals.InterfaceID.MagicSpellbook.SHADOW_BLITZ;
        public static final int SHADOW_BURST = net.titan.gamevals.InterfaceID.MagicSpellbook.SHADOW_BURST;
        public static final int SHADOW_BARRAGE = net.titan.gamevals.InterfaceID.MagicSpellbook.SHADOW_BARRAGE;
        public static final int ZAROSTELEPORT1 = net.titan.gamevals.InterfaceID.MagicSpellbook.ZAROSTELEPORT1;
        public static final int ZAROSTELEPORT2 = net.titan.gamevals.InterfaceID.MagicSpellbook.ZAROSTELEPORT2;
        public static final int ZAROSTELEPORT3 = net.titan.gamevals.InterfaceID.MagicSpellbook.ZAROSTELEPORT3;
        public static final int ZAROSTELEPORT4 = net.titan.gamevals.InterfaceID.MagicSpellbook.ZAROSTELEPORT4;
        public static final int ZAROSTELEPORT5 = net.titan.gamevals.InterfaceID.MagicSpellbook.ZAROSTELEPORT5;
        public static final int ZAROSTELEPORT6 = net.titan.gamevals.InterfaceID.MagicSpellbook.ZAROSTELEPORT6;
        public static final int ZAROSTELEPORT7 = net.titan.gamevals.InterfaceID.MagicSpellbook.ZAROSTELEPORT7;
        public static final int ZAROSTELEPORT8 = net.titan.gamevals.InterfaceID.MagicSpellbook.ZAROSTELEPORT8;
        public static final int TELEPORT_HOME_ZAROS = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_HOME_ZAROS;
        public static final int TELEPORT_HOME_LUNAR = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_HOME_LUNAR;
        public static final int BAKE_PIE = net.titan.gamevals.InterfaceID.MagicSpellbook.BAKE_PIE;
        public static final int CURE_PLANT = net.titan.gamevals.InterfaceID.MagicSpellbook.CURE_PLANT;
        public static final int MONSTER_EXAMINE = net.titan.gamevals.InterfaceID.MagicSpellbook.MONSTER_EXAMINE;
        public static final int NPC_CONTACT = net.titan.gamevals.InterfaceID.MagicSpellbook.NPC_CONTACT;
        public static final int CURE_OTHER = net.titan.gamevals.InterfaceID.MagicSpellbook.CURE_OTHER;
        public static final int HUMIDIFY = net.titan.gamevals.InterfaceID.MagicSpellbook.HUMIDIFY;
        public static final int TELE_MOONCLAN = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_MOONCLAN;
        public static final int TELE_GROUP_MOONCLAN = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_GROUP_MOONCLAN;
        public static final int CURE_ME = net.titan.gamevals.InterfaceID.MagicSpellbook.CURE_ME;
        public static final int HUNTER_KIT = net.titan.gamevals.InterfaceID.MagicSpellbook.HUNTER_KIT;
        public static final int TELE_WATERBIRTH = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_WATERBIRTH;
        public static final int TELE_GROUP_WATERBIRTH = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_GROUP_WATERBIRTH;
        public static final int CURE_GROUP = net.titan.gamevals.InterfaceID.MagicSpellbook.CURE_GROUP;
        public static final int STAT_SPY = net.titan.gamevals.InterfaceID.MagicSpellbook.STAT_SPY;
        public static final int TELE_BARB_OUT = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_BARB_OUT;
        public static final int TELE_GROUP_BARBARIAN = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_GROUP_BARBARIAN;
        public static final int SUPERGLASS = net.titan.gamevals.InterfaceID.MagicSpellbook.SUPERGLASS;
        public static final int TAN_LEATHER = net.titan.gamevals.InterfaceID.MagicSpellbook.TAN_LEATHER;
        public static final int TELE_KHAZARD = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_KHAZARD;
        public static final int TELE_GROUP_KHAZARD = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_GROUP_KHAZARD;
        public static final int DREAM = net.titan.gamevals.InterfaceID.MagicSpellbook.DREAM;
        public static final int STRING_JEWEL = net.titan.gamevals.InterfaceID.MagicSpellbook.STRING_JEWEL;
        public static final int REST_POT_SHARE = net.titan.gamevals.InterfaceID.MagicSpellbook.REST_POT_SHARE;
        public static final int MAGIC_IMBUE = net.titan.gamevals.InterfaceID.MagicSpellbook.MAGIC_IMBUE;
        public static final int FERTILE_SOIL = net.titan.gamevals.InterfaceID.MagicSpellbook.FERTILE_SOIL;
        public static final int STREN_POT_SHARE = net.titan.gamevals.InterfaceID.MagicSpellbook.STREN_POT_SHARE;
        public static final int TELE_FISH = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_FISH;
        public static final int TELE_GROUP_FISHING_GUILD = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_GROUP_FISHING_GUILD;
        public static final int PLANK_MAKE = net.titan.gamevals.InterfaceID.MagicSpellbook.PLANK_MAKE;
        public static final int TELE_CATHER = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_CATHER;
        public static final int TELE_GROUP_CATHERBY = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_GROUP_CATHERBY;
        public static final int RECHARGE_DRAGONSTONE = net.titan.gamevals.InterfaceID.MagicSpellbook.RECHARGE_DRAGONSTONE;
        public static final int TELE_GHORROCK = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_GHORROCK;
        public static final int TELE_GROUP_GHORROCK = net.titan.gamevals.InterfaceID.MagicSpellbook.TELE_GROUP_GHORROCK;
        public static final int ENERGY_TRANS = net.titan.gamevals.InterfaceID.MagicSpellbook.ENERGY_TRANS;
        public static final int HEAL_OTHER = net.titan.gamevals.InterfaceID.MagicSpellbook.HEAL_OTHER;
        public static final int VENGEANCE_OTHER = net.titan.gamevals.InterfaceID.MagicSpellbook.VENGEANCE_OTHER;
        public static final int VENGEANCE = net.titan.gamevals.InterfaceID.MagicSpellbook.VENGEANCE;
        public static final int HEAL_GROUP = net.titan.gamevals.InterfaceID.MagicSpellbook.HEAL_GROUP;
        public static final int SPELLBOOK_SWAP = net.titan.gamevals.InterfaceID.MagicSpellbook.SPELLBOOK_SWAP;
        public static final int GEOMANCY = net.titan.gamevals.InterfaceID.MagicSpellbook.GEOMANCY;
        public static final int SPIN_FLAX = net.titan.gamevals.InterfaceID.MagicSpellbook.SPIN_FLAX;
        public static final int OURANIA_TELEPORT = net.titan.gamevals.InterfaceID.MagicSpellbook.OURANIA_TELEPORT;
        public static final int TELEPORT_HOME_ARCEUUS = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_HOME_ARCEUUS;
        public static final int REANIMATION_BASIC = net.titan.gamevals.InterfaceID.MagicSpellbook.REANIMATION_BASIC;
        public static final int TELEPORT_ARCEUUS_LIBRARY = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_ARCEUUS_LIBRARY;
        public static final int REANIMATION_ADEPT = net.titan.gamevals.InterfaceID.MagicSpellbook.REANIMATION_ADEPT;
        public static final int REANIMATION_EXPERT = net.titan.gamevals.InterfaceID.MagicSpellbook.REANIMATION_EXPERT;
        public static final int REANIMATION_MASTER = net.titan.gamevals.InterfaceID.MagicSpellbook.REANIMATION_MASTER;
        public static final int TELEPORT_DRAYNOR_MANOR = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_DRAYNOR_MANOR;
        public static final int NECROMANCY_DOG = net.titan.gamevals.InterfaceID.MagicSpellbook.NECROMANCY_DOG;
        public static final int TELEPORT_MIND_ALTAR = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_MIND_ALTAR;
        public static final int TELEPORT_RESPAWN = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_RESPAWN;
        public static final int TELEPORT_SALVE_GRAVEYARD = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_SALVE_GRAVEYARD;
        public static final int TELEPORT_FENKENSTRAIN_CASTLE = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_FENKENSTRAIN_CASTLE;
        public static final int TELEPORT_WEST_ARDOUGNE = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_WEST_ARDOUGNE;
        public static final int TELEPORT_HARMONY_ISLAND = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_HARMONY_ISLAND;
        public static final int TELEPORT_CEMETERY = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_CEMETERY;
        public static final int RESURRECT_CROPS = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_CROPS;
        public static final int TELEPORT_BARROWS = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_BARROWS;
        public static final int TELEPORT_APE_ATOLL_DUNGEON = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_APE_ATOLL_DUNGEON;
        public static final int TELEPORT_BATTLEFRONT = net.titan.gamevals.InterfaceID.MagicSpellbook.TELEPORT_BATTLEFRONT;
        public static final int INFERIOR_DEMONBANE = net.titan.gamevals.InterfaceID.MagicSpellbook.INFERIOR_DEMONBANE;
        public static final int SUPERIOR_DEMONBANE = net.titan.gamevals.InterfaceID.MagicSpellbook.SUPERIOR_DEMONBANE;
        public static final int DARK_DEMONBANE = net.titan.gamevals.InterfaceID.MagicSpellbook.DARK_DEMONBANE;
        public static final int MARK_OF_DARKNESS = net.titan.gamevals.InterfaceID.MagicSpellbook.MARK_OF_DARKNESS;
        public static final int GHOSTLY_GRASP = net.titan.gamevals.InterfaceID.MagicSpellbook.GHOSTLY_GRASP;
        public static final int SKELETAL_GRASP = net.titan.gamevals.InterfaceID.MagicSpellbook.SKELETAL_GRASP;
        public static final int UNDEAD_GRASP = net.titan.gamevals.InterfaceID.MagicSpellbook.UNDEAD_GRASP;
        public static final int WARD_OF_ARCEUUS = net.titan.gamevals.InterfaceID.MagicSpellbook.WARD_OF_ARCEUUS;
        public static final int LESSER_CORRUPTION = net.titan.gamevals.InterfaceID.MagicSpellbook.LESSER_CORRUPTION;
        public static final int GREATER_CORRUPTION = net.titan.gamevals.InterfaceID.MagicSpellbook.GREATER_CORRUPTION;
        public static final int DEMONIC_OFFERING = net.titan.gamevals.InterfaceID.MagicSpellbook.DEMONIC_OFFERING;
        public static final int SINISTER_OFFERING = net.titan.gamevals.InterfaceID.MagicSpellbook.SINISTER_OFFERING;
        public static final int DEGRIME = net.titan.gamevals.InterfaceID.MagicSpellbook.DEGRIME;
        public static final int SHADOW_VEIL = net.titan.gamevals.InterfaceID.MagicSpellbook.SHADOW_VEIL;
        public static final int VILE_VIGOUR = net.titan.gamevals.InterfaceID.MagicSpellbook.VILE_VIGOUR;
        public static final int DARK_LURE = net.titan.gamevals.InterfaceID.MagicSpellbook.DARK_LURE;
        public static final int DEATH_CHARGE = net.titan.gamevals.InterfaceID.MagicSpellbook.DEATH_CHARGE;
        public static final int RESURRECT_LESSER_GHOST = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_LESSER_GHOST;
        public static final int RESURRECT_LESSER_SKELETON = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_LESSER_SKELETON;
        public static final int RESURRECT_LESSER_ZOMBIE = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_LESSER_ZOMBIE;
        public static final int RESURRECT_SUPERIOR_GHOST = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_GHOST;
        public static final int RESURRECT_SUPERIOR_SKELETON = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_SKELETON;
        public static final int RESURRECT_SUPERIOR_ZOMBIE = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_SUPERIOR_ZOMBIE;
        public static final int RESURRECT_GREATER_GHOST = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_GREATER_GHOST;
        public static final int RESURRECT_GREATER_SKELETON = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_GREATER_SKELETON;
        public static final int RESURRECT_GREATER_ZOMBIE = net.titan.gamevals.InterfaceID.MagicSpellbook.RESURRECT_GREATER_ZOMBIE;
        public static final int MONSTER_INSPECT = net.titan.gamevals.InterfaceID.MagicSpellbook.MONSTER_INSPECT;
        public static final int TRANSMUTE_UPGRADE = net.titan.gamevals.InterfaceID.MagicSpellbook.TRANSMUTE_UPGRADE;
        public static final int TRANSMUTE_DOWNGRADE = net.titan.gamevals.InterfaceID.MagicSpellbook.TRANSMUTE_DOWNGRADE;
        public static final int INFOLAYER = net.titan.gamevals.InterfaceID.MagicSpellbook.INFOLAYER;
        public static final int INFOLAYER_GRAPHIC0 = net.titan.gamevals.InterfaceID.MagicSpellbook.COM_202;
        public static final int FILTERMENU_CONTAINER = net.titan.gamevals.InterfaceID.MagicSpellbook.FILTERMENU_CONTAINER;
        public static final int FILTERMENU_CONTAINER_GRAPHIC0 = net.titan.gamevals.InterfaceID.MagicSpellbook.COM_204;
        public static final int FILTERMENU_CONTAINER_TEXT1 = net.titan.gamevals.InterfaceID.MagicSpellbook.COM_205;
        public static final int FILTERMENU = net.titan.gamevals.InterfaceID.MagicSpellbook.FILTERMENU;
        public static final int BOTTOM = net.titan.gamevals.InterfaceID.MagicSpellbook.BOTTOM;
        public static final int INFOBUTTON = net.titan.gamevals.InterfaceID.MagicSpellbook.INFOBUTTON;
        public static final int FILTERBUTTON = net.titan.gamevals.InterfaceID.MagicSpellbook.FILTERBUTTON;
        public static final int TOOLTIP = net.titan.gamevals.InterfaceID.MagicSpellbook.TOOLTIP;
    }
}
