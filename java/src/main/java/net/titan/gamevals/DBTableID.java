package net.titan.gamevals;

import java.util.Optional;

import net.titan.gamevals.internal.DBTableIDEntries;

public final class DBTableID
        implements DBTableIDConstants_0 {
    private DBTableID() {}

    public static final String SOURCE_CATALOG = "tabletypes";

    public static String sourceCatalog() { return SOURCE_CATALOG; }

    public static final class Quest {
        private Quest() {}

        public static final int ID = 0;
        public static final int COL_ID = 0;
        public static final int COL_SORTNAME = 1;
        public static final int COL_DISPLAYNAME = 2;
        public static final int COL_RELEASE_TYPE = 3;
        public static final int COL_TYPE = 4;
        public static final int COL_MEMBERS = 5;
        public static final int COL_DIFFICULTY = 6;
        public static final int COL_LENGTH = 7;
        public static final int COL_LOCATION = 8;
        public static final int COL_RELEASEDATE = 9;
        public static final int COL_SERIES = 10;
        public static final int COL_SERIESNO = 11;
        public static final int COL_SERIESNO_OVERRIDE = 12;
        public static final int COL_STARTCOORD = 13;
        public static final int COL_STARTNPC = 14;
        public static final int COL_STARTLOC = 15;
        public static final int COL_MAPELEMENT = 16;
        public static final int COL_QUESTPOINTS = 17;
        public static final int COL_UNSTARTEDSTATE = 18;
        public static final int COL_ENDSTATE = 19;
        public static final int COL_VERSION = 20;
        public static final int COL_PARENT_QUEST = 21;
        public static final int COL_HAS_SUBQUESTS = 22;
        public static final int COL_REQUIREMENT_STATS = 23;
        public static final int COL_RECOMMENDED_STATS = 24;
        public static final int COL_REQUIREMENT_QUESTS = 25;
        public static final int COL_REQUIREMENT_QUESTPOINTS = 26;
        public static final int COL_REQUIREMENT_COMBAT = 27;
        public static final int COL_RECOMMENDED_COMBAT = 28;
        public static final int COL_REQUIREMENT_CHECK_SKILLS_ON_START = 29;
        public static final int COL_REQUIREMENTS_BOOSTABLE = 30;
        public static final int COL_SPEEDRUN = 31;
        public static final int COL_TOTAL_XP_AWARDED = 32;
        public static final int COL_STAT_XP_AWARDED = 33;
        public static final int COL_PREREQUISITE_DIRECT = 34;
        public static final int COL_PREREQUISITE_INDIRECT = 35;
        public static final int COL_FTUE_STARTER = 36;
        public static final int COL_CR_CAN_RECOMMEND = 37;
        public static final int COL_CR_EXPERIENCE_PROFILE = 38;
        public static final int COL_CR_RECOMMENDATION_REASON = 39;
        public static final int COL_CR_RECOMMENDATION_REASON_IS_PRIMARY = 40;
        public static final int COL_CR_POINTS_SKILL = 41;
        public static final int COL_CR_POINTS_TRANSPORT = 42;
        public static final int COL_CR_POINTS_EQUIPMENT = 43;
        public static final int COL_CR_POINTS_AREA = 44;
        public static final int COL_CR_POINTS_XP_TYPE = 45;
        public static final int COL_CR_STARTER = 46;
        public static final int COL_FSW_WORLD_FIRST_ID = 47;
        public static final int COL_RELATED_CONTENT = 48;

        public static final class Row {
            private Row() {}

            public static final int QUEST_ANIMALMAGNETISM = 0;
            public static final int QUEST_ANOTHERSLICEOFHAM = 1;
            public static final int QUEST_ASCENTOFARCEUUS = 3;
            public static final int MINIQUEST_BARCRAWL = 4;
            public static final int MINIQUEST_BEARYOURSOUL = 5;
            public static final int QUEST_BELOWICEMOUNTAIN = 6;
            public static final int QUEST_BETWEENAROCK = 7;
            public static final int QUEST_BIGCHOMPYBIRDHUNTING = 8;
            public static final int QUEST_BIOHAZARD = 9;
            public static final int QUEST_BLACKKNIGHTSFORTRESS = 10;
            public static final int QUEST_BONEVOYAGE = 11;
            public static final int QUEST_CABINFEVER = 12;
            public static final int QUEST_CLIENTOFKOUREND = 13;
            public static final int QUEST_CLOCKTOWER = 14;
            public static final int QUEST_COLDWAR = 15;
            public static final int QUEST_CONTACT = 16;
            public static final int QUEST_COOKSASSISTANT = 17;
            public static final int QUEST_CORSAIRCURSE = 18;
            public static final int QUEST_CREATUREOFFENKENSTRAIN = 19;
            public static final int MINIQUEST_CURSEOFTHEEMPTYLORD = 20;
            public static final int MINIQUEST_DADDYSHOME = 21;
            public static final int QUEST_DARKNESSOFHALLOWVALE = 22;
            public static final int QUEST_DEATHPLATEAU = 23;
            public static final int QUEST_DEATHTOTHEDORGESHUUN = 24;
            public static final int QUEST_DEMONSLAYER = 25;
            public static final int QUEST_DEPTHSOFDESPAIR = 26;
            public static final int QUEST_DESERTTREASURE = 27;
            public static final int QUEST_DEVIOUSMINDS = 28;
            public static final int QUEST_DIGSITE = 29;
            public static final int QUEST_DORICS = 30;
            public static final int QUEST_DRAGONSLAYER1 = 31;
            public static final int QUEST_DRAGONSLAYER2 = 32;
            public static final int QUEST_DREAMMENTOR = 33;
            public static final int QUEST_DRUIDICRITUAL = 34;
            public static final int QUEST_DWARFCANNON = 35;
            public static final int QUEST_EADGARSRUSE = 36;
            public static final int QUEST_EAGLESPEAK = 37;
            public static final int QUEST_ELEMENTALWORKSHOP1 = 38;
            public static final int QUEST_ELEMENTALWORKSHOP2 = 39;
            public static final int QUEST_ENAKHRASLAMENT = 40;
            public static final int MINIQUEST_ENCHANTEDKEY = 41;
            public static final int QUEST_ENLIGHTENEDJOURNEY = 42;
            public static final int MINIQUEST_ENTERTHEABYSS = 43;
            public static final int QUEST_ERNESTTHECHICKEN = 44;
            public static final int QUEST_EYESOFGLOUPHRIE = 45;
            public static final int QUEST_FAIRYTALE1 = 46;
            public static final int QUEST_FAIRYTALE2 = 47;
            public static final int QUEST_FAMILYCREST = 48;
            public static final int MINIQUEST_FAMILYPEST = 49;
            public static final int QUEST_FEUD = 50;
            public static final int QUEST_FIGHTARENA = 51;
            public static final int QUEST_FISHINGCONTEST = 52;
            public static final int QUEST_FORGETTABLETALE = 53;
            public static final int QUEST_FORSAKENTOWER = 54;
            public static final int QUEST_FREMENNIKEXILES = 55;
            public static final int QUEST_FREMENNIKISLES = 56;
            public static final int QUEST_FREMENNIKTRIALS = 57;
            public static final int QUEST_GARDENOFTRANQUILLITY = 58;
            public static final int MINIQUEST_GENERALSSHADOW = 59;
            public static final int QUEST_GERTRUDESCAT = 60;
            public static final int QUEST_GETTINGAHEAD = 61;
            public static final int QUEST_GHOSTSAHOY = 62;
            public static final int QUEST_GIANTDWARF = 63;
            public static final int QUEST_GOBLINDIPLOMACY = 64;
            public static final int QUEST_GOLEM = 65;
            public static final int QUEST_GRANDTREE = 66;
            public static final int QUEST_GREATBRAINROBBERY = 67;
            public static final int QUEST_GRIMTALES = 68;
            public static final int QUEST_HANDINTHESAND = 69;
            public static final int QUEST_HAUNTEDMINE = 70;
            public static final int QUEST_HAZEELCULT = 71;
            public static final int QUEST_HEROES = 72;
            public static final int QUEST_HOLYGRAIL = 73;
            public static final int QUEST_HORRORFROMTHEDEEP = 74;
            public static final int QUEST_ICTHLARINSLITTLEHELPER = 75;
            public static final int QUEST_IMPCATCHER = 76;
            public static final int QUEST_INAIDOFTHEMYREQUE = 77;
            public static final int MINIQUEST_INSEARCHOFKNOWLEDGE = 78;
            public static final int QUEST_INSEARCHOFTHEMYREQUE = 79;
            public static final int QUEST_JUNGLEPOTION = 80;
            public static final int QUEST_KINGDOMDIVIDED = 81;
            public static final int QUEST_KINGSRANSOM = 82;
            public static final int QUEST_KNIGHTSSWORD = 83;
            public static final int MINIQUEST_LAIROFTARNRAZORLOR = 84;
            public static final int QUEST_LEGENDS = 85;
            public static final int QUEST_LOSTCITY = 86;
            public static final int QUEST_LOSTTRIBE = 87;
            public static final int QUEST_LUNARDIPLOMACY = 88;
            public static final int MINIQUEST_MAGEARENA1 = 89;
            public static final int MINIQUEST_MAGEARENA2 = 90;
            public static final int QUEST_MAKINGFRIENDSWITHMYARM = 91;
            public static final int QUEST_MAKINGHISTORY = 92;
            public static final int QUEST_MERLINSCRYSTAL = 93;
            public static final int QUEST_MISTHALINMYSTERY = 94;
            public static final int QUEST_MONKEYMADNESS1 = 95;
            public static final int QUEST_MONKEYMADNESS2 = 96;
            public static final int QUEST_MONKSFRIEND = 97;
            public static final int QUEST_MOUNTAINDAUGHTER = 98;
            public static final int QUEST_MOURNINGSENDPART1 = 99;
            public static final int QUEST_MOURNINGSENDPART2 = 100;
            public static final int QUEST_MURDERMYSTERY = 101;
            public static final int QUEST_MYARMSBIGADVENTURE = 102;
            public static final int QUEST_NATURESPIRIT = 103;
            public static final int QUEST_NIGHTATTHETHEATRE = 104;
            public static final int QUEST_OBSERVATORY = 105;
            public static final int QUEST_OLAFS = 106;
            public static final int QUEST_ONESMALLFAVOUR = 107;
            public static final int QUEST_PIRATESTREASURE = 108;
            public static final int QUEST_PLAGUECITY = 109;
            public static final int QUEST_PORCINEOFINTEREST = 110;
            public static final int QUEST_PRIESTINPERIL = 111;
            public static final int QUEST_PRINCEALIRESCUE = 112;
            public static final int QUEST_QUEENOFTHIEVES = 113;
            public static final int QUEST_RAGANDBONEMAN1 = 114;
            public static final int QUEST_RAGANDBONEMAN2 = 115;
            public static final int QUEST_RATCATCHERS = 116;
            public static final int QUEST_RECIPEFORDISASTER = 117;
            public static final int QUEST_RECRUITMENTDRIVE = 118;
            public static final int QUEST_REGICIDE = 119;
            public static final int QUEST_RESTLESSGHOST = 120;
            public static final int QUEST_ROMEOANDJULIET = 121;
            public static final int QUEST_ROVINGELVES = 122;
            public static final int QUEST_ROYALTROUBLE = 123;
            public static final int QUEST_RUMDEAL = 124;
            public static final int QUEST_RUNEMYSTERIES = 125;
            public static final int QUEST_SCORPIONCATCHER = 126;
            public static final int QUEST_SEASLUG = 127;
            public static final int QUEST_SHADESOFMORTTON = 128;
            public static final int QUEST_SHADOWOFTHESTORM = 129;
            public static final int QUEST_SHEEPHERDER = 130;
            public static final int QUEST_SHEEPSHEARER = 131;
            public static final int QUEST_SHIELDOFARRAV = 132;
            public static final int QUEST_SHILOVILLAGE = 133;
            public static final int QUEST_SINSOFTHEFATHER = 134;
            public static final int MINIQUEST_SKIPPYANDTHEMOGRES = 135;
            public static final int QUEST_SLUGMENACE = 136;
            public static final int QUEST_SONGOFTHEELVES = 137;
            public static final int QUEST_SOULSBANE = 138;
            public static final int QUEST_SPIRITSOFTHEELID = 139;
            public static final int QUEST_SWANSONG = 140;
            public static final int QUEST_TAIBWOWANNAITRIO = 141;
            public static final int QUEST_TAILOFTWOCATS = 142;
            public static final int QUEST_TALEOFTHERIGHTEOUS = 143;
            public static final int QUEST_TASTEOFHOPE = 144;
            public static final int QUEST_TEARSOFGUTHIX = 145;
            public static final int QUEST_TEMPLEOFIKOV = 146;
            public static final int QUEST_THRONEOFMISCELLANIA = 147;
            public static final int QUEST_TOURISTTRAP = 148;
            public static final int QUEST_TOWEROFLIFE = 149;
            public static final int QUEST_TREEGNOMEVILLAGE = 150;
            public static final int QUEST_TRIBALTOTEM = 151;
            public static final int QUEST_TROLLROMANCE = 152;
            public static final int QUEST_TROLLSTRONGHOLD = 153;
            public static final int QUEST_UNDERGROUNDPASS = 154;
            public static final int QUEST_VAMPYRESLAYER = 155;
            public static final int QUEST_WANTED = 156;
            public static final int QUEST_WATCHTOWER = 157;
            public static final int QUEST_WATERFALL = 158;
            public static final int QUEST_WHATLIESBELOW = 159;
            public static final int QUEST_WITCHSHOUSE = 160;
            public static final int QUEST_WITCHSPOTION = 161;
            public static final int QUEST_XMARKSTHESPOT = 162;
            public static final int QUEST_ZOGREFLESHEATERS = 163;
            public static final int MINIQUEST_FROZENDOOR = 164;
            public static final int QUEST_LANDOFTHEGOBLINS = 165;
            public static final int MINIQUEST_HOPESPEARSWILL = 166;
            public static final int QUEST_TEMPLEOFTHEEYE = 167;
            public static final int QUEST_BENEATHCURSEDSANDS = 168;
            public static final int QUEST_SLEEPINGGIANTS = 169;
            public static final int QUEST_GARDENOFDEATH = 180;
            public static final int MINIQUEST_INTOTHETOMBS = 2306;
            public static final int SUBQUEST_RFD_INTRO = 2307;
            public static final int SUBQUEST_RFD_DWARF = 2308;
            public static final int SUBQUEST_RFD_GOBLINS = 2309;
            public static final int SUBQUEST_RFD_PIRATE = 2310;
            public static final int SUBQUEST_RFD_LUMBRIDGEGUIDE = 2311;
            public static final int SUBQUEST_RFD_EVILDAVE = 2312;
            public static final int SUBQUEST_RFD_OGRE = 2313;
            public static final int SUBQUEST_RFD_AMIKVARZE = 2314;
            public static final int SUBQUEST_RFD_MONKEY = 2315;
            public static final int SUBQUEST_RFD_FINALE = 2316;
            public static final int QUEST_SECRETSOFTHENORTH = 2338;
            public static final int QUEST_DESERTTREASURE2 = 2343;
            public static final int MINIQUEST_HISFAITHFULSERVANTS = 3250;
            public static final int QUEST_PATHOFGLOUPHRIE = 3425;
            public static final int QUEST_CHILDRENOFTHESUN = 3450;
            public static final int MINIQUEST_BARBARIANTRAINING = 3451;
            public static final int QUEST_DEFENDEROFVARROCK = 3466;
            public static final int QUEST_WHILEGUTHIXSLEEPS = 3467;
            public static final int QUEST_TWILIGHTSPROMISE = 3512;
            public static final int QUEST_ATFIRSTLIGHT = 3513;
            public static final int QUEST_PERILOUSMOONS = 3514;
            public static final int QUEST_RIBBITINGTALE = 3515;
            public static final int QUEST_HEARTOFDARKNESS = 3710;
            public static final int QUEST_DEATHONTHEISLE = 3711;
            public static final int QUEST_MEATANDGREET = 3712;
            public static final int QUEST_ETHICALLYACQUIREDANTIQUITIES = 3713;
            public static final int QUEST_CURSEOFARRAV = 3937;
            public static final int QUEST_FINALDAWN = 5189;
            public static final int QUEST_SHADOWSOFCUSTODIA = 5190;
            public static final int QUEST_SCRAMBLED = 5191;
            public static final int QUEST_EXISTENTIALCRISIS = 5192;
            public static final int QUEST_IMPENDINGCHAOS = 5193;
            public static final int MINIQUEST_VALETOTEMS = 5194;
            public static final int QUEST_PANDEMONIUM = 7103;
            public static final int QUEST_PRYINGTIMES = 7104;
            public static final int QUEST_CURRENTAFFAIRS = 7105;
            public static final int QUEST_TROUBLEDTORTUGANS = 7106;
            public static final int QUEST_REDREEF = 7107;
            public static final int QUEST_BURIALATSEA = 7108;
            public static final int QUEST_LEARNINGTHEROPES = 9643;
            public static final int QUEST_IDESOFMILK = 9645;
            public static final int QUEST_BLOODMOONRISES = 16414;
        }
    }

    public static final class Events {
        private Events() {}

        public static final int ID = 1;
        public static final int COL_NAME = 0;
        public static final int COL_YEAR_OF_RELEASE = 1;
        public static final int COL_TYPE = 2;
        public static final int COL_IDENTIFIER_OBJ = 3;
        public static final int COL_REWARD_NAME = 4;
        public static final int COL_REWARD_OBJ = 5;
        public static final int COL_FINISHED_STATE = 6;
        public static final int COL_FAREWELL_MESSAGE = 7;
        public static final int COL_REWARD_USESYSTEM = 8;

        public static final class Row {
            private Row() {}

            public static final int XMAS_2022 = 2331;
            public static final int BIRTHDAY_2023 = 2341;
            public static final int EASTER_2023 = 2342;
            public static final int PRIDE_2023 = 2506;
            public static final int BIRTHDAY_2024 = 3288;
            public static final int HALLOWEEN_2023 = 3431;
            public static final int XMAS_2023 = 3446;
            public static final int EASTER_2024 = 3528;
            public static final int PRIDE_2024 = 3594;
            public static final int HALLOWEEN_2024 = 3935;
            public static final int XMAS_2024 = 4228;
            public static final int BIRTHDAY_2025 = 4318;
            public static final int XMAS_2025 = 4968;
            public static final int EASTER_2025 = 5107;
            public static final int HALLOWEEN_2025 = 6989;
            public static final int BIRTHDAY_2026 = 7033;
            public static final int EASTER_2026 = 13319;
        }
    }

    public static final class CrModule {
        private CrModule() {}

        public static final int ID = 2;
        public static final int COL_DISPLAYNAME = 0;
        public static final int COL_ICON = 1;
        public static final int COL_ICON_OFFSET = 2;
        public static final int COL_CONTENT_CONTAINER = 3;

        public static final class Row {
            private Row() {}

            public static final int CR_MODULE_QUESTS = 4143;
            public static final int CR_MODULE_SKILLS = 4144;
            public static final int CR_MODULE_UTILITY = 4145;
            public static final int CR_MODULE_MISC = 4146;
            public static final int UNCOOKED_BERRY_PIE_RECIPE = 16915;
            public static final int PIE_SHELL_RECIPE = 16916;
            public static final int BREAD_RECIPE = 16917;
            public static final int PASTRY_DOUGH_RECIPE = 16918;
            public static final int BREAD_DOUGH_RECIPE = 16919;
        }
    }

    public static final class CluehelperCluetype {
        private CluehelperCluetype() {}

        public static final int ID = 3;
        public static final int COL_NAME = 0;
        public static final int COL_QUEST_CLUES = 1;
        public static final int COL_BEGINNER_CLUES = 2;
        public static final int COL_EASY_CLUES = 3;
        public static final int COL_MEDIUM_CLUES = 4;
        public static final int COL_HARD_CLUES = 5;
        public static final int COL_ELITE_CLUES = 6;
        public static final int COL_MASTER_CLUES = 7;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_CLUETYPE_HOTCOLD = 684;
            public static final int CLUEHELPER_CLUETYPE_ANAGRAM = 1498;
            public static final int CLUEHELPER_CLUETYPE_MAP = 2222;
        }
    }

    public static final class CluehelperClueAnagram {
        private CluehelperClueAnagram() {}

        public static final int ID = 4;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_CLUE_TEXT = 2;
        public static final int COL_TARGET = 3;
        public static final int COL_CHALLENGE = 4;
        public static final int COL_REGION = 5;
        public static final int COL_REQUIREMENTS = 6;
        public static final int COL_ALLREGIONS = 7;
        public static final int COL_RELATED_CONTENT = 8;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_ANAGRAM_BEGINNER_RANAEL = 1499;
            public static final int CLUEHELPER_ANAGRAM_BEGINNER_APOTHECARY = 1500;
            public static final int CLUEHELPER_ANAGRAM_BEGINNER_SEDRIDOR = 1501;
            public static final int CLUEHELPER_ANAGRAM_BEGINNER_DORIC = 1502;
            public static final int CLUEHELPER_ANAGRAM_BEGINNER_BRIAN = 1503;
            public static final int CLUEHELPER_ANAGRAM_BEGINNER_VERONICA = 1504;
            public static final int CLUEHELPER_ANAGRAM_BEGINNER_GERTRUDE = 1505;
            public static final int CLUEHELPER_ANAGRAM_BEGINNER_HAIRDRESSER = 1506;
            public static final int CLUEHELPER_ANAGRAM_BEGINNER_FORTUNATO = 1507;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_BARAEK = 1508;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_SABA = 1509;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_CAPTAIN_TOBIAS = 1510;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_JARAAH = 1511;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_ARETHA = 1512;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_CAROLINE = 1513;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_FATHER_AERECK = 1514;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_ORACLE = 1515;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_CHARLIE_THE_TRAMP = 1516;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_BRIMSTAIL = 1517;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_MADAME_CALDARIUM = 1518;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_NICHOLAS = 1519;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_BRUNDT_THE_CHIEFTAIN = 1520;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_ZOO_KEEPER = 1521;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_LOWE = 1522;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_OTTO_GODBLESSED = 1523;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_KING_BOLREN = 1524;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_GABOOTY = 1525;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_LUTHAS = 1526;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_EOHRIC = 1527;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_JETHICK = 1528;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_HORPHIS = 1529;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_MARISI = 1530;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_FYCIE = 1531;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_DOMINIC_ONION = 1532;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_NIEVE = 1533;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_SIR_KAY = 1534;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_KING_ROALD = 1535;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_KAYLEE = 1536;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_GALLOW = 1537;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_FEMI = 1538;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_EDMOND = 1539;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_COOK = 1540;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_CAPTAIN_GINEA = 1541;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_FLAX_KEEPER = 1542;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_PARTY_PETE = 1543;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_SQUIRE = 1544;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_KARIM = 1545;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_TARIA = 1546;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_CLERRIS = 1547;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_DUNSTAN = 1548;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_DOCKMASTER = 1549;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_HICKTON = 1550;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_DRUNKEN_SOLDIER = 1551;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_STEVE = 1552;
            public static final int CLUEHELPER_ANAGRAM_HARD_BRAMBICKLE = 1553;
            public static final int CLUEHELPER_ANAGRAM_HARD_LUMBRIDGE_GUIDE = 1554;
            public static final int CLUEHELPER_ANAGRAM_HARD_BOLKOY = 1555;
            public static final int CLUEHELPER_ANAGRAM_HARD_GNOME_COACH = 1556;
            public static final int CLUEHELPER_ANAGRAM_HARD_PROSPECTOR_PERCY = 1557;
            public static final int CLUEHELPER_ANAGRAM_HARD_DAER_KRAND = 1558;
            public static final int CLUEHELPER_ANAGRAM_HARD_DARK_MAGE = 1559;
            public static final int CLUEHELPER_ANAGRAM_HARD_DOOMSAYER = 1560;
            public static final int CLUEHELPER_ANAGRAM_HARD_STRANGE_OLD_MAN = 1561;
            public static final int CLUEHELPER_ANAGRAM_HARD_DRUNKEN_DWARF = 1562;
            public static final int CLUEHELPER_ANAGRAM_HARD_BROTHER_OMAD = 1563;
            public static final int CLUEHELPER_ANAGRAM_HARD_LAMMY_LANGLE = 1564;
            public static final int CLUEHELPER_ANAGRAM_HARD_CAPN_IZZY_NO_BEARD = 1565;
            public static final int CLUEHELPER_ANAGRAM_HARD_BROTHER_TRANQUILITY = 1566;
            public static final int CLUEHELPER_ANAGRAM_HARD_MARTIN_THWAIT = 1567;
            public static final int CLUEHELPER_ANAGRAM_HARD_WISE_OLD_MAN = 1568;
            public static final int CLUEHELPER_ANAGRAM_HARD_EVIL_DAVE = 1569;
            public static final int CLUEHELPER_ANAGRAM_HARD_KING_AWOWOGEI = 1570;
            public static final int CLUEHELPER_ANAGRAM_ELITE_REGATH = 1571;
            public static final int CLUEHELPER_ANAGRAM_ELITE_ONEIROMANCER = 1572;
            public static final int CLUEHELPER_ANAGRAM_ELITE_OLD_CRONE = 1573;
            public static final int CLUEHELPER_ANAGRAM_ELITE_MANDRITH = 1574;
            public static final int CLUEHELPER_ANAGRAM_ELITE_GUARD_VEMMELDO = 1575;
            public static final int CLUEHELPER_ANAGRAM_ELITE_CAM_THE_CAMEL = 1576;
            public static final int CLUEHELPER_ANAGRAM_ELITE_AMBASSADOR_ALVIJAR = 1577;
            public static final int CLUEHELPER_ANAGRAM_ELITE_ORONWEN = 1578;
            public static final int CLUEHELPER_ANAGRAM_ELITE_NURSE_WOONED = 1579;
            public static final int CLUEHELPER_ANAGRAM_ELITE_LISSE_ISAAKSON = 1580;
            public static final int CLUEHELPER_ANAGRAM_ELITE_SIGLI_THE_HUNTSMAN = 1581;
            public static final int CLUEHELPER_ANAGRAM_ELITE_JARDRIC = 1582;
            public static final int CLUEHELPER_ANAGRAM_MASTER_SNOWFLAKE = 1583;
            public static final int CLUEHELPER_ANAGRAM_MASTER_CAPTAIN_BRUCE = 1584;
            public static final int CLUEHELPER_ANAGRAM_MASTER_SACRIFICE = 1585;
            public static final int CLUEHELPER_ANAGRAM_MASTER_EDWARD = 1586;
            public static final int CLUEHELPER_ANAGRAM_MASTER_MANDRITH = 1587;
            public static final int CLUEHELPER_ANAGRAM_MASTER_DUGOPUL = 1588;
            public static final int CLUEHELPER_ANAGRAM_MASTER_RUNOLF = 1589;
            public static final int CLUEHELPER_ANAGRAM_MASTER_IMMENIZZ = 1590;
            public static final int CLUEHELPER_ANAGRAM_MASTER_LUMINATA = 1591;
            public static final int CLUEHELPER_ANAGRAM_MASTER_OLD_MAN_RAL = 1592;
            public static final int CLUEHELPER_ANAGRAM_MASTER_RADIMUS_ERKLE = 1593;
            public static final int CLUEHELPER_ANAGRAM_MASTER_PRIMULA = 1594;
            public static final int CLUEHELPER_ANAGRAM_MASTER_GOREU = 1595;
            public static final int CLUEHELPER_ANAGRAM_MASTER_GUILDMASTER_LARS = 1596;
            public static final int CLUEHELPER_ANAGRAM_MASTER_WINGSTONE = 1597;
            public static final int CLUEHELPER_ANAGRAM_MASTER_NEW_RECRUIT_TONY = 1598;
            public static final int CLUEHELPER_ANAGRAM_SOTE_3 = 2299;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_METLA = 3498;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_TEICUH = 5175;
            public static final int CLUEHELPER_ANAGRAM_MEDIUM_TORGAN = 7041;
        }
    }

    public static final class CluehelperClueMap {
        private CluehelperClueMap() {}

        public static final int ID = 5;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_TARGET = 2;
        public static final int COL_REQUIREMENTS = 3;
        public static final int COL_REGION = 4;
        public static final int COL_ALLREGIONS = 5;
        public static final int COL_RELATED_CONTENT = 6;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_MAP_BEGINNER_0 = 2223;
            public static final int CLUEHELPER_MAP_BEGINNER_1 = 2224;
            public static final int CLUEHELPER_MAP_BEGINNER_2 = 2225;
            public static final int CLUEHELPER_MAP_BEGINNER_3 = 2226;
            public static final int CLUEHELPER_MAP_BEGINNER_4 = 2227;
            public static final int CLUEHELPER_MAP_EASY_0 = 2228;
            public static final int CLUEHELPER_MAP_EASY_1 = 2229;
            public static final int CLUEHELPER_MAP_EASY_2 = 2230;
            public static final int CLUEHELPER_MAP_EASY_3 = 2231;
            public static final int CLUEHELPER_MAP_EASY_4 = 2232;
            public static final int CLUEHELPER_MAP_EASY_5 = 2233;
            public static final int CLUEHELPER_MAP_EASY_6 = 2234;
            public static final int CLUEHELPER_MAP_MEDIUM_0 = 2235;
            public static final int CLUEHELPER_MAP_MEDIUM_1 = 2236;
            public static final int CLUEHELPER_MAP_MEDIUM_2 = 2237;
            public static final int CLUEHELPER_MAP_MEDIUM_3 = 2238;
            public static final int CLUEHELPER_MAP_MEDIUM_4 = 2239;
            public static final int CLUEHELPER_MAP_MEDIUM_5 = 2240;
            public static final int CLUEHELPER_MAP_MEDIUM_6 = 2241;
            public static final int CLUEHELPER_MAP_MEDIUM_7 = 2242;
            public static final int CLUEHELPER_MAP_MEDIUM_8 = 2243;
            public static final int CLUEHELPER_MAP_MEDIUM_9 = 2244;
            public static final int CLUEHELPER_MAP_MEDIUM_10 = 2245;
            public static final int CLUEHELPER_MAP_MEDIUM_11 = 2246;
            public static final int CLUEHELPER_MAP_HARD_0 = 2247;
            public static final int CLUEHELPER_MAP_HARD_1 = 2248;
            public static final int CLUEHELPER_MAP_HARD_2 = 2249;
            public static final int CLUEHELPER_MAP_HARD_3 = 2250;
            public static final int CLUEHELPER_MAP_HARD_4 = 2251;
            public static final int CLUEHELPER_MAP_HARD_5 = 2252;
            public static final int CLUEHELPER_MAP_HARD_6 = 2253;
            public static final int CLUEHELPER_MAP_ELITE_0 = 2254;
            public static final int CLUEHELPER_MAP_ELITE_1 = 2255;
            public static final int CLUEHELPER_MAP_ELITE_2 = 2256;
            public static final int CLUEHELPER_MAP_ELITE_3 = 2257;
            public static final int CLUEHELPER_MAP_ELITE_4 = 2258;
            public static final int CLUEHELPER_MAP_ELITE_5 = 2259;
            public static final int CLUEHELPER_MAP_TREASURE_SCROLL_0 = 2260;
            public static final int CLUEHELPER_MAP_MYSTERIOUS_ORB_0 = 2261;
            public static final int CLUEHELPER_MAP_CLUEQUEST_2 = 2303;
            public static final int CLUEHELPER_MAP_SOTE_2 = 2304;
        }
    }

    public static final class CluehelperClueCipher {
        private CluehelperClueCipher() {}

        public static final int ID = 6;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_CLUE_TEXT = 2;
        public static final int COL_TARGET = 3;
        public static final int COL_CHALLENGE = 4;
        public static final int COL_REQUIREMENTS = 5;
        public static final int COL_REGION = 6;
        public static final int COL_ALLREGIONS = 7;
        public static final int COL_RELATED_CONTENT = 8;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_CIPHER_XMTS_EASY_0 = 2262;
            public static final int CLUEHELPER_CIPHER_MEDIUM_0 = 2263;
            public static final int CLUEHELPER_CIPHER_MEDIUM_1 = 2264;
            public static final int CLUEHELPER_CIPHER_MEDIUM_2 = 2265;
            public static final int CLUEHELPER_CIPHER_MEDIUM_3 = 2266;
            public static final int CLUEHELPER_CIPHER_MEDIUM_4 = 2267;
            public static final int CLUEHELPER_CIPHER_MEDIUM_5 = 2268;
            public static final int CLUEHELPER_CIPHER_HARD_0 = 2269;
            public static final int CLUEHELPER_CIPHER_HARD_1 = 2270;
            public static final int CLUEHELPER_CIPHER_HARD_2 = 2271;
            public static final int CLUEHELPER_CIPHER_HARD_3 = 2272;
            public static final int CLUEHELPER_CIPHER_HARD_4 = 2273;
            public static final int CLUEHELPER_CIPHER_HARD_5 = 2274;
            public static final int CLUEHELPER_CIPHER_HARD_6 = 2275;
            public static final int CLUEHELPER_CIPHER_HARD_7 = 2276;
            public static final int CLUEHELPER_CIPHER_HARD_8 = 2277;
            public static final int CLUEHELPER_CIPHER_HARD_VM01 = 3510;
            public static final int CLUEHELPER_CIPHER_HARD_VM02 = 5183;
        }
    }

    public static final class CluehelperClueCoordinate {
        private CluehelperClueCoordinate() {}

        public static final int ID = 7;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_CLUE_TEXT = 2;
        public static final int COL_TARGET = 3;
        public static final int COL_REQUIREMENTS = 4;
        public static final int COL_COMBAT_ENCOUNTER = 5;
        public static final int COL_REGION = 6;
        public static final int COL_ALLREGIONS = 7;
        public static final int COL_RELATED_CONTENT = 8;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_COORDINATE_MEDIUM_0 = 1923;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_1 = 1924;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_2 = 1925;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_3 = 1926;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_4 = 1927;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_5 = 1928;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_6 = 1929;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_7 = 1930;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_8 = 1931;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_9 = 1932;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_10 = 1933;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_11 = 1934;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_12 = 1935;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_13 = 1936;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_14 = 1937;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_15 = 1938;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_16 = 1939;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_17 = 1940;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_18 = 1941;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_19 = 1942;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_20 = 1943;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_21 = 1944;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_22 = 1945;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_23 = 1946;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_24 = 1947;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_25 = 1948;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_26 = 1949;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_27 = 1950;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_28 = 1951;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_29 = 1952;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_30 = 1953;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_31 = 1954;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_32 = 1955;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_33 = 1956;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_34 = 1957;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_35 = 1958;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_36 = 1959;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_37 = 1960;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_38 = 1961;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_39 = 1962;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_40 = 1963;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_41 = 1964;
            public static final int CLUEHELPER_COORDINATE_HARD_0 = 1965;
            public static final int CLUEHELPER_COORDINATE_HARD_1 = 1966;
            public static final int CLUEHELPER_COORDINATE_HARD_2 = 1967;
            public static final int CLUEHELPER_COORDINATE_HARD_3 = 1968;
            public static final int CLUEHELPER_COORDINATE_HARD_4 = 1969;
            public static final int CLUEHELPER_COORDINATE_HARD_5 = 1970;
            public static final int CLUEHELPER_COORDINATE_HARD_6 = 1971;
            public static final int CLUEHELPER_COORDINATE_HARD_7 = 1972;
            public static final int CLUEHELPER_COORDINATE_HARD_8 = 1973;
            public static final int CLUEHELPER_COORDINATE_HARD_9 = 1974;
            public static final int CLUEHELPER_COORDINATE_HARD_10 = 1975;
            public static final int CLUEHELPER_COORDINATE_HARD_11 = 1976;
            public static final int CLUEHELPER_COORDINATE_HARD_12 = 1977;
            public static final int CLUEHELPER_COORDINATE_HARD_13 = 1978;
            public static final int CLUEHELPER_COORDINATE_HARD_14 = 1979;
            public static final int CLUEHELPER_COORDINATE_HARD_15 = 1980;
            public static final int CLUEHELPER_COORDINATE_HARD_16 = 1981;
            public static final int CLUEHELPER_COORDINATE_HARD_17 = 1982;
            public static final int CLUEHELPER_COORDINATE_HARD_18 = 1983;
            public static final int CLUEHELPER_COORDINATE_HARD_19 = 1984;
            public static final int CLUEHELPER_COORDINATE_HARD_20 = 1985;
            public static final int CLUEHELPER_COORDINATE_HARD_21 = 1986;
            public static final int CLUEHELPER_COORDINATE_HARD_22 = 1987;
            public static final int CLUEHELPER_COORDINATE_HARD_23 = 1988;
            public static final int CLUEHELPER_COORDINATE_HARD_24 = 1989;
            public static final int CLUEHELPER_COORDINATE_HARD_25 = 1990;
            public static final int CLUEHELPER_COORDINATE_HARD_26 = 1991;
            public static final int CLUEHELPER_COORDINATE_HARD_27 = 1992;
            public static final int CLUEHELPER_COORDINATE_HARD_28 = 1993;
            public static final int CLUEHELPER_COORDINATE_HARD_29 = 1994;
            public static final int CLUEHELPER_COORDINATE_HARD_30 = 1995;
            public static final int CLUEHELPER_COORDINATE_HARD_31 = 1996;
            public static final int CLUEHELPER_COORDINATE_HARD_32 = 1997;
            public static final int CLUEHELPER_COORDINATE_HARD_33 = 1998;
            public static final int CLUEHELPER_COORDINATE_HARD_34 = 1999;
            public static final int CLUEHELPER_COORDINATE_HARD_35 = 2000;
            public static final int CLUEHELPER_COORDINATE_HARD_36 = 2001;
            public static final int CLUEHELPER_COORDINATE_HARD_37 = 2002;
            public static final int CLUEHELPER_COORDINATE_HARD_38 = 2003;
            public static final int CLUEHELPER_COORDINATE_HARD_39 = 2004;
            public static final int CLUEHELPER_COORDINATE_HARD_40 = 2005;
            public static final int CLUEHELPER_COORDINATE_HARD_41 = 2006;
            public static final int CLUEHELPER_COORDINATE_HARD_42 = 2007;
            public static final int CLUEHELPER_COORDINATE_HARD_43 = 2008;
            public static final int CLUEHELPER_COORDINATE_HARD_44 = 2009;
            public static final int CLUEHELPER_COORDINATE_HARD_45 = 2010;
            public static final int CLUEHELPER_COORDINATE_HARD_46 = 2011;
            public static final int CLUEHELPER_COORDINATE_HARD_47 = 2012;
            public static final int CLUEHELPER_COORDINATE_HARD_48 = 2013;
            public static final int CLUEHELPER_COORDINATE_HARD_49 = 2014;
            public static final int CLUEHELPER_COORDINATE_HARD_50 = 2015;
            public static final int CLUEHELPER_COORDINATE_HARD_51 = 2016;
            public static final int CLUEHELPER_COORDINATE_HARD_52 = 2017;
            public static final int CLUEHELPER_COORDINATE_ELITE_0 = 2018;
            public static final int CLUEHELPER_COORDINATE_ELITE_1 = 2019;
            public static final int CLUEHELPER_COORDINATE_ELITE_2 = 2020;
            public static final int CLUEHELPER_COORDINATE_ELITE_3 = 2021;
            public static final int CLUEHELPER_COORDINATE_ELITE_4 = 2022;
            public static final int CLUEHELPER_COORDINATE_ELITE_5 = 2023;
            public static final int CLUEHELPER_COORDINATE_ELITE_6 = 2024;
            public static final int CLUEHELPER_COORDINATE_ELITE_7 = 2025;
            public static final int CLUEHELPER_COORDINATE_ELITE_8 = 2026;
            public static final int CLUEHELPER_COORDINATE_ELITE_9 = 2027;
            public static final int CLUEHELPER_COORDINATE_ELITE_10 = 2028;
            public static final int CLUEHELPER_COORDINATE_ELITE_11 = 2029;
            public static final int CLUEHELPER_COORDINATE_ELITE_12 = 2030;
            public static final int CLUEHELPER_COORDINATE_ELITE_13 = 2031;
            public static final int CLUEHELPER_COORDINATE_ELITE_14 = 2032;
            public static final int CLUEHELPER_COORDINATE_ELITE_15 = 2033;
            public static final int CLUEHELPER_COORDINATE_ELITE_16 = 2034;
            public static final int CLUEHELPER_COORDINATE_ELITE_17 = 2035;
            public static final int CLUEHELPER_COORDINATE_ELITE_18 = 2036;
            public static final int CLUEHELPER_COORDINATE_ELITE_19 = 2037;
            public static final int CLUEHELPER_COORDINATE_ELITE_20 = 2038;
            public static final int CLUEHELPER_COORDINATE_ELITE_21 = 2039;
            public static final int CLUEHELPER_COORDINATE_ELITE_22 = 2040;
            public static final int CLUEHELPER_COORDINATE_ELITE_23 = 2041;
            public static final int CLUEHELPER_COORDINATE_ELITE_24 = 2042;
            public static final int CLUEHELPER_COORDINATE_ELITE_25 = 2043;
            public static final int CLUEHELPER_COORDINATE_ELITE_26 = 2044;
            public static final int CLUEHELPER_COORDINATE_ELITE_27 = 2045;
            public static final int CLUEHELPER_COORDINATE_ELITE_28 = 2046;
            public static final int CLUEHELPER_COORDINATE_ELITE_29 = 2047;
            public static final int CLUEHELPER_COORDINATE_ELITE_30 = 2048;
            public static final int CLUEHELPER_COORDINATE_ELITE_31 = 2049;
            public static final int CLUEHELPER_COORDINATE_ELITE_32 = 2050;
            public static final int CLUEHELPER_COORDINATE_ELITE_33 = 2051;
            public static final int CLUEHELPER_COORDINATE_ELITE_34 = 2052;
            public static final int CLUEHELPER_COORDINATE_ELITE_35 = 2053;
            public static final int CLUEHELPER_COORDINATE_ELITE_36 = 2054;
            public static final int CLUEHELPER_COORDINATE_ELITE_37 = 2055;
            public static final int CLUEHELPER_COORDINATE_MASTER_0 = 2056;
            public static final int CLUEHELPER_COORDINATE_MASTER_1 = 2057;
            public static final int CLUEHELPER_COORDINATE_MASTER_2 = 2058;
            public static final int CLUEHELPER_COORDINATE_MASTER_3 = 2059;
            public static final int CLUEHELPER_COORDINATE_MASTER_4 = 2060;
            public static final int CLUEHELPER_COORDINATE_MASTER_5 = 2061;
            public static final int CLUEHELPER_COORDINATE_MASTER_6 = 2062;
            public static final int CLUEHELPER_COORDINATE_MASTER_7 = 2063;
            public static final int CLUEHELPER_COORDINATE_MASTER_8 = 2064;
            public static final int CLUEHELPER_COORDINATE_MASTER_9 = 2065;
            public static final int CLUEHELPER_COORDINATE_MASTER_10 = 2066;
            public static final int CLUEHELPER_COORDINATE_MASTER_11 = 2067;
            public static final int CLUEHELPER_COORDINATE_MASTER_12 = 2068;
            public static final int CLUEHELPER_COORDINATE_MASTER_13 = 2069;
            public static final int CLUEHELPER_COORDINATE_MASTER_14 = 2070;
            public static final int CLUEHELPER_COORDINATE_MASTER_15 = 2071;
            public static final int CLUEHELPER_COORDINATE_MASTER_16 = 2072;
            public static final int CLUEHELPER_COORDINATE_MASTER_17 = 2073;
            public static final int CLUEHELPER_COORDINATE_MASTER_18 = 2074;
            public static final int CLUEHELPER_COORDINATE_MASTER_19 = 2075;
            public static final int CLUEHELPER_COORDINATE_MASTER_20 = 2076;
            public static final int CLUEHELPER_COORDINATE_MASTER_21 = 2077;
            public static final int CLUEHELPER_COORDINATE_MASTER_22 = 2078;
            public static final int CLUEHELPER_COORDINATE_MASTER_23 = 2079;
            public static final int CLUEHELPER_COORDINATE_MASTER_24 = 2080;
            public static final int CLUEHELPER_COORDINATE_MASTER_25 = 2081;
            public static final int CLUEHELPER_COORDINATE_MASTER_26 = 2082;
            public static final int CLUEHELPER_COORDINATE_MASTER_27 = 2083;
            public static final int CLUEHELPER_COORDINATE_SOTE_1 = 2301;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_VM01 = 3505;
            public static final int CLUEHELPER_COORDINATE_ELITE_VM01 = 3506;
            public static final int CLUEHELPER_COORDINATE_ELITE_VM02 = 3697;
            public static final int CLUEHELPER_COORDINATE_HARD_53 = 5171;
            public static final int CLUEHELPER_COORDINATE_MEDIUM_SAIL = 7042;
            public static final int CLUEHELPER_COORDINATE_HARD_SAIL = 7043;
            public static final int CLUEHELPER_COORDINATE_ELITE_SAIL = 7044;
        }
    }

    public static final class CluehelperClueCryptic {
        private CluehelperClueCryptic() {}

        public static final int ID = 8;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_CLUE_TEXT = 2;
        public static final int COL_TARGET = 3;
        public static final int COL_REQUIREMENTS = 4;
        public static final int COL_CHALLENGE = 5;
        public static final int COL_OUTFIT_TEXT_FALLBACK = 6;
        public static final int COL_OUTFIT = 7;
        public static final int COL_HIDEY_HOLE_LOC = 8;
        public static final int COL_HIDEY_HOLE_COORD = 9;
        public static final int COL_REGION = 10;
        public static final int COL_ALLREGIONS = 11;
        public static final int COL_RELATED_CONTENT = 12;
        public static final int COL_LEAGUE_CLUE_TEXT = 13;
        public static final int COL_LEAGUE_TARGET = 14;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_CRYPTIC_BEGINNER_HANS = 1233;
            public static final int CLUEHELPER_CRYPTIC_BEGINNER_COOK = 1234;
            public static final int CLUEHELPER_CRYPTIC_BEGINNER_HUNDING = 1235;
            public static final int CLUEHELPER_CRYPTIC_BEGINNER_CHARLIE_THE_TRAMP = 1236;
            public static final int CLUEHELPER_CRYPTIC_BEGINNER_SHANTAY = 1237;
            public static final int CLUEHELPER_CRYPTIC_BEGINNER_RELDO = 1238;
            public static final int CLUEHELPER_CRYPTIC_EASY_BARTENDER_RUSTY_ANCHOR = 1239;
            public static final int CLUEHELPER_CRYPTIC_EASY_SARAH = 1240;
            public static final int CLUEHELPER_CRYPTIC_EASY_SIR_KAY = 1241;
            public static final int CLUEHELPER_CRYPTIC_EASY_BARTENDER_BLUE_MOON = 1242;
            public static final int CLUEHELPER_CRYPTIC_EASY_FATHER_JEAN = 1243;
            public static final int CLUEHELPER_CRYPTIC_EASY_CAPTAIN_TOBIAS = 1244;
            public static final int CLUEHELPER_CRYPTIC_EASY_DORIS = 1245;
            public static final int CLUEHELPER_CRYPTIC_EASY_NED = 1246;
            public static final int CLUEHELPER_CRYPTIC_EASY_HANS = 1247;
            public static final int CLUEHELPER_CRYPTIC_EASY_RUSTY = 1248;
            public static final int CLUEHELPER_CRYPTIC_EASY_AMBASSADOR_SPANFIPPLE = 1249;
            public static final int CLUEHELPER_CRYPTIC_EASY_LUCY = 1250;
            public static final int CLUEHELPER_CRYPTIC_EASY_LOUISA = 1251;
            public static final int CLUEHELPER_CRYPTIC_EASY_VANNAKA = 1252;
            public static final int CLUEHELPER_CRYPTIC_EASY_THE_LADY_OF_THE_LAKE = 1253;
            public static final int CLUEHELPER_CRYPTIC_EASY_JATIX = 1254;
            public static final int CLUEHELPER_CRYPTIC_EASY_GAIUS = 1255;
            public static final int CLUEHELPER_CRYPTIC_EASY_HAIRDRESSER = 1256;
            public static final int CLUEHELPER_CRYPTIC_EASY_JEED = 1257;
            public static final int CLUEHELPER_CRYPTIC_EASY_ARHEIN = 1258;
            public static final int CLUEHELPER_CRYPTIC_EASY_DORIC = 1259;
            public static final int CLUEHELPER_CRYPTIC_EASY_ERMIN = 1260;
            public static final int CLUEHELPER_CRYPTIC_EASY_APOTHECARY = 1261;
            public static final int CLUEHELPER_CRYPTIC_EASY_CASSIE = 1262;
            public static final int CLUEHELPER_CRYPTIC_EASY_DOOMSAYER = 1263;
            public static final int CLUEHELPER_CRYPTIC_EASY_HERQUIN = 1264;
            public static final int CLUEHELPER_CRYPTIC_EASY_THE_FACE = 1265;
            public static final int CLUEHELPER_CRYPTIC_EASY_SQUIRE = 1266;
            public static final int CLUEHELPER_CRYPTIC_EASY_TYNAN = 1267;
            public static final int CLUEHELPER_CRYPTIC_EASY_TOOL_LEPRECHAUN = 1268;
            public static final int CLUEHELPER_CRYPTIC_EASY_KONOO = 1269;
            public static final int CLUEHELPER_CRYPTIC_EASY_ZEKE = 1270;
            public static final int CLUEHELPER_CRYPTIC_EASY_ELLIS = 1271;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHEMIST = 1272;
            public static final int CLUEHELPER_CRYPTIC_EASY_MORGAN = 1273;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHARLES = 1274;
            public static final int CLUEHELPER_CRYPTIC_EASY_ALI_THE_LEAFLET_DROPPER = 1275;
            public static final int CLUEHELPER_CRYPTIC_EASY_COOK = 1276;
            public static final int CLUEHELPER_CRYPTIC_EASY_WAYNE = 1277;
            public static final int CLUEHELPER_CRYPTIC_EASY_TURAEL = 1278;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE2_OLD_HEMENSTER = 1279;
            public static final int CLUEHELPER_CRYPTIC_EASY_PRISON_BUCKET = 1280;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_LARGE_CRATES_0_49_153_51_33 = 1281;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_WIZTOWER_BOOKCASE = 1282;
            public static final int CLUEHELPER_CRYPTIC_EASY_KR_CRATE = 1283;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE = 1284;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE3 = 1285;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE2_TOAD_AND_CHICKEN = 1286;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHESTCLOSED_1_47_50_8_5 = 1287;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_FALADOR_SMALL_CRATES_0_47_52_21_27 = 1288;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAYNOR_CRATE = 1289;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_LARGE_CRATES_0_50_53_26_60 = 1290;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_BOXES = 1291;
            public static final int CLUEHELPER_CRYPTIC_EASY_CANAFIS_CRATE_0_54_54_53_41 = 1292;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE2_OLD_DWARVEN_MINE = 1293;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE2_OLD_ARDOUGNE_CHURCH = 1294;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAYNOR_WARDROBE = 1295;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_RED_CRATE = 1296;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_BARBARIAN_SMALL_CRATES = 1297;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_FALADOR_SMALL_CRATES_1_0_46_52_11_62 = 1298;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS3 = 1299;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE2_OLD_LUMBRIDGE_SOUTH_TOWER = 1300;
            public static final int CLUEHELPER_CRYPTIC_EASY_COFFIN = 1301;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE2_OLD_YANILLE_PIANO = 1302;
            public static final int CLUEHELPER_CRYPTIC_EASY_SARIM_CRATE2 = 1303;
            public static final int CLUEHELPER_CRYPTIC_EASY_BOXES = 1304;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS1_0_46_51_25_47 = 1305;
            public static final int CLUEHELPER_CRYPTIC_EASY_DIGSITEBUSH = 1306;
            public static final int CLUEHELPER_CRYPTIC_EASY_BOOKCASE = 1307;
            public static final int CLUEHELPER_CRYPTIC_EASY_KR_SEERS_VILLAGE_DRAWERS1 = 1308;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHESTCLOSED_0_48_53_13_37 = 1309;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE2_OLD_TAVERLEY_OUTHOUSE = 1310;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHESTCLOSED_1_47_52_33_36 = 1311;
            public static final int CLUEHELPER_CRYPTIC_EASY_SARIM_BOOKCASE2_0_49_49_10_41 = 1312;
            public static final int CLUEHELPER_CRYPTIC_EASY_BOXES3_0_51_50_44_6 = 1313;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS2_0_41_51_29_56 = 1314;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS1_1_46_52_27_58 = 1315;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_DRAWERS_1_50_53_6_27 = 1316;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_DRAWERS_0_49_53_20_14 = 1317;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS1_0_47_50_16_59 = 1318;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS2_0_45_53_14_26 = 1319;
            public static final int CLUEHELPER_CRYPTIC_EASY_DEATH_BOXES_2 = 1320;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS2_0_40_48_10_13 = 1321;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS1_0_45_55_49_50 = 1322;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE_OLD_0_41_52_21_10 = 1323;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS2_0_47_52_31_14 = 1324;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS1_1_40_51_14_62 = 1325;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS2_0_48_51_25_13 = 1326;
            public static final int CLUEHELPER_CRYPTIC_EASY_BOXES2_1_50_50_9_18 = 1327;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHESTCLOSED = 1328;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHESTCLOSED_1_51_49_37_33 = 1329;
            public static final int CLUEHELPER_CRYPTIC_EASY_BOXES3_0_41_51_30_35 = 1330;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS2_0_27_54_18_34 = 1331;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS2_0_44_53_9_50 = 1332;
            public static final int CLUEHELPER_CRYPTIC_EASY_KR_CAMELOT_CHESTCLOSED = 1333;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE2_OLD_ARDOUGNE_GENERAL_STORE = 1334;
            public static final int CLUEHELPER_CRYPTIC_EASY_RIMMINGTON_WHEEL_BARROW = 1335;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_LARGE_CRATE_2_0_50_53_28_41 = 1336;
            public static final int CLUEHELPER_CRYPTIC_EASY_DRAWERS2_1_43_53_57_59 = 1337;
            public static final int CLUEHELPER_CRYPTIC_EASY_CRATE3_OLD = 1338;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHESTCLOSED_0_46_153_56_6 = 1339;
            public static final int CLUEHELPER_CRYPTIC_EASY_HOS_CRATE_02_0_26_56_19_32 = 1340;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_DRAWERS_1_50_53_50_28 = 1341;
            public static final int CLUEHELPER_CRYPTIC_EASY_CHESTOPEN = 1342;
            public static final int CLUEHELPER_CRYPTIC_EASY_SARIM_BOOKCASE2_0_49_49_13_41 = 1343;
            public static final int CLUEHELPER_CRYPTIC_EASY_SARIM_CRATE = 1344;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_VARROCK_LARGE_CRATE_1_0_50_53_28_41 = 1345;
            public static final int CLUEHELPER_CRYPTIC_EASY_FAI_FALADOR_SMALL_CRATES_2_0_46_52_11_62 = 1346;
            public static final int CLUEHELPER_CRYPTIC_EASY_BOXES2 = 1347;
            public static final int CLUEHELPER_CRYPTIC_EASY_HOS_CRATE_02_0_28_56_7_29 = 1348;
            public static final int CLUEHELPER_CRYPTIC_EASY_0_47_53_32_7 = 1349;
            public static final int CLUEHELPER_CRYPTIC_EASY_0_38_54_26_48 = 1350;
            public static final int CLUEHELPER_CRYPTIC_EASY_0_50_50_21_19 = 1351;
            public static final int CLUEHELPER_CRYPTIC_EASY_0_25_57_39_25 = 1352;
            public static final int CLUEHELPER_CRYPTIC_EASY_0_39_44_33_22 = 1353;
            public static final int CLUEHELPER_CRYPTIC_EASY_0_22_56_10_7 = 1354;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_GNOME_BALL_REFEREE = 1356;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_ULIZIUS = 1357;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_DONOVAN_THE_FAMILY_HANDYMAN = 1358;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_KANGAI_MAU = 1359;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_HAJEDY = 1360;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_ROAVAR = 1361;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_CRATE2 = 1362;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_CANAFIS_CRATE_0_54_54_42_51 = 1363;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_CRATE_OLD_0_43_48_48_2 = 1364;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_TOURTRAP_QIP_CRATE_SINGLE = 1365;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_SLUG2_CRATE_SINGLE = 1366;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_CRATE_OLD_0_41_53_47_45 = 1367;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_HOS_BASKET_APPLE = 1368;
            public static final int CLUEHELPER_CRYPTIC_HARD_BROTHER_KOJO = 1369;
            public static final int CLUEHELPER_CRYPTIC_HARD_MINER_MAGNUS = 1370;
            public static final int CLUEHELPER_CRYPTIC_HARD_ENT = 1371;
            public static final int CLUEHELPER_CRYPTIC_HARD_OZIACH = 1372;
            public static final int CLUEHELPER_CRYPTIC_HARD_HECKEL_FUNCH = 1373;
            public static final int CLUEHELPER_CRYPTIC_HARD_HANS = 1374;
            public static final int CLUEHELPER_CRYPTIC_HARD_SANIBOCH = 1375;
            public static final int CLUEHELPER_CRYPTIC_HARD_HAMID = 1376;
            public static final int CLUEHELPER_CRYPTIC_HARD_CAPTAIN_BLEEMADGE = 1377;
            public static final int CLUEHELPER_CRYPTIC_HARD_HEAD_CHEF = 1378;
            public static final int CLUEHELPER_CRYPTIC_HARD_ZUL_CHERAY = 1379;
            public static final int CLUEHELPER_CRYPTIC_HARD_ELLENA = 1380;
            public static final int CLUEHELPER_CRYPTIC_HARD_GNOME_TRAINER = 1381;
            public static final int CLUEHELPER_CRYPTIC_HARD_GUARDIAN_MUMMY = 1382;
            public static final int CLUEHELPER_CRYPTIC_HARD_SIR_VYVIN = 1383;
            public static final int CLUEHELPER_CRYPTIC_HARD_WILOUGH = 1384;
            public static final int CLUEHELPER_CRYPTIC_HARD_GERRANT = 1385;
            public static final int CLUEHELPER_CRYPTIC_HARD_EXAMINER = 1386;
            public static final int CLUEHELPER_CRYPTIC_HARD_GENERAL_BENTNOZE = 1387;
            public static final int CLUEHELPER_CRYPTIC_HARD_SIR_PRYSIN = 1388;
            public static final int CLUEHELPER_CRYPTIC_HARD_MAWNIS_BUROWGAR = 1389;
            public static final int CLUEHELPER_CRYPTIC_HARD_WIZARD_MIZGOG = 1390;
            public static final int CLUEHELPER_CRYPTIC_HARD_ABBOT_LANGLEY = 1391;
            public static final int CLUEHELPER_CRYPTIC_HARD_GENERAL_HINING = 1392;
            public static final int CLUEHELPER_CRYPTIC_HARD_DRAWERS4 = 1393;
            public static final int CLUEHELPER_CRYPTIC_HARD_CRATE2_UZER = 1394;
            public static final int CLUEHELPER_CRYPTIC_HARD_QIP_COOK_CRATE_STACKED = 1395;
            public static final int CLUEHELPER_CRYPTIC_HARD_BOOKCASE_0_48_149_24_36 = 1396;
            public static final int CLUEHELPER_CRYPTIC_HARD_HAYSTACK3 = 1397;
            public static final int CLUEHELPER_CRYPTIC_HARD_BOXES = 1398;
            public static final int CLUEHELPER_CRYPTIC_HARD_DRAWERS2_0_44_52_2_23 = 1399;
            public static final int CLUEHELPER_CRYPTIC_HARD_DWARF_KELDAGRIM_WOODEN_BOXES = 1400;
            public static final int CLUEHELPER_CRYPTIC_HARD_BOOKCASE_1_42_53_15_17 = 1401;
            public static final int CLUEHELPER_CRYPTIC_HARD_DWARFROCK_BOOK_CART = 1402;
            public static final int CLUEHELPER_CRYPTIC_HARD_CRATE2_OLD_YANILLE_DUNGEON = 1403;
            public static final int CLUEHELPER_CRYPTIC_HARD_CRATE2_BANDIT_CAMP = 1404;
            public static final int CLUEHELPER_CRYPTIC_HARD_SHELVES_BAMBOO = 1405;
            public static final int CLUEHELPER_CRYPTIC_HARD_CRATE2_OLD_UNDERCOOK = 1406;
            public static final int CLUEHELPER_CRYPTIC_HARD_ELEM_CRATE_1 = 1407;
            public static final int CLUEHELPER_CRYPTIC_HARD_DRAWERS2_0_40_51_1_59 = 1408;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_40_51_38_3 = 1409;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_48_54_17_12 = 1410;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_48_53_9_29 = 1411;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_54_51_32_25 = 1412;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_49_57_38_15 = 1413;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_44_149_16_50 = 1414;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_40_60_31_39 = 1415;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_49_52_59_29 = 1416;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_49_60_34_45 = 1417;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_49_153_55_33 = 1418;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_55_49_27_47 = 1419;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_52_49_60_16 = 1420;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_48_50_13_55 = 1421;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_49_154_25_48 = 1422;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_18_56_27_42 = 1423;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_28_155_28_15 = 1424;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_55_68_52_20 = 1425;
            public static final int CLUEHELPER_CRYPTIC_ELITE_SHERLOCK = 1426;
            public static final int CLUEHELPER_CRYPTIC_ELITE_FATHER_AERECK = 1427;
            public static final int CLUEHELPER_CRYPTIC_ELITE_CANDLE_MAKER = 1428;
            public static final int CLUEHELPER_CRYPTIC_ELITE_BARKER = 1429;
            public static final int CLUEHELPER_CRYPTIC_ELITE_VANNAKA = 1430;
            public static final int CLUEHELPER_CRYPTIC_ELITE_KAMFREENA = 1431;
            public static final int CLUEHELPER_CRYPTIC_ELITE_WYSON_THE_GARDENER = 1432;
            public static final int CLUEHELPER_CRYPTIC_ELITE_DOMINIC_ONION = 1433;
            public static final int CLUEHELPER_CRYPTIC_ELITE_HORACIO = 1434;
            public static final int CLUEHELPER_CRYPTIC_ELITE_GYPSY_ARIS = 1435;
            public static final int CLUEHELPER_CRYPTIC_ELITE_DAGA = 1436;
            public static final int CLUEHELPER_CRYPTIC_ELITE_SQUIRE_VETERAN = 1437;
            public static final int CLUEHELPER_CRYPTIC_ELITE_MAWRTH = 1438;
            public static final int CLUEHELPER_CRYPTIC_ELITE_GENIE = 1439;
            public static final int CLUEHELPER_CRYPTIC_ELITE_BOULDER4_SEARCH = 1440;
            public static final int CLUEHELPER_CRYPTIC_ELITE_ROOFTOPS_SEERS_CRATE = 1441;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_53_51_18_60 = 1442;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_43_59_28_7 = 1443;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_47_75_41_39 = 1444;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_44_55_51_26 = 1445;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_29_68_54_15 = 1446;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_49_71_3_10 = 1447;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_39_58_27_27 = 1448;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_51_148_43_33 = 1449;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_36_57_38_29 = 1450;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_59_59_40_34 = 1451;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_49_61_47_37 = 1452;
            public static final int CLUEHELPER_CRYPTIC_ELITE_0_34_48_45_19 = 1453;
            public static final int CLUEHELPER_CRYPTIC_ELITE_SPECTRE = 1454;
            public static final int CLUEHELPER_CRYPTIC_ELITE_ANKOU = 1455;
            public static final int CLUEHELPER_CRYPTIC_ELITE_WATERFIEND = 1456;
            public static final int CLUEHELPER_CRYPTIC_ELITE_BASILISK = 1457;
            public static final int CLUEHELPER_CRYPTIC_ELITE_BARBARIAN = 1458;
            public static final int CLUEHELPER_CRYPTIC_ELITE_ROCK_CRAB = 1459;
            public static final int CLUEHELPER_CRYPTIC_ELITE_BLOODVELD = 1460;
            public static final int CLUEHELPER_CRYPTIC_ELITE_AVIANSIE = 1461;
            public static final int CLUEHELPER_CRYPTIC_ELITE_GREEN_DRAGON = 1462;
            public static final int CLUEHELPER_CRYPTIC_ELITE_CROCODILE = 1463;
            public static final int CLUEHELPER_CRYPTIC_ELITE_HELLHOUND = 1464;
            public static final int CLUEHELPER_CRYPTIC_MASTER_FALO_THE_BARD = 1465;
            public static final int CLUEHELPER_CRYPTIC_MASTER_KEY_MASTER = 1466;
            public static final int CLUEHELPER_CRYPTIC_MASTER_PILES = 1467;
            public static final int CLUEHELPER_CRYPTIC_MASTER_ROBIN = 1468;
            public static final int CLUEHELPER_CRYPTIC_MASTER_ABBOT_LANGLEY = 1469;
            public static final int CLUEHELPER_CRYPTIC_MASTER_EWESEY = 1470;
            public static final int CLUEHELPER_CRYPTIC_MASTER_THORGEL = 1471;
            public static final int CLUEHELPER_CRYPTIC_MASTER_MAGE_OF_ZAMORAK = 1472;
            public static final int CLUEHELPER_CRYPTIC_MASTER_LOVADA = 1473;
            public static final int CLUEHELPER_CRYPTIC_MASTER_MONK_OF_ENTRANA = 1474;
            public static final int CLUEHELPER_CRYPTIC_MASTER_GHOMMAL = 1475;
            public static final int CLUEHELPER_CRYPTIC_MASTER_LOGOSIA = 1476;
            public static final int CLUEHELPER_CRYPTIC_MASTER_WIZARD_CROMPERTY = 1477;
            public static final int CLUEHELPER_CRYPTIC_MASTER_SIR_VYVIN = 1478;
            public static final int CLUEHELPER_CRYPTIC_MASTER_BIBLIA = 1479;
            public static final int CLUEHELPER_CRYPTIC_MASTER_1_47_77_35_46 = 1480;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_51_60_33_50 = 1481;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_27_77_28_12 = 1482;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_47_160_37_25 = 1483;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_47_161_47_34 = 1484;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_37_73_42_42 = 1485;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_44_58_58_45 = 1486;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_42_79_56_60 = 1487;
            public static final int CLUEHELPER_CRYPTIC_MASTER_1_45_82_34_52 = 1488;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_44_46_41_22 = 1489;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_41_162_47_28 = 1490;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_47_61_61_31 = 1491;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_45_58_47_49 = 1492;
            public static final int CLUEHELPER_CRYPTIC_MASTER_0_51_95_39_12 = 1493;
            public static final int CLUEHELPER_CRYPTIC_MASTER_JUNA = 1494;
            public static final int CLUEHELPER_CRYPTIC_MASTER_VIGGORA = 1495;
            public static final int CLUEHELPER_CRYPTIC_MASTER_JORRAL = 1496;
            public static final int CLUEHELPER_CRYPTIC_MASTER_WATSON = 1497;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_GUARD_DRAWERS = 2278;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_BARBARIAN_CHESTCLOSED = 2279;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_MAN_CHESTCLOSED = 2280;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_WIZARD_DRAWERS = 2281;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_GUARD_DOG_DRAWERS = 2282;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_MARKET_GUARD_DRAWERS = 2283;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_PENDA_DRAWERS = 2284;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_PIRATE_DRAWERS = 2285;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_CHICKEN_DRAWERS = 2286;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_HILL_GIANT_CRATE = 2287;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_MONK_CHESTCLOSED = 2288;
            public static final int CLUEHELPER_CRYPTIC_ELITE_KING_BLACK_DRAGON_CHESTCLOSED = 2289;
            public static final int CLUEHELPER_CRYPTIC_CLUEQUEST_1 = 2298;
            public static final int CLUEHELPER_CRYPTIC_EASY_HUNTERGUILD_SHELF = 3495;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_GLORY = 3496;
            public static final int CLUEHELPER_CRYPTIC_ELITE_FUNBO = 3497;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_UGLUG_NAR = 3597;
            public static final int CLUEHELPER_CRYPTIC_EASY_0_23_50_18_57 = 3695;
            public static final int CLUEHELPER_CRYPTIC_ELITE_FROST_NAGUA = 3696;
            public static final int CLUEHELPER_CRYPTIC_EASY_0_21_45_46_46 = 3812;
            public static final int CLUEHELPER_CRYPTIC_EASY_RANULPH = 5178;
            public static final int CLUEHELPER_CRYPTIC_MEDIUM_AUBURN = 5179;
            public static final int CLUEHELPER_CRYPTIC_ELITE_EARTH_NAGUA = 5180;
            public static final int CLUEHELPER_CRYPTIC_HARD_0_36_43_20_20 = 7045;
            public static final int CLUEHELPER_CRYPTIC_ELITE_GRYPHON_BOSS = 7046;
        }
    }

    public static final class CluehelperClueEmote {
        private CluehelperClueEmote() {}

        public static final int ID = 9;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_CLUE_TEXT = 2;
        public static final int COL_TARGET = 3;
        public static final int COL_EMOTE = 4;
        public static final int COL_OUTFIT_TEXT_FALLBACK = 5;
        public static final int COL_OUTFIT = 6;
        public static final int COL_HIDEY_HOLE_LOC = 7;
        public static final int COL_HIDEY_HOLE_COORD = 8;
        public static final int COL_COMBAT_ENCOUNTER = 9;
        public static final int COL_REQUIREMENTS = 10;
        public static final int COL_REGION = 11;
        public static final int COL_ALLREGIONS = 12;
        public static final int COL_RELATED_CONTENT = 13;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_EMOTE_BEGINNER_0 = 2084;
            public static final int CLUEHELPER_EMOTE_BEGINNER_1 = 2085;
            public static final int CLUEHELPER_EMOTE_BEGINNER_2 = 2086;
            public static final int CLUEHELPER_EMOTE_BEGINNER_3 = 2087;
            public static final int CLUEHELPER_EMOTE_BEGINNER_4 = 2088;
            public static final int CLUEHELPER_EMOTE_BEGINNER_5 = 2089;
            public static final int CLUEHELPER_EMOTE_EASY_0 = 2090;
            public static final int CLUEHELPER_EMOTE_EASY_1 = 2091;
            public static final int CLUEHELPER_EMOTE_EASY_2 = 2092;
            public static final int CLUEHELPER_EMOTE_EASY_3 = 2093;
            public static final int CLUEHELPER_EMOTE_EASY_4 = 2094;
            public static final int CLUEHELPER_EMOTE_EASY_5 = 2095;
            public static final int CLUEHELPER_EMOTE_EASY_6 = 2096;
            public static final int CLUEHELPER_EMOTE_EASY_7 = 2097;
            public static final int CLUEHELPER_EMOTE_EASY_8 = 2098;
            public static final int CLUEHELPER_EMOTE_EASY_9 = 2099;
            public static final int CLUEHELPER_EMOTE_EASY_10 = 2100;
            public static final int CLUEHELPER_EMOTE_EASY_11 = 2101;
            public static final int CLUEHELPER_EMOTE_EASY_12 = 2102;
            public static final int CLUEHELPER_EMOTE_EASY_13 = 2103;
            public static final int CLUEHELPER_EMOTE_EASY_14 = 2104;
            public static final int CLUEHELPER_EMOTE_EASY_15 = 2105;
            public static final int CLUEHELPER_EMOTE_EASY_16 = 2106;
            public static final int CLUEHELPER_EMOTE_EASY_17 = 2107;
            public static final int CLUEHELPER_EMOTE_EASY_18 = 2108;
            public static final int CLUEHELPER_EMOTE_EASY_19 = 2109;
            public static final int CLUEHELPER_EMOTE_EASY_20 = 2110;
            public static final int CLUEHELPER_EMOTE_EASY_21 = 2111;
            public static final int CLUEHELPER_EMOTE_EASY_22 = 2112;
            public static final int CLUEHELPER_EMOTE_EASY_23 = 2113;
            public static final int CLUEHELPER_EMOTE_EASY_24 = 2114;
            public static final int CLUEHELPER_EMOTE_EASY_25 = 2115;
            public static final int CLUEHELPER_EMOTE_EASY_26 = 2116;
            public static final int CLUEHELPER_EMOTE_EASY_27 = 2117;
            public static final int CLUEHELPER_EMOTE_EASY_28 = 2118;
            public static final int CLUEHELPER_EMOTE_EASY_29 = 2119;
            public static final int CLUEHELPER_EMOTE_EASY_30 = 2120;
            public static final int CLUEHELPER_EMOTE_MEDIUM_0 = 2121;
            public static final int CLUEHELPER_EMOTE_MEDIUM_1 = 2122;
            public static final int CLUEHELPER_EMOTE_MEDIUM_2 = 2123;
            public static final int CLUEHELPER_EMOTE_MEDIUM_3 = 2124;
            public static final int CLUEHELPER_EMOTE_MEDIUM_4 = 2125;
            public static final int CLUEHELPER_EMOTE_MEDIUM_5 = 2126;
            public static final int CLUEHELPER_EMOTE_MEDIUM_6 = 2127;
            public static final int CLUEHELPER_EMOTE_MEDIUM_7 = 2128;
            public static final int CLUEHELPER_EMOTE_MEDIUM_8 = 2129;
            public static final int CLUEHELPER_EMOTE_MEDIUM_9 = 2130;
            public static final int CLUEHELPER_EMOTE_MEDIUM_10 = 2131;
            public static final int CLUEHELPER_EMOTE_MEDIUM_11 = 2132;
            public static final int CLUEHELPER_EMOTE_MEDIUM_12 = 2133;
            public static final int CLUEHELPER_EMOTE_MEDIUM_13 = 2134;
            public static final int CLUEHELPER_EMOTE_MEDIUM_14 = 2135;
            public static final int CLUEHELPER_EMOTE_MEDIUM_15 = 2136;
            public static final int CLUEHELPER_EMOTE_MEDIUM_16 = 2137;
            public static final int CLUEHELPER_EMOTE_MEDIUM_17 = 2138;
            public static final int CLUEHELPER_EMOTE_MEDIUM_18 = 2139;
            public static final int CLUEHELPER_EMOTE_MEDIUM_19 = 2140;
            public static final int CLUEHELPER_EMOTE_MEDIUM_20 = 2141;
            public static final int CLUEHELPER_EMOTE_MEDIUM_21 = 2142;
            public static final int CLUEHELPER_EMOTE_MEDIUM_22 = 2143;
            public static final int CLUEHELPER_EMOTE_HARD_0 = 2144;
            public static final int CLUEHELPER_EMOTE_HARD_1 = 2145;
            public static final int CLUEHELPER_EMOTE_HARD_2 = 2146;
            public static final int CLUEHELPER_EMOTE_HARD_3 = 2147;
            public static final int CLUEHELPER_EMOTE_HARD_4 = 2148;
            public static final int CLUEHELPER_EMOTE_HARD_5 = 2149;
            public static final int CLUEHELPER_EMOTE_HARD_6 = 2150;
            public static final int CLUEHELPER_EMOTE_HARD_7 = 2151;
            public static final int CLUEHELPER_EMOTE_HARD_8 = 2152;
            public static final int CLUEHELPER_EMOTE_HARD_9 = 2153;
            public static final int CLUEHELPER_EMOTE_HARD_10 = 2154;
            public static final int CLUEHELPER_EMOTE_HARD_11 = 2155;
            public static final int CLUEHELPER_EMOTE_HARD_12 = 2156;
            public static final int CLUEHELPER_EMOTE_HARD_13 = 2157;
            public static final int CLUEHELPER_EMOTE_HARD_14 = 2158;
            public static final int CLUEHELPER_EMOTE_HARD_15 = 2159;
            public static final int CLUEHELPER_EMOTE_ELITE_0 = 2160;
            public static final int CLUEHELPER_EMOTE_ELITE_1 = 2161;
            public static final int CLUEHELPER_EMOTE_ELITE_2 = 2162;
            public static final int CLUEHELPER_EMOTE_ELITE_3 = 2163;
            public static final int CLUEHELPER_EMOTE_ELITE_4 = 2164;
            public static final int CLUEHELPER_EMOTE_ELITE_5 = 2165;
            public static final int CLUEHELPER_EMOTE_ELITE_6 = 2166;
            public static final int CLUEHELPER_EMOTE_ELITE_7 = 2167;
            public static final int CLUEHELPER_EMOTE_ELITE_8 = 2168;
            public static final int CLUEHELPER_EMOTE_ELITE_9 = 2169;
            public static final int CLUEHELPER_EMOTE_ELITE_10 = 2170;
            public static final int CLUEHELPER_EMOTE_ELITE_11 = 2171;
            public static final int CLUEHELPER_EMOTE_ELITE_12 = 2172;
            public static final int CLUEHELPER_EMOTE_ELITE_13 = 2173;
            public static final int CLUEHELPER_EMOTE_ELITE_14 = 2174;
            public static final int CLUEHELPER_EMOTE_ELITE_15 = 2175;
            public static final int CLUEHELPER_EMOTE_ELITE_16 = 2176;
            public static final int CLUEHELPER_EMOTE_MASTER_0 = 2177;
            public static final int CLUEHELPER_EMOTE_MASTER_1 = 2178;
            public static final int CLUEHELPER_EMOTE_MASTER_2 = 2179;
            public static final int CLUEHELPER_EMOTE_MASTER_3 = 2180;
            public static final int CLUEHELPER_EMOTE_MASTER_4 = 2181;
            public static final int CLUEHELPER_EMOTE_MASTER_5 = 2182;
            public static final int CLUEHELPER_EMOTE_MASTER_6 = 2183;
            public static final int CLUEHELPER_EMOTE_MASTER_7 = 2184;
            public static final int CLUEHELPER_EMOTE_MASTER_8 = 2185;
            public static final int CLUEHELPER_EMOTE_MASTER_9 = 2186;
            public static final int CLUEHELPER_EMOTE_MASTER_10 = 2187;
            public static final int CLUEHELPER_EMOTE_MASTER_11 = 2188;
            public static final int CLUEHELPER_EMOTE_MASTER_12 = 2189;
            public static final int CLUEHELPER_EMOTE_MASTER_13 = 2190;
            public static final int CLUEHELPER_EMOTE_MASTER_14 = 2191;
            public static final int CLUEHELPER_EMOTE_MASTER_15 = 2192;
            public static final int CLUEHELPER_EMOTE_MASTER_16 = 2193;
            public static final int CLUEHELPER_EMOTE_MASTER_17 = 2194;
            public static final int CLUEHELPER_EMOTE_MASTER_18 = 2195;
            public static final int CLUEHELPER_EMOTE_MASTER_19 = 2196;
            public static final int CLUEHELPER_EMOTE_MASTER_20 = 2197;
            public static final int CLUEHELPER_EMOTE_MASTER_21 = 2198;
            public static final int CLUEHELPER_EMOTE_SOTE_4 = 2302;
            public static final int CLUEHELPER_EMOTE_EASY_VM01 = 3507;
            public static final int CLUEHELPER_EMOTE_ELITE_VM01 = 3508;
            public static final int CLUEHELPER_EMOTE_MASTER_VM01 = 3509;
            public static final int CLUEHELPER_EMOTE_MEDIUM_23 = 3690;
            public static final int CLUEHELPER_EMOTE_MEDIUM_24 = 3691;
            public static final int CLUEHELPER_EMOTE_HARD_VM2 = 3692;
            public static final int CLUEHELPER_EMOTE_MASTER_VM02 = 3693;
            public static final int CLUEHELPER_EMOTE_EASY_SAIL = 7047;
            public static final int CLUEHELPER_EMOTE_ELITE_SAIL = 7048;
            public static final int CLUEHELPER_EMOTE_MASTER_SAIL = 7049;
        }
    }

    public static final class CluehelperClueFairyring {
        private CluehelperClueFairyring() {}

        public static final int ID = 10;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_CLUE_TEXT = 2;
        public static final int COL_FAIRYRING = 3;
        public static final int COL_STEPS = 4;
        public static final int COL_TARGET = 5;
        public static final int COL_REQUIREMENTS = 6;
        public static final int COL_REGION = 7;
        public static final int COL_ALLREGIONS = 8;
        public static final int COL_RELATED_CONTENT = 9;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_FAIRYRING_HARD_0 = 1901;
            public static final int CLUEHELPER_FAIRYRING_HARD_1 = 1902;
            public static final int CLUEHELPER_FAIRYRING_HARD_2 = 1903;
            public static final int CLUEHELPER_FAIRYRING_HARD_3 = 1904;
            public static final int CLUEHELPER_FAIRYRING_HARD_4 = 1905;
            public static final int CLUEHELPER_FAIRYRING_HARD_5 = 1906;
            public static final int CLUEHELPER_FAIRYRING_HARD_6 = 1907;
            public static final int CLUEHELPER_FAIRYRING_HARD_7 = 1908;
            public static final int CLUEHELPER_FAIRYRING_HARD_8 = 1909;
            public static final int CLUEHELPER_FAIRYRING_HARD_9 = 1910;
            public static final int CLUEHELPER_FAIRYRING_HARD_VM01 = 3504;
        }
    }

    public static final class CluehelperClueFalobard {
        private CluehelperClueFalobard() {}

        public static final int ID = 11;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_CLUE_TEXT = 2;
        public static final int COL_TARGET = 3;
        public static final int COL_REQUIREMENTS = 4;
        public static final int COL_REGION = 5;
        public static final int COL_ALLREGIONS = 6;
        public static final int COL_RELATED_CONTENT = 7;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_FALOBARD_MASTER_0 = 1722;
            public static final int CLUEHELPER_FALOBARD_MASTER_1 = 1723;
            public static final int CLUEHELPER_FALOBARD_MASTER_2 = 1724;
            public static final int CLUEHELPER_FALOBARD_MASTER_3 = 1725;
            public static final int CLUEHELPER_FALOBARD_MASTER_4 = 1726;
            public static final int CLUEHELPER_FALOBARD_MASTER_5 = 1727;
            public static final int CLUEHELPER_FALOBARD_MASTER_6 = 1728;
            public static final int CLUEHELPER_FALOBARD_MASTER_7 = 1729;
            public static final int CLUEHELPER_FALOBARD_MASTER_8 = 1730;
            public static final int CLUEHELPER_FALOBARD_MASTER_9 = 1731;
            public static final int CLUEHELPER_FALOBARD_MASTER_10 = 1732;
            public static final int CLUEHELPER_FALOBARD_MASTER_11 = 1733;
            public static final int CLUEHELPER_FALOBARD_MASTER_12 = 1734;
            public static final int CLUEHELPER_FALOBARD_MASTER_13 = 1735;
            public static final int CLUEHELPER_FALOBARD_MASTER_14 = 1736;
            public static final int CLUEHELPER_FALOBARD_MASTER_15 = 1737;
            public static final int CLUEHELPER_FALOBARD_MASTER_16 = 1738;
            public static final int CLUEHELPER_FALOBARD_MASTER_17 = 1739;
            public static final int CLUEHELPER_FALOBARD_MASTER_18 = 1740;
            public static final int CLUEHELPER_FALOBARD_MASTER_SAIL = 7050;
        }
    }

    public static final class CluehelperClueHotcold {
        private CluehelperClueHotcold() {}

        public static final int ID = 12;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_TARGET = 2;
        public static final int COL_REQUIREMENTS = 3;
        public static final int COL_COMBAT_ENCOUNTER = 4;
        public static final int COL_REGION = 5;
        public static final int COL_ALLREGIONS = 6;
        public static final int COL_RELATED_CONTENT = 7;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_HOTCOLD_BEGINNER_DRAYNOR_MANOR_MUSHROOMS = 685;
            public static final int CLUEHELPER_HOTCOLD_BEGINNER_DRAYNOR_WHEAT_FIELD = 686;
            public static final int CLUEHELPER_HOTCOLD_BEGINNER_ICE_MOUNTAIN = 687;
            public static final int CLUEHELPER_HOTCOLD_BEGINNER_LUMBRIDGE_COW_FIELD = 688;
            public static final int CLUEHELPER_HOTCOLD_BEGINNER_NORTHEAST_OF_AL_KHARID_MINE = 689;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_WARRIORS = 690;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_JATIX = 691;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_BARB = 692;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_MIAZRQA = 693;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_COW = 694;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_PARTY_ROOM = 695;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_CRAFT_GUILD = 696;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_RIMMINGTON = 697;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_MUDSKIPPER = 698;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ASGARNIA_TROLL = 699;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_GENIE = 700;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_ALKHARID_MINE = 701;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_MENAPHOS_GATE = 702;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_BEDABIN_CAMP = 703;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_UZER = 704;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_POLLNIVNEACH = 705;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_MTA = 706;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_SHANTY = 707;
            public static final int CLUEHELPER_HOTCOLD_MASTER_DESERT_ULLEK = 708;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FELDIP_HILLS_JIGGIG = 709;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FELDIP_HILLS_SW = 710;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FELDIP_HILLS_GNOME_GLITER = 711;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FELDIP_HILLS_RANTZ = 712;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FELDIP_HILLS_SOUTH = 713;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FELDIP_HILLS_RED_CHIN = 714;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FELDIP_HILLS_SE = 715;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FELDIP_HILLS_CW_BALLOON = 716;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_MTN_CAMP = 717;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_RELLEKKA_HUNTER = 718;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_KELGADRIM_ENTRANCE = 719;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_SW = 720;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_LIGHTHOUSE = 721;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_ETCETERIA_CASTLE = 722;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_MISC_COURTYARD = 723;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_FREMMY_ISLES_MINE = 724;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_WEST_ISLES_MINE = 725;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_WEST_JATIZSO_ENTRANCE = 726;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_PIRATES_COVE = 727;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_ASTRAL_ALTER = 728;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_LUNAR_VILLAGE = 729;
            public static final int CLUEHELPER_HOTCOLD_MASTER_FREMENNIK_PROVINCE_LUNAR_NORTH = 730;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ISLE_OF_SOULS_MINE = 731;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_SINCLAR_MANSION = 732;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_CATHERBY = 733;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_GRAND_TREE = 734;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_SEERS = 735;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_MCGRUBORS_WOOD = 736;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_FISHING_BUILD = 737;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_WITCHHAVEN = 738;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_NECRO_TOWER = 739;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_FIGHT_ARENA = 740;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_TREE_GNOME_VILLAGE = 741;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_GRAVE_OF_SCORPIUS = 742;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_KHAZARD_BATTLEFIELD = 743;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_WEST_ARDY = 744;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_SW_TREE_GNOME_STRONGHOLD = 745;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_OUTPOST = 746;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_BAXTORIAN_FALLS = 747;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KANDARIN_BA_AGILITY_COURSE = 748;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KARAMJA_MUSA_POINT = 749;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KARAMJA_BRIMHAVEN_FRUIT_TREE = 750;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KARAMJA_WEST_BRIMHAVEN = 751;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KARAMJA_GLIDER = 752;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KARAMJA_KHARAZI_NE = 753;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KARAMJA_KHARAZI_SW = 754;
            public static final int CLUEHELPER_HOTCOLD_MASTER_KARAMJA_CRASH_ISLAND = 755;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MISTHALIN_VARROCK_STONE_CIRCLE = 756;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MISTHALIN_LUMBRIDGE = 757;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MISTHALIN_LUMBRIDGE_2 = 758;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MISTHALIN_GERTUDES = 759;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MISTHALIN_DRAYNOR_BANK = 760;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MISTHALIN_LUMBER_YARD = 761;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_BURGH_DE_ROTT = 762;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_DARKMEYER = 763;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_PORT_PHASMATYS = 764;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_HOLLOWS = 765;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_SWAMP = 766;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_HAUNTED_MINE = 767;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_MAUSOLEUM = 768;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_MOS_LES_HARMLESS = 769;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_MOS_LES_HARMLESS_BAR = 770;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_DRAGONTOOTH_NORTH = 771;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_DRAGONTOOTH_SOUTH = 772;
            public static final int CLUEHELPER_HOTCOLD_MASTER_MORYTANIA_SLEPE_TENTS = 773;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_EAGLES_PEAK = 774;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_PISCATORIS = 775;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_PISCATORIS_HUNTER_AREA = 776;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_ARANDAR = 777;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_ELF_CAMP_EAST = 778;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_ELF_CAMP_NW = 779;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_LLETYA = 780;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_TYRAS = 781;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WESTERN_PROVINCE_ZULANDRA = 782;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_5 = 783;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_12 = 784;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_20 = 785;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_27 = 786;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_28 = 787;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_32 = 788;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_35 = 789;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_37 = 790;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_38 = 791;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_49 = 792;
            public static final int CLUEHELPER_HOTCOLD_MASTER_WILDERNESS_54 = 793;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_BLASTMINE_BANK = 794;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_BLASTMINE_NORTH = 795;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_LOVAKITE_FURNACE = 796;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_LOVAKENGJ_MINE = 797;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_SULPHR_MINE = 798;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_SHAYZIEN_BANK = 799;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_OVERPASS = 800;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_LIZARDMAN = 801;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_COMBAT_RING = 802;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_SHAYZIEN_BANK_2 = 803;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_LIBRARY = 804;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_HOUSECHURCH = 805;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_DARK_ALTAR = 806;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_ARCEUUS_HOUSE = 807;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_ESSENCE_MINE = 808;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_ESSENCE_MINE_NE = 809;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_PISCARILUS_MINE = 810;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_GOLDEN_FIELD_TAVERN = 811;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_MESS_HALL = 812;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_WATSONS_HOUSE = 813;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_VANNAHS_FARM_STORE = 814;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_FARMING_GUILD_W = 815;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_DAIRY_COW = 816;
            public static final int CLUEHELPER_HOTCOLD_MASTER_ZEAH_CRIMSON_SWIFTS = 817;
            public static final int CLUEHELPER_HOTCOLD_CLUEQUEST_3 = 2297;
            public static final int CLUEHELPER_HOTCOLD_MASTER_VARLAMORE_OASIS = 3487;
            public static final int CLUEHELPER_HOTCOLD_MASTER_VARLAMORE_SUNSET = 3488;
            public static final int CLUEHELPER_HOTCOLD_MASTER_VARLAMORE_BAZAAR = 3489;
            public static final int CLUEHELPER_HOTCOLD_MASTER_VARLAMORE_ALDARIN_THEATRE = 3689;
            public static final int CLUEHELPER_HOTCOLD_MASTER_SAIL_BUCCANEERS_HAVEN = 4963;
            public static final int CLUEHELPER_HOTCOLD_MASTER_VARLAMORE_RAINFOREST_CENTRE = 5182;
            public static final int CLUEHELPER_HOTCOLD_MASTER_SAIL_GREAT_CONCH = 7051;
            public static final int CLUEHELPER_HOTCOLD_MASTER_SAIL_DRUMSTICK_ISLE = 7053;
        }
    }

    public static final class CluehelperClueMusic {
        private CluehelperClueMusic() {}

        public static final int ID = 13;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_MUSIC = 2;
        public static final int COL_UNLOCK_TEXT = 3;
        public static final int COL_TARGET = 4;
        public static final int COL_REQUIREMENTS = 5;
        public static final int COL_REGION = 6;
        public static final int COL_ALLREGIONS = 7;
        public static final int COL_RELATED_CONTENT = 8;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_MUSIC_EASY_0 = 1876;
            public static final int CLUEHELPER_MUSIC_EASY_1 = 1877;
            public static final int CLUEHELPER_MUSIC_EASY_2 = 1878;
            public static final int CLUEHELPER_MUSIC_EASY_3 = 1879;
            public static final int CLUEHELPER_MUSIC_EASY_4 = 1880;
            public static final int CLUEHELPER_MUSIC_EASY_5 = 1881;
            public static final int CLUEHELPER_MUSIC_MEDIUM_0 = 1882;
            public static final int CLUEHELPER_MUSIC_MEDIUM_1 = 1883;
            public static final int CLUEHELPER_MUSIC_MEDIUM_2 = 1884;
            public static final int CLUEHELPER_MUSIC_MEDIUM_3 = 1885;
            public static final int CLUEHELPER_MUSIC_MEDIUM_4 = 1886;
            public static final int CLUEHELPER_MUSIC_MEDIUM_5 = 1887;
            public static final int CLUEHELPER_MUSIC_MEDIUM_6 = 1888;
            public static final int CLUEHELPER_MUSIC_HARD_0 = 1889;
            public static final int CLUEHELPER_MUSIC_HARD_1 = 1890;
            public static final int CLUEHELPER_MUSIC_HARD_2 = 1891;
            public static final int CLUEHELPER_MUSIC_HARD_3 = 1892;
            public static final int CLUEHELPER_MUSIC_HARD_4 = 1893;
            public static final int CLUEHELPER_MUSIC_HARD_5 = 1894;
            public static final int CLUEHELPER_MUSIC_HARD_6 = 1895;
            public static final int CLUEHELPER_MUSIC_HARD_7 = 1896;
            public static final int CLUEHELPER_MUSIC_HARD_8 = 1897;
            public static final int CLUEHELPER_MUSIC_ELITE_0 = 1898;
            public static final int CLUEHELPER_MUSIC_ELITE_1 = 1899;
            public static final int CLUEHELPER_MUSIC_HARD_VM01 = 3503;
        }
    }

    public static final class CluehelperClueSkillchallenge {
        private CluehelperClueSkillchallenge() {}

        public static final int ID = 14;
        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_CLUE_TEXT = 2;
        public static final int COL_REQUIREMENTS = 3;
        public static final int COL_TARGET = 4;
        public static final int COL_REGION = 5;
        public static final int COL_ALLREGIONS = 6;
        public static final int COL_RELATED_CONTENT = 7;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_SKILLCHALLENGE_BEGINNER_0 = 1811;
            public static final int CLUEHELPER_SKILLCHALLENGE_BEGINNER_1 = 1812;
            public static final int CLUEHELPER_SKILLCHALLENGE_BEGINNER_2 = 1813;
            public static final int CLUEHELPER_SKILLCHALLENGE_BEGINNER_3 = 1814;
            public static final int CLUEHELPER_SKILLCHALLENGE_BEGINNER_4 = 1815;
            public static final int CLUEHELPER_SKILLCHALLENGE_BEGINNER_5 = 1816;
            public static final int CLUEHELPER_SKILLCHALLENGE_BEGINNER_6 = 1817;
            public static final int CLUEHELPER_SKILLCHALLENGE_BEGINNER_7 = 1818;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_0 = 1819;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_1 = 1820;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_2 = 1821;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_3 = 1822;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_4 = 1823;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_5 = 1824;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_6 = 1825;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_7 = 1826;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_8 = 1827;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_9 = 1828;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_10 = 1829;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_11 = 1830;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_12 = 1831;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_13 = 1832;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_14 = 1833;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_15 = 1834;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_16 = 1835;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_17 = 1836;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_18 = 1837;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_19 = 1838;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_20 = 1839;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_21 = 1840;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_22 = 1841;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_23 = 1842;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_0 = 1843;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_1 = 1844;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_2 = 1845;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_3 = 1846;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_4 = 1847;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_5 = 1848;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_6 = 1849;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_7 = 1850;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_8 = 1851;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_9 = 1852;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_10 = 1853;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_11 = 1854;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_12 = 1855;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_13 = 1856;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_14 = 1857;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_15 = 1858;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_16 = 1859;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_17 = 1860;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_18 = 1861;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_19 = 1862;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_20 = 1863;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_21 = 1864;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_22 = 1865;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_23 = 1866;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_24 = 1867;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_25 = 1868;
            public static final int CLUEHELPER_SKILLCHALLENGE_MASTER_VM01 = 3502;
            public static final int CLUEHELPER_SKILLCHALLENGE_ELITE_NICKEL = 7059;
        }
    }

    public static final class CluehelperTargetNpc {
        private CluehelperTargetNpc() {}

        public static final int ID = 15;
        public static final int COL_NPC = 0;
        public static final int COL_FALLBACK_NPC = 1;
        public static final int COL_COORD = 2;
        public static final int COL_DESCRIPTION = 3;
        public static final int COL_TELEPORT_COORD = 4;
        public static final int COL_TELEPORT_RESTRICTION_QUEST = 5;
        public static final int COL_TELEPORT_RESTRICTION_SKILL = 6;
        public static final int COL_TELEPORT_RESTRICTION_CUSTOM = 7;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_TARGET_NPC_RANAEL = 832;
            public static final int CLUEHELPER_TARGET_NPC_APOTHECARY = 833;
            public static final int CLUEHELPER_TARGET_NPC_SEDRIDOR = 834;
            public static final int CLUEHELPER_TARGET_NPC_DORIC = 835;
            public static final int CLUEHELPER_TARGET_NPC_BRIAN = 836;
            public static final int CLUEHELPER_TARGET_NPC_VERONICA = 837;
            public static final int CLUEHELPER_TARGET_NPC_HAIRDRESSER = 839;
            public static final int CLUEHELPER_TARGET_NPC_FORTUNATO = 840;
            public static final int CLUEHELPER_TARGET_NPC_BARAEK = 841;
            public static final int CLUEHELPER_TARGET_NPC_SABA = 842;
            public static final int CLUEHELPER_TARGET_NPC_CAPTAIN_TOBIAS = 843;
            public static final int CLUEHELPER_TARGET_NPC_JARAAH = 844;
            public static final int CLUEHELPER_TARGET_NPC_ARETHA = 845;
            public static final int CLUEHELPER_TARGET_NPC_CAROLINE = 846;
            public static final int CLUEHELPER_TARGET_NPC_ORACLE = 847;
            public static final int CLUEHELPER_TARGET_NPC_CHARLIE_THE_TRAMP = 848;
            public static final int CLUEHELPER_TARGET_NPC_BRIMSTAIL = 849;
            public static final int CLUEHELPER_TARGET_NPC_MADAME_CALDARIUM = 850;
            public static final int CLUEHELPER_TARGET_NPC_NICHOLAS = 851;
            public static final int CLUEHELPER_TARGET_NPC_ZOO_KEEPER = 853;
            public static final int CLUEHELPER_TARGET_NPC_LOWE = 854;
            public static final int CLUEHELPER_TARGET_NPC_OTTO_GODBLESSED = 855;
            public static final int CLUEHELPER_TARGET_NPC_KING_BOLREN = 856;
            public static final int CLUEHELPER_TARGET_NPC_LUTHAS = 858;
            public static final int CLUEHELPER_TARGET_NPC_EOHRIC = 859;
            public static final int CLUEHELPER_TARGET_NPC_HORPHIS = 861;
            public static final int CLUEHELPER_TARGET_NPC_MARISI = 862;
            public static final int CLUEHELPER_TARGET_NPC_FYCIE = 863;
            public static final int CLUEHELPER_TARGET_NPC_DOMINIC_ONION = 864;
            public static final int CLUEHELPER_TARGET_NPC_SIR_KAY = 866;
            public static final int CLUEHELPER_TARGET_NPC_KING_ROALD = 867;
            public static final int CLUEHELPER_TARGET_NPC_KAYLEE = 868;
            public static final int CLUEHELPER_TARGET_NPC_GALLOW = 869;
            public static final int CLUEHELPER_TARGET_NPC_FEMI = 870;
            public static final int CLUEHELPER_TARGET_NPC_EDMOND = 871;
            public static final int CLUEHELPER_TARGET_NPC_CAPTAIN_GINEA = 872;
            public static final int CLUEHELPER_TARGET_NPC_FLAX_KEEPER = 873;
            public static final int CLUEHELPER_TARGET_NPC_PARTY_PETE = 874;
            public static final int CLUEHELPER_TARGET_NPC_KARIM = 875;
            public static final int CLUEHELPER_TARGET_NPC_TARIA = 876;
            public static final int CLUEHELPER_TARGET_NPC_CLERRIS = 877;
            public static final int CLUEHELPER_TARGET_NPC_DUNSTAN = 878;
            public static final int CLUEHELPER_TARGET_NPC_DOCKMASTER = 879;
            public static final int CLUEHELPER_TARGET_NPC_HICKTON = 880;
            public static final int CLUEHELPER_TARGET_NPC_DRUNKEN_SOLDIER = 881;
            public static final int CLUEHELPER_TARGET_NPC_BRAMBICKLE = 883;
            public static final int CLUEHELPER_TARGET_NPC_LUMBRIDGE_GUIDE = 884;
            public static final int CLUEHELPER_TARGET_NPC_BOLKOY = 885;
            public static final int CLUEHELPER_TARGET_NPC_GNOME_COACH = 886;
            public static final int CLUEHELPER_TARGET_NPC_PROSPECTOR_PERCY = 887;
            public static final int CLUEHELPER_TARGET_NPC_DAER_KRAND = 888;
            public static final int CLUEHELPER_TARGET_NPC_DARK_MAGE = 889;
            public static final int CLUEHELPER_TARGET_NPC_STRANGE_OLD_MAN = 891;
            public static final int CLUEHELPER_TARGET_NPC_DRUNKEN_DWARF = 892;
            public static final int CLUEHELPER_TARGET_NPC_BROTHER_OMAD = 893;
            public static final int CLUEHELPER_TARGET_NPC_LAMMY_LANGLE = 894;
            public static final int CLUEHELPER_TARGET_NPC_CAPN_IZZY_NO_BEARD = 895;
            public static final int CLUEHELPER_TARGET_NPC_MARTIN_THWAIT = 897;
            public static final int CLUEHELPER_TARGET_NPC_EVIL_DAVE = 899;
            public static final int CLUEHELPER_TARGET_NPC_REGATH = 900;
            public static final int CLUEHELPER_TARGET_NPC_ONEIROMANCER = 901;
            public static final int CLUEHELPER_TARGET_NPC_OLD_CRONE = 902;
            public static final int CLUEHELPER_TARGET_NPC_GUARD_VEMMELDO = 903;
            public static final int CLUEHELPER_TARGET_NPC_CAM_THE_CAMEL = 904;
            public static final int CLUEHELPER_TARGET_NPC_AMBASSADOR_ALVIJAR = 905;
            public static final int CLUEHELPER_TARGET_NPC_ORONWEN = 906;
            public static final int CLUEHELPER_TARGET_NPC_NURSE_WOONED = 907;
            public static final int CLUEHELPER_TARGET_NPC_LISSE_ISAAKSON = 908;
            public static final int CLUEHELPER_TARGET_NPC_SIGLI_THE_HUNTSMAN = 909;
            public static final int CLUEHELPER_TARGET_NPC_JARDRIC_CAMP = 910;
            public static final int CLUEHELPER_TARGET_NPC_JARDRIC_BOAT = 911;
            public static final int CLUEHELPER_TARGET_NPC_CAPTAIN_BRUCE = 913;
            public static final int CLUEHELPER_TARGET_NPC_SACRIFICE = 914;
            public static final int CLUEHELPER_TARGET_NPC_EDWARD = 915;
            public static final int CLUEHELPER_TARGET_NPC_MANDRITH = 916;
            public static final int CLUEHELPER_TARGET_NPC_DUGOPUL = 917;
            public static final int CLUEHELPER_TARGET_NPC_RUNOLF = 918;
            public static final int CLUEHELPER_TARGET_NPC_IMMENIZZ = 919;
            public static final int CLUEHELPER_TARGET_NPC_LUMINATA = 920;
            public static final int CLUEHELPER_TARGET_NPC_OLD_MAN_RAL = 921;
            public static final int CLUEHELPER_TARGET_NPC_PRIMULA = 923;
            public static final int CLUEHELPER_TARGET_NPC_GOREU = 924;
            public static final int CLUEHELPER_TARGET_NPC_GUILDMASTER_LARS = 925;
            public static final int CLUEHELPER_TARGET_NPC_WINGSTONE = 926;
            public static final int CLUEHELPER_TARGET_NPC_NEW_RECRUIT_TONY = 927;
            public static final int CLUEHELPER_TARGET_NPC_FALADOR_WOMAN = 928;
            public static final int CLUEHELPER_TARGET_NPC_GNOME_BALL_REFEREE = 930;
            public static final int CLUEHELPER_TARGET_NPC_BROTHER_KOJO = 931;
            public static final int CLUEHELPER_TARGET_NPC_MINER_MAGNUS = 932;
            public static final int CLUEHELPER_TARGET_NPC_HANS = 933;
            public static final int CLUEHELPER_TARGET_NPC_COOK = 934;
            public static final int CLUEHELPER_TARGET_NPC_HUNDING = 935;
            public static final int CLUEHELPER_TARGET_NPC_CHARLIE_THE_TRAMP_TASK = 936;
            public static final int CLUEHELPER_TARGET_NPC_SHANTAY = 937;
            public static final int CLUEHELPER_TARGET_NPC_BARTENDER_RUSTY_ANCHOR = 938;
            public static final int CLUEHELPER_TARGET_NPC_SARAH = 939;
            public static final int CLUEHELPER_TARGET_NPC_BARTENDER_BLUE_MOON = 940;
            public static final int CLUEHELPER_TARGET_NPC_FATHER_JEAN = 941;
            public static final int CLUEHELPER_TARGET_NPC_DORIS = 942;
            public static final int CLUEHELPER_TARGET_NPC_NED = 943;
            public static final int CLUEHELPER_TARGET_NPC_RUSTY = 944;
            public static final int CLUEHELPER_TARGET_NPC_AMBASSADOR_SPANFIPPLE = 945;
            public static final int CLUEHELPER_TARGET_NPC_LUCY = 946;
            public static final int CLUEHELPER_TARGET_NPC_LOUISA = 947;
            public static final int CLUEHELPER_TARGET_NPC_VANNAKA = 948;
            public static final int CLUEHELPER_TARGET_NPC_THE_LADY_OF_THE_LAKE = 949;
            public static final int CLUEHELPER_TARGET_NPC_JATIX = 950;
            public static final int CLUEHELPER_TARGET_NPC_GAIUS = 951;
            public static final int CLUEHELPER_TARGET_NPC_JEED = 952;
            public static final int CLUEHELPER_TARGET_NPC_ARHEIN = 953;
            public static final int CLUEHELPER_TARGET_NPC_ERMIN = 954;
            public static final int CLUEHELPER_TARGET_NPC_CASSIE = 955;
            public static final int CLUEHELPER_TARGET_NPC_HERQUIN = 956;
            public static final int CLUEHELPER_TARGET_NPC_THE_FACE = 957;
            public static final int CLUEHELPER_TARGET_NPC_SQUIRE = 958;
            public static final int CLUEHELPER_TARGET_NPC_TYNAN = 959;
            public static final int CLUEHELPER_TARGET_NPC_TOOL_LEPRECHAUN = 960;
            public static final int CLUEHELPER_TARGET_NPC_KONOO = 961;
            public static final int CLUEHELPER_TARGET_NPC_ZEKE = 962;
            public static final int CLUEHELPER_TARGET_NPC_ELLIS = 963;
            public static final int CLUEHELPER_TARGET_NPC_CHEMIST = 964;
            public static final int CLUEHELPER_TARGET_NPC_MORGAN = 965;
            public static final int CLUEHELPER_TARGET_NPC_CHARLES = 966;
            public static final int CLUEHELPER_TARGET_NPC_ALI_THE_LEAFLET_DROPPER = 967;
            public static final int CLUEHELPER_TARGET_NPC_COOK_BLUE_MOON = 968;
            public static final int CLUEHELPER_TARGET_NPC_WAYNE = 969;
            public static final int CLUEHELPER_TARGET_NPC_SHERLOCK = 971;
            public static final int CLUEHELPER_TARGET_NPC_WATSON = 972;
            public static final int CLUEHELPER_TARGET_NPC_JORRAL = 974;
            public static final int CLUEHELPER_TARGET_NPC_FATHER_AERECK = 975;
            public static final int CLUEHELPER_TARGET_NPC_CANDLE_MAKER = 976;
            public static final int CLUEHELPER_TARGET_NPC_BARKER = 977;
            public static final int CLUEHELPER_TARGET_NPC_KAMFREENA = 978;
            public static final int CLUEHELPER_TARGET_NPC_WYSON_THE_GARDENER = 979;
            public static final int CLUEHELPER_TARGET_NPC_HORACIO = 980;
            public static final int CLUEHELPER_TARGET_NPC_GYPSY_ARIS = 981;
            public static final int CLUEHELPER_TARGET_NPC_DAGA = 982;
            public static final int CLUEHELPER_TARGET_NPC_SQUIRE_VETERAN = 983;
            public static final int CLUEHELPER_TARGET_NPC_MAWRTH = 984;
            public static final int CLUEHELPER_TARGET_NPC_GENIE = 985;
            public static final int CLUEHELPER_TARGET_NPC_OZIACH = 986;
            public static final int CLUEHELPER_TARGET_NPC_HECKEL_FUNCH = 987;
            public static final int CLUEHELPER_TARGET_NPC_SANIBOCH = 988;
            public static final int CLUEHELPER_TARGET_NPC_HAMID = 989;
            public static final int CLUEHELPER_TARGET_NPC_HEAD_CHEF = 991;
            public static final int CLUEHELPER_TARGET_NPC_ZUL_CHERAY = 992;
            public static final int CLUEHELPER_TARGET_NPC_ELLENA = 993;
            public static final int CLUEHELPER_TARGET_NPC_GNOME_TRAINER = 994;
            public static final int CLUEHELPER_TARGET_NPC_GUARDIAN_MUMMY = 995;
            public static final int CLUEHELPER_TARGET_NPC_SIR_VYVIN = 996;
            public static final int CLUEHELPER_TARGET_NPC_WILOUGH = 997;
            public static final int CLUEHELPER_TARGET_NPC_GERRANT = 998;
            public static final int CLUEHELPER_TARGET_NPC_EXAMINER = 999;
            public static final int CLUEHELPER_TARGET_NPC_GENERAL_BENTNOZE = 1000;
            public static final int CLUEHELPER_TARGET_NPC_SIR_PRYSIN = 1001;
            public static final int CLUEHELPER_TARGET_NPC_ABBOT_LANGLEY = 1004;
            public static final int CLUEHELPER_TARGET_NPC_GENERAL_HINING = 1005;
            public static final int CLUEHELPER_TARGET_NPC_FALO_THE_BARD = 1006;
            public static final int CLUEHELPER_TARGET_NPC_KEY_MASTER = 1007;
            public static final int CLUEHELPER_TARGET_NPC_PILES = 1008;
            public static final int CLUEHELPER_TARGET_NPC_ROBIN = 1009;
            public static final int CLUEHELPER_TARGET_NPC_ABBOT_LANGLEY_PRAYER = 1010;
            public static final int CLUEHELPER_TARGET_NPC_EWESEY = 1011;
            public static final int CLUEHELPER_TARGET_NPC_LOVADA = 1014;
            public static final int CLUEHELPER_TARGET_NPC_MONK_OF_ENTRANA = 1015;
            public static final int CLUEHELPER_TARGET_NPC_LOGOSIA = 1017;
            public static final int CLUEHELPER_TARGET_NPC_SIR_VYVIN_ARMOUR = 1019;
            public static final int CLUEHELPER_TARGET_NPC_BIBLIA = 1020;
            public static final int CLUEHELPER_TARGET_NPC_ULIZIUS = 1021;
            public static final int CLUEHELPER_TARGET_NPC_DONOVAN_THE_FAMILY_HANDYMAN = 1022;
            public static final int CLUEHELPER_TARGET_NPC_KANGAI_MAU = 1023;
            public static final int CLUEHELPER_TARGET_NPC_HAJEDY = 1024;
            public static final int CLUEHELPER_TARGET_NPC_ROAVAR = 1025;
            public static final int CLUEHELPER_TARGET_NPC_IRON_MAN_TUTOR = 1029;
            public static final int CLUEHELPER_TARGET_NPC_DREZEL = 1032;
            public static final int CLUEHELPER_TARGET_NPC_GRACKLEBONE = 1033;
            public static final int CLUEHELPER_TARGET_NPC_TRAIBORN = 1034;
            public static final int CLUEHELPER_TARGET_NPC_FAIRY_QUEEN = 1035;
            public static final int CLUEHELPER_TARGET_NPC_FAIRY_GODFATHER = 1036;
            public static final int CLUEHELPER_TARGET_NPC_ELUNED_LLETYA = 1037;
            public static final int CLUEHELPER_TARGET_NPC_ELUNED_PRIF = 1038;
            public static final int CLUEHELPER_TARGET_NPC_KING_PERCIVAL = 1039;
            public static final int CLUEHELPER_TARGET_NPC_WEIRD_OLD_MAN = 1040;
            public static final int CLUEHELPER_TARGET_NPC_PIRATE_PETE = 1041;
            public static final int CLUEHELPER_TARGET_NPC_GUNNJORN = 1042;
            public static final int CLUEHELPER_TARGET_NPC_ARNOLD_LYDSPOR = 1043;
            public static final int CLUEHELPER_TARGET_NPC_HUNTERGUILD_PITRI = 3490;
            public static final int CLUEHELPER_TARGET_NPC_METLA = 3491;
            public static final int CLUEHELPER_TARGET_NPC_FUNBO = 3492;
            public static final int CLUEHELPER_TARGET_NPC_GLORY = 3493;
            public static final int CLUEHELPER_TARGET_NPC_UGLUG_NAR = 3596;
            public static final int CLUEHELPER_TARGET_NPC_TEICUH = 5184;
            public static final int CLUEHELPER_TARGET_NPC_RANULPH = 5185;
            public static final int CLUEHELPER_TARGET_NPC_SULISAL = 5186;
            public static final int CLUEHELPER_TARGET_NPC_TORGAN = 7071;
            public static final int CLUEHELPER_TARGET_NPC_LEAGUE_TUTOR = 13322;
        }
    }

    public static final class CluehelperTargetLoc {
        private CluehelperTargetLoc() {}

        public static final int ID = 16;
        public static final int COL_LOC = 0;
        public static final int COL_FALLBACK_LOC = 1;
        public static final int COL_COORD = 2;
        public static final int COL_DESCRIPTION = 3;
        public static final int COL_TELEPORT_COORD = 4;
        public static final int COL_TELEPORT_RESTRICTION_QUEST = 5;
        public static final int COL_TELEPORT_RESTRICTION_SKILL = 6;
        public static final int COL_TELEPORT_RESTRICTION_CUSTOM = 7;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_TARGET_LOC_KING_AWOWOGEI = 1044;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_0_42_42_15_28 = 1045;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_0_41_54_34_32 = 1046;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_0_40_50_5_48 = 1047;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_0_51_54_45_47 = 1048;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_0_38_49_25_46 = 1049;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_0_47_56_18_44 = 1050;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_HEMENSTER = 1051;
            public static final int CLUEHELPER_TARGET_LOC_PRISON_BUCKET = 1052;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_LARGE_CRATES_0_49_153_51_33 = 1053;
            public static final int CLUEHELPER_TARGET_LOC_FAI_WIZTOWER_BOOKCASE = 1054;
            public static final int CLUEHELPER_TARGET_LOC_KR_CRATE = 1055;
            public static final int CLUEHELPER_TARGET_LOC_CRATE = 1056;
            public static final int CLUEHELPER_TARGET_LOC_CRATE3 = 1057;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_TOAD_AND_CHICKEN = 1058;
            public static final int CLUEHELPER_TARGET_LOC_CHESTCLOSED_1_47_50_8_5 = 1059;
            public static final int CLUEHELPER_TARGET_LOC_FAI_FALADOR_SMALL_CRATES_0_47_52_21_27 = 1060;
            public static final int CLUEHELPER_TARGET_LOC_DRAYNOR_CRATE = 1061;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_LARGE_CRATES_0_50_53_26_60 = 1062;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_BOXES = 1063;
            public static final int CLUEHELPER_TARGET_LOC_CANAFIS_CRATE_0_54_54_53_41 = 1064;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_DWARVEN_MINE = 1065;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_ARDOUGNE_CHURCH = 1066;
            public static final int CLUEHELPER_TARGET_LOC_DRAYNOR_WARDROBE = 1067;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_RED_CRATE = 1068;
            public static final int CLUEHELPER_TARGET_LOC_FAI_BARBARIAN_SMALL_CRATES = 1069;
            public static final int CLUEHELPER_TARGET_LOC_FAI_FALADOR_SMALL_CRATES_0_46_52_11_62 = 1070;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS3 = 1071;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_LUMBRIDGE_SOUTH_TOWER = 1072;
            public static final int CLUEHELPER_TARGET_LOC_COFFIN = 1073;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_YANILLE_PIANO = 1074;
            public static final int CLUEHELPER_TARGET_LOC_SARIM_CRATE2 = 1075;
            public static final int CLUEHELPER_TARGET_LOC_BOXES_0_50_50_45_45 = 1076;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS1_0_46_51_25_47 = 1077;
            public static final int CLUEHELPER_TARGET_LOC_DIGSITEBUSH = 1078;
            public static final int CLUEHELPER_TARGET_LOC_BOOKCASE_0_47_54_46_28 = 1079;
            public static final int CLUEHELPER_TARGET_LOC_KR_SEERS_VILLAGE_DRAWERS1 = 1080;
            public static final int CLUEHELPER_TARGET_LOC_CHESTCLOSED_0_48_53_13_37 = 1081;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_TAVERLEY_OUTHOUSE = 1082;
            public static final int CLUEHELPER_TARGET_LOC_CHESTCLOSED_1_47_52_33_36 = 1083;
            public static final int CLUEHELPER_TARGET_LOC_SARIM_BOOKCASE2_0_49_49_10_41 = 1084;
            public static final int CLUEHELPER_TARGET_LOC_BOXES3_0_51_50_44_6 = 1085;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_41_51_29_56 = 1086;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS1_1_46_52_27_58 = 1087;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_DRAWERS_1_50_53_6_27 = 1088;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_DRAWERS_0_49_53_20_14 = 1089;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS1_0_47_50_16_59 = 1090;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_45_53_14_26 = 1091;
            public static final int CLUEHELPER_TARGET_LOC_DEATH_BOXES_2 = 1092;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_40_48_10_13 = 1093;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS1_0_45_55_49_50 = 1094;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_OLD_0_41_52_21_10 = 1095;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_47_52_31_14 = 1096;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS1_1_40_51_14_62 = 1097;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_48_51_25_13 = 1098;
            public static final int CLUEHELPER_TARGET_LOC_BOXES2_0_45_53_6_57 = 1099;
            public static final int CLUEHELPER_TARGET_LOC_CHESTCLOSED_1_50_50_9_18 = 1100;
            public static final int CLUEHELPER_TARGET_LOC_CHESTCLOSED_1_51_49_37_33 = 1101;
            public static final int CLUEHELPER_TARGET_LOC_BOXES3_0_41_51_30_35 = 1102;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_27_54_18_34 = 1103;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_44_53_9_50 = 1104;
            public static final int CLUEHELPER_TARGET_LOC_KR_CAMELOT_CHESTCLOSED = 1105;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_ARDOUGNE_GENERAL_STORE = 1106;
            public static final int CLUEHELPER_TARGET_LOC_RIMMINGTON_WHEEL_BARROW = 1107;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_LARGE_CRATE_2_0_50_53_28_41 = 1108;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_1_43_53_57_59 = 1109;
            public static final int CLUEHELPER_TARGET_LOC_CRATE3_OLD = 1110;
            public static final int CLUEHELPER_TARGET_LOC_CHESTCLOSED_0_46_153_56_6 = 1111;
            public static final int CLUEHELPER_TARGET_LOC_HOS_CRATE_02_0_26_56_19_32 = 1112;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_DRAWERS_1_50_53_50_28 = 1113;
            public static final int CLUEHELPER_TARGET_LOC_CHESTOPEN = 1114;
            public static final int CLUEHELPER_TARGET_LOC_SARIM_BOOKCASE2_0_49_49_13_41 = 1115;
            public static final int CLUEHELPER_TARGET_LOC_SARIM_CRATE = 1116;
            public static final int CLUEHELPER_TARGET_LOC_FAI_VARROCK_LARGE_CRATE_1_0_50_53_28_41 = 1117;
            public static final int CLUEHELPER_TARGET_LOC_BOXES2_0_45_53_35_60 = 1118;
            public static final int CLUEHELPER_TARGET_LOC_HOS_CRATE_02_0_28_56_7_29 = 1119;
            public static final int CLUEHELPER_TARGET_LOC_BOULDER4_SEARCH = 1120;
            public static final int CLUEHELPER_TARGET_LOC_ROOFTOPS_SEERS_CRATE = 1121;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS4 = 1122;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_UZER = 1123;
            public static final int CLUEHELPER_TARGET_LOC_QIP_COOK_CRATE_STACKED = 1124;
            public static final int CLUEHELPER_TARGET_LOC_BOOKCASE_0_48_149_24_36 = 1125;
            public static final int CLUEHELPER_TARGET_LOC_HAYSTACK3 = 1126;
            public static final int CLUEHELPER_TARGET_LOC_BOXES_1_39_54_27_37 = 1127;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_44_52_2_23 = 1128;
            public static final int CLUEHELPER_TARGET_LOC_DWARF_KELDAGRIM_WOODEN_BOXES = 1129;
            public static final int CLUEHELPER_TARGET_LOC_BOOKCASE_1_42_53_15_17 = 1130;
            public static final int CLUEHELPER_TARGET_LOC_DWARFROCK_BOOK_CART = 1131;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_YANILLE_DUNGEON = 1132;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_BANDIT_CAMP = 1133;
            public static final int CLUEHELPER_TARGET_LOC_SHELVES_BAMBOO = 1134;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_OLD_UNDERCOOK = 1135;
            public static final int CLUEHELPER_TARGET_LOC_ELEM_CRATE_1 = 1136;
            public static final int CLUEHELPER_TARGET_LOC_DRAWERS2_0_40_51_1_59 = 1137;
            public static final int CLUEHELPER_TARGET_LOC_CRATE2_KANDARIN_MONASTERY = 1138;
            public static final int CLUEHELPER_TARGET_LOC_CANAFIS_CRATE_0_54_54_42_51 = 1139;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_OLD_0_43_48_48_2 = 1140;
            public static final int CLUEHELPER_TARGET_LOC_TOURTRAP_QIP_CRATE_SINGLE = 1141;
            public static final int CLUEHELPER_TARGET_LOC_SLUG2_CRATE_SINGLE = 1142;
            public static final int CLUEHELPER_TARGET_LOC_CRATE_OLD_0_41_53_47_45 = 1143;
            public static final int CLUEHELPER_TARGET_LOC_HOS_BASKET_APPLE = 1144;
            public static final int CLUEHELPER_TARGET_LOC_HUNTERGUILD_SHELF = 3494;
            public static final int CLUEHELPER_TARGET_LOC_AUBURN_CRATE = 5174;
        }
    }

    public static final class CluehelperTargetMapzone {
        private CluehelperTargetMapzone() {}

        public static final int ID = 17;
        public static final int COL_COORD_SW = 0;
        public static final int COL_COORD_NE = 1;
        public static final int COL_DESCRIPTION = 2;
        public static final int COL_TELEPORT_COORD = 3;
        public static final int COL_TELEPORT_RESTRICTION_QUEST = 4;
        public static final int COL_TELEPORT_RESTRICTION_SKILL = 5;
        public static final int COL_TELEPORT_RESTRICTION_CUSTOM = 6;
    }

    public static final class CluehelperTargetCoord {
        private CluehelperTargetCoord() {}

        public static final int ID = 18;
        public static final int COL_COORD = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_DESCRIPTION_SHORT = 2;
        public static final int COL_TELEPORT_COORD = 3;
        public static final int COL_TELEPORT_RESTRICTION_QUEST = 4;
        public static final int COL_TELEPORT_RESTRICTION_SKILL = 5;
        public static final int COL_TELEPORT_RESTRICTION_CUSTOM = 6;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_TARGET_COORD_0_49_52_31_32 = 185;
            public static final int CLUEHELPER_TARGET_COORD_0_51_52_26_45 = 186;
            public static final int CLUEHELPER_TARGET_COORD_0_48_50_20_26 = 187;
            public static final int CLUEHELPER_TARGET_COORD_0_48_49_37_17 = 188;
            public static final int CLUEHELPER_TARGET_COORD_0_42_53_14_37 = 189;
            public static final int CLUEHELPER_TARGET_COORD_0_51_51_36_27 = 190;
            public static final int CLUEHELPER_TARGET_COORD_0_49_52_30_33 = 191;
            public static final int CLUEHELPER_TARGET_COORD_0_47_53_35_6 = 192;
            public static final int CLUEHELPER_TARGET_COORD_0_40_54_52_26 = 193;
            public static final int CLUEHELPER_TARGET_COORD_0_46_53_26_23 = 194;
            public static final int CLUEHELPER_TARGET_COORD_0_45_51_27_31 = 195;
            public static final int CLUEHELPER_TARGET_COORD_0_41_50_26_31 = 196;
            public static final int CLUEHELPER_TARGET_COORD_0_45_50_44_9 = 197;
            public static final int CLUEHELPER_TARGET_COORD_0_39_60_40_25 = 198;
            public static final int CLUEHELPER_TARGET_COORD_0_53_51_42_2 = 199;
            public static final int CLUEHELPER_TARGET_COORD_0_38_50_22_30 = 200;
            public static final int CLUEHELPER_TARGET_COORD_0_40_56_18_13 = 201;
            public static final int CLUEHELPER_TARGET_COORD_0_41_55_43_42 = 202;
            public static final int CLUEHELPER_TARGET_COORD_0_40_48_56_5 = 203;
            public static final int CLUEHELPER_TARGET_COORD_0_38_51_56_44 = 204;
            public static final int CLUEHELPER_TARGET_COORD_0_47_61_13_8 = 205;
            public static final int CLUEHELPER_TARGET_COORD_0_42_52_34_10 = 206;
            public static final int CLUEHELPER_TARGET_COORD_0_38_48_17_58 = 207;
            public static final int CLUEHELPER_TARGET_COORD_1_46_148_9_51 = 208;
            public static final int CLUEHELPER_TARGET_COORD_0_34_47_26_54 = 209;
            public static final int CLUEHELPER_TARGET_COORD_0_28_60_23_12 = 210;
            public static final int CLUEHELPER_TARGET_COORD_0_55_50_18_8 = 211;
            public static final int CLUEHELPER_TARGET_COORD_0_50_50_3_13 = 212;
            public static final int CLUEHELPER_TARGET_COORD_0_48_50_36_62 = 213;
            public static final int CLUEHELPER_TARGET_COORD_DESERT_ULLEK = 232;
            public static final int CLUEHELPER_TARGET_COORD_0_50_53_3_32 = 347;
            public static final int CLUEHELPER_TARGET_COORD_0_49_54_28_21 = 348;
            public static final int CLUEHELPER_TARGET_COORD_0_50_53_5_24 = 349;
            public static final int CLUEHELPER_TARGET_COORD_0_50_50_31_3 = 350;
            public static final int CLUEHELPER_TARGET_COORD_0_51_51_36_50 = 351;
            public static final int CLUEHELPER_TARGET_COORD_0_46_52_6_59 = 352;
            public static final int CLUEHELPER_TARGET_COORD_0_40_51_47_18 = 353;
            public static final int CLUEHELPER_TARGET_COORD_0_43_53_5_9 = 354;
            public static final int CLUEHELPER_TARGET_COORD_0_51_50_50_41 = 355;
            public static final int CLUEHELPER_TARGET_COORD_0_42_52_41_21 = 356;
            public static final int CLUEHELPER_TARGET_COORD_0_45_54_44_22 = 357;
            public static final int CLUEHELPER_TARGET_COORD_0_34_77_31_24 = 358;
            public static final int CLUEHELPER_TARGET_COORD_0_47_50_39_37 = 359;
            public static final int CLUEHELPER_TARGET_COORD_0_52_52_33_11 = 360;
            public static final int CLUEHELPER_TARGET_COORD_0_48_49_41_60 = 361;
            public static final int CLUEHELPER_TARGET_COORD_3_41_52_11_57 = 362;
            public static final int CLUEHELPER_TARGET_COORD_0_40_52_50_63 = 363;
            public static final int CLUEHELPER_TARGET_COORD_0_48_51_37_30 = 364;
            public static final int CLUEHELPER_TARGET_COORD_0_49_54_29_11 = 365;
            public static final int CLUEHELPER_TARGET_COORD_0_47_52_37_48 = 366;
            public static final int CLUEHELPER_TARGET_COORD_0_50_49_3_33 = 367;
            public static final int CLUEHELPER_TARGET_COORD_0_50_53_53_9 = 368;
            public static final int CLUEHELPER_TARGET_COORD_0_51_51_35_25 = 369;
            public static final int CLUEHELPER_TARGET_COORD_0_43_53_7_53 = 370;
            public static final int CLUEHELPER_TARGET_COORD_0_42_55_53_16 = 371;
            public static final int CLUEHELPER_TARGET_COORD_0_52_54_44_42 = 372;
            public static final int CLUEHELPER_TARGET_COORD_0_41_49_52_33 = 373;
            public static final int CLUEHELPER_TARGET_COORD_0_46_50_32_38 = 374;
            public static final int CLUEHELPER_TARGET_COORD_0_46_51_37_12 = 375;
            public static final int CLUEHELPER_TARGET_COORD_0_48_52_16_8 = 376;
            public static final int CLUEHELPER_TARGET_COORD_0_50_54_13_7 = 377;
            public static final int CLUEHELPER_TARGET_COORD_0_49_51_23_34 = 378;
            public static final int CLUEHELPER_TARGET_COORD_0_51_54_43_35 = 379;
            public static final int CLUEHELPER_TARGET_COORD_0_46_52_1_7 = 380;
            public static final int CLUEHELPER_TARGET_COORD_0_46_48_45_38 = 381;
            public static final int CLUEHELPER_TARGET_COORD_0_48_50_11_53 = 382;
            public static final int CLUEHELPER_TARGET_COORD_0_50_54_9_36 = 383;
            public static final int CLUEHELPER_TARGET_COORD_0_47_60_61_21 = 384;
            public static final int CLUEHELPER_TARGET_COORD_0_42_52_40_49 = 385;
            public static final int CLUEHELPER_TARGET_COORD_1_47_54_48_28 = 386;
            public static final int CLUEHELPER_TARGET_COORD_0_41_79_5_15 = 387;
            public static final int CLUEHELPER_TARGET_COORD_0_43_51_30_9 = 388;
            public static final int CLUEHELPER_TARGET_COORD_2_53_55_29_17 = 389;
            public static final int CLUEHELPER_TARGET_COORD_0_40_49_8_13 = 390;
            public static final int CLUEHELPER_TARGET_COORD_0_37_60_7_10 = 391;
            public static final int CLUEHELPER_TARGET_COORD_1_27_83_40_54 = 392;
            public static final int CLUEHELPER_TARGET_COORD_0_45_154_40_37 = 393;
            public static final int CLUEHELPER_TARGET_COORD_0_41_51_42_40 = 394;
            public static final int CLUEHELPER_TARGET_COORD_0_43_59_24_5 = 395;
            public static final int CLUEHELPER_TARGET_COORD_0_26_54_50_11 = 396;
            public static final int CLUEHELPER_TARGET_COORD_0_44_55_28_22 = 397;
            public static final int CLUEHELPER_TARGET_COORD_0_23_56_15_51 = 398;
            public static final int CLUEHELPER_TARGET_COORD_0_39_51_34_26 = 399;
            public static final int CLUEHELPER_TARGET_COORD_0_45_57_7_28 = 400;
            public static final int CLUEHELPER_TARGET_COORD_0_46_45_10_53 = 401;
            public static final int CLUEHELPER_TARGET_COORD_0_44_46_35_10 = 402;
            public static final int CLUEHELPER_TARGET_COORD_0_40_53_28_27 = 403;
            public static final int CLUEHELPER_TARGET_COORD_2_39_56_15_57 = 404;
            public static final int CLUEHELPER_TARGET_COORD_3_47_73_35_25 = 405;
            public static final int CLUEHELPER_TARGET_COORD_0_51_43_30_29 = 406;
            public static final int CLUEHELPER_TARGET_COORD_0_52_52_34_12 = 407;
            public static final int CLUEHELPER_TARGET_COORD_0_38_47_45_39 = 408;
            public static final int CLUEHELPER_TARGET_COORD_0_43_57_60_33 = 409;
            public static final int CLUEHELPER_TARGET_COORD_0_44_54_31_43 = 410;
            public static final int CLUEHELPER_TARGET_COORD_0_56_54_27_36 = 411;
            public static final int CLUEHELPER_TARGET_COORD_0_52_61_40_31 = 412;
            public static final int CLUEHELPER_TARGET_COORD_0_45_49_34_32 = 413;
            public static final int CLUEHELPER_TARGET_COORD_0_25_56_46_47 = 414;
            public static final int CLUEHELPER_TARGET_COORD_0_46_61_26_9 = 415;
            public static final int CLUEHELPER_TARGET_COORD_0_47_57_18_53 = 416;
            public static final int CLUEHELPER_TARGET_COORD_0_34_53_35_35 = 417;
            public static final int CLUEHELPER_TARGET_COORD_2_45_83_45_21 = 418;
            public static final int CLUEHELPER_TARGET_COORD_0_44_55_27_23 = 419;
            public static final int CLUEHELPER_TARGET_COORD_0_34_50_29_52 = 420;
            public static final int CLUEHELPER_TARGET_COORD_0_44_52_36_21 = 421;
            public static final int CLUEHELPER_TARGET_COORD_0_49_61_55_56 = 422;
            public static final int CLUEHELPER_TARGET_COORD_0_38_80_31_29 = 423;
            public static final int CLUEHELPER_TARGET_COORD_0_31_73_27_40 = 424;
            public static final int CLUEHELPER_TARGET_COORD_0_35_73_31_8 = 425;
            public static final int CLUEHELPER_TARGET_COORD_0_55_151_31_30 = 426;
            public static final int CLUEHELPER_TARGET_COORD_0_34_75_29_38 = 427;
            public static final int CLUEHELPER_TARGET_COORD_0_46_54_12_49 = 428;
            public static final int CLUEHELPER_TARGET_COORD_0_34_47_23_48 = 429;
            public static final int CLUEHELPER_TARGET_COORD_0_50_59_27_55 = 430;
            public static final int CLUEHELPER_TARGET_COORD_0_48_50_16_54 = 431;
            public static final int CLUEHELPER_TARGET_COORD_0_50_54_30_22 = 432;
            public static final int CLUEHELPER_TARGET_COORD_0_25_156_63_61 = 433;
            public static final int CLUEHELPER_TARGET_COORD_0_28_60_23_16 = 434;
            public static final int CLUEHELPER_TARGET_COORD_0_47_70_22_42 = 435;
            public static final int CLUEHELPER_TARGET_COORD_2_45_73_50_45 = 436;
            public static final int CLUEHELPER_TARGET_COORD_0_55_52_42_51 = 437;
            public static final int CLUEHELPER_TARGET_COORD_0_30_69_24_11 = 438;
            public static final int CLUEHELPER_TARGET_COORD_0_43_48_51_1 = 439;
            public static final int CLUEHELPER_TARGET_COORD_0_52_53_42_33 = 440;
            public static final int CLUEHELPER_TARGET_COORD_0_24_56_7_39 = 441;
            public static final int CLUEHELPER_TARGET_COORD_0_39_55_56_36 = 442;
            public static final int CLUEHELPER_TARGET_COORD_0_48_54_8_53 = 443;
            public static final int CLUEHELPER_TARGET_COORD_0_39_52_31_47 = 444;
            public static final int CLUEHELPER_TARGET_COORD_0_42_54_47_13 = 445;
            public static final int CLUEHELPER_TARGET_COORD_0_20_59_26_63 = 446;
            public static final int CLUEHELPER_TARGET_COORD_0_44_53_7_51 = 447;
            public static final int CLUEHELPER_TARGET_COORD_0_48_50_56_45 = 448;
            public static final int CLUEHELPER_TARGET_COORD_0_44_53_36_37 = 449;
            public static final int CLUEHELPER_TARGET_COORD_2_38_53_41_28 = 450;
            public static final int CLUEHELPER_TARGET_COORD_0_51_48_40_52 = 451;
            public static final int CLUEHELPER_TARGET_COORD_0_54_54_36_32 = 452;
            public static final int CLUEHELPER_TARGET_COORD_0_49_149_32_35 = 453;
            public static final int CLUEHELPER_TARGET_COORD_0_38_80_45_26 = 454;
            public static final int CLUEHELPER_TARGET_COORD_0_40_48_50_20 = 455;
            public static final int CLUEHELPER_TARGET_COORD_0_54_55_48_56 = 456;
            public static final int CLUEHELPER_TARGET_COORD_0_43_53_56_48 = 457;
            public static final int CLUEHELPER_TARGET_COORD_0_48_53_33_28 = 458;
            public static final int CLUEHELPER_TARGET_COORD_0_38_49_7_25 = 459;
            public static final int CLUEHELPER_TARGET_COORD_0_38_48_8_20 = 460;
            public static final int CLUEHELPER_TARGET_COORD_0_25_59_32_31 = 461;
            public static final int CLUEHELPER_TARGET_COORD_0_38_49_47_22 = 462;
            public static final int CLUEHELPER_TARGET_COORD_0_45_49_7_18 = 463;
            public static final int CLUEHELPER_TARGET_COORD_0_42_49_55_15 = 464;
            public static final int CLUEHELPER_TARGET_COORD_0_49_49_48_14 = 465;
            public static final int CLUEHELPER_TARGET_COORD_0_50_49_17_41 = 466;
            public static final int CLUEHELPER_TARGET_COORD_0_46_49_63_8 = 467;
            public static final int CLUEHELPER_TARGET_COORD_0_45_48_16_47 = 468;
            public static final int CLUEHELPER_TARGET_COORD_0_42_50_9_7 = 469;
            public static final int CLUEHELPER_TARGET_COORD_0_41_48_55_38 = 470;
            public static final int CLUEHELPER_TARGET_COORD_0_54_48_54_2 = 471;
            public static final int CLUEHELPER_TARGET_COORD_0_49_50_24_51 = 472;
            public static final int CLUEHELPER_TARGET_COORD_0_41_50_19_52 = 473;
            public static final int CLUEHELPER_TARGET_COORD_0_36_47_18_53 = 474;
            public static final int CLUEHELPER_TARGET_COORD_0_44_47_59_38 = 475;
            public static final int CLUEHELPER_TARGET_COORD_0_44_47_33_25 = 476;
            public static final int CLUEHELPER_TARGET_COORD_0_44_51_32_32 = 477;
            public static final int CLUEHELPER_TARGET_COORD_0_40_46_23_46 = 478;
            public static final int CLUEHELPER_TARGET_COORD_0_49_52_43_16 = 479;
            public static final int CLUEHELPER_TARGET_COORD_0_37_52_15_42 = 480;
            public static final int CLUEHELPER_TARGET_COORD_0_51_52_48_47 = 481;
            public static final int CLUEHELPER_TARGET_COORD_0_48_52_49_56 = 482;
            public static final int CLUEHELPER_TARGET_COORD_0_53_52_38_60 = 483;
            public static final int CLUEHELPER_TARGET_COORD_0_45_53_40_11 = 484;
            public static final int CLUEHELPER_TARGET_COORD_0_40_45_34_19 = 485;
            public static final int CLUEHELPER_TARGET_COORD_0_37_53_19_43 = 486;
            public static final int CLUEHELPER_TARGET_COORD_0_39_54_16_11 = 487;
            public static final int CLUEHELPER_TARGET_COORD_0_37_54_13_12 = 488;
            public static final int CLUEHELPER_TARGET_COORD_0_46_54_61_19 = 489;
            public static final int CLUEHELPER_TARGET_COORD_0_40_54_25_49 = 490;
            public static final int CLUEHELPER_TARGET_COORD_0_53_54_51_59 = 491;
            public static final int CLUEHELPER_TARGET_COORD_0_37_54_48_60 = 492;
            public static final int CLUEHELPER_TARGET_COORD_0_53_55_37_3 = 493;
            public static final int CLUEHELPER_TARGET_COORD_0_36_55_59_11 = 494;
            public static final int CLUEHELPER_TARGET_COORD_0_45_55_39_15 = 495;
            public static final int CLUEHELPER_TARGET_COORD_0_55_55_28_40 = 496;
            public static final int CLUEHELPER_TARGET_COORD_0_23_55_4_46 = 497;
            public static final int CLUEHELPER_TARGET_COORD_0_42_56_47_54 = 498;
            public static final int CLUEHELPER_TARGET_COORD_0_41_57_57_5 = 499;
            public static final int CLUEHELPER_TARGET_COORD_0_39_60_41_41 = 500;
            public static final int CLUEHELPER_TARGET_COORD_0_44_50_12_34 = 501;
            public static final int CLUEHELPER_TARGET_COORD_0_19_58_31_14 = 502;
            public static final int CLUEHELPER_TARGET_COORD_0_34_49_33_25 = 504;
            public static final int CLUEHELPER_TARGET_COORD_0_34_50_5_6 = 505;
            public static final int CLUEHELPER_TARGET_COORD_0_48_50_9_9 = 506;
            public static final int CLUEHELPER_TARGET_COORD_0_53_50_7_46 = 507;
            public static final int CLUEHELPER_TARGET_COORD_0_42_50_11_51 = 508;
            public static final int CLUEHELPER_TARGET_COORD_0_55_50_26_51 = 509;
            public static final int CLUEHELPER_TARGET_COORD_0_44_51_25_3 = 511;
            public static final int CLUEHELPER_TARGET_COORD_0_49_47_32_33 = 512;
            public static final int CLUEHELPER_TARGET_COORD_0_39_47_46_23 = 513;
            public static final int CLUEHELPER_TARGET_COORD_0_40_47_21_22 = 514;
            public static final int CLUEHELPER_TARGET_COORD_0_46_47_17_16 = 515;
            public static final int CLUEHELPER_TARGET_COORD_0_36_51_35_47 = 516;
            public static final int CLUEHELPER_TARGET_COORD_0_53_52_48_13 = 517;
            public static final int CLUEHELPER_TARGET_COORD_0_43_46_11_30 = 518;
            public static final int CLUEHELPER_TARGET_COORD_0_49_46_2_25 = 519;
            public static final int CLUEHELPER_TARGET_COORD_0_45_46_44_19 = 520;
            public static final int CLUEHELPER_TARGET_COORD_0_44_45_22_34 = 521;
            public static final int CLUEHELPER_TARGET_COORD_0_53_53_49_27 = 522;
            public static final int CLUEHELPER_TARGET_COORD_0_46_45_6_22 = 523;
            public static final int CLUEHELPER_TARGET_COORD_0_43_45_23_11 = 524;
            public static final int CLUEHELPER_TARGET_COORD_0_48_56_41_18 = 525;
            public static final int CLUEHELPER_TARGET_COORD_0_45_57_12_27 = 526;
            public static final int CLUEHELPER_TARGET_COORD_0_49_57_32_29 = 527;
            public static final int CLUEHELPER_TARGET_COORD_0_44_57_37_42 = 528;
            public static final int CLUEHELPER_TARGET_COORD_0_51_57_41_44 = 529;
            public static final int CLUEHELPER_TARGET_COORD_0_47_57_47_48 = 530;
            public static final int CLUEHELPER_TARGET_COORD_0_51_57_38_48 = 531;
            public static final int CLUEHELPER_TARGET_COORD_0_23_57_7_51 = 532;
            public static final int CLUEHELPER_TARGET_COORD_0_42_58_24_20 = 533;
            public static final int CLUEHELPER_TARGET_COORD_0_46_58_26_37 = 534;
            public static final int CLUEHELPER_TARGET_COORD_0_48_58_22_52 = 535;
            public static final int CLUEHELPER_TARGET_COORD_0_51_58_47_57 = 536;
            public static final int CLUEHELPER_TARGET_COORD_0_22_59_52_6 = 537;
            public static final int CLUEHELPER_TARGET_COORD_0_50_59_44_16 = 538;
            public static final int CLUEHELPER_TARGET_COORD_0_49_59_4_28 = 539;
            public static final int CLUEHELPER_TARGET_COORD_0_46_59_2_43 = 540;
            public static final int CLUEHELPER_TARGET_COORD_0_58_59_59_49 = 541;
            public static final int CLUEHELPER_TARGET_COORD_0_47_60_5_6 = 542;
            public static final int CLUEHELPER_TARGET_COORD_0_47_60_50_44 = 543;
            public static final int CLUEHELPER_TARGET_COORD_0_51_60_26_49 = 544;
            public static final int CLUEHELPER_TARGET_COORD_0_39_60_9_59 = 546;
            public static final int CLUEHELPER_TARGET_COORD_0_51_61_21_38 = 547;
            public static final int CLUEHELPER_TARGET_COORD_0_49_61_23_55 = 548;
            public static final int CLUEHELPER_TARGET_COORD_0_47_61_31_56 = 549;
            public static final int CLUEHELPER_TARGET_COORD_0_46_61_43_59 = 550;
            public static final int CLUEHELPER_TARGET_COORD_0_49_61_53_59 = 551;
            public static final int CLUEHELPER_TARGET_COORD_0_36_57_37_49 = 552;
            public static final int CLUEHELPER_TARGET_COORD_0_49_58_7_62 = 553;
            public static final int CLUEHELPER_TARGET_COORD_0_47_61_35_36 = 554;
            public static final int CLUEHELPER_TARGET_COORD_0_22_56_2_27 = 555;
            public static final int CLUEHELPER_TARGET_COORD_0_22_54_1_27 = 556;
            public static final int CLUEHELPER_TARGET_COORD_0_36_49_53_15 = 557;
            public static final int CLUEHELPER_TARGET_COORD_0_56_49_3_44 = 558;
            public static final int CLUEHELPER_TARGET_COORD_0_44_48_4_6 = 559;
            public static final int CLUEHELPER_TARGET_COORD_0_59_47_35_52 = 560;
            public static final int CLUEHELPER_TARGET_COORD_0_34_51_4_18 = 561;
            public static final int CLUEHELPER_TARGET_COORD_0_44_46_54_53 = 562;
            public static final int CLUEHELPER_TARGET_COORD_0_51_46_38_44 = 563;
            public static final int CLUEHELPER_TARGET_COORD_0_39_46_15_36 = 564;
            public static final int CLUEHELPER_TARGET_COORD_0_42_52_44_44 = 565;
            public static final int CLUEHELPER_TARGET_COORD_0_55_53_53_33 = 566;
            public static final int CLUEHELPER_TARGET_COORD_0_59_44_52_32 = 567;
            public static final int CLUEHELPER_TARGET_COORD_0_50_44_25_22 = 568;
            public static final int CLUEHELPER_TARGET_COORD_0_27_54_45_54 = 569;
            public static final int CLUEHELPER_TARGET_COORD_0_59_55_46_42 = 570;
            public static final int CLUEHELPER_TARGET_COORD_0_56_55_19_44 = 571;
            public static final int CLUEHELPER_TARGET_COORD_0_45_42_56_33 = 572;
            public static final int CLUEHELPER_TARGET_COORD_0_42_42_9_17 = 573;
            public static final int CLUEHELPER_TARGET_COORD_0_43_57_26_30 = 574;
            public static final int CLUEHELPER_TARGET_COORD_0_44_58_11_28 = 575;
            public static final int CLUEHELPER_TARGET_COORD_0_36_59_55_23 = 576;
            public static final int CLUEHELPER_TARGET_COORD_0_34_59_18_31 = 577;
            public static final int CLUEHELPER_TARGET_COORD_0_42_59_12_32 = 578;
            public static final int CLUEHELPER_TARGET_COORD_0_50_59_15_59 = 579;
            public static final int CLUEHELPER_TARGET_COORD_0_52_60_41_54 = 580;
            public static final int CLUEHELPER_TARGET_COORD_0_32_61_17_19 = 581;
            public static final int CLUEHELPER_TARGET_COORD_0_49_61_52_29 = 582;
            public static final int CLUEHELPER_TARGET_COORD_0_52_61_52_59 = 584;
            public static final int CLUEHELPER_TARGET_COORD_0_47_58_43_24 = 585;
            public static final int CLUEHELPER_TARGET_COORD_0_36_59_12_38 = 586;
            public static final int CLUEHELPER_TARGET_COORD_0_44_61_56_33 = 587;
            public static final int CLUEHELPER_TARGET_COORD_0_38_62_52_48 = 588;
            public static final int CLUEHELPER_TARGET_COORD_0_55_62_40_19 = 590;
            public static final int CLUEHELPER_TARGET_COORD_0_36_46_14_10 = 591;
            public static final int CLUEHELPER_TARGET_COORD_0_32_45_46_9 = 592;
            public static final int CLUEHELPER_TARGET_COORD_0_22_54_43_53 = 593;
            public static final int CLUEHELPER_TARGET_COORD_0_51_42_54_18 = 594;
            public static final int CLUEHELPER_TARGET_COORD_0_34_50_2_9 = 595;
            public static final int CLUEHELPER_TARGET_COORD_0_33_48_43_28 = 596;
            public static final int CLUEHELPER_TARGET_COORD_0_34_48_41_20 = 597;
            public static final int CLUEHELPER_TARGET_COORD_0_59_47_54_52 = 598;
            public static final int CLUEHELPER_TARGET_COORD_0_44_51_18_7 = 599;
            public static final int CLUEHELPER_TARGET_COORD_0_42_51_44_20 = 600;
            public static final int CLUEHELPER_TARGET_COORD_0_56_51_38_56 = 601;
            public static final int CLUEHELPER_TARGET_COORD_0_35_52_63_0 = 602;
            public static final int CLUEHELPER_TARGET_COORD_0_55_53_50_13 = 603;
            public static final int CLUEHELPER_TARGET_COORD_0_44_53_24_31 = 604;
            public static final int CLUEHELPER_TARGET_COORD_0_56_55_20_44 = 605;
            public static final int CLUEHELPER_TARGET_COORD_0_48_55_13_49 = 606;
            public static final int CLUEHELPER_TARGET_COORD_0_45_42_54_39 = 607;
            public static final int CLUEHELPER_TARGET_COORD_0_22_57_43_47 = 608;
            public static final int CLUEHELPER_TARGET_COORD_0_39_58_42_27 = 609;
            public static final int CLUEHELPER_TARGET_COORD_0_19_58_32_39 = 610;
            public static final int CLUEHELPER_TARGET_COORD_0_26_59_34_16 = 611;
            public static final int CLUEHELPER_TARGET_COORD_0_46_59_7_44 = 612;
            public static final int CLUEHELPER_TARGET_COORD_0_34_59_26_49 = 613;
            public static final int CLUEHELPER_TARGET_COORD_0_27_60_33_13 = 614;
            public static final int CLUEHELPER_TARGET_COORD_0_32_60_42_23 = 615;
            public static final int CLUEHELPER_TARGET_COORD_0_22_60_34_38 = 616;
            public static final int CLUEHELPER_TARGET_COORD_0_52_61_52_25 = 617;
            public static final int CLUEHELPER_TARGET_COORD_0_49_61_52_35 = 618;
            public static final int CLUEHELPER_TARGET_COORD_0_51_61_40_37 = 619;
            public static final int CLUEHELPER_TARGET_COORD_0_27_53_41_26 = 621;
            public static final int CLUEHELPER_TARGET_COORD_0_51_46_24_38 = 622;
            public static final int CLUEHELPER_TARGET_COORD_0_47_53_32_7 = 623;
            public static final int CLUEHELPER_TARGET_COORD_0_38_54_26_48 = 624;
            public static final int CLUEHELPER_TARGET_COORD_0_50_50_21_19 = 625;
            public static final int CLUEHELPER_TARGET_COORD_0_25_57_39_25 = 626;
            public static final int CLUEHELPER_TARGET_COORD_0_39_44_33_22 = 627;
            public static final int CLUEHELPER_TARGET_COORD_0_22_56_10_7 = 628;
            public static final int CLUEHELPER_TARGET_COORD_0_53_51_18_60 = 629;
            public static final int CLUEHELPER_TARGET_COORD_0_43_59_28_7 = 630;
            public static final int CLUEHELPER_TARGET_COORD_0_47_75_41_39 = 631;
            public static final int CLUEHELPER_TARGET_COORD_0_44_55_51_26 = 632;
            public static final int CLUEHELPER_TARGET_COORD_0_29_68_54_15 = 633;
            public static final int CLUEHELPER_TARGET_COORD_0_49_71_3_10 = 634;
            public static final int CLUEHELPER_TARGET_COORD_0_39_58_27_27 = 635;
            public static final int CLUEHELPER_TARGET_COORD_0_51_148_43_33 = 636;
            public static final int CLUEHELPER_TARGET_COORD_0_36_57_38_29 = 637;
            public static final int CLUEHELPER_TARGET_COORD_0_59_59_40_34 = 638;
            public static final int CLUEHELPER_TARGET_COORD_0_49_61_47_37 = 639;
            public static final int CLUEHELPER_TARGET_COORD_0_34_48_45_19 = 640;
            public static final int CLUEHELPER_TARGET_COORD_0_40_51_38_3 = 641;
            public static final int CLUEHELPER_TARGET_COORD_0_48_54_17_12 = 642;
            public static final int CLUEHELPER_TARGET_COORD_0_48_53_9_29 = 643;
            public static final int CLUEHELPER_TARGET_COORD_0_54_51_32_25 = 644;
            public static final int CLUEHELPER_TARGET_COORD_0_49_57_38_15 = 645;
            public static final int CLUEHELPER_TARGET_COORD_0_44_149_16_50 = 646;
            public static final int CLUEHELPER_TARGET_COORD_0_40_60_31_39 = 647;
            public static final int CLUEHELPER_TARGET_COORD_0_49_52_59_29 = 648;
            public static final int CLUEHELPER_TARGET_COORD_0_49_60_34_45 = 649;
            public static final int CLUEHELPER_TARGET_COORD_0_49_153_55_33 = 650;
            public static final int CLUEHELPER_TARGET_COORD_0_55_49_27_47 = 651;
            public static final int CLUEHELPER_TARGET_COORD_0_52_49_60_16 = 652;
            public static final int CLUEHELPER_TARGET_COORD_0_48_50_13_55 = 653;
            public static final int CLUEHELPER_TARGET_COORD_0_49_154_25_48 = 654;
            public static final int CLUEHELPER_TARGET_COORD_0_18_56_27_42 = 655;
            public static final int CLUEHELPER_TARGET_COORD_0_28_155_28_15 = 656;
            public static final int CLUEHELPER_TARGET_COORD_0_55_68_52_20 = 657;
            public static final int CLUEHELPER_TARGET_COORD_1_47_77_35_46 = 658;
            public static final int CLUEHELPER_TARGET_COORD_0_51_60_33_50 = 659;
            public static final int CLUEHELPER_TARGET_COORD_0_27_77_28_12 = 660;
            public static final int CLUEHELPER_TARGET_COORD_0_47_160_37_25 = 661;
            public static final int CLUEHELPER_TARGET_COORD_0_47_161_47_34 = 662;
            public static final int CLUEHELPER_TARGET_COORD_0_37_73_42_42 = 663;
            public static final int CLUEHELPER_TARGET_COORD_0_44_58_58_45 = 664;
            public static final int CLUEHELPER_TARGET_COORD_0_42_79_56_60 = 665;
            public static final int CLUEHELPER_TARGET_COORD_1_45_82_34_52 = 666;
            public static final int CLUEHELPER_TARGET_COORD_0_44_46_41_22 = 667;
            public static final int CLUEHELPER_TARGET_COORD_0_41_162_47_28 = 668;
            public static final int CLUEHELPER_TARGET_COORD_0_47_61_61_31 = 669;
            public static final int CLUEHELPER_TARGET_COORD_0_45_58_47_49 = 670;
            public static final int CLUEHELPER_TARGET_COORD_0_51_95_39_12 = 671;
            public static final int CLUEHELPER_TARGET_COORD_0_48_50_5_60 = 673;
            public static final int CLUEHELPER_TARGET_COORD_0_42_50_14_46 = 674;
            public static final int CLUEHELPER_TARGET_COORD_0_46_48_56_38 = 675;
            public static final int CLUEHELPER_TARGET_COORD_0_39_56_8_49 = 676;
            public static final int CLUEHELPER_TARGET_COORD_0_38_80_7_12 = 677;
            public static final int CLUEHELPER_TARGET_COORD_0_41_73_24_57 = 678;
            public static final int CLUEHELPER_TARGET_COORD_0_53_52_15_2 = 679;
            public static final int CLUEHELPER_TARGET_COORD_0_25_60_30_28 = 680;
            public static final int CLUEHELPER_TARGET_COORD_0_32_75_25_46 = 681;
            public static final int CLUEHELPER_TARGET_COORD_0_47_74_33_34 = 682;
            public static final int CLUEHELPER_TARGET_COORD_0_42_58_59_8 = 683;
            public static final int CLUEHELPER_TARGET_COORD_CLUEQUEST_1 = 2290;
            public static final int CLUEHELPER_TARGET_COORD_CLUEQUEST_2 = 2291;
            public static final int CLUEHELPER_TARGET_COORD_SOTE_1 = 2293;
            public static final int CLUEHELPER_TARGET_COORD_SOTE_2 = 2294;
            public static final int CLUEHELPER_TARGET_COORD_SOTE_3 = 2295;
            public static final int CLUEHELPER_TARGET_COORD_SOTE_4 = 2296;
            public static final int CLUEHELPER_TARGET_COORD_0_50_95_46_3 = 2416;
            public static final int CLUEHELPER_TARGET_COORD_VARLAMORE_OASIS = 3478;
            public static final int CLUEHELPER_TARGET_COORD_VARLAMORE_SUNSET = 3479;
            public static final int CLUEHELPER_TARGET_COORD_VARLAMORE_BAZAAR = 3480;
            public static final int CLUEHELPER_TARGET_COORD_0_25_47_46_4 = 3481;
            public static final int CLUEHELPER_TARGET_COORD_0_24_49_21_47 = 3482;
            public static final int CLUEHELPER_TARGET_COORD_0_25_48_59_39 = 3483;
            public static final int CLUEHELPER_TARGET_COORD_0_26_49_48_27 = 3484;
            public static final int CLUEHELPER_TARGET_COORD_0_26_48_35_15 = 3485;
            public static final int CLUEHELPER_TARGET_COORD_0_22_48_28_42 = 3486;
            public static final int CLUEHELPER_TARGET_COORD_0_50_57_41_24 = 3529;
            public static final int CLUEHELPER_TARGET_COORD_0_47_61_20_24 = 3530;
            public static final int CLUEHELPER_TARGET_COORD_0_23_50_18_57 = 3682;
            public static final int CLUEHELPER_TARGET_COORD_0_26_51_8_20 = 3683;
            public static final int CLUEHELPER_TARGET_COORD_0_25_50_26_41 = 3684;
            public static final int CLUEHELPER_TARGET_COORD_0_24_50_35_45 = 3685;
            public static final int CLUEHELPER_TARGET_COORD_VARLAMORE_ALDARIN_THEATRE = 3686;
            public static final int CLUEHELPER_TARGET_COORD_0_21_45_46_46 = 3742;
            public static final int CLUEHELPER_TARGET_COORD_0_26_50_30_47 = 3810;
            public static final int CLUEHELPER_TARGET_COORD_0_25_51_10_38 = 3811;
            public static final int CLUEHELPER_TARGET_COORD_1_25_83_40_54 = 3936;
            public static final int CLUEHELPER_TARGET_COORD_VARLAMORE_RAINFOREST_CENTRE = 5172;
            public static final int CLUEHELPER_TARGET_COORD_0_20_51_41_59 = 5173;
            public static final int CLUEHELPER_TARGET_COORD_0_58_60_52_60 = 6152;
            public static final int CLUEHELPER_TARGET_COORD_0_58_60_53_59 = 6153;
            public static final int CLUEHELPER_TARGET_COORD_0_49_38_47_21 = 7060;
            public static final int CLUEHELPER_TARGET_COORD_0_47_46_41_22 = 7061;
            public static final int CLUEHELPER_TARGET_COORD_0_36_43_20_20 = 7062;
            public static final int CLUEHELPER_TARGET_COORD_0_18_43_41_22 = 7063;
            public static final int CLUEHELPER_TARGET_COORD_0_32_49_33_48 = 7064;
            public static final int CLUEHELPER_TARGET_COORD_0_30_63_26_42 = 7065;
            public static final int CLUEHELPER_TARGET_COORD_0_32_40_21_48 = 7069;
            public static final int CLUEHELPER_TARGET_COORD_0_55_50_24_55 = 16413;
        }
    }

    public static final class CluehelperTargetKey {
        private CluehelperTargetKey() {}

        public static final int ID = 19;
        public static final int COL_LOC = 0;
        public static final int COL_LOC_COORD = 1;
        public static final int COL_NPCS = 2;
        public static final int COL_KEY = 3;
        public static final int COL_KEY_COORD = 4;
        public static final int COL_INV = 5;
        public static final int COL_COUNT = 6;
        public static final int COL_DESCRIPTION = 7;
        public static final int COL_TELEPORT_COORD = 8;
        public static final int COL_TELEPORT_RESTRICTION_QUEST = 9;
        public static final int COL_TELEPORT_RESTRICTION_SKILL = 10;
        public static final int COL_TELEPORT_RESTRICTION_CUSTOM = 11;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_TARGET_KEY_KING_BLACK_DRAGON_CHESTCLOSED = 1911;
            public static final int CLUEHELPER_TARGET_KEY_GUARD_DRAWERS = 1912;
            public static final int CLUEHELPER_TARGET_KEY_BARBARIAN_CHESTCLOSED = 1913;
            public static final int CLUEHELPER_TARGET_KEY_MAN_CHESTCLOSED = 1914;
            public static final int CLUEHELPER_TARGET_KEY_WIZARD_DRAWERS = 1915;
            public static final int CLUEHELPER_TARGET_KEY_GUARD_DOG_DRAWERS = 1916;
            public static final int CLUEHELPER_TARGET_KEY_MARKET_GUARD_DRAWERS = 1917;
            public static final int CLUEHELPER_TARGET_KEY_PENDA_DRAWERS = 1918;
            public static final int CLUEHELPER_TARGET_KEY_PIRATE_DRAWERS = 1919;
            public static final int CLUEHELPER_TARGET_KEY_CHICKEN_DRAWERS = 1920;
            public static final int CLUEHELPER_TARGET_KEY_HILL_GIANT_CRATE = 1921;
            public static final int CLUEHELPER_TARGET_KEY_MONK_CHESTCLOSED = 1922;
        }
    }

    public static final class CluehelperTargetKill {
        private CluehelperTargetKill() {}

        public static final int ID = 20;
        public static final int COL_NPCS = 0;
        public static final int COL_COORD = 1;
        public static final int COL_DESCRIPTION = 2;
        public static final int COL_TELEPORT_COORD = 3;
        public static final int COL_TELEPORT_RESTRICTION_QUEST = 4;
        public static final int COL_TELEPORT_RESTRICTION_SKILL = 5;
        public static final int COL_TELEPORT_RESTRICTION_CUSTOM = 6;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_TARGET_KILL_SPECTRE = 818;
            public static final int CLUEHELPER_TARGET_KILL_ANKOU = 819;
            public static final int CLUEHELPER_TARGET_KILL_WATERFIEND = 820;
            public static final int CLUEHELPER_TARGET_KILL_BASILISK = 821;
            public static final int CLUEHELPER_TARGET_KILL_BARBARIAN = 822;
            public static final int CLUEHELPER_TARGET_KILL_ROCK_CRAB = 823;
            public static final int CLUEHELPER_TARGET_KILL_BLOODVELD = 824;
            public static final int CLUEHELPER_TARGET_KILL_AVIANSIE = 825;
            public static final int CLUEHELPER_TARGET_KILL_GREEN_DRAGON = 826;
            public static final int CLUEHELPER_TARGET_KILL_CROCODILE = 827;
            public static final int CLUEHELPER_TARGET_KILL_HELLHOUND = 828;
            public static final int CLUEHELPER_TARGET_KILL_FROST_NAGUA = 3688;
            public static final int CLUEHELPER_TARGET_KILL_EARTH_NAGUA = 5181;
            public static final int CLUEHELPER_TARGET_KILL_GRYPHON_BOSS = 7070;
        }
    }

    public static final class CluehelperRequirementObj {
        private CluehelperRequirementObj() {}

        public static final int ID = 21;
        public static final int COL_DESCRIPTION = 0;
        public static final int COL_ITEM = 1;
        public static final int COL_INV = 2;
        public static final int COL_COUNT = 3;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_REQUIREMENT_OBJ_BULLROARER = 2200;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_SLED = 2201;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_RING_OF_VISIBILITY = 2202;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_ANY_CRYSTAL_BOW = 2203;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_ANY_INFERNAL_AXE = 2204;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_MARK_OF_GRACE = 2205;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_LAVA_DRAGON_BONES = 2206;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_ARMADYL_HELMET = 2207;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_WARRIOR_GUILD_TOKEN = 2208;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_GREENMANS_ALE = 2209;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_BARRELCHEST_ANCHOR = 2210;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_BASALT = 2211;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_BARROWS_GLOVES = 2212;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_COOKING_GAUNTLETS = 2213;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_NUMULITE = 2214;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_RUNE_PLATEBODY = 2215;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_DIVING_APPARATUS = 2216;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_DIVING_HELMET = 2217;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_GHOSTSPEAK_AMULET = 2218;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_ECTOTOKEN_25 = 2219;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_PET_ROCK = 2220;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_RUNE_THROWNAXE = 2221;
            public static final int CLUEHELPER_REQUIREMENT_OBJ_SAILING_BOAT_KEEL_PART_DRAGON = 7057;
        }
    }

    public static final class CluehelperRequirementObjParamTrailItem {
        private CluehelperRequirementObjParamTrailItem() {}

        public static final int ID = 22;
        public static final int COL_DESCRIPTION = 0;
        public static final int COL_ITEM_GROUP = 1;
    }

    public static final class CluehelperRequirementQuest {
        private CluehelperRequirementQuest() {}

        public static final int ID = 23;
        public static final int COL_DESCRIPTION = 0;
        public static final int COL_QUEST = 1;
        public static final int COL_VARSTATE = 2;
    }

    public static final class CluehelperRequirementStat {
        private CluehelperRequirementStat() {}

        public static final int ID = 24;
        public static final int COL_STAT = 0;
        public static final int COL_LEVEL = 1;
    }

    public static final class CluehelperChallengeQuestion {
        private CluehelperChallengeQuestion() {}

        public static final int ID = 25;
        public static final int COL_QUESTION = 0;
    }

    public static final class CluehelperChallengeBox {
        private CluehelperChallengeBox() {}

        public static final int ID = 26;
        public static final int COL_DESCRIPTION = 0;
    }

    public static final class CluehelperCombatEncounter {
        private CluehelperCombatEncounter() {}

        public static final int ID = 27;
        public static final int COL_DESCRIPTION = 0;
        public static final int COL_NPCS = 1;
    }

    public static final class CluehelperOutfit {
        private CluehelperOutfit() {}

        public static final int ID = 28;
        public static final int COL_DESCRIPTION = 0;
        public static final int COL_WEARPOS_HAT = 1;
        public static final int COL_WEARPOS_BACK = 2;
        public static final int COL_WEARPOS_FRONT = 3;
        public static final int COL_WEARPOS_RHAND = 4;
        public static final int COL_WEARPOS_TORSO = 5;
        public static final int COL_WEARPOS_LHAND = 6;
        public static final int COL_WEARPOS_LEGS = 7;
        public static final int COL_WEARPOS_HANDS = 8;
        public static final int COL_WEARPOS_FEET = 9;
        public static final int COL_WEARPOS_RING = 10;
        public static final int COL_WEARPOS_QUIVER = 11;
        public static final int COL_WEARPOS_PARAM_HAT = 12;
        public static final int COL_WEARPOS_PARAM_BACK = 13;
        public static final int COL_WEARPOS_PARAM_FRONT = 14;
        public static final int COL_WEARPOS_PARAM_RHAND = 15;
        public static final int COL_WEARPOS_PARAM_TORSO = 16;
        public static final int COL_WEARPOS_PARAM_LHAND = 17;
        public static final int COL_WEARPOS_PARAM_LEGS = 18;
        public static final int COL_WEARPOS_PARAM_HANDS = 19;
        public static final int COL_WEARPOS_PARAM_FEET = 20;
        public static final int COL_WEARPOS_PARAM_RING = 21;
        public static final int COL_WEARPOS_PARAM_QUIVER = 22;
        public static final int COL_WEARPOS_PARAM_ANY = 23;

        public static final class Row {
            private Row() {}

            public static final int CLUEHELPER_OUTFIT_EMOTE_BEGINNER_0 = 1599;
            public static final int CLUEHELPER_OUTFIT_EMOTE_BEGINNER_2 = 1600;
            public static final int CLUEHELPER_OUTFIT_EMOTE_BEGINNER_3 = 1601;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_0 = 1602;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_1 = 1603;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_2 = 1604;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_3 = 1605;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_4 = 1606;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_5 = 1607;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_6 = 1608;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_7 = 1609;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_8 = 1610;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_9 = 1611;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_10 = 1612;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_11 = 1613;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_12 = 1614;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_13 = 1615;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_14 = 1616;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_15 = 1617;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_16 = 1618;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_17 = 1619;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_18 = 1620;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_19 = 1621;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_20 = 1622;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_21 = 1623;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_22 = 1624;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_23 = 1625;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_24 = 1626;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_25 = 1627;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_26 = 1628;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_27 = 1629;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_28 = 1630;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_29 = 1631;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_30 = 1632;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_0 = 1633;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_1 = 1634;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_2 = 1635;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_3 = 1636;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_4 = 1637;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_5 = 1638;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_6 = 1639;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_7 = 1640;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_8 = 1641;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_9 = 1642;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_10 = 1643;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_11 = 1644;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_12 = 1645;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_13 = 1646;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_14 = 1647;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_15 = 1648;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_16 = 1649;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_17 = 1650;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_18 = 1651;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_19 = 1652;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_20 = 1653;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_21 = 1654;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_22 = 1655;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_0 = 1656;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_1 = 1657;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_2 = 1658;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_3 = 1659;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_4 = 1660;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_5 = 1661;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_6 = 1662;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_7 = 1663;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_8 = 1664;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_9 = 1665;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_10 = 1666;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_11 = 1667;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_12 = 1668;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_13 = 1669;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_14 = 1670;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_15 = 1671;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_0 = 1672;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_1 = 1673;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_2 = 1674;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_3 = 1675;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_4 = 1676;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_5 = 1677;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_6 = 1678;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_7 = 1679;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_8 = 1680;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_9 = 1681;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_10 = 1682;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_11 = 1683;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_12 = 1684;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_13 = 1685;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_14 = 1686;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_15 = 1687;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_16 = 1688;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_0 = 1689;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_1 = 1690;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_2 = 1691;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_3 = 1692;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_4 = 1693;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_5 = 1694;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_6 = 1695;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_7 = 1696;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_8 = 1697;
            public static final int CLUEHELPER_OUTFIT_OUTFIT_BARROWS_AHRIM = 1698;
            public static final int CLUEHELPER_OUTFIT_OUTFIT_BARROWS_DHAROK = 1699;
            public static final int CLUEHELPER_OUTFIT_OUTFIT_BARROWS_GUTHAN = 1700;
            public static final int CLUEHELPER_OUTFIT_OUTFIT_BARROWS_KARIL = 1701;
            public static final int CLUEHELPER_OUTFIT_OUTFIT_BARROWS_TORAG = 1702;
            public static final int CLUEHELPER_OUTFIT_OUTFIT_BARROWS_VERAC = 1703;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_10 = 1704;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_11 = 1705;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_12 = 1706;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_13 = 1707;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_14 = 1708;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_15 = 1709;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_16 = 1710;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_17 = 1711;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_18 = 1712;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_19 = 1713;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_20 = 1714;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_21 = 1715;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_22A = 1716;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_22B = 1717;
            public static final int CLUEHELPER_OUTFIT_CRYPTIC_MASTER_VIGGORA = 1718;
            public static final int CLUEHELPER_OUTFIT_CRYPTIC_MASTER_JUNA = 1719;
            public static final int CLUEHELPER_OUTFIT_CRYPTIC_MASTER_SIR_VYVIN_ARMOUR = 1720;
            public static final int CLUEHELPER_OUTFIT_CRYPTIC_MASTER_MAGE_OF_ZAMORAK = 1721;
            public static final int CLUEHELPER_OUTFIT_EMOTE_SOTE_4 = 2300;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_VM01 = 3499;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_VM01 = 3500;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_VM01 = 3501;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_23 = 3676;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MEDIUM_24 = 3677;
            public static final int CLUEHELPER_OUTFIT_EMOTE_HARD_VM1 = 3678;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_VM02 = 3679;
            public static final int CLUEHELPER_OUTFIT_EMOTE_EASY_SAIL = 7054;
            public static final int CLUEHELPER_OUTFIT_EMOTE_ELITE_SAIL = 7055;
            public static final int CLUEHELPER_OUTFIT_EMOTE_MASTER_SAIL = 7056;
        }
    }

    public static final class DbgDummyTable {
        private DbgDummyTable() {}

        public static final int ID = 29;
        public static final int COL_ID = 0;

        public static final class Row {
            private Row() {}

            public static final int DBG_DUMMY_ROW1 = 672;
            public static final int DBG_DUMMY_ROW0 = 2199;
        }
    }

    public static final class FswInfoFreshTable {
        private FswInfoFreshTable() {}

        public static final int ID = 30;
        public static final int COL_INFO = 0;
    }

    public static final class FswInfoNormalTable {
        private FswInfoNormalTable() {}

        public static final int ID = 31;
        public static final int COL_INFO = 0;
    }

    public static final class FswPointsInfoTable {
        private FswPointsInfoTable() {}

        public static final int ID = 32;
        public static final int COL_INFO = 0;
    }

    public static final class FswPointsBossInfoTable {
        private FswPointsBossInfoTable() {}

        public static final int ID = 33;
        public static final int COL_INFO = 0;
    }

    public static final class ItemTransmog {
        private ItemTransmog() {}

        public static final int ID = 34;
        public static final int COL_GROUP = 0;
        public static final int COL_GROUP_ID = 1;
        public static final int COL_NAMEDOBJ = 2;
        public static final int COL_UI_NAME = 3;
        public static final int COL_UI_EXAMINE = 4;
        public static final int COL_UI_OP_NAME = 5;
        public static final int COL_SHOW_WHEN_UNAVAILABLE = 6;

        public static final class Row {
            private Row() {}

            public static final int PRIDE22_FLOWER_CROWN_PRIDE = 174;
            public static final int PRIDE22_FLOWER_CROWN_BISEXUAL = 175;
            public static final int PRIDE22_FLOWER_CROWN_ASEXUAL = 176;
            public static final int PRIDE22_FLOWER_CROWN_TRANSGENDER = 177;
            public static final int PRIDE22_FLOWER_CROWN_PANSEXUAL = 178;
            public static final int PRIDE22_FLOWER_CROWN_NONBINARY = 179;
            public static final int PRIDE17_SCARF = 922;
            public static final int PRIDE22_FLOWER_CROWN_GENDERQUEER = 2317;
            public static final int PRIDE22_FLOWER_CROWN_LESBIAN = 2318;
            public static final int PRIDE22_FLOWER_CROWN_GAY = 2319;
            public static final int HW22_TREAT_CAULDRON_0 = 2320;
            public static final int HW22_TREAT_CAULDRON_1 = 2321;
            public static final int HW22_TREAT_CAULDRON_2 = 2322;
            public static final int HW22_TREAT_CAULDRON_3 = 2323;
            public static final int HW22_TREAT_CAULDRON_4 = 2324;
            public static final int HW22_WIG_01 = 2325;
            public static final int HW22_WIG_02 = 2326;
            public static final int HW22_WIG_03 = 2327;
            public static final int HW22_WIG_04 = 2328;
            public static final int HW22_WIG_05 = 2329;
            public static final int HW22_WIG_06 = 2330;
            public static final int PRIDE23_SCARF_BISEXUAL = 2489;
            public static final int PRIDE23_SCARF_ASEXUAL = 2490;
            public static final int PRIDE23_SCARF_TRANSGENDER = 2491;
            public static final int PRIDE23_SCARF_PANSEXUAL = 2492;
            public static final int PRIDE23_SCARF_NONBINARY = 2493;
            public static final int PRIDE23_SCARF_GENDERQUEER = 2494;
            public static final int PRIDE23_SCARF_LESBIAN = 2495;
            public static final int PRIDE23_SCARF_GAY = 2496;
            public static final int PRIDE23_JUMPER_PRIDE = 2497;
            public static final int PRIDE23_JUMPER_BISEXUAL = 2498;
            public static final int PRIDE23_JUMPER_ASEXUAL = 2499;
            public static final int PRIDE23_JUMPER_TRANSGENDER = 2500;
            public static final int PRIDE23_JUMPER_PANSEXUAL = 2501;
            public static final int PRIDE23_JUMPER_NONBINARY = 2502;
            public static final int PRIDE23_JUMPER_GENDERQUEER = 2503;
            public static final int PRIDE23_JUMPER_LESBIAN = 2504;
            public static final int PRIDE23_JUMPER_GAY = 2505;
            public static final int HW23_SPIDERHAT = 3426;
            public static final int HW23_SPIDERHAT_02 = 3427;
            public static final int HW23_SPIDERHAT_03 = 3428;
            public static final int HW23_SPIDERHAT_04 = 3429;
            public static final int HW23_SPIDERHAT_05 = 3430;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_EW = 3477;
            public static final int PRIDE24_CAPE_PRIDE = 3576;
            public static final int PRIDE24_CAPE_BISEXUAL = 3577;
            public static final int PRIDE24_CAPE_ASEXUAL = 3578;
            public static final int PRIDE24_CAPE_TRANSGENDER = 3579;
            public static final int PRIDE24_CAPE_PANSEXUAL = 3580;
            public static final int PRIDE24_CAPE_NONBINARY = 3581;
            public static final int PRIDE24_CAPE_GENDERQUEER = 3582;
            public static final int PRIDE24_CAPE_LESBIAN = 3583;
            public static final int PRIDE24_CAPE_GAY = 3584;
            public static final int PRIDE24_SHIRT_PRIDE = 3585;
            public static final int PRIDE24_SHIRT_BISEXUAL = 3586;
            public static final int PRIDE24_SHIRT_ASEXUAL = 3587;
            public static final int PRIDE24_SHIRT_TRANSGENDER = 3588;
            public static final int PRIDE24_SHIRT_PANSEXUAL = 3589;
            public static final int PRIDE24_SHIRT_NONBINARY = 3590;
            public static final int PRIDE24_SHIRT_GENDERQUEER = 3591;
            public static final int PRIDE24_SHIRT_LESBIAN = 3592;
            public static final int PRIDE24_SHIRT_GAY = 3593;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_EW = 3680;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_EW = 3681;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_EW = 3687;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_EW = 3694;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_EW = 3814;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_EW = 3815;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_EW = 3816;
            public static final int HW24_PUMPKIN_BYCOL_RED_EW = 3817;
            public static final int HW24_PUMPKIN_BYFACE_RED_EW = 3818;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_EW = 3819;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_EW = 3820;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_EW = 3821;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_EW = 3822;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_DERP = 3823;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_DERP = 3824;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_DERP = 3825;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_DERP = 3826;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_DERP = 3827;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_DERP = 3828;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_DERP = 3829;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_DERP = 3830;
            public static final int HW24_PUMPKIN_BYCOL_RED_DERP = 3831;
            public static final int HW24_PUMPKIN_BYFACE_RED_DERP = 3832;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_DERP = 3833;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_DERP = 3834;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_DERP = 3835;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_DERP = 3836;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_EVIL = 3837;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_EVIL = 3838;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_EVIL = 3839;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_EVIL = 3840;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_EVIL = 3841;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_EVIL = 3842;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_EVIL = 3843;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_EVIL = 3844;
            public static final int HW24_PUMPKIN_BYCOL_RED_EVIL = 3845;
            public static final int HW24_PUMPKIN_BYFACE_RED_EVIL = 3846;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_EVIL = 3847;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_EVIL = 3848;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_EVIL = 3849;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_EVIL = 3850;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_GRR = 3851;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_GRR = 3852;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_GRR = 3853;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_GRR = 3854;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_GRR = 3855;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_GRR = 3856;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_GRR = 3857;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_GRR = 3858;
            public static final int HW24_PUMPKIN_BYCOL_RED_GRR = 3859;
            public static final int HW24_PUMPKIN_BYFACE_RED_GRR = 3860;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_GRR = 3861;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_GRR = 3862;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_GRR = 3863;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_GRR = 3864;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_MEH = 3865;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_MEH = 3866;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_MEH = 3867;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_MEH = 3868;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_MEH = 3869;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_MEH = 3870;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_MEH = 3871;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_MEH = 3872;
            public static final int HW24_PUMPKIN_BYCOL_RED_MEH = 3873;
            public static final int HW24_PUMPKIN_BYFACE_RED_MEH = 3874;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_MEH = 3875;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_MEH = 3876;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_MEH = 3877;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_MEH = 3878;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_OH = 3879;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_OH = 3880;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_OH = 3881;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_OH = 3882;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_OH = 3883;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_OH = 3884;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_OH = 3885;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_OH = 3886;
            public static final int HW24_PUMPKIN_BYCOL_RED_OH = 3887;
            public static final int HW24_PUMPKIN_BYFACE_RED_OH = 3888;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_OH = 3889;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_OH = 3890;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_OH = 3891;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_OH = 3892;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_SAD = 3893;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_SAD = 3894;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_SAD = 3895;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_SAD = 3896;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_SAD = 3897;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_SAD = 3898;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_SAD = 3899;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_SAD = 3900;
            public static final int HW24_PUMPKIN_BYCOL_RED_SAD = 3901;
            public static final int HW24_PUMPKIN_BYFACE_RED_SAD = 3902;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_SAD = 3903;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_SAD = 3904;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_SAD = 3905;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_SAD = 3906;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_WOO = 3907;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_WOO = 3908;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_WOO = 3909;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_WOO = 3910;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_WOO = 3911;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_WOO = 3912;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_WOO = 3913;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_WOO = 3914;
            public static final int HW24_PUMPKIN_BYCOL_RED_WOO = 3915;
            public static final int HW24_PUMPKIN_BYFACE_RED_WOO = 3916;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_WOO = 3917;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_WOO = 3918;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_WOO = 3919;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_WOO = 3920;
            public static final int HW24_PUMPKIN_BYCOL_BEIGE_YAY = 3921;
            public static final int HW24_PUMPKIN_BYFACE_BEIGE_YAY = 3922;
            public static final int HW24_PUMPKIN_BYCOL_WHITE_YAY = 3923;
            public static final int HW24_PUMPKIN_BYFACE_WHITE_YAY = 3924;
            public static final int HW24_PUMPKIN_BYCOL_YELLOW_YAY = 3925;
            public static final int HW24_PUMPKIN_BYFACE_YELLOW_YAY = 3926;
            public static final int HW24_PUMPKIN_BYCOL_ORANGE_YAY = 3927;
            public static final int HW24_PUMPKIN_BYFACE_ORANGE_YAY = 3928;
            public static final int HW24_PUMPKIN_BYCOL_RED_YAY = 3929;
            public static final int HW24_PUMPKIN_BYFACE_RED_YAY = 3930;
            public static final int HW24_PUMPKIN_BYCOL_DARKGREEN_YAY = 3931;
            public static final int HW24_PUMPKIN_BYFACE_DARKGREEN_YAY = 3932;
            public static final int HW24_PUMPKIN_BYCOL_POWDERGREY_YAY = 3933;
            public static final int HW24_PUMPKIN_BYFACE_POWDERGREY_YAY = 3934;
            public static final int XMAS24_PRESENT_HAT_FESTIVE = 4229;
            public static final int XMAS24_PRESENT_HAT_SIMPLE = 4230;
            public static final int XMAS24_PRESENT_HAT_ICY = 4231;
            public static final int XMAS24_PRESENT_HAT_ANTISANTA = 4232;
            public static final int GREENMAN_MASK_TRANSMOG = 5390;
            public static final int GREENMAN_MASK_REGULAR_TRANSMOG = 5391;
            public static final int GREENMAN_MASK_OAK_TRANSMOG = 5392;
            public static final int GREENMAN_MASK_WILLOW_TRANSMOG = 5393;
            public static final int GREENMAN_MASK_MAPLE_TRANSMOG = 5394;
            public static final int GREENMAN_MASK_YEW_TRANSMOG = 5395;
            public static final int GREENMAN_MASK_MAGIC_TRANSMOG = 5396;
            public static final int EASTER26_EGG_COMPANION_ARCHIBALD = 13312;
            public static final int EASTER26_EGG_COMPANION_GRID = 13313;
            public static final int EASTER26_EGG_COMPANION_BUNNY = 13314;
            public static final int EASTER26_EGG_COMPANION_DIAMONDS = 13315;
            public static final int EASTER26_EGG_COMPANION_CHICK = 13316;
            public static final int EASTER26_EGG_COMPANION_MELTED = 13317;
            public static final int EASTER26_EGG_COMPANION_DRAGON = 13318;
        }
    }

    public static final class CombinationLockDataset {
        private CombinationLockDataset() {}

        public static final int ID = 35;
        public static final int COL_MIN_LENGTH = 0;
        public static final int COL_MAX_LENGTH = 1;
        public static final int COL_VALUE_TYPE = 2;
        public static final int COL_VALUES = 3;
        public static final int COL_RANDOMISE_START = 4;

        public static final class Row {
            private Row() {}

            public static final int COMBINATION_LOCK_DATASET_NUMERIC = 2339;
            public static final int COMBINATION_LOCK_DATASET_NUMERIC_4 = 3413;
        }
    }

    public static final class CombinationLockValues {
        private CombinationLockValues() {}

        public static final int ID = 36;
        public static final int COL_STRING_VALUE = 0;
        public static final int COL_GRAPHIC_VALUE = 1;

        public static final class Row {
            private Row() {}

            public static final int COMBINATION_LOCK_VALUES_NUMERIC = 2332;
            public static final int COMBINATION_LOCK_VALUES_ALPHABETIC_1 = 2333;
            public static final int COMBINATION_LOCK_VALUES_ALPHABETIC_2 = 2334;
            public static final int COMBINATION_LOCK_VALUES_ALPHABETIC_3 = 2335;
            public static final int COMBINATION_LOCK_VALUES_ALPHABETIC_4 = 2336;
            public static final int COMBINATION_LOCK_VALUES_ALPHABETIC_5 = 2337;
            public static final int COMBINATION_LOCK_VALUES_NUMERIC_4 = 3377;
            public static final int COMBINATION_LOCK_VALUES_SYMBOLIC = 3378;
            public static final int VMQ3_TOWER_LOCK_VALUES_ALPHABETIC_1 = 3698;
            public static final int VMQ3_TOWER_LOCK_VALUES_ALPHABETIC_2 = 3699;
            public static final int VMQ3_TOWER_LOCK_VALUES_ALPHABETIC_3 = 3700;
            public static final int VMQ3_TOWER_LOCK_VALUES_ALPHABETIC_4 = 3701;
            public static final int COMBINATION_LOCK_VALUES_VMQ4_JANUS_LOCK_1 = 5196;
            public static final int COMBINATION_LOCK_VALUES_VMQ4_JANUS_LOCK_2 = 5197;
            public static final int COMBINATION_LOCK_VALUES_VMQ4_JANUS_LOCK_3 = 5198;
        }
    }

    public static final class HairStyles {
        private HairStyles() {}

        public static final int ID = 37;
        public static final int COL_NAME = 0;
        public static final int COL_PLAYER_KIT_ID_TYPE_A = 1;
        public static final int COL_PLAYER_KIT_ID_TYPE_B = 2;
        public static final int COL_HEAD_MODEL = 3;
        public static final int COL_UPDO_VARIANT_TYPE_A = 4;
        public static final int COL_UPDO_VARIANT_TYPE_B = 5;

        public static final class Row {
            private Row() {}

            public static final int DREADLOCKS = 2360;
            public static final int TONSURE = 2363;
            public static final int MOHAWK = 2368;
            public static final int QUIFF = 2370;
            public static final int SAMURAI = 2371;
            public static final int PRINCELY = 2372;
            public static final int FRONTSPLIT = 2375;
            public static final int TOUSLED = 2376;
            public static final int SIDEWEDGE = 2377;
            public static final int FRONTWEDGE = 2378;
            public static final int FRONTSPIKES = 2379;
            public static final int FROHAWK = 2380;
            public static final int REARSKIRT = 2381;
            public static final int WARRIORMONK = 2382;
            public static final int STRAIGHTBRAIDS = 2394;
            public static final int TWOBACK = 2395;
            public static final int MULLET = 2396;
            public static final int UNDERCUT = 2397;
            public static final int SHORTLOCS = 2402;
            public static final int CURTAINQUIFF = 2405;
            public static final int CHOPPY = 2406;
            public static final int PUNK = 2408;
            public static final int SHAVED = 2409;
            public static final int VIKING = 2410;
            public static final int ELVEN = 2411;
            public static final int MIDCOILS = 2412;
            public static final int PLAITS = 2414;
            public static final int BALD = 3612;
            public static final int LONG_ = 3613;
            public static final int MEDIUM = 3614;
            public static final int SHORT_ = 3615;
            public static final int CROPPED = 3616;
            public static final int WILDSPIKES = 3619;
            public static final int SPIKES = 3620;
            public static final int WINDBRAIDS = 3621;
            public static final int CURTAINS = 3622;
            public static final int EARMUFFS = 3625;
            public static final int CURLS = 3627;
            public static final int PONYTAIL = 3628;
            public static final int BRAIDS = 3631;
            public static final int BOB = 3633;
            public static final int LAYERED = 3634;
            public static final int STRAIGHT = 3635;
        }
    }

    public static final class FacialHairStyles {
        private FacialHairStyles() {}

        public static final int ID = 38;
        public static final int COL_NAME = 0;
        public static final int COL_PLAYER_KIT_ID_TYPE_A = 1;
        public static final int COL_PLAYER_KIT_ID_TYPE_B = 2;
        public static final int COL_CHAT_HEAD = 3;
    }

    public static final class OmnishopShopData {
        private OmnishopShopData() {}

        public static final int ID = 39;
        public static final int COL_OMNISHOP_SHOP_NAME = 0;
        public static final int COL_OMNISHOP_SHOP_INV = 1;
        public static final int COL_OMNISHOP_SHOP_CURRENCY = 2;
        public static final int COL_OMNISHOP_SHOP_FILTER_TITLES = 3;
        public static final int COL_OMNISHOP_SHOP_PURSE = 4;
        public static final int COL_OMNISHOP_SHOP_STOCK = 5;
        public static final int COL_OMNISHOP_SHOP_COST_MOD_BUY = 6;
        public static final int COL_OMNISHOP_SHOP_COST_MOD_SELL = 7;
        public static final int COL_OMNISHOP_SHOP_COST_MOD_HAGGLE = 8;
        public static final int COL_OMNISHOP_SHOP_COST_CURRENCY = 9;
        public static final int COL_OMNISHOP_SHOP_INFO_TITLE = 10;
        public static final int COL_OMNISHOP_SHOP_INFO_INTRO_DESC = 11;
        public static final int COL_OMNISHOP_SHOP_INFO_INSTRUCTIONS = 12;
        public static final int COL_OMNISHOP_SHOP_MAIN_OP_TEXT = 13;
        public static final int COL_OMNISHOP_SHOP_SIDE_OP_TEXT = 14;
        public static final int COL_OMNISHOP_SHOP_COST_HIDE = 15;
        public static final int COL_OMNISHOP_SHOP_ALLOW_SELLING = 16;
        public static final int COL_OMNISHOP_SHOP_SHOW_STOCK = 17;
        public static final int COL_OMNISHOP_SHOP_SHOW_COST_IN_INFO = 18;

        public static final class Row {
            private Row() {}

            public static final int AGILITYARENA_REWARDS_NEWSHOP = 583;
            public static final int BH_SHOP_DATA = 589;
            public static final int DEADMAN_SHOP_DATA = 929;
            public static final int OMNISHOP_TEST_SHOP_1 = 2468;
            public static final int OMNISHOP_TEST_SHOP_2 = 2478;
            public static final int OMNISHOP_TEST_SHOP_3 = 2479;
            public static final int FORESTRY_SHOP = 3284;
            public static final int FORESTRY_SHOP_UIM = 3355;
            public static final int BREW_SHOP_DATA = 3544;
            public static final int MIXOLOGY_REWARDS = 3746;
            public static final int WYRM_AGILITY_REWARDS_SHOP = 3764;
            public static final int TITHE_SHOP_DATA = 4981;
            public static final int CW_SHOP_DATA = 5063;
            public static final int ENT_TOTEMS_SHOP = 5458;
            public static final int ENT_TOTEMS_SHOP_IRONMAN = 5459;
            public static final int SAILING_BOAT_SHOP = 8548;
            public static final int DEADMAN_SKULL_SHOP = 9575;
            public static final int VAMPYRIUM_CONSUMABLE_SHOP = 16873;
            public static final int VAMPYRIUM_CONSUMABLE_SHOP_IVAN = 16874;
        }
    }

    public static final class OmnishopStockData {
        private OmnishopStockData() {}

        public static final int ID = 40;
        public static final int COL_OMNISHOP_STOCK_OBJ = 0;
        public static final int COL_OMNISHOP_STOCK_ALTERNATEOBJ = 1;
        public static final int COL_OMNISHOP_STOCK_DISPLAYOBJ = 2;
        public static final int COL_OMNISHOP_STOCK_TOGGLE_VAR = 3;
        public static final int COL_OMNISHOP_STOCK_TOGGLE_VAR_OWNED_STATE = 4;
        public static final int COL_OMNISHOP_STOCK_RECLAIMABLE = 5;
        public static final int COL_OMNISHOP_STOCK_FILTER_ID = 6;
        public static final int COL_OMNISHOP_STOCK_COST = 7;
        public static final int COL_OMNISHOP_STOCK_COST_MOD_BUY = 8;
        public static final int COL_OMNISHOP_STOCK_COST_MOD_SELL = 9;
        public static final int COL_OMNISHOP_STOCK_COST_MOD_HAGGLE = 10;
        public static final int COL_OMNISHOP_STOCK_TRANSACTION_MULTIPLIER = 11;
        public static final int COL_OMNISHOP_STOCK_RESTRICTED = 12;
        public static final int COL_OMNISHOP_STOCK_RESTRICTED_CATEGORY = 13;
        public static final int COL_OMNISHOP_STOCK_RESTRICTED_UIM = 14;
        public static final int COL_OMNISHOP_STOCK_MAX_PURCHASE = 15;
        public static final int COL_OMNISHOP_STOCK_HIDE_COUNT = 16;
        public static final int COL_OMNISHOP_STOCK_SHOW_UNLIMITED = 17;
        public static final int COL_OMNISHOP_STOCK_BUYABLE = 18;
        public static final int COL_OMNISHOP_STOCK_SELLABLE = 19;
        public static final int COL_OMNISHOP_STOCK_SOLD_NOTED = 20;
        public static final int COL_OMNISHOP_STOCK_VIEW_ONLY = 21;
        public static final int COL_OMNISHOP_STOCK_NAME_OVERRIDE = 22;
        public static final int COL_OMNISHOP_STOCK_DESCRIPTION_DYNAMIC = 23;
        public static final int COL_OMNISHOP_STOCK_USE_SHORTNAME = 24;
        public static final int COL_OMNISHOP_STOCK_SHORTNAME = 25;

        public static final class Row {
            private Row() {}

            public static final int DEADMAN_SHOP_2024_WEAPON_ORNAMENT_SCROLL = 1355;
            public static final int BH_SHOP_DATA_VESTAS_CHAINBODY = 2418;
            public static final int BH_SHOP_DATA_VESTAS_PLATESKIRT = 2419;
            public static final int BH_SHOP_DATA_STATIUS_FULL_HELM = 2420;
            public static final int BH_SHOP_DATA_STATIUS_PLATEBODY = 2421;
            public static final int BH_SHOP_DATA_STATIUS_PLATELEGS = 2422;
            public static final int BH_SHOP_DATA_MORRIGANS_COIF = 2423;
            public static final int BH_SHOP_DATA_MORRIGANS_LEATHER_BODY = 2424;
            public static final int BH_SHOP_DATA_MORRIGANS_LEATHER_CHAPS = 2425;
            public static final int BH_SHOP_DATA_ZURIELS_HOOD = 2426;
            public static final int BH_SHOP_DATA_ZURIELS_ROBE_TOP = 2427;
            public static final int BH_SHOP_DATA_ZURIELS_ROBE_BOTTOM = 2428;
            public static final int BH_SHOP_DATA_VESTAS_SPEAR = 2429;
            public static final int BH_SHOP_DATA_VESTAS_LONGSWORD = 2430;
            public static final int BH_SHOP_DATA_STATIUS_WARHAMMER = 2431;
            public static final int BH_SHOP_DATA_MORRIGANS_THROWNAXE = 2432;
            public static final int BH_SHOP_DATA_MORRIGANS_JAVELIN = 2433;
            public static final int BH_SHOP_DATA_ZURIELS_STAFF = 2434;
            public static final int BH_SHOP_DATA_DARKBOW_IMBUE_SCROLL = 2435;
            public static final int BH_SHOP_DATA_BRAIN_ANCHOR_IMBUE_SCROLL = 2436;
            public static final int BH_SHOP_DATA_DRAGON_MACE_IMBUE_SCROLL = 2437;
            public static final int BH_SHOP_DATA_DRAGON_LONGSWORD_IMBUE_SCROLL = 2438;
            public static final int BH_SHOP_DATA_ABYSSAL_DAGGER_IMBUE_SCROLL = 2439;
            public static final int BH_SHOP_DATA_ORNAMENT_KIT = 2440;
            public static final int BH_SHOP_DATA_ELDER_MAUL_KIT = 2441;
            public static final int BH_SHOP_DATA_HEAVY_BALLISTA_KIT = 2442;
            public static final int BH_SHOP_DATA_ELDERCHAOS_ROBES_KIT = 2443;
            public static final int BH_SHOP_DATA_TARGET_TELEPORT_SCROLL = 2444;
            public static final int BH_SHOP_DATA_BLIGHTED_KARAMBWAN = 2445;
            public static final int BH_SHOP_DATA_BLIGHTED_MANTARAY = 2446;
            public static final int BH_SHOP_DATA_BLIGHTED_ANGLERFISH = 2447;
            public static final int BH_SHOP_DATA_BLIGHTED_SUPERRESTORE = 2448;
            public static final int BH_SHOP_DATA_EMBLEM_1 = 2449;
            public static final int BH_SHOP_DATA_EMBLEM_2 = 2450;
            public static final int BH_SHOP_DATA_EMBLEM_3 = 2451;
            public static final int BH_SHOP_DATA_EMBLEM_4 = 2452;
            public static final int BH_SHOP_DATA_EMBLEM_5 = 2453;
            public static final int BH_SHOP_DATA_EMBLEM_6 = 2454;
            public static final int BH_SHOP_DATA_EMBLEM_7 = 2455;
            public static final int BH_SHOP_DATA_EMBLEM_8 = 2456;
            public static final int BH_SHOP_DATA_EMBLEM_9 = 2457;
            public static final int BH_SHOP_DATA_EMBLEM_10 = 2458;
            public static final int BH_SHOP_DATA_XP_ATTACK = 2459;
            public static final int BH_SHOP_DATA_XP_STRENGTH = 2460;
            public static final int BH_SHOP_DATA_XP_DEFENCE = 2461;
            public static final int BH_SHOP_DATA_XP_HITPOINTS = 2462;
            public static final int BH_SHOP_DATA_XP_MAGIC = 2463;
            public static final int BH_SHOP_DATA_XP_RANGED = 2464;
            public static final int BH_SHOP_DATA_XP_PRAYER = 2465;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_0 = 2469;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_1 = 2470;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_2 = 2471;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_3 = 2472;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_4 = 2473;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_5 = 2474;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_6 = 2475;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_7 = 2476;
            public static final int OMNISHOP_TEST_SHOP_1_STOCK_8 = 2477;
            public static final int OMNISHOP_TEST_SHOP_3_STOCK_0 = 2480;
            public static final int OMNISHOP_TEST_SHOP_3_STOCK_1 = 2481;
            public static final int OMNISHOP_TEST_SHOP_3_STOCK_2 = 2482;
            public static final int OMNISHOP_TEST_SHOP_3_STOCK_3 = 2483;
            public static final int OMNISHOP_TEST_SHOP_3_STOCK_4 = 2484;
            public static final int FORESTRY_SHOP_STOCK_0 = 3285;
            public static final int FORESTRY_SHOP_STOCK_1 = 3286;
            public static final int FORESTRY_SHOP_STOCK_2 = 3287;
            public static final int FORESTRY_SHOP_STOCK_5 = 3290;
            public static final int FORESTRY_SHOP_STOCK_6 = 3291;
            public static final int FORESTRY_SHOP_STOCK_7 = 3292;
            public static final int FORESTRY_SHOP_STOCK_8 = 3293;
            public static final int FORESTRY_SHOP_STOCK_9 = 3294;
            public static final int FORESTRY_SHOP_STOCK_10 = 3295;
            public static final int FORESTRY_SHOP_STOCK_11 = 3296;
            public static final int FORESTRY_SHOP_STOCK_12 = 3297;
            public static final int FORESTRY_SHOP_STOCK_13 = 3298;
            public static final int FORESTRY_SHOP_STOCK_14 = 3299;
            public static final int FORESTRY_SHOP_STOCK_15 = 3300;
            public static final int FORESTRY_SHOP_STOCK_16 = 3301;
            public static final int FORESTRY_SHOP_STOCK_7_UIM = 3356;
            public static final int FORESTRY_SHOP_STOCK_8_UIM = 3357;
            public static final int FORESTRY_SHOP_STOCK_18 = 3433;
            public static final int FORESTRY_SHOP_STOCK_19 = 3434;
            public static final int FORESTRY_SHOP_STOCK_20 = 3435;
            public static final int FORESTRY_SHOP_STOCK_24 = 3439;
            public static final int FORESTRY_SHOP_STOCK_25 = 3440;
            public static final int FORESTRY_SHOP_STOCK_26 = 3441;
            public static final int FORESTRY_SHOP_STOCK_27 = 3442;
            public static final int FORESTRY_SHOP_STOCK_28 = 3443;
            public static final int FORESTRY_SHOP_STOCK_29 = 3444;
            public static final int AGILITY_ARENA_SHOP_STOCK_1 = 3538;
            public static final int AGILITY_ARENA_SHOP_STOCK_2 = 3539;
            public static final int AGILITY_ARENA_SHOP_STOCK_3 = 3540;
            public static final int AGILITY_ARENA_SHOP_STOCK_4 = 3541;
            public static final int AGILITY_ARENA_SHOP_STOCK_5 = 3542;
            public static final int AGILITY_ARENA_SHOP_STOCK_6 = 3543;
            public static final int BREW_SHOP_BREW_UNIFORM_BLUE = 3546;
            public static final int BREW_SHOP_BREW_TRICORN_BLUE = 3547;
            public static final int BREW_SHOP_BREW_NAVY_SLACKS_BLUE = 3548;
            public static final int BREW_SHOP_BREW_UNIFORM_GREEN = 3549;
            public static final int BREW_SHOP_BREW_TRICORN_GREEN = 3550;
            public static final int BREW_SHOP_BREW_NAVY_SLACKS_GREEN = 3551;
            public static final int BREW_SHOP_BREW_UNIFORM_RED = 3552;
            public static final int BREW_SHOP_BREW_TRICORN_RED = 3553;
            public static final int BREW_SHOP_BREW_NAVY_SLACKS_RED = 3554;
            public static final int BREW_SHOP_BREW_UNIFORM_BROWN = 3555;
            public static final int BREW_SHOP_BREW_TRICORN_BROWN = 3556;
            public static final int BREW_SHOP_BREW_NAVY_SLACKS_BROWN = 3557;
            public static final int BREW_SHOP_BREW_UNIFORM_BLACK = 3558;
            public static final int BREW_SHOP_BREW_TRICORN_BLACK = 3559;
            public static final int BREW_SHOP_BREW_NAVY_SLACKS_BLACK = 3560;
            public static final int BREW_SHOP_BREW_UNIFORM_PURPLE = 3561;
            public static final int BREW_SHOP_BREW_TRICORN_PURPLE = 3562;
            public static final int BREW_SHOP_BREW_NAVY_SLACKS_PURPLE = 3563;
            public static final int BREW_SHOP_BREW_UNIFORM_GREY = 3564;
            public static final int BREW_SHOP_BREW_TRICORN_GREY = 3565;
            public static final int BREW_SHOP_BREW_NAVY_SLACKS_GREY = 3566;
            public static final int BREW_SHOP_BREW_FLAG_1 = 3567;
            public static final int BREW_SHOP_BREW_FLAG_2 = 3568;
            public static final int BREW_SHOP_BREW_FLAG_3 = 3569;
            public static final int BREW_SHOP_BREW_FLAG_4 = 3570;
            public static final int BREW_SHOP_BREW_FLAG_5 = 3571;
            public static final int BREW_SHOP_BREW_FLAG_6 = 3572;
            public static final int BREW_SHOP_BREW_HYPER_YEAST = 3573;
            public static final int BREW_SHOP_BREW_RED_RUM = 3574;
            public static final int BREW_SHOP_BREW_BLUE_RUM = 3575;
            public static final int DEADMAN_SHOP_2024_CAPE_ORNAMENT_SCROLL = 3609;
            public static final int DEADMAN_SHOP_2024_HOME_TELEPORT_SCROLL = 3610;
            public static final int DEADMAN_SHOP_2024_DEADMAN_RUG = 3611;
            public static final int MIXOLOGY_STOCK_LOW_POTION_PACK = 3750;
            public static final int MIXOLOGY_STOCK_MED_POTION_PACK = 3751;
            public static final int MIXOLOGY_STOCK_HIGH_POTION_PACK = 3752;
            public static final int MIXOLOGY_STOCK_REAGENTS_POUCH = 3753;
            public static final int MIXOLOGY_STOCK_PREPOT = 3754;
            public static final int MIXOLOGY_STOCK_ALCHEMIST_GOGGLES = 3755;
            public static final int MIXOLOGY_STOCK_AMULET = 3756;
            public static final int MIXOLOGY_STOCK_ALCHEMIST_BODY = 3757;
            public static final int MIXOLOGY_STOCK_ALCHEMIST_LEGS = 3758;
            public static final int MIXOLOGY_STOCK_ALCHEMIST_GLOVES = 3759;
            public static final int MIXOLOGY_STOCK_JUICE = 3760;
            public static final int MIXOLOGY_STOCK_POTION_STORAGE = 3761;
            public static final int WYRM_AGILITY_REWARDS_SHOP_STOCK_1 = 3772;
            public static final int WYRM_AGILITY_REWARDS_SHOP_STOCK_2 = 3773;
            public static final int WYRM_AGILITY_REWARDS_SHOP_STOCK_3 = 3774;
            public static final int WYRM_AGILITY_REWARDS_SHOP_STOCK_4 = 3775;
            public static final int TITHE_SHOP_FARMERS_HAT = 4983;
            public static final int TITHE_SHOP_FARMERS_TORSO = 4984;
            public static final int TITHE_SHOP_FARMERS_LEGS = 4985;
            public static final int TITHE_SHOP_FARMERS_FEET = 4986;
            public static final int TITHE_SHOP_COMPOST = 4987;
            public static final int TITHE_SHOP_SUPERCOMPOST = 4988;
            public static final int TITHE_SHOP_GRAPE_SEED = 4989;
            public static final int TITHE_SHOP_GRAPE_BLESSING = 4990;
            public static final int TITHE_SHOP_HERB_BOX = 4991;
            public static final int TITHE_SHOP_SEED_PACK = 4992;
            public static final int TITHE_SHOP_SEED_BOX = 4993;
            public static final int TITHE_SHOP_HERB_SACK = 4994;
            public static final int TITHE_SHOP_WATERING_CAN = 4995;
            public static final int TITHE_SHOP_WATERING_CAN_RECHARGE = 4996;
            public static final int TITHE_SHOP_AUTOWEED = 4997;
            public static final int TITHE_SHOP_AUTOWEED_TOGGLE = 4998;
            public static final int CW_SHOP_DATA_CASTLEWARS_CRATE = 5064;
            public static final int CW_SHOP_DATA_CASTLEWARS_MED_HELM = 5065;
            public static final int CW_SHOP_DATA_CASTLEWARS_FULL_HELM = 5066;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_BODY = 5067;
            public static final int CW_SHOP_DATA_CASTLEWARS_SWORD = 5068;
            public static final int CW_SHOP_DATA_CASTLEWARS_SHIELD = 5069;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_LEGS = 5070;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_SKIRT = 5071;
            public static final int CW_SHOP_DATA_CASTLEWARS_BOOTS = 5072;
            public static final int CW_SHOP_DATA_CASTLEWARS_MED_HELM_2 = 5073;
            public static final int CW_SHOP_DATA_CASTLEWARS_FULL_HELM_2 = 5074;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_BODY_2 = 5075;
            public static final int CW_SHOP_DATA_CASTLEWARS_SWORD_2 = 5076;
            public static final int CW_SHOP_DATA_CASTLEWARS_SHIELD_2 = 5077;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_LEGS_2 = 5078;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_SKIRT_2 = 5079;
            public static final int CW_SHOP_DATA_CASTLEWARS_BOOTS_2 = 5080;
            public static final int CW_SHOP_DATA_CASTLEWARS_MED_HELM_3 = 5081;
            public static final int CW_SHOP_DATA_CASTLEWARS_FULL_HELM_3 = 5082;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_BODY_3 = 5083;
            public static final int CW_SHOP_DATA_CASTLEWARS_SWORD_3 = 5084;
            public static final int CW_SHOP_DATA_CASTLEWARS_SHIELD_3 = 5085;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_LEGS_3 = 5086;
            public static final int CW_SHOP_DATA_CASTLEWARS_ARMOUR_SKIRT_3 = 5087;
            public static final int CW_SHOP_DATA_CASTLEWARS_BOOTS_3 = 5088;
            public static final int CW_SHOP_DATA_CASTLEWARS_HOOD_SARADOMIN_PRIZE = 5089;
            public static final int CW_SHOP_DATA_CASTLEWARS_CLOAK_SARADOMIN_PRIZE = 5090;
            public static final int CW_SHOP_DATA_CASTLEWARS_HOOD_ZAMORAK_PRIZE = 5091;
            public static final int CW_SHOP_DATA_CASTLEWARS_CLOAK_ZAMORAK_PRIZE = 5092;
            public static final int CW_SHOP_DATA_SARADOMIN_REWARD_BANNER = 5093;
            public static final int CW_SHOP_DATA_ZAMORAK_REWARD_BANNER = 5094;
            public static final int CW_SHOP_DATA_CASTLEWARS_MAGE_HAT = 5095;
            public static final int CW_SHOP_DATA_CASTLEWARS_MAGE_TOP = 5096;
            public static final int CW_SHOP_DATA_CASTLEWARS_MAGE_LEGS = 5097;
            public static final int CW_SHOP_DATA_CASTLEWARS_RANGE_TOP = 5098;
            public static final int CW_SHOP_DATA_CASTLEWARS_RANGE_LEGS = 5099;
            public static final int CW_SHOP_DATA_CASTLEWARS_RANGE_QUIVER = 5100;
            public static final int CW_SHOP_DATA_CASTLEWARS_SARADOMIN_HALO = 5101;
            public static final int CW_SHOP_DATA_CASTLEWARS_ZAMORAK_HALO = 5102;
            public static final int CW_SHOP_DATA_CASTLEWARS_GUTHIX_HALO = 5103;
            public static final int ENT_TOTEMS_SHOP_STOCK_SPOOL = 5461;
            public static final int ENT_TOTEMS_SHOP_STOCK_KNIFE = 5462;
            public static final int ENT_TOTEMS_SHOP_STOCK_MASK = 5463;
            public static final int ENT_TOTEMS_SHOP_IRONMAN_STOCK_MASK = 5464;
            public static final int ENT_TOTEMS_SHOP_STOCK_BRANCH = 5465;
            public static final int SAILING_SHIP_STOCK_RAFT = 8549;
            public static final int SAILING_SHIP_STOCK_SKIFF = 8550;
            public static final int SAILING_SHIP_STOCK_SLOOP = 8551;
            public static final int DEADMAN_SHOP_2026_WEAPON_ORNAMENT_SCROLL = 9572;
            public static final int DEADMAN_SHOP_2026_POH_ORNAMENT_SCROLL = 9573;
            public static final int DEADMAN_SHOP_2026_HOME_TELEPORT_SCROLL = 9574;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_AGILE_FORTUNE = 9577;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_HOARDING = 9578;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_DECEPTION = 9579;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_LITHE = 9580;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_FOOD_MASTER = 9581;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_WELL_FED = 9582;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_POTION_MASTER = 9583;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_TREASURE_HUNTER = 9584;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_HUNTER = 9585;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_INFERNAL_CHEF = 9586;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_NATURE = 9587;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_DEVOTION = 9588;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_FAITH = 9589;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_ALCHEMANIAC = 9590;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_RESISTANCE = 9591;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_DEFT_STRIKES = 9592;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_AUGMENTED_THRALL = 9593;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_ONSLAUGHT = 9594;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_RESTORATION = 9595;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_TITANIUM = 9596;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_METICULOUSNESS = 9597;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_ENHANCED_HARVEST = 9598;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_SLAUGHTER = 9599;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_WOODCRAFT = 9600;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_REMOTE_STORAGE = 9601;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_CONSISTENCY = 9602;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_RIGOROUS_RANGER = 9603;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_METICULOUS_MAGE = 9604;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_LIGHTBEARER = 9605;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_SPECIALISED_STRIKES = 9606;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_PORCUPINE = 9607;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_FORTIFICATION = 9608;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_RUTHLESS_RANGER = 9609;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_FORMIDABLE_FIGHTER = 9610;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_MENACING_MAGE = 9611;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_SWASHBUCKLER = 9612;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_GUNSLINGER = 9613;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_ARCANE_SWIFTNESS = 9614;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_ADROIT = 9615;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_BARROWS = 9616;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_FINALITY = 9617;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_PIOUS_PROTECTION = 9618;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_AGGRESSION = 9619;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_RAMPAGE = 9620;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_THE_GODS = 9621;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_REVOKED_LIMITATION = 9622;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_CONCLUSION = 9623;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_AUTOMATION = 9624;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_ETERNAL_BELIEF = 9625;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_EFFICIENCY = 9626;
            public static final int DEADMAN_SKULL_SHOP_SIGIL_OF_RUINOUS_PRAYER_BOOK = 9627;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_RECIPE_FOR_DISASTER = 9628;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_CAMELOT_STORYLINE = 9629;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_FREMENNIK_STORYLINE = 9630;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_ELF_STORYLINE = 9631;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_GNOME_STORYLINE = 9632;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_KHARIDIAN_STORYLINE = 9633;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_MYREQUE_STORYLINE = 9634;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_DRAGONKIN_STORYLINE = 9635;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_MAHJARRAT_STORYLINE_1 = 9636;
            public static final int DEADMAN_SKULL_SHOP_QUEST_LAMP_MAHJARRAT_STORYLINE_2 = 9637;
            public static final int VAMPYRIUM_STICKY_POTION = 16875;
            public static final int VAMPYRIUM_CHUNKY_POTION = 16876;
            public static final int VAMPYRIUM_SLIMY_POTION = 16877;
            public static final int VAMPYRIUM_FROTHY_POTION = 16878;
            public static final int VAMPYRIUM_MYSTERY_JERKY = 16879;
            public static final int VAMPYRIUM_MYSTERY_KEBAB = 16880;
            public static final int VAMPYRIUM_CONGEALED_BLOOD = 16881;
            public static final int VAMPYRIUM_STICKY_POTION_3 = 16882;
            public static final int VAMPYRIUM_STICKY_POTION_2 = 16883;
            public static final int VAMPYRIUM_STICKY_POTION_1 = 16884;
            public static final int VAMPYRIUM_CHUNKY_POTION_3 = 16885;
            public static final int VAMPYRIUM_CHUNKY_POTION_2 = 16886;
            public static final int VAMPYRIUM_CHUNKY_POTION_1 = 16887;
            public static final int VAMPYRIUM_SLIMY_POTION_3 = 16888;
            public static final int VAMPYRIUM_SLIMY_POTION_2 = 16889;
            public static final int VAMPYRIUM_SLIMY_POTION_1 = 16890;
            public static final int VAMPYRIUM_FROTHY_POTION_3 = 16891;
            public static final int VAMPYRIUM_FROTHY_POTION_2 = 16892;
            public static final int VAMPYRIUM_FROTHY_POTION_1 = 16893;
        }
    }

    public static final class OmnishopCurrencyData {
        private OmnishopCurrencyData() {}

        public static final int ID = 41;
        public static final int COL_OMNISHOP_CURRENCY_OBJ = 0;
        public static final int COL_OMNISHOP_CURRENCY_NAME_SINGULAR = 1;
        public static final int COL_OMNISHOP_CURRENCY_NAME_PLURAL = 2;
        public static final int COL_OMNISHOP_CURRENCY_GRAPHIC = 3;

        public static final class Row {
            private Row() {}

            public static final int CURRENCY_AGILITY_ARENA_TICKET = 620;
            public static final int OMNISHOP_CURRENCY_DEADMAN_POINTS = 1206;
            public static final int OMNISHOP_CURRENCY_BH_POINTS = 2466;
            public static final int CURRENCY_COINS = 2467;
            public static final int OMNISHOP_CURRENCY_OMNI_POINTS = 2486;
            public static final int FORESTRY_SHOP_CURRENCY_FORESTRY_CURRENCY = 3302;
            public static final int FORESTRY_SHOP_CURRENCY_LOG = 3303;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_OAK = 3304;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_WILLOW = 3305;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_YEW = 3306;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_MAPLE = 3307;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_MAGIC = 3308;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_TEAK = 3309;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_MAHOGANY = 3310;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_REDWOOD = 3311;
            public static final int FORESTRY_SHOP_CURRENCY_LOG_ARCTIC_PINE = 3312;
            public static final int FORESTRY_SHOP_CURRENCY_RAMBLE_LUMBERJACK_BOOTS = 3313;
            public static final int FORESTRY_SHOP_CURRENCY_RAMBLE_LUMBERJACK_HAT = 3314;
            public static final int FORESTRY_SHOP_CURRENCY_RAMBLE_LUMBERJACK_LEGS = 3315;
            public static final int FORESTRY_SHOP_CURRENCY_RAMBLE_LUMBERJACK_TOP = 3316;
            public static final int CURRENCY_FREE = 3353;
            public static final int CURRENCY_AGILITY_ARENA_VOUCHER = 3531;
            public static final int CURRENCY_AGILITY_ARENA_HOOD = 3532;
            public static final int CURRENCY_AGILITY_ARENA_CAPE = 3533;
            public static final int CURRENCY_AGILITY_ARENA_TOP = 3534;
            public static final int CURRENCY_AGILITY_ARENA_LEGS = 3535;
            public static final int CURRENCY_AGILITY_ARENA_GLOVES = 3536;
            public static final int CURRENCY_AGILITY_ARENA_BOOTS = 3537;
            public static final int OMNISHOP_CURRENCY_BREW_PIECES = 3545;
            public static final int OMNISHOP_CURRENCY_MIXOLOGY_AGA = 3747;
            public static final int OMNISHOP_CURRENCY_MIXOLOGY_MOX = 3748;
            public static final int OMNISHOP_CURRENCY_MIXOLOGY_LYE = 3749;
            public static final int CURRENCY_WYRM_AGILITY_TERMITE = 3765;
            public static final int CURRENCY_WYRM_AGILITY_HOOD = 3766;
            public static final int CURRENCY_WYRM_AGILITY_CAPE = 3767;
            public static final int CURRENCY_WYRM_AGILITY_TOP = 3768;
            public static final int CURRENCY_WYRM_AGILITY_LEGS = 3769;
            public static final int CURRENCY_WYRM_AGILITY_GLOVES = 3770;
            public static final int CURRENCY_WYRM_AGILITY_BOOTS = 3771;
            public static final int OMNISHOP_CURRENCY_TITHE_POINTS = 4982;
            public static final int OMNISHOP_CURRENCY_CW_TICKETS = 5104;
            public static final int OMNISHOP_CURRENCY_CW_PLAUDITS = 5105;
            public static final int OMNISHOP_CURRENCY_ENT_TOTEMS_RESEARCH_POINTS = 5460;
            public static final int OMNISHOP_CURRENCY_DEADMAN_SKULL_POINTS = 9576;
        }
    }

    public static final class OmnishopPurseData {
        private OmnishopPurseData() {}

        public static final int ID = 42;
        public static final int COL_OMNISHOP_PURSE_INV = 0;
        public static final int COL_OMNISHOP_PURSE_OBJ = 1;

        public static final class Row {
            private Row() {}

            public static final int OMNISHOP_PURSE_TEST_1 = 2487;
            public static final int FORESTRY_KIT_PURSE = 3281;
            public static final int FORESTRY_KIT_BASKET_CLOSED_PURSE = 3282;
            public static final int FORESTRY_KIT_BASKET_OPEN_PURSE = 3283;
            public static final int FORESTRY_SHOP_LOG_STORAGE_PURSE = 3354;
        }
    }

    public static final class WhispererSeedSpawns {
        private WhispererSeedSpawns() {}

        public static final int ID = 43;
        public static final int COL_ID = 0;
        public static final int COL_TIMELIMIT = 1;
        public static final int COL_TIMELIMIT_AWAKENED = 2;
        public static final int COL_WEAK_SEED = 3;
        public static final int COL_DANGER_SEED = 4;

        public static final class Row {
            private Row() {}

            public static final int WHISPERER_SEED_PATTERN_1 = 3379;
            public static final int WHISPERER_SEED_PATTERN_1_VARIANT = 3380;
            public static final int WHISPERER_SEED_PATTERN_2 = 3381;
            public static final int WHISPERER_SEED_PATTERN_2_VARIANT = 3382;
        }
    }

    public static final class Music {
        private Music() {}

        public static final int ID = 44;
        public static final int COL_SORTNAME = 0;
        public static final int COL_DISPLAYNAME = 1;
        public static final int COL_UNLOCKHINT = 2;
        public static final int COL_DURATION = 3;
        public static final int COL_MIDI = 4;
        public static final int COL_VARIABLE = 5;
        public static final int COL_AUTOMATIC_UNLOCK = 6;
        public static final int COL_AREA = 7;
        public static final int COL_AREA_DEFAULT = 8;
        public static final int COL_HIDDEN = 9;
        public static final int COL_HOLIDAY = 10;
        public static final int COL_SECONDARY_TRACK = 11;
        public static final int COL_RELEASE_TYPE = 12;
        public static final int COL_RELATED_CONTENT = 13;

        public static final class Row {
            private Row() {}

            public static final int MUSIC_RAT_BOSS = 2;
            public static final int MUSIC_7TH_REALM = 2511;
            public static final int MUSIC_A_FARMERS_GRIND = 2512;
            public static final int MUSIC_ADVENTURE = 2513;
            public static final int MUSIC_AL_KHARID = 2514;
            public static final int MUSIC_ALCHEMICAL_HYDRA = 2515;
            public static final int MUSIC_ALLS_FAIRY_IN_LOVE_N_WAR = 2516;
            public static final int MUSIC_ALONE = 2517;
            public static final int MUSIC_ALTAREGO = 2518;
            public static final int MUSIC_ALTERNATIVE_ROOT = 2519;
            public static final int MUSIC_AMASCUTS_THRALLS = 2520;
            public static final int MUSIC_AMBIENT_JUNGLE = 2521;
            public static final int MUSIC_AMLODD = 2522;
            public static final int MUSIC_ANCIENT_PRISON = 2523;
            public static final int MUSIC_ANYWHERE = 2524;
            public static final int MUSIC_ARABIAN = 2525;
            public static final int MUSIC_ARABIAN2 = 2526;
            public static final int MUSIC_ARABIAN3 = 2527;
            public static final int MUSIC_ARABIQUE = 2528;
            public static final int MUSIC_ARAXYTE = 2529;
            public static final int MUSIC_ARBORETUM = 2530;
            public static final int MUSIC_ARCEUUS = 2531;
            public static final int MUSIC_ARCEUUS_OUTSKIRTS = 2532;
            public static final int MUSIC_ARMADYL_ALLIANCE = 2533;
            public static final int MUSIC_ARMAGEDDON = 2534;
            public static final int MUSIC_ARMY_OF_DARKNESS = 2535;
            public static final int MUSIC_ARRIVAL = 2536;
            public static final int MUSIC_ARTISTRY = 2537;
            public static final int MUSIC_ASCENT = 2538;
            public static final int MUSIC_ASSAULT_AND_BATTERY = 2539;
            public static final int MUSIC_ATHLETES_FOOT = 2540;
            public static final int MUSIC_ATTACK1 = 2541;
            public static final int MUSIC_ATTACK2 = 2542;
            public static final int MUSIC_ATTACK3 = 2543;
            public static final int MUSIC_ATTACK4 = 2544;
            public static final int MUSIC_ATTACK5 = 2545;
            public static final int MUSIC_ATTACK6 = 2546;
            public static final int MUSIC_ATTENTION = 2547;
            public static final int MUSIC_AUTUMN_IN_BRIDGELUM = 2548;
            public static final int MUSIC_AUTUMN_VOYAGE = 2549;
            public static final int MUSIC_AYE_CAR_RUM_BA = 2550;
            public static final int MUSIC_AZTEC = 2551;
            public static final int MUSIC_BACK_TO_LIFE = 2552;
            public static final int MUSIC_BACKGROUND = 2553;
            public static final int MUSIC_BAIT = 2554;
            public static final int MUSIC_BALLAD_OF_ENCHANTMENT = 2555;
            public static final int MUSIC_BANDIT_CAMP = 2556;
            public static final int MUSIC_BANDOS_BATTALION = 2557;
            public static final int MUSIC_BARB_ASSAULT_TUTORIAL = 2558;
            public static final int MUSIC_BARB_WIRE = 2559;
            public static final int MUSIC_BARBARIANISM = 2560;
            public static final int MUSIC_BARKING_MAD = 2561;
            public static final int MUSIC_BAROQUE = 2562;
            public static final int MUSIC_BARRELCHEST_BATTLE = 2563;
            public static final int MUSIC_BARRENLAND = 2564;
            public static final int MUSIC_BASILISK_BALLAD = 2565;
            public static final int MUSIC_BASILISK_FIGHT = 2566;
            public static final int MUSIC_BASILISK_JAWS = 2567;
            public static final int MUSIC_BASILISK_LAIR = 2568;
            public static final int MUSIC_BASILISK_REIGN = 2569;
            public static final int MUSIC_BATTLEFRONT = 2570;
            public static final int MUSIC_BEETLE_JUICE = 2571;
            public static final int MUSIC_BENEATH_THE_STRONGHOLD = 2572;
            public static final int MUSIC_BERATING_THE_KING = 2573;
            public static final int MUSIC_BEYOND = 2574;
            public static final int MUSIC_BIG_CHORDS = 2575;
            public static final int MUSIC_BIM_TRAINING = 2576;
            public static final int MUSIC_BLISTERING_BARNACLES = 2577;
            public static final int MUSIC_BLOODBATH = 2578;
            public static final int MUSIC_BOBS_ON_HOLIDAY = 2579;
            public static final int MUSIC_BODY_PARTS = 2580;
            public static final int MUSIC_BONE_DANCE = 2581;
            public static final int MUSIC_BONE_DRY = 2582;
            public static final int MUSIC_BOOK_OF_SPELLS = 2583;
            public static final int MUSIC_BORDERLAND = 2584;
            public static final int MUSIC_BOXOFDELIGHTS = 2585;
            public static final int MUSIC_BREEZE = 2586;
            public static final int MUSIC_BREW_HOO_HOO = 2587;
            public static final int MUSIC_BRIMSTAILS_SCALES = 2588;
            public static final int MUSIC_BUBBLE_AND_SQUEAK = 2589;
            public static final int MUSIC_BUNNY_SUGAR_RUSH = 2590;
            public static final int MUSIC_BURNING_DESIRE = 2591;
            public static final int MUSIC_CABIN_FEVER = 2592;
            public static final int MUSIC_CAMDOZAAL_RUINS = 2593;
            public static final int MUSIC_CAMDOZAAL_VAULT = 2594;
            public static final int MUSIC_CAMELOT = 2595;
            public static final int MUSIC_CASTLEWARS = 2596;
            public static final int MUSIC_CATACOMBS_AND_TOMBS = 2597;
            public static final int MUSIC_CATCH_ME_IF_YOU_CAN = 2598;
            public static final int MUSIC_CAVE_BACKGROUND = 2599;
            public static final int MUSIC_CAVE_OF_BEASTS = 2600;
            public static final int MUSIC_CAVE_OF_THE_GOBLINS = 2601;
            public static final int MUSIC_CAVERN = 2602;
            public static final int MUSIC_CELLAR_SONG = 2603;
            public static final int MUSIC_CHAIN_OF_COMMAND = 2604;
            public static final int MUSIC_CHAMBER = 2605;
            public static final int MUSIC_CHEF_SURPRISE = 2606;
            public static final int MUSIC_CHICKENED_OUT = 2607;
            public static final int MUSIC_CHOMPY_HUNT = 2608;
            public static final int MUSIC_CITY_GUARDIANS = 2609;
            public static final int MUSIC_CITY_OF_THE_DEAD = 2610;
            public static final int MUSIC_CLANLINESS = 2611;
            public static final int MUSIC_CLANWARS = 2612;
            public static final int MUSIC_CLAUSTROPHOBIA = 2613;
            public static final int MUSIC_CLOSE_QUARTERS = 2614;
            public static final int MUSIC_COIL = 2615;
            public static final int MUSIC_COMPETITION = 2616;
            public static final int MUSIC_COMPLICATION = 2617;
            public static final int MUSIC_CONFRONTATION = 2618;
            public static final int MUSIC_CONSPIRACY = 2619;
            public static final int MUSIC_CONTEST = 2620;
            public static final int MUSIC_CORONATION = 2621;
            public static final int MUSIC_CORPORAL_PUNISHMENT = 2622;
            public static final int MUSIC_CORPOREALBEAST = 2623;
            public static final int MUSIC_CORRIDORS_OF_POWER = 2624;
            public static final int MUSIC_COURAGE = 2625;
            public static final int MUSIC_CREATURE_CRUELTY = 2626;
            public static final int MUSIC_CREST_OF_A_WAVE = 2627;
            public static final int MUSIC_CRWYS = 2628;
            public static final int MUSIC_CRYSTAL_CASTLE = 2629;
            public static final int MUSIC_CRYSTAL_CAVE = 2630;
            public static final int MUSIC_CRYSTAL_SWORD = 2631;
            public static final int MUSIC_CURSED = 2632;
            public static final int MUSIC_DAGANNOTH_DAWN = 2633;
            public static final int MUSIC_DANCE_OF_DEATH = 2634;
            public static final int MUSIC_DANCE_OF_THE_MEILYR = 2635;
            public static final int MUSIC_DANCE_OF_THE_UNDEAD = 2636;
            public static final int MUSIC_DANGEROUS = 2637;
            public static final int MUSIC_DANGEROUS_ROAD = 2638;
            public static final int MUSIC_DANGEROUS_WAY = 2639;
            public static final int MUSIC_DARK = 2640;
            public static final int MUSIC_DARKLYALTARED = 2641;
            public static final int MUSIC_DARKMEYER = 2642;
            public static final int MUSIC_DARKNESSINTHEDEPTHS = 2643;
            public static final int MUSIC_DAVY_JONES_LOCKER = 2644;
            public static final int MUSIC_DEAD_CAN_DANCE = 2645;
            public static final int MUSIC_DEAD_QUIET = 2646;
            public static final int MUSIC_DEADLANDS = 2647;
            public static final int MUSIC_DEEP_DOWN = 2648;
            public static final int MUSIC_DEEP_WILDY = 2649;
            public static final int MUSIC_DELRITH_SUMMONING = 2650;
            public static final int MUSIC_DESERT_HEAT = 2651;
            public static final int MUSIC_DESERT_VOYAGE = 2652;
            public static final int MUSIC_DESOLATEMAGE = 2653;
            public static final int MUSIC_DEVILS_MAY_CARE = 2654;
            public static final int MUSIC_DIANGOS_LITTLE_HELPERS = 2655;
            public static final int MUSIC_DIESIRAE = 2656;
            public static final int MUSIC_DIMENSION_X = 2657;
            public static final int MUSIC_DISTANT_LAND = 2658;
            public static final int MUSIC_DISTILLERY_HILARITY = 2659;
            public static final int MUSIC_DOGS_OF_WAR = 2660;
            public static final int MUSIC_DOMAIN_OF_THE_VAMPYRES = 2661;
            public static final int MUSIC_DONT_PANIC_ZANIK = 2662;
            public static final int MUSIC_DOORWAYS = 2663;
            public static final int MUSIC_DORGESHUUN_CITY = 2664;
            public static final int MUSIC_DORGESHUUN_DEEP = 2665;
            public static final int MUSIC_DORGESHUUN_TREATY = 2666;
            public static final int MUSIC_DOWN_AND_OUT = 2667;
            public static final int MUSIC_DOWN_BELOW = 2668;
            public static final int MUSIC_DOWN_TO_EARTH = 2669;
            public static final int MUSIC_DRAGONKINTEMPLE = 2670;
            public static final int MUSIC_DRAGONKINTHEME = 2671;
            public static final int MUSIC_DRAGONTOOTH_ISLAND = 2672;
            public static final int MUSIC_DRAGONWAVES = 2673;
            public static final int MUSIC_DREAM = 2674;
            public static final int MUSIC_DREAMSTATE = 2675;
            public static final int MUSIC_DRUNKEN_DWARF = 2676;
            public static final int MUSIC_DUELARENA = 2677;
            public static final int MUSIC_DUNJUN = 2678;
            public static final int MUSIC_DUSK_IN_YUBIUSK = 2679;
            public static final int MUSIC_DWARF_THEME = 2680;
            public static final int MUSIC_DYNASTY = 2681;
            public static final int MUSIC_EAGLE_PEAK = 2682;
            public static final int MUSIC_EASTER_JIG = 2683;
            public static final int MUSIC_EGYPT = 2684;
            public static final int MUSIC_ELVARGS_THEME = 2685;
            public static final int MUSIC_ELVEN_MIST = 2686;
            public static final int MUSIC_ELVEN_SEED = 2687;
            public static final int MUSIC_EMOTION = 2688;
            public static final int MUSIC_EMPEROR = 2689;
            public static final int MUSIC_ENCLAVE = 2690;
            public static final int MUSIC_ESCAPE = 2691;
            public static final int MUSIC_ESPIONAGE = 2692;
            public static final int MUSIC_ETCETERA_THEME = 2693;
            public static final int MUSIC_ETCETERIA = 2694;
            public static final int MUSIC_EVERLASTING = 2695;
            public static final int MUSIC_EVERLASTING_FIRE = 2696;
            public static final int MUSIC_EVERYWHERE = 2697;
            public static final int MUSIC_EVES_EPINETTE = 2698;
            public static final int MUSIC_EVIL_BOBS_ISLAND = 2699;
            public static final int MUSIC_EXPANSE = 2700;
            public static final int MUSIC_EXPECTING = 2701;
            public static final int MUSIC_EXPEDITION = 2702;
            public static final int MUSIC_EXPOSED = 2703;
            public static final int MUSIC_FAERIE = 2704;
            public static final int MUSIC_FAIRY_DRAGON = 2705;
            public static final int MUSIC_FAITH_OF_THE_HEFIN = 2706;
            public static final int MUSIC_FAITHLESS = 2707;
            public static final int MUSIC_FANFARE = 2708;
            public static final int MUSIC_FANFARE2 = 2709;
            public static final int MUSIC_FANFARE3 = 2710;
            public static final int MUSIC_FANGS_FOR_THE_MEMORY = 2711;
            public static final int MUSIC_FAR_AWAY = 2712;
            public static final int MUSIC_FE_FI_FO_FUM = 2713;
            public static final int MUSIC_FEAR_AND_LOATHING = 2714;
            public static final int MUSIC_FENKENSTRAINS = 2715;
            public static final int MUSIC_FIGHT_OR_FLIGHT = 2716;
            public static final int MUSIC_FIND_MY_WAY = 2717;
            public static final int MUSIC_FIRE_AND_BRIMSTONE = 2718;
            public static final int MUSIC_FISHING = 2719;
            public static final int MUSIC_FLOATING_FREE = 2720;
            public static final int MUSIC_FLUTE_SALAD = 2721;
            public static final int MUSIC_FOOD_FOR_THOUGHT = 2722;
            public static final int MUSIC_FORBIDDEN = 2723;
            public static final int MUSIC_FOREST = 2724;
            public static final int MUSIC_FOREVER = 2725;
            public static final int MUSIC_FORGETTABLE_MELODY = 2726;
            public static final int MUSIC_FORGOTTEN = 2727;
            public static final int MUSIC_FORLORNHOMESTEAD = 2728;
            public static final int MUSIC_FORSAKEN_TOWER = 2729;
            public static final int MUSIC_FORTHOS_DUNGEON = 2730;
            public static final int MUSIC_FOSSIL_LAVA_MINE = 2731;
            public static final int MUSIC_FOSSIL_OVERGROUND_1 = 2732;
            public static final int MUSIC_FOSSIL_OVERGROUND_2 = 2733;
            public static final int MUSIC_FOSSIL_UNDERGROUND = 2734;
            public static final int MUSIC_FOSSIL_UNDERWATER = 2735;
            public static final int MUSIC_FROGLAND = 2736;
            public static final int MUSIC_FROSTBITE = 2737;
            public static final int MUSIC_FRUITS_DE_MER = 2738;
            public static final int MUSIC_FUL_TO_THE_BRIM = 2739;
            public static final int MUSIC_FUNNY_BUNNIES = 2740;
            public static final int MUSIC_GALVICBOSSFIGHT = 2741;
            public static final int MUSIC_GAOL = 2742;
            public static final int MUSIC_GARDEN = 2743;
            public static final int MUSIC_GARDEN_OF_AUTUMN = 2744;
            public static final int MUSIC_GARDEN_OF_SPRING = 2745;
            public static final int MUSIC_GARDEN_OF_SUMMER = 2746;
            public static final int MUSIC_GARDEN_OF_WINTER = 2747;
            public static final int MUSIC_GARGOYLEBOSS = 2748;
            public static final int MUSIC_GAUNTLET_MINIGAME = 2749;
            public static final int MUSIC_GETTING_DOWN_TO_BUSINESS = 2750;
            public static final int MUSIC_GHOST_BOUNCER = 2751;
            public static final int MUSIC_GIANT_DWARF_MEETING = 2752;
            public static final int MUSIC_GIANTS_FOUNDRY = 2753;
            public static final int MUSIC_GILL_BILL = 2754;
            public static final int MUSIC_GNOME_KING = 2755;
            public static final int MUSIC_GNOME_VILLAGE = 2756;
            public static final int MUSIC_GNOME_VILLAGE_PARTY = 2757;
            public static final int MUSIC_GNOME_VILLAGE2 = 2758;
            public static final int MUSIC_GNOMEBALL = 2759;
            public static final int MUSIC_GOBLIN_GAME = 2760;
            public static final int MUSIC_GOBLIN_VILLAGE = 2761;
            public static final int MUSIC_GOLDEN_TOUCH = 2762;
            public static final int MUSIC_GOTR_MINIGAME = 2763;
            public static final int MUSIC_GOTR_PREP = 2764;
            public static final int MUSIC_GREATNESS = 2765;
            public static final int MUSIC_GRIMLYFIENDISH = 2766;
            public static final int MUSIC_GRIP_OF_THE_TALON = 2767;
            public static final int MUSIC_GROTTO = 2768;
            public static final int MUSIC_GROUND_SCAPE = 2769;
            public static final int MUSIC_GROW_GROW_GROW = 2770;
            public static final int MUSIC_GRUMPY = 2771;
            public static final int MUSIC_HALLOWED_SEPULCHRE = 2772;
            public static final int MUSIC_HALLOWED_SEPULCHRE_LOBBY = 2773;
            public static final int MUSIC_HAM_AND_SEEK = 2774;
            public static final int MUSIC_HAM_ATTACK = 2775;
            public static final int MUSIC_HAM_FISTED = 2776;
            public static final int MUSIC_HARMONY = 2777;
            public static final int MUSIC_HARMONY2 = 2778;
            public static final int MUSIC_HAUNTED_MINE = 2779;
            public static final int MUSIC_HAVE_A_BLAST = 2780;
            public static final int MUSIC_HAVE_AN_ICE_DAY = 2781;
            public static final int MUSIC_HEAD_TO_HEAD = 2782;
            public static final int MUSIC_HEART_AND_MIND = 2783;
            public static final int MUSIC_HELLS_BELLS = 2784;
            public static final int MUSIC_HERMIT = 2785;
            public static final int MUSIC_HESPORI = 2786;
            public static final int MUSIC_HESPORI_CAVE = 2787;
            public static final int MUSIC_HIGH_SEAS = 2788;
            public static final int MUSIC_HIGH_SPIRITS = 2789;
            public static final int MUSIC_HOE_DOWN = 2790;
            public static final int MUSIC_HOME_SWEET_HOME = 2791;
            public static final int MUSIC_HOMESCAPE = 2792;
            public static final int MUSIC_HORIZON = 2793;
            public static final int MUSIC_HOSIDIUS = 2794;
            public static final int MUSIC_HOSIDIUS_OUTSKIRTS = 2795;
            public static final int MUSIC_HYPNOTIZED = 2796;
            public static final int MUSIC_IBAN = 2797;
            public static final int MUSIC_ICE_MELODY = 2798;
            public static final int MUSIC_ICE_TROLL_KING = 2799;
            public static final int MUSIC_ICYENE_GRAVEYARD = 2800;
            public static final int MUSIC_ILLUSIVE = 2801;
            public static final int MUSIC_IMPETUOUS = 2802;
            public static final int MUSIC_IMPETUOUS_CLUE = 2803;
            public static final int MUSIC_IN_BETWEEN = 2804;
            public static final int MUSIC_IN_THE_BRINE = 2805;
            public static final int MUSIC_IN_THE_CLINK = 2806;
            public static final int MUSIC_IN_THE_MANOR = 2807;
            public static final int MUSIC_IN_THE_PITS = 2808;
            public static final int MUSIC_INADEQUACY = 2809;
            public static final int MUSIC_INCANTATION = 2810;
            public static final int MUSIC_INFERNO = 2811;
            public static final int MUSIC_INSECT_QUEEN = 2812;
            public static final int MUSIC_INSPIRATION = 2813;
            public static final int MUSIC_INTO_THE_ABYSS = 2814;
            public static final int MUSIC_INTREPID = 2815;
            public static final int MUSIC_IOWERTH_SLAYER_DUNGEON = 2816;
            public static final int MUSIC_IOWERTHS_LAMENT = 2817;
            public static final int MUSIC_ISLAND_LIFE = 2818;
            public static final int MUSIC_ISLAND_OF_THE_TROLLS = 2819;
            public static final int MUSIC_ISLE_OF_EVERYWHERE = 2820;
            public static final int MUSIC_ITHELL = 2821;
            public static final int MUSIC_JESTER_MINUTE = 2822;
            public static final int MUSIC_JOLLYR = 2823;
            public static final int MUSIC_JOY_OF_THE_HUNT = 2824;
            public static final int MUSIC_JUDGE_OF_YAMA = 2825;
            public static final int MUSIC_JUNGLE_BELLS = 2826;
            public static final int MUSIC_JUNGLE_HUNT = 2827;
            public static final int MUSIC_JUNGLE_ISLAND = 2828;
            public static final int MUSIC_JUNGLE_ISLAND_XMAS = 2829;
            public static final int MUSIC_JUNGLE_TROUBLES = 2830;
            public static final int MUSIC_JUNGLY1 = 2831;
            public static final int MUSIC_JUNGLY2 = 2832;
            public static final int MUSIC_JUNGLY3 = 2833;
            public static final int MUSIC_KANON_OF_KHALITH = 2834;
            public static final int MUSIC_KARAMJA_JAM = 2835;
            public static final int MUSIC_KEMESIS = 2836;
            public static final int MUSIC_KINGDOM = 2837;
            public static final int MUSIC_KINGS_BETRAYAL = 2838;
            public static final int MUSIC_KNIGHTLY = 2839;
            public static final int MUSIC_KNIGHTMARE = 2840;
            public static final int MUSIC_KOUREND_CATACOMBS = 2841;
            public static final int MUSIC_KOUREND_HOUSES = 2842;
            public static final int MUSIC_KOURENDTHEMAGNIFICENT = 2843;
            public static final int MUSIC_LA_MORT = 2844;
            public static final int MUSIC_LABYRINTH = 2845;
            public static final int MUSIC_LAIR = 2846;
            public static final int MUSIC_LAMENT = 2847;
            public static final int MUSIC_LAMENT_OF_MEIYERDITCH = 2848;
            public static final int MUSIC_LAND_DOWN_UNDER = 2849;
            public static final int MUSIC_LAND_OF_SNOW = 2850;
            public static final int MUSIC_LAND_OF_THE_DWARVES = 2851;
            public static final int MUSIC_LANDLUBBER = 2852;
            public static final int MUSIC_LAST_STAND = 2853;
            public static final int MUSIC_LASTING = 2854;
            public static final int MUSIC_LASTMANSTANDING = 2855;
            public static final int MUSIC_LEAGUES_3 = 2856;
            public static final int MUSIC_LEGEND = 2857;
            public static final int MUSIC_LEGION = 2858;
            public static final int MUSIC_LIFES_A_BEACH = 2859;
            public static final int MUSIC_LIGHTHOUSE = 2860;
            public static final int MUSIC_LIGHTNESS = 2861;
            public static final int MUSIC_LIGHTWALK = 2862;
            public static final int MUSIC_LITTLE_CAVE_OF_HORRORS = 2863;
            public static final int MUSIC_LLETYAS_DESTRUCTION = 2864;
            public static final int MUSIC_LONESOME = 2865;
            public static final int MUSIC_LONG_AGO = 2866;
            public static final int MUSIC_LONG_WAY_HOME = 2867;
            public static final int MUSIC_LOOKING_BACK = 2868;
            public static final int MUSIC_LORE_AND_ORDER = 2869;
            public static final int MUSIC_LOST_SOUL = 2870;
            public static final int MUSIC_LOVAKENGJ = 2871;
            public static final int MUSIC_LOVAKENGJ_OUTSKIRTS = 2872;
            public static final int MUSIC_LOWERDEPTHS = 2873;
            public static final int MUSIC_LOWERNIEL = 2874;
            public static final int MUSIC_LOWERNIEL_PART_1 = 2875;
            public static final int MUSIC_LOWERNIEL_PART_2 = 2876;
            public static final int MUSIC_LUCIDDREAM = 2877;
            public static final int MUSIC_LUCIDNIGHTMARE = 2878;
            public static final int MUSIC_LULLABY = 2879;
            public static final int MUSIC_MAD_EADGAR = 2880;
            public static final int MUSIC_MAGE_ARENA = 2881;
            public static final int MUSIC_MAGIC_DANCE = 2882;
            public static final int MUSIC_MAGIC_MAGIC_MAGIC = 2883;
            public static final int MUSIC_MAGICAL_JOURNEY = 2884;
            public static final int MUSIC_MAIDEN_AMBIENCE = 2885;
            public static final int MUSIC_MAIDEN_COMBAT = 2886;
            public static final int MUSIC_MAJOR_MINER = 2887;
            public static final int MUSIC_MAKING_WAVES = 2888;
            public static final int MUSIC_MALADY = 2889;
            public static final int MUSIC_MARCH = 2890;
            public static final int MUSIC_MAROONED = 2891;
            public static final int MUSIC_MARZIPAN = 2892;
            public static final int MUSIC_MASQUERADE = 2893;
            public static final int MUSIC_MASTERMINDLESS = 2894;
            public static final int MUSIC_MAUSOLEUM = 2895;
            public static final int MUSIC_MAWSJAWSCLAWS = 2896;
            public static final int MUSIC_MEDDLING_KIDS = 2897;
            public static final int MUSIC_MEDIEVAL = 2898;
            public static final int MUSIC_MEHHARS_TOMB = 2899;
            public static final int MUSIC_MELLOW = 2900;
            public static final int MUSIC_MELODRAMA = 2901;
            public static final int MUSIC_MELZARS_MAZE = 2902;
            public static final int MUSIC_MENAPHOS_GATES = 2903;
            public static final int MUSIC_MERIDIAN = 2904;
            public static final int MUSIC_METHOD_OF_MADNESS = 2905;
            public static final int MUSIC_MILES_AWAY = 2906;
            public static final int MUSIC_MIND_OVER_MATTER = 2907;
            public static final int MUSIC_MINED_OUT = 2908;
            public static final int MUSIC_MIRACLE_DANCE = 2909;
            public static final int MUSIC_MIRAGE = 2910;
            public static final int MUSIC_MISCELLANIA = 2911;
            public static final int MUSIC_MM2_BATTLE = 2912;
            public static final int MUSIC_MM2_BUNKERCORRIDORS = 2913;
            public static final int MUSIC_MM2_BUNKERLAB = 2914;
            public static final int MUSIC_MM2_KRUK = 2915;
            public static final int MUSIC_MM2_SCAPE_APE = 2916;
            public static final int MUSIC_MOLCH = 2917;
            public static final int MUSIC_MONARCH_WALTZ = 2918;
            public static final int MUSIC_MONKEY_MADNESS = 2919;
            public static final int MUSIC_MONSTER_MELEE = 2920;
            public static final int MUSIC_MOODY = 2921;
            public static final int MUSIC_MOR_UL_REK = 2922;
            public static final int MUSIC_MORYTANIA = 2923;
            public static final int MUSIC_MOUSE_TRAP = 2924;
            public static final int MUSIC_MUDSKIPPER_MELODY = 2925;
            public static final int MUSIC_MUSEUMMEDLEY = 2926;
            public static final int MUSIC_MUTANT_MEDLEY = 2927;
            public static final int MUSIC_MY_ARMS_JOURNEY = 2928;
            public static final int MUSIC_MY2ARM_BOSSBATTLE = 2929;
            public static final int MUSIC_MY2ARM_LOVETHEME = 2930;
            public static final int MUSIC_MYTHSGUILD = 2931;
            public static final int MUSIC_NARNODES = 2932;
            public static final int MUSIC_NATURAL = 2933;
            public static final int MUSIC_NECROPOLIS = 2934;
            public static final int MUSIC_NETHER_REALM = 2935;
            public static final int MUSIC_NEVERLAND = 2936;
            public static final int MUSIC_NEWBIE_FARMING = 2937;
            public static final int MUSIC_NEWBIE_MELODY = 2938;
            public static final int MUSIC_NIGHT_OF_THE_VAMPYRE = 2939;
            public static final int MUSIC_NIGHTFALL = 2940;
            public static final int MUSIC_NIGHTMARE_COMBAT = 2941;
            public static final int MUSIC_NO_PASARAN = 2942;
            public static final int MUSIC_NO_WAY_OUT = 2943;
            public static final int MUSIC_NOMAD = 2944;
            public static final int MUSIC_NORSE_CODE = 2945;
            public static final int MUSIC_NOXIRAE = 2946;
            public static final int MUSIC_NULL_AND_VOID = 2947;
            public static final int MUSIC_NYLOCAS_AMBIENCE = 2948;
            public static final int MUSIC_NYLOCAS_COMBAT = 2949;
            public static final int MUSIC_OBSERVATORY_TELESCOPE = 2950;
            public static final int MUSIC_OGRE_THE_TOP = 2951;
            public static final int MUSIC_OLMBATTLE = 2952;
            public static final int MUSIC_ON_THE_UP = 2953;
            public static final int MUSIC_ON_THE_WING = 2954;
            public static final int MUSIC_ONTHESHORE = 2955;
            public static final int MUSIC_ORGAN_1 = 2956;
            public static final int MUSIC_ORGAN_2 = 2957;
            public static final int MUSIC_ORIENTAL = 2958;
            public static final int MUSIC_OUT_OF_THE_DEEP = 2959;
            public static final int MUSIC_OVER_TO_NARDAH = 2960;
            public static final int MUSIC_OVERPASS = 2961;
            public static final int MUSIC_OVERTURE = 2962;
            public static final int MUSIC_PARADE = 2963;
            public static final int MUSIC_PATH_OF_PERIL = 2964;
            public static final int MUSIC_PATHWAYS = 2965;
            public static final int MUSIC_PENGUIN_BARDS = 2966;
            public static final int MUSIC_PENGUIN_PLANS = 2967;
            public static final int MUSIC_PEST_CONTROL = 2968;
            public static final int MUSIC_PESTILENT_BLOAT_AMBIENCE = 2969;
            public static final int MUSIC_PESTILENT_BLOAT_COMBAT = 2970;
            public static final int MUSIC_PHARAOHS_TOMB = 2971;
            public static final int MUSIC_PHASMATYS = 2972;
            public static final int MUSIC_PHEASANT_PEASANT = 2973;
            public static final int MUSIC_PICK_AND_SHOVEL = 2974;
            public static final int MUSIC_PINBALL_WIZARD = 2975;
            public static final int MUSIC_PIRATES_OF_PENANCE = 2976;
            public static final int MUSIC_PIRATES_OF_PERIL = 2977;
            public static final int MUSIC_PISCARILIUS = 2978;
            public static final int MUSIC_POLES_APART = 2979;
            public static final int MUSIC_PRIME_TIME = 2980;
            public static final int MUSIC_PRINCIPALITY = 2981;
            public static final int MUSIC_QUEST = 2982;
            public static final int MUSIC_RANISDUEL = 2983;
            public static final int MUSIC_RAT_A_TAT_TAT = 2984;
            public static final int MUSIC_RAT_HUNT = 2985;
            public static final int MUSIC_READY_FOR_BATTLE = 2986;
            public static final int MUSIC_REGAL = 2987;
            public static final int MUSIC_REGGAE = 2988;
            public static final int MUSIC_REGGAE2 = 2989;
            public static final int MUSIC_RELLEKKA = 2990;
            public static final int MUSIC_RESTINPEACE = 2991;
            public static final int MUSIC_REVENANT_SLAYER_CAVE = 2992;
            public static final int MUSIC_RHAPSODY = 2993;
            public static final int MUSIC_RIGHT_ON_TRACK = 2994;
            public static final int MUSIC_RIGHTEOUSNESS = 2995;
            public static final int MUSIC_RISING_DAMP = 2996;
            public static final int MUSIC_RIVERSIDE = 2997;
            public static final int MUSIC_ROC_AND_ROLL = 2998;
            public static final int MUSIC_ROLL_THE_BONES = 2999;
            public static final int MUSIC_ROMANCING_THE_CRONE = 3000;
            public static final int MUSIC_ROMPER_CHOMPER = 3001;
            public static final int MUSIC_ROOTS_AND_FLUTES = 3002;
            public static final int MUSIC_ROSE = 3003;
            public static final int MUSIC_ROYALE = 3004;
            public static final int MUSIC_RUGGEDTERRAIN = 3005;
            public static final int MUSIC_RUNE_ESSENCE = 3006;
            public static final int MUSIC_SAD_MEADOW = 3007;
            public static final int MUSIC_SAGA = 3008;
            public static final int MUSIC_SARACHNIS_LAIR = 3009;
            public static final int MUSIC_SARCOPHAGUS = 3010;
            public static final int MUSIC_SARIMS_VERMIN = 3011;
            public static final int MUSIC_SCAPE_CAVE = 3012;
            public static final int MUSIC_SCAPE_CRYSTAL = 3013;
            public static final int MUSIC_SCAPE_HUNTER = 3014;
            public static final int MUSIC_SCAPE_MAIN = 3015;
            public static final int MUSIC_SCAPE_ORIGINAL = 3016;
            public static final int MUSIC_SCAPE_SAD = 3017;
            public static final int MUSIC_SCAPE_SANTA = 3018;
            public static final int MUSIC_SCAPE_SCARED = 3019;
            public static final int MUSIC_SCAPE_SOFT = 3020;
            public static final int MUSIC_SCAPE_WILD = 3021;
            public static final int MUSIC_SCAPEFIVE = 3022;
            public static final int MUSIC_SCARAB = 3023;
            public static final int MUSIC_SCHOOLS_OUT = 3024;
            public static final int MUSIC_SCORPIA_DANCES = 3025;
            public static final int MUSIC_SCRUBFOOTS_DESCENT = 3026;
            public static final int MUSIC_SEA_SHANTY = 3027;
            public static final int MUSIC_SEA_SHANTY_XMAS = 3028;
            public static final int MUSIC_SEA_SHANTY2 = 3029;
            public static final int MUSIC_SEAMINORSHANTY = 3030;
            public static final int MUSIC_SERENADE = 3031;
            public static final int MUSIC_SERENE = 3032;
            public static final int MUSIC_SETTLEMENT = 3033;
            public static final int MUSIC_SHADOWLAND = 3034;
            public static final int MUSIC_SHAYZIEN = 3035;
            public static final int MUSIC_SHAYZIEN_CAMP = 3036;
            public static final int MUSIC_SHAYZIEN_OUTSKIRTS = 3037;
            public static final int MUSIC_SHAYZIENCRYPT = 3038;
            public static final int MUSIC_SHAYZIENMARCH = 3039;
            public static final int MUSIC_SHINE = 3040;
            public static final int MUSIC_SHINING = 3041;
            public static final int MUSIC_SHININGSPIRIT = 3042;
            public static final int MUSIC_SHIPWRECKED = 3043;
            public static final int MUSIC_SHOWDOWN = 3044;
            public static final int MUSIC_SIGMUNDS_SHOWDOWN = 3045;
            public static final int MUSIC_SIRE = 3046;
            public static final int MUSIC_SLEPE_DUNGEON = 3047;
            public static final int MUSIC_SLICE_OF_SILENT_MOVIE = 3048;
            public static final int MUSIC_SLICE_OF_STATION = 3049;
            public static final int MUSIC_SLITHER_AND_THITHER = 3050;
            public static final int MUSIC_SLUG_A_BUG_BALL = 3051;
            public static final int MUSIC_SOJOURN = 3052;
            public static final int MUSIC_SORCERESSS_GARDEN = 3053;
            public static final int MUSIC_SOTETSEG_AMBIENCE = 3054;
            public static final int MUSIC_SOTETSEG_COMBAT = 3055;
            public static final int MUSIC_SOUL_WARS = 3056;
            public static final int MUSIC_SOULFALL = 3057;
            public static final int MUSIC_SOUNDSCAPE = 3058;
            public static final int MUSIC_SOURHOG = 3059;
            public static final int MUSIC_SPHINX = 3060;
            public static final int MUSIC_SPIRIT = 3061;
            public static final int MUSIC_SPIRITS_OF_ELID = 3062;
            public static final int MUSIC_SPLENDOUR = 3063;
            public static final int MUSIC_SPOOKY = 3064;
            public static final int MUSIC_SPOOKY2 = 3065;
            public static final int MUSIC_SPOOKYJUNGLE = 3066;
            public static final int MUSIC_SPY_GAMES = 3067;
            public static final int MUSIC_SPYMASTER = 3068;
            public static final int MUSIC_STAGNANT = 3069;
            public static final int MUSIC_STAND_UP_AND_BE_COUNTED = 3070;
            public static final int MUSIC_STARLIGHT = 3071;
            public static final int MUSIC_START = 3072;
            public static final int MUSIC_STILL_NIGHT = 3073;
            public static final int MUSIC_STILLNESS = 3074;
            public static final int MUSIC_STORM_BREW = 3075;
            public static final int MUSIC_STRANDED = 3076;
            public static final int MUSIC_STRANGE_PLACE = 3077;
            public static final int MUSIC_STRATOSPHERE = 3078;
            public static final int MUSIC_STRENGTH_OF_SARADOMIN = 3079;
            public static final int MUSIC_STUCK_IN_THE_MIRE = 3080;
            public static final int MUSIC_SUBTERRANEA = 3081;
            public static final int MUSIC_SUNBURN = 3082;
            public static final int MUSIC_SUPERSTITION = 3083;
            public static final int MUSIC_SUROKS_REVENGE = 3084;
            public static final int MUSIC_SUSPICIOUS = 3085;
            public static final int MUSIC_TALE_OF_KELDAGRIM = 3086;
            public static final int MUSIC_TALKING_FOREST = 3087;
            public static final int MUSIC_TARNS_THEME = 3088;
            public static final int MUSIC_TEARS_OF_GUTHIX_MUSIC = 3089;
            public static final int MUSIC_TECHNOLOGY = 3090;
            public static final int MUSIC_TEMPLE = 3091;
            public static final int MUSIC_TEMPLE_OF_LIGHT = 3092;
            public static final int MUSIC_TEMPLE_OF_THE_EYE = 3093;
            public static final int MUSIC_TEMPLE_OF_TRIBES = 3094;
            public static final int MUSIC_TEMPOROSS = 3095;
            public static final int MUSIC_TERRIBLE_CAVERNS = 3096;
            public static final int MUSIC_TERRIBLE_TUNNELS = 3097;
            public static final int MUSIC_THATSULLENHALL = 3098;
            public static final int MUSIC_THE_ADVENTURER = 3099;
            public static final int MUSIC_THE_ANGELS_FURY = 3100;
            public static final int MUSIC_THE_CELLAR_DWELLERS = 3101;
            public static final int MUSIC_THE_CHOSEN = 3102;
            public static final int MUSIC_THE_DARK_FRAGMENT = 3103;
            public static final int MUSIC_THE_DEPTHS = 3104;
            public static final int MUSIC_THE_DESERT = 3105;
            public static final int MUSIC_THE_DESOLATE_ISLE = 3106;
            public static final int MUSIC_THE_ENCHANTER = 3107;
            public static final int MUSIC_THE_FAR_SIDE = 3108;
            public static final int MUSIC_THE_GALLEON = 3109;
            public static final int MUSIC_THE_GENIE = 3110;
            public static final int MUSIC_THE_GOLEM = 3111;
            public static final int MUSIC_THE_LAST_SHANTY = 3112;
            public static final int MUSIC_THE_LOST_MELODY = 3113;
            public static final int MUSIC_THE_LOST_TRIBE = 3114;
            public static final int MUSIC_THE_LUNAR_ISLE = 3115;
            public static final int MUSIC_THE_MAD_MOLE = 3116;
            public static final int MUSIC_THE_MOLLUSC_MENACE = 3117;
            public static final int MUSIC_THE_MONSTERS_BELOW = 3118;
            public static final int MUSIC_THE_NAVIGATOR = 3119;
            public static final int MUSIC_THE_NOBLE_RODENT = 3120;
            public static final int MUSIC_THE_OTHER_SIDE = 3121;
            public static final int MUSIC_THE_POWER_OF_TEARS = 3122;
            public static final int MUSIC_THE_QUIZMASTER = 3123;
            public static final int MUSIC_THE_ROGUES_DEN = 3124;
            public static final int MUSIC_THE_SHADOW = 3125;
            public static final int MUSIC_THE_SLAYER = 3126;
            public static final int MUSIC_THE_TERRIBLE_TOWER = 3127;
            public static final int MUSIC_THE_TOWER = 3128;
            public static final int MUSIC_THE_TOWER_OF_VOICES = 3129;
            public static final int MUSIC_THE_TRADE_PARADE = 3130;
            public static final int MUSIC_THEME = 3131;
            public static final int MUSIC_THETHEATRE = 3132;
            public static final int MUSIC_THRALL_OF_THE_SERPENT = 3133;
            public static final int MUSIC_THRONE_OF_THE_DEMON = 3134;
            public static final int MUSIC_TIME_OUT = 3135;
            public static final int MUSIC_TIME_TO_MINE = 3136;
            public static final int MUSIC_TIPTOE = 3137;
            public static final int MUSIC_TITLE_FIGHT = 3138;
            public static final int MUSIC_TOA_AMBIENCE = 3139;
            public static final int MUSIC_TOA_BOSS_AKKHA = 3140;
            public static final int MUSIC_TOA_BOSS_BABA = 3141;
            public static final int MUSIC_TOA_BOSS_KEPHRI = 3142;
            public static final int MUSIC_TOA_BOSS_WARDENS = 3143;
            public static final int MUSIC_TOA_BOSS_ZEBAK = 3144;
            public static final int MUSIC_TOA_LOBBY = 3145;
            public static final int MUSIC_TOA_PATH_APMEKEN = 3146;
            public static final int MUSIC_TOA_PATH_CRONDIS = 3147;
            public static final int MUSIC_TOA_PATH_HET = 3148;
            public static final int MUSIC_TOA_PATH_SCABARAS = 3149;
            public static final int MUSIC_TOA_VICTORY = 3150;
            public static final int MUSIC_TOMB_RAIDER = 3151;
            public static final int MUSIC_TOMORROW = 3152;
            public static final int MUSIC_TOO_MANY_COOKS = 3153;
            public static final int MUSIC_TRAEHERN_TOIL = 3154;
            public static final int MUSIC_TRAWLER = 3155;
            public static final int MUSIC_TRAWLER_MINOR = 3156;
            public static final int MUSIC_TREE_SPIRITS = 3157;
            public static final int MUSIC_TREMBLE = 3158;
            public static final int MUSIC_TRIBAL = 3159;
            public static final int MUSIC_TRIBAL_BACKGROUND = 3160;
            public static final int MUSIC_TRIBAL2 = 3161;
            public static final int MUSIC_TRINITY = 3162;
            public static final int MUSIC_TROLLSHUFFLE = 3163;
            public static final int MUSIC_TROUBLE_BREWING = 3164;
            public static final int MUSIC_TROUBLED = 3165;
            public static final int MUSIC_TROUBLED_WATERS = 3166;
            public static final int MUSIC_TWILIGHT = 3167;
            public static final int MUSIC_TZHAAR = 3168;
            public static final int MUSIC_ULLEK = 3169;
            public static final int MUSIC_UNDEAD_DUNGEON = 3170;
            public static final int MUSIC_UNDERCURRENT = 3171;
            public static final int MUSIC_UNDERGROUND = 3172;
            public static final int MUSIC_UNDERSTANDING = 3173;
            public static final int MUSIC_UNKNOWN_LAND = 3174;
            public static final int MUSIC_UNTOUCHABLE = 3175;
            public static final int MUSIC_UPASS = 3176;
            public static final int MUSIC_UPCOMING = 3177;
            public static final int MUSIC_UPIR_LIKHYI = 3178;
            public static final int MUSIC_UPPERDEPTHS = 3179;
            public static final int MUSIC_VANESCULA = 3180;
            public static final int MUSIC_VANSTROM = 3181;
            public static final int MUSIC_VENOMOUS = 3182;
            public static final int MUSIC_VENTURE = 3183;
            public static final int MUSIC_VENTURE2 = 3184;
            public static final int MUSIC_VERZIK_AMBIENCE = 3185;
            public static final int MUSIC_VERZIK_COMBAT = 3186;
            public static final int MUSIC_VERZIK_DEFEATED = 3187;
            public static final int MUSIC_VICTORY_IS_MINE = 3188;
            public static final int MUSIC_VILLAGE = 3189;
            public static final int MUSIC_VISION = 3190;
            public static final int MUSIC_VOLCANIC_VIKINGS = 3191;
            public static final int MUSIC_VOODOO_CULT = 3192;
            public static final int MUSIC_VOYAGE = 3193;
            public static final int MUSIC_WAITING_GAME = 3194;
            public static final int MUSIC_WAKING_DREAM = 3195;
            public static final int MUSIC_WALLY_CUTSCENE = 3196;
            public static final int MUSIC_WANDER = 3197;
            public static final int MUSIC_WARPATH = 3198;
            public static final int MUSIC_WARPED_LIBRARY = 3199;
            public static final int MUSIC_WARRENS = 3200;
            public static final int MUSIC_WARRIOR = 3201;
            public static final int MUSIC_WARRIORS_GUILD = 3202;
            public static final int MUSIC_WASTELAND = 3203;
            public static final int MUSIC_WATERFALL = 3204;
            public static final int MUSIC_WATERLOGGED = 3205;
            public static final int MUSIC_WAY_OF_THE_ENCHANTER = 3206;
            public static final int MUSIC_WAY_OF_THE_WYRM = 3207;
            public static final int MUSIC_WAYWARD = 3208;
            public static final int MUSIC_WE_ARE_THE_FAIRIES = 3209;
            public static final int MUSIC_WEISS_TOWN = 3210;
            public static final int MUSIC_WELL_OF_VOYAGE = 3211;
            public static final int MUSIC_WEREWOLVES = 3212;
            public static final int MUSIC_WHERE_EAGLES_LAIR = 3213;
            public static final int MUSIC_WILD_ISLE = 3214;
            public static final int MUSIC_WILD_SIDE = 3215;
            public static final int MUSIC_WILDERNESS = 3216;
            public static final int MUSIC_WILDERNESS_CAVE = 3217;
            public static final int MUSIC_WILDERNESS2 = 3218;
            public static final int MUSIC_WILDERNESS3 = 3219;
            public static final int MUSIC_WILDWOOD = 3220;
            public static final int MUSIC_WINTER_FUNFARE = 3221;
            public static final int MUSIC_WINTERTODT = 3222;
            public static final int MUSIC_WITCHING = 3223;
            public static final int MUSIC_WOE_OF_THE_WYVERN = 3224;
            public static final int MUSIC_WONDER = 3225;
            public static final int MUSIC_WONDEROUS = 3226;
            public static final int MUSIC_WOODLAND = 3227;
            public static final int MUSIC_WOODLAND_WALK = 3228;
            public static final int MUSIC_WORK_WORK_WORK = 3229;
            public static final int MUSIC_WORKSHOP = 3230;
            public static final int MUSIC_WRATH_AND_RUIN = 3231;
            public static final int MUSIC_XAMPHUR = 3232;
            public static final int MUSIC_XARPUS_AMBIENCE = 3233;
            public static final int MUSIC_XARPUS_COMBAT = 3234;
            public static final int MUSIC_XENOPHOBE = 3235;
            public static final int MUSIC_XMAS_2021 = 3236;
            public static final int MUSIC_YESTERYEAR = 3237;
            public static final int MUSIC_ZALCANO_COMBAT = 3238;
            public static final int MUSIC_ZAMORAK_ZOO = 3239;
            public static final int MUSIC_ZANIKS_THEME = 3240;
            public static final int MUSIC_ZAROS_ZEITGEIST = 3241;
            public static final int MUSIC_ZEALOT = 3242;
            public static final int MUSIC_ZOGRE_DANCE = 3243;
            public static final int MUSIC_ZOMBIEDRAGON = 3244;
            public static final int MUSIC_ZOMBIISM = 3245;
            public static final int MUSIC_OLD_ONES = 3246;
            public static final int MUSIC_GHORROCK_DUNGEON = 3247;
            public static final int MUSIC_ASSASSIN_BOSS = 3248;
            public static final int MUSIC_MUSPAH_BOSS = 3249;
            public static final int MUSIC_DT2_VAULT = 3358;
            public static final int MUSIC_DT2_LASSAR = 3359;
            public static final int MUSIC_DT2_GHORROCK = 3360;
            public static final int MUSIC_DT2_SCAR = 3361;
            public static final int MUSIC_DT2_PURSUER = 3362;
            public static final int MUSIC_DT2_LEVIATHAN = 3363;
            public static final int MUSIC_DT2_SUCELLUS = 3364;
            public static final int MUSIC_DT2_VARDORVIS = 3365;
            public static final int MUSIC_DT2_WHISPERER = 3366;
            public static final int MUSIC_DT2_DIGSITE = 3367;
            public static final int MUSIC_DT2_SLISKE = 3368;
            public static final int MUSIC_DT2_WIGHTS = 3369;
            public static final int MUSIC_DT2_HORN = 3370;
            public static final int MUSIC_DT2_HORN_PART_1 = 3371;
            public static final int MUSIC_DT2_HORN_PART_2 = 3372;
            public static final int MUSIC_DT2_HORN_PART_3 = 3373;
            public static final int MUSIC_DT2_HORN_PART_4 = 3374;
            public static final int MUSIC_DT2_HORN_PART_5 = 3375;
            public static final int MUSIC_DT2_HORN_PART_6 = 3376;
            public static final int MUSIC_DT2_LASSAR_SILENT = 3415;
            public static final int MUSIC_DT2_STRANGLEWOOD = 3416;
            public static final int MUSIC_BOLRIES_DIARY = 3419;
            public static final int MUSIC_HEALIN_FEELIN = 3420;
            public static final int MUSIC_LONGRAMBLE_SCRAMBLE = 3421;
            public static final int MUSIC_STOREROOM_SHUFFLE = 3422;
            public static final int MUSIC_TERRORBIRD_TUSSLE = 3423;
            public static final int MUSIC_WASTE_DEFACED = 3424;
            public static final int MUSIC_DOTS_YULETIDE = 3445;
            public static final int MUSIC_CHILDREN_OF_THE_SUN = 3447;
            public static final int MUSIC_PLOTS_AND_PLANS = 3448;
            public static final int MUSIC_THE_BURNING_SUN = 3449;
            public static final int MUSIC_UNDEAD_ARMY = 3463;
            public static final int MUSIC_DREAM_THEATRE = 3464;
            public static final int MUSIC_ZOMBIE_INVASION = 3465;
            public static final int MUSIC_CIVITAS_ILLA_FORTIS = 3470;
            public static final int MUSIC_AVIUM_SAVANNAH = 3471;
            public static final int MUSIC_RALOS_RISE = 3472;
            public static final int MUSIC_CAM_TORUM = 3473;
            public static final int MUSIC_HUNTER_GUILD = 3474;
            public static final int MUSIC_FORTIS_COLOSSEUM = 3475;
            public static final int MUSIC_PERILOUS_MOONS = 3476;
            public static final int MUSIC_EASTER_2024 = 3527;
            public static final int MUSIC_BLACK_OF_KNIGHT = 3598;
            public static final int MUSIC_DANGEROUS_LOGIC = 3599;
            public static final int MUSIC_TEMPLE_DESECRATED = 3600;
            public static final int MUSIC_THE_EVIL_WITHIN = 3601;
            public static final int MUSIC_THE_KIN = 3602;
            public static final int MUSIC_THE_KIN_FADE = 3603;
            public static final int MUSIC_THE_ROUTE_OF_ALL_EVIL = 3604;
            public static final int MUSIC_THE_SOUND_OF_GUTHIX = 3605;
            public static final int MUSIC_THE_STONE = 3606;
            public static final int MUSIC_INCARCERATION = 3607;
            public static final int MUSIC_THE_ROUTE_OF_THE_PROBLEM = 3608;
            public static final int MUSIC_ARAXXOR = 3675;
            public static final int MUSIC_QUETZACALLI_GORGE = 3733;
            public static final int MUSIC_TWILIGHT_TEMPLE = 3734;
            public static final int MUSIC_ALDARIN = 3735;
            public static final int MUSIC_MISTROCK = 3736;
            public static final int MUSIC_SUNSET_COAST = 3737;
            public static final int MUSIC_AMOXLIATL = 3738;
            public static final int MUSIC_HUEYCOATL = 3739;
            public static final int MUSIC_ALDARIN_2 = 3740;
            public static final int MUSIC_AVIUM_SAVANNAH_2 = 3741;
            public static final int MUSIC_CIVITAS_ILLA_FORTIS_2 = 3743;
            public static final int MUSIC_HAILSTORM_MOUNTAINS = 3744;
            public static final int MUSIC_MATTER_OF_INTRIGUE = 3745;
            public static final int MUSIC_OLD_ONE_RUINS = 3813;
            public static final int MUSIC_LAMISTARDS_LABYRINTH = 3969;
            public static final int MUSIC_THE_NORTH = 3970;
            public static final int MUSIC_THE_HEIST = 3971;
            public static final int MUSIC_HEAVY_SECURITY = 3972;
            public static final int MUSIC_THE_PLUNDERED_TOMB = 3973;
            public static final int MUSIC_CHRISTMAS_2025 = 4969;
            public static final int MUSIC_YAMA_COMBAT = 5154;
            public static final int MUSIC_TLATI_RAINFOREST = 5377;
            public static final int MUSIC_AUBURN_VALLEY_1 = 5378;
            public static final int MUSIC_AUBURN_VALLEY_2 = 5379;
            public static final int MUSIC_KASTORI = 5380;
            public static final int MUSIC_TAL_TEKLAN = 5381;
            public static final int MUSIC_EMISSARY_COMBAT_1 = 5382;
            public static final int MUSIC_EMISSARY_COMBAT_2 = 5383;
            public static final int MUSIC_FINAL_DAWN_METZLI_BOSS = 5384;
            public static final int MUSIC_AVIUM_SAVANNAH_3 = 5385;
            public static final int MUSIC_CUSTODIA_MOUNTAINS = 5386;
            public static final int MUSIC_MOKHAIOTL = 5387;
            public static final int MUSIC_DOOM_BOSS = 5388;
            public static final int MUSIC_STALKER_DUNGEON = 5389;
            public static final int MUSIC_ARDENT_OCEAN = 7073;
            public static final int MUSIC_ARDENT_OCEAN_2 = 7074;
            public static final int MUSIC_UNQUIET_OCEAN = 7075;
            public static final int MUSIC_UNQUIET_OCEAN_2 = 7076;
            public static final int MUSIC_SHROUDED_OCEAN = 7077;
            public static final int MUSIC_SHROUDED_OCEAN_2 = 7078;
            public static final int MUSIC_NORTHERN_OCEAN = 7079;
            public static final int MUSIC_NORTHERN_OCEAN_2 = 7080;
            public static final int MUSIC_WESTERN_OCEAN = 7081;
            public static final int MUSIC_WESTERN_OCEAN_2 = 7082;
            public static final int MUSIC_SUNSET_OCEAN = 7083;
            public static final int MUSIC_SUNSET_OCEAN_2 = 7084;
            public static final int MUSIC_BARRACUDA_TRIALS = 7085;
            public static final int MUSIC_CALM_ISLANDS = 7086;
            public static final int MUSIC_MYSTERIOUS_ISLANDS = 7087;
            public static final int MUSIC_DANGEROUS_ISLANDS = 7088;
            public static final int MUSIC_INDUSTRIAL_ISLANDS = 7089;
            public static final int MUSIC_GRYPHON_BOSS = 7090;
            public static final int MUSIC_HONKYTONKY_SEA_SHANTY2 = 7091;
            public static final int MUSIC_YO_HO_HO = 7092;
            public static final int MUSIC_GREAT_CONCH = 7093;
            public static final int MUSIC_GREAT_CONCH_2 = 7094;
            public static final int MUSIC_GREAT_CONCH_UNDERGROUND = 7095;
            public static final int MUSIC_SCAPE_SAIL = 7096;
            public static final int MUSIC_BRUTUS_COW_BOSS = 8662;
            public static final int MUSIC_SAILING_COMBAT = 16223;
            public static final int MUSIC_RED_ROCK_ISLAND = 16224;
            public static final int MUSIC_RED_REEFS_UNDERWATER = 16225;
            public static final int MUSIC_SAILING_COMBAT_02 = 16226;
            public static final int MUSIC_SUGADINTIS_HIDEOUT = 16393;
            public static final int MUSIC_WYRD_BOSS = 16394;
            public static final int MUSIC_DRAKAN_BOSS_1 = 16395;
            public static final int MUSIC_DRAKAN_BOSS_3 = 16396;
            public static final int MUSIC_DRAKAN_BOSS_4 = 16397;
            public static final int MUSIC_DRAKAN_BOSS_4_MAIN_SECTION = 16398;
            public static final int MUSIC_MAGGOT_BOSS = 16399;
            public static final int MUSIC_HOPEFUL_LAMENT = 16400;
            public static final int MUSIC_HOPELESS_LAMENT = 16401;
            public static final int MUSIC_HIDEOUT_PREPARATION = 16402;
            public static final int MUSIC_HIDEOUT_PREPARATION_2 = 16403;
            public static final int MUSIC_HIDEOUT_DEFENCE_PART_1 = 16404;
            public static final int MUSIC_HIDEOUT_DEFENCE_PART_2 = 16405;
            public static final int MUSIC_HIDEOUT_DEFENCE_PART_3 = 16406;
            public static final int MUSIC_HIDEOUT_DEFENCE_PART_4 = 16407;
            public static final int MUSIC_DRAKANS_SPEECH = 16408;
            public static final int MUSIC_SOTFA_FOREST = 16409;
            public static final int MUSIC_CASTLE_DRAKAN = 16410;
            public static final int MUSIC_SANGVESTI = 16411;
            public static final int MUSIC_BRIDGE_ATTACK = 16412;
        }
    }

    public static final class WoodcuttingResource {
        private WoodcuttingResource() {}

        public static final int ID = 45;
        public static final int COL_STAT_REQUIREMENT = 0;
        public static final int COL_ACTION_DELAY = 1;
        public static final int COL_BASE_GATHER_CHANCE = 2;
        public static final int COL_AXE_GATHER_CHANCE = 3;
        public static final int COL_CHOP_ANIM_ENUM = 4;
        public static final int COL_GATHER_SOUND = 5;
        public static final int COL_TRACK_LAST_RESOURCE = 6;
        public static final int COL_GROUP_GATHERING_CONFIG = 7;
        public static final int COL_GROUP_LEVEL_BOOST_CAP = 8;
        public static final int COL_GROUP_XP_SCALAR_CAP = 9;
        public static final int COL_BEHAVIOUR_DATA = 10;

        public static final class Row {
            private Row() {}

            public static final int WOODCUTTING_RESOURCE_YEW_TREE = 3251;
            public static final int WOODCUTTING_RESOURCE_YEW_TREE_FARMING_PATCH = 3252;
            public static final int WOODCUTTING_RESOURCE_TEAK_TREE = 3256;
            public static final int WOODCUTTING_RESOURCE_TEAK_TREE_FARMING_PATCH = 3257;
            public static final int WOODCUTTING_RESOURCE_MAHOGANY_TREE = 3261;
            public static final int WOODCUTTING_RESOURCE_MAHOGANY_TREE_FARMING_PATCH = 3262;
            public static final int WOODCUTTING_RESOURCE_MAPLE_TREE = 3266;
            public static final int WOODCUTTING_RESOURCE_MAPLE_TREE_FARMING_PATCH = 3267;
            public static final int WOODCUTTING_RESOURCE_MAGIC_TREE = 3271;
            public static final int WOODCUTTING_RESOURCE_MAGIC_TREE_FARMING_PATCH = 3272;
            public static final int WOODCUTTING_RESOURCE_REDWOOD_TREE = 3276;
            public static final int WOODCUTTING_RESOURCE_REDWOOD_TREE_FARMING_PATCH = 3277;
            public static final int WOODCUTTING_RESOURCE_ARCTIC_PINE = 3317;
            public static final int WOODCUTTING_RESOURCE_WILLOW_TREE = 3320;
            public static final int WOODCUTTING_RESOURCE_WILLOW_TREE_FARMING_PATCH = 3321;
            public static final int WOODCUTTING_RESOURCE_HOLLOW_TREE = 3325;
            public static final int WOODCUTTING_RESOURCE_OAK_TREE = 3328;
            public static final int WOODCUTTING_RESOURCE_OAK_TREE_FARMING_PATCH = 3329;
            public static final int WOODCUTTING_RESOURCE_RISING_ROOTS_TREE = 3333;
            public static final int WOODCUTTING_RESOURCE_MISC_MAPLE_TREE = 3340;
            public static final int WOODCUTTING_RESOURCE_MISC_TEAK_TREE = 3343;
            public static final int WOODCUTTING_RESOURCE_MISC_MAHOGANY_TREE = 3346;
            public static final int WOODCUTTING_RESOURCE_CAMPHOR_TREE = 9424;
            public static final int WOODCUTTING_RESOURCE_CAMPHOR_TREE_FARMING_PATCH = 9425;
            public static final int WOODCUTTING_RESOURCE_IRONWOOD_TREE = 9430;
            public static final int WOODCUTTING_RESOURCE_IRONWOOD_TREE_FARMING_PATCH = 9431;
            public static final int WOODCUTTING_RESOURCE_JATOBA_TREE = 9436;
            public static final int WOODCUTTING_RESOURCE_ROSEWOOD_TREE = 9440;
            public static final int WOODCUTTING_RESOURCE_ROSEWOOD_TREE_FARMING_PATCH = 9441;
        }
    }

    public static final class WoodcuttingBasicResourceData {
        private WoodcuttingBasicResourceData() {}

        public static final int ID = 46;
        public static final int COL_IS_FARMING_PATCH = 0;
        public static final int COL_GATHER_XP = 1;
        public static final int COL_CLUENEST_CHANCE = 2;
        public static final int COL_BIRDSNEST_CHANCE = 3;
        public static final int COL_SKILLPET = 4;
        public static final int COL_SKILLPET_CHANCE = 5;
        public static final int COL_GATHER_OBJ = 6;
        public static final int COL_LEAVES_OBJ = 7;
        public static final int COL_DEPLETE_CHANCE = 8;
        public static final int COL_RESPAWN_TIME_RANGE = 9;

        public static final class Row {
            private Row() {}

            public static final int WOODCUTTING_RESOURCE_YEW_TREE_DATA = 3253;
            public static final int WOODCUTTING_RESOURCE_YEW_TREE_FARMING_PATCH_DATA = 3254;
            public static final int WOODCUTTING_RESOURCE_TEAK_TREE_DATA = 3258;
            public static final int WOODCUTTING_RESOURCE_TEAK_TREE_FARMING_PATCH_DATA = 3259;
            public static final int WOODCUTTING_RESOURCE_MAHOGANY_TREE_DATA = 3263;
            public static final int WOODCUTTING_RESOURCE_MAHOGANY_TREE_FARMING_PATCH_DATA = 3264;
            public static final int WOODCUTTING_RESOURCE_MAPLE_TREE_DATA = 3268;
            public static final int WOODCUTTING_RESOURCE_MAPLE_TREE_FARMING_PATCH_DATA = 3269;
            public static final int WOODCUTTING_RESOURCE_MAGIC_TREE_DATA = 3273;
            public static final int WOODCUTTING_RESOURCE_MAGIC_TREE_FARMING_PATCH_DATA = 3274;
            public static final int WOODCUTTING_RESOURCE_REDWOOD_TREE_DATA = 3278;
            public static final int WOODCUTTING_RESOURCE_REDWOOD_TREE_FARMING_PATCH_DATA = 3279;
            public static final int WOODCUTTING_RESOURCE_ARCTIC_PINE_DATA = 3318;
            public static final int WOODCUTTING_RESOURCE_WILLOW_TREE_DATA = 3322;
            public static final int WOODCUTTING_RESOURCE_WILLOW_TREE_FARMING_PATCH_DATA = 3323;
            public static final int WOODCUTTING_RESOURCE_HOLLOW_TREE_DATA = 3326;
            public static final int WOODCUTTING_RESOURCE_OAK_TREE_DATA = 3330;
            public static final int WOODCUTTING_RESOURCE_OAK_TREE_FARMING_PATCH_DATA = 3331;
            public static final int WOODCUTTING_RESOURCE_RISING_ROOTS_TREE_DATA = 3334;
            public static final int WOODCUTTING_RESOURCE_CAMPHOR_TREE_DATA = 9426;
            public static final int WOODCUTTING_RESOURCE_CAMPHOR_TREE_FARMING_PATCH_DATA = 9427;
            public static final int WOODCUTTING_RESOURCE_IRONWOOD_TREE_DATA = 9432;
            public static final int WOODCUTTING_RESOURCE_IRONWOOD_TREE_FARMING_PATCH_DATA = 9433;
            public static final int WOODCUTTING_RESOURCE_JATOBA_TREE_DATA = 9437;
            public static final int WOODCUTTING_RESOURCE_ROSEWOOD_TREE_DATA = 9442;
            public static final int WOODCUTTING_RESOURCE_ROSEWOOD_TREE_FARMING_PATCH_DATA = 9443;
        }
    }

    public static final class GatheringEventSaplingLoc {
        private GatheringEventSaplingLoc() {}

        public static final int ID = 47;
        public static final int COL_LOC = 0;
        public static final int COL_WIDTH = 1;
        public static final int COL_LENGTH = 2;

        public static final class Row {
            private Row() {}

            public static final int GATHERING_EVENT_SAPLING_1X1 = 3336;
            public static final int GATHERING_EVENT_SAPLING_2X2 = 3337;
            public static final int GATHERING_EVENT_SAPLING_3X3 = 3338;
            public static final int GATHERING_EVENT_SAPLING_4X4 = 3469;
        }
    }

    public static final class GroupGatheringResource {
        private GroupGatheringResource() {}

        public static final int ID = 48;
        public static final int COL_LIFETIME_RANGE = 0;
        public static final int COL_RESPAWN_TIME_RANGE = 1;
        public static final int COL_BUFF_PLAYER_CAP = 2;
        public static final int COL_BUFF_ID = 3;
        public static final int COL_GATHERING_STAT = 4;
        public static final int COL_WEAKQUEUE_ACTION_DELAY = 5;
        public static final int COL_GATHERING_EVENT_CHANCE_DATA = 6;

        public static final class Row {
            private Row() {}

            public static final int GROUP_GATHERING_RESOURCE_YEW_TREE = 3255;
            public static final int GROUP_GATHERING_RESOURCE_TEAK_TREE = 3260;
            public static final int GROUP_GATHERING_RESOURCE_MAHOGANY_TREE = 3265;
            public static final int GROUP_GATHERING_RESOURCE_MAPLE_TREE = 3270;
            public static final int GROUP_GATHERING_RESOURCE_MAGIC_TREE = 3275;
            public static final int GROUP_GATHERING_RESOURCE_REDWOOD_TREE = 3280;
            public static final int GROUP_GATHERING_RESOURCE_ARCTIC_PINE = 3319;
            public static final int GROUP_GATHERING_RESOURCE_WILLOW_TREE = 3324;
            public static final int GROUP_GATHERING_RESOURCE_HOLLOW_TREE = 3327;
            public static final int GROUP_GATHERING_RESOURCE_OAK_TREE = 3332;
            public static final int GROUP_GATHERING_RESOURCE_RISING_ROOTS_TREE = 3335;
            public static final int GROUP_GATHERING_RESOURCE_MISC_MAPLE_TREE = 3342;
            public static final int GROUP_GATHERING_RESOURCE_MISC_TEAK_TREE = 3345;
            public static final int GROUP_GATHERING_RESOURCE_MISC_MAHOGANY_TREE = 3348;
            public static final int GROUP_GATHERING_RESOURCE_CAMPHOR_TREE = 9428;
            public static final int GROUP_GATHERING_RESOURCE_IRONWOOD_TREE = 9434;
            public static final int GROUP_GATHERING_RESOURCE_JATOBA_TREE = 9438;
            public static final int GROUP_GATHERING_RESOURCE_ROSEWOOD_TREE = 9444;
        }
    }

    public static final class GatheringEventChanceData {
        private GatheringEventChanceData() {}

        public static final int ID = 49;
        public static final int COL_EVENT_LOW_CHANCE = 0;
        public static final int COL_EVENT_HIGH_CHANCE = 1;
        public static final int COL_EVENT_CHANCE_PLAYER_CAP = 2;
        public static final int COL_EVENT_START_TIME_BUFFER = 3;
        public static final int COL_EVENT_END_TIME_BUFFER = 4;
        public static final int COL_EVENTS_LIST = 5;

        public static final class Row {
            private Row() {}

            public static final int GATHERING_EVENTS_TREE_YEW = 2507;
            public static final int GATHERING_EVENTS_TREE_TEAK = 2508;
            public static final int GATHERING_EVENTS_TREE_MAHOGANY = 2509;
            public static final int GATHERING_EVENTS_TREE_MAPLE = 2510;
            public static final int GATHERING_EVENTS_TREE_MAGIC = 3349;
            public static final int GATHERING_EVENTS_TREE_ARCTIC_PINE = 3350;
            public static final int GATHERING_EVENTS_TREE_WILLOW = 3351;
            public static final int GATHERING_EVENTS_TREE_OAK = 3352;
            public static final int GATHERING_EVENTS_TREE_CAMPHOR = 9429;
            public static final int GATHERING_EVENTS_TREE_IRONWOOD = 9435;
            public static final int GATHERING_EVENTS_TREE_JATOBA = 9439;
            public static final int GATHERING_EVENTS_TREE_ROSEWOOD = 9445;
        }
    }

    public static final class GatheringEventEventsList {
        private GatheringEventEventsList() {}

        public static final int ID = 50;
        public static final int COL_EVENT = 0;

        public static final class Row {
            private Row() {}

            public static final int GATHERING_EVENTS_LIST_TREE_GENERAL_PURPOSE = 3339;
        }
    }

    public static final class MiscWoodcuttingResourceData {
        private MiscWoodcuttingResourceData() {}

        public static final int ID = 51;
        public static final int COL_OWNING_NPC = 0;
        public static final int COL_OWNING_NPC_NAME = 1;
        public static final int COL_RESOURCE_NAME = 2;
        public static final int COL_GATHER_XP = 3;
        public static final int COL_MISC_QUEST_VAR_REQUIREMENT = 4;
        public static final int COL_ROYAL_QUEST_VAR_REQUIREMENT = 5;
        public static final int COL_APPROVAL_CHANCE = 6;
        public static final int COL_APPROVAL_AMOUNT = 7;

        public static final class Row {
            private Row() {}

            public static final int WOODCUTTING_RESOURCE_MISC_MAPLE_TREE_DATA = 3341;
            public static final int WOODCUTTING_RESOURCE_MISC_TEAK_TREE_DATA = 3344;
            public static final int WOODCUTTING_RESOURCE_MISC_MAHOGANY_TREE_DATA = 3347;
        }
    }

    public static final class Dt2LassarBarrier {
        private Dt2LassarBarrier() {}

        public static final int ID = 52;
        public static final int COL_TIER = 0;
        public static final int COL_ID = 1;
        public static final int COL_COORD = 2;

        public static final class Row {
            private Row() {}

            public static final int DT2_LASSAR_BARRIER_T1_A = 3383;
            public static final int DT2_LASSAR_BARRIER_T1_B = 3384;
            public static final int DT2_LASSAR_BARRIER_T1_C = 3385;
            public static final int DT2_LASSAR_BARRIER_T1_D = 3386;
            public static final int DT2_LASSAR_BARRIER_T2_A = 3387;
            public static final int DT2_LASSAR_BARRIER_T2_B = 3388;
            public static final int DT2_LASSAR_BARRIER_T2_C = 3389;
            public static final int DT2_LASSAR_BARRIER_T2_D = 3390;
            public static final int DT2_LASSAR_BARRIER_T3_A = 3391;
        }
    }

    public static final class Dt2LassarRemnant {
        private Dt2LassarRemnant() {}

        public static final int ID = 53;
        public static final int COL_ID = 0;
        public static final int COL_COORD = 1;
        public static final int COL_EMPOWERED = 2;
        public static final int COL_QUEST_STAGE = 3;

        public static final class Row {
            private Row() {}

            public static final int DT2_LASSAR_REMNANT_1 = 3392;
            public static final int DT2_LASSAR_REMNANT_2 = 3393;
            public static final int DT2_LASSAR_REMNANT_3 = 3394;
            public static final int DT2_LASSAR_REMNANT_4 = 3395;
            public static final int DT2_LASSAR_REMNANT_5 = 3396;
            public static final int DT2_LASSAR_REMNANT_6 = 3397;
            public static final int DT2_LASSAR_REMNANT_7 = 3398;
            public static final int DT2_LASSAR_REMNANT_8 = 3399;
            public static final int DT2_LASSAR_REMNANT_9 = 3400;
            public static final int DT2_LASSAR_REMNANT_10 = 4233;
        }
    }

    public static final class Dt2LassarDoor {
        private Dt2LassarDoor() {}

        public static final int ID = 54;
        public static final int COL_LOCK_ID = 0;
        public static final int COL_LEFT_COORD = 1;
        public static final int COL_RIGHT_COORD = 2;
        public static final int COL_ROTATION = 3;
        public static final int COL_BRAZIERS_REQUIRED = 4;
        public static final int COL_KEY_OBJ = 5;
        public static final int COL_KEY_NAMEDOBJ = 6;
        public static final int COL_INSTANT_UNLOCK = 7;

        public static final class Row {
            private Row() {}

            public static final int DT2_LASSAR_DOOR_1 = 3401;
            public static final int DT2_LASSAR_DOOR_2 = 3402;
        }
    }

    public static final class Dt2LassarChest {
        private Dt2LassarChest() {}

        public static final int ID = 55;
        public static final int COL_LOCK_ID = 0;
        public static final int COL_COORD = 1;
        public static final int COL_BASE_CHEST = 2;
        public static final int COL_ROTATION = 3;
        public static final int COL_LOC_SHAPE = 4;
        public static final int COL_BRAZIERS_REQUIRED = 5;
        public static final int COL_KEY_OBJ = 6;
        public static final int COL_KEY_NAMEDOBJ = 7;
        public static final int COL_ITEM_TO_GIVE = 8;
        public static final int COL_GIVE_STRING = 9;
        public static final int COL_INSTANT_UNLOCK = 10;

        public static final class Row {
            private Row() {}

            public static final int DT2_LASSAR_CHEST_1 = 3403;
            public static final int DT2_LASSAR_CHEST_2 = 3404;
            public static final int DT2_LASSAR_CHEST_3 = 3405;
        }
    }

    public static final class Dt2LassarGhosts {
        private Dt2LassarGhosts() {}

        public static final int ID = 56;
        public static final int COL_GHOST = 0;

        public static final class Row {
            private Row() {}

            public static final int DT2_LASSAR_GHOSTS = 3406;
        }
    }

    public static final class Dt2LassarNpcs {
        private Dt2LassarNpcs() {}

        public static final int ID = 57;
        public static final int COL_NPC = 0;

        public static final class Row {
            private Row() {}

            public static final int DT2_LASSAR_NPCS = 3407;
        }
    }

    public static final class Dt2LassarItems {
        private Dt2LassarItems() {}

        public static final int ID = 58;
        public static final int COL_ITEM = 0;

        public static final class Row {
            private Row() {}

            public static final int DT2_LASSAR_ITEMS = 3408;
        }
    }

    public static final class Dt2LassarBraziers {
        private Dt2LassarBraziers() {}

        public static final int ID = 59;
        public static final int COL_ID = 0;
        public static final int COL_COORD = 1;

        public static final class Row {
            private Row() {}

            public static final int DT2_LASSAR_BRAZIERS_1 = 3409;
        }
    }

    public static final class Dt2ScarMaze {
        private Dt2ScarMaze() {}

        public static final int ID = 60;
        public static final int COL_ID = 0;
        public static final int COL_SOURCE = 1;
        public static final int COL_ENTRY_COORD = 2;
        public static final int COL_BOAT_ROOM_COORD = 3;

        public static final class Row {
            private Row() {}

            public static final int DT2_SCAR_MAZE_1 = 3410;
            public static final int DT2_SCAR_MAZE_2 = 3411;
            public static final int DT2_SCAR_MAZE_3 = 3412;
        }
    }

    public static final class Speedrun {
        private Speedrun() {}

        public static final int ID = 61;
        public static final int COL_QUEST = 0;
        public static final int COL_SPEEDRUN_STATE = 1;
        public static final int COL_SPEEDRUN_TROPHY_TIMES = 2;
        public static final int COL_SPEEDRUN_ITEM_UNLOCKS = 3;
        public static final int COL_SPEEDRUN_QUEST_UNLOCKS = 4;
        public static final int COL_SPEEDRUN_STAT_UNLOCKS = 5;
        public static final int COL_SPEEDRUN_COMBAT_STATS_UNLOCK = 6;
        public static final int COL_SPEEDRUN_POH_LOCATION = 7;

        public static final class Row {
            private Row() {}

            public static final int SPEEDRUN_BELOWICEMOUNTAIN = 3289;
            public static final int SPEEDRUN_BLACKKNIGHTSFORTRESS = 3432;
            public static final int SPEEDRUN_COOKSASSISTANT = 3436;
            public static final int SPEEDRUN_DEMONSLAYER = 3437;
            public static final int SPEEDRUN_DRAGONSLAYER1 = 3438;
            public static final int SPEEDRUN_DRAGONSLAYER2 = 3452;
            public static final int SPEEDRUN_ERNESTTHECHICKEN = 3453;
            public static final int SPEEDRUN_KINGDOMDIVIDED = 3454;
            public static final int SPEEDRUN_KNIGHTSSWORD = 3455;
            public static final int SPEEDRUN_PRINCEALIRESCUE = 3456;
            public static final int SPEEDRUN_SINSOFTHEFATHER = 3457;
            public static final int SPEEDRUN_TASTEOFHOPE = 3458;
            public static final int SPEEDRUN_VAMPYRESLAYER = 3459;
            public static final int SPEEDRUN_XMARKSTHESPOT = 3460;
            public static final int SPEEDRUN_TEMPLEOFTHEEYE = 3461;
            public static final int SPEEDRUN_BENEATHCURSEDSANDS = 3462;
        }
    }

    public static final class ClanSettingOptionsList {
        private ClanSettingOptionsList() {}

        public static final int ID = 62;
        public static final int COL_CLAN_SETTING_TITLE = 0;
        public static final int COL_CLAN_SETTING_OPTION = 1;
        public static final int COL_CLAN_SETTING_ENTRY_HEIGHT = 2;
        public static final int COL_CLAN_SETTING_MOBILE_ENTRY_HEIGHT = 3;
        public static final int COL_CLAN_SETTING_ICON_SIZE = 4;

        public static final class Row {
            private Row() {}

            public static final int CLAN_SETTING_OPTIONS_COMBAT_ACHIEVEMENTS = 3468;
            public static final int CLAN_SETTING_OPTIONS_COLLECTION_BROADCASTS_EVERY_X_LOGS = 4100;
            public static final int CLAN_SETTING_OPTIONS_BROADCASTEVENTS_NOTIFICATION = 4101;
            public static final int CLAN_SETTING_OPTIONS_BROADCASTMAINGAME_NOTIFICATION = 4102;
        }
    }

    public static final class VarlamoreThievingHouse {
        private VarlamoreThievingHouse() {}

        public static final int ID = 63;
        public static final int COL_ID = 0;
        public static final int COL_HOUSE_COORD = 1;
        public static final int COL_OUTSIDE_DOOR_COORD = 2;
        public static final int COL_INSIDE_DOOR_COORD = 3;
        public static final int COL_WANDER_COORD = 4;
        public static final int COL_WINDOW_OUTSIDE_COORD = 5;
        public static final int COL_HOUSE_NAME = 6;
        public static final int COL_HOUSE_NAME_PRONOUN = 7;
        public static final int COL_HOUSE_AREA = 8;
        public static final int COL_HOUSE_LOOTABLE = 9;
        public static final int COL_HOUSE_DOOR_ROTATION_OPEN = 10;
        public static final int COL_HOUSE_DOOR_ROTATION_CLOSED = 11;

        public static final class Row {
            private Row() {}

            public static final int VARLAMORE_THIEVING_HOUSE_1 = 3516;
            public static final int VARLAMORE_THIEVING_HOUSE_2 = 3517;
            public static final int VARLAMORE_THIEVING_HOUSE_3 = 3518;
        }
    }

    public static final class Quetzal {
        private Quetzal() {}

        public static final int ID = 64;
        public static final int COL_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_COORD = 2;
        public static final int COL_IF_MODEL = 3;
        public static final int COL_IF_X_POS = 4;
        public static final int COL_IF_Y_POS = 5;
        public static final int COL_AUTO_UNLOCKED = 6;
        public static final int COL_NPC = 7;
        public static final int COL_LOC = 8;
    }

    public static final class TorsoStyles {
        private TorsoStyles() {}

        public static final int ID = 65;
        public static final int COL_NAME = 0;
        public static final int COL_PLAYER_KIT_ID_TYPE_A = 1;
        public static final int COL_PLAYER_KIT_ID_TYPE_B = 2;
        public static final int COL_TORSO_MODEL_TYPE_A_1 = 3;
        public static final int COL_TORSO_MODEL_TYPE_A_2 = 4;
        public static final int COL_TORSO_MODEL_TYPE_B_1 = 5;
        public static final int COL_TORSO_MODEL_TYPE_B_2 = 6;

        public static final class Row {
            private Row() {}

            public static final int TORSO_PLAIN = 2359;
            public static final int TORSO_TORN = 2361;
            public static final int TORSO_SWEATER = 2362;
            public static final int TORSO_CUFFED_SHIRT = 2364;
            public static final int TORSO_VEST = 2365;
            public static final int TORSO_TWO_TONED = 2384;
            public static final int TORSO_PRINCELY = 2385;
            public static final int TORSO_RIPPED_WESKIT = 2386;
            public static final int TORSO_TORN_WESKIT = 2387;
            public static final int TORSO_CROP_TOP = 2388;
            public static final int TORSO_POLO_NECK = 2389;
            public static final int TORSO_SIMPLE = 2390;
            public static final int TORSO_FRILLY = 2391;
            public static final int TORSO_CORSETRY = 2392;
            public static final int TORSO_BODICE = 2393;
        }
    }

    public static final class SleeveStyles {
        private SleeveStyles() {}

        public static final int ID = 66;
        public static final int COL_NAME = 0;
        public static final int COL_PLAYER_KIT_ID_TYPE_A = 1;
        public static final int COL_PLAYER_KIT_ID_TYPE_B = 2;
        public static final int COL_SLEEVE_MODEL_TYPE_A = 3;
        public static final int COL_SLEEVE_MODEL_TYPE_B = 4;
    }

    public static final class LeggingStyles {
        private LeggingStyles() {}

        public static final int ID = 67;
        public static final int COL_NAME = 0;
        public static final int COL_PLAYER_KIT_ID_TYPE_A = 1;
        public static final int COL_PLAYER_KIT_ID_TYPE_B = 2;
        public static final int COL_LEGGING_MODEL_TYPE_A = 3;
        public static final int COL_LEGGING_MODEL_TYPE_B = 4;
    }

    public static final class ShoeStyles {
        private ShoeStyles() {}

        public static final int ID = 68;
        public static final int COL_NAME = 0;
        public static final int COL_PLAYER_KIT_ID_TYPE_A = 1;
        public static final int COL_PLAYER_KIT_ID_TYPE_B = 2;
        public static final int COL_SHOE_MODEL_TYPE_A = 3;
        public static final int COL_SHOE_MODEL_TYPE_B = 4;
    }

    public static final class HandStyles {
        private HandStyles() {}

        public static final int ID = 69;
        public static final int COL_NAME = 0;
        public static final int COL_PLAYER_KIT_ID_TYPE_A = 1;
        public static final int COL_PLAYER_KIT_ID_TYPE_B = 2;
        public static final int COL_HAND_MODEL_TYPE_A = 3;
        public static final int COL_HAND_MODEL_TYPE_B = 4;
    }

    public static final class Vmq3TowerTrial3 {
        private Vmq3TowerTrial3() {}

        public static final int ID = 70;
        public static final int COL_SUSPECT_ID = 0;
        public static final int COL_SUSPECT_NPC = 1;
        public static final int COL_NAME = 2;
        public static final int COL_RESPONSE = 3;
        public static final int COL_RESPONSE_WRONG = 4;

        public static final class Row {
            private Row() {}

            public static final int VMQ3_TRIAL_3_SUSPECT_1 = 3702;
            public static final int VMQ3_TRIAL_3_SUSPECT_2 = 3703;
            public static final int VMQ3_TRIAL_3_SUSPECT_3 = 3704;
            public static final int VMQ3_TRIAL_3_SUSPECT_4 = 3705;
        }
    }

    public static final class Vmq3TowerTrial4Cone {
        private Vmq3TowerTrial4Cone() {}

        public static final int ID = 71;
        public static final int COL_DIRECTION = 0;
        public static final int COL_TILE_DATA = 1;

        public static final class Row {
            private Row() {}

            public static final int VMQ3_TRIAL_4_CONE_NORTH = 3706;
            public static final int VMQ3_TRIAL_4_CONE_SOUTH = 3707;
            public static final int VMQ3_TRIAL_4_CONE_EAST = 3708;
            public static final int VMQ3_TRIAL_4_CONE_WEST = 3709;
        }
    }

    public static final class PendantOfAtesTeleports {
        private PendantOfAtesTeleports() {}

        public static final int ID = 72;
        public static final int COL_ID = 0;
        public static final int COL_TELEPORT_NAME = 1;
        public static final int COL_TELEPORT_COORD = 2;
        public static final int COL_TELEPORT_IF_LAYER = 3;
    }

    public static final class EaaShameGame {
        private EaaShameGame() {}

        public static final int ID = 73;
        public static final int COL_ID = 0;
        public static final int COL_OPTION = 1;

        public static final class Row {
            private Row() {}

            public static final int EAA_SHAME_GAME_CHOICE_1 = 3714;
            public static final int EAA_SHAME_GAME_CHOICE_2 = 3715;
            public static final int EAA_SHAME_GAME_CHOICE_3 = 3716;
            public static final int EAA_SHAME_GAME_CHOICE_4 = 3717;
            public static final int EAA_SHAME_GAME_CHOICE_5 = 3718;
            public static final int EAA_SHAME_GAME_CHOICE_6 = 3719;
            public static final int EAA_SHAME_GAME_CHOICE_7 = 3720;
            public static final int EAA_SHAME_GAME_CHOICE_8 = 3721;
            public static final int EAA_SHAME_GAME_CHOICE_9 = 3722;
            public static final int EAA_SHAME_GAME_CHOICE_10 = 3723;
            public static final int EAA_SHAME_GAME_CHOICE_11 = 3724;
            public static final int EAA_SHAME_GAME_CHOICE_12 = 3725;
            public static final int EAA_SHAME_GAME_CHOICE_13 = 3726;
            public static final int EAA_SHAME_GAME_CHOICE_14 = 3727;
            public static final int EAA_SHAME_GAME_CHOICE_15 = 3728;
            public static final int EAA_SHAME_GAME_CHOICE_16 = 3729;
            public static final int EAA_SHAME_GAME_CHOICE_17 = 3730;
            public static final int EAA_SHAME_GAME_CHOICE_18 = 3731;
            public static final int EAA_SHAME_GAME_CHOICE_19 = 3732;
        }
    }

    public static final class VarlamoreWyrmAgilityRoute {
        private VarlamoreWyrmAgilityRoute() {}

        public static final int ID = 74;
        public static final int COL_NAME_LOWERCASE = 0;
        public static final int COL_NAME_CAPITALISED = 1;
        public static final int COL_AGILITY_LEVEL = 2;
        public static final int COL_PET_RATE = 3;
        public static final int COL_COMPLETION_XP = 4;
        public static final int COL_ROUTE_LENGTH = 5;
        public static final int COL_OBSTACLES_ENUM = 6;
        public static final int COL_TERMITE_OBSTACLE_IDS = 7;

        public static final class Row {
            private Row() {}

            public static final int VARLAMORE_WYRM_AGILITY_ROUTE_BASIC = 3762;
            public static final int VARLAMORE_WYRM_AGILITY_ROUTE_ADVANCED = 3763;
        }
    }

    public static final class HueySpecialAttack {
        private HueySpecialAttack() {}

        public static final int ID = 75;
        public static final int COL_PHASE = 0;
        public static final int COL_CD = 1;
        public static final int COL_ATTACKS = 2;

        public static final class Row {
            private Row() {}

            public static final int HUEY_SPECIAL_ATTACK_P1 = 3776;
            public static final int HUEY_SPECIAL_ATTACK_P2 = 3777;
            public static final int HUEY_SPECIAL_ATTACK_P3 = 3778;
            public static final int HUEY_SPECIAL_ATTACK_P4 = 3779;
        }
    }

    public static final class DynamicBuildersDemoSets {
        private DynamicBuildersDemoSets() {}

        public static final int ID = 76;
        public static final int COL_BUTTON_STYLES = 0;
    }

    public static final class PrepotDeviceLoadoutUi {
        private PrepotDeviceLoadoutUi() {}

        public static final int ID = 77;
        public static final int COL_LOADOUT_ID = 0;
        public static final int COL_CONTAINER_COM = 1;
        public static final int COL_CONTENTS_COM = 2;
        public static final int COL_LOAD_BTN_COM = 3;
        public static final int COL_SAVE_BTN_COM = 4;
    }

    public static final class CombatInterfaceWeaponCategory {
        private CombatInterfaceWeaponCategory() {}

        public static final int ID = 78;
        public static final int COL_ID = 0;
        public static final int COL_BUTTON = 1;
    }

    public static final class HiscoresSkillInfo {
        private HiscoresSkillInfo() {}

        public static final int ID = 79;
        public static final int COL_SKILLNAME = 0;
        public static final int COL_SKILLID = 1;
        public static final int COL_SKILLICON = 2;
    }

    public static final class HiscoresActivityInfo {
        private HiscoresActivityInfo() {}

        public static final int ID = 80;
        public static final int COL_ACTIVITYVARP = 0;
        public static final int COL_ACTIVITYNAME = 1;
        public static final int COL_ACTIVITYICON = 2;
    }

    public static final class HiscoresBossesInfo {
        private HiscoresBossesInfo() {}

        public static final int ID = 81;
        public static final int COL_BOSSNAME = 0;
        public static final int COL_BOSSICON = 1;
        public static final int COL_BOSSVARP = 2;
    }

    public static final class RegionData {
        private RegionData() {}

        public static final int ID = 82;
        public static final int COL_REGION_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_MAP_GRAPHIC = 2;
        public static final int COL_MAP_COMPONENT = 3;
        public static final int COL_NAME_COMPONENT = 4;
        public static final int COL_SHIELD_COMPONENT = 5;
        public static final int COL_MAP_SHIELD_SPRITE = 6;
        public static final int COL_MAP_SHIELD_SPRITE_HIGHLIGHTED = 7;
        public static final int COL_MAP_SHIELD_SPRITE_SMALL = 8;
        public static final int COL_MAP_NAME_SPRITE = 9;
        public static final int COL_MAP_NAME_SPRITE_HIGHLIGHTED = 10;
        public static final int COL_MAP_SLIDESHOW = 11;
        public static final int COL_AREA_INFO = 12;
        public static final int COL_AREA_TELEPORT_COORD = 13;

        public static final class Row {
            private Row() {}

            public static final int REGION_KOUREND = 4081;
            public static final int REGION_KEBOS = 4082;
            public static final int REGION_TUTORIAL_ISLAND = 4083;
            public static final int REGION_RANDOM_EVENT = 4084;
            public static final int REGION_POH = 4085;
            public static final int REGION_VOID = 4086;
            public static final int REGION_DEATHS_OFFICE = 4087;
            public static final int REGION_ESSENCE_MINES = 4088;
            public static final int REGION_DORGESHUUN_TRAINSTATION = 4089;
            public static final int REGION_BOAT_TRAVEL_INSTANCE = 4090;
            public static final int REGION_IGNORE = 4227;
            public static final int REGION_OCEANS = 7038;
        }
    }

    public static final class ToggleListInterface {
        private ToggleListInterface() {}

        public static final int ID = 83;
        public static final int COL_TITLE = 0;
        public static final int COL_INFO = 1;
        public static final int COL_DISABLE_ON_LEAGUES = 2;
    }

    public static final class LeaguesEchoBosses {
        private LeaguesEchoBosses() {}

        public static final int ID = 85;
        public static final int COL_ECHO_ORB_DROPRATE = 0;
        public static final int COL_ECHO_ORB_OBJECT = 1;
        public static final int COL_UNIQUE_DROP = 2;
        public static final int COL_UNIQUE_DROP_RATE = 3;
        public static final int COL_CA_DATA = 4;
        public static final int COL_DESCRIPTION = 5;
        public static final int COL_DIFFICULTY = 6;
        public static final int COL_REGION = 7;
        public static final int COL_NAME = 8;
        public static final int COL_REQUIREMENTS = 9;
        public static final int COL_DISABLED = 10;

        public static final class Row {
            private Row() {}

            public static final int LEAGUES_ECHO_CERBERUS = 4218;
            public static final int LEAGUES_ECHO_KALPHITE_QUEEN = 4219;
            public static final int LEAGUES_ECHO_DAGANNOTH_KINGS = 4220;
            public static final int LEAGUES_ECHO_THERMONUCLEAR_SMOKE_DEVIL = 4221;
            public static final int LEAGUES_ECHO_GROTESQUE_GUARDIANS = 4222;
            public static final int LEAGUES_ECHO_HUNLEFF = 4223;
            public static final int LEAGUES_ECHO_KBD = 4224;
            public static final int LEAGUES_ECHO_HESPORI = 4225;
            public static final int LEAGUES_ECHO_SOL_HEREDIT = 4226;
            public static final int LEAGUES_ECHO_AMOXLIATL = 16179;
        }
    }

    public static final class MagicEnchant {
        private MagicEnchant() {}

        public static final int ID = 86;
        public static final int COL_BASE = 0;
        public static final int COL_OUTPUT = 1;
        public static final int COL_SPELL = 2;
        public static final int COL_MEMBERS = 3;
        public static final int COL_CASTXP = 4;
        public static final int COL_ANIM = 5;
        public static final int COL_SPOTANIM = 6;
        public static final int COL_SOUND = 7;
        public static final int COL_SPECIAL = 8;
        public static final int COL_FAILMES = 9;

        public static final class Row {
            private Row() {}

            public static final int MAGIC_ENCHANT_SAPPHIRE_AMULET = 4235;
            public static final int MAGIC_ENCHANT_SAPPHIRE_RING = 4236;
            public static final int MAGIC_ENCHANT_SAPPHIRE_NECKLACE = 4237;
            public static final int MAGIC_ENCHANT_SAPPHIRE_BRACELET = 4238;
            public static final int MAGIC_ENCHANT_OPAL_AMULET = 4239;
            public static final int MAGIC_ENCHANT_OPAL_RING = 4240;
            public static final int MAGIC_ENCHANT_OPAL_NECKLACE = 4241;
            public static final int MAGIC_ENCHANT_OPAL_BRACELET = 4242;
            public static final int MAGIC_ENCHANT_UNSTRUNG_SAPPHIRE_AMULET = 4245;
            public static final int MAGIC_ENCHANT_UNSTRUNG_OPAL_AMULET = 4246;
            public static final int MAGIC_ENCHANT_EMERALD_AMULET = 4247;
            public static final int MAGIC_ENCHANT_PRENATURE_AMULET = 4248;
            public static final int MAGIC_ENCHANT_EMERALD_RING = 4249;
            public static final int MAGIC_ENCHANT_EMERALD_NECKLACE = 4250;
            public static final int MAGIC_ENCHANT_EMERALD_BRACELET = 4251;
            public static final int MAGIC_ENCHANT_JADE_AMULET = 4252;
            public static final int MAGIC_ENCHANT_JADE_RING = 4253;
            public static final int MAGIC_ENCHANT_JADE_NECKLACE = 4254;
            public static final int MAGIC_ENCHANT_JADE_BRACELET = 4255;
            public static final int MAGIC_ENCHANT_UNSTRUNG_EMERALD_AMULET = 4256;
            public static final int MAGIC_ENCHANT_UNSTRUNG_JADE_AMULET = 4257;
            public static final int MAGIC_ENCHANT_SILVER_SICKLE_EMERALD = 4258;
            public static final int MAGIC_ENCHANT_RUBY_AMULET = 4259;
            public static final int MAGIC_ENCHANT_RUBY_RING = 4260;
            public static final int MAGIC_ENCHANT_RUBY_NECKLACE = 4261;
            public static final int MAGIC_ENCHANT_RUBY_BRACELET = 4262;
            public static final int MAGIC_ENCHANT_UNSTRUNG_RUBY_AMULET = 4263;
            public static final int MAGIC_ENCHANT_TOPAZ_AMULET = 4264;
            public static final int MAGIC_ENCHANT_TOPAZ_RING = 4265;
            public static final int MAGIC_ENCHANT_TOPAZ_NECKLACE = 4266;
            public static final int MAGIC_ENCHANT_TOPAZ_BRACELET = 4267;
            public static final int MAGIC_ENCHANT_UNSTRUNG_TOPAZ_AMULET = 4268;
            public static final int MAGIC_ENCHANT_SILVER_SICKLE_RUBY = 4269;
            public static final int MAGIC_ENCHANT_DIAMOND_AMULET = 4270;
            public static final int MAGIC_ENCHANT_DIAMOND_RING = 4271;
            public static final int MAGIC_ENCHANT_DIAMOND_BRACELET = 4272;
            public static final int MAGIC_ENCHANT_DIAMOND_NECKLACE = 4273;
            public static final int MAGIC_ENCHANT_UNSTRUNG_DIAMOND_AMULET = 4274;
            public static final int MAGIC_ENCHANT_DRAGONSTONE_AMULET = 4275;
            public static final int MAGIC_ENCHANT_DRAGONSTONE_RING = 4276;
            public static final int MAGIC_ENCHANT_DRAGONSTONE_NECKLACE = 4277;
            public static final int MAGIC_ENCHANT_DRAGONSTONE_BRACELET = 4278;
            public static final int MAGIC_ENCHANT_UNSTRUNG_DRAGONSTONE_AMULET = 4279;
            public static final int MAGIC_ENCHANT_ONYX_AMULET = 4280;
            public static final int MAGIC_ENCHANT_ONYX_NECKLACE = 4281;
            public static final int MAGIC_ENCHANT_ONYX_BRACELET = 4282;
            public static final int MAGIC_ENCHANT_ONYX_RING = 4283;
            public static final int MAGIC_ENCHANT_UNSTRUNG_ONYX_AMULET = 4284;
            public static final int MAGIC_ENCHANT_ZENYTE_AMULET = 4285;
            public static final int MAGIC_ENCHANT_ZENYTE_NECKLACE = 4286;
            public static final int MAGIC_ENCHANT_ZENYTE_BRACELET = 4287;
            public static final int MAGIC_ENCHANT_ZENYTE_RING = 4288;
            public static final int MAGIC_ENCHANT_UNSTRUNG_ZENYTE_AMULET = 4289;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CYLINDER1 = 4290;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CUBE1 = 4291;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_ICOSAHENDRON1 = 4292;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_PENTAMID1 = 4293;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CYLINDER2 = 4294;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CUBE2 = 4295;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_ICOSAHENDRON2 = 4296;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_PENTAMID2 = 4297;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CYLINDER3 = 4298;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CUBE3 = 4299;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_ICOSAHENDRON3 = 4300;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_PENTAMID3 = 4301;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CYLINDER4 = 4302;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CUBE4 = 4303;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_ICOSAHENDRON4 = 4304;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_PENTAMID4 = 4305;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CYLINDER5 = 4306;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CUBE5 = 4307;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_ICOSAHENDRON5 = 4308;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_PENTAMID5 = 4309;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CYLINDER6 = 4310;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CUBE6 = 4311;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_ICOSAHENDRON6 = 4312;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_PENTAMID6 = 4313;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CYLINDER7 = 4314;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_CUBE7 = 4315;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_ICOSAHENDRON7 = 4316;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_PENTAMID7 = 4317;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_DRAGONSTONE1 = 4319;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_DRAGONSTONE2 = 4320;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_DRAGONSTONE3 = 4321;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_DRAGONSTONE4 = 4322;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_DRAGONSTONE5 = 4323;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_DRAGONSTONE6 = 4324;
            public static final int MAGIC_ENCHANT_MAGICTRAINING_DRAGONSTONE7 = 4325;
            public static final int MAGIC_ENCHANT_SILVTHRILL_ROD = 16934;
            public static final int MAGIC_ENCHANT_SILVTHRILL_ROD2 = 16935;
            public static final int MAGIC_ENCHANT_SILVER_SICKLE_DIAMOND = 16936;
        }
    }

    public static final class Charges {
        private Charges() {}

        public static final int ID = 87;
        public static final int COL_MAX_CHARGES = 0;
        public static final int COL_CHARGE_ITEM_AND_QUANTITY = 1;
        public static final int COL_CHARGES_GRANTED = 2;
        public static final int COL_CHARGES_TYPE = 3;
        public static final int COL_CHARGES_QUANTITY_BITS_AND_VARP = 4;

        public static final class Row {
            private Row() {}

            public static final int CHARGES_TRIDENT_OF_THE_SEAS = 4326;
            public static final int CHARGES_TRIDENT_OF_THE_SEAS_E = 4327;
            public static final int CHARGES_WARPED_SCEPTRE = 4328;
            public static final int CHARGES_TUMEKENS_SHADOW = 4329;
            public static final int CHARGES_TRIDENT_OF_THE_SWAMP = 4330;
            public static final int CHARGES_TRIDENT_OF_THE_SWAMP_E = 4331;
            public static final int CHARGES_TOXIC_BLOWPIPE = 4332;
            public static final int CHARGES_SERPENTINE_HELM = 4333;
            public static final int CHARGES_TOXIC_STAFF_OF_THE_DEAD = 4334;
            public static final int CHARGES_BOW_OF_FAERDHINEN = 4335;
            public static final int CHARGES_BLADE_OF_SAELDOR = 4336;
            public static final int CHARGES_CRYSTAL_ARMOUR = 4337;
            public static final int CHARGES_CRYSTAL_TOOLS = 4338;
            public static final int CHARGES_ARCLIGHT = 4339;
            public static final int CHARGES_BRYOPHYTAS_STAFF = 4340;
            public static final int CHARGES_BONECRUSHER = 4341;
            public static final int CHARGES_ASH_SANCTIFIER = 4342;
            public static final int CHARGES_CIRCLET_OF_WATER = 4343;
            public static final int CHARGES_SOUL_BEARER = 4344;
            public static final int CHARGES_CELESTIAL_RING = 4345;
            public static final int CHARGES_WILDERNESS_WEAPON = 4346;
            public static final int CHARGES_BRACELET_OF_ETHEREUM = 4347;
            public static final int CHARGES_SANGUINESTI_STAFF = 4348;
            public static final int CHARGES_RING_OF_SUFFERING = 4349;
            public static final int CHARGES_XERICS_TALISMAN = 4350;
            public static final int CHARGES_RING_OF_SHADOWS = 4351;
            public static final int CHARGES_VENATOR_BOW = 4352;
            public static final int CHARGES_TONALZTICS_OF_RALOS = 4353;
            public static final int CHARGES_DIZANAS_QUIVER = 4354;
            public static final int CHARGES_ECHO_BOOTS = 4355;
            public static final int CHARGES_AMULET_OF_BLOOD_FURY = 4356;
            public static final int CHARGES_TOME_OF_WATER = 4357;
            public static final int CHARGES_TOME_OF_FIRE = 4358;
            public static final int CHARGES_TOME_OF_FIRE_SEARING = 4359;
            public static final int CHARGES_TOME_OF_EARTH = 4360;
            public static final int CHARGES_CRYSTAL_WEAK = 4361;
            public static final int CHARGES_ALCHEMIST_AMULET = 4362;
            public static final int CHARGES_GIANTSOUL_AMULET = 4363;
            public static final int CHARGES_PENDANT_OF_ATES = 4364;
            public static final int CHARGES_EYE_OF_AYAK_TEARS = 5187;
            public static final int CHARGES_EYE_OF_AYAK_RUNES = 5188;
            public static final int CHARGES_HORN_OF_PLENTY = 7039;
            public static final int CHARGES_SAILORS_AMULET = 7040;
            public static final int CHARGES_COWBELL_AMULET = 9644;
        }
    }

    public static final class Synth {
        private Synth() {}

        public static final int ID = 88;
        public static final int COL_NAME = 0;
        public static final int COL_SUB_MENU = 1;
        public static final int COL_SYNTH = 2;
        public static final int COL_PARENT_DIRECTORY = 3;

        public static final class Row {
            private Row() {}

            public static final int SYNTH_SYNTH = 4365;
            public static final int SYNTH_ANIMATIONREFRESH = 4366;
            public static final int SYNTH_OSMUMTENSFANG = 4367;
            public static final int SYNTH_AREAS = 4368;
            public static final int SYNTH_BRIMSTONEDUNGEONS = 4370;
            public static final int SYNTH_FARMATMOSPHERICS = 4371;
            public static final int SYNTH_HESPORI = 4372;
            public static final int SYNTH_DWARVENCITY = 4375;
            public static final int SYNTH_HUNTERGUILDBAR = 4376;
            public static final int SYNTH_COLOSSEUMREWARDS = 4377;
            public static final int SYNTH_GLAIVEOFRALOS = 4378;
            public static final int SYNTH_NPCCOLOSSEUM = 4379;
            public static final int SYNTH_BOSSCOLOSSI = 4380;
            public static final int SYNTH_COLOSSEUMMODIFIERS = 4381;
            public static final int SYNTH_DOOMSNAIL = 4382;
            public static final int SYNTH_FREMENNIKMAGE = 4383;
            public static final int SYNTH_JAGUARWARRIOR = 4384;
            public static final int SYNTH_JAVELINCOLOSSI = 4385;
            public static final int SYNTH_MANTICORE = 4386;
            public static final int SYNTH_MINOTAUR = 4387;
            public static final int SYNTH_SERPENTSHAMAN = 4388;
            public static final int SYNTH_SHOCKWAVECOLOSSI = 4389;
            public static final int SYNTH_NPCHUNTERGUILDCREATURES = 4390;
            public static final int SYNTH_BLOODNAGUA = 4392;
            public static final int SYNTH_ECLIPSENAGUA = 4393;
            public static final int SYNTH_MOONNAGUA = 4394;
            public static final int SYNTH_UEYATLALLIPROTECTIONSPELL = 4395;
            public static final int SYNTH_PERILOUSMOONS = 4396;
            public static final int SYNTH_QUETZALTRAVEL = 4397;
            public static final int SYNTH_THIEVING = 4398;
            public static final int SYNTH_TWILIGHTSPROMISE = 4399;
            public static final int SYNTH_AMBIENCES = 4401;
            public static final int SYNTH_DEATHONTHEISLE = 4402;
            public static final int SYNTH_AMOXLIATL = 4404;
            public static final int SYNTH_ICENAGUA = 4405;
            public static final int SYNTH_STATUES = 4406;
            public static final int SYNTH_HERBLOREACTIVITY = 4407;
            public static final int SYNTH_HUEYCOATL = 4408;
            public static final int SYNTH_PORCUPINE = 4409;
            public static final int SYNTH_WYRMAGILITY = 4410;
            public static final int SYNTH_ARMOUR = 4411;
            public static final int SYNTH_ADAMMAIL = 4413;
            public static final int SYNTH_ADAMPLATE = 4414;
            public static final int SYNTH_BLACKMAIL = 4416;
            public static final int SYNTH_BLACKPLATE = 4417;
            public static final int SYNTH_BRONZEMAIL = 4419;
            public static final int SYNTH_BRONZEPLATE = 4420;
            public static final int SYNTH_DRAGON = 4421;
            public static final int SYNTH_DRAGONHIDE = 4422;
            public static final int SYNTH_IRONMAIL = 4424;
            public static final int SYNTH_IRONPLATE = 4425;
            public static final int SYNTH_LEATHERARMOUR = 4427;
            public static final int SYNTH_MITHMAIL = 4429;
            public static final int SYNTH_MITHPLATE = 4430;
            public static final int SYNTH_RUNEMAIL = 4432;
            public static final int SYNTH_RUNEPLATE = 4433;
            public static final int SYNTH_STEELMAIL = 4435;
            public static final int SYNTH_STEELPLATE = 4436;
            public static final int SYNTH_STONE = 4437;
            public static final int SYNTH_STONEARMOUR = 4438;
            public static final int SYNTH_WOOD = 4439;
            public static final int SYNTH_ABYSS = 4441;
            public static final int SYNTH_ANIMALS = 4442;
            public static final int SYNTH_BANK = 4443;
            public static final int SYNTH_BATTLEGROUND = 4444;
            public static final int SYNTH_CAVE = 4445;
            public static final int SYNTH_DESERT = 4446;
            public static final int SYNTH_FARMS = 4447;
            public static final int SYNTH_FIRE = 4448;
            public static final int SYNTH_FORESTWOOD = 4449;
            public static final int SYNTH_ICYAREAS = 4450;
            public static final int SYNTH_JUNGLE = 4451;
            public static final int SYNTH_MANUALLABOUR = 4452;
            public static final int SYNTH_MARKET = 4453;
            public static final int SYNTH_MORYTANIA = 4454;
            public static final int SYNTH_PUB = 4455;
            public static final int SYNTH_RATPITS = 4456;
            public static final int SYNTH_SWAMP = 4457;
            public static final int SYNTH_WATER = 4458;
            public static final int SYNTH_WILDERNESS = 4459;
            public static final int SYNTH_WIND = 4460;
            public static final int SYNTH_BOSSES = 4461;
            public static final int SYNTH_ARAXXORMINIONS = 4463;
            public static final int SYNTH_ACIDICMINION = 4464;
            public static final int SYNTH_MIRRORBACKMINION = 4465;
            public static final int SYNTH_CAVEAMBIENCES = 4466;
            public static final int SYNTH_NOXIOUSHALBERDSPECIALATTACK = 4467;
            public static final int SYNTH_RANCOURAMULET = 4468;
            public static final int SYNTH_BRANDAFIREQUEENLOCATTACKS = 4471;
            public static final int SYNTH_ELDRICICEKINGLOCATTACKS = 4473;
            public static final int SYNTH_MINIONS = 4474;
            public static final int SYNTH_THENIGHTMAREOFASHIHAMA = 4475;
            public static final int SYNTH_TELEPORTSCROLL = 4478;
            public static final int SYNTH_DEADMANREBORN2021 = 4479;
            public static final int SYNTH_DOORSANDOPENABLES = 4480;
            public static final int SYNTH_EQUIPSOUNDS = 4481;
            public static final int SYNTH_CHRISTMAS2021 = 4483;
            public static final int SYNTH_EASTER2021 = 4484;
            public static final int SYNTH_EASTER2022 = 4485;
            public static final int SYNTH_EASTER2024 = 4486;
            public static final int SYNTH_HALLOWEEN2021 = 4487;
            public static final int SYNTH_HALLOWEEN2024 = 4488;
            public static final int SYNTH_OSRS10BIRTHDAY = 4489;
            public static final int SYNTH_PRIDE22 = 4490;
            public static final int SYNTH_PRIDE23 = 4491;
            public static final int SYNTH_XMAS2023 = 4492;
            public static final int SYNTH_XMAS2024 = 4493;
            public static final int SYNTH_GRANDEXCHANGE = 4495;
            public static final int SYNTH_LEAGUES = 4496;
            public static final int SYNTH_LEAGUES4 = 4497;
            public static final int SYNTH_LEAGUES2TRAILBLAZER = 4498;
            public static final int SYNTH_CERBERUSECHO = 4500;
            public static final int SYNTH_COMBATMASTERYUISPARKS = 4501;
            public static final int SYNTH_DAGGANOTHECHO = 4502;
            public static final int SYNTH_DEATHANIMOVERIDE = 4503;
            public static final int SYNTH_ECHOMELEEMASTERY = 4504;
            public static final int SYNTH_GODSWORD = 4505;
            public static final int SYNTH_GROTESQUEGUARDIANSECHO = 4506;
            public static final int SYNTH_GUARDIANATTACKMAGIC = 4507;
            public static final int SYNTH_HESPORIECHO = 4508;
            public static final int SYNTH_HOMETELEPORT = 4509;
            public static final int SYNTH_NATURESREPRISAL = 4510;
            public static final int SYNTH_SOLHEREDITECHO = 4511;
            public static final int SYNTH_SPEARANDSHIELD = 4512;
            public static final int SYNTH_THERMONUCLEAR = 4513;
            public static final int SYNTH_THUNDERKHOPESH = 4514;
            public static final int SYNTH_LEAGUESIII = 4515;
            public static final int SYNTH_LEAGUETWISTED = 4516;
            public static final int SYNTH_EVILTWIN = 4518;
            public static final int SYNTH_PATTERNRECOGNITION = 4519;
            public static final int SYNTH_PILLORY = 4520;
            public static final int SYNTH_PINBALL = 4521;
            public static final int SYNTH_ALCHEMY = 4523;
            public static final int SYNTH_ARCEUUSSPELLS = 4524;
            public static final int SYNTH_BIND = 4525;
            public static final int SYNTH_BLOOD = 4526;
            public static final int SYNTH_BONESTOBANANAS = 4527;
            public static final int SYNTH_CHARGEORB = 4528;
            public static final int SYNTH_CONFUSE = 4529;
            public static final int SYNTH_CRUMBLEUNDEAD = 4530;
            public static final int SYNTH_CURSE = 4531;
            public static final int SYNTH_EARTH = 4532;
            public static final int SYNTH_ENCHANT = 4533;
            public static final int SYNTH_ENFEEBLE = 4534;
            public static final int SYNTH_ENTANGLE = 4535;
            public static final int SYNTH_FIRE_DUPE1 = 4536;
            public static final int SYNTH_FORMERLYMAGICALSOUNDS = 4537;
            public static final int SYNTH_HEAL = 4538;
            public static final int SYNTH_ICE = 4539;
            public static final int SYNTH_LUNARSPELLS = 4540;
            public static final int SYNTH_MAGICDART = 4541;
            public static final int SYNTH_SHADOW = 4542;
            public static final int SYNTH_SMOKE = 4543;
            public static final int SYNTH_SNARE = 4544;
            public static final int SYNTH_STUN = 4545;
            public static final int SYNTH_SUMMON = 4546;
            public static final int SYNTH_SUPERHEAT = 4547;
            public static final int SYNTH_TELEGRAB = 4548;
            public static final int SYNTH_TELEPORT = 4549;
            public static final int SYNTH_UNDEADROT = 4550;
            public static final int SYNTH_VULNERABILITY = 4551;
            public static final int SYNTH_WATER_DUPE1 = 4552;
            public static final int SYNTH_WEAKEN = 4553;
            public static final int SYNTH_WIND_DUPE1 = 4554;
            public static final int SYNTH_ZAP = 4555;
            public static final int SYNTH_MINIGAMES = 4556;
            public static final int SYNTH_SHOOTINGSTAR = 4557;
            public static final int SYNTH_LOCS = 4559;
            public static final int SYNTH_NPCS = 4560;
            public static final int SYNTH_AVATARFIREBOSS = 4561;
            public static final int SYNTH_AVATARNATUREBOSS = 4562;
            public static final int SYNTH_MISCSCENERY = 4563;
            public static final int SYNTH_ABBERANTSPECTRE = 4565;
            public static final int SYNTH_ABYSSAL = 4566;
            public static final int SYNTH_ABYSSALCREATURES = 4567;
            public static final int SYNTH_ANIMATED = 4568;
            public static final int SYNTH_BANSHEE = 4569;
            public static final int SYNTH_BASILISK = 4570;
            public static final int SYNTH_BAT = 4571;
            public static final int SYNTH_BEAR = 4572;
            public static final int SYNTH_BEARMAN = 4573;
            public static final int SYNTH_BIRD = 4574;
            public static final int SYNTH_BLOODVELD = 4575;
            public static final int SYNTH_BONEGUARD = 4576;
            public static final int SYNTH_BOULDER = 4577;
            public static final int SYNTH_BRAWLER = 4578;
            public static final int SYNTH_CAMEL = 4579;
            public static final int SYNTH_CATABLEPON = 4580;
            public static final int SYNTH_CATSANDKITTENS = 4581;
            public static final int SYNTH_CAVECRAWLER = 4582;
            public static final int SYNTH_CHAOSBEAST = 4583;
            public static final int SYNTH_CHICKEN = 4584;
            public static final int SYNTH_CHINCHOMPA = 4585;
            public static final int SYNTH_COCKATRICE = 4586;
            public static final int SYNTH_CORPOREALBEAST = 4587;
            public static final int SYNTH_COSMICENTITY = 4588;
            public static final int SYNTH_COW = 4589;
            public static final int SYNTH_CRAB = 4590;
            public static final int SYNTH_CRAWLINGHAND = 4591;
            public static final int SYNTH_CREEPER = 4592;
            public static final int SYNTH_CROCODILE = 4593;
            public static final int SYNTH_DARKBEAST = 4594;
            public static final int SYNTH_DEFILER = 4595;
            public static final int SYNTH_DEMON = 4596;
            public static final int SYNTH_DOGS = 4597;
            public static final int SYNTH_DRAGON_DUPE1 = 4598;
            public static final int SYNTH_DUCK = 4599;
            public static final int SYNTH_DUSTDEVIL = 4600;
            public static final int SYNTH_DWARF = 4601;
            public static final int SYNTH_EAGLE = 4602;
            public static final int SYNTH_ELF = 4603;
            public static final int SYNTH_FAIRY = 4604;
            public static final int SYNTH_GARGOYLE = 4605;
            public static final int SYNTH_GHAST = 4606;
            public static final int SYNTH_GHOST = 4607;
            public static final int SYNTH_GHOUL = 4608;
            public static final int SYNTH_GIANT = 4609;
            public static final int SYNTH_GIANTCAVEBUG = 4610;
            public static final int SYNTH_GNOME = 4611;
            public static final int SYNTH_GNOMETORTOISE = 4612;
            public static final int SYNTH_GOBLIN = 4613;
            public static final int SYNTH_GOLEM = 4614;
            public static final int SYNTH_GORAK = 4615;
            public static final int SYNTH_GORILLA = 4616;
            public static final int SYNTH_GROWLS = 4617;
            public static final int SYNTH_HALFWEREWOLF = 4618;
            public static final int SYNTH_HARPIEBUGSWARM = 4619;
            public static final int SYNTH_HOPELESSCREATURE = 4620;
            public static final int SYNTH_HORRORS = 4621;
            public static final int SYNTH_HUMAN = 4622;
            public static final int SYNTH_HUNTINGBEAST = 4623;
            public static final int SYNTH_ICELORD = 4624;
            public static final int SYNTH_ICEWARRIOR = 4625;
            public static final int SYNTH_ICEFIEND = 4626;
            public static final int SYNTH_IMP = 4627;
            public static final int SYNTH_INSECT = 4628;
            public static final int SYNTH_JACKAL = 4629;
            public static final int SYNTH_JELLY = 4630;
            public static final int SYNTH_KALPHITE = 4631;
            public static final int SYNTH_KILLERWATT = 4632;
            public static final int SYNTH_KINGBLACKDRAGON = 4633;
            public static final int SYNTH_KURASK = 4634;
            public static final int SYNTH_LAVA = 4635;
            public static final int SYNTH_LAVABEAST = 4636;
            public static final int SYNTH_LEECH = 4637;
            public static final int SYNTH_LIZARD = 4638;
            public static final int SYNTH_LIZARDMAN = 4639;
            public static final int SYNTH_LOCUST = 4640;
            public static final int SYNTH_MAMMOTH = 4641;
            public static final int SYNTH_MINOTAUR_DUPE1 = 4642;
            public static final int SYNTH_MOGRE = 4643;
            public static final int SYNTH_MOLANISK = 4644;
            public static final int SYNTH_MONKEY = 4645;
            public static final int SYNTH_MOSQUITO = 4646;
            public static final int SYNTH_MUMMY = 4647;
            public static final int SYNTH_NECHRAYEL = 4648;
            public static final int SYNTH_NEX = 4649;
            public static final int SYNTH_ORC = 4650;
            public static final int SYNTH_OTHER_DUPE1 = 4651;
            public static final int SYNTH_OTHERWORLD = 4652;
            public static final int SYNTH_PANTHER = 4653;
            public static final int SYNTH_PENANCE = 4654;
            public static final int SYNTH_PENGUIN = 4655;
            public static final int SYNTH_PHEASANT = 4656;
            public static final int SYNTH_PIG = 4657;
            public static final int SYNTH_PYREFIEND = 4658;
            public static final int SYNTH_RABBIT = 4659;
            public static final int SYNTH_RAT = 4660;
            public static final int SYNTH_RATBOSS = 4661;
            public static final int SYNTH_RAVAGER = 4662;
            public static final int SYNTH_ROC = 4663;
            public static final int SYNTH_ROCKCRAB = 4664;
            public static final int SYNTH_ROCKLOBSTER = 4665;
            public static final int SYNTH_ROCKSLUG = 4666;
            public static final int SYNTH_SALAMANDER = 4667;
            public static final int SYNTH_SCARABBOSS = 4668;
            public static final int SYNTH_SCORPION = 4669;
            public static final int SYNTH_SHADE = 4670;
            public static final int SYNTH_SHADOWBEAST = 4671;
            public static final int SYNTH_SHARK = 4672;
            public static final int SYNTH_SHEEP = 4673;
            public static final int SYNTH_SHIFTER = 4674;
            public static final int SYNTH_SKELETALWYVERN = 4675;
            public static final int SYNTH_SKELETON = 4676;
            public static final int SYNTH_SLAGILITH = 4677;
            public static final int SYNTH_SLIME = 4678;
            public static final int SYNTH_SNAIL = 4679;
            public static final int SYNTH_SNAKE = 4680;
            public static final int SYNTH_SPHINX = 4681;
            public static final int SYNTH_SPIDER = 4682;
            public static final int SYNTH_SPINNER = 4683;
            public static final int SYNTH_SPLATTER = 4684;
            public static final int SYNTH_STAG = 4685;
            public static final int SYNTH_SUKQA = 4686;
            public static final int SYNTH_SWARM = 4687;
            public static final int SYNTH_TANGLEFOOT = 4688;
            public static final int SYNTH_TERRORBIRD = 4689;
            public static final int SYNTH_TIGER = 4690;
            public static final int SYNTH_TOAD = 4691;
            public static final int SYNTH_TORCHER = 4692;
            public static final int SYNTH_TOWNCRIER = 4693;
            public static final int SYNTH_TROLL = 4694;
            public static final int SYNTH_TUROTH = 4695;
            public static final int SYNTH_TZHAAR = 4696;
            public static final int SYNTH_UNICORN = 4697;
            public static final int SYNTH_VAMPIRE = 4698;
            public static final int SYNTH_VULTURE = 4699;
            public static final int SYNTH_WALLBEAST = 4700;
            public static final int SYNTH_WATERCREATURE = 4701;
            public static final int SYNTH_WATERFIEND = 4702;
            public static final int SYNTH_WIZARD = 4703;
            public static final int SYNTH_WOLF = 4704;
            public static final int SYNTH_ZOGRE = 4705;
            public static final int SYNTH_ZOMBIE = 4706;
            public static final int SYNTH_OTHER = 4707;
            public static final int SYNTH_POH = 4708;
            public static final int SYNTH_QUEST = 4709;
            public static final int SYNTH_AMIKVARZE = 4711;
            public static final int SYNTH_DWARF_DUPE1 = 4712;
            public static final int SYNTH_GOBLIN_DUPE1 = 4713;
            public static final int SYNTH_GUIDE = 4714;
            public static final int SYNTH_JUBBLY = 4715;
            public static final int SYNTH_MONKEYS = 4716;
            public static final int SYNTH_PIRATE = 4717;
            public static final int SYNTH_APORCINEOFINTEREST = 4718;
            public static final int SYNTH_AGILITYARENA = 4719;
            public static final int SYNTH_AGILITYPYRAMID = 4720;
            public static final int SYNTH_ANIMALMAGNETISM = 4721;
            public static final int SYNTH_ANOTHERSLICEOFHAM = 4722;
            public static final int SYNTH_AREARIMMINGTON = 4723;
            public static final int SYNTH_AREATASKSJUNGLE = 4724;
            public static final int SYNTH_AKINGDOMDIVIDED = 4725;
            public static final int SYNTH_BANKPIN = 4726;
            public static final int SYNTH_BARBARIANASSAULT = 4727;
            public static final int SYNTH_BELOWICEMOUNTAIN = 4728;
            public static final int SYNTH_BENEATHCURSEDSANDS = 4729;
            public static final int SYNTH_BETWEENAROCK = 4730;
            public static final int SYNTH_BLACKKINGHTSFORTRESS = 4731;
            public static final int SYNTH_BLASTFURNACE = 4732;
            public static final int SYNTH_BRUTALSKILLS = 4733;
            public static final int SYNTH_CABINFEVER = 4734;
            public static final int SYNTH_CASTLEWARS = 4735;
            public static final int SYNTH_CHOMPYBIRD = 4736;
            public static final int SYNTH_COAELIASBINDSPELL = 4739;
            public static final int SYNTH_COAROCKGOLEM = 4740;
            public static final int SYNTH_COLDWAR = 4741;
            public static final int SYNTH_CONTACT = 4742;
            public static final int SYNTH_COX = 4743;
            public static final int SYNTH_CREATUREOFFENKENSTRAIN = 4744;
            public static final int SYNTH_DAGGANOTHDUNGEON = 4745;
            public static final int SYNTH_DEATHPLATEAU = 4746;
            public static final int SYNTH_DEATHTOTHEDORGESHUN = 4747;
            public static final int SYNTH_DEMONSLAYERQIP = 4748;
            public static final int SYNTH_DESERTTREASURE = 4749;
            public static final int SYNTH_DESERTTREASUREII = 4750;
            public static final int SYNTH_DUKEBOSS = 4751;
            public static final int SYNTH_LEVIATHANBOSS = 4752;
            public static final int SYNTH_STRANGLEWOODNPC = 4753;
            public static final int SYNTH_VARDORVISBOSS = 4754;
            public static final int SYNTH_WEAPONREWARD = 4755;
            public static final int SYNTH_WHISPERERBOSS = 4756;
            public static final int SYNTH_DEVIOUSMINDS = 4757;
            public static final int SYNTH_DIGSITE = 4758;
            public static final int SYNTH_DORGESHUNCITY = 4759;
            public static final int SYNTH_DOV = 4760;
            public static final int SYNTH_DRAGONSLAYERQIP = 4761;
            public static final int SYNTH_DRAGONSLAYER2 = 4762;
            public static final int SYNTH_DREAMMENTOR = 4763;
            public static final int SYNTH_DRUIDICSPIRIT = 4764;
            public static final int SYNTH_EADGARSRUSE = 4765;
            public static final int SYNTH_EAGLEPEAK = 4766;
            public static final int SYNTH_EASTER2006 = 4767;
            public static final int SYNTH_ELEMENTALWORKSHOP = 4768;
            public static final int SYNTH_ELEMENTALWORKSHOP2 = 4769;
            public static final int SYNTH_ENAKHRASLAMENT = 4770;
            public static final int SYNTH_ERNESTTHECHICKEN = 4771;
            public static final int SYNTH_EYESOFGLOUPHRIE = 4772;
            public static final int SYNTH_FAIRYTALE = 4773;
            public static final int SYNTH_FORGETTABLETALE = 4774;
            public static final int SYNTH_FREMMENIKEXILES = 4775;
            public static final int SYNTH_FREMMENIKISLES = 4776;
            public static final int SYNTH_GAMESROOM = 4777;
            public static final int SYNTH_GARDENOFTRANQUILITY = 4778;
            public static final int SYNTH_GENERALSSHADOW = 4779;
            public static final int SYNTH_GHOSTSAHOY = 4780;
            public static final int SYNTH_GIANTDWARF = 4781;
            public static final int SYNTH_GNOMEBALL = 4782;
            public static final int SYNTH_GOBLINCAVES = 4783;
            public static final int SYNTH_GODWARS = 4784;
            public static final int SYNTH_GRANDTREE = 4785;
            public static final int SYNTH_GREATBRAINROBBERY = 4786;
            public static final int SYNTH_GRIMTALES = 4787;
            public static final int SYNTH_GUARDIANSOFTHERIFT = 4788;
            public static final int SYNTH_HALLOWEEN2006 = 4789;
            public static final int SYNTH_HANDINTHESAND = 4790;
            public static final int SYNTH_HAUNTEDMINE = 4791;
            public static final int SYNTH_HOLYGRAIL = 4792;
            public static final int SYNTH_HORRORFROMTHEDEEP = 4793;
            public static final int SYNTH_ICSLITTLEHELPER = 4794;
            public static final int SYNTH_IKOV = 4795;
            public static final int SYNTH_IMPCATCHER = 4796;
            public static final int SYNTH_IMPETUOUSIMPULSES = 4797;
            public static final int SYNTH_INSEARCHOFMYREQUE = 4798;
            public static final int SYNTH_KINGSRANSOM = 4799;
            public static final int SYNTH_LEADZEPPLIN = 4800;
            public static final int SYNTH_LEGENDARYSWORD = 4801;
            public static final int SYNTH_LEGENDSGUILD = 4802;
            public static final int SYNTH_LOTG = 4803;
            public static final int SYNTH_MADMOLE = 4804;
            public static final int SYNTH_MAGEARENA = 4805;
            public static final int SYNTH_MAGETRAININGAREA = 4806;
            public static final int SYNTH_MAGICCARPET = 4807;
            public static final int SYNTH_MAKINGHISTORY = 4808;
            public static final int SYNTH_MCANNON = 4809;
            public static final int SYNTH_MONKEYMADNESS = 4810;
            public static final int SYNTH_MOONCLAN = 4811;
            public static final int SYNTH_MOUNTAINDAUGHTER = 4812;
            public static final int SYNTH_MOURNINGSENDS = 4813;
            public static final int SYNTH_MYARM = 4814;
            public static final int SYNTH_MYREQUE2 = 4815;
            public static final int SYNTH_MYREQUE3 = 4816;
            public static final int SYNTH_MYREQUE4 = 4817;
            public static final int SYNTH_MYREQUE5 = 4818;
            public static final int SYNTH_NTK = 4819;
            public static final int SYNTH_OBSERVATORY = 4820;
            public static final int SYNTH_ONESMALLFAVOUR = 4821;
            public static final int SYNTH_OSMANSGARDEN = 4822;
            public static final int SYNTH_PESTCONTROL = 4823;
            public static final int SYNTH_PLAGUECITY = 4824;
            public static final int SYNTH_POG = 4825;
            public static final int SYNTH_PRIESTINPERIL = 4826;
            public static final int SYNTH_RAGANDBONEMAN = 4827;
            public static final int SYNTH_RAMBLE = 4828;
            public static final int SYNTH_RATPITS_DUPE1 = 4829;
            public static final int SYNTH_RECRUITMENTDRIVE = 4830;
            public static final int SYNTH_RESTLESSGHOST = 4831;
            public static final int SYNTH_ROGUETRADER = 4832;
            public static final int SYNTH_ROGUESDEN = 4833;
            public static final int SYNTH_ROVINGELVES = 4834;
            public static final int SYNTH_ROYALTROUBLE = 4835;
            public static final int SYNTH_RUMDEAL = 4836;
            public static final int SYNTH_SEASLUGQIP = 4837;
            public static final int SYNTH_SHADESOFMORTTON = 4838;
            public static final int SYNTH_SLEEPINGGIANTS = 4839;
            public static final int SYNTH_SLUGMENACE = 4840;
            public static final int SYNTH_SOS = 4841;
            public static final int SYNTH_SOTE = 4842;
            public static final int SYNTH_SOTN = 4843;
            public static final int SYNTH_SOULSBANE = 4844;
            public static final int SYNTH_SPIRTSOFELID = 4845;
            public static final int SYNTH_SUROK = 4846;
            public static final int SYNTH_SVENTHEHELMSMAN = 4847;
            public static final int SYNTH_SWANSONG = 4848;
            public static final int SYNTH_TBWT = 4849;
            public static final int SYNTH_TBWTCLEANUP = 4850;
            public static final int SYNTH_TEARSOFGUTHIX = 4851;
            public static final int SYNTH_TEMPLEOFLIGHT = 4852;
            public static final int SYNTH_TEMPLETREKKING = 4853;
            public static final int SYNTH_THEFEUD = 4854;
            public static final int SYNTH_THEGOLEM = 4855;
            public static final int SYNTH_THELOSTTRIBE = 4856;
            public static final int SYNTH_THRONEOFMISCELLANIA = 4857;
            public static final int SYNTH_TOB = 4858;
            public static final int SYNTH_TOTE = 4859;
            public static final int SYNTH_TOURISTTRAPQIP = 4860;
            public static final int SYNTH_TOWEROFLIFE = 4861;
            public static final int SYNTH_TRADEFLOOR = 4862;
            public static final int SYNTH_TRAWLER = 4863;
            public static final int SYNTH_TREASUREHUNT = 4864;
            public static final int SYNTH_TREEVILLAGE = 4865;
            public static final int SYNTH_TROLLRESCUE = 4866;
            public static final int SYNTH_TROLLROMANCE = 4867;
            public static final int SYNTH_TROUBLEBREWING = 4868;
            public static final int SYNTH_TWOCATS = 4869;
            public static final int SYNTH_UNDEADBARROWS = 4870;
            public static final int SYNTH_UNDEADDUNGEONLOTR = 4871;
            public static final int SYNTH_UNDERGROUNDPASS = 4872;
            public static final int SYNTH_UNSUMMONING = 4873;
            public static final int SYNTH_UPASS4 = 4874;
            public static final int SYNTH_VAMPIREWARRIOR = 4875;
            public static final int SYNTH_VARROCKMUSEUM = 4876;
            public static final int SYNTH_WANTED = 4877;
            public static final int SYNTH_WARRIORSGUILD = 4878;
            public static final int SYNTH_WATCHTOWER = 4879;
            public static final int SYNTH_WEREWOLFAGILITYAREA = 4880;
            public static final int SYNTH_WGS2024 = 4882;
            public static final int SYNTH_BALANCEELEMENETALDEATH2024 = 4883;
            public static final int SYNTH_BALANCEELEMENETALSPAWN2024 = 4884;
            public static final int SYNTH_BALANCEELEMENTAL = 4885;
            public static final int SYNTH_HAZELMERE = 4886;
            public static final int SYNTH_LUCIEN2024 = 4887;
            public static final int SYNTH_TORMENTEDDEMONS = 4888;
            public static final int SYNTH_WEAPONREWARDS = 4889;
            public static final int SYNTH_BURNINGCLAWS = 4890;
            public static final int SYNTH_EMBERLIGHT = 4891;
            public static final int SYNTH_IGNITEDSTAFF = 4892;
            public static final int SYNTH_SCORCHINGBOW = 4893;
            public static final int SYNTH_WISEOLDMAN = 4894;
            public static final int SYNTH_XMAS2006EVILGUBLINCH = 4895;
            public static final int SYNTH_ZOGREFLESHEATERS = 4896;
            public static final int SYNTH_ZYGOMITES = 4897;
            public static final int SYNTH_RAIDS = 4898;
            public static final int SYNTH_TOMBSOFAMASCUT = 4899;
            public static final int SYNTH_AKKABOSS = 4900;
            public static final int SYNTH_AMBIENCE = 4901;
            public static final int SYNTH_AMPEKEN = 4902;
            public static final int SYNTH_BABABOSS = 4903;
            public static final int SYNTH_KEPHRIBOSS = 4904;
            public static final int SYNTH_LOOT = 4905;
            public static final int SYNTH_PATHOFCRONDIS = 4906;
            public static final int SYNTH_PATHOFHET = 4907;
            public static final int SYNTH_PATHOFSCABARAS = 4908;
            public static final int SYNTH_SFX = 4909;
            public static final int SYNTH_UI = 4910;
            public static final int SYNTH_WARDENSBOSS = 4911;
            public static final int SYNTH_ZABAKBOSS = 4912;
            public static final int SYNTH_SHIELDS = 4913;
            public static final int SYNTH_ADAMANTITE_DUPE1 = 4914;
            public static final int SYNTH_BLACK_DUPE1 = 4915;
            public static final int SYNTH_BRONZE_DUPE1 = 4916;
            public static final int SYNTH_DRAGON_DUPE2 = 4917;
            public static final int SYNTH_IRON_DUPE1 = 4918;
            public static final int SYNTH_MITHRIL_DUPE1 = 4919;
            public static final int SYNTH_RUNE_DUPE1 = 4920;
            public static final int SYNTH_STEEL_DUPE1 = 4921;
            public static final int SYNTH_STONE_DUPE1 = 4922;
            public static final int SYNTH_WOOD_DUPE1 = 4923;
            public static final int SYNTH_AGILITY = 4925;
            public static final int SYNTH_ANIMALLORE = 4926;
            public static final int SYNTH_COMBAT = 4927;
            public static final int SYNTH_BATTLEAXE = 4928;
            public static final int SYNTH_COMBATEFFECTS = 4929;
            public static final int SYNTH_HACKSWORD = 4930;
            public static final int SYNTH_HEAVYSWORD = 4931;
            public static final int SYNTH_KERIS = 4932;
            public static final int SYNTH_MACE = 4933;
            public static final int SYNTH_METALS = 4934;
            public static final int SYNTH_OTHERS = 4935;
            public static final int SYNTH_SCYTHE = 4936;
            public static final int SYNTH_SICKLE = 4937;
            public static final int SYNTH_SPECIALATTACKS = 4938;
            public static final int SYNTH_STABSWORD = 4939;
            public static final int SYNTH_STAFF = 4940;
            public static final int SYNTH_UNARMED = 4941;
            public static final int SYNTH_WARHAMMER = 4942;
            public static final int SYNTH_COOKING = 4943;
            public static final int SYNTH_CRAFTING = 4944;
            public static final int SYNTH_FARMING = 4945;
            public static final int SYNTH_FIREMAKING = 4946;
            public static final int SYNTH_FISHINGSKILLBOSS = 4948;
            public static final int SYNTH_FLETCHING = 4949;
            public static final int SYNTH_HERBLORE = 4950;
            public static final int SYNTH_HUNTING = 4951;
            public static final int SYNTH_MINING = 4952;
            public static final int SYNTH_PRAYER = 4953;
            public static final int SYNTH_RANGING = 4954;
            public static final int SYNTH_RUNECRAFTING = 4955;
            public static final int SYNTH_CARGOHANDLING = 4957;
            public static final int SYNTH_DISEMBARKING = 4958;
            public static final int SYNTH_DOCKING = 4959;
            public static final int SYNTH_JOBBOARD = 4960;
            public static final int SYNTH_LUFFING = 4961;
            public static final int SYNTH_NAVIGATION = 4962;
            public static final int SYNTH_SALVAGING = 4971;
            public static final int SYNTH_SMITHING = 4973;
            public static final int SYNTH_THIEVING_DUPE1 = 4974;
            public static final int SYNTH_WOTF = 4976;
            public static final int SYNTH_SPEEDRUNNING = 4977;
            public static final int SYNTH_WEAPONS = 4978;
            public static final int SYNTH_VETION = 4980;
            public static final int SYNTH_ARKANBLADE = 16327;
            public static final int SYNTH_CUSTODIANSTALKERS = 16328;
            public static final int SYNTH_REWARDS = 16330;
            public static final int SYNTH_DUOCULTISTSCOMBAT = 16331;
            public static final int SYNTH_EARTHNAGUA = 16332;
            public static final int SYNTH_ENNIUSCOMBAT = 16333;
            public static final int SYNTH_ENVIRONMENTS = 16334;
            public static final int SYNTH_MOKHAIOTLDUNGEON = 16335;
            public static final int SYNTH_FINALDAWN = 16336;
            public static final int SYNTH_FLETCHINGACTIVITY = 16337;
            public static final int SYNTH_GEMSTONECRAB = 16338;
            public static final int SYNTH_GUARDCOMBAT = 16339;
            public static final int SYNTH_METZLICOMBAT = 16340;
            public static final int SYNTH_SCRAMBLEDQUEST = 16341;
            public static final int SYNTH_ANIMALS_DUPE1 = 16342;
            public static final int SYNTH_BRUTUSCOWBOSS = 16343;
            public static final int SYNTH_YAMA = 16344;
            public static final int SYNTH_DEADMAN2026 = 16345;
            public static final int SYNTH_EASTER2025 = 16346;
            public static final int SYNTH_HALLOWEEN2025 = 16347;
            public static final int SYNTH_LEAGUES6 = 16348;
            public static final int SYNTH_REDREEF = 16349;
            public static final int SYNTH_TCS = 16350;
            public static final int SYNTH_ANCHOR = 16351;
            public static final int SYNTH_BARRACUDATRAILS = 16352;
            public static final int SYNTH_CANNONS = 16353;
            public static final int SYNTH_CREATURESMISC = 16354;
            public static final int SYNTH_CUSTOMISING = 16355;
            public static final int SYNTH_DEEPSEATRAWLING = 16356;
            public static final int SYNTH_GRYPHON = 16357;
            public static final int SYNTH_HORNOFPLENTY = 16358;
            public static final int SYNTH_KANDARINRAFT = 16359;
            public static final int SYNTH_LOOTNET = 16360;
            public static final int SYNTH_MERMAID = 16361;
            public static final int SYNTH_MOGRE_DUPE1 = 16362;
            public static final int SYNTH_MONODON = 16363;
            public static final int SYNTH_ARDENTOCEAN = 16365;
            public static final int SYNTH_HAZARDS = 16366;
            public static final int SYNTH_HAZARDFETIDWATERS = 16367;
            public static final int SYNTH_HAZARDFLECKEDWATERS = 16368;
            public static final int SYNTH_HAZARDPROFANEWATERS = 16369;
            public static final int SYNTH_HAZARDSTORMYSEAS = 16370;
            public static final int SYNTH_HAZARDSUNBAKEDSEAS = 16371;
            public static final int SYNTH_HAZARDTANGLEDKELP = 16372;
            public static final int SYNTH_NORTHERNOCEAN = 16373;
            public static final int SYNTH_SUNSETOCEAN = 16374;
            public static final int SYNTH_UNQUIETOCEAN = 16375;
            public static final int SYNTH_SAILINGCOMBATREBALANCE = 16376;
            public static final int SYNTH_SAILMOVEMENT = 16377;
            public static final int SYNTH_SHARK_DUPE1 = 16378;
            public static final int SYNTH_SHIPWRECK = 16379;
            public static final int SYNTH_SKILLCAPEEMOTE = 16380;
            public static final int SYNTH_TORTUGAN = 16381;
            public static final int SYNTH_TRAWLING = 16382;
            public static final int SYNTH_WINDCATCHER = 16383;
            public static final int SYNTH_AQUANITES = 16384;
            public static final int SYNTH_STRYKEWYRMS = 16385;
        }
    }

    public static final class Fairyring {
        private Fairyring() {}

        public static final int ID = 89;
        public static final int COL_ID = 0;
        public static final int COL_MULTILOC_STATE = 1;
        public static final int COL_DEST_COORD = 2;
        public static final int COL_CODE = 3;
        public static final int COL_TEXT_COMPONENT = 4;
        public static final int COL_FAVE_ICON_COMPONENT = 5;
        public static final int COL_MAPELEMENT = 6;
        public static final int COL_MAPELEMENT_TOOLTIP = 7;
        public static final int COL_DESC = 8;
        public static final int COL_APPARITION_NPC = 9;
        public static final int COL_SHOW_APPARITION = 10;
        public static final int COL_NO_STAFF_RETURN = 11;

        public static final class Row {
            private Row() {}

            public static final int FAIRYRINGS_AIP = 3786;
            public static final int FAIRYRINGS_AIS = 3787;
            public static final int FAIRYRINGS_AIR = 3788;
            public static final int FAIRYRINGS_AIQ = 3789;
            public static final int FAIRYRINGS_ALP = 3790;
            public static final int FAIRYRINGS_ALS = 3791;
            public static final int FAIRYRINGS_ALR = 3792;
            public static final int FAIRYRINGS_ALQ = 3793;
            public static final int FAIRYRINGS_AKP = 3794;
            public static final int FAIRYRINGS_AKS = 3795;
            public static final int FAIRYRINGS_AKR = 3796;
            public static final int FAIRYRINGS_AKQ = 3797;
            public static final int FAIRYRINGS_AJP = 3798;
            public static final int FAIRYRINGS_AJS = 3799;
            public static final int FAIRYRINGS_AJR = 3800;
            public static final int FAIRYRINGS_AJQ = 3801;
            public static final int FAIRYRINGS_DIS = 3803;
            public static final int FAIRYRINGS_DIR = 3804;
            public static final int FAIRYRINGS_DIQ = 3805;
            public static final int FAIRYRINGS_DLP = 3806;
            public static final int FAIRYRINGS_DLS = 3807;
            public static final int FAIRYRINGS_DLR = 3808;
            public static final int FAIRYRINGS_DLQ = 3809;
            public static final int FAIRYRINGS_DKP = 4103;
            public static final int FAIRYRINGS_DKS = 4104;
            public static final int FAIRYRINGS_DKR = 4105;
            public static final int FAIRYRINGS_DKQ = 4106;
            public static final int FAIRYRINGS_DJP = 4107;
            public static final int FAIRYRINGS_DJS = 4108;
            public static final int FAIRYRINGS_DJR = 4109;
            public static final int FAIRYRINGS_DJQ = 4110;
            public static final int FAIRYRINGS_CIP = 4111;
            public static final int FAIRYRINGS_CIS = 4112;
            public static final int FAIRYRINGS_CIR = 4113;
            public static final int FAIRYRINGS_CIQ = 4114;
            public static final int FAIRYRINGS_CLP = 4115;
            public static final int FAIRYRINGS_CLS = 4116;
            public static final int FAIRYRINGS_CLR = 4117;
            public static final int FAIRYRINGS_CLQ = 4118;
            public static final int FAIRYRINGS_CKP = 4119;
            public static final int FAIRYRINGS_CKS = 4120;
            public static final int FAIRYRINGS_CKR = 4121;
            public static final int FAIRYRINGS_CKQ = 4122;
            public static final int FAIRYRINGS_CJP = 4123;
            public static final int FAIRYRINGS_CJS = 4124;
            public static final int FAIRYRINGS_CJR = 4125;
            public static final int FAIRYRINGS_CJQ = 4126;
            public static final int FAIRYRINGS_BIP = 4127;
            public static final int FAIRYRINGS_BIS = 4128;
            public static final int FAIRYRINGS_BIR = 4129;
            public static final int FAIRYRINGS_BIQ = 4130;
            public static final int FAIRYRINGS_BLP = 4131;
            public static final int FAIRYRINGS_BLS = 4132;
            public static final int FAIRYRINGS_BLR = 4133;
            public static final int FAIRYRINGS_BLQ = 4134;
            public static final int FAIRYRINGS_BKP = 4135;
            public static final int FAIRYRINGS_BKS = 4136;
            public static final int FAIRYRINGS_BKR = 4137;
            public static final int FAIRYRINGS_BKQ = 4138;
            public static final int FAIRYRINGS_BJP = 4139;
            public static final int FAIRYRINGS_BJS = 4140;
            public static final int FAIRYRINGS_BJR = 4141;
            public static final int FAIRYRINGS_BJQ = 4142;
        }
    }

    public static final class Didyouknow {
        private Didyouknow() {}

        public static final int ID = 90;
        public static final int COL_ISTIP = 0;
        public static final int COL_TIP = 1;
        public static final int COL_MEMBERSONLY = 2;
        public static final int COL_ONMOBILE = 3;
        public static final int COL_MOBILEONLY = 4;
        public static final int COL_EXTRAREQ = 5;

        public static final class Row {
            private Row() {}

            public static final int DIDYOUKNOW_BANKHELP = 5108;
            public static final int DIDYOUKNOW_CERTPRODUCE = 5109;
            public static final int DIDYOUKNOW_FAIRYRING_DESKTOP = 5110;
            public static final int DIDYOUKNOW_FAIRYRING_MOBILE = 5111;
            public static final int DIDYOUKNOW_PHIALS_F2P = 5112;
            public static final int DIDYOUKNOW_PHIALS = 5113;
            public static final int DIDYOUKNOW_ROOFS = 5114;
            public static final int DIDYOUKNOW_LOCKSLOT = 5115;
            public static final int DIDYOUKNOW_TICKEAT = 5116;
            public static final int DIDYOUKNOW_VIYELDI_CAVE_BARRELS = 5117;
            public static final int DIDYOUKNOW_VIRTUAL_LEVELLING = 5118;
            public static final int DIDYOUKNOW_PIZZAHALVES = 5119;
            public static final int DIDYOUKNOW_KEBABCOMPOST = 5120;
            public static final int DIDYOUKNOW_POISONCURE2_DESKTOP = 5123;
            public static final int DIDYOUKNOW_POISONCURE2_MOBILE = 5124;
            public static final int DIDYOUKNOW_COMBINE_JEWELLERY = 5125;
            public static final int DIDYOUKNOW_COMBINE_POTIONS = 5126;
            public static final int DIDYOUKNOW_HUMIDIFY = 5127;
            public static final int DIDYOUKNOW_TEMPOROSS_LEAVE_DESKTOP = 5128;
            public static final int DIDYOUKNOW_TEMPOROSS_LEAVE_MOBILE = 5129;
            public static final int DIDYOUKNOW_GLORY_GEMS = 5131;
            public static final int DIDYOUKNOW_SNOWFLAKE_BASALT = 5132;
            public static final int DIDYOUKNOW_FARMINGGEAR = 5133;
            public static final int DIDYOUKNOW_MINIMAP_HIDE = 5134;
            public static final int DIDYOUKNOW_UNKAHFIRE = 5135;
            public static final int DIDYOUKNOW_DORGESH_KALPHITE = 5136;
            public static final int DIDYOUKNOW_SNELMS = 5137;
            public static final int DIDYOUKNOW_STRANGE_FRUIT = 5138;
            public static final int DIDYOUKNOW_TWINFLAME_STAFF = 5139;
            public static final int DIDYOUKNOW_BOXTRAP = 5140;
            public static final int DIDYOUKNOW_HUNTER_FAIL = 5141;
            public static final int DIDYOUKNOW_BOLT_POUCH = 5142;
            public static final int DIDYOUKNOW_SUPERCOMPOST = 5143;
            public static final int DIDYOUKNOW_RUNSETTING = 5144;
            public static final int DIDYOUKNOW_HOUSE_VIEWER = 5145;
            public static final int DIDYOUKNOW_HITSPLAT_THRESHOLD = 5146;
            public static final int DIDYOUKNOW_II_STRENGTH_XP = 5147;
            public static final int DIDYOUKNOW_SMASH_VIALS = 5148;
            public static final int DIDYOUKNOW_SMASH_POTS = 5149;
            public static final int DIDYOUKNOW_ZAMMY_GRAPES = 5150;
            public static final int DIDYOUKNOW_MAKEOVER = 5151;
            public static final int DIDYOUKNOW_PEEK_DESKTOP = 5152;
            public static final int DIDYOUKNOW_PEEK_MOBILE = 5153;
            public static final int DIDYOUKNOW_SAILING_BOAT_BOTTLES = 7098;
            public static final int DIDYOUKNOW_SAILING_BOAT_ESCAPING = 7099;
            public static final int DIDYOUKNOW_SAILING_SHIPWRIGHTS = 7100;
            public static final int DIDYOUKNOW_SAILING_WITH_FRIENDS = 7101;
            public static final int DIDYOUKNOW_SAILING_REPAIR_KITS = 7102;
        }
    }

    public static final class Multirunes {
        private Multirunes() {}

        public static final int ID = 91;
        public static final int COL_BASE_RUNE = 0;
        public static final int COL_COMBO_AND_ALTERNATIVE_RUNES = 1;
    }

    public static final class ComboruneRecipe {
        private ComboruneRecipe() {}

        public static final int ID = 92;
        public static final int COL_LEVELREQ = 0;
        public static final int COL_COMBO_RUNE = 1;
        public static final int COL_FAIL_CHANCE = 2;
        public static final int COL_SCAR_ESSENCE_TYPE_AND_BONUS = 3;
        public static final int COL_ITEM_REQUIRED_PER_CRAFT = 4;
        public static final int COL_ITEM_REQUIRED_PER_ESSENCE = 5;
        public static final int COL_ITEM_REQUIRED_PER_RUNE = 6;
        public static final int COL_MULTIPLE_RUNE_THRESHOLD = 7;
        public static final int COL_MULTIPLE_RUNE_XP_SCALING = 8;
        public static final int COL_GOTR_ESSENCE_GIVEN = 9;
        public static final int COL_GOTR_TALISMAN_GIVEN = 10;

        public static final class Row {
            private Row() {}

            public static final int LAVA_FROM_EARTHALTAR = 5157;
            public static final int LAVA_FROM_FIREALTAR = 5158;
            public static final int SMOKE_FROM_FIREALTAR = 5159;
            public static final int SMOKE_FROM_AIRALTAR = 5160;
            public static final int STEAM_FROM_FIREALTAR = 5161;
            public static final int STEAM_FROM_WATERALTAR = 5162;
            public static final int DUST_FROM_AIRALTAR = 5163;
            public static final int DUST_FROM_EARTHALTAR = 5164;
            public static final int MUD_FROM_EARTHALTAR = 5165;
            public static final int MUD_FROM_WATERALTAR = 5166;
            public static final int MIST_FROM_WATERALTAR = 5167;
            public static final int MIST_FROM_AIRALTAR = 5168;
            public static final int SUNFIRE_FROM_RALOSSHRINE = 5169;
            public static final int COSMICSOUL_FROM_COSMICALTAR = 5170;
        }
    }

    public static final class Vmq4MetzliBossSpecialTeleport {
        private Vmq4MetzliBossSpecialTeleport() {}

        public static final int ID = 93;
        public static final int COL_ID = 0;
        public static final int COL_ZONE_ID = 1;
        public static final int COL_START_COORD = 2;
        public static final int COL_END_COORD = 3;
        public static final int COL_ROTATION = 4;

        public static final class Row {
            private Row() {}

            public static final int VMQ4_METZLI_BOSS_SUN_TELEPORT_1 = 5199;
            public static final int VMQ4_METZLI_BOSS_SUN_TELEPORT_2 = 5200;
            public static final int VMQ4_METZLI_BOSS_SUN_TELEPORT_3 = 5201;
            public static final int VMQ4_METZLI_BOSS_SUN_TELEPORT_4 = 5202;
            public static final int VMQ4_METZLI_BOSS_MOON_TELEPORT_NE_1 = 5203;
            public static final int VMQ4_METZLI_BOSS_MOON_TELEPORT_SE_1 = 5204;
            public static final int VMQ4_METZLI_BOSS_MOON_TELEPORT_SW_1 = 5205;
            public static final int VMQ4_METZLI_BOSS_MOON_TELEPORT_NW_1 = 5206;
            public static final int VMQ4_METZLI_BOSS_MOON_TELEPORT_NE_2 = 5207;
            public static final int VMQ4_METZLI_BOSS_MOON_TELEPORT_SE_2 = 5208;
            public static final int VMQ4_METZLI_BOSS_MOON_TELEPORT_SW_2 = 5209;
            public static final int VMQ4_METZLI_BOSS_MOON_TELEPORT_NW_2 = 5210;
        }
    }

    public static final class Vmq4Teleporters {
        private Vmq4Teleporters() {}

        public static final int ID = 94;
        public static final int COL_COORD_1 = 0;
        public static final int COL_COORD_2 = 1;
        public static final int COL_TELEPORTER_TYPE = 2;
        public static final int COL_PUZZLE_TELEPORT = 3;

        public static final class Row {
            private Row() {}

            public static final int VMQ4_TELEPORTER_MOON_PUZZLE = 5211;
            public static final int VMQ4_TELEPORTER_SUN_PUZZLE = 5212;
            public static final int CRYPT_OF_TONALI_TELEPORTER_1 = 5213;
            public static final int CRYPT_OF_TONALI_TELEPORTER_2 = 5214;
            public static final int CRYPT_OF_TONALI_TELEPORTER_3 = 5215;
            public static final int CRYPT_OF_TONALI_TELEPORTER_4 = 5216;
            public static final int CRYPT_OF_TONALI_TELEPORTER_5 = 5217;
            public static final int CRYPT_OF_TONALI_TELEPORTER_6 = 5218;
            public static final int CRYPT_OF_TONALI_TELEPORTER_7 = 5219;
        }
    }

    public static final class Vmq4ZemaTahtTranslations {
        private Vmq4ZemaTahtTranslations() {}

        public static final int ID = 95;
        public static final int COL_ZEMA_TAHT_WORD = 0;
        public static final int COL_INPUT_HASH = 1;
        public static final int COL_TRANSLATION = 2;

        public static final class Row {
            private Row() {}

            public static final int VMQ4_TRANSLATION_NORTH = 5220;
            public static final int VMQ4_TRANSLATION_EAST = 5221;
            public static final int VMQ4_TRANSLATION_SOUTH = 5222;
            public static final int VMQ4_TRANSLATION_WEST = 5223;
            public static final int VMQ4_TRANSLATION_0 = 5224;
            public static final int VMQ4_TRANSLATION_1 = 5225;
            public static final int VMQ4_TRANSLATION_2 = 5226;
            public static final int VMQ4_TRANSLATION_3 = 5227;
            public static final int VMQ4_TRANSLATION_4 = 5228;
            public static final int VMQ4_TRANSLATION_5 = 5229;
            public static final int VMQ4_TRANSLATION_6 = 5230;
            public static final int VMQ4_TRANSLATION_7 = 5231;
            public static final int VMQ4_TRANSLATION_8 = 5232;
            public static final int VMQ4_TRANSLATION_9 = 5233;
            public static final int VMQ4_TRANSLATION_10 = 5234;
            public static final int VMQ4_TRANSLATION_20 = 5235;
            public static final int VMQ4_TRANSLATION_20_ALT = 5236;
            public static final int VMQ4_TRANSLATION_30 = 5237;
            public static final int VMQ4_TRANSLATION_30_ALT = 5238;
            public static final int VMQ4_TRANSLATION_40 = 5239;
            public static final int VMQ4_TRANSLATION_40_ALT = 5240;
            public static final int VMQ4_TRANSLATION_50 = 5241;
            public static final int VMQ4_TRANSLATION_50_ALT = 5242;
            public static final int VMQ4_TRANSLATION_60 = 5243;
            public static final int VMQ4_TRANSLATION_60_ALT = 5244;
            public static final int VMQ4_TRANSLATION_70 = 5245;
            public static final int VMQ4_TRANSLATION_70_ALT = 5246;
            public static final int VMQ4_TRANSLATION_80 = 5247;
            public static final int VMQ4_TRANSLATION_80_ALT = 5248;
            public static final int VMQ4_TRANSLATION_90 = 5249;
            public static final int VMQ4_TRANSLATION_90_ALT = 5250;
            public static final int VMQ4_TRANSLATION_FUN_1 = 5251;
        }
    }

    public static final class Vmq4SunPuzzleAltars {
        private Vmq4SunPuzzleAltars() {}

        public static final int ID = 96;
        public static final int COL_ID = 0;
        public static final int COL_ALTAR_COORD = 1;

        public static final class Row {
            private Row() {}

            public static final int VMQ4_SUN_ALTAR_NORTH = 5252;
            public static final int VMQ4_SUN_ALTAR_NORTHEAST = 5253;
            public static final int VMQ4_SUN_ALTAR_EAST = 5254;
            public static final int VMQ4_SUN_ALTAR_SOUTHEAST = 5255;
            public static final int VMQ4_SUN_ALTAR_SOUTH = 5256;
            public static final int VMQ4_SUN_ALTAR_SOUTHWEST = 5257;
            public static final int VMQ4_SUN_ALTAR_WEST = 5258;
            public static final int VMQ4_SUN_ALTAR_NORTHWEST = 5259;
        }
    }

    public static final class Vmq4MoonPuzzleRoots {
        private Vmq4MoonPuzzleRoots() {}

        public static final int ID = 97;
        public static final int COL_ID = 0;
        public static final int COL_ROOT_COORD = 1;

        public static final class Row {
            private Row() {}

            public static final int VMQ4_MOON_ROOT_0 = 5260;
            public static final int VMQ4_MOON_ROOT_1 = 5261;
            public static final int VMQ4_MOON_ROOT_2 = 5262;
            public static final int VMQ4_MOON_ROOT_3 = 5263;
            public static final int VMQ4_MOON_ROOT_4 = 5264;
            public static final int VMQ4_MOON_ROOT_5 = 5265;
            public static final int VMQ4_MOON_ROOT_6 = 5266;
        }
    }

    public static final class Vmq4MoonPuzzleBraziers {
        private Vmq4MoonPuzzleBraziers() {}

        public static final int ID = 98;
        public static final int COL_ID = 0;
        public static final int COL_TYPE = 1;
        public static final int COL_BRAZIER_COORD = 2;

        public static final class Row {
            private Row() {}

            public static final int VMQ4_MOON_BRAZIER_0 = 5267;
            public static final int VMQ4_MOON_BRAZIER_1 = 5268;
            public static final int VMQ4_MOON_BRAZIER_2 = 5269;
            public static final int VMQ4_MOON_BRAZIER_3 = 5270;
            public static final int VMQ4_MOON_BRAZIER_4 = 5271;
            public static final int VMQ4_MOON_BRAZIER_5 = 5272;
            public static final int VMQ4_MOON_BRAZIER_6 = 5273;
            public static final int VMQ4_MOON_BRAZIER_7 = 5274;
        }
    }

    public static final class Vmq4CryptWaves {
        private Vmq4CryptWaves() {}

        public static final int ID = 99;
        public static final int COL_ID = 0;
        public static final int COL_MELEE_1 = 1;
        public static final int COL_MELEE_2 = 2;
        public static final int COL_RANGE = 3;
        public static final int COL_MAGE = 4;

        public static final class Row {
            private Row() {}

            public static final int VMQ4_WAVE1 = 5275;
            public static final int VMQ4_WAVE2 = 5276;
            public static final int VMQ4_WAVE3 = 5277;
            public static final int VMQ4_WAVE4 = 5278;
            public static final int VMQ4_WAVE5 = 5279;
        }
    }

    public static final class PohHeraldicDecorVariant {
        private PohHeraldicDecorVariant() {}

        public static final int ID = 100;
        public static final int COL_FURNITURE = 0;
        public static final int COL_BASE_VARIANT = 1;
        public static final int COL_LOGO = 2;
        public static final int COL_VARIANT = 3;

        public static final class Row {
            private Row() {}

            public static final int POH_HERALDIC_DECOR_TEAK_ARRAV = 5280;
            public static final int POH_HERALDIC_DECOR_TEAK_ASGARNIA = 5281;
            public static final int POH_HERALDIC_DECOR_TEAK_DORGESHUUN = 5282;
            public static final int POH_HERALDIC_DECOR_TEAK_DRAGON = 5283;
            public static final int POH_HERALDIC_DECOR_TEAK_FAIRY = 5284;
            public static final int POH_HERALDIC_DECOR_TEAK_GUTHIX = 5285;
            public static final int POH_HERALDIC_DECOR_TEAK_HAM = 5286;
            public static final int POH_HERALDIC_DECOR_TEAK_HORSE = 5287;
            public static final int POH_HERALDIC_DECOR_TEAK_JOGRE = 5288;
            public static final int POH_HERALDIC_DECOR_TEAK_KANDARIN = 5289;
            public static final int POH_HERALDIC_DECOR_TEAK_MISTHALIN = 5290;
            public static final int POH_HERALDIC_DECOR_TEAK_MONEY = 5291;
            public static final int POH_HERALDIC_DECOR_TEAK_SARADOMIN = 5292;
            public static final int POH_HERALDIC_DECOR_TEAK_SKULL = 5293;
            public static final int POH_HERALDIC_DECOR_TEAK_VARROCK = 5294;
            public static final int POH_HERALDIC_DECOR_TEAK_ZAMORAK = 5295;
            public static final int POH_HERALDIC_DECOR_OAK_ARRAV = 5296;
            public static final int POH_HERALDIC_DECOR_OAK_ASGARNIA = 5297;
            public static final int POH_HERALDIC_DECOR_OAK_DORGESHUUN = 5298;
            public static final int POH_HERALDIC_DECOR_OAK_DRAGON = 5299;
            public static final int POH_HERALDIC_DECOR_OAK_FAIRY = 5300;
            public static final int POH_HERALDIC_DECOR_OAK_GUTHIX = 5301;
            public static final int POH_HERALDIC_DECOR_OAK_HAM = 5302;
            public static final int POH_HERALDIC_DECOR_OAK_HORSE = 5303;
            public static final int POH_HERALDIC_DECOR_OAK_JOGRE = 5304;
            public static final int POH_HERALDIC_DECOR_OAK_KANDARIN = 5305;
            public static final int POH_HERALDIC_DECOR_OAK_MISTHALIN = 5306;
            public static final int POH_HERALDIC_DECOR_OAK_MONEY = 5307;
            public static final int POH_HERALDIC_DECOR_OAK_SARADOMIN = 5308;
            public static final int POH_HERALDIC_DECOR_OAK_SKULL = 5309;
            public static final int POH_HERALDIC_DECOR_OAK_VARROCK = 5310;
            public static final int POH_HERALDIC_DECOR_OAK_ZAMORAK = 5311;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_ARRAV = 5312;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_ASGARNIA = 5313;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_DORGESHUUN = 5314;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_DRAGON = 5315;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_FAIRY = 5316;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_GUTHIX = 5317;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_HAM = 5318;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_HORSE = 5319;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_JOGRE = 5320;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_KANDARIN = 5321;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_MISTHALIN = 5322;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_MONEY = 5323;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_SARADOMIN = 5324;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_SKULL = 5325;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_VARROCK = 5326;
            public static final int POH_HERALDIC_DECOR_MAHOGANY_ZAMORAK = 5327;
            public static final int POH_HERALDIC_KITE_SHIELD_ARRAV = 5328;
            public static final int POH_HERALDIC_KITE_SHIELD_ASGARNIA = 5329;
            public static final int POH_HERALDIC_KITE_SHIELD_DORGESHUUN = 5330;
            public static final int POH_HERALDIC_KITE_SHIELD_DRAGON = 5331;
            public static final int POH_HERALDIC_KITE_SHIELD_FAIRY = 5332;
            public static final int POH_HERALDIC_KITE_SHIELD_GUTHIX = 5333;
            public static final int POH_HERALDIC_KITE_SHIELD_HAM = 5334;
            public static final int POH_HERALDIC_KITE_SHIELD_HORSE = 5335;
            public static final int POH_HERALDIC_KITE_SHIELD_JOGRE = 5336;
            public static final int POH_HERALDIC_KITE_SHIELD_KANDARIN = 5337;
            public static final int POH_HERALDIC_KITE_SHIELD_MISTHALIN = 5338;
            public static final int POH_HERALDIC_KITE_SHIELD_MONEY = 5339;
            public static final int POH_HERALDIC_KITE_SHIELD_SARADOMIN = 5340;
            public static final int POH_HERALDIC_KITE_SHIELD_SKULL = 5341;
            public static final int POH_HERALDIC_KITE_SHIELD_VARROCK = 5342;
            public static final int POH_HERALDIC_KITE_SHIELD_ZAMORAK = 5343;
            public static final int POH_HERALDIC_SQUARE_SHIELD_ARRAV = 5344;
            public static final int POH_HERALDIC_SQUARE_SHIELD_ASGARNIA = 5345;
            public static final int POH_HERALDIC_SQUARE_SHIELD_DORGESHUUN = 5346;
            public static final int POH_HERALDIC_SQUARE_SHIELD_DRAGON = 5347;
            public static final int POH_HERALDIC_SQUARE_SHIELD_FAIRY = 5348;
            public static final int POH_HERALDIC_SQUARE_SHIELD_GUTHIX = 5349;
            public static final int POH_HERALDIC_SQUARE_SHIELD_HAM = 5350;
            public static final int POH_HERALDIC_SQUARE_SHIELD_HORSE = 5351;
            public static final int POH_HERALDIC_SQUARE_SHIELD_JOGRE = 5352;
            public static final int POH_HERALDIC_SQUARE_SHIELD_KANDARIN = 5353;
            public static final int POH_HERALDIC_SQUARE_SHIELD_MISTHALIN = 5354;
            public static final int POH_HERALDIC_SQUARE_SHIELD_MONEY = 5355;
            public static final int POH_HERALDIC_SQUARE_SHIELD_SARADOMIN = 5356;
            public static final int POH_HERALDIC_SQUARE_SHIELD_SKULL = 5357;
            public static final int POH_HERALDIC_SQUARE_SHIELD_VARROCK = 5358;
            public static final int POH_HERALDIC_SQUARE_SHIELD_ZAMORAK = 5359;
            public static final int POH_HERALDIC_ROUND_SHIELD_ARRAV = 5360;
            public static final int POH_HERALDIC_ROUND_SHIELD_ASGARNIA = 5361;
            public static final int POH_HERALDIC_ROUND_SHIELD_DORGESHUUN = 5362;
            public static final int POH_HERALDIC_ROUND_SHIELD_DRAGON = 5363;
            public static final int POH_HERALDIC_ROUND_SHIELD_FAIRY = 5364;
            public static final int POH_HERALDIC_ROUND_SHIELD_GUTHIX = 5365;
            public static final int POH_HERALDIC_ROUND_SHIELD_HAM = 5366;
            public static final int POH_HERALDIC_ROUND_SHIELD_HORSE = 5367;
            public static final int POH_HERALDIC_ROUND_SHIELD_JOGRE = 5368;
            public static final int POH_HERALDIC_ROUND_SHIELD_KANDARIN = 5369;
            public static final int POH_HERALDIC_ROUND_SHIELD_MISTHALIN = 5370;
            public static final int POH_HERALDIC_ROUND_SHIELD_MONEY = 5371;
            public static final int POH_HERALDIC_ROUND_SHIELD_SARADOMIN = 5372;
            public static final int POH_HERALDIC_ROUND_SHIELD_SKULL = 5373;
            public static final int POH_HERALDIC_ROUND_SHIELD_VARROCK = 5374;
            public static final int POH_HERALDIC_ROUND_SHIELD_ZAMORAK = 5375;
        }
    }

    public static final class Jigsaw {
        private Jigsaw() {}

        public static final int ID = 101;
        public static final int COL_PIECE = 0;
        public static final int COL_PIECE_START_POSITION = 1;
        public static final int COL_BACKING = 2;
        public static final int COL_PIECE_NAME = 3;
        public static final int COL_SNAP_LEEWAY = 4;
        public static final int COL_PIECE_ZOOM = 5;
        public static final int COL_PIECE_SIZE_X = 6;
        public static final int COL_PIECE_SIZE_Y = 7;
        public static final int COL_PREVIEW = 8;

        public static final class Row {
            private Row() {}

            public static final int JIGSAW_SCRAMBLED = 5397;
        }
    }

    public static final class FletchGreenmanData {
        private FletchGreenmanData() {}

        public static final int ID = 102;
        public static final int COL_PRODUCT = 0;
        public static final int COL_FLETCHING_LEVEL = 1;
        public static final int COL_MATERIALS = 2;
        public static final int COL_CRAFTING_DELAY = 3;
        public static final int COL_CRAFTING_XP = 4;
        public static final int COL_FLETCHING_SIDEEFFECTS_LOG_TYPE = 5;

        public static final class Row {
            private Row() {}

            public static final int FLETCH_GREENMAN_STATUE = 5398;
            public static final int FLETCH_GREENMAN_WALL_DECORATION = 5399;
        }
    }

    public static final class GreenmanMask {
        private GreenmanMask() {}

        public static final int ID = 103;
        public static final int COL_MASK_OBJECT = 0;
        public static final int COL_LEAF_REQUIRED = 1;
        public static final int COL_LEAF_REQUIRED_AMOUNT = 2;
        public static final int COL_UNLOCKED_BIT = 3;

        public static final class Row {
            private Row() {}

            public static final int GREENMAN_MASK_BASE = 5400;
        }
    }

    public static final class EntTotemsBaseData {
        private EntTotemsBaseData() {}

        public static final int ID = 104;
        public static final int COL_ENT_TOTEMS_BASE_ID = 0;
        public static final int COL_ENT_TOTEMS_BASE_LOG = 1;
        public static final int COL_ENT_TOTEMS_BASE_NAME = 2;
        public static final int COL_ENT_TOTEMS_BASE_NAME_WITH_ARTICLE = 3;
        public static final int COL_ENT_TOTEMS_BASE_CONSTRUCTION_XP = 4;
        public static final int COL_ENT_TOTEMS_BASE_DECAY = 5;
        public static final int COL_ENT_TOTEMS_BASE_FLETCHING_LEVEL = 6;
        public static final int COL_ENT_TOTEMS_BASE_CARVE_FLETCHING_XP = 7;
        public static final int COL_ENT_TOTEMS_BASE_DECORATION_FLETCHING_XP = 8;
        public static final int COL_ENT_TOTEMS_BASE_POINTS_MODIFIER = 9;

        public static final class Row {
            private Row() {}

            public static final int ENT_TOTEMS_BASE_OAK = 5407;
            public static final int ENT_TOTEMS_BASE_WILLOW = 5408;
            public static final int ENT_TOTEMS_BASE_MAPLE = 5409;
            public static final int ENT_TOTEMS_BASE_YEW = 5410;
            public static final int ENT_TOTEMS_BASE_MAGIC = 5411;
            public static final int ENT_TOTEMS_BASE_REDWOOD = 5412;
        }
    }

    public static final class EntTotemsAnimalData {
        private EntTotemsAnimalData() {}

        public static final int ID = 105;
        public static final int COL_ENT_TOTEMS_ANIMAL_ID = 0;
        public static final int COL_ENT_TOTEMS_ANIMAL_CARVINGID = 1;
        public static final int COL_ENT_TOTEMS_ANIMAL_NAME = 2;
        public static final int COL_ENT_TOTEMS_ANIMAL_NAME_ARTICLE = 3;
        public static final int COL_ENT_TOTEMS_ANIMAL_COLOUR_TAG = 4;
        public static final int COL_ENT_TOTEMS_ANIMAL_ICON = 5;
        public static final int COL_ENT_TOTEMS_ANIMAL_DISABLED_ICON = 6;

        public static final class Row {
            private Row() {}

            public static final int ENT_TOTEMS_ANIMAL_A = 5413;
            public static final int ENT_TOTEMS_ANIMAL_B = 5414;
            public static final int ENT_TOTEMS_ANIMAL_C = 5415;
            public static final int ENT_TOTEMS_ANIMAL_D = 5416;
            public static final int ENT_TOTEMS_ANIMAL_E = 5417;
        }
    }

    public static final class EntTotemsSiteData {
        private EntTotemsSiteData() {}

        public static final int ID = 106;
        public static final int COL_ENT_TOTEMS_SITE_ID = 0;
        public static final int COL_ENT_TOTEMS_DESTINATION_NPCCONFIG = 1;
        public static final int COL_ENT_TOTEMS_SITE_TOTEM_COORD = 2;
        public static final int COL_ENT_TOTEMS_SITE_OFFERINGS_COORD = 3;
        public static final int COL_ENT_TOTEMS_SITE_WORSHIP_COORD = 4;
        public static final int COL_ENT_TOTEMS_SITE_TRAIL_BUFFER = 5;
        public static final int COL_ENT_TOTEMS_SITE_PATH = 6;

        public static final class Row {
            private Row() {}

            public static final int ENT_TOTEMS_SITE_1 = 5418;
            public static final int ENT_TOTEMS_SITE_2 = 5419;
            public static final int ENT_TOTEMS_SITE_3 = 5420;
            public static final int ENT_TOTEMS_SITE_4 = 5421;
            public static final int ENT_TOTEMS_SITE_5 = 5422;
            public static final int ENT_TOTEMS_SITE_6 = 5423;
            public static final int ENT_TOTEMS_SITE_7 = 5424;
            public static final int ENT_TOTEMS_SITE_8 = 5425;
        }
    }

    public static final class EntTotemsDecorationData {
        private EntTotemsDecorationData() {}

        public static final int ID = 107;
        public static final int COL_ENT_TOTEMS_DECORATION_OBJ = 0;
        public static final int COL_ENT_TOTEMS_DECORATION_NAME = 1;
        public static final int COL_ENT_TOTEMS_DECORATION_BASE_ID = 2;
        public static final int COL_ENT_TOTEMS_DECORATION_VALUE = 3;

        public static final class Row {
            private Row() {}

            public static final int ENT_TOTEMS_DECORATION_OAK_SHORTBOW = 5426;
            public static final int ENT_TOTEMS_DECORATION_OAK_SHORTBOW_UNSTRUNG = 5427;
            public static final int ENT_TOTEMS_DECORATION_OAK_LONGBOW = 5428;
            public static final int ENT_TOTEMS_DECORATION_OAK_LONGBOW_UNSTRUNG = 5429;
            public static final int ENT_TOTEMS_DECORATION_OAK_STOCK = 5430;
            public static final int ENT_TOTEMS_DECORATION_OAK_SHIELD = 5431;
            public static final int ENT_TOTEMS_DECORATION_WILLOW_SHORTBOW = 5432;
            public static final int ENT_TOTEMS_DECORATION_WILLOW_SHORTBOW_UNSTRUNG = 5433;
            public static final int ENT_TOTEMS_DECORATION_WILLOW_LONGBOW = 5434;
            public static final int ENT_TOTEMS_DECORATION_WILLOW_LONGBOW_UNSTRUNG = 5435;
            public static final int ENT_TOTEMS_DECORATION_WILLOW_STOCK = 5436;
            public static final int ENT_TOTEMS_DECORATION_WILLOW_SHIELD = 5437;
            public static final int ENT_TOTEMS_DECORATION_MAPLE_SHORTBOW = 5438;
            public static final int ENT_TOTEMS_DECORATION_MAPLE_SHORTBOW_UNSTRUNG = 5439;
            public static final int ENT_TOTEMS_DECORATION_MAPLE_LONGBOW = 5440;
            public static final int ENT_TOTEMS_DECORATION_MAPLE_LONGBOW_UNSTRUNG = 5441;
            public static final int ENT_TOTEMS_DECORATION_MAPLE_STOCK = 5442;
            public static final int ENT_TOTEMS_DECORATION_MAPLE_SHIELD = 5443;
            public static final int ENT_TOTEMS_DECORATION_YEW_SHORTBOW = 5444;
            public static final int ENT_TOTEMS_DECORATION_YEW_SHORTBOW_UNSTRUNG = 5445;
            public static final int ENT_TOTEMS_DECORATION_YEW_LONGBOW = 5446;
            public static final int ENT_TOTEMS_DECORATION_YEW_LONGBOW_UNSTRUNG = 5447;
            public static final int ENT_TOTEMS_DECORATION_YEW_STOCK = 5448;
            public static final int ENT_TOTEMS_DECORATION_YEW_SHIELD = 5449;
            public static final int ENT_TOTEMS_DECORATION_MAGIC_SHORTBOW = 5450;
            public static final int ENT_TOTEMS_DECORATION_MAGIC_SHORTBOW_UNSTRUNG = 5451;
            public static final int ENT_TOTEMS_DECORATION_MAGIC_LONGBOW = 5452;
            public static final int ENT_TOTEMS_DECORATION_MAGIC_LONGBOW_UNSTRUNG = 5453;
            public static final int ENT_TOTEMS_DECORATION_MAGIC_STOCK = 5454;
            public static final int ENT_TOTEMS_DECORATION_MAGIC_SHIELD = 5455;
            public static final int ENT_TOTEMS_DECORATION_REDWOOD_HIKING_STAFF = 5456;
            public static final int ENT_TOTEMS_DECORATION_REDWOOD_SHIELD = 5457;
        }
    }

    public static final class DomDroptable {
        private DomDroptable() {}

        public static final int ID = 108;
        public static final int COL_TABLE_CHANCE = 0;
        public static final int COL_MODIFY_DROPRATE_ID = 1;
        public static final int COL_TOTAL_WEIGHT = 2;
        public static final int COL_ITEMS = 3;

        public static final class Row {
            private Row() {}

            public static final int DOM_BASIC_DROPTABLE = 5469;
            public static final int DOM_UNIQUE_DROPTABLE_BASIC = 5470;
            public static final int DOM_UNIQUE_DROPTABLE_ENHANCED = 5471;
            public static final int DOM_UNIQUE_DROPTABLE_ULTIMATE = 5472;
            public static final int DOM_UNIQUE_DROPTABLE_FINALE = 5473;
            public static final int DOM_PET_DROPTABLE = 5474;
        }
    }

    public static final class DomDelveLevel {
        private DomDelveLevel() {}

        public static final int ID = 109;
        public static final int COL_HITPOINTS_PERCENT_INCREASE = 0;
        public static final int COL_VISLEVEL_PERCENT_INCREASE = 1;
        public static final int COL_ATTACKSPEED = 2;
        public static final int COL_DAMAGE_PERCENT_INCREASE = 3;
        public static final int COL_DEFENCE_PERCENT_INCREASE = 4;
        public static final int COL_MAX_BLEEDS = 5;
        public static final int COL_BASIC_PROJ_TYPES = 6;
        public static final int COL_BASIC_PROJ_SPEED = 7;
        public static final int COL_SECONDARY_PROJ_TYPES = 8;
        public static final int COL_SECONDARY_PROJ_SPEED = 9;
        public static final int COL_ROCK_PROJ_SPEED = 10;
        public static final int COL_ROCK_SHRAPNEL_QUANTITY = 11;
        public static final int COL_SHIELD_MOVE_SPEED = 12;
        public static final int COL_ORB_MOVE_SPEED = 13;
        public static final int COL_ORB_SPAWN_CHANCE = 14;
        public static final int COL_ORB_SPAWN_QUANITITY = 15;
        public static final int COL_ORB_PRAYER_TYPE = 16;
        public static final int COL_ORB_FREQUENCY_PERCENT = 17;
        public static final int COL_SHIELDPHASE_ORB_SPAWN_BASE_RATE = 18;
        public static final int COL_BEAM_DAMAGE = 19;
        public static final int COL_BURROWPHASE_CHARGES = 20;
        public static final int COL_MAINPHASE_ORBS_SPAWNING = 21;
        public static final int COL_MAINPHASE_BEAM = 22;
        public static final int COL_BURROWPHASE_BEAM = 23;
        public static final int COL_BURROWPHASE_ATTACKS = 24;
        public static final int COL_BURROWPHASE_PRAYERS = 25;
        public static final int COL_BURROWPHASE_FREE_ROCK_GROUPS = 26;
        public static final int COL_BURROWPHASE_RUSH_SPEED = 27;
        public static final int COL_BURROWPHASE_RUSH_DAMAGE = 28;
        public static final int COL_BURROWPHASE_SLAMS = 29;
        public static final int COL_NODE_PHASE_SLAM_COUNT = 30;
        public static final int COL_DOUBLE_SCOOPS = 31;
        public static final int COL_BEAM_INTERRUPT_STRENGTHBONUS_THRESHOLD = 32;
        public static final int COL_EXCLUSIVE_DROPTABLES = 33;
        public static final int COL_DROPTABLES = 34;
        public static final int COL_GUARANTEED_DROPS = 35;

        public static final class Row {
            private Row() {}

            public static final int DOM_DELVE_LEVEL_0 = 5475;
            public static final int DOM_DELVE_LEVEL_1 = 5476;
            public static final int DOM_DELVE_LEVEL_2 = 5477;
            public static final int DOM_DELVE_LEVEL_3 = 5478;
            public static final int DOM_DELVE_LEVEL_4 = 5479;
            public static final int DOM_DELVE_LEVEL_5 = 5480;
            public static final int DOM_DELVE_LEVEL_6 = 5481;
            public static final int DOM_DELVE_LEVEL_7 = 5482;
        }
    }

    public static final class Furniture {
        private Furniture() {}

        public static final int ID = 110;
        public static final int COL_MODEL_OBJ = 0;
        public static final int COL_NAME = 1;
        public static final int COL_MATERIAL_COST = 2;
        public static final int COL_LEVEL_REQUIREMENT = 3;
        public static final int COL_BUILD_ANIMATION_STYLE = 4;
        public static final int COL_HIDDEN_IN_BUILD_MENU = 5;
        public static final int COL_UPGRADE_SOURCE_RELATIVE = 6;
        public static final int COL_UPGRADE_SOURCE_ABSOLUTE = 7;
        public static final int COL_STAIRCASE_DIRECTION = 8;
        public static final int COL_FLATPACK = 9;

        public static final class Row {
            private Row() {}

            public static final int POH_ARMCHAIR_1 = 5517;
            public static final int POH_ARMCHAIR_2 = 5518;
            public static final int POH_ARMCHAIR_3 = 5519;
            public static final int POH_ARMCHAIR_4 = 5520;
            public static final int POH_ARMCHAIR_5 = 5521;
            public static final int POH_ARMCHAIR_6 = 5522;
            public static final int POH_ARMCHAIR_7 = 5523;
            public static final int POH_RUG_1 = 5524;
            public static final int POH_RUG_2 = 5525;
            public static final int POH_RUG_3 = 5526;
            public static final int POH_RUG_DEADMAN = 5527;
            public static final int POH_RUG_LEAGUE_5 = 5528;
            public static final int POH_BOOKCASE_1 = 5529;
            public static final int POH_BOOKCASE_2 = 5530;
            public static final int POH_BOOKCASE_3 = 5531;
            public static final int POH_FIREPLACE_1 = 5532;
            public static final int POH_FIREPLACE_2 = 5533;
            public static final int POH_FIREPLACE_3 = 5534;
            public static final int POH_CURTAINS_1 = 5535;
            public static final int POH_CURTAINS_2 = 5536;
            public static final int POH_CURTAINS_3 = 5537;
            public static final int POH_CURTAINS_LEAGUE5 = 5538;
            public static final int POH_STOVE_1 = 5539;
            public static final int POH_STOVE_2 = 5540;
            public static final int POH_STOVE_3 = 5541;
            public static final int POH_STOVE_4 = 5542;
            public static final int POH_STOVE_5 = 5543;
            public static final int POH_STOVE_6 = 5544;
            public static final int POH_STOVE_7 = 5545;
            public static final int POH_KITCHEN_SHELVES_1 = 5546;
            public static final int POH_KITCHEN_SHELVES_2 = 5547;
            public static final int POH_KITCHEN_SHELVES_3 = 5548;
            public static final int POH_KITCHEN_SHELVES_4 = 5549;
            public static final int POH_KITCHEN_SHELVES_5 = 5550;
            public static final int POH_KITCHEN_SHELVES_6 = 5551;
            public static final int POH_KITCHEN_SHELVES_7 = 5552;
            public static final int POH_BARREL_1 = 5553;
            public static final int POH_BARREL_2 = 5554;
            public static final int POH_BARREL_3 = 5555;
            public static final int POH_BARREL_4 = 5556;
            public static final int POH_BARREL_5 = 5557;
            public static final int POH_BARREL_6 = 5558;
            public static final int POH_CAT_BASKET_1 = 5559;
            public static final int POH_CAT_BASKET_2 = 5560;
            public static final int POH_CAT_BASKET_3 = 5561;
            public static final int POH_LARDER_1 = 5562;
            public static final int POH_LARDER_2 = 5563;
            public static final int POH_LARDER_3 = 5564;
            public static final int POH_SINK_1 = 5565;
            public static final int POH_SINK_2 = 5566;
            public static final int POH_SINK_3 = 5567;
            public static final int POH_SINK_4 = 5568;
            public static final int POH_KITCHENTABLE_1 = 5569;
            public static final int POH_KITCHENTABLE_2 = 5570;
            public static final int POH_KITCHENTABLE_3 = 5571;
            public static final int POH_KITCHEN_RACK_1 = 5572;
            public static final int POH_DINING_TABLE_1 = 5573;
            public static final int POH_DINING_TABLE_2 = 5574;
            public static final int POH_DINING_TABLE_3 = 5575;
            public static final int POH_DINING_TABLE_4 = 5576;
            public static final int POH_DINING_TABLE_5 = 5577;
            public static final int POH_DINING_TABLE_6 = 5578;
            public static final int POH_DINING_TABLE_7 = 5579;
            public static final int POH_DINING_CHAIRS_1 = 5580;
            public static final int POH_DINING_CHAIRS_2 = 5581;
            public static final int POH_DINING_CHAIRS_3 = 5582;
            public static final int POH_DINING_CHAIRS_4 = 5583;
            public static final int POH_DINING_CHAIRS_5 = 5584;
            public static final int POH_DINING_CHAIRS_6 = 5585;
            public static final int POH_DINING_CHAIRS_7 = 5586;
            public static final int POH_WALL_CREST_1 = 5587;
            public static final int POH_WALL_CREST_2 = 5588;
            public static final int POH_WALL_CREST_3 = 5589;
            public static final int POH_BELLPULL_1 = 5590;
            public static final int POH_BELLPULL_2 = 5591;
            public static final int POH_BELLPULL_3 = 5592;
            public static final int POH_BED_1 = 5593;
            public static final int POH_BED_2 = 5594;
            public static final int POH_BED_3 = 5595;
            public static final int POH_BED_4 = 5596;
            public static final int POH_BED_5 = 5597;
            public static final int POH_BED_6 = 5598;
            public static final int POH_BED_7 = 5599;
            public static final int POH_WARDROBE_1 = 5600;
            public static final int POH_WARDROBE_2 = 5601;
            public static final int POH_WARDROBE_3 = 5602;
            public static final int POH_WARDROBE_4 = 5603;
            public static final int POH_WARDROBE_5 = 5604;
            public static final int POH_WARDROBE_6 = 5605;
            public static final int POH_WARDROBE_7 = 5606;
            public static final int POH_MIRROR_1 = 5607;
            public static final int POH_MIRROR_2 = 5608;
            public static final int POH_MIRROR_3 = 5609;
            public static final int POH_MIRROR_4 = 5610;
            public static final int POH_MIRROR_5 = 5611;
            public static final int POH_MIRROR_6 = 5612;
            public static final int POH_MIRROR_7 = 5613;
            public static final int POH_CLOCK_1 = 5614;
            public static final int POH_CLOCK_2 = 5615;
            public static final int POH_CLOCK_3 = 5616;
            public static final int POH_SERVANT_MONEYBAG = 5617;
            public static final int POH_PARTY_GAME_1 = 5618;
            public static final int POH_PARTY_GAME_2 = 5619;
            public static final int POH_PARTY_GAME_3 = 5620;
            public static final int POH_PRIZE_CHEST_1 = 5621;
            public static final int POH_PRIZE_CHEST_2 = 5622;
            public static final int POH_PRIZE_CHEST_3 = 5623;
            public static final int POH_ATTACK_STONE_1 = 5624;
            public static final int POH_ATTACK_STONE_2 = 5625;
            public static final int POH_ATTACK_STONE_3 = 5626;
            public static final int POH_ELEMENTAL_BALANCE_1 = 5627;
            public static final int POH_ELEMENTAL_BALANCE_2 = 5628;
            public static final int POH_ELEMENTAL_BALANCE_3 = 5629;
            public static final int POH_RANGING_GAME_1 = 5630;
            public static final int POH_RANGING_GAME_2 = 5631;
            public static final int POH_RANGING_GAME_3 = 5632;
            public static final int POH_COMBAT_RING_1 = 5633;
            public static final int POH_COMBAT_RING_2 = 5634;
            public static final int POH_COMBAT_RING_3 = 5635;
            public static final int POH_COMBAT_RING_4 = 5636;
            public static final int POH_COMBAT_RING_5 = 5637;
            public static final int POH_WEAPON_RACK_1 = 5638;
            public static final int POH_WEAPON_RACK_2 = 5639;
            public static final int POH_WEAPON_RACK_3 = 5640;
            public static final int POH_COMBAT_DUMMY = 5641;
            public static final int POH_COMBAT_DUMMY_UNDEADSLAYER = 5642;
            public static final int POH_COMBAT_DUMMY_UPGRADED_1_UNDEAD = 5643;
            public static final int POH_STAIRS_OAK = 5644;
            public static final int POH_STAIRS_OAK_UP = 5645;
            public static final int POH_STAIRS_OAK_DOWN = 5646;
            public static final int POH_STAIRS_TEAK = 5647;
            public static final int POH_STAIRS_TEAK_UP = 5648;
            public static final int POH_STAIRS_TEAK_DOWN = 5649;
            public static final int POH_SPIRALSTAIRS_LIMESTONE = 5650;
            public static final int POH_STAIRS_MARBLE = 5651;
            public static final int POH_STAIRS_MARBLE_UP = 5652;
            public static final int POH_STAIRS_MARBLE_DOWN = 5653;
            public static final int POH_SPIRALSTAIRS_MARBLE = 5654;
            public static final int POH_TROPHY_HEAD_BLANK_TEAK = 5655;
            public static final int POH_TROPHY_HEAD_BLANK_MAHOGANY = 5656;
            public static final int POH_TROPHY_HEAD_BLANK_UPGRADETOMAHOGANY = 5657;
            public static final int POH_TROPHY_HEAD_BLANK_GILDED = 5658;
            public static final int POH_TROPHY_HEAD_BLANK_UPGRADETOGILDED = 5659;
            public static final int POH_TROPHY_CRAWLINGHAND = 5660;
            public static final int POH_TROPHY_CRAWLINGHAND_MAHOGANY = 5661;
            public static final int POH_TROPHY_CRAWLINGHAND_GILDED = 5662;
            public static final int POH_TROPHY_COCKATRICE = 5663;
            public static final int POH_TROPHY_COCKATRICE_MAHOGANY = 5664;
            public static final int POH_TROPHY_COCKATRICE_GILDED = 5665;
            public static final int POH_TROPHY_BASILISK = 5666;
            public static final int POH_TROPHY_BASILISK_MAHOGANY = 5667;
            public static final int POH_TROPHY_BASILISK_GILDED = 5668;
            public static final int POH_TROPHY_KURASK = 5669;
            public static final int POH_TROPHY_KURASK_GILDED = 5670;
            public static final int POH_TROPHY_ABYSSAL = 5671;
            public static final int POH_TROPHY_ABYSSAL_GILDED = 5672;
            public static final int POH_TROPHY_KBD = 5673;
            public static final int POH_TROPHY_KALPHITEQUEEN = 5674;
            public static final int POH_TROPHY_VORKATH = 5675;
            public static final int POH_TROPHY_ALCHEMICAL_HYDRA_HEAD = 5676;
            public static final int POH_TROPHY_FISH_BLANK_OAK = 5677;
            public static final int POH_TROPHY_FISH_BLANK_TEAK = 5678;
            public static final int POH_TROPHY_FISH_BLANK_UPGRADETOTEAK = 5679;
            public static final int POH_TROPHY_FISH_BLANK_MAHOGANY = 5680;
            public static final int POH_TROPHY_FISH_BLANK_UPGRADETOMAHOGANY = 5681;
            public static final int POH_TROPHY_BASS = 5682;
            public static final int POH_TROPHY_BASS_TEAK = 5683;
            public static final int POH_TROPHY_BASS_MAHOGANY = 5684;
            public static final int POH_TROPHY_SWORDFISH = 5685;
            public static final int POH_TROPHY_SWORDFISH_MAHOGANY = 5686;
            public static final int POH_TROPHY_SHARK = 5687;
            public static final int POH_TROPHY_HARPOONFISH = 5688;
            public static final int POH_TROPHY_HARPOONFISH_MAHOGANY = 5689;
            public static final int POH_TROPHY_ARMOUR_MITHRIL = 5690;
            public static final int POH_TROPHY_ARMOUR_ADAMANT = 5691;
            public static final int POH_TROPHY_ARMOUR_RUNE = 5692;
            public static final int POH_TROPHY_CASTLEWARS_1 = 5693;
            public static final int POH_TROPHY_CASTLEWARS_2 = 5694;
            public static final int POH_TROPHY_CASTLEWARS_3 = 5695;
            public static final int POH_TROPHY_RUNECRAFTING_1 = 5696;
            public static final int POH_TROPHY_RUNECRAFTING_2 = 5697;
            public static final int POH_TROPHY_RUNECRAFTING_3 = 5698;
            public static final int POH_PORTRAIT_1 = 5699;
            public static final int POH_PORTRAIT_2 = 5700;
            public static final int POH_PORTRAIT_3 = 5701;
            public static final int POH_PORTRAIT_4 = 5702;
            public static final int POH_LANDSCAPE_1 = 5703;
            public static final int POH_LANDSCAPE_2 = 5704;
            public static final int POH_LANDSCAPE_3 = 5705;
            public static final int POH_LANDSCAPE_4 = 5706;
            public static final int POH_LANDSCAPE_5 = 5707;
            public static final int POH_TROPHY_ANTIDRAGONBREATH = 5708;
            public static final int POH_TROPHY_AMULETOFGLORY = 5709;
            public static final int POH_TROPHY_LEGENDSCAPE = 5710;
            public static final int POH_TROPHY_MYTHICAL_CAPE = 5711;
            public static final int POH_TROPHY_SILVERLIGHT = 5712;
            public static final int POH_TROPHY_EXCALIBUR = 5713;
            public static final int POH_TROPHY_DARKLIGHT = 5714;
            public static final int POH_MAP_1 = 5715;
            public static final int POH_MAP_2 = 5716;
            public static final int POH_MAP_3 = 5717;
            public static final int POH_SYMBOL_SARADOMIN = 5718;
            public static final int POH_SYMBOL_ZAMORAK = 5719;
            public static final int POH_SYMBOL_GUTHIX = 5720;
            public static final int POH_ICON_SARADOMIN = 5721;
            public static final int POH_ICON_ZAMORAK = 5722;
            public static final int POH_ICON_GUTHIX = 5723;
            public static final int POH_ICON_BOB = 5724;
            public static final int POH_ICON_GNOMECHILD = 5725;
            public static final int POH_ALTAR_OAK = 5726;
            public static final int POH_ALTAR_TEAK = 5727;
            public static final int POH_ALTAR_TEAK_CLOTH = 5728;
            public static final int POH_ALTAR_MAHOGANY = 5729;
            public static final int POH_ALTAR_LIMESTONE = 5730;
            public static final int POH_ALTAR_MARBLE = 5731;
            public static final int POH_ALTAR_MARBLE_GILT = 5732;
            public static final int POH_TORCHES_STEEL = 5733;
            public static final int POH_TORCHES_WOODEN = 5734;
            public static final int POH_CANDLESTICKS_STEEL = 5735;
            public static final int POH_CANDLESTICKS_GILT = 5736;
            public static final int POH_INCENSE_BURNER_OAK = 5737;
            public static final int POH_INCENSE_BURNER_MAHOGANY = 5738;
            public static final int POH_INCENSE_BURNER_MARBLE = 5739;
            public static final int POH_CHAPEL_WINDOW_SHUTTERS = 5740;
            public static final int POH_CHAPEL_WINDOW_DECORATIVE = 5741;
            public static final int POH_CHAPEL_WINDOW_STAINEDGLASS = 5742;
            public static final int POH_STATUE_SAINT = 5743;
            public static final int POH_STATUE_ANGEL = 5744;
            public static final int POH_STATUE_GOD = 5745;
            public static final int POH_WINDCHIMES = 5746;
            public static final int POH_BELLS = 5747;
            public static final int POH_ORGAN = 5748;
            public static final int POH_WORKBENCH_1 = 5749;
            public static final int POH_WORKBENCH_2 = 5750;
            public static final int POH_WORKBENCH_3 = 5751;
            public static final int POH_WORKBENCH_4 = 5752;
            public static final int POH_WORKBENCH_5 = 5753;
            public static final int POH_CRAFTING_TABLE_1 = 5754;
            public static final int POH_CRAFTING_TABLE_2 = 5755;
            public static final int POH_CRAFTING_TABLE_3 = 5756;
            public static final int POH_CRAFTING_TABLE_4 = 5757;
            public static final int POH_TOOL_STORE_1 = 5758;
            public static final int POH_TOOL_STORE_2 = 5759;
            public static final int POH_TOOL_STORE_3 = 5760;
            public static final int POH_TOOL_STORE_4 = 5761;
            public static final int POH_TOOL_STORE_5 = 5762;
            public static final int POH_REPAIR_BENCH_1 = 5763;
            public static final int POH_REPAIR_BENCH_2 = 5764;
            public static final int POH_REPAIR_BENCH_3 = 5765;
            public static final int POH_HERALDRY_BENCH_1 = 5766;
            public static final int POH_HERALDRY_BENCH_2 = 5767;
            public static final int POH_HERALDRY_BENCH_3 = 5768;
            public static final int POH_LECTERN_1 = 5769;
            public static final int POH_LECTERN_2 = 5770;
            public static final int POH_LECTERN_3 = 5771;
            public static final int POH_LECTERN_4 = 5772;
            public static final int POH_LECTERN_5 = 5773;
            public static final int POH_LECTERN_6 = 5774;
            public static final int POH_LECTERN_7 = 5775;
            public static final int POH_LECTERN_8 = 5776;
            public static final int POH_GLOBE_1 = 5777;
            public static final int POH_GLOBE_2 = 5778;
            public static final int POH_GLOBE_3 = 5779;
            public static final int POH_GLOBE_4 = 5780;
            public static final int POH_GLOBE_5 = 5781;
            public static final int POH_GLOBE_6 = 5782;
            public static final int POH_GLOBE_7 = 5783;
            public static final int POH_CRYSTALBALL_1 = 5784;
            public static final int POH_CRYSTALBALL_2 = 5785;
            public static final int POH_CRYSTALBALL_3 = 5786;
            public static final int POH_WALLCHART_1 = 5787;
            public static final int POH_WALLCHART_2 = 5788;
            public static final int POH_WALLCHART_3 = 5789;
            public static final int POH_WALLCHART_4 = 5790;
            public static final int POH_TELESCOPE_1 = 5791;
            public static final int POH_TELESCOPE_2 = 5792;
            public static final int POH_TELESCOPE_3 = 5793;
            public static final int POH_PORTAL_FRAME_1 = 5794;
            public static final int POH_PORTAL_FRAME_2 = 5795;
            public static final int POH_PORTAL_FRAME_3 = 5796;
            public static final int POH_PORTAL_FRAME_LEAGUE_5 = 5797;
            public static final int POH_TELEPORT_CENTREPIECE_1 = 5798;
            public static final int POH_TELEPORT_CENTREPIECE_2 = 5799;
            public static final int POH_TELEPORT_CENTREPIECE_3 = 5800;
            public static final int POH_TELEPORT_CENTREPIECE_LEAGUE5 = 5801;
            public static final int POH_PORTAL_NEXUS_1 = 5802;
            public static final int POH_PORTAL_NEXUS_2 = 5803;
            public static final int POH_PORTAL_NEXUS_3 = 5804;
            public static final int POH_PORTAL_NEXUS_LEAGUE_5 = 5805;
            public static final int POH_AMULET_XERIC = 5806;
            public static final int POH_AMULET_DIGSITE = 5807;
            public static final int POH_THRONE_1 = 5808;
            public static final int POH_THRONE_2 = 5809;
            public static final int POH_THRONE_3 = 5810;
            public static final int POH_THRONE_4 = 5811;
            public static final int POH_THRONE_5 = 5812;
            public static final int POH_THRONE_6 = 5813;
            public static final int POH_THRONE_7 = 5814;
            public static final int POH_THRONE_ROOM_CAGE_1 = 5815;
            public static final int POH_THRONE_ROOM_CAGE_2 = 5816;
            public static final int POH_THRONE_ROOM_CAGE_3 = 5817;
            public static final int POH_THRONE_ROOM_CAGE_4 = 5818;
            public static final int POH_THRONE_ROOM_CAGE_5 = 5819;
            public static final int POH_WALL_CREST_4 = 5820;
            public static final int POH_WALL_CREST_5 = 5821;
            public static final int POH_WALL_CREST_6 = 5822;
            public static final int POH_WALL_CREST_6_DUMMY = 5823;
            public static final int POH_LEVER_OAK = 5824;
            public static final int POH_LEVER_TEAK = 5825;
            public static final int POH_LEVER_MAG = 5826;
            public static final int POH_TRAPDOOR_OAK = 5827;
            public static final int POH_TRAPDOOR_TEAK = 5828;
            public static final int POH_TRAPDOOR_MAG = 5829;
            public static final int POH_OUBLIETTE_SPIKES = 5830;
            public static final int POH_OUBLIETTE_POOL = 5831;
            public static final int POH_OUBLIETTE_FIRE = 5832;
            public static final int POH_OUB_MONSTER1 = 5833;
            public static final int POH_CAGE_DUNGEON_OAK = 5834;
            public static final int POH_CAGE_DUNGEON_OAK_STEEL = 5835;
            public static final int POH_CAGE_DUNGEON_STEEL = 5836;
            public static final int POH_CAGE_DUNGEON_STEEL_SPIKES = 5837;
            public static final int POH_CAGE_DUNGEON_BONES = 5838;
            public static final int POH_SKELETON_GUARD = 5839;
            public static final int POH_GUARD_DOG = 5840;
            public static final int POH_HOBGOBLIN = 5841;
            public static final int POH_DRAGON = 5842;
            public static final int POH_SPIDER = 5843;
            public static final int POH_TROLL = 5844;
            public static final int POH_HELLHOUND = 5845;
            public static final int POH_DUNGEON_CANDLE = 5846;
            public static final int POH_DUNGEON_TORCH = 5847;
            public static final int POH_DUNGEON_SKULLTORCH = 5848;
            public static final int POH_DUNGEON_LADDER_OAK = 5849;
            public static final int POH_DUNGEON_LADDER_TEAK = 5850;
            public static final int POH_DUNGEON_LADDER_MAG = 5851;
            public static final int POH_DUNGEON_BLOODSTAIN = 5852;
            public static final int POH_DUNGEON_PIPE = 5853;
            public static final int POH_DUNGEON_SKELETON_DECORATIVE = 5854;
            public static final int POH_SPIKE_TRAP = 5855;
            public static final int POH_MAN_TRAP = 5856;
            public static final int POH_VINE_TRAP = 5857;
            public static final int POH_MARBLE_TRAP = 5858;
            public static final int POH_TELEPORT_TRAP = 5859;
            public static final int POH_DUNGEON_DOOR_OAK = 5860;
            public static final int POH_DUNGEON_DOOR_STEEL = 5861;
            public static final int POH_DUNGEON_DOOR_MARBLE = 5862;
            public static final int POH_TREASURE_WOOD = 5863;
            public static final int POH_TREASURE_OAK = 5864;
            public static final int POH_TREASURE_TEAK = 5865;
            public static final int POH_TREASURE_MAHOGANY = 5866;
            public static final int POH_TREASURE_MAGIC = 5867;
            public static final int POH_DEMON = 5868;
            public static final int POH_KALPHITE_SOLDIER = 5869;
            public static final int POH_DAGANNOTH = 5870;
            public static final int POH_TOK_XIL = 5871;
            public static final int POH_STEEL_DRAGON = 5872;
            public static final int POH_RUNE_DRAGON = 5873;
            public static final int POH_GARDEN_CENTREPIECE_1 = 5874;
            public static final int POH_GARDEN_CENTREPIECE_2 = 5875;
            public static final int POH_GARDEN_CENTREPIECE_3 = 5876;
            public static final int POH_GARDEN_CENTREPIECE_4 = 5877;
            public static final int POH_GARDEN_CENTREPIECE_5 = 5878;
            public static final int POH_GARDEN_CENTREPIECE_5_DUMMY = 5879;
            public static final int POH_TREE_1 = 5880;
            public static final int POH_TREE_2 = 5881;
            public static final int POH_TREE_3 = 5882;
            public static final int POH_TREE_4 = 5883;
            public static final int POH_TREE_5 = 5884;
            public static final int POH_TREE_6 = 5885;
            public static final int POH_TREE_7 = 5886;
            public static final int POH_PLANTBIG1A = 5887;
            public static final int POH_PLANTBIG1B = 5888;
            public static final int POH_PLANTBIG1C = 5889;
            public static final int POH_PLANTBIG2A = 5890;
            public static final int POH_PLANTBIG2B = 5891;
            public static final int POH_PLANTBIG2C = 5892;
            public static final int POH_PLANTSMALL1A = 5893;
            public static final int POH_PLANTSMALL1B = 5894;
            public static final int POH_PLANTSMALL1C = 5895;
            public static final int POH_PLANTSMALL2A = 5896;
            public static final int POH_PLANTSMALL2B = 5897;
            public static final int POH_PLANTSMALL2C = 5898;
            public static final int POH_TIPJAR = 5899;
            public static final int POH_FORMAL_GARDEN_CENTREPIECE_2 = 5900;
            public static final int POH_FORMAL_GARDEN_CENTREPIECE_3 = 5901;
            public static final int POH_FORMAL_GARDEN_CENTREPIECE_4 = 5902;
            public static final int POH_FORMAL_GARDEN_CENTREPIECE_5 = 5903;
            public static final int POH_FENCING1 = 5904;
            public static final int POH_FENCING2 = 5905;
            public static final int POH_FENCING3 = 5906;
            public static final int POH_FENCING4 = 5907;
            public static final int POH_FENCING5 = 5908;
            public static final int POH_FENCING6 = 5909;
            public static final int POH_FENCING7 = 5910;
            public static final int POH_HEDGE1 = 5911;
            public static final int POH_HEDGE2 = 5912;
            public static final int POH_HEDGE3 = 5913;
            public static final int POH_HEDGE4 = 5914;
            public static final int POH_HEDGE5 = 5915;
            public static final int POH_HEDGE6 = 5916;
            public static final int POH_HEDGE7 = 5917;
            public static final int POH_FLOWERA1 = 5918;
            public static final int POH_FLOWERA2 = 5919;
            public static final int POH_FLOWERA3 = 5920;
            public static final int POH_FLOWERB1 = 5921;
            public static final int POH_FLOWERB2 = 5922;
            public static final int POH_FLOWERB3 = 5923;
            public static final int POH_COS_ROOM_CAPE_RACK_OAK = 5924;
            public static final int POH_COS_ROOM_CAPE_RACK_TEAK = 5925;
            public static final int POH_COS_ROOM_CAPE_RACK_MAHOGANY = 5926;
            public static final int POH_COS_ROOM_CAPE_RACK_MAHOGANY_GILDED = 5927;
            public static final int POH_COS_ROOM_CAPE_RACK_MARBLE = 5928;
            public static final int POH_COS_ROOM_CAPE_RACK_MAGIC_STONE = 5929;
            public static final int POH_COS_ROOM_MAGIC_WARDROBE_OAK = 5930;
            public static final int POH_COS_ROOM_MAGIC_WARDROBE_CARVED_OAK = 5931;
            public static final int POH_COS_ROOM_MAGIC_WARDROBE_TEAK = 5932;
            public static final int POH_COS_ROOM_MAGIC_WARDROBE_TEAK_CARVED = 5933;
            public static final int POH_COS_ROOM_MAGIC_WARDROBE_MAHOGANY = 5934;
            public static final int POH_COS_ROOM_MAGIC_WARDROBE_MAHOGANY_GILDED = 5935;
            public static final int POH_COS_ROOM_MAGIC_WARDROBE_MARBLE = 5936;
            public static final int POH_COS_ROOM_TOY_BOX_OAK = 5937;
            public static final int POH_COS_ROOM_TOY_BOX_TEAK = 5938;
            public static final int POH_COS_ROOM_TOY_BOX_MAHOGANY = 5939;
            public static final int POH_COS_ROOM_TREASURE_CHEST_OAK = 5940;
            public static final int POH_COS_ROOM_TREASURE_CHEST_TEAK = 5941;
            public static final int POH_COS_ROOM_TREASURE_CHEST_MAHOGANY = 5942;
            public static final int POH_COS_ROOM_FANCY_DRESS_BOX_OAK = 5943;
            public static final int POH_COS_ROOM_FANCY_DRESS_BOX_TEAK = 5944;
            public static final int POH_COS_ROOM_FANCY_DRESS_BOX_MAHOGANY = 5945;
            public static final int POH_COS_ROOM_ARMOUR_CASE_OAK = 5946;
            public static final int POH_COS_ROOM_ARMOUR_CASE_TEAK = 5947;
            public static final int POH_COS_ROOM_ARMOUR_CASE_MAHOGANY = 5948;
            public static final int POH_MENAGERIE_PETHOUSE_1 = 5949;
            public static final int POH_MENAGERIE_PETHOUSE_2 = 5950;
            public static final int POH_MENAGERIE_PETHOUSE_3 = 5951;
            public static final int POH_MENAGERIE_PETHOUSE_4 = 5952;
            public static final int POH_MENAGERIE_PETHOUSE_5 = 5953;
            public static final int POH_MENAGERIE_PETHOUSE_6 = 5954;
            public static final int POH_MENAGERIE_SCRATCHINGPOST_1 = 5955;
            public static final int POH_MENAGERIE_SCRATCHINGPOST_2 = 5956;
            public static final int POH_MENAGERIE_SCRATCHINGPOST_3 = 5957;
            public static final int POH_MENAGERIE_COMBATRING_1 = 5958;
            public static final int POH_MENAGERIE_COMBATRING_2 = 5959;
            public static final int POH_MENAGERIE_COMBATRING_3 = 5960;
            public static final int POH_MENAGERIE_PETLIST_1 = 5961;
            public static final int POH_MENAGERIE_PETFEEDER_1 = 5962;
            public static final int POH_MENAGERIE_PETFEEDER_2 = 5963;
            public static final int POH_MENAGERIE_PETFEEDER_3 = 5964;
            public static final int POH_MENAGERIE_HABITAT_1 = 5965;
            public static final int POH_MENAGERIE_HABITAT_2 = 5966;
            public static final int POH_MENAGERIE_HABITAT_3 = 5967;
            public static final int POH_MENAGERIE_HABITAT_4 = 5968;
            public static final int POH_MENAGERIE_HABITAT_5 = 5969;
            public static final int POH_SUPERIOR_GARDEN_TELEPORT_TREE = 5970;
            public static final int POH_SUPERIOR_GARDEN_TELEPORT_RING = 5971;
            public static final int POH_SUPERIOR_GARDEN_TELEPORT_TREERING = 5972;
            public static final int POH_SUPERIOR_GARDEN_WILDERNESS_OBELISK = 5973;
            public static final int LEAGUE_5_POH_SUPERIOR_GARDEN_TELEPORT_TREERING = 5974;
            public static final int LEAGUE_5_POH_SUPERIOR_GARDEN_TELEPORT_TREE = 5975;
            public static final int POH_SUPERIOR_GARDEN_TOPIARY = 5976;
            public static final int POH_SUPERIOR_GARDEN_POOL_RESTORATION = 5977;
            public static final int POH_SUPERIOR_GARDEN_POOL_REVITALISATION = 5978;
            public static final int POH_SUPERIOR_GARDEN_POOL_REJUVENATION = 5979;
            public static final int POH_SUPERIOR_GARDEN_POOL_RECOVERY = 5980;
            public static final int POH_SUPERIOR_GARDEN_POOL_REGENERATION = 5981;
            public static final int POH_SUPERIOR_GARDEN_THEME_ZEN = 5982;
            public static final int POH_SUPERIOR_GARDEN_THEME_ZANARIS = 5983;
            public static final int POH_SUPERIOR_GARDEN_THEME_TZHAAR = 5984;
            public static final int POH_SUPERIOR_GARDEN_REDWOOD_FENCE = 5985;
            public static final int POH_SUPERIOR_GARDEN_OBSIDIAN_FENCE = 5986;
            public static final int POH_SUPERIOR_GARDEN_BENCH_TEAK = 5987;
            public static final int POH_SUPERIOR_GARDEN_BENCH_MAHOGANY = 5988;
            public static final int POH_SUPERIOR_GARDEN_BENCH_MARBLE = 5989;
            public static final int POH_SUPERIOR_GARDEN_BENCH_OBSIDIAN = 5990;
            public static final int POH_ACHIEVEMENTGALLERY_ALTAR_ANCIENT = 5991;
            public static final int POH_ACHIEVEMENTGALLERY_ALTAR_LUNAR = 5992;
            public static final int POH_ACHIEVEMENTGALLERY_ALTAR_ARCEUUS = 5993;
            public static final int POH_ACHIEVEMENTGALLERY_ALTAR_ECUMENICAL_FROMANCIENT = 5994;
            public static final int POH_ACHIEVEMENTGALLERY_ALTAR_ECUMENICAL_FROMLUNAR = 5995;
            public static final int POH_ACHIEVEMENTGALLERY_ALTAR_ECUMENICAL_FROMARCEUUS = 5996;
            public static final int POH_ACHIEVEMENTGALLERY_ADVENTURELOG_1 = 5997;
            public static final int POH_ACHIEVEMENTGALLERY_ADVENTURELOG_2 = 5998;
            public static final int POH_ACHIEVEMENTGALLERY_ADVENTURELOG_3 = 5999;
            public static final int POH_ACHIEVEMENTGALLERY_JEWELLERYBOX_1 = 6000;
            public static final int POH_ACHIEVEMENTGALLERY_JEWELLERYBOX_2 = 6001;
            public static final int POH_ACHIEVEMENTGALLERY_JEWELLERYBOX_3 = 6002;
            public static final int POH_ACHIEVEMENTGALLERY_BOSSLAIR_BLANK = 6003;
            public static final int POH_ACHIEVEMENTGALLERY_MOUNTEDDISPLAY_EMBLEM = 6004;
            public static final int POH_ACHIEVEMENTGALLERY_MOUNTEDDISPLAY_COINS = 6005;
            public static final int POH_ACHIEVEMENTGALLERY_MOUNTEDDISPLAY_CAPESTAND = 6006;
            public static final int POH_ACHIEVEMENTGALLERY_QUESTLIST = 6007;
            public static final int POH_LEAGUEHALL_TROPHY_PEDESTAL_SIMPLE = 6008;
            public static final int POH_LEAGUEHALL_TROPHY_PEDESTAL_DECORATIVE = 6009;
            public static final int POH_LEAGUEHALL_RUG_SIMPLE = 6010;
            public static final int POH_LEAGUEHALL_RUG_DECORATIVE = 6011;
            public static final int POH_LEAGUEHALL_RUG_TRAILBLAZER = 6012;
            public static final int POH_LEAGUEHALL_TROPHYCASE_OAK = 6013;
            public static final int POH_LEAGUEHALL_TROPHYCASE_MAHOGANY = 6014;
            public static final int POH_LEAGUEHALL_BANNERSTAND_SIMPLE = 6015;
            public static final int POH_LEAGUEHALL_BANNERSTAND_DECORATIVE = 6016;
            public static final int POH_LEAGUEHALL_OUTFITSTAND_OAK = 6017;
            public static final int POH_LEAGUEHALL_OUTFITSTAND_MAHOGANY = 6018;
            public static final int POH_LEAGUEHALL_STATUE_SIMPLE = 6019;
            public static final int POH_LEAGUEHALL_STATUE_DECORATIVE = 6020;
            public static final int POH_LEAGUEHALL_STATUE_TRAILBLAZER = 6021;
            public static final int POH_LEAGUEHALL_ACCOMPLISHMENT_SCROLL = 6022;
            public static final int HW20_POH_PUMPKIN_BUILD = 6023;
            public static final int FORESTRY_POH_BEEHIVE01 = 6024;
            public static final int FORESTRY_POH_BEEHIVE02 = 6025;
            public static final int MY2ARM_COFFIN = 6026;
            public static final int POH_GREENMAN_WALL_DECORATION = 6027;
            public static final int POH_GREENMAN_STATUE = 6028;
            public static final int POH_ICON_BINGO = 7024;
            public static final int POH_INCENSE_BURNER_BINGO = 7025;
            public static final int POH_TROPHY_GIANT_KRILL = 7119;
            public static final int POH_TROPHY_GIANT_KRILL_MAHOGANY = 7120;
            public static final int POH_TROPHY_HADDOCK = 7121;
            public static final int POH_TROPHY_HADDOCK_MAHOGANY = 7122;
            public static final int POH_TROPHY_YELLOWFIN = 7123;
            public static final int POH_TROPHY_YELLOWFIN_MAHOGANY = 7124;
            public static final int POH_TROPHY_HALIBUT = 7125;
            public static final int POH_TROPHY_BLUEFIN = 7126;
            public static final int POH_TROPHY_MARLIN = 7127;
            public static final int POH_GARDEN_CENTREPIECE_6 = 9654;
            public static final int POH_THRONE_8 = 16324;
        }
    }

    public static final class PohRoom {
        private PohRoom() {}

        public static final int ID = 111;
        public static final int COL_NAME = 0;
        public static final int COL_NAME_UPPERCASE = 1;
        public static final int COL_COST = 2;
        public static final int COL_ROOM_TYPE = 3;
        public static final int COL_LEVEL_REQUIREMENT = 4;
        public static final int COL_SOURCE_OFFSET = 5;
        public static final int COL_DOOR_LOCATIONS = 6;
        public static final int COL_HOTSPOT = 7;
        public static final int COL_FLOOR_RESTRICTION = 8;
        public static final int COL_HAS_ROOF = 9;
        public static final int COL_ROOM_OBJ = 10;
        public static final int COL_BUTTON = 11;

        public static final class Row {
            private Row() {}

            public static final int POH_DUMMY_NULL = 4165;
            public static final int POH_DUMMY_PARLOUR = 4166;
            public static final int POH_DUMMY_KITCHEN = 4168;
            public static final int POH_DUMMY_DINING_ROOM = 4169;
            public static final int POH_DUMMY_BEDROOM = 4170;
            public static final int POH_DUMMY_GAMES_ROOM = 4171;
            public static final int POH_DUMMY_HALL1 = 4172;
            public static final int POH_DUMMY_HALL1A = 4173;
            public static final int POH_DUMMY_HALL2 = 4174;
            public static final int POH_DUMMY_HALL2A = 4175;
            public static final int POH_DUMMY_CHAPEL = 4176;
            public static final int POH_DUMMY_WORKSHOP = 4177;
            public static final int POH_DUMMY_STUDY = 4178;
            public static final int POH_DUMMY_PORTALROOM = 4179;
            public static final int POH_DUMMY_THRONEROOM = 4180;
            public static final int POH_DUMMY_OUBLIETTE = 4181;
            public static final int POH_DUMMY_DUNGEON_CORRIDOR = 4182;
            public static final int POH_DUMMY_DUNGEON_CROSS = 4183;
            public static final int POH_DUMMY_DUNGEON_STAIRS = 4184;
            public static final int POH_DUMMY_DUNGEON_TREASURE = 4185;
            public static final int POH_DUMMY_COMBAT_ROOM = 4187;
            public static final int POH_DUMMY_COSTUME_ROOM = 4188;
            public static final int POH_DUMMY_MENAGERIE_INDOORS = 4189;
            public static final int POH_DUMMY_ACHIEVEMENT_GALLERY = 4192;
            public static final int POH_DUMMY_PORTALNEXUS = 4193;
            public static final int POH_DUMMY_LEAGUEHALL = 4194;
        }
    }

    public static final class PohHotspot {
        private PohHotspot() {}

        public static final int ID = 112;
        public static final int COL_BUILDDATA = 0;

        public static final class Row {
            private Row() {}

            public static final int NULL_HOTSPOT = 6029;
        }
    }

    public static final class SlayerTask {
        private SlayerTask() {}

        public static final int ID = 113;
        public static final int COL_ID = 0;
        public static final int COL_MIN_COMLEVEL = 1;
        public static final int COL_MIN_STAT_REQUIREMENT_ALL = 2;
        public static final int COL_MIN_STAT_REQUIREMENT_ANY = 3;
        public static final int COL_LEAGUES_MIN_COMLEVEL = 4;
        public static final int COL_LEAGUES_MAX_COMLEVEL = 5;
        public static final int COL_QUESTS_REQUIRED_ALL = 6;
        public static final int COL_QUESTS_REQUIRED_ANY = 7;
        public static final int COL_REGIONS = 8;
        public static final int COL_NAME_LOWERCASE = 9;
        public static final int COL_NAME_UPPERCASE = 10;
        public static final int COL_SLAYER_TIP = 11;
        public static final int COL_TWISTED_MIN_COMLEVEL = 12;
        public static final int COL_EQUIPMENT_REQUIRED = 13;
        public static final int COL_TASK_SUBLIST = 14;
        public static final int COL_EXTENSION_MIN_MAX = 15;
        public static final int COL_EXTENSION_ADDITIVE = 16;
        public static final int COL_UNLOCK_WEIGHTING = 17;
        public static final int COL_BLOCK_UNLOCK = 18;
        public static final int COL_RELATED_CONTENT = 19;

        public static final class Row {
            private Row() {}

            public static final int SLAYER_TASK_KREEARRA = 545;
            public static final int SLAYER_TASK_ZILYANA = 6155;
            public static final int SLAYER_TASK_GRAARDOR = 6157;
            public static final int SLAYER_TASK_KRILTSUTSAROTH = 6159;
            public static final int SLAYER_TASK_DAGANNOTHKINGS = 6161;
            public static final int SLAYER_TASK_MOLE = 6163;
            public static final int SLAYER_TASK_KQ = 6165;
            public static final int SLAYER_TASK_KBD = 6167;
            public static final int SLAYER_TASK_CALLISTO = 6169;
            public static final int SLAYER_TASK_VENENATIS = 6171;
            public static final int SLAYER_TASK_VETION = 6173;
            public static final int SLAYER_TASK_CHAOSELEMENTAL = 6175;
            public static final int SLAYER_TASK_CHAOSFANATIC = 6177;
            public static final int SLAYER_TASK_CRAZYARCHAEOLOGIST = 6179;
            public static final int SLAYER_TASK_SCORPIA = 6181;
            public static final int SLAYER_TASK_ZULRAH = 6183;
            public static final int SLAYER_TASK_BARROWSBROTHERS = 6185;
            public static final int SLAYER_TASK_CAVEKRAKENBOSS = 6187;
            public static final int SLAYER_TASK_SMOKEDEVILBOSS = 6189;
            public static final int SLAYER_TASK_CERBERUS = 6191;
            public static final int SLAYER_TASK_ABYSSALSIRE = 6193;
            public static final int SLAYER_TASK_GROTESQUEGUARDIANS = 6195;
            public static final int SLAYER_TASK_VORKATH = 6197;
            public static final int SLAYER_TASK_HYDRABOSS = 6199;
            public static final int SLAYER_TASK_SARACHNIS = 6201;
            public static final int SLAYER_TASK_MUSPAH = 6203;
            public static final int SLAYER_TASK_VARDORVIS = 6205;
            public static final int SLAYER_TASK_DUKESUCELLUS = 6207;
            public static final int SLAYER_TASK_WHISPERER = 6209;
            public static final int SLAYER_TASK_LEVIATHAN = 6211;
            public static final int SLAYER_TASK_ARAXXOR = 6213;
            public static final int SLAYER_TARGET_MONKEYS = 6214;
            public static final int SLAYER_TARGET_GOBLINS = 6215;
            public static final int SLAYER_TARGET_RATS = 6216;
            public static final int SLAYER_TARGET_SPIDERS = 6217;
            public static final int SLAYER_TARGET_BIRDS = 6218;
            public static final int SLAYER_TARGET_COWS = 6219;
            public static final int SLAYER_TARGET_SCORPIONS = 6220;
            public static final int SLAYER_TARGET_BATS = 6221;
            public static final int SLAYER_TARGET_WOLVES = 6222;
            public static final int SLAYER_TARGET_ZOMBIES = 6223;
            public static final int SLAYER_TARGET_SKELETONS = 6224;
            public static final int SLAYER_TARGET_GHOSTS = 6225;
            public static final int SLAYER_TARGET_BEARS = 6226;
            public static final int SLAYER_TARGET_HILLGIANTS = 6227;
            public static final int SLAYER_TARGET_ICEGIANTS = 6228;
            public static final int SLAYER_TARGET_FIREGIANTS = 6229;
            public static final int SLAYER_TARGET_MOSSGIANTS = 6230;
            public static final int SLAYER_TARGET_TROLLS = 6231;
            public static final int SLAYER_TARGET_ICEWARRIORS = 6232;
            public static final int SLAYER_TARGET_OGRES = 6233;
            public static final int SLAYER_TARGET_HOBGOBLINS = 6234;
            public static final int SLAYER_TARGET_DOGS = 6235;
            public static final int SLAYER_TARGET_GHOULS = 6236;
            public static final int SLAYER_TARGET_GREENDRAGONS = 6237;
            public static final int SLAYER_TARGET_BLUEDRAGONS = 6238;
            public static final int SLAYER_TARGET_REDDRAGONS = 6239;
            public static final int SLAYER_TARGET_BLACKDRAGONS = 6240;
            public static final int SLAYER_TARGET_LESSERDEMONS = 6241;
            public static final int SLAYER_TARGET_GREATERDEMONS = 6242;
            public static final int SLAYER_TARGET_BLACKDEMONS = 6243;
            public static final int SLAYER_TARGET_HELLHOUNDS = 6244;
            public static final int SLAYER_TARGET_SHADOWWARRIORS = 6245;
            public static final int SLAYER_TARGET_WEREWOLVES = 6246;
            public static final int SLAYER_TARGET_VAMPYRES = 6247;
            public static final int SLAYER_TARGET_DAGANNOTH = 6248;
            public static final int SLAYER_TARGET_TUROTH = 6249;
            public static final int SLAYER_TARGET_CAVECRAWLERS = 6250;
            public static final int SLAYER_TARGET_BANSHEES = 6251;
            public static final int SLAYER_TARGET_CRAWLINGHANDS = 6252;
            public static final int SLAYER_TARGET_INFERNALMAGES = 6253;
            public static final int SLAYER_TARGET_ABERRANTSPECTRES = 6254;
            public static final int SLAYER_TARGET_ABYSSALDEMONS = 6255;
            public static final int SLAYER_TARGET_BASILISKS = 6256;
            public static final int SLAYER_TARGET_COCKATRICE = 6257;
            public static final int SLAYER_TARGET_KURASK = 6258;
            public static final int SLAYER_TARGET_GARGOYLES = 6259;
            public static final int SLAYER_TARGET_PYREFIENDS = 6260;
            public static final int SLAYER_TARGET_BLOODVELD = 6261;
            public static final int SLAYER_TARGET_DUSTDEVILS = 6262;
            public static final int SLAYER_TARGET_JELLIES = 6263;
            public static final int SLAYER_TARGET_ROCKSLUGS = 6264;
            public static final int SLAYER_TARGET_NECHRYAEL = 6265;
            public static final int SLAYER_TARGET_KALPHITE = 6266;
            public static final int SLAYER_TARGET_EARTHWARRIORS = 6267;
            public static final int SLAYER_TARGET_OTHERWORLDLYBEINGS = 6268;
            public static final int SLAYER_TARGET_ELVES = 6269;
            public static final int SLAYER_TARGET_DWARVES = 6270;
            public static final int SLAYER_TARGET_BRONZEDRAGONS = 6271;
            public static final int SLAYER_TARGET_IRONDRAGONS = 6272;
            public static final int SLAYER_TARGET_STEELDRAGONS = 6273;
            public static final int SLAYER_TARGET_WALLBEASTS = 6274;
            public static final int SLAYER_TARGET_CAVESLIMES = 6275;
            public static final int SLAYER_TARGET_CAVEBUGS = 6276;
            public static final int SLAYER_TARGET_SHADES = 6277;
            public static final int SLAYER_TARGET_CROCODILES = 6278;
            public static final int SLAYER_TARGET_DARKBEASTS = 6279;
            public static final int SLAYER_TARGET_MOGRES = 6280;
            public static final int SLAYER_TARGET_LIZARDS = 6281;
            public static final int SLAYER_TARGET_FEVERSPIDERS = 6282;
            public static final int SLAYER_TARGET_HARPIEBUGSWARMS = 6283;
            public static final int SLAYER_TARGET_SEASNAKES = 6284;
            public static final int SLAYER_TARGET_SKELETALWYVERNS = 6285;
            public static final int SLAYER_TARGET_KILLERWATTS = 6286;
            public static final int SLAYER_TARGET_MUTATEDZYGOMITES = 6287;
            public static final int SLAYER_TARGET_ICEFIENDS = 6288;
            public static final int SLAYER_TARGET_MINOTAURS = 6289;
            public static final int SLAYER_TARGET_FLESHCRAWLERS = 6290;
            public static final int SLAYER_TARGET_CATABLEPON = 6291;
            public static final int SLAYER_TARGET_ANKOU = 6292;
            public static final int SLAYER_TARGET_CAVEHORRORS = 6293;
            public static final int SLAYER_TARGET_JUNGLEHORRORS = 6294;
            public static final int SLAYER_TARGET_SUQAHS = 6295;
            public static final int SLAYER_TARGET_BRINERATS = 6296;
            public static final int SLAYER_TARGET_SCABARITES = 6297;
            public static final int SLAYER_TARGET_TERRORDOGS = 6298;
            public static final int SLAYER_TARGET_MOLANISKS = 6299;
            public static final int SLAYER_TARGET_WATERFIENDS = 6300;
            public static final int SLAYER_TARGET_SPIRITUALGWD = 6301;
            public static final int SLAYER_TARGET_LIZARDMEN = 6302;
            public static final int SLAYER_TARGET_MAGICAXES = 6303;
            public static final int SLAYER_TARGET_CAVEKRAKEN = 6304;
            public static final int SLAYER_TARGET_MITHRILDRAGONS = 6305;
            public static final int SLAYER_TARGET_AVIANSIES = 6306;
            public static final int SLAYER_TARGET_SMOKEDEVIL = 6307;
            public static final int SLAYER_TARGET_TZHAAR = 6308;
            public static final int SLAYER_TARGET_ZUK = 6309;
            public static final int SLAYER_TARGET_JAD = 6310;
            public static final int SLAYER_TARGET_BOSS = 6311;
            public static final int SLAYER_TARGET_MAMMOTHS = 6312;
            public static final int SLAYER_TARGET_ROGUES = 6313;
            public static final int SLAYER_TARGET_ENTS = 6314;
            public static final int SLAYER_TARGET_BANDITS = 6315;
            public static final int SLAYER_TARGET_DARKWARRIORS = 6316;
            public static final int SLAYER_TARGET_LAVADRAGONS = 6317;
            public static final int SLAYER_TARGET_FOSSILWYVERNS = 6318;
            public static final int SLAYER_TARGET_REVENANTS = 6319;
            public static final int SLAYER_TARGET_ADAMANTDRAGONS = 6320;
            public static final int SLAYER_TARGET_RUNEDRAGONS = 6321;
            public static final int SLAYER_TARGET_METALDRAGONS = 6322;
            public static final int SLAYER_TARGET_CHAOSDRUIDS = 6323;
            public static final int SLAYER_TARGET_WYRMS = 6324;
            public static final int SLAYER_TARGET_DRAKES = 6325;
            public static final int SLAYER_TARGET_HYDRAS = 6326;
            public static final int SLAYER_TARGET_TEMPLESPIDERS = 6327;
            public static final int SLAYER_TARGET_UNDEADDRUIDS = 6328;
            public static final int SLAYER_TARGET_SULPHURLIZARDS = 6329;
            public static final int SLAYER_TARGET_BRUTALBLACKDRAGONS = 6330;
            public static final int SLAYER_TARGET_BRUTALDRAGONS = 6331;
            public static final int SLAYER_TARGET_SANDCRABS = 6332;
            public static final int SLAYER_TARGET_CRABS = 6333;
            public static final int SLAYER_TARGET_BLACKKNIGHT = 6334;
            public static final int SLAYER_TARGET_PIRATES = 6335;
            public static final int SLAYER_TARGET_SOURHOGS = 6336;
            public static final int SLAYER_TARGET_WARPEDCREATURES = 6337;
            public static final int SLAYER_TARGET_LESSER_NAGUA = 6338;
            public static final int SLAYER_TARGET_ARAXYTES = 6339;
            public static final int SLAYER_TARGET_CUSTODIANS = 6340;
            public static final int SLAYER_TASK_GRYPHONBOSS = 9405;
            public static final int SLAYER_TARGET_GRYPHONS = 9414;
            public static final int SLAYER_TARGET_AQUANITES = 9415;
            public static final int SLAYER_TARGET_FROST_DRAGONS = 9416;
            public static final int SLAYER_TASK_MAGGOT_KING = 16939;
        }
    }

    public static final class SlayerMasterTask {
        private SlayerMasterTask() {}

        public static final int ID = 114;
        public static final int COL_MASTER_ID = 0;
        public static final int COL_TASK = 1;
        public static final int COL_WEIGHT = 2;
        public static final int COL_MIN_AMOUNT = 3;
        public static final int COL_MAX_AMOUNT = 4;
        public static final int COL_AREAS = 5;
        public static final int COL_TASK_UNLOCK = 6;

        public static final class Row {
            private Row() {}

            public static final int CHAELDAR_CUSTODIANS = 4097;
            public static final int NIEVE_CUSTODIANS = 4098;
            public static final int KONAR_LESSER_NAGUA = 6450;
            public static final int KONAR_MUTATEDZYGOMITES = 6454;
            public static final int KONAR_BRINERATS = 6455;
            public static final int KONAR_WATERFIENDS = 6456;
            public static final int KONAR_TUROTH = 6457;
            public static final int KONAR_KURASK = 6458;
            public static final int KONAR_BLUEDRAGONS = 6459;
            public static final int KONAR_SKELETALWYVERNS = 6460;
            public static final int KONAR_FOSSILWYVERNS = 6461;
            public static final int KONAR_ANKOU = 6462;
            public static final int KONAR_DARKBEASTS = 6464;
            public static final int KONAR_BLACKDRAGONS = 6465;
            public static final int KONAR_JELLIES = 6466;
            public static final int KONAR_TROLLS = 6467;
            public static final int KONAR_ABERRANTSPECTRES = 6468;
            public static final int KONAR_DUSTDEVILS = 6469;
            public static final int KONAR_GARGOYLES = 6470;
            public static final int KONAR_GREATERDEMONS = 6472;
            public static final int KONAR_SMOKEDEVIL = 6473;
            public static final int KONAR_NECHRYAEL = 6474;
            public static final int KONAR_HELLHOUNDS = 6476;
            public static final int KONAR_DAGANNOTH = 6477;
            public static final int KONAR_BOSS = 6478;
            public static final int KONAR_WYRMS = 6479;
            public static final int KONAR_DRAKES = 6480;
            public static final int KONAR_HYDRAS = 6481;
            public static final int KONAR_CAVEKRAKEN = 6482;
            public static final int KONAR_FIREGIANTS = 6483;
            public static final int KONAR_KALPHITE = 6484;
            public static final int KONAR_BLOODVELD = 6485;
            public static final int KONAR_BLACKDEMONS = 6486;
            public static final int KONAR_ABYSSALDEMONS = 6487;
            public static final int KONAR_METALDRAGONS = 6488;
            public static final int DURADEL_ARAXYTES = 6489;
            public static final int DURADEL_WARPEDCREATURES = 6490;
            public static final int DURADEL_VAMPYRES = 6491;
            public static final int DURADEL_BASILISKS = 6492;
            public static final int DURADEL_DRAKES = 6493;
            public static final int DURADEL_WYRMS = 6494;
            public static final int DURADEL_REDDRAGONS = 6495;
            public static final int DURADEL_LIZARDMEN = 6496;
            public static final int DURADEL_ELVES = 6497;
            public static final int DURADEL_BLUEDRAGONS = 6498;
            public static final int DURADEL_WATERFIENDS = 6499;
            public static final int DURADEL_CAVEHORRORS = 6500;
            public static final int DURADEL_TROLLS = 6501;
            public static final int DURADEL_DUSTDEVILS = 6502;
            public static final int DURADEL_KURASK = 6503;
            public static final int DURADEL_ANKOU = 6504;
            public static final int DURADEL_MUTATEDZYGOMITES = 6505;
            public static final int DURADEL_HELLHOUNDS = 6506;
            public static final int DURADEL_DAGANNOTH = 6507;
            public static final int DURADEL_FIREGIANTS = 6508;
            public static final int DURADEL_SUQAHS = 6509;
            public static final int DURADEL_SKELETALWYVERNS = 6510;
            public static final int DURADEL_FOSSILWYVERNS = 6511;
            public static final int DURADEL_GREATERDEMONS = 6512;
            public static final int DURADEL_BLACKDRAGONS = 6513;
            public static final int DURADEL_AVIANSIES = 6514;
            public static final int DURADEL_SPIRITUALGWD = 6515;
            public static final int DURADEL_BLOODVELD = 6516;
            public static final int DURADEL_BLACKDEMONS = 6517;
            public static final int DURADEL_GARGOYLES = 6518;
            public static final int DURADEL_ABERRANTSPECTRES = 6519;
            public static final int DURADEL_CAVEKRAKEN = 6520;
            public static final int DURADEL_SMOKEDEVIL = 6521;
            public static final int DURADEL_NECHRYAEL = 6522;
            public static final int DURADEL_KALPHITE = 6523;
            public static final int DURADEL_ABYSSALDEMONS = 6524;
            public static final int DURADEL_DARKBEASTS = 6525;
            public static final int DURADEL_BOSS = 6526;
            public static final int DURADEL_TZHAAR = 6527;
            public static final int DURADEL_METALDRAGONS = 6528;
            public static final int VANNAKA_CRABS = 6529;
            public static final int VANNAKA_SPIRITUALGWD = 6530;
            public static final int VANNAKA_ABYSSALDEMONS = 6531;
            public static final int VANNAKA_NECHRYAEL = 6532;
            public static final int VANNAKA_GARGOYLES = 6533;
            public static final int VANNAKA_HOBGOBLINS = 6534;
            public static final int VANNAKA_HILLGIANTS = 6535;
            public static final int VANNAKA_GHOULS = 6536;
            public static final int VANNAKA_ICEWARRIORS = 6537;
            public static final int VANNAKA_OTHERWORLDLYBEINGS = 6538;
            public static final int VANNAKA_COCKATRICE = 6539;
            public static final int VANNAKA_SHADES = 6540;
            public static final int VANNAKA_PYREFIENDS = 6541;
            public static final int VANNAKA_MOGRES = 6542;
            public static final int VANNAKA_MOLANISKS = 6543;
            public static final int VANNAKA_VAMPYRES = 6544;
            public static final int VANNAKA_BASILISKS = 6545;
            public static final int VANNAKA_FEVERSPIDERS = 6546;
            public static final int VANNAKA_BRINERATS = 6547;
            public static final int VANNAKA_INFERNALMAGES = 6548;
            public static final int VANNAKA_JELLIES = 6549;
            public static final int VANNAKA_ICEGIANTS = 6550;
            public static final int VANNAKA_ANKOU = 6551;
            public static final int VANNAKA_TUROTH = 6552;
            public static final int VANNAKA_JUNGLEHORRORS = 6553;
            public static final int VANNAKA_LESSERDEMONS = 6554;
            public static final int VANNAKA_TERRORDOGS = 6555;
            public static final int VANNAKA_ABERRANTSPECTRES = 6556;
            public static final int VANNAKA_BLUEDRAGONS = 6557;
            public static final int VANNAKA_DUSTDEVILS = 6558;
            public static final int VANNAKA_HELLHOUNDS = 6559;
            public static final int VANNAKA_TROLLS = 6560;
            public static final int VANNAKA_MOSSGIANTS = 6561;
            public static final int VANNAKA_SEASNAKES = 6562;
            public static final int VANNAKA_OGRES = 6563;
            public static final int VANNAKA_CROCODILES = 6564;
            public static final int VANNAKA_FIREGIANTS = 6565;
            public static final int VANNAKA_KALPHITE = 6566;
            public static final int VANNAKA_KURASK = 6567;
            public static final int VANNAKA_BLOODVELD = 6568;
            public static final int VANNAKA_WEREWOLVES = 6569;
            public static final int VANNAKA_SHADOWWARRIORS = 6570;
            public static final int VANNAKA_ELVES = 6571;
            public static final int VANNAKA_DAGANNOTH = 6572;
            public static final int VANNAKA_HARPIEBUGSWARMS = 6573;
            public static final int KRYSTILLIA_ABYSSALDEMONS = 6574;
            public static final int KRYSTILLIA_DUSTDEVILS = 6575;
            public static final int KRYSTILLIA_NECHRYAEL = 6576;
            public static final int KRYSTILLIA_JELLIES = 6577;
            public static final int KRYSTILLIA_PIRATES = 6578;
            public static final int KRYSTILLIA_BLACKKNIGHT = 6579;
            public static final int KRYSTILLIA_ZOMBIES = 6580;
            public static final int KRYSTILLIA_MOSSGIANTS = 6581;
            public static final int KRYSTILLIA_HILLGIANTS = 6582;
            public static final int KRYSTILLIA_BLOODVELD = 6583;
            public static final int KRYSTILLIA_CHAOSDRUIDS = 6584;
            public static final int KRYSTILLIA_LESSERDEMONS = 6585;
            public static final int KRYSTILLIA_REVENANTS = 6586;
            public static final int KRYSTILLIA_LAVADRAGONS = 6587;
            public static final int KRYSTILLIA_GREENDRAGONS = 6588;
            public static final int KRYSTILLIA_DARKWARRIORS = 6589;
            public static final int KRYSTILLIA_BLACKDRAGONS = 6590;
            public static final int KRYSTILLIA_BANDITS = 6591;
            public static final int KRYSTILLIA_ENTS = 6592;
            public static final int KRYSTILLIA_SKELETONS = 6593;
            public static final int KRYSTILLIA_ROGUES = 6594;
            public static final int KRYSTILLIA_MAMMOTHS = 6595;
            public static final int KRYSTILLIA_SPIDERS = 6596;
            public static final int KRYSTILLIA_SCORPIONS = 6597;
            public static final int KRYSTILLIA_BEARS = 6598;
            public static final int KRYSTILLIA_EARTHWARRIORS = 6599;
            public static final int KRYSTILLIA_ANKOU = 6600;
            public static final int KRYSTILLIA_ICEGIANTS = 6601;
            public static final int KRYSTILLIA_SPIRITUALGWD = 6602;
            public static final int KRYSTILLIA_MAGICAXES = 6603;
            public static final int KRYSTILLIA_HELLHOUNDS = 6604;
            public static final int KRYSTILLIA_BLACKDEMONS = 6605;
            public static final int KRYSTILLIA_AVIANSIES = 6606;
            public static final int KRYSTILLIA_ICEWARRIORS = 6607;
            public static final int KRYSTILLIA_FIREGIANTS = 6608;
            public static final int KRYSTILLIA_BOSS = 6609;
            public static final int KRYSTILLIA_GREATERDEMONS = 6610;
            public static final int MAZCHNA_CRABS = 6611;
            public static final int MAZCHNA_SCORPIONS = 6612;
            public static final int MAZCHNA_BATS = 6613;
            public static final int MAZCHNA_CAVEBUGS = 6614;
            public static final int MAZCHNA_ICEWARRIORS = 6615;
            public static final int MAZCHNA_ZOMBIES = 6616;
            public static final int MAZCHNA_BEARS = 6617;
            public static final int MAZCHNA_GHOSTS = 6618;
            public static final int MAZCHNA_CAVECRAWLERS = 6619;
            public static final int MAZCHNA_KALPHITE = 6620;
            public static final int MAZCHNA_WALLBEASTS = 6621;
            public static final int MAZCHNA_CATABLEPON = 6622;
            public static final int MAZCHNA_FLESHCRAWLERS = 6623;
            public static final int MAZCHNA_DOGS = 6624;
            public static final int MAZCHNA_WOLVES = 6625;
            public static final int MAZCHNA_HOBGOBLINS = 6626;
            public static final int MAZCHNA_CAVESLIMES = 6627;
            public static final int MAZCHNA_LIZARDS = 6628;
            public static final int MAZCHNA_ROCKSLUGS = 6629;
            public static final int MAZCHNA_HILLGIANTS = 6630;
            public static final int MAZCHNA_COCKATRICE = 6631;
            public static final int MAZCHNA_SHADES = 6632;
            public static final int MAZCHNA_KILLERWATTS = 6633;
            public static final int MAZCHNA_PYREFIENDS = 6634;
            public static final int MAZCHNA_MOGRES = 6635;
            public static final int MAZCHNA_SKELETONS = 6636;
            public static final int MAZCHNA_CRAWLINGHANDS = 6637;
            public static final int MAZCHNA_BANSHEES = 6638;
            public static final int MAZCHNA_GHOULS = 6639;
            public static final int MAZCHNA_VAMPYRES = 6640;
            public static final int CHAELDAR_CRABS = 6641;
            public static final int CHAELDAR_LESSER_NAGUA = 6642;
            public static final int CHAELDAR_WARPEDCREATURES = 6643;
            public static final int CHAELDAR_VAMPYRES = 6644;
            public static final int CHAELDAR_WYRMS = 6645;
            public static final int CHAELDAR_TZHAAR = 6646;
            public static final int CHAELDAR_LIZARDMEN = 6647;
            public static final int CHAELDAR_BASILISKS = 6648;
            public static final int CHAELDAR_FEVERSPIDERS = 6649;
            public static final int CHAELDAR_BRINERATS = 6650;
            public static final int CHAELDAR_MUTATEDZYGOMITES = 6651;
            public static final int CHAELDAR_SKELETALWYVERNS = 6652;
            public static final int CHAELDAR_FOSSILWYVERNS = 6653;
            public static final int CHAELDAR_BLOODVELD = 6654;
            public static final int CHAELDAR_SHADOWWARRIORS = 6655;
            public static final int CHAELDAR_ABERRANTSPECTRES = 6656;
            public static final int CHAELDAR_ELVES = 6657;
            public static final int CHAELDAR_BLUEDRAGONS = 6658;
            public static final int CHAELDAR_DUSTDEVILS = 6659;
            public static final int CHAELDAR_HELLHOUNDS = 6660;
            public static final int CHAELDAR_GREATERDEMONS = 6661;
            public static final int CHAELDAR_LESSERDEMONS = 6662;
            public static final int CHAELDAR_JELLIES = 6663;
            public static final int CHAELDAR_CAVEHORRORS = 6664;
            public static final int CHAELDAR_TUROTH = 6665;
            public static final int CHAELDAR_BLACKDEMONS = 6666;
            public static final int CHAELDAR_JUNGLEHORRORS = 6667;
            public static final int CHAELDAR_TROLLS = 6668;
            public static final int CHAELDAR_AVIANSIES = 6669;
            public static final int CHAELDAR_SPIRITUALGWD = 6670;
            public static final int CHAELDAR_DAGANNOTH = 6671;
            public static final int CHAELDAR_KALPHITE = 6672;
            public static final int CHAELDAR_GARGOYLES = 6673;
            public static final int CHAELDAR_FIREGIANTS = 6674;
            public static final int CHAELDAR_KURASK = 6675;
            public static final int CHAELDAR_CAVEKRAKEN = 6676;
            public static final int CHAELDAR_NECHRYAEL = 6677;
            public static final int CHAELDAR_ABYSSALDEMONS = 6678;
            public static final int NIEVE_ARAXYTES = 6679;
            public static final int NIEVE_WARPEDCREATURES = 6680;
            public static final int NIEVE_VAMPYRES = 6681;
            public static final int NIEVE_BASILISKS = 6682;
            public static final int NIEVE_DRAKES = 6683;
            public static final int NIEVE_WYRMS = 6684;
            public static final int NIEVE_REDDRAGONS = 6685;
            public static final int NIEVE_LIZARDMEN = 6686;
            public static final int NIEVE_MUTATEDZYGOMITES = 6687;
            public static final int NIEVE_BRINERATS = 6688;
            public static final int NIEVE_BLACKDRAGONS = 6689;
            public static final int NIEVE_SKELETALWYVERNS = 6690;
            public static final int NIEVE_FOSSILWYVERNS = 6691;
            public static final int NIEVE_CAVEHORRORS = 6692;
            public static final int NIEVE_TUROTH = 6693;
            public static final int NIEVE_KURASK = 6694;
            public static final int NIEVE_SCABARITES = 6695;
            public static final int NIEVE_SUQAHS = 6696;
            public static final int NIEVE_FIREGIANTS = 6697;
            public static final int NIEVE_TROLLS = 6698;
            public static final int NIEVE_ANKOU = 6699;
            public static final int NIEVE_ABERRANTSPECTRES = 6700;
            public static final int NIEVE_BLUEDRAGONS = 6701;
            public static final int NIEVE_DARKBEASTS = 6702;
            public static final int NIEVE_GREATERDEMONS = 6703;
            public static final int NIEVE_DUSTDEVILS = 6704;
            public static final int NIEVE_ELVES = 6705;
            public static final int NIEVE_KALPHITE = 6706;
            public static final int NIEVE_SMOKEDEVIL = 6707;
            public static final int NIEVE_CAVEKRAKEN = 6708;
            public static final int NIEVE_BLOODVELD = 6709;
            public static final int NIEVE_GARGOYLES = 6710;
            public static final int NIEVE_HELLHOUNDS = 6711;
            public static final int NIEVE_AVIANSIES = 6712;
            public static final int NIEVE_SPIRITUALGWD = 6713;
            public static final int NIEVE_DAGANNOTH = 6714;
            public static final int NIEVE_BLACKDEMONS = 6715;
            public static final int NIEVE_NECHRYAEL = 6716;
            public static final int NIEVE_ABYSSALDEMONS = 6717;
            public static final int NIEVE_BOSS = 6718;
            public static final int NIEVE_TZHAAR = 6719;
            public static final int NIEVE_METALDRAGONS = 6720;
            public static final int TURAEL_RATS = 6721;
            public static final int TURAEL_MONKEYS = 6722;
            public static final int TURAEL_COWS = 6723;
            public static final int TURAEL_BIRDS = 6724;
            public static final int TURAEL_SPIDERS = 6725;
            public static final int TURAEL_DWARVES = 6726;
            public static final int TURAEL_LIZARDS = 6727;
            public static final int TURAEL_CAVEBUGS = 6728;
            public static final int TURAEL_SCORPIONS = 6729;
            public static final int TURAEL_ZOMBIES = 6730;
            public static final int TURAEL_BATS = 6731;
            public static final int TURAEL_BEARS = 6732;
            public static final int TURAEL_GHOSTS = 6733;
            public static final int TURAEL_CAVECRAWLERS = 6734;
            public static final int TURAEL_MINOTAURS = 6735;
            public static final int TURAEL_ICEFIENDS = 6736;
            public static final int TURAEL_KALPHITE = 6737;
            public static final int TURAEL_CAVESLIMES = 6738;
            public static final int TURAEL_DOGS = 6739;
            public static final int TURAEL_GOBLINS = 6740;
            public static final int TURAEL_WOLVES = 6741;
            public static final int TURAEL_SKELETONS = 6742;
            public static final int TURAEL_CRAWLINGHANDS = 6743;
            public static final int TURAEL_BANSHEES = 6744;
            public static final int SPRIA_SOURHOGS = 6745;
            public static final int SPRIA_RATS = 6746;
            public static final int SPRIA_MONKEYS = 6747;
            public static final int SPRIA_COWS = 6748;
            public static final int SPRIA_BIRDS = 6749;
            public static final int SPRIA_SPIDERS = 6750;
            public static final int SPRIA_DWARVES = 6751;
            public static final int SPRIA_LIZARDS = 6752;
            public static final int SPRIA_CAVEBUGS = 6753;
            public static final int SPRIA_SCORPIONS = 6754;
            public static final int SPRIA_ZOMBIES = 6755;
            public static final int SPRIA_BATS = 6756;
            public static final int SPRIA_BEARS = 6757;
            public static final int SPRIA_GHOSTS = 6758;
            public static final int SPRIA_CAVECRAWLERS = 6759;
            public static final int SPRIA_MINOTAURS = 6760;
            public static final int SPRIA_ICEFIENDS = 6761;
            public static final int SPRIA_KALPHITE = 6762;
            public static final int SPRIA_CAVESLIMES = 6763;
            public static final int SPRIA_DOGS = 6764;
            public static final int SPRIA_GOBLINS = 6765;
            public static final int SPRIA_WOLVES = 6766;
            public static final int SPRIA_SKELETONS = 6767;
            public static final int SPRIA_CRAWLINGHANDS = 6768;
            public static final int SPRIA_BANSHEES = 6769;
            public static final int CHAELDAR_GRYPHONS = 9406;
            public static final int DURADEL_GRYPHONS = 9407;
            public static final int DURADEL_AQUANITES = 9408;
            public static final int DURADEL_FROST_DRAGONS = 9409;
            public static final int NIEVE_GRYPHONS = 9410;
            public static final int NIEVE_AQUANITES = 9411;
            public static final int NIEVE_FROST_DRAGONS = 9412;
            public static final int VANNAKA_GRYPHONS = 9413;
            public static final int LEAGUES_COWS = 16208;
            public static final int LEAGUES_BIRDS = 16210;
        }
    }

    public static final class SlayerArea {
        private SlayerArea() {}

        public static final int ID = 115;
        public static final int COL_AREA_ID = 0;
        public static final int COL_WORLD_REGION = 1;
        public static final int COL_AREA_TEXT = 2;
        public static final int COL_AREA_NAME_IN_HELPER = 3;
        public static final int COL_AREA_HINT = 4;
        public static final int COL_MIN_STAT_REQUIREMENT_ALL = 5;
        public static final int COL_MIN_STAT_REQUIREMENT_ANY = 6;
        public static final int COL_QUESTS_REQUIRED_ALL = 7;
        public static final int COL_RELATED_CONTENT = 8;

        public static final class Row {
            private Row() {}

            public static final int SLAYERAREA_CRYPTOFTONALI = 4099;
            public static final int SLAYERAREA_ZEAHCATACOMBS = 6341;
            public static final int SLAYERAREA_SMOKEDUNGEON = 6342;
            public static final int SLAYERAREA_SMOKEDEVILDUNGEON = 6343;
            public static final int SLAYERAREA_BRIMSTONEDUNGEON = 6344;
            public static final int SLAYERAREA_STRONGHOLDDUNGEON = 6345;
            public static final int SLAYERAREA_WATERFALLDUNGEON = 6346;
            public static final int SLAYERAREA_BRIMHAVENDUNGEON = 6347;
            public static final int SLAYERAREA_ISLEOFSOULS = 6348;
            public static final int SLAYERAREA_GIANTSDEN = 6349;
            public static final int SLAYERAREA_CHASMOFFIRE = 6350;
            public static final int SLAYERAREA_TAVERLEYDUNGEON = 6351;
            public static final int SLAYERAREA_WITCHHAVENDUNGEON = 6352;
            public static final int SLAYERAREA_SLAYERTOWER = 6353;
            public static final int SLAYERAREA_GODWARS = 6354;
            public static final int SLAYERAREA_KALPHITELAIR = 6355;
            public static final int SLAYERAREA_KALPHITECAVE = 6356;
            public static final int SLAYERAREA_KRAKENCOVE = 6357;
            public static final int SLAYERAREA_LIGHTHOUSE = 6358;
            public static final int SLAYERAREA_WATERBIRTHISLAND = 6359;
            public static final int SLAYERAREA_LIZARDMANCANYON = 6360;
            public static final int SLAYERAREA_MOLCH = 6361;
            public static final int SLAYERAREA_LIZARDMANSETTLEMENT = 6362;
            public static final int SLAYERAREA_DEATHPLATEAU = 6363;
            public static final int SLAYERAREA_TROLLSTRONGHOLD = 6364;
            public static final int SLAYERAREA_KELDAGRIM = 6365;
            public static final int SLAYERAREA_SOUTHQUIDAMORTEM = 6366;
            public static final int SLAYERAREA_JATNEIT = 6367;
            public static final int SLAYERAREA_FREMDUNGEON = 6368;
            public static final int SLAYERAREA_MYTHSGUILD = 6369;
            public static final int SLAYERAREA_MOURNERTUNNELS = 6370;
            public static final int SLAYERAREA_LITHKRENVAULT = 6371;
            public static final int SLAYERAREA_ANCIENTCAVERN = 6372;
            public static final int SLAYERAREA_SECURITYSTRONGHOLD = 6373;
            public static final int SLAYERAREA_FOSSILISLAND = 6374;
            public static final int SLAYERAREA_OGREENCLAVE = 6375;
            public static final int SLAYERAREA_BRINERATCAVERN = 6376;
            public static final int SLAYERAREA_ZANARIS = 6377;
            public static final int SLAYERAREA_EVILCHICKENLAIR = 6378;
            public static final int SLAYERAREA_ABYSS = 6379;
            public static final int SLAYERAREA_KEBOSSWAMP = 6380;
            public static final int SLAYERAREA_KEBOSBATTLEFRONT = 6381;
            public static final int SLAYERAREA_HOSIDIUSDUNGEON = 6382;
            public static final int SLAYERAREA_IORWERTHDUNGEON = 6383;
            public static final int SLAYERAREA_JORMUNGANDPRISON = 6384;
            public static final int SLAYERAREA_DARKMEYER = 6385;
            public static final int SLAYERAREA_SLEPE = 6386;
            public static final int SLAYERAREA_MEIYERDITCHLABS = 6387;
            public static final int SLAYERAREA_POISONWASTEDUNGEON = 6388;
            public static final int SLAYERAREA_PERILOUSMOONSDUNGEON = 6389;
            public static final int SLAYERAREA_TAPOYAUIK = 6390;
            public static final int SLAYERAREA_ICEDUNGEON = 6391;
            public static final int SLAYERAREA_GREATCONCH = 9402;
            public static final int SLAYERAREA_CHARRED_ISLAND_DUNGEON = 9403;
            public static final int SLAYERAREA_VAMPYRIUM = 16937;
        }
    }

    public static final class SlayerTaskSublist {
        private SlayerTaskSublist() {}

        public static final int ID = 116;
        public static final int COL_SUBTABLE_ID = 0;
        public static final int COL_TASK_SUBTABLE_ID = 1;
        public static final int COL_SLAYER_MASTER_EXCLUSIVE = 2;
        public static final int COL_EXCLUDE_SLAYER_MASTER = 3;
        public static final int COL_TASK = 4;

        public static final class Row {
            private Row() {}

            public static final int SLAYER_BOSS_TASK_KREEARRA = 503;
            public static final int SLAYER_BOSS_TASK_ZILYANA = 6154;
            public static final int SLAYER_BOSS_TASK_GRAARDOR = 6156;
            public static final int SLAYER_BOSS_TASK_KRILTSUTSAROTH = 6158;
            public static final int SLAYER_BOSS_TASK_DAGANNOTHKINGS = 6160;
            public static final int SLAYER_BOSS_TASK_MOLE = 6162;
            public static final int SLAYER_BOSS_TASK_KQ = 6164;
            public static final int SLAYER_BOSS_TASK_KBD = 6166;
            public static final int SLAYER_BOSS_TASK_CALLISTO = 6168;
            public static final int SLAYER_BOSS_TASK_VENENATIS = 6170;
            public static final int SLAYER_BOSS_TASK_VETION = 6172;
            public static final int SLAYER_BOSS_TASK_CHAOSELEMENTAL = 6174;
            public static final int SLAYER_BOSS_TASK_CHAOSFANATIC = 6176;
            public static final int SLAYER_BOSS_TASK_CRAZYARCHAEOLOGIST = 6178;
            public static final int SLAYER_BOSS_TASK_SCORPIA = 6180;
            public static final int SLAYER_BOSS_TASK_ZULRAH = 6182;
            public static final int SLAYER_BOSS_TASK_BARROWSBROTHERS = 6184;
            public static final int SLAYER_BOSS_TASK_CAVEKRAKENBOSS = 6186;
            public static final int SLAYER_BOSS_TASK_SMOKEDEVILBOSS = 6188;
            public static final int SLAYER_BOSS_TASK_CERBERUS = 6190;
            public static final int SLAYER_BOSS_TASK_ABYSSALSIRE = 6192;
            public static final int SLAYER_BOSS_TASK_GROTESQUEGUARDIANS = 6194;
            public static final int SLAYER_BOSS_TASK_VORKATH = 6196;
            public static final int SLAYER_BOSS_TASK_HYDRABOSS = 6198;
            public static final int SLAYER_BOSS_TASK_SARACHNIS = 6200;
            public static final int SLAYER_BOSS_TASK_MUSPAH = 6202;
            public static final int SLAYER_BOSS_TASK_VARDORVIS = 6204;
            public static final int SLAYER_BOSS_TASK_DUKESUCELLUS = 6206;
            public static final int SLAYER_BOSS_TASK_WHISPERER = 6208;
            public static final int SLAYER_BOSS_TASK_LEVIATHAN = 6210;
            public static final int SLAYER_BOSS_TASK_ARAXXOR = 6212;
            public static final int SLAYER_BOSS_TASK_GRYPHONBOSS = 9404;
            public static final int SLAYER_BOSS_TASK_MAGGOT_KING = 16938;
        }
    }

    public static final class SlayerUnlock {
        private SlayerUnlock() {}

        public static final int ID = 117;
        public static final int COL_BIT = 0;
        public static final int COL_COST = 1;
        public static final int COL_ICON = 2;
        public static final int COL_NAME = 3;
        public static final int COL_DESCRIPTION = 4;
        public static final int COL_REFUNDABLE = 5;
        public static final int COL_LIST_POSITION = 6;
        public static final int COL_RELATED_TASK = 7;

        public static final class Row {
            private Row() {}

            public static final int SLAYER_HELM_UNLOCKED = 6396;
            public static final int SLAYER_RING_UNLOCKED = 6397;
            public static final int SLAYER_AMMO_UNLOCKED = 6398;
            public static final int SLAYER_REWARDS_UNLOCK_BOSSES = 6412;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_BLACK = 6424;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_GREEN = 6425;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_RED = 6426;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_PURPLE = 6427;
            public static final int SLAYER_REWARDS_UNLOCK_SUPERIORMOBS = 6428;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_TURQUOISE = 6434;
            public static final int SLAYER_REWARDS_UNLOCK_FOSSILWYVERNBLOCK = 6435;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_HYDRA = 6437;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_ARAXYTE = 6438;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_TWISTED = 6441;
            public static final int SLAYER_REWARDS_UNLOCK_STORAGE = 6444;
            public static final int SLAYER_REWARDS_LONGER_REVENANTS = 6446;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_HOODED = 9641;
            public static final int SLAYER_REWARDS_UNLOCK_HELM_LEAGUE_6 = 16325;
        }
    }

    public static final class Action {
        private Action() {}

        public static final int ID = 118;
        public static final int COL_ACTION_NAME = 0;
        public static final int COL_ACTION_DESC = 1;
        public static final int COL_ACTION_DISPLAY_GRAPHIC = 2;
        public static final int COL_ACTION_DISPLAY_OBJECT = 3;
        public static final int COL_ACTION_DISPLAY_DESC = 4;
        public static final int COL_ACTION_DISPLAY_SHOW_DERIVED_DESC = 5;
        public static final int COL_REPEATABLE = 6;
        public static final int COL_ZONE = 7;
        public static final int COL_CUSTOM_TRACKING = 8;
        public static final int COL_BOSS_KILL = 9;
        public static final int COL_BOSS_KILL_CATEGORY = 10;
        public static final int COL_BOSS_KILL_EXTRAS = 11;
        public static final int COL_NPC_KILL = 12;
        public static final int COL_NPC_KILL_CATEGORY = 13;
        public static final int COL_NPC_KILL_SLAYER_CATEGORY = 14;
        public static final int COL_EQUIP_ITEM = 15;
        public static final int COL_TOTAL_LEVEL = 16;
        public static final int COL_LEVEL = 17;
        public static final int COL_COLLECTION_GENERIC = 18;
        public static final int COL_COLLECTION_SPECIFIC = 19;
        public static final int COL_LOOT_DROP = 20;
        public static final int COL_LOOT_DROP_SPECIFIC_NPC = 21;
        public static final int COL_QUEST = 22;
        public static final int COL_MINE_ORE = 23;
        public static final int COL_CATCH_FISH = 24;
        public static final int COL_HUNTER = 25;
        public static final int COL_POH_BUILD = 26;
        public static final int COL_POH_SET_PORTAL = 27;
        public static final int COL_CREATE_ITEM = 28;
        public static final int COL_CHOP_LOGS = 29;
        public static final int COL_LEAGUES_TASK = 30;
        public static final int COL_CHILD_ACTION = 31;
        public static final int COL_ACTION_DIFFICULTY = 32;
        public static final int COL_LEAGUE_TRACKING_ID = 33;
        public static final int COL_IN_CURRENT_LEAGUE = 34;
        public static final int COL_CATEGORY = 35;
        public static final int COL_TASK_AREA = 36;

        public static final class Row {
            private Row() {}

            public static final int ACTION_KALPHITE_QUEEN_UNIQUE = 6770;
            public static final int ACTION_YAMA_UNIQUE = 6771;
            public static final int ACTION_HUEY_UNIQUE = 6772;
            public static final int ACTION_LOOT_BARROWS_UNIQUE_WHILE_WEARING_BARROWS_ITEM = 6773;
            public static final int ACTION_COX_UNIQUE = 6774;
            public static final int ACTION_GET_TOA_UNIQUE = 6775;
            public static final int ACTION_GET_TOB_UNIQUE = 6776;
            public static final int ACTION_VORKATH_UNIQUE = 6777;
            public static final int ACTION_DROP_WARRIOR_RING = 6778;
            public static final int ACTION_DROP_RANGER_RING = 6779;
            public static final int ACTION_DROP_SEER_RING = 6780;
            public static final int ACTION_DROP_BERZERKER_RING = 6781;
            public static final int ACTION_OBTAIN_KINGS_RINGS_ONE_TRIP = 6782;
            public static final int ACTION_MINE_GOLD_ORE = 6783;
            public static final int ACTION_POH_BUILD_MARBLE_PORTAL_NEXUS = 6784;
            public static final int ACTION_TOTAL_LEVEL_500 = 6785;
            public static final int ACTION_TOTAL_LEVEL_1500 = 6786;
            public static final int ACTION_TOTAL_LEVEL_2000 = 6787;
            public static final int ACTION_CREATE_ZAMORAK_BREW_AT_ALTAR_WITH_ROBES = 6788;
            public static final int ACTION_CREATE_SHARK_WITH_CHEFS_HAT = 6789;
            public static final int ACTION_CREATE_YEW_SHORTBOW = 6790;
            public static final int ACTION_CHOP_MAPLE_LOGS = 6791;
            public static final int ACTION_STEAL_FROM_FRUIT_STALL = 6792;
            public static final int ACTION_COMPLETE_VAMPYRE_SLAYER = 6793;
            public static final int ACTION_CLEAN_GRIMY_HERB = 6794;
            public static final int ACTION_HUNTER_MOTH_BAREHANDED = 6795;
            public static final int ACTION_POH_SET_PORTAL_CATHERBY = 6796;
            public static final int ACTION_FULLY_UNLOCK_THE_MYCELIUM_TRANSPORTATION_SYSTEM = 6797;
            public static final int ACTION_COMPLETE_A_TASK_FOR_THE_WISE_OLD_MAN = 6798;
            public static final int ACTION_BUILD_A_QUETZAL_LANDING_SITE = 6799;
            public static final int ACTION_SUBDUE_THE_MOONS_OF_PERIL = 6800;
            public static final int ACTION_SCORE_A_GOAL_IN_GNOMEBALL = 6801;
            public static final int ACTION_KILL_OGRESS = 6802;
            public static final int ACTION_KILL_LESSER_DEMON_IN_WIZARDS_TOWER = 6803;
            public static final int ACTION_CASTLE_WARS_TICKET = 6804;
            public static final int ACTION_KILL_JALZEK = 6805;
            public static final int ACTION_MINE_FROM_GEMSTONE_CRAB = 6806;
            public static final int ACTION_WITNESS_GEMSTONE_CRAB_BURROW = 6807;
            public static final int ACTION_COMPLETE_DOM_DELVE_8 = 6808;
            public static final int ACTION_FINISH_MAGIC_ENT_TOTEM_DECORATION = 6809;
            public static final int ACTION_BINGO_TUTORIAL = 6810;
            public static final int ACTION_KILL_LIZARD_SHAMAN_IN_TEMPLE = 6811;
            public static final int ACTION_ARAXXOR_UNIQUE = 6812;
            public static final int ACTION_BLESS_DRAGON_BONE = 6813;
            public static final int ACTION_OBTAIN_ANY_INSURABLE_PET = 6814;
            public static final int ACTION_BUILD_DEMONIC_THRONE = 6815;
            public static final int ACTION_HALLOWED_SEPULCHRE_FLOOR_5 = 6816;
            public static final int ACTION_VARLAMORE_THIEVING_HOUSE_VALUABLE = 6817;
            public static final int ACTION_KILL_DRAGON = 6818;
            public static final int ACTION_KILL_GIANT = 6819;
            public static final int ACTION_BOSS_KILL_KALPHITE_QUEEN = 6820;
            public static final int ACTION_BOSS_KILL_KREEARRA = 6821;
            public static final int ACTION_BOSS_KILL_GENERAL_GRAARDOR = 6822;
            public static final int ACTION_BOSS_KILL_COMMANDER_ZILYANA = 6823;
            public static final int ACTION_BOSS_KILL_KRIL_TSUTSAROTH = 6824;
            public static final int ACTION_BOSS_KILL_SIMPLE_GWD_BOSSES_WITHOUT_BANKING = 6825;
            public static final int ACTION_BOSS_KILL_SOL_HEREDIT = 6826;
            public static final int ACTION_BOSS_KILL_YAMA_SOLO = 6827;
            public static final int ACTION_BOSS_KILL_ZULRAH_WITH_BLOWPIPE = 6828;
            public static final int ACTION_BOSS_KILL_ALL_AWAKENED = 6829;
            public static final int ACTION_BOSS_KILL_DUKE_SUCELLUS_AWAKENED = 6830;
            public static final int ACTION_BOSS_KILL_LEVIATHAN_AWAKENED = 6831;
            public static final int ACTION_BOSS_KILL_VARDORVIS_AWAKENED = 6832;
            public static final int ACTION_BOSS_KILL_WHISPERER_AWAKENED = 6833;
            public static final int ACTION_BOSS_KILL_SCURRIUS = 6834;
            public static final int ACTION_BOSS_KILL_VORKATH = 6835;
            public static final int ACTION_BOSS_KILL_BRYOPHYTA = 6836;
            public static final int ACTION_BOSS_KILL_HUEYCOATL = 6837;
            public static final int ACTION_BOSS_KILL_TEMPOROSS = 6838;
            public static final int ACTION_BOSS_KILL_JAD = 6839;
            public static final int ACTION_BOSS_KILL_GAUNTLET_HARDMODE = 6840;
            public static final int ACTION_INFERNO_IN_30 = 6841;
            public static final int ACTION_COMPLETE_RAID = 6842;
            public static final int ACTION_COMPLETE_COX = 6843;
            public static final int ACTION_COMPLETE_TOB = 6844;
            public static final int ACTION_COMPLETE_TOA = 6845;
            public static final int ACTION_EQUIP_INFERNAL_CAPE = 6846;
            public static final int ACTION_EQUIP_SOS_BOOTS = 6847;
            public static final int ACTION_EQUIP_ENCHANTED_ONYX_AMULET = 6848;
            public static final int ACTION_EQUIP_SOS_SKULL_SCEPTRE = 6849;
            public static final int ACTION_EQUIP_GODWARS_UNIQUE = 6850;
            public static final int ACTION_EQUIP_MAX_CAPE = 6851;
            public static final int ACTION_EQUIP_INFINITE_MONEYBAG = 6852;
            public static final int ACTION_POH_SET_PORTAL_ANY = 6853;
            public static final int ACTION_ALCHEMY_30K = 6854;
            public static final int ACTION_THROW_GNOMEBALL = 6855;
            public static final int ACTION_CHRONICLE_TELEPORT = 6856;
            public static final int ACTION_CHECK_A_GROWN_FRUIT_TREE = 6857;
            public static final int ACTION_ENTER_INFERNO = 6858;
            public static final int ACTION_BUILD_ALL_FOSSIL_CAMP_BUILDINGS = 6859;
            public static final int ACTION_POH_SKILLCAPE_DEPOSIT = 6860;
            public static final int ACTION_DRINK_STEAMFORGE_BREW_INSIDE_SYO_INN = 6861;
            public static final int ACTION_EAT_CABBAGE_INFRONT_OF_BRASSICAN_MAGE = 6862;
            public static final int LEAGUE_TASK_ALL_EASY_COMBAT_ACHIEVEMENTS = 13323;
            public static final int LEAGUE_TASK_ALL_MEDIUM_COMBAT_ACHIEVEMENTS = 13324;
            public static final int LEAGUE_TASK_SCURRIUS_COMBAT_ACHIEVEMENTS = 13325;
            public static final int LEAGUE_TASK_THE_INFERNO_COMBAT_ACHIEVEMENTS = 13326;
            public static final int LEAGUE_TASK_TZHAARKETRAKS_COMBAT_ACHIEVEMENTS = 13327;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_KARAMJA_DIARY = 13328;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_LUMBRIDGE_DRAYNOR_DIARY = 13329;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_VARROCK_DIARY = 13330;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_KARAMJA_DIARY = 13331;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_LUMBRIDGE_DRAYNOR_DIARY = 13332;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_VARROCK_DIARY = 13333;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_KARAMJA_DIARY = 13334;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_LUMBRIDGE_DRAYNOR_DIARY = 13335;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_VARROCK_DIARY = 13336;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_KARAMJA_DIARY = 13337;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_LUMBRIDGE_DRAYNOR_DIARY = 13338;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_VARROCK_DIARY = 13339;
            public static final int LEAGUE_TASK_GROTESQUE_GUARDIANS_COMBAT_ACHIEVEMENTS = 13340;
            public static final int LEAGUE_TASK_PHOSANIS_NIGHTMARE_COMBAT_ACHIEVEMENTS = 13341;
            public static final int LEAGUE_TASK_KOUREND_AND_KEBOS_ELITE_DIARY_TASKS = 13342;
            public static final int LEAGUE_TASK_ALCHEMICAL_HYDRA_COMBAT_ACHIEVEMENTS = 13343;
            public static final int LEAGUE_TASK_THE_NIGHTMARE_COMBAT_ACHIEVEMENTS = 13344;
            public static final int LEAGUE_TASK_PHANTOM_MUSPAH_COMBAT_ACHIEVEMENTS = 13345;
            public static final int LEAGUE_TASK_VORKATH_COMBAT_ACHIEVEMENTS = 13346;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_WILDERNESS_DIARY = 13347;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_FREMENNIK_DIARY = 13348;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_ARDOUGNE_DIARY = 13349;
            public static final int LEAGUE_TASK_DAGANNOTH_KINGS_COMBAT_ACHIEVEMENTS = 13350;
            public static final int LEAGUE_TASK_ARAXXOR_COMBAT_ACHIEVEMENTS = 13351;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_MORYTANIA_DIARY = 13352;
            public static final int LEAGUE_TASK_THE_LEVIATHAN_COMBAT_ACHIEVEMENTS = 13353;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_DESERT_DIARY = 13354;
            public static final int LEAGUE_TASK_PERILOUS_MOONS_COMBAT_ACHIEVEMENTS = 13355;
            public static final int LEAGUE_TASK_ZULRAH_COMBAT_ACHIEVEMENTS = 13356;
            public static final int LEAGUE_TASK_DUKE_SUCELLUS_COMBAT_ACHIEVEMENTS = 13357;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_KANDARIN_DIARY = 13358;
            public static final int LEAGUE_TASK_THE_WHISPERER_COMBAT_ACHIEVEMENTS = 13359;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_WESTERN_PROVINCES_DIARY = 13360;
            public static final int LEAGUE_TASK_COMPLETE_THE_ELITE_FALADOR_DIARY = 13361;
            public static final int LEAGUE_TASK_1_COMBAT_ACHIEVEMENT = 13362;
            public static final int LEAGUE_TASK_1500_COMBAT_ACHIEVEMENT_POINTS = 13363;
            public static final int LEAGUE_TASK_100_COLLECTION_LOG_SLOTS = 13364;
            public static final int LEAGUE_TASK_350_COMBAT_ACHIEVEMENTS = 13365;
            public static final int LEAGUE_TASK_VARDORVIS_COMBAT_ACHIEVEMENTS = 13366;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_FREMENNIK_DIARY = 13367;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_FALADOR_DIARY = 13368;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_MORYTANIA_DIARY = 13369;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_WILDERNESS_DIARY = 13370;
            public static final int LEAGUE_TASK_400_COMBAT_ACHIEVEMENTS = 13371;
            public static final int LEAGUE_TASK_100_COMBAT_ACHIEVEMENTS = 13372;
            public static final int LEAGUE_TASK_800_COMBAT_ACHIEVEMENT_POINTS = 13373;
            public static final int LEAGUE_TASK_KOUREND_AND_KEBOS_HARD_DIARY_TASKS = 13374;
            public static final int LEAGUE_TASK_15_COLLECTION_LOG_SLOTS = 13375;
            public static final int LEAGUE_TASK_150_COMBAT_ACHIEVEMENTS = 13376;
            public static final int LEAGUE_TASK_200_COLLECTION_LOG_SLOTS = 13377;
            public static final int LEAGUE_TASK_200_COMBAT_ACHIEVEMENTS = 13378;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_DESERT_DIARY = 13379;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_ARDOUGNE_DIARY = 13380;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_KANDARIN_DIARY = 13381;
            public static final int LEAGUE_TASK_COMPLETE_THE_HARD_WESTERN_PROVINCES_DIARY = 13382;
            public static final int LEAGUE_TASK_ROYAL_TITAN_COMBAT_ACHIEVEMENTS = 13383;
            public static final int LEAGUE_TASK_HUEYCOATL_COMBAT_ACHIEVEMENTS = 13384;
            public static final int LEAGUE_TASK_TELEPORT_TO_THE_FORTIS_COLOSSEUM = 13385;
            public static final int LEAGUE_TASK_AMOXLIATL_COMBAT_ACHIEVEMENTS = 13386;
            public static final int LEAGUE_TASK_GIANT_MOLE_COMBAT_ACHIEVEMENTS = 13387;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_WILDERNESS_DIARY = 13388;
            public static final int LEAGUE_TASK_THEATRE_OF_BLOOD_COMBAT_ACHIEVEMENTS = 13389;
            public static final int LEAGUE_TASK_GODWARS_DUNGEON_COMBAT_ACHIEVEMENTS = 13390;
            public static final int LEAGUE_TASK_KQ_COMBAT_ACHIEVEMENTS = 13391;
            public static final int LEAGUE_TASK_TOMBS_OF_AMASCUT_COMBAT_ACHIEVEMENTS = 13392;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_DESERT_DIARY = 13393;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_DESERT_DIARY = 13394;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_MORYTANIA_DIARY = 13395;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_MORYTANIA_DIARY = 13396;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_WILDERNESS_DIARY = 13397;
            public static final int LEAGUE_TASK_THERMONUCLEAR_SMOKE_DEVIL_COMBAT_ACHIEVEMENTS = 13398;
            public static final int LEAGUE_TASK_100_COMBAT_ACHIEVEMENT_POINTS = 13399;
            public static final int LEAGUE_TASK_1000_COMBAT_ACHIEVEMENT_POINTS = 13400;
            public static final int LEAGUE_TASK_1100_COMBAT_ACHIEVEMENT_POINTS = 13401;
            public static final int LEAGUE_TASK_1250_COMBAT_ACHIEVEMENT_POINTS = 13402;
            public static final int LEAGUE_TASK_200_COMBAT_ACHIEVEMENT_POINTS = 13403;
            public static final int LEAGUE_TASK_25_COMBAT_ACHIEVEMENTS = 13404;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_FALADOR_DIARY = 13405;
            public static final int LEAGUE_TASK_300_COMBAT_ACHIEVEMENT_POINTS = 13406;
            public static final int LEAGUE_TASK_400_COMBAT_ACHIEVEMENT_POINTS = 13407;
            public static final int LEAGUE_TASK_500_COMBAT_ACHIEVEMENT_POINTS = 13408;
            public static final int LEAGUE_TASK_600_COMBAT_ACHIEVEMENT_POINTS = 13409;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_FALADOR_DIARY = 13410;
            public static final int LEAGUE_TASK_700_COMBAT_ACHIEVEMENT_POINTS = 13411;
            public static final int LEAGUE_TASK_900_COMBAT_ACHIEVEMENT_POINTS = 13412;
            public static final int LEAGUE_TASK_ALL_MASTER_COMBAT_ACHIEVEMENTS = 13413;
            public static final int LEAGUE_TASK_250_COMBAT_ACHIEVEMENTS = 13414;
            public static final int LEAGUE_TASK_30_COLLECTION_LOG_SLOTS = 13415;
            public static final int LEAGUE_TASK_COLOSSEUM_COMBAT_ACHIEVEMENTS = 13416;
            public static final int LEAGUE_TASK_DOOM_OF_MOKHAIOTL_COMBAT_ACHIEVEMENTS = 13417;
            public static final int LEAGUE_TASK_CHAMBERS_OF_XERIC_COMBAT_ACHIEVEMENTS = 13418;
            public static final int LEAGUE_TASK_KOUREND_AND_KEBOS_EASY_DIARY_TASKS = 13419;
            public static final int LEAGUE_TASK_KOUREND_AND_KEBOS_MEDIUM_DIARY_TASKS = 13420;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_FREMENNIK_DIARY = 13421;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_ARDOUGNE_DIARY = 13422;
            public static final int LEAGUE_TASK_10_COMBAT_ACHIEVEMENTS = 13423;
            public static final int LEAGUE_TASK_300_COMBAT_ACHIEVEMENTS = 13424;
            public static final int LEAGUE_TASK_350_COLLECTION_LOG_SLOTS = 13425;
            public static final int LEAGUE_TASK_5_COLLECTION_LOG_SLOTS = 13426;
            public static final int LEAGUE_TASK_COMBAT_ACHIEVEMENTS_MASTER_TIER = 13427;
            public static final int LEAGUE_TASK_50_COLLECTION_LOG_SLOTS = 13428;
            public static final int LEAGUE_TASK_25_COMBAT_ACHIEVEMENT_POINTS = 13429;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_KANDARIN_DIARY = 13430;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_ARDOUGNE_DIARY = 13431;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_KANDARIN_DIARY = 13432;
            public static final int LEAGUE_TASK_50_COMBAT_ACHIEVEMENTS = 13433;
            public static final int LEAGUE_TASK_500_COLLECTION_LOG_SLOTS = 13434;
            public static final int LEAGUE_TASK_ZALCANO_COMBAT_ACHIEVEMENTS = 13435;
            public static final int LEAGUE_TASK_50_COMBAT_ACHIEVEMENT_POINTS = 13436;
            public static final int LEAGUE_TASK_ALL_ELITE_COMBAT_ACHIEVEMENTS = 13437;
            public static final int LEAGUE_TASK_ALL_GRANDMASTER_COMBAT_ACHIEVEMENTS = 13438;
            public static final int LEAGUE_TASK_ALL_HARD_COMBAT_ACHIEVEMENTS = 13439;
            public static final int LEAGUE_TASK_COMPLETE_THE_EASY_WESTERN_PROVINCES_DIARY = 13440;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_WESTERN_PROVINCES_DIARY = 13441;
            public static final int LEAGUE_TASK_COMPLETE_THE_MEDIUM_FREMENNIK_DIARY = 13442;
            public static final int LEAGUE_TASK_GAUNTLET_COMBAT_ACHIEVEMENTS = 13443;
            public static final int LEAGUE_TASK_1000_COLLECTION_LOG_SLOTS = 13444;
            public static final int LEAGUE_TASK_KRAKEN_COMBAT_ACHIEVEMENTS = 13445;
            public static final int LEAGUE_TASK_750_COLLECTION_LOG_SLOTS = 13446;
            public static final int LEAGUE_TASK_ABYSSAL_SIRE_COMBAT_ACHIEVEMENTS = 13447;
            public static final int LEAGUE_TASK_COMBAT_ACHIEVEMENTS_EASY_TIER = 13448;
            public static final int LEAGUE_TASK_COMBAT_ACHIEVEMENTS_ELITE_TIER = 13449;
            public static final int LEAGUE_TASK_COMBAT_ACHIEVEMENTS_HARD_TIER = 13450;
            public static final int LEAGUE_TASK_COMBAT_ACHIEVEMENTS_MEDIUM_TIER = 13451;
            public static final int LEAGUE_TASK_COMPLETE_1_SPEED_TASK = 13452;
            public static final int LEAGUE_TASK_COMPLETE_10_SPEED_TASKS = 13453;
            public static final int LEAGUE_TASK_COMPLETE_20_SPEED_TASKS = 13454;
            public static final int LEAGUE_TASK_COMPLETE_30_SPEED_TASKS = 13455;
            public static final int LEAGUE_TASK_COMPLETE_5_SPEED_TASKS = 13456;
            public static final int LEAGUE_TASK_COMPLETE_1_BOSSES_COMBAT_ACHIEVEMENTS = 13457;
            public static final int LEAGUE_TASK_COMPLETE_10_BOSSES_COMBAT_ACHIEVEMENTS = 13458;
            public static final int LEAGUE_TASK_COMPLETE_3_BOSSES_COMBAT_ACHIEVEMENTS = 13459;
            public static final int LEAGUE_TASK_COMPLETE_5_BOSSES_COMBAT_ACHIEVEMENTS = 13460;
            public static final int LEAGUE_TASK_REACH_COMBAT_LEVEL_10 = 13461;
            public static final int LEAGUE_TASK_RECEIVE_A_SLAYER_TASK = 13462;
            public static final int LEAGUE_TASK_RESTORE_5_PRAYER_POINTS_AT_AN_ALTAR = 13463;
            public static final int LEAGUE_TASK_SUPERHUMAN_STRENGTH_AND_IMPROVED_REFLEXES = 13464;
            public static final int LEAGUE_TASK_VISIT_DEATHS_DOMAIN = 13465;
            public static final int LEAGUE_TASK_OPEN_1_GRUBBY_CHEST = 13466;
            public static final int LEAGUE_TASK_SET_A_MUMMY_ABLAZE = 13467;
            public static final int LEAGUE_TASK_BURY_SOME_BONES = 13468;
            public static final int LEAGUE_TASK_CHECK_YOUR_SLAYER_TASK = 13469;
            public static final int LEAGUE_TASK_PRAY_AT_AN_ALTAR_IN_MOLCH_WHILE_POISONED = 13470;
            public static final int LEAGUE_TASK_GET_YOUR_REVENGE_AGAINST_A_DARK_WIZARD = 13471;
            public static final int LEAGUE_TASK_KILL_10_LOCUSTS = 13472;
            public static final int LEAGUE_TASK_RECEIVE_A_KONAR_SLAYER_TASK = 13473;
            public static final int LEAGUE_TASK_KILL_A_BARBARIAN_IN_THE_BARBARIAN_VILLAGE = 13474;
            public static final int LEAGUE_TASK_KILL_A_CHICKEN_WITH_YOUR_FISTS = 13475;
            public static final int LEAGUE_TASK_KILL_A_COW_IN_ONE_HIT = 13476;
            public static final int LEAGUE_TASK_BRUTALLY_MURDER_CONRAD_KING = 13477;
            public static final int LEAGUE_TASK_KILL_A_DUCK_WITH_A_FIRE_SPELL = 13478;
            public static final int LEAGUE_TASK_KILL_A_GOBLIN_HOLDING_A_SPEAR = 13479;
            public static final int LEAGUE_TASK_KILL_A_JUBSTER = 13480;
            public static final int LEAGUE_TASK_RECOLOUR_A_PIECE_OF_THE_ANCESTRAL_ROBES = 13481;
            public static final int LEAGUE_TASK_KILL_A_MUGGER = 13482;
            public static final int LEAGUE_TASK_RECEIVE_A_SLAYER_TASK_FROM_TURAEL_OR_SPRIA = 13483;
            public static final int LEAGUE_TASK_KILL_A_RAM = 13484;
            public static final int LEAGUE_TASK_KILL_A_SEAGULL_IN_THE_DESERT = 13485;
            public static final int LEAGUE_TASK_KILL_A_SPIDER_BY_KICKING_IT = 13486;
            public static final int LEAGUE_TASK_OBTAIN_FOSSILISED_DUNG = 13487;
            public static final int LEAGUE_TASK_OPEN_THE_DARK_CHEST = 13488;
            public static final int LEAGUE_TASK_RECEIVE_A_SLAYER_TASK_FROM_DURADEL = 13489;
            public static final int LEAGUE_TASK_RECEIVE_A_SLAYER_TASK_FROM_MAZCHNA = 13490;
            public static final int LEAGUE_TASK_RECEIVE_A_SLAYER_TASK_FROM_NIEVESTEVE = 13491;
            public static final int LEAGUE_TASK_RECEIVE_A_SLAYER_TASK_FROM_VANNAKA = 13492;
            public static final int LEAGUE_TASK_STUN_A_MONSTER = 13493;
            public static final int LEAGUE_TASK_TRY_AND_FEED_A_STRAY_DOG_SOMETHING_EXOTIC = 13494;
            public static final int LEAGUE_TASK_ATTACH_A_HOLY_ORNAMENT_KIT_TO_THE_SCYTHE_OF_VITUR = 13495;
            public static final int LEAGUE_TASK_ATTACH_A_SANGUINE_ORNAMENT_KIT_TO_THE_SCYTHE = 13496;
            public static final int LEAGUE_TASK_CAST_BLOOD_BARRAGE = 13497;
            public static final int LEAGUE_TASK_CAST_SHADOW_BARRAGE = 13498;
            public static final int LEAGUE_TASK_CAST_SMOKE_BARRAGE = 13499;
            public static final int LEAGUE_TASK_COMPLETE_THE_THEATRE_OF_BLOOD_100_TIMES = 13500;
            public static final int LEAGUE_TASK_COMPLETE_THE_THEATRE_OF_BLOOD_25_TIMES = 13501;
            public static final int LEAGUE_TASK_COMPLETE_THE_THEATRE_OF_BLOOD_50_TIMES = 13502;
            public static final int LEAGUE_TASK_COMPLETE_THE_THEATRE_OF_BLOOD_WITH_NO_DEATHS = 13503;
            public static final int LEAGUE_TASK_HIT_150_WITH_THE_KERIS_PARTISAN = 13504;
            public static final int LEAGUE_TASK_COMPLETE_TOMBS_OF_AMASCUT_100_TIMES = 13505;
            public static final int LEAGUE_TASK_CAST_ICE_BARRAGE = 13506;
            public static final int LEAGUE_TASK_COMPLETE_TOMBS_OF_AMASCUT_50_TIMES = 13507;
            public static final int LEAGUE_TASK_LOAD_A_BLOWPIPE_WITH_DRAGON_DARTS = 13508;
            public static final int LEAGUE_TASK_EAT_EEL_SUSHI = 13509;
            public static final int LEAGUE_TASK_USE_THE_PIETY_PRAYER = 13510;
            public static final int LEAGUE_TASK_OBTAIN_A_CRYSTAL_TOOL_SEED = 13511;
            public static final int LEAGUE_TASK_CRAFT_A_TOXIC_BLOWPIPE = 13512;
            public static final int LEAGUE_TASK_CRAFT_A_TOXIC_TRIDENT = 13513;
            public static final int LEAGUE_TASK_DISMANTLE_A_ZULRAH_SCALE_UNIQUE = 13514;
            public static final int LEAGUE_TASK_OBTAIN_A_FROZEN_CACHE_FROM_A_CACHE = 13515;
            public static final int LEAGUE_TASK_OBTAIN_20000_GLORY = 13516;
            public static final int LEAGUE_TASK_OBTAIN_40000_GLORY = 13517;
            public static final int LEAGUE_TASK_ATTACH_A_TOB_ORNAMENT_KIT = 13518;
            public static final int ACTION_COMPLETE_1_DEEP_DELVE = 13519;
            public static final int ACTION_COMPLETE_25_DEEP_DELVES = 13520;
            public static final int ACTION_COMPLETE_75_DEEP_DELVES = 13521;
            public static final int LEAGUE_TASK_COMPLETE_WAVE_12_OF_FORTIS_COLOSSEUM = 13522;
            public static final int LEAGUE_TASK_IMBUE_A_GOD_CAPE = 13523;
            public static final int LEAGUE_TASK_OPEN_10_MUDDY_CHESTS = 13524;
            public static final int LEAGUE_TASK_OPEN_15_ZOMBIE_PIRATE_LOCKERS = 13525;
            public static final int LEAGUE_TASK_OPEN_50_LARRANS_CHESTS = 13526;
            public static final int LEAGUE_TASK_OPEN_50_ZOMBIE_PIRATE_LOCKERS = 13527;
            public static final int LEAGUE_TASK_USE_A_TELEPORT_ANCHORING_SCROLL = 13528;
            public static final int LEAGUE_TASK_CAST_ICE_BLITZ = 13529;
            public static final int LEAGUE_TASK_ACTIVATE_A_PRAYER_NEAR_AN_ALTAR = 13530;
            public static final int LEAGUE_TASK_CAST_ICE_BURST = 13531;
            public static final int LEAGUE_TASK_ACTIVATE_AN_IMBUED_HEART = 13532;
            public static final int LEAGUE_TASK_CREATE_AN_AMULET_OF_BLOOD_FURY = 13533;
            public static final int LEAGUE_TASK_10_SKOTIZO_KILLS = 13534;
            public static final int LEAGUE_TASK_COMPLETE_TOMBS_OF_AMASCUT = 13535;
            public static final int LEAGUE_TASK_COMPLETE_TOMBS_OF_AMASCUT_25_TIMES = 13536;
            public static final int LEAGUE_TASK_USE_THE_CHIVALRY_PRAYER = 13537;
            public static final int LEAGUE_TASK_ACTIVATE_DEADEYE = 13538;
            public static final int LEAGUE_TASK_ACTIVATE_MYSTIC_VIGOUR = 13539;
            public static final int LEAGUE_TASK_ACTIVATE_AN_ARCANE_OR_DEXTEROUS_PRAYER_SCROLL = 13540;
            public static final int LEAGUE_TASK_CAST_A_SURGE_SPELL = 13541;
            public static final int LEAGUE_TASK_150_AMOXLIATL_KILLS = 13542;
            public static final int LEAGUE_TASK_150_HUEYCOATL_KILLS = 13543;
            public static final int LEAGUE_TASK_ASSEMBLE_A_SLAYER_HELM = 13544;
            public static final int LEAGUE_TASK_USE_THE_BANK_CHEST_INSIDE_FORTIS_COLOSSEUM = 13545;
            public static final int LEAGUE_TASK_USE_THE_FORTIS_SALUTE_EMOTE = 13546;
            public static final int LEAGUE_TASK_OBTAIN_EVERY_REVENANT_WEAPON = 13547;
            public static final int LEAGUE_TASK_COMPLETE_WAVE_6_OF_FORTIS_COLOSSEUM = 13548;
            public static final int LEAGUE_TASK_25_SKOTIZO_KILLS = 13549;
            public static final int LEAGUE_TASK_CAST_ICE_RUSH = 13550;
            public static final int LEAGUE_TASK_CREATE_THE_DIVINE_RUNE_POUCH = 13551;
            public static final int LEAGUE_TASK_LOOT_A_BARROWS_CHEST = 13552;
            public static final int LEAGUE_TASK_LOAD_A_BLOWPIPE_WITH_RUNE_DARTS = 13553;
            public static final int LEAGUE_TASK_OBTAIN_AN_ECUMENICAL_KEY = 13554;
            public static final int LEAGUE_TASK_OPEN_1_MUDDY_CHEST = 13555;
            public static final int LEAGUE_TASK_OPEN_1_ZOMBIE_PIRATE_LOCKER = 13556;
            public static final int LEAGUE_TASK_OPEN_15_LARRANS_CHESTS = 13557;
            public static final int LEAGUE_TASK_SET_UP_A_DWARF_CANNON = 13558;
            public static final int LEAGUE_TASK_ADD_A_JAR_TO_A_DISPLAY_CASE = 13559;
            public static final int LEAGUE_TASK_OPEN_ONE_OF_LARRANS_CHESTS = 13560;
            public static final int LEAGUE_TASK_COMPLETE_100_SLAYER_TASKS = 13561;
            public static final int LEAGUE_TASK_COMPLETE_50_SLAYER_TASKS = 13562;
            public static final int LEAGUE_TASK_CAST_CLAWS_OF_GUTHIX = 13563;
            public static final int LEAGUE_TASK_DEAL_66_DAMAGE_WITH_ELEMENTAL_SPELL = 13564;
            public static final int LEAGUE_TASK_TURN_IN_100_MOLE_CLAWS_TO_WYSON_THE_GARDENER = 13565;
            public static final int LEAGUE_TASK_CAST_FLAMES_OF_ZAMORAK = 13566;
            public static final int LEAGUE_TASK_CAST_SARADOMIN_STRIKE = 13567;
            public static final int LEAGUE_TASK_UNLOCK_A_GATE_IN_TAVERLEY_DUNGEON = 13568;
            public static final int LEAGUE_TASK_CHARGE_AN_AMULET_OF_GLORY_IN_THE_HEROES_GUILD = 13569;
            public static final int LEAGUE_TASK_CONSUME_A_SARADOMINS_LIGHT = 13570;
            public static final int LEAGUE_TASK_CAST_SURGE_AT_BLACK_DRAGON_IN_KANDARIN = 13571;
            public static final int LEAGUE_TASK_ENHANCE_A_TRIDENT_OF_THE_SEAS = 13572;
            public static final int LEAGUE_TASK_OBTAIN_58000_GLORY = 13573;
            public static final int LEAGUE_TASK_CREATE_THE_SATURATED_HEART = 13574;
            public static final int LEAGUE_TASK_KILL_5_SAND_CRABS = 13575;
            public static final int LEAGUE_TASK_KILL_A_CREATURE_IN_MOUNT_KARUULM = 13576;
            public static final int LEAGUE_TASK_COMPLETE_WAVE_1_OF_FORTIS_COLOSSEUM = 13577;
            public static final int LEAGUE_TASK_OBTAIN_THE_TEMPLE_KEY = 13578;
            public static final int LEAGUE_TASK_OPEN_10_GRUBBY_CHESTS = 13579;
            public static final int LEAGUE_TASK_OPEN_25_GRUBBY_CHESTS = 13580;
            public static final int LEAGUE_TASK_KILL_6_COWS_IN_10_SECONDS = 13581;
            public static final int LEAGUE_TASK_BURY_6_BONES = 13582;
            public static final int LEAGUE_TASK_BURY_SOME_WYVERN_OR_DRAGON_BONES = 13583;
            public static final int LEAGUE_TASK_CAST_A_WAVE_SPELL = 13584;
            public static final int LEAGUE_TASK_CAST_AN_EARTH_BLAST_SPELL = 13585;
            public static final int LEAGUE_TASK_CAST_A_BLAST_SPELL = 13586;
            public static final int LEAGUE_TASK_ENTER_THE_RANGING_GUILD = 13587;
            public static final int LEAGUE_TASK_LOOT_A_LYRE = 13588;
            public static final int LEAGUE_TASK_ENTER_THE_WIZARDS_GUILD = 13589;
            public static final int LEAGUE_TASK_OPEN_A_FROZEN_CACHE = 13590;
            public static final int LEAGUE_TASK_USE_THE_SPECIAL_ATTACK_OF_A_DRAGON_AXE = 13591;
            public static final int LEAGUE_TASK_CORRUPTED_GAUNTLET_430 = 13592;
            public static final int LEAGUE_TASK_FULLY_CHARGE_BRACELET_OF_ETHEREUM = 13593;
            public static final int LEAGUE_TASK_TELEPORT_WITH_GIANTSOUL_AMULET = 13594;
            public static final int LEAGUE_TASK_BLOW_THE_SOULFLAME_HORN = 13595;
            public static final int LEAGUE_TASK_UNLOCK_VILE_TRANSFERANCE = 13596;
            public static final int LEAGUE_TASK_COMPLETE_1_SLAYER_TASK = 13597;
            public static final int LEAGUE_TASK_COMPLETE_200_SLAYER_TASKS = 13598;
            public static final int LEAGUE_TASK_EAT_A_PIECE_OF_FOOD_THAT_RESTORES_AT_LEAST_6_HITPOINTS = 13599;
            public static final int LEAGUE_TASK_GET_A_GEM_FROM_A_GORAK = 13600;
            public static final int LEAGUE_TASK_OBTAIN_A_BOSS_PET = 13601;
            public static final int LEAGUE_TASK_OFFER_AN_UNSIRED_TO_THE_FONT_OF_CONSUMPTION = 13602;
            public static final int LEAGUE_TASK_PERFORM_A_SPECIAL_ATTACK = 13603;
            public static final int LEAGUE_TASK_REACH_A_PRAYER_BONUS_OF_15 = 13604;
            public static final int LEAGUE_TASK_REACH_A_PRAYER_BONUS_OF_30 = 13605;
            public static final int LEAGUE_TASK_REACH_COMBAT_LEVEL_100 = 13606;
            public static final int LEAGUE_TASK_REACH_COMBAT_LEVEL_110 = 13607;
            public static final int LEAGUE_TASK_REACH_COMBAT_LEVEL_120 = 13608;
            public static final int LEAGUE_TASK_REACH_COMBAT_LEVEL_126 = 13609;
            public static final int LEAGUE_TASK_REACH_COMBAT_LEVEL_25 = 13610;
            public static final int LEAGUE_TASK_REACH_COMBAT_LEVEL_50 = 13611;
            public static final int LEAGUE_TASK_REACH_COMBAT_LEVEL_75 = 13612;
            public static final int LEAGUE_TASK_RESTORE_75_PRAYER_POINTS_AT_AN_ALTAR = 13613;
            public static final int LEAGUE_TASK_SACRIFICE_SOMETHING_TO_DEATHS_COFFER = 13614;
            public static final int LEAGUE_TASK_SCRAPE_SOME_BLUE_DRAGONHIDE = 13615;
            public static final int LEAGUE_TASK_SHOOT_6_IRON_ARROWS = 13616;
            public static final int LEAGUE_TASK_SLAY_250_CREATURES = 13617;
            public static final int LEAGUE_TASK_SLAY_AN_ABYSSAL_DEMON = 13618;
            public static final int LEAGUE_TASK_USE_THE_PROTECT_FROM_MELEE_PRAYER = 13619;
            public static final int LEAGUE_TASK_CREATE_SILKLINED_HERB_SACK = 13620;
            public static final int LEAGUE_TASK_100_SUPERIOR_SLAYER_ENCOUNTERS = 13621;
            public static final int LEAGUE_TASK_25_SUPERIOR_SLAYER_ENCOUNTERS = 13622;
            public static final int LEAGUE_TASK_75_SUPERIOR_SLAYER_ENCOUNTERS = 13623;
            public static final int LEAGUE_TASK_KILL_A_PORT_SARIM_PRISONER = 13624;
            public static final int LEAGUE_TASK_KILL_A_BUFFALO = 13625;
            public static final int LEAGUE_TASK_KILL_A_BARBARIAN_IN_KOUREND = 13626;
            public static final int LEAGUE_TASK_KILL_A_GUARD_IN_VARLAMORE_UNDERGROUND = 13627;
            public static final int LEAGUE_TASK_KILL_A_ROOSTER = 13628;
            public static final int ACTION_KILL_A_SEAGULL = 13629;
            public static final int LEAGUE_TASK_KILL_A_BILLY_GOAT = 13630;
            public static final int ACTION_KILL_A_THIEF = 13631;
            public static final int LEAGUE_TASK_KILL_A_YAK = 13632;
            public static final int LEAGUE_TASK_KILL_AN_ICEFIEND_IN_VARLAMORE = 13633;
            public static final int ACTION_KILL_AN_IMP_IN_A_BASEMENT = 13634;
            public static final int LEAGUE_TASK_KILL_SALARIN_THE_TWISTED = 13635;
            public static final int LEAGUE_TASK_300_ALCHEMICAL_HYDRA_KILLS = 13636;
            public static final int LEAGUE_TASK_KILL_A_SNAKE_IN_KARAMJA = 13637;
            public static final int LEAGUE_TASK_300_ARAXXOR_KILLS = 13638;
            public static final int LEAGUE_TASK_KILL_A_ROCK_LOBSTER = 13639;
            public static final int LEAGUE_TASK_1_ALCHEMICAL_HYDRA_KILL = 13640;
            public static final int LEAGUE_TASK_1_ARAXXOR_KILL = 13641;
            public static final int LEAGUE_TASK_150_ARAXXOR_KILLS = 13642;
            public static final int LEAGUE_TASK_1_MIMIC_KILL = 13643;
            public static final int LEAGUE_TASK_50_ARAXXOR_KILLS = 13644;
            public static final int LEAGUE_TASK_1_SKOTIZO_KILL = 13645;
            public static final int LEAGUE_TASK_KILL_A_FROGEEL = 13646;
            public static final int LEAGUE_TASK_150_ALCHEMICAL_HYDRA_KILLS = 13647;
            public static final int LEAGUE_TASK_150_SARACHNIS_KILLS = 13648;
            public static final int LEAGUE_TASK_300_LIZARDMEN_SHAMAN_KILLS = 13649;
            public static final int LEAGUE_TASK_300_SARACHNIS_KILLS = 13650;
            public static final int LEAGUE_TASK_5_MIMIC_KILLS = 13651;
            public static final int LEAGUE_TASK_50_ALCHEMICAL_HYDRA_KILLS = 13652;
            public static final int LEAGUE_TASK_KILL_50_ELVES_IN_TIRANNWN = 13653;
            public static final int ACTION_KILL_THE_DOOM_OF_MOKHIATL = 13654;
            public static final int LEAGUE_TASK_SUBDUE_THE_MOONS_OF_PERIL_50_TIMES = 13655;
            public static final int LEAGUE_TASK_50_AMOXLIATL_KILLS = 13656;
            public static final int LEAGUE_TASK_50_HUEYCOATL_KILLS = 13657;
            public static final int LEAGUE_TASK_KILL_30_BANDITS = 13658;
            public static final int LEAGUE_TASK_KILL_A_KALPHITE_WITH_THE_KERIS_PARTISAN = 13659;
            public static final int LEAGUE_TASK_KILL_A_JUBSTER_WITH_BERET = 13660;
            public static final int LEAGUE_TASK_KILL_5_SPINNERS = 13661;
            public static final int LEAGUE_TASK_1_HUEYCOATL_KILL = 13662;
            public static final int LEAGUE_TASK_1_AMOXLIATL_KILL = 13663;
            public static final int LEAGUE_TASK_KILL_10_FROST_CRABS = 13664;
            public static final int LEAGUE_TASK_KILL_A_DIRE_WOLF = 13665;
            public static final int LEAGUE_TASK_KILL_A_JAGUAR_WITHOUT_TAKING_ANY_DAMAGE = 13666;
            public static final int LEAGUE_TASK_KILL_A_ORYX_WITH_MELEE = 13667;
            public static final int LEAGUE_TASK_SUBDUE_THE_MOONS_OF_PERIL = 13668;
            public static final int LEAGUE_TASK_1_SARACHNIS_KILL = 13669;
            public static final int LEAGUE_TASK_SUBDUE_THE_MOONS_OF_PERIL_10_TIMES = 13670;
            public static final int LEAGUE_TASK_SUBDUE_THE_MOONS_OF_PERIL_25_TIMES = 13671;
            public static final int LEAGUE_TASK_150_LIZARDMEN_SHAMAN_KILLS = 13672;
            public static final int LEAGUE_TASK_KILL_A_KING_SAND_CRAB = 13673;
            public static final int LEAGUE_TASK_KILL_A_MOSS_FIRE_HILL_GIANT_IN_KOUREND = 13674;
            public static final int LEAGUE_TASK_50_LIZARDMEN_SHAMAN_KILLS = 13675;
            public static final int LEAGUE_TASK_50_SARACHNIS_KILLS = 13676;
            public static final int LEAGUE_TASK_KILL_8_PENGUINS_WITHIN_5_SECONDS = 13677;
            public static final int LEAGUE_TASK_KILL_A_BRINE_RAT = 13678;
            public static final int LEAGUE_TASK_KILL_A_SUQAH = 13679;
            public static final int LEAGUE_TASK_KILL_A_WALLASALKI = 13680;
            public static final int LEAGUE_TASK_KILL_A_NEWTROOST = 13681;
            public static final int LEAGUE_TASK_KILL_A_SWORDCHICK = 13682;
            public static final int LEAGUE_TASK_KILL_A_BLACK_DRAGON_IN_TIRANNWN = 13683;
            public static final int LEAGUE_TASK_KILL_A_SPIDINE = 13684;
            public static final int LEAGUE_TASK_DEFEAT_A_MOSS_GIANT_IN_TIRANNWN = 13685;
            public static final int LEAGUE_TASK_DEFEAT_A_WEREWOLF_IN_MORYTANIA = 13686;
            public static final int LEAGUE_TASK_DEFEAT_A_TROLL_IN_ASGARNIA = 13687;
            public static final int LEAGUE_TASK_DEFEAT_A_WATERFIEND_IN_TIRANNWN = 13688;
            public static final int LEAGUE_TASK_DEFEAT_A_GOBLIN = 13689;
            public static final int LEAGUE_TASK_DEFEAT_A_GUARD = 13690;
            public static final int LEAGUE_TASK_DEFEAT_A_MOSS_GIANT = 13691;
            public static final int LEAGUE_TASK_DEFEAT_AN_AL_KHARID_WARRIOR = 13692;
            public static final int LEAGUE_TASK_DEFEAT_A_COCKATRICE_IN_THE_FREMENNIK_PROVINCE = 13693;
            public static final int LEAGUE_TASK_DEFEAT_A_FIRE_GIANT_IN_KANDARIN = 13694;
            public static final int LEAGUE_TASK_DEFEAT_A_CHAOS_DWARF_IN_THE_WILDERNESS = 13695;
            public static final int LEAGUE_TASK_DEFEAT_A_PYREFIEND_IN_THE_FREMENNIK_PROVINCE = 13696;
            public static final int LEAGUE_TASK_DEFEAT_A_GHAST = 13697;
            public static final int LEAGUE_TASK_DEFEAT_A_FIRE_GIANT_IN_THE_WILDERNESS = 13698;
            public static final int LEAGUE_TASK_DEFEAT_A_ROCK_CRAB_IN_THE_FREMENNIK_PROVINCE = 13699;
            public static final int LEAGUE_TASK_DEFEAT_SNAIL = 13700;
            public static final int LEAGUE_TASK_DEFEAT_A_MAMMOTH = 13701;
            public static final int LEAGUE_TASK_DEFEAT_A_ZOMBIE_PIRATE = 13702;
            public static final int LEAGUE_TASK_DEFEAT_CALLISTO_300_TIMES = 13703;
            public static final int LEAGUE_TASK_DEFEAT_THE_CORPOREAL_BEAST_150_TIMES = 13704;
            public static final int LEAGUE_TASK_DEFEAT_THE_CORPOREAL_BEAST_250_TIMES = 13705;
            public static final int LEAGUE_TASK_DEFEAT_VENENATIS_300_TIMES = 13706;
            public static final int LEAGUE_TASK_DEFEAT_VETION_300_TIMES = 13707;
            public static final int LEAGUE_TASK_DEFEAT_NEX_300_TIMES = 13708;
            public static final int LEAGUE_TASK_DEFEAT_THE_NIGHTMARE_150_TIMES = 13709;
            public static final int LEAGUE_TASK_DEFEAT_AWAKENED_LEVIATHAN = 13710;
            public static final int LEAGUE_TASK_DEFEAT_300_DEMONIC_GORILLAS = 13711;
            public static final int LEAGUE_TASK_THE_FIGHT_CAVES_COMBAT_ACHIEVEMENTS = 13712;
            public static final int LEAGUE_TASK_COMPLETE_THE_FIGHT_CAVES_10_TIMES = 13713;
            public static final int LEAGUE_TASK_COMPLETE_THE_FIGHT_CAVES_15_TIMES = 13714;
            public static final int LEAGUE_TASK_COMPLETE_THE_FIGHT_CAVES_5_TIMES = 13715;
            public static final int LEAGUE_TASK_DEFEAT_500_DEMONIC_GORILLAS = 13716;
            public static final int LEAGUE_TASK_DEFEAT_150_TORMENTED_DEMONS = 13717;
            public static final int LEAGUE_TASK_DEFEAT_300_TORMENTED_DEMONS = 13718;
            public static final int LEAGUE_TASK_DEFEAT_50_TORMENTED_DEMONS = 13719;
            public static final int LEAGUE_TASK_DEFEAT_THE_KRAKEN_BOSS_300_TIMES = 13720;
            public static final int LEAGUE_TASK_DEFEAT_AWAKENED_DUKE_SUCELLUS = 13721;
            public static final int LEAGUE_TASK_DEFEAT_ANY_GOD_WARS_DUNGEON_BOSS_500_TIMES = 13722;
            public static final int LEAGUE_TASK_DEFEAT_DUKE_SUCELLUS_300_TIMES = 13723;
            public static final int LEAGUE_TASK_DEFEAT_A_CROCODILE = 13724;
            public static final int LEAGUE_TASK_DEFEAT_EACH_DAGANNOTH_KING_300_TIMES = 13725;
            public static final int LEAGUE_TASK_DEFEAT_LEVIATHAN_300_TIMES = 13726;
            public static final int LEAGUE_TASK_DEFEAT_THE_NIGHTMARE_50_TIMES = 13727;
            public static final int LEAGUE_TASK_DEFEAT_A_FOSSIL_ISLAND_WYVERN = 13728;
            public static final int LEAGUE_TASK_DEFEAT_A_GREATER_DEMON_ON_KARAMJA = 13729;
            public static final int LEAGUE_TASK_DEFEAT_PHANTOM_MUSPAH_300_TIMES = 13730;
            public static final int LEAGUE_TASK_DEFEAT_VORKATH_300_TIMES = 13731;
            public static final int LEAGUE_TASK_DEFEAT_ZALCANO_100_TIMES = 13732;
            public static final int LEAGUE_TASK_DEFEAT_VORKATH_5_TIMES_WITHOUT_SPECIAL_DAMAGE = 13733;
            public static final int LEAGUE_TASK_DEFEAT_A_RUNE_DRAGON = 13734;
            public static final int LEAGUE_TASK_DEFEAT_AWAKENED_WHISPERER = 13735;
            public static final int LEAGUE_TASK_DEFEAT_A_STEEL_DRAGON_ON_KARAMJA = 13736;
            public static final int LEAGUE_TASK_DEFEAT_A_TORMENTED_DEMON = 13737;
            public static final int LEAGUE_TASK_DEFEAT_CERBERUS_300_TIMES = 13738;
            public static final int LEAGUE_TASK_DEFEAT_A_TZHAAR = 13739;
            public static final int LEAGUE_TASK_DEFEAT_A_WALL_BEAST = 13740;
            public static final int LEAGUE_TASK_DEFEAT_ZULRAH_150_TIMES = 13741;
            public static final int LEAGUE_TASK_DEFEAT_AN_ANCIENT_WYVERN = 13742;
            public static final int LEAGUE_TASK_DEFEAT_ZULRAH_300_TIMES = 13743;
            public static final int LEAGUE_TASK_DEFEAT_WHISPERER_300_TIMES = 13744;
            public static final int LEAGUE_TASK_DEFEAT_BRYOPHYTA = 13745;
            public static final int LEAGUE_TASK_DEFEAT_CERBERUS_BEFORE_SHE_SUMMONS_SOULS = 13746;
            public static final int LEAGUE_TASK_DEFEAT_NEX_200_TIMES = 13747;
            public static final int LEAGUE_TASK_DEFEAT_OBOR = 13748;
            public static final int LEAGUE_TASK_DEFEAT_ONE_OF_FENKENSTRAINS_EXPERIMENTS = 13749;
            public static final int LEAGUE_TASK_DEFEAT_SCURRIUS = 13750;
            public static final int LEAGUE_TASK_DEFEAT_SCURRIUS_10_TIMES = 13751;
            public static final int LEAGUE_TASK_DEFEAT_SCURRIUS_25_TIMES = 13752;
            public static final int LEAGUE_TASK_DEFEAT_A_BASILISK_KNIGHT = 13753;
            public static final int LEAGUE_TASK_KILL_GALVEK = 13754;
            public static final int LEAGUE_TASK_DEFEAT_THE_LESSER_DEMON_IN_THE_WIZARDS_TOWER = 13755;
            public static final int LEAGUE_TASK_DEFEAT_THE_THEATRE_OF_BLOOD_HARDMODE = 13756;
            public static final int LEAGUE_TASK_DEFEAT_PHOSANIS_NIGHTMARE = 13757;
            public static final int LEAGUE_TASK_DEFEAT_A_URIUM_SHADE = 13758;
            public static final int LEAGUE_TASK_DEFEAT_THE_THEATRE_OF_BLOOD_HARDMODE_50_TIMES = 13759;
            public static final int LEAGUE_TASK_DEFEAT_CALLISTO = 13760;
            public static final int LEAGUE_TASK_DEFEAT_DUKE_SUCELLUS = 13761;
            public static final int LEAGUE_TASK_DEFEAT_VORKATH_15_TIMES_WITHOUT_LEAVING = 13762;
            public static final int LEAGUE_TASK_DEFEAT_DUKE_SUCELLUS_150_TIMES = 13763;
            public static final int LEAGUE_TASK_DEFEAT_DUKE_SUCELLUS_50_TIMES = 13764;
            public static final int LEAGUE_TASK_DEFEAT_EACH_DAGANNOTH_KING_150_TIMES = 13765;
            public static final int LEAGUE_TASK_DEFEAT_EACH_DAGANNOTH_KING_50_TIMES = 13766;
            public static final int LEAGUE_TASK_DEFEAT_ANY_GOD_WARS_DUNGEON_BOSS_100_TIMES = 13767;
            public static final int LEAGUE_TASK_DEFEAT_ANY_GOD_WARS_DUNGEON_BOSS_250_TIMES = 13768;
            public static final int LEAGUE_TASK_DEFEAT_CERBERUS = 13769;
            public static final int LEAGUE_TASK_DEFEAT_CERBERUS_150_TIMES = 13770;
            public static final int LEAGUE_TASK_DEFEAT_CALLISTO_150_TIMES = 13771;
            public static final int LEAGUE_TASK_DEFEAT_CALLISTO_50_TIMES = 13772;
            public static final int LEAGUE_TASK_DEFEAT_SCORPIA = 13773;
            public static final int LEAGUE_TASK_DEFEAT_PHANTOM_MUSPAH = 13774;
            public static final int LEAGUE_TASK_DEFEAT_THE_CHAOS_ELEMENTAL = 13775;
            public static final int LEAGUE_TASK_DEFEAT_THE_CORPOREAL_BEAST = 13776;
            public static final int LEAGUE_TASK_DEFEAT_PHANTOM_MUSPAH_150_TIMES = 13777;
            public static final int LEAGUE_TASK_DEFEAT_PHANTOM_MUSPAH_50_TIMES = 13778;
            public static final int LEAGUE_TASK_DEFEAT_SEREN = 13779;
            public static final int LEAGUE_TASK_DEFEAT_THE_DAGANNOTH_KINGS_WITHOUT_LEAVING = 13780;
            public static final int LEAGUE_TASK_DEFEAT_NEX_150_TIMES = 13781;
            public static final int LEAGUE_TASK_DEFEAT_THE_CORPOREAL_BEAST_50_TIMES = 13782;
            public static final int LEAGUE_TASK_DEFEAT_VORKATH = 13783;
            public static final int LEAGUE_TASK_DEFEAT_CERBERUS_50_TIMES = 13784;
            public static final int LEAGUE_TASK_DEFEAT_VORKATH_150_TIMES = 13785;
            public static final int LEAGUE_TASK_DEFEAT_VORKATH_50_TIMES = 13786;
            public static final int LEAGUE_TASK_DEFEAT_THE_KING_BLACK_DRAGON = 13787;
            public static final int LEAGUE_TASK_DEFEAT_AWAKENED_VARDORVIS = 13788;
            public static final int LEAGUE_TASK_DEFEAT_COMMANDER_ZILYANA = 13789;
            public static final int LEAGUE_TASK_DEFEAT_GENERAL_GRAARDOR = 13790;
            public static final int LEAGUE_TASK_DEFEAT_VENENATIS = 13791;
            public static final int LEAGUE_TASK_DEFEAT_KREEARRA = 13792;
            public static final int LEAGUE_TASK_DEFEAT_KRIL_TSUTSAROTH = 13793;
            public static final int LEAGUE_TASK_DEFEAT_NEX = 13794;
            public static final int LEAGUE_TASK_DEFEAT_NEX_50_TIMES = 13795;
            public static final int LEAGUE_TASK_DEFEAT_THE_GIANT_MOLE_300_TIMES = 13796;
            public static final int LEAGUE_TASK_DEFEAT_WHISPERER = 13797;
            public static final int LEAGUE_TASK_DEFEAT_WHISPERER_150_TIMES = 13798;
            public static final int LEAGUE_TASK_DEFEAT_VENENATIS_150_TIMES = 13799;
            public static final int LEAGUE_TASK_DEFEAT_THE_GROTESQUE_GUARDIANS = 13800;
            public static final int LEAGUE_TASK_DEFEAT_VARDORVIS_300_TIMES = 13801;
            public static final int LEAGUE_TASK_DEFEAT_VENENATIS_50_TIMES = 13802;
            public static final int LEAGUE_TASK_DEFEAT_150_DEMONIC_GORILLAS = 13803;
            public static final int LEAGUE_TASK_DEFEAT_A_DUST_DEVIL_IN_THE_KHARIDIAN_DESERT = 13804;
            public static final int LEAGUE_TASK_DEFEAT_A_CAVE_KRAKEN = 13805;
            public static final int LEAGUE_TASK_DEFEAT_THE_GROTESQUE_GUARDIANS_150_TIMES = 13806;
            public static final int LEAGUE_TASK_DEFEAT_THE_GROTESQUE_GUARDIANS_50_TIMES = 13807;
            public static final int LEAGUE_TASK_DEFEAT_LEVIATHAN = 13808;
            public static final int LEAGUE_TASK_DEFEAT_THE_NIGHTMARE = 13809;
            public static final int LEAGUE_TASK_DEFEAT_LEVIATHAN_150_TIMES = 13810;
            public static final int LEAGUE_TASK_DEFEAT_WHISPERER_50_TIMES = 13811;
            public static final int LEAGUE_TASK_DEFEAT_VETION = 13812;
            public static final int LEAGUE_TASK_DEFEAT_LEVIATHAN_50_TIMES = 13813;
            public static final int LEAGUE_TASK_DEFEAT_THE_NIGHTMARE_25_TIMES = 13814;
            public static final int LEAGUE_TASK_DEFEAT_VETION_150_TIMES = 13815;
            public static final int LEAGUE_TASK_DEFEAT_VETION_50_TIMES = 13816;
            public static final int LEAGUE_TASK_DEFEAT_A_DEMONIC_GORILLA = 13817;
            public static final int LEAGUE_TASK_DEFEAT_NEX_100_TIMES = 13818;
            public static final int LEAGUE_TASK_DEFEAT_A_MITHRIL_DRAGON = 13819;
            public static final int LEAGUE_TASK_DEFEAT_A_SMOKE_DEVIL = 13820;
            public static final int LEAGUE_TASK_DEFEAT_ROYAL_TITANS_SOLO = 13821;
            public static final int LEAGUE_TASK_DEFEAT_THE_KRAKEN_BOSS_150_TIMES = 13822;
            public static final int LEAGUE_TASK_DEFEAT_THE_KRAKEN_BOSS_50_TIMES = 13823;
            public static final int LEAGUE_TASK_DEFEAT_THE_KALPHITE_QUEEN = 13824;
            public static final int LEAGUE_TASK_DEFEAT_THE_KALPHITE_QUEEN_150_TIMES = 13825;
            public static final int LEAGUE_TASK_DEFEAT_THE_PENANCE_QUEEN = 13826;
            public static final int LEAGUE_TASK_DEFEAT_A_RUNITE_GOLEM = 13827;
            public static final int LEAGUE_TASK_DEFEAT_AN_ABYSSAL_DEMON_IN_MORYTANIA = 13828;
            public static final int LEAGUE_TASK_KILL_A_ROCK_CRAB_ONE_HIT = 13829;
            public static final int LEAGUE_TASK_DEFEAT_THE_KALPHITE_QUEEN_50_TIMES = 13830;
            public static final int LEAGUE_TASK_DEFEAT_A_GARGOYLE_IN_MORYTANIA = 13831;
            public static final int LEAGUE_TASK_DEFEAT_VARDORVIS = 13832;
            public static final int LEAGUE_TASK_DEFEAT_VARDORVIS_150_TIMES = 13833;
            public static final int LEAGUE_TASK_DEFEAT_A_KALPHITE_GUARDIAN = 13834;
            public static final int LEAGUE_TASK_DEFEAT_A_SCARAB_MAGE = 13835;
            public static final int LEAGUE_TASK_DEFEAT_NEX_SOLO = 13836;
            public static final int LEAGUE_TASK_DEFEAT_30_BLACK_DRAGONS_IN_ASGARNIA = 13837;
            public static final int LEAGUE_TASK_DEFEAT_A_DARK_BEAST_IN_TIRANNWN = 13838;
            public static final int LEAGUE_TASK_DEFEAT_A_NECHRYAEL_IN_TIRANNWN = 13839;
            public static final int LEAGUE_TASK_DEFEAT_A_FIYR_SHADE = 13840;
            public static final int LEAGUE_TASK_DEFEAT_A_VYREWATCH_SENTINEL = 13841;
            public static final int LEAGUE_TASK_DEFEAT_VARDORVIS_50_TIMES = 13842;
            public static final int LEAGUE_TASK_DEFEAT_SOME_ANIMATED_RUNE_ARMOUR = 13843;
            public static final int LEAGUE_TASK_DEFEAT_A_HYDRA = 13844;
            public static final int LEAGUE_TASK_DEFEAT_ROYAL_TITANS_50_TIMES = 13845;
            public static final int LEAGUE_TASK_DEFEAT_ZALCANO = 13846;
            public static final int LEAGUE_TASK_DEFEAT_ZALCANO_50_TIMES = 13847;
            public static final int LEAGUE_TASK_DEFEAT_ZULRAH = 13848;
            public static final int LEAGUE_TASK_DEFEAT_ZULRAH_50_TIMES = 13849;
            public static final int LEAGUE_TASK_DEFEAT_A_SLAYER_BOSS = 13850;
            public static final int LEAGUE_TASK_DEFEAT_THE_EVIL_CHICKEN = 13851;
            public static final int LEAGUE_TASK_DEFEAT_A_GANG_BOSS = 13852;
            public static final int LEAGUE_TASK_DEFEAT_A_BLACK_DEMON_IN_ASGARNIA = 13853;
            public static final int LEAGUE_TASK_DEFEAT_A_BLUE_DRAGON_IN_ASGARNIA = 13854;
            public static final int LEAGUE_TASK_DEFEAT_SOL_HEREDIT_10_TIMES = 13855;
            public static final int LEAGUE_TASK_DEFEAT_SOL_HEREDIT_5_TIMES = 13856;
            public static final int LEAGUE_TASK_DEFEAT_A_SKELETAL_WYVERN = 13857;
            public static final int LEAGUE_TASK_DEFEAT_A_BLOODVELD_IN_KANDARIN = 13858;
            public static final int LEAGUE_TASK_DEFEAT_THE_GIANT_MOLE = 13859;
            public static final int LEAGUE_TASK_DEFEAT_THE_GIANT_MOLE_150_TIMES = 13860;
            public static final int LEAGUE_TASK_DEFEAT_A_GREEN_DRAGON_IN_THE_WILDERNESS = 13861;
            public static final int LEAGUE_TASK_DEFEAT_A_LAVA_DRAGON_IN_THE_WILDERNESS = 13862;
            public static final int LEAGUE_TASK_DEFEAT_A_LESSER_DEMON = 13863;
            public static final int LEAGUE_TASK_DEFEAT_AN_ADULT_CHROMATIC_DRAGON = 13864;
            public static final int LEAGUE_TASK_DEFEAT_THE_CHAOS_FANATIC = 13865;
            public static final int LEAGUE_TASK_DEFEAT_A_DRAKE = 13866;
            public static final int LEAGUE_TASK_DEFEAT_THE_CRAZY_ARCHAEOLOGIST = 13867;
            public static final int LEAGUE_TASK_DEFEAT_A_TORTOISE_WITH_RIDERS_IN_KANDARIN = 13868;
            public static final int LEAGUE_TASK_DEFEAT_THE_THERMONUCLEAR_SMOKE_DEVIL = 13869;
            public static final int LEAGUE_TASK_DEFEAT_A_DAGANNOTH_IN_THE_FREMENNIK_PROVINCE = 13870;
            public static final int LEAGUE_TASK_DEFEAT_A_JELLY_IN_THE_FREMENNIK_PROVINCE = 13871;
            public static final int LEAGUE_TASK_DEFEAT_A_KURASK_IN_THE_FREMENNIK_PROVINCE = 13872;
            public static final int LEAGUE_TASK_DEFEAT_A_TROLL_IN_THE_FREMENNIK_PROVINCE = 13873;
            public static final int LEAGUE_TASK_DEFEAT_A_TUROTH_IN_THE_FREMENNIK_PROVINCE = 13874;
            public static final int LEAGUE_TASK_DEFEAT_THE_GIANT_MOLE_50_TIMES = 13875;
            public static final int LEAGUE_TASK_DEFEAT_A_BLOODVELD_IN_TIRANNWN = 13876;
            public static final int LEAGUE_TASK_DEFEAT_A_KURASK_IN_TIRANNWN = 13877;
            public static final int LEAGUE_TASK_DEFEAT_A_BLACK_DEMON_ON_KARAMJA = 13878;
            public static final int LEAGUE_TASK_DEFEAT_6_6_6_JADS = 13879;
            public static final int LEAGUE_TASK_DEFEAT_ROYAL_TITANS = 13880;
            public static final int LEAGUE_TASK_DEFEAT_AN_ELF_IN_TIRANNWN = 13881;
            public static final int LEAGUE_TASK_DEFEAT_A_REVENANT_DRAGON = 13882;
            public static final int LEAGUE_TASK_KILL_BLACK_KNIGHT = 13883;
            public static final int LEAGUE_TASK_DEFEAT_10_SUPERIOR_SLAYER_CREATURES = 13884;
            public static final int LEAGUE_TASK_DEFEAT_20_SUPERIOR_SLAYER_CREATURES = 13885;
            public static final int LEAGUE_TASK_DEFEAT_50_SUPERIOR_SLAYER_CREATURES = 13886;
            public static final int ACTION_KILL_A_CHICKEN = 13887;
            public static final int ACTION_KILL_A_HILL_GIANT = 13888;
            public static final int LEAGUE_TASK_DEFEAT_A_SUPERIOR_SLAYER_CREATURE = 13889;
            public static final int LEAGUE_TASK_DEFEAT_THE_ABYSSAL_SIRE = 13890;
            public static final int LEAGUE_TASK_DEFEAT_THE_ABYSSAL_SIRE_150_TIMES = 13891;
            public static final int LEAGUE_TASK_DEFEAT_THE_ABYSSAL_SIRE_300_TIMES = 13892;
            public static final int LEAGUE_TASK_DEFEAT_THE_ABYSSAL_SIRE_50_TIMES = 13893;
            public static final int LEAGUE_TASK_1_WINTERTODT_KILL = 13894;
            public static final int LEAGUE_TASK_KILL_1_UNIQUE_ECHO_BOSS = 13895;
            public static final int LEAGUE_TASK_KILL_150_ECHO_BOSSES = 13896;
            public static final int LEAGUE_TASK_KILL_2_UNIQUE_ECHO_BOSSES = 13897;
            public static final int LEAGUE_TASK_KILL_25_ECHO_BOSSES = 13898;
            public static final int LEAGUE_TASK_KILL_3_UNIQUE_ECHO_BOSSES = 13899;
            public static final int LEAGUE_TASK_KILL_4_UNIQUE_ECHO_BOSSES = 13900;
            public static final int LEAGUE_TASK_KILL_5_BUNNIES = 13901;
            public static final int LEAGUE_TASK_KILL_5_CREATURES_WITH_A_MACE = 13902;
            public static final int LEAGUE_TASK_KILL_75_ECHO_BOSSES = 13903;
            public static final int LEAGUE_TASK_KILL_A_FROG = 13904;
            public static final int LEAGUE_TASK_KILL_A_NECROMANCER = 13905;
            public static final int LEAGUE_TASK_KILL_A_RAT = 13906;
            public static final int ACTION_KILL_A_SCORPION = 13907;
            public static final int LEAGUE_TASK_KILL_A_SCORPION_WITH_A_MITHRIL_SPEAR = 13908;
            public static final int LEAGUE_TASK_KILL_AN_IMP_WITH_AN_EARTH_SPELL = 13909;
            public static final int LEAGUE_TASK_KILL_THREE_CHICKENS_IN_6_SECONDS = 13910;
            public static final int LEAGUE_TASK_50_WINTERTODT_KILLS = 13911;
            public static final int LEAGUE_TASK_1_TEMPOROSS_KILL = 13912;
            public static final int LEAGUE_TASK_10_TEMPOROSS_KILLS = 13913;
            public static final int LEAGUE_TASK_25_TEMPOROSS_KILLS = 13914;
            public static final int LEAGUE_TASK_10_WINTERTODT_KILLS = 13915;
            public static final int LEAGUE_TASK_25_WINTERTODT_KILLS = 13916;
            public static final int LEAGUE_TASK_1_YAMA_KILL = 13917;
            public static final int LEAGUE_TASK_50_YAMA_KILLS = 13918;
            public static final int LEAGUE_TASK_150_YAMA_KILLS = 13919;
            public static final int LEAGUE_TASK_EQUIP_A_PROTEST_BANNER = 13920;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_MITHRIL_SET = 13921;
            public static final int LEAGUE_TASK_EQUIP_A_MAPLE_SHORTBOW = 13922;
            public static final int LEAGUE_TASK_EQUIP_A_SPINY_HELMET = 13923;
            public static final int LEAGUE_TASK_EQUIP_A_STUDDED_BODY_AND_CHAPS = 13924;
            public static final int LEAGUE_TASK_EQUIP_A_WIZARD_ROBE_AND_HAT = 13925;
            public static final int LEAGUE_TASK_EQUIP_A_TEAM_CAPE = 13926;
            public static final int LEAGUE_TASK_EQUIP_A_BONECRUSHER_NECKLACE = 13927;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_HUNTER_CROSSBOW = 13928;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_HUNTER_LANCE = 13929;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_WARHAMMER = 13930;
            public static final int LEAGUE_TASK_EQUIP_A_GOLDEN_TENCH = 13931;
            public static final int LEAGUE_TASK_EQUIP_A_KODAI_WAND = 13932;
            public static final int LEAGUE_TASK_EQUIP_A_PAIR_OF_DRAGON_CLAWS = 13933;
            public static final int LEAGUE_TASK_EQUIP_A_TWISTED_BUCKLER = 13934;
            public static final int LEAGUE_TASK_EQUIP_AN_ELDER_MAUL = 13935;
            public static final int LEAGUE_TASK_EQUIP_ANY_ANCESTRAL_PIECE = 13936;
            public static final int LEAGUE_TASK_EQUIP_DINHS_BULWARK = 13937;
            public static final int LEAGUE_TASK_EQUIP_500_BLACK_CHINCHOMPAS = 13938;
            public static final int LEAGUE_TASK_EQUIP_A_BLESSED_SPIRIT_SHIELD = 13939;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_DAGONHAI_SET = 13940;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_ARCHER_CHOMPY_HAT = 13941;
            public static final int LEAGUE_TASK_EQUIP_A_FIGHTER_TORSO = 13942;
            public static final int LEAGUE_TASK_EQUIP_A_MALEDICTION_WARD = 13943;
            public static final int LEAGUE_TASK_EQUIP_A_HEAVY_BALLISTA = 13944;
            public static final int LEAGUE_TASK_EQUIP_A_LIGHT_BALLISTA = 13945;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_THE_DAGONHAI_SET = 13946;
            public static final int LEAGUE_TASK_EQUIP_A_GHRAZI_RAPIER = 13947;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_THE_INQUISITORS_SET = 13948;
            public static final int LEAGUE_TASK_EQUIP_A_RING_OF_THE_GODS = 13949;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_CHAINBODY_IN_THE_KHARIDIAN_DESERT = 13950;
            public static final int LEAGUE_TASK_EQUIP_A_THAMMARONS_SCEPTRE = 13951;
            public static final int LEAGUE_TASK_EQUIP_AN_ABYSSAL_TENTACLE = 13952;
            public static final int LEAGUE_TASK_EQUIP_A_COMPLETED_GOD_BOOK = 13953;
            public static final int LEAGUE_TASK_EQUIP_AN_OCCULT_NECKLACE = 13954;
            public static final int LEAGUE_TASK_EQUIP_SOME_ZENYTE_JEWELRY = 13955;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_CROSSBOW = 13956;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGONBONE_NECKLACE = 13957;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_THE_JUSTICIAR_SET = 13958;
            public static final int LEAGUE_TASK_EQUIP_A_SANGUINESTI_STAFF = 13959;
            public static final int LEAGUE_TASK_EQUIP_AN_INQUISITORS_MACE = 13960;
            public static final int LEAGUE_TASK_EQUIP_SOME_GUARDIAN_BOOTS = 13961;
            public static final int LEAGUE_TASK_EQUIP_A_MAGES_BOOK = 13962;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_ARMADYL_ARMOUR_SET = 13963;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_BANDOS_ARMOUR_SET = 13964;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGONFIRE_WARD = 13965;
            public static final int LEAGUE_TASK_EQUIP_A_MASTER_WAND = 13966;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_THE_CARPENTERS_OUTFIT = 13967;
            public static final int LEAGUE_TASK_EQUIP_A_TREASONOUS_RING = 13968;
            public static final int LEAGUE_TASK_EQUIP_A_TYRANNICAL_RING = 13969;
            public static final int LEAGUE_TASK_EQUIP_AN_ODIUM_WARD = 13970;
            public static final int LEAGUE_TASK_EQUIP_CRAWS_BOW = 13971;
            public static final int LEAGUE_TASK_EQUIP_A_GODSWORD = 13972;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_THE_ARMADYL_ARMOUR_SET = 13973;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_THE_BANDOS_ARMOUR_SET = 13974;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_TORVA_ARMOUR = 13975;
            public static final int LEAGUE_TASK_EQUIP_A_STAFF_OF_THE_DEAD = 13976;
            public static final int LEAGUE_TASK_EQUIP_FULL_RECOLOUR_GRACEFUL_OUTFIT = 13977;
            public static final int LEAGUE_TASK_EQUIP_A_NEITIZNOT_FACEGUARD = 13978;
            public static final int LEAGUE_TASK_EQUIP_EVERY_DAGANNOTH_KING_RING = 13979;
            public static final int LEAGUE_TASK_EQUIP_THE_MAGUS_RING = 13980;
            public static final int LEAGUE_TASK_EQUIP_THE_VENATOR_BOW = 13981;
            public static final int LEAGUE_TASK_EQUIP_ICE_SCEPTRE = 13982;
            public static final int LEAGUE_TASK_EQUIP_A_ZARYTE_CROSSBOW = 13983;
            public static final int LEAGUE_TASK_EQUIP_AN_ARMADYL_CROSSBOW = 13984;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_MASORI_ARMOUR = 13985;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_THE_INFINITY_ROBE_SET = 13986;
            public static final int LEAGUE_TASK_EQUIP_OSMUMTENS_FANG = 13987;
            public static final int LEAGUE_TASK_EQUIP_SOME_PRIMORDIAL_PEGASIAN_OR_ETERNAL_BOOTS = 13988;
            public static final int LEAGUE_TASK_EQUIP_THE_BELLATOR_RING = 13989;
            public static final int LEAGUE_TASK_EQUIP_SOME_ZARYTE_VAMBRACES = 13990;
            public static final int LEAGUE_TASK_EQUIP_THE_AMULET_OF_RANCOUR = 13991;
            public static final int LEAGUE_TASK_EQUIP_FULL_ELDER_CHAOS_ROBE = 13992;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_GRACEFUL_OUTFIT = 13993;
            public static final int LEAGUE_TASK_EQUIP_THE_ACCURSED_SCEPTRE = 13994;
            public static final int LEAGUE_TASK_EQUIP_THE_URSINE_CHAINMACE = 13995;
            public static final int LEAGUE_TASK_EQUIP_THE_VOIDWAKER = 13996;
            public static final int LEAGUE_TASK_EQUIP_THE_WEBWEAVER = 13997;
            public static final int LEAGUE_TASK_EQUIP_VIGGORAS_CHAINMACE = 13998;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_GOLDEN_PROSPECTOR = 13999;
            public static final int LEAGUE_TASK_EQUIP_AN_INFERNAL_TOOL = 14000;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_TORVA_ARMOUR = 14001;
            public static final int LEAGUE_TASK_EQUIP_TWINFLAME_STAFF = 14002;
            public static final int LEAGUE_TASK_EQUIP_THE_NOXIOUS_HALBERD = 14003;
            public static final int LEAGUE_TASK_EQUIP_THE_ELIDINIS_WARD = 14004;
            public static final int LEAGUE_TASK_EQUIP_THE_VENATOR_RING = 14005;
            public static final int LEAGUE_TASK_EQUIP_A_MANIACAL_MONKEY_BACKPACK = 14006;
            public static final int LEAGUE_TASK_EQUIP_A_CRYSTAL_GRAIL = 14007;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_CRYSTAL_ARMOUR_SET = 14008;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_THE_DRAGONSTONE_ARMOUR_SET = 14009;
            public static final int LEAGUE_TASK_EQUIP_A_SERPENTINE_HELM = 14010;
            public static final int LEAGUE_TASK_EQUIP_AN_ENHANCED_CRYSTAL_WEAPON = 14011;
            public static final int LEAGUE_TASK_EQUIP_AN_ELVEN_SIGNET = 14012;
            public static final int LEAGUE_TASK_EQUIP_A_CRYSTAL_HALBERD = 14013;
            public static final int LEAGUE_TASK_EQUIP_A_CRYSTAL_TOOL = 14014;
            public static final int LEAGUE_TASK_EQUIP_A_DORGESHUUN_CROSSBOW = 14015;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_PLATEBODY_OR_DRAGON_KITESHIELD = 14016;
            public static final int LEAGUE_TASK_EQUIP_A_FIRE_CAPE = 14017;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_OBSIDIAN_ARMOUR_SET = 14018;
            public static final int LEAGUE_TASK_EQUIP_A_BERSERKER_RING = 14019;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_BLOODBARK_ARMOUR = 14020;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_RAIMENT_OF_THE_EYE = 14021;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_SWAMPBARK_ARMOUR = 14022;
            public static final int LEAGUE_TASK_EQUIP_A_BRINE_SABRE = 14023;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_ZEALOTS_ROBES = 14024;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_ROCKSHELL_ARMOUR_SET = 14025;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SKELETAL_ARMOUR_SET = 14026;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_TAI_BWO_WANNAI_VILLAGER_SET = 14027;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_PROSPECTOR_OUTFIT = 14028;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_THE_FORESTRY_OUTFIT = 14029;
            public static final int LEAGUE_TASK_EQUIP_A_GNOME_SCARF = 14030;
            public static final int LEAGUE_TASK_EQUIP_A_HEAVY_CASKET = 14031;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SPINED_ARMOUR_SET = 14032;
            public static final int LEAGUE_TASK_EQUIP_A_HILL_GIANT_CLUB = 14033;
            public static final int LEAGUE_TASK_EQUIP_A_KRUK_JR_MONKEY_BACKPACK = 14034;
            public static final int LEAGUE_TASK_EQUIP_A_LARGE_SPADE = 14035;
            public static final int LEAGUE_TASK_EQUIP_A_LEAFBLADED_BATTLEAXE = 14036;
            public static final int LEAGUE_TASK_EQUIP_A_SCROLL_SACK = 14037;
            public static final int LEAGUE_TASK_EQUIP_A_MATCHING_TRIBAL_MASK_AND_BROODOO_SHIELD = 14038;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_ROGUE_OUTFIT = 14039;
            public static final int LEAGUE_TASK_EQUIP_THE_MASK_OF_REBIRTH = 14040;
            public static final int LEAGUE_TASK_EQUIP_A_MUD_BATTLESTAFF = 14041;
            public static final int LEAGUE_TASK_EQUIP_A_SEERCULL = 14042;
            public static final int LEAGUE_TASK_EQUIP_A_TECU_SALAMANDER = 14043;
            public static final int ACTION_EQUIP_AVERNIC_TREADS = 14044;
            public static final int ACTION_EQUIP_THE_CONFLICTION_GAUNTLETS = 14045;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_DEFENDER = 14046;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_VOID_KNIGHT_SET = 14047;
            public static final int LEAGUE_TASK_EQUIP_A_NIGHTMARE_STAFF = 14048;
            public static final int LEAGUE_TASK_EQUIP_A_SARADOMIN_SWORD = 14049;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_ZEALOTS_ROBES = 14050;
            public static final int LEAGUE_TASK_EQUIP_A_PIRATE_HOOK = 14051;
            public static final int LEAGUE_TASK_EQUIP_A_PRINCE_AWOWOGEI_MONKEY_BACKPACK = 14052;
            public static final int LEAGUE_TASK_EQUIP_A_RAT_BONE_WEAPON = 14053;
            public static final int LEAGUE_TASK_EQUIP_A_RED_TOPAZ_MACHETE = 14054;
            public static final int LEAGUE_TASK_EQUIP_A_RING_OF_ENDURANCE = 14055;
            public static final int LEAGUE_TASK_EQUIP_AN_AVERNIC_DEFENDER = 14056;
            public static final int LEAGUE_TASK_EQUIP_ANY_FULL_BARROWS_ARMOUR_SET = 14057;
            public static final int LEAGUE_TASK_EQUIP_A_ZAMORAKIAN_SPEAR = 14058;
            public static final int LEAGUE_TASK_EQUIP_ARANEA_BOOTS = 14059;
            public static final int LEAGUE_TASK_EQUIP_A_SEERS_RING = 14060;
            public static final int LEAGUE_TASK_EQUIP_A_WARRIOR_RING = 14061;
            public static final int LEAGUE_TASK_EQUIP_100_BLACK_CHINCHOMPAS = 14062;
            public static final int ACTION_EQUIP_THE_EYE_OF_AYAK = 14063;
            public static final int LEAGUE_TASK_EQUIP_250_BLACK_CHINCHOMPAS = 14064;
            public static final int LEAGUE_TASK_EQUIP_A_TOKTZKETXIL = 14065;
            public static final int LEAGUE_TASK_EQUIP_A_TOKTZXILAK = 14066;
            public static final int LEAGUE_TASK_EQUIP_A_TOKTZXILEK = 14067;
            public static final int LEAGUE_TASK_EQUIP_A_TORMENTED_WEAPON = 14068;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_2HANDED_SWORD_IN_THE_WILDERNESS = 14069;
            public static final int LEAGUE_TASK_EQUIP_AN_ENCHANTED_SLAYER_STAFF = 14070;
            public static final int LEAGUE_TASK_EQUIP_A_TZHAARKETOM = 14071;
            public static final int LEAGUE_TASK_EQUIP_AN_ARCHERS_RING = 14072;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_THE_GOLDEN_PROSPECTOR_SET = 14073;
            public static final int LEAGUE_TASK_EQUIP_A_PAIR_OF_DRAGON_BOOTS = 14074;
            public static final int LEAGUE_TASK_EQUIP_A_ZOMBIE_AXE = 14075;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_ANGLERS_OUTFIT = 14076;
            public static final int LEAGUE_TASK_EQUIP_AN_AMULET_OF_ETERNAL_GLORY = 14077;
            public static final int LEAGUE_TASK_EQUIP_AN_ANCIENT_WYVERN_SHIELD = 14078;
            public static final int LEAGUE_TASK_EQUIP_THE_MOONLIGHT_FANATIC_HELMET = 14079;
            public static final int LEAGUE_TASK_EQUIP_AN_AVAS_ASSEMBLER = 14080;
            public static final int LEAGUE_TASK_EQUIP_THE_ANCIENT_SCEPTRE = 14081;
            public static final int LEAGUE_TASK_EQUIP_FULL_AHRIMS_ARMOUR_SET = 14082;
            public static final int LEAGUE_TASK_EQUIP_THE_ULTOR_RING = 14083;
            public static final int LEAGUE_TASK_EQUIP_AN_IBANS_STAFF_U = 14084;
            public static final int LEAGUE_TASK_EQUIP_AN_INFERNAL_CAPE = 14085;
            public static final int LEAGUE_TASK_EQUIP_FULL_DHAROKS_ARMOUR_SET = 14086;
            public static final int LEAGUE_TASK_EQUIP_AN_OBSIDIAN_CAPE = 14087;
            public static final int LEAGUE_TASK_EQUIP_A_KARAMJA_MONKEY_BACKPACK = 14088;
            public static final int LEAGUE_TASK_EQUIP_TONALZTICS_OF_RALOS = 14089;
            public static final int LEAGUE_TASK_EQUIP_A_BRIMSTONE_RING = 14090;
            public static final int LEAGUE_TASK_EQUIP_FULL_GUTHANS_ARMOUR_SET = 14091;
            public static final int LEAGUE_TASK_EQUIP_FULL_KARILS_ARMOUR_SET = 14092;
            public static final int LEAGUE_TASK_EQUIP_A_DUST_BATTLESTAFF = 14093;
            public static final int LEAGUE_TASK_EQUIP_BRYOPHYTAS_STAFF = 14094;
            public static final int LEAGUE_TASK_EQUIP_BURNING_CLAWS = 14095;
            public static final int LEAGUE_TASK_EQUIP_A_FISH_SACK = 14096;
            public static final int LEAGUE_TASK_EQUIP_EVERY_BARROWS_ARMOUR_SET = 14097;
            public static final int LEAGUE_TASK_EQUIP_EVERY_DRACONIC_SHIELD = 14098;
            public static final int LEAGUE_TASK_EQUIP_AN_ANCIENT_GODSWORD = 14099;
            public static final int LEAGUE_TASK_EQUIP_A_SARACHNIS_CUDGEL = 14100;
            public static final int LEAGUE_TASK_EQUIP_FULL_TORAGS_ARMOUR_SET = 14101;
            public static final int LEAGUE_TASK_EQUIP_FULL_VERACS_ARMOUR_SET = 14102;
            public static final int LEAGUE_TASK_EQUIP_MASORI_ASSEMBLER = 14103;
            public static final int LEAGUE_TASK_EQUIP_SOME_DRAGON_PLATELEGS_OR_A_DRAGON_PLATESKIRT = 14104;
            public static final int LEAGUE_TASK_EQUIP_SOME_FANCY_BOOTS_OR_FIGHTING_BOOTS = 14105;
            public static final int LEAGUE_TASK_EQUIP_SOME_MITHRIL_GLOVES = 14106;
            public static final int LEAGUE_TASK_EQUIP_THE_ABYSSAL_LANTERN = 14107;
            public static final int LEAGUE_TASK_EQUIP_THE_AMULET_OF_THE_EYE_UNDER_THE_PORTAL = 14108;
            public static final int LEAGUE_TASK_EQUIP_BOOTS_OF_BRIMSTONE = 14109;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_2HANDED_SWORD = 14110;
            public static final int LEAGUE_TASK_EQUIP_THE_ELIDINIS_WARD_OR = 14111;
            public static final int LEAGUE_TASK_EQUIP_FEROCIOUS_GLOVES = 14112;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_THE_SMITHS_OUTFIT = 14113;
            public static final int LEAGUE_TASK_EQUIP_THE_FARMERS_OUTFIT = 14114;
            public static final int LEAGUE_TASK_EQUIP_THE_RING_OF_THE_ELEMENTS = 14115;
            public static final int LEAGUE_TASK_EQUIP_THE_LIGHTBEARER = 14116;
            public static final int LEAGUE_TASK_EQUIP_A_GRANITE_HAMMER_OR_GRANITE_RING = 14117;
            public static final int LEAGUE_TASK_EQUIP_A_SPECTRAL_OR_ARCANE_SPIRIT_SHIELD = 14118;
            public static final int LEAGUE_TASK_EQUIP_AN_ELYSIAN_SPIRIT_SHIELD = 14119;
            public static final int LEAGUE_TASK_EQUIP_THE_PYROMANCERS_GARB = 14120;
            public static final int LEAGUE_TASK_EQUIP_TIER_5_SHAYZIEN_ARMOUR = 14121;
            public static final int LEAGUE_TASK_EQUIP_A_TRIDENT_OF_THE_SEAS = 14122;
            public static final int LEAGUE_TASK_EQUIP_A_WARPED_SCEPTRE = 14123;
            public static final int LEAGUE_TASK_EQUIP_AN_OGRE_EXPERT_CHOMPY_HAT = 14124;
            public static final int LEAGUE_TASK_EQUIP_A_PAIR_OF_DRAGON_BOOTS_IN_ASGARNIA = 14125;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_INQUISITORS_SET = 14126;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_JUSTICIAR_SET = 14127;
            public static final int LEAGUE_TASK_EQUIP_A_NIGHTMARE_STAFF_WITH_AN_ORB = 14128;
            public static final int LEAGUE_TASK_EQUIP_EVERY_GODSWORD = 14129;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_INFINITY_ROBE_SET = 14130;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_MASORI = 14131;
            public static final int LEAGUE_TASK_EQUIP_OSMUMTENS_FANG_OR = 14132;
            public static final int LEAGUE_TASK_EQUIP_THE_COLOSSAL_BLADE = 14133;
            public static final int LEAGUE_TASK_EQUIP_THE_TOME_OF_WATER = 14134;
            public static final int LEAGUE_TASK_EQUIP_SOME_PRIMORDIAL_PEGASIAN_AND_ETERNAL_BOOTS = 14135;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_VYRE_NOBLE = 14136;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_ANY_BARROWS_ARMOUR_SET = 14137;
            public static final int LEAGUE_TASK_EQUIP_A_SALVE_AMULET_E = 14138;
            public static final int ACTION_EQUIP_A_FLETCHING_KNIFE = 14139;
            public static final int ACTION_EQUIP_A_GREENMAN_MASK = 14140;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_SUNFIRE_FANATIC = 14141;
            public static final int ACTION_EQUIP_AN_ANTLER_GUARD = 14142;
            public static final int ACTION_EQUIP_AN_EGG = 14143;
            public static final int LEAGUE_TASK_EQUIP_ECHO_BOOTS = 14144;
            public static final int LEAGUE_TASK_EQUIP_SOME_GRANITE_LEGS = 14145;
            public static final int LEAGUE_TASK_EQUIP_FULL_ALCHEMISTS_OUTFIT = 14146;
            public static final int LEAGUE_TASK_EQUIP_FULL_BLOOD_MOON_ARMOUR = 14147;
            public static final int LEAGUE_TASK_EQUIP_FULL_BLUE_MOON_ARMOUR = 14148;
            public static final int LEAGUE_TASK_EQUIP_FULL_ECLIPSE_MOON_ARMOUR = 14149;
            public static final int LEAGUE_TASK_EQUIP_FULL_GUILD_HUNTER_OUTFIT = 14150;
            public static final int LEAGUE_TASK_EQUIP_FULL_HUEYCOATL_ARMOUR = 14151;
            public static final int LEAGUE_TASK_EQUIP_FULL_SUNFIRE_FANATIC = 14152;
            public static final int LEAGUE_TASK_EQUIP_GLACIAL_TEMOTLI = 14153;
            public static final int LEAGUE_TASK_EQUIP_A_CRYSTAL_BOW = 14154;
            public static final int LEAGUE_TASK_EQUIP_A_CRYSTAL_SHIELD = 14155;
            public static final int LEAGUE_TASK_EQUIP_A_DARK_BOW_IN_TIRANNWN = 14156;
            public static final int LEAGUE_TASK_EQUIP_ANY_PIECE_OF_CRYSTAL_ARMOUR = 14157;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_PICKAXE = 14158;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_FULL_HELM = 14159;
            public static final int LEAGUE_TASK_EQUIP_ALL_ZENYTE_JEWELRY = 14160;
            public static final int LEAGUE_TASK_EQUIP_AN_EXPERT_DRAGON_ARCHER_CHOMPY_HAT = 14161;
            public static final int LEAGUE_TASK_EQUIP_BLESSED_DIZANAS_QUIVER = 14162;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_ANCESTRAL = 14163;
            public static final int LEAGUE_TASK_EQUIP_EVERY_COMPLETED_GOD_BOOK = 14164;
            public static final int LEAGUE_TASK_EQUIP_A_MONKEY_BACKPACK = 14165;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_GILDED_SET = 14166;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_SET_OF_YAKHIDE_ARMOUR = 14167;
            public static final int LEAGUE_TASK_EQUIP_A_SPOTTIER_CAPE = 14168;
            public static final int LEAGUE_TASK_EQUIP_A_DEFENDER = 14169;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_ALCHEMISTS_OUTFIT = 14170;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_HUEYCOATL_ARMOUR = 14171;
            public static final int ACTION_EQUIP_AN_ORANGE = 14172;
            public static final int LEAGUE_TASK_EQUIP_PENDANT_OF_ATES = 14173;
            public static final int ACTION_EQUIP_ANY_PIECE_OF_MOONS_OF_PERIL_ARMOUR = 14174;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_DRAGON_SET = 14175;
            public static final int LEAGUE_TASK_EQUIP_A_PEARL_BARBARIAN_ROD = 14176;
            public static final int ACTION_EQUIP_A_PIECE_OF_BLOOD_MOON_ARMOUR = 14177;
            public static final int ACTION_EQUIP_A_PIECE_OF_BLUE_MOON_ARMOUR = 14178;
            public static final int ACTION_EQUIP_A_PIECE_OF_ECLIPSE_MOON_ARMOUR = 14179;
            public static final int LEAGUE_TASK_EQUIP_A_FROG_MASK = 14180;
            public static final int LEAGUE_TASK_EQUIP_A_HARPIE_BUG_LANTERN = 14181;
            public static final int LEAGUE_TASK_EQUIP_FULL_GRAAHK_LARUPIA_OR_KYATT = 14182;
            public static final int LEAGUE_TASK_EQUIP_A_BRACELET_OF_ETHEREUM = 14183;
            public static final int LEAGUE_TASK_EQUIP_A_PEARL_FISHING_ROD = 14184;
            public static final int LEAGUE_TASK_EQUIP_THE_CURSED_AMULET_OF_MAGIC = 14185;
            public static final int LEAGUE_TASK_EQUIP_XERICS_TALISMAN = 14186;
            public static final int LEAGUE_TASK_EQUIP_A_FEDORA = 14187;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_SCIMITAR = 14188;
            public static final int LEAGUE_TASK_EQUIP_A_GOD_CAPE = 14189;
            public static final int LEAGUE_TASK_EQUIP_A_DAMAGED_GOD_BOOK = 14190;
            public static final int LEAGUE_TASK_EQUIP_A_MARKSMAN_CHOMPY_HAT = 14191;
            public static final int LEAGUE_TASK_EQUIP_A_HELM_OF_NEITIZNOT = 14192;
            public static final int LEAGUE_TASK_EQUIP_AN_OGRE_FORESTER_CHOMPY_HAT = 14193;
            public static final int LEAGUE_TASK_EQUIP_A_CORRUPTED_WEAPON = 14194;
            public static final int LEAGUE_TASK_EQUIP_A_GRANITE_SHIELD = 14195;
            public static final int LEAGUE_TASK_EQUIP_A_CRYSTAL_CROWN = 14196;
            public static final int LEAGUE_TASK_EQUIP_KARAMBWAN_POISONED_SPEAR = 14197;
            public static final int LEAGUE_TASK_EQUIP_A_LAVA_BATTLESTAFF = 14198;
            public static final int LEAGUE_TASK_EQUIP_AMYS_SAW = 14199;
            public static final int LEAGUE_TASK_EQUIP_AN_IMCANDO_HAMMER = 14200;
            public static final int LEAGUE_TASK_EQUIP_A_LEAFBLADED_SWORD = 14201;
            public static final int LEAGUE_TASK_EQUIP_A_PAIR_OF_DRAGON_BOOTS_IN_WILDERNESS = 14202;
            public static final int LEAGUE_TASK_EQUIP_COMP_OGRE_BOW = 14203;
            public static final int LEAGUE_TASK_EQUIP_DRAGON_PICKAXE_IN_DESERT = 14204;
            public static final int LEAGUE_TASK_EQUIP_GRANITE_SHIELD_IN_FREMENNIK = 14205;
            public static final int LEAGUE_TASK_EQUIP_A_LEAFBLADED_WEAPON_TIRANNWN = 14206;
            public static final int LEAGUE_TASK_EQUIP_RADIANT_OATHPLATE = 14207;
            public static final int LEAGUE_TASK_EQUIP_OATHPLATE_PIECE = 14208;
            public static final int LEAGUE_TASK_EQUIP_FULL_OATHPLATE = 14209;
            public static final int LEAGUE_TASK_EQUIP_A_COMBINATION_BATTLESTAFF_OR_MYSTIC_STAFF = 14210;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGON_WEAPON = 14211;
            public static final int LEAGUE_TASK_EQUIP_A_DRAGONFIRE_SHIELD = 14212;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_ADAMANT_SET = 14213;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_BLACK_DRAGONHIDE_SET = 14214;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_BLUE_DRAGONHIDE_SET = 14215;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_BRONZE_SET = 14216;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_GOD_DRAGONHIDE_SET = 14217;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_GOD_RUNE_SET = 14218;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_GREEN_DRAGONHIDE_SET = 14219;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_RED_DRAGONHIDE_SET = 14220;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_RUNE_SET = 14221;
            public static final int LEAGUE_TASK_EQUIP_A_FULL_VESTMENT_SET = 14222;
            public static final int LEAGUE_TASK_EQUIP_A_GILDED_OR_TRIMMED_WIZARD_ITEM = 14223;
            public static final int LEAGUE_TASK_EQUIP_A_MAGIC_SHORTBOW = 14224;
            public static final int LEAGUE_TASK_EQUIP_A_MIST_BATTLESTAFF = 14225;
            public static final int LEAGUE_TASK_EQUIP_A_MITHRIL_WEAPON = 14226;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_A_MYSTIC_SET = 14227;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_BEEKEEPERS_OUTFIT = 14228;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_CAMOUFLAGE_OUTFIT = 14229;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_MIME_OUTFIT = 14230;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_VIRTUS = 14231;
            public static final int LEAGUE_TASK_EQUIP_A_PIECE_OF_ZOMBIE_OUTFIT = 14232;
            public static final int LEAGUE_TASK_EQUIP_A_RUNE_CROSSBOW = 14233;
            public static final int LEAGUE_TASK_EQUIP_A_RUNE_WEAPON = 14234;
            public static final int LEAGUE_TASK_EQUIP_A_SCYTHE_OF_VITUR = 14235;
            public static final int LEAGUE_TASK_EQUIP_A_TRIMMED_AMULET = 14236;
            public static final int LEAGUE_TASK_EQUIP_A_TWISTED_BOW = 14237;
            public static final int LEAGUE_TASK_EQUIP_A_TWOHANDED_AXE = 14238;
            public static final int ACTION_EQUIP_A_TYRAS_HELM = 14239;
            public static final int LEAGUE_TASK_EQUIP_A_WILLOW_SHIELD = 14240;
            public static final int LEAGUE_TASK_EQUIP_A_YEW_SHORTBOW = 14241;
            public static final int LEAGUE_TASK_EQUIP_AN_ABYSSAL_BLUDGEON = 14242;
            public static final int LEAGUE_TASK_EQUIP_AN_ABYSSAL_DAGGER = 14243;
            public static final int LEAGUE_TASK_EQUIP_AN_ABYSSAL_WHIP = 14244;
            public static final int LEAGUE_TASK_EQUIP_AN_ADAMANT_WEAPON = 14245;
            public static final int LEAGUE_TASK_EQUIP_AN_ELEMENTAL_BATTLESTAFF_OR_MYSTIC_STAFF = 14246;
            public static final int LEAGUE_TASK_EQUIP_AN_ELEMENTAL_STAFF = 14247;
            public static final int LEAGUE_TASK_EQUIP_AN_ETERNAL_SLAYER_RING = 14248;
            public static final int LEAGUE_TASK_EQUIP_AN_IRON_DAGGER = 14249;
            public static final int LEAGUE_TASK_EQUIP_AN_ORNAMENT_KIT_ITEM = 14250;
            public static final int LEAGUE_TASK_EQUIP_FOUR_UNIQUE_ECHO_ITEMS = 14251;
            public static final int LEAGUE_TASK_EQUIP_FULL_VIRTUS = 14252;
            public static final int LEAGUE_TASK_EQUIP_ONE_UNIQUE_ECHO_ITEM = 14253;
            public static final int LEAGUE_TASK_EQUIP_SOME_BLACK_ARMOUR = 14254;
            public static final int LEAGUE_TASK_EQUIP_SOME_RANGER_BOOTS = 14255;
            public static final int LEAGUE_TASK_EQUIP_SOME_STEEL_ARMOUR = 14256;
            public static final int LEAGUE_TASK_EQUIP_THE_FORESTRY_BASKET = 14257;
            public static final int LEAGUE_TASK_EQUIP_THE_SOULREAPER_AXE = 14258;
            public static final int LEAGUE_TASK_EQUIP_THE_TUMEKENS_SHADOW = 14259;
            public static final int LEAGUE_TASK_EQUIP_THREE_UNIQUE_ECHO_ITEMS = 14260;
            public static final int LEAGUE_TASK_EQUIP_TWO_UNIQUE_ECHO_ITEMS = 14261;
            public static final int LEAGUE_TASK_EQUIP_SULPHUR_BLADES = 14262;
            public static final int LEAGUE_TASK_EQUIP_EARTHBOUND_TECPATL = 14263;
            public static final int LEAGUE_TASK_EQUIP_HOLY_MOLEYS = 14264;
            public static final int LEAGUE_TASK_1_EASY_CLUE_SCROLL = 14265;
            public static final int LEAGUE_TASK_1_ELITE_CLUE_SCROLL = 14266;
            public static final int LEAGUE_TASK_1_HARD_CLUE_SCROLL = 14267;
            public static final int LEAGUE_TASK_1_MASTER_CLUE_SCROLL = 14268;
            public static final int LEAGUE_TASK_1_MEDIUM_CLUE_SCROLL = 14269;
            public static final int LEAGUE_TASK_100_ELITE_CLUE_SCROLLS = 14270;
            public static final int LEAGUE_TASK_100_MASTER_CLUE_SCROLLS = 14271;
            public static final int LEAGUE_TASK_25_EASY_CLUE_SCROLLS = 14272;
            public static final int LEAGUE_TASK_25_ELITE_CLUE_SCROLLS = 14273;
            public static final int LEAGUE_TASK_25_HARD_CLUE_SCROLLS = 14274;
            public static final int LEAGUE_TASK_25_MASTER_CLUE_SCROLLS = 14275;
            public static final int LEAGUE_TASK_25_MEDIUM_CLUE_SCROLLS = 14276;
            public static final int LEAGUE_TASK_75_EASY_CLUE_SCROLLS = 14277;
            public static final int LEAGUE_TASK_75_ELITE_CLUE_SCROLLS = 14278;
            public static final int LEAGUE_TASK_75_HARD_CLUE_SCROLLS = 14279;
            public static final int LEAGUE_TASK_75_MASTER_CLUE_SCROLLS = 14280;
            public static final int LEAGUE_TASK_75_MEDIUM_CLUE_SCROLLS = 14281;
            public static final int LEAGUE_TASK_BURN_SOME_COLOURED_LOGS = 14282;
            public static final int LEAGUE_TASK_CATCH_200_IMPLINGS_IN_PUROPURO = 14283;
            public static final int LEAGUE_TASK_CATCH_50_IMPLINGS_IN_PUROPURO = 14284;
            public static final int LEAGUE_TASK_CHOP_SOME_RISING_ROOTS = 14285;
            public static final int LEAGUE_TASK_COMPLETE_THE_FLOWERING_BUSH_EVENT = 14286;
            public static final int LEAGUE_TASK_COMPLETE_THE_PHEASANT_FORESTRY_EVENT = 14287;
            public static final int LEAGUE_TASK_COMPLETE_THE_RITUAL_FORESTRY_EVENT = 14288;
            public static final int LEAGUE_TASK_COMPLETE_THE_STRUGGLING_SAPLING_EVENT = 14289;
            public static final int LEAGUE_TASK_OPEN_A_BRONZE_CHEST = 14290;
            public static final int LEAGUE_TASK_COMPLETE_AN_EASY_TEMPLE_TREK = 14291;
            public static final int LEAGUE_TASK_PICK_A_WINTER_SQIRK = 14292;
            public static final int LEAGUE_TASK_ROOM_1_OF_PYRAMID_PLUNDER = 14293;
            public static final int LEAGUE_TASK_ROOM_2_OF_PYRAMID_PLUNDER = 14294;
            public static final int LEAGUE_TASK_ROOM_3_OF_PYRAMID_PLUNDER = 14295;
            public static final int LEAGUE_TASK_COMPLETE_A_GAME_OF_NOVICE_PEST_CONTROL = 14296;
            public static final int LEAGUE_TASK_FLOOR_5_OF_THE_HALLOWED_SEPULCHRE = 14297;
            public static final int LEAGUE_TASK_ROOM_8_OF_PYRAMID_PLUNDER_75_TIMES = 14298;
            public static final int LEAGUE_TASK_GET_1000_TARGET_POINTS = 14299;
            public static final int LEAGUE_TASK_REACH_LEVEL_5_IN_ANY_BARBARIAN_ASSAULT_ROLE = 14300;
            public static final int LEAGUE_TASK_COMPLETE_THE_HALLOWED_SEPULCHRE_IN_630 = 14301;
            public static final int LEAGUE_TASK_COMPLETE_THE_CORRUPTED_GAUNTLET_50_TIMES = 14302;
            public static final int LEAGUE_TASK_COMPLETE_THE_CORRUPTED_GAUNTLET_100_TIMES = 14303;
            public static final int LEAGUE_TASK_TURN_IN_50_SUMMER_SQIRKJUICES_TO_OSMAN = 14304;
            public static final int LEAGUE_TASK_OBTAIN_THE_FISH_BARREL = 14305;
            public static final int LEAGUE_TASK_1_CHAMBERS_OF_XERIC_CHALLENGE_MODE = 14306;
            public static final int LEAGUE_TASK_KEEP_THE_VETERAN_VOID_KNIGHT_ABOVE_150_HITPOINTS = 14307;
            public static final int LEAGUE_TASK_150_ELITE_CLUE_SCROLLS = 14308;
            public static final int LEAGUE_TASK_150_MASTER_CLUE_SCROLLS = 14309;
            public static final int LEAGUE_TASK_READ_A_CLUELESS_SCROLL = 14310;
            public static final int LEAGUE_TASK_TRANSFORM_INTO_URI = 14311;
            public static final int LEAGUE_TASK_USE_THE_EXPLORE_EMOTE = 14312;
            public static final int LEAGUE_TASK_FILL_EVERY_BEGINNER_CLUE_COLLECTION_LOG_SLOT = 14313;
            public static final int LEAGUE_TASK_OBTAIN_EVERY_HALLOWED_TOOL = 14314;
            public static final int LEAGUE_TASK_OPEN_A_GOLD_CHEST = 14315;
            public static final int LEAGUE_TASK_FLOOR_4_OF_THE_HALLOWED_SEPULCHRE = 14316;
            public static final int LEAGUE_TASK_GIANTS_FOUNDRY_150_QUALITY_SWORD = 14317;
            public static final int LEAGUE_TASK_1_CHAMBERS_OF_XERIC = 14318;
            public static final int LEAGUE_TASK_25_CHAMBERS_OF_XERIC = 14319;
            public static final int LEAGUE_TASK_GIANTS_FOUNDRY_25_HANDINS = 14320;
            public static final int LEAGUE_TASK_GIANTS_FOUNDRY_100_QUALITY_SWORD = 14321;
            public static final int LEAGUE_TASK_GIANTS_FOUNDRY_50_HANDINS = 14322;
            public static final int LEAGUE_TASK_PICK_A_SUMMER_SQIRK = 14323;
            public static final int LEAGUE_TASK_ROOM_7_OF_PYRAMID_PLUNDER = 14324;
            public static final int LEAGUE_TASK_ROOM_8_OF_PYRAMID_PLUNDER = 14325;
            public static final int LEAGUE_TASK_MINE_A_RUNITE_ORE_FRAGMENT = 14326;
            public static final int LEAGUE_TASK_OBTAIN_A_PUFFERFISH_FROM_DRIFT_NET_FISHING = 14327;
            public static final int LEAGUE_TASK_ROOM_8_OF_PYRAMID_PLUNDER_25_TIMES = 14328;
            public static final int LEAGUE_TASK_TURN_IN_25_AUTUMN_SQIRKJUICES_TO_OSMAN = 14329;
            public static final int LEAGUE_TASK_GIANTS_FOUNDRY_125_QUALITY_SWORD = 14330;
            public static final int LEAGUE_TASK_50_CHAMBERS_OF_XERIC = 14331;
            public static final int LEAGUE_TASK_PURCHASE_AN_ECTOPLASMATOR = 14332;
            public static final int LEAGUE_TASK_GET_750_TARGET_POINTS = 14333;
            public static final int LEAGUE_TASK_REACH_LEVEL_5_IN_EVERY_BARBARIAN_ASSAULT_ROLE = 14334;
            public static final int LEAGUE_TASK_RECEIVE_30_AGILITY_ARENA_TICKETS_WITH_NO_MISTAKES = 14335;
            public static final int LEAGUE_TASK_RECEIVE_AN_AGILITY_ARENA_TICKET = 14336;
            public static final int LEAGUE_TASK_TURN_IN_A_WINTER_SQIRKJUICE_TO_OSMAN = 14337;
            public static final int LEAGUE_TASK_ROOM_8_OF_PYRAMID_PLUNDER_150_TIMES = 14338;
            public static final int LEAGUE_TASK_SACRIFICE_A_FIRE_CAPE_TO_ACCESS_THE_INFERNO = 14339;
            public static final int LEAGUE_TASK_SURVIVE_A_HIT_FROM_TZTOKJAD_WITHOUT_PRAYER = 14340;
            public static final int LEAGUE_TASK_TRADE_100_AGILITY_ARENA_TICKETS_FOR_XP = 14341;
            public static final int LEAGUE_TASK_WIN_A_GAME_OF_SOUL_WARS = 14342;
            public static final int LEAGUE_TASK_BUY_A_SNAPDRAGON_FROM_PIRATE_JACKIE_THE_FRUIT = 14343;
            public static final int LEAGUE_TASK_BUY_AN_ASH_COVERED_TOME = 14344;
            public static final int LEAGUE_TASK_COMPLETE_A_VOLCANIC_MINE_GAME = 14345;
            public static final int LEAGUE_TASK_COMPLETE_THE_CORRUPTED_GAUNTLET_150_TIMES = 14346;
            public static final int LEAGUE_TASK_COMPLETE_THE_HALLOWED_SEPULCHRE_IN_800 = 14347;
            public static final int LEAGUE_TASK_COMPLETE_THE_INFERNO_10_TIMES = 14348;
            public static final int LEAGUE_TASK_COMPLETE_THE_INFERNO_15_TIMES = 14349;
            public static final int LEAGUE_TASK_COMPLETE_THE_INFERNO_5_TIMES = 14350;
            public static final int LEAGUE_TASK_COMPLETE_TZHAARKETRAKS_FIFTH_CHALLENGE = 14351;
            public static final int LEAGUE_TASK_COMPLETE_TZHAARKETRAKS_FIRST_CHALLENGE = 14352;
            public static final int LEAGUE_TASK_COMPLETE_TZHAARKETRAKS_FOURTH_CHALLENGE = 14353;
            public static final int LEAGUE_TASK_COMPLETE_TZHAARKETRAKS_SECOND_CHALLENGE = 14354;
            public static final int LEAGUE_TASK_COMPLETE_TZHAARKETRAKS_SIXTH_CHALLENGE = 14355;
            public static final int LEAGUE_TASK_COMPLETE_TZHAARKETRAKS_SPECIAL_CHALLENGE = 14356;
            public static final int LEAGUE_TASK_COMPLETE_TZHAARKETRAKS_THIRD_CHALLENGE = 14357;
            public static final int LEAGUE_TASK_FIND_A_GOUT_TUBER = 14358;
            public static final int LEAGUE_TASK_OPEN_A_BLACK_CHEST = 14359;
            public static final int LEAGUE_TASK_PURCHASE_A_REWARD_FROM_THE_HALLOWED_SEPULCHRE = 14360;
            public static final int LEAGUE_TASK_ACHIEVE_100_SHADES_OF_MORTTON_SANCTITY = 14361;
            public static final int LEAGUE_TASK_GIANTS_FOUNDRY_10_HANDINS = 14362;
            public static final int LEAGUE_TASK_GIANTS_FOUNDRY_50_QUALITY_SWORD = 14363;
            public static final int LEAGUE_TASK_COMPLETE_A_HARD_TEMPLE_TREK = 14364;
            public static final int LEAGUE_TASK_OBTAIN_THE_BIG_HARPOONFISH = 14365;
            public static final int LEAGUE_TASK_COMPLETE_A_GAME_OF_VETERAN_PEST_CONTROL = 14366;
            public static final int LEAGUE_TASK_COMPLETE_A_MEDIUM_TEMPLE_TREK = 14367;
            public static final int LEAGUE_TASK_FLOOR_1_OF_THE_HALLOWED_SEPULCHRE = 14368;
            public static final int LEAGUE_TASK_PICK_A_AUTUMN_SQIRK = 14369;
            public static final int LEAGUE_TASK_FLOOR_2_OF_THE_HALLOWED_SEPULCHRE = 14370;
            public static final int LEAGUE_TASK_FLOOR_3_OF_THE_HALLOWED_SEPULCHRE = 14371;
            public static final int LEAGUE_TASK_PICK_A_SPRING_SQIRK = 14372;
            public static final int LEAGUE_TASK_COMPLETE_THE_CORRUPTED_GAUNTLET = 14373;
            public static final int LEAGUE_TASK_ROOM_4_OF_PYRAMID_PLUNDER = 14374;
            public static final int LEAGUE_TASK_ROOM_5_OF_PYRAMID_PLUNDER = 14375;
            public static final int LEAGUE_TASK_100_CHAMBERS_OF_XERIC = 14376;
            public static final int LEAGUE_TASK_ROOM_6_OF_PYRAMID_PLUNDER = 14377;
            public static final int LEAGUE_TASK_COMPLETE_THE_GAUNTLET = 14378;
            public static final int LEAGUE_TASK_25_CHAMBERS_OF_XERIC_CHALLENGE_MODE = 14379;
            public static final int LEAGUE_TASK_150_HARD_CLUE_SCROLLS = 14380;
            public static final int LEAGUE_TASK_GAIN_35_UNIQUE_ITEMS_FROM_BEGINNER_CLUES = 14381;
            public static final int LEAGUE_TASK_COMPLETE_50_MAHOGANY_HOMES_CONTRACTS = 14382;
            public static final int LEAGUE_TASK_COMPLETE_A_MAHOGANY_HOMES_EXPERT_CONTRACT = 14383;
            public static final int LEAGUE_TASK_FILL_10_BEGINNER_CLUE_COLLECTION_LOG_SLOTS = 14384;
            public static final int LEAGUE_TASK_TURN_IN_10_SPRING_SQIRKJUICES_TO_OSMAN = 14385;
            public static final int LEAGUE_TASK_PURCHASE_GRICOLLERS_CAN = 14386;
            public static final int LEAGUE_TASK_150_EASY_CLUE_SCROLLS = 14387;
            public static final int LEAGUE_TASK_150_MEDIUM_CLUE_SCROLLS = 14388;
            public static final int LEAGUE_TASK_GAIN_10_UNIQUE_ITEMS_FROM_BEGINNER_CLUES = 14389;
            public static final int LEAGUE_TASK_PURCHASE_A_SEED_BOX = 14390;
            public static final int LEAGUE_TASK_GAIN_A_UNIQUE_ITEM_FROM_A_BEGINNER_CLUE = 14391;
            public static final int LEAGUE_TASK_EAT_SOME_PURPLE_SWEETS = 14392;
            public static final int LEAGUE_TASK_COMPLETE_10_MAHOGANY_HOMES_CONTRACTS = 14393;
            public static final int LEAGUE_TASK_COMPLETE_25_MAHOGANY_HOMES_CONTRACTS = 14394;
            public static final int LEAGUE_TASK_COMPLETE_A_MAHOGANY_HOMES_ADEPT_CONTRACT = 14395;
            public static final int LEAGUE_TASK_COMPLETE_A_MAHOGANY_HOMES_BEGINNER_CONTRACT = 14396;
            public static final int LEAGUE_TASK_COMPLETE_A_MAHOGANY_HOMES_NOVICE_CONTRACT = 14397;
            public static final int LEAGUE_TASK_FILL_5_BEGINNER_CLUE_COLLECTION_LOG_SLOTS = 14398;
            public static final int LEAGUE_TASK_GET_250_TARGET_POINTS = 14399;
            public static final int LEAGUE_TASK_UNLOCK_FREE_USE_OF_THE_BLAST_FURNACE = 14400;
            public static final int LEAGUE_TASK_WIN_A_GAME_OF_CASTLE_WARS = 14401;
            public static final int LEAGUE_TASK_OBTAIN_THE_PLANK_SACK = 14402;
            public static final int LEAGUE_TASK_OPEN_AN_ORNATE_LOCKBOX = 14403;
            public static final int LEAGUE_TASK_COMPLETE_A_GAME_OF_INTERMEDIATE_PEST_CONTROL = 14404;
            public static final int LEAGUE_TASK_COMPLETE_A_FISHING_TRAWLER_GAME = 14405;
            public static final int LEAGUE_TASK_FILL_10_ELITE_CLUE_COLLECTION_LOG_SLOTS = 14406;
            public static final int LEAGUE_TASK_FILL_15_HARD_CLUE_COLLECTION_LOG_SLOTS = 14407;
            public static final int LEAGUE_TASK_FILL_20_EASY_CLUE_COLLECTION_LOG_SLOTS = 14408;
            public static final int LEAGUE_TASK_FILL_20_MEDIUM_CLUE_COLLECTION_LOG_SLOTS = 14409;
            public static final int LEAGUE_TASK_FILL_25_ELITE_CLUE_COLLECTION_LOG_SLOTS = 14410;
            public static final int LEAGUE_TASK_FILL_25_MASTER_CLUE_COLLECTION_LOG_SLOTS = 14411;
            public static final int LEAGUE_TASK_FILL_3_ELITE_CLUE_COLLECTION_LOG_SLOTS = 14412;
            public static final int LEAGUE_TASK_FILL_3_HARD_CLUE_COLLECTION_LOG_SLOTS = 14413;
            public static final int LEAGUE_TASK_FILL_30_HARD_CLUE_COLLECTION_LOG_SLOTS = 14414;
            public static final int LEAGUE_TASK_FILL_40_MEDIUM_CLUE_COLLECTION_LOG_SLOTS = 14415;
            public static final int LEAGUE_TASK_FILL_5_EASY_CLUE_COLLECTION_LOG_SLOTS = 14416;
            public static final int LEAGUE_TASK_FILL_5_MASTER_CLUE_COLLECTION_LOG_SLOTS = 14417;
            public static final int LEAGUE_TASK_FILL_5_MEDIUM_CLUE_COLLECTION_LOG_SLOTS = 14418;
            public static final int LEAGUE_TASK_FILL_50_EASY_CLUE_COLLECTION_LOG_SLOTS = 14419;
            public static final int LEAGUE_TASK_GAIN_10_UNIQUE_ITEMS_FROM_EASY_CLUES = 14420;
            public static final int LEAGUE_TASK_GAIN_10_UNIQUE_ITEMS_FROM_ELITE_CLUES = 14421;
            public static final int LEAGUE_TASK_GAIN_10_UNIQUE_ITEMS_FROM_MASTER_CLUES = 14422;
            public static final int LEAGUE_TASK_GAIN_10_UNIQUE_ITEMS_FROM_MEDIUM_CLUES = 14423;
            public static final int LEAGUE_TASK_GAIN_20_UNIQUE_ITEMS_FROM_HARD_CLUES = 14424;
            public static final int LEAGUE_TASK_GAIN_25_UNIQUE_ITEMS_FROM_ELITE_CLUES = 14425;
            public static final int LEAGUE_TASK_GAIN_25_UNIQUE_ITEMS_FROM_MASTER_CLUES = 14426;
            public static final int LEAGUE_TASK_GAIN_25_UNIQUE_ITEMS_FROM_MEDIUM_CLUES = 14427;
            public static final int LEAGUE_TASK_GAIN_35_UNIQUE_ITEMS_FROM_EASY_CLUES = 14428;
            public static final int LEAGUE_TASK_GAIN_5_UNIQUE_ITEMS_FROM_HARD_CLUES = 14429;
            public static final int LEAGUE_TASK_GAIN_50_UNIQUE_ITEMS_FROM_HARD_CLUES = 14430;
            public static final int LEAGUE_TASK_GAIN_A_UNIQUE_ITEM_FROM_A_HARD_CLUE = 14431;
            public static final int LEAGUE_TASK_GAIN_A_UNIQUE_ITEM_FROM_A_MASTER_CLUE = 14432;
            public static final int LEAGUE_TASK_GAIN_A_UNIQUE_ITEM_FROM_A_MEDIUM_CLUE = 14433;
            public static final int LEAGUE_TASK_GAIN_A_UNIQUE_ITEM_FROM_AN_EASY_CLUE = 14434;
            public static final int LEAGUE_TASK_GAIN_A_UNIQUE_ITEM_FROM_AN_ELITE_CLUE = 14435;
            public static final int LEAGUE_TASK_GIVE_AN_ENTLING_A_HAIRCUT = 14436;
            public static final int LEAGUE_TASK_HAVE_A_LEPRECHAUN_SEND_SOMETHING_TO_THE_BANK = 14437;
            public static final int LEAGUE_TASK_MINE_A_SHOOTING_STAR = 14438;
            public static final int LEAGUE_TASK_MINE_A_SHOOTING_STAR_LEVEL_8 = 14439;
            public static final int LEAGUE_TASK_BUY_SOMETHING_FROM_TRADER_CREWMEMBERS = 14440;
            public static final int LEAGUE_TASK_ATTACK_A_DUMMY = 14441;
            public static final int LEAGUE_TASK_BUTTER_A_POTATO = 14442;
            public static final int LEAGUE_TASK_CHURN_SOME_BUTTER = 14443;
            public static final int LEAGUE_TASK_COMPLETE_THE_EVIL_BOB_RANDOM_EVENT = 14444;
            public static final int LEAGUE_TASK_COMPLETE_THE_LEAGUES_TUTORIAL = 14445;
            public static final int LEAGUE_TASK_COMPLETE_THE_MAZE_RANDOM_EVENT = 14446;
            public static final int LEAGUE_TASK_COMPLETE_THE_PILLORY_RANDOM_EVENT = 14447;
            public static final int LEAGUE_TASK_COMPLETE_THE_PINBALL_RANDOM_EVENT = 14448;
            public static final int LEAGUE_TASK_COMPLETE_THE_POSTIE_PETE_RANDOM_EVENT = 14449;
            public static final int LEAGUE_TASK_COMPLETE_THE_PRISON_PETE_RANDOM_EVENT = 14450;
            public static final int LEAGUE_TASK_COMPLETE_THE_SURPRISE_EXAM_RANDOM_EVENT = 14451;
            public static final int LEAGUE_TASK_DYE_A_CAPE_PURPLE = 14452;
            public static final int LEAGUE_TASK_EAT_A_RABBIT = 14453;
            public static final int LEAGUE_TASK_EAT_AN_ONION = 14454;
            public static final int LEAGUE_TASK_TRAVEL_TO_MOLCH_ISLAND = 14455;
            public static final int LEAGUE_TASK_VISIT_HARMONY_ISLAND = 14456;
            public static final int LEAGUE_TASK_HAVE_A_CONVERSATION_WITH_A_CAT = 14457;
            public static final int LEAGUE_TASK_MINE_SOME_ESSENCE = 14458;
            public static final int LEAGUE_TASK_USE_THE_MINE_CART_TRANSPORTATION_SYSTEM = 14459;
            public static final int LEAGUE_TASK_PROVE_YOURSELF_TO_PONTS = 14460;
            public static final int LEAGUE_TASK_SCATTER_SOME_ASHES = 14461;
            public static final int LEAGUE_TASK_VISIT_MOS_LEHARMLESS = 14462;
            public static final int LEAGUE_TASK_BANK_AT_LANDS_END = 14463;
            public static final int LEAGUE_TASK_USE_A_HAT_STAND = 14464;
            public static final int LEAGUE_TASK_USE_A_HERRING_ON_A_TREE = 14465;
            public static final int LEAGUE_TASK_CRY_IN_A_WHEAT_FIELD = 14466;
            public static final int LEAGUE_TASK_DANCE_IN_A_GRAVEYARD = 14467;
            public static final int LEAGUE_TASK_DYE_A_CAPE_ORANGE = 14468;
            public static final int LEAGUE_TASK_EAT_A_BANANA = 14469;
            public static final int LEAGUE_TASK_TALK_TO_THE_MYSTERIOUS_OLD_MAN_IN_FALADOR = 14470;
            public static final int LEAGUE_TASK_VISIT_PORT_PHASMATYS = 14471;
            public static final int LEAGUE_TASK_GET_STUNG_BY_BEES = 14472;
            public static final int LEAGUE_TASK_USE_THE_FALADOR_PARTY_ROOM = 14473;
            public static final int LEAGUE_TASK_OBTAIN_JACKS_LEGENDARY_ROD = 14474;
            public static final int LEAGUE_TASK_PICK_WHEAT_IN_CATHERBY = 14475;
            public static final int LEAGUE_TASK_BALANCE_5_BARRELS_ON_YOUR_HEAD = 14476;
            public static final int LEAGUE_TASK_RECEIVE_A_GIFT_FROM_ARNOLD = 14477;
            public static final int LEAGUE_TASK_ENTER_PURO_PURO_FROM_GIELINOR = 14478;
            public static final int LEAGUE_TASK_VISIT_THE_TREE_GNOME_STRONGHOLD = 14479;
            public static final int LEAGUE_TASK_BECOME_A_MUSHROOM = 14480;
            public static final int LEAGUE_TASK_PICK_UP_SNAPEGRASS = 14481;
            public static final int LEAGUE_TASK_VISIT_THE_TREE_GNOME_VILLAGE = 14482;
            public static final int LEAGUE_TASK_ENTER_HAUNTED_MINE = 14483;
            public static final int LEAGUE_TASK_OPEN_A_LOOTING_BAG = 14484;
            public static final int LEAGUE_TASK_USE_THE_BANK_ON_JATIZSO = 14485;
            public static final int LEAGUE_TASK_USE_THE_BANK_ON_LUNAR_ISLE = 14486;
            public static final int LEAGUE_TASK_GIVE_OLI_SOME_STEW = 14487;
            public static final int LEAGUE_TASK_BANK_AT_MOUNT_QUIDAMORTEM = 14488;
            public static final int LEAGUE_TASK_MILK_A_BUFFALO = 14489;
            public static final int LEAGUE_TASK_DRINK_A_LIZARDKICKER = 14490;
            public static final int LEAGUE_TASK_EAT_A_FIELD_RATION = 14491;
            public static final int LEAGUE_TASK_PET_A_CAIQUE = 14492;
            public static final int LEAGUE_TASK_PET_RENU = 14493;
            public static final int LEAGUE_TASK_USE_THE_BANK_AT_THE_MAGE_ARENA = 14494;
            public static final int LEAGUE_TASK_PET_XOLO_IN_CIVITAS = 14495;
            public static final int LEAGUE_TASK_PICK_UP_A_CABBAGE_IN_THE_CITHAREDE_ABBEY = 14496;
            public static final int LEAGUE_TASK_ENTER_THE_TAVERLEY_DUNGEON = 14497;
            public static final int LEAGUE_TASK_TALK_TO_ILFEEN_IN_TIRANNWN = 14498;
            public static final int LEAGUE_TASK_USE_THE_BANK_ON_NEITIZNOT = 14499;
            public static final int LEAGUE_TASK_TAKE_A_CARPET_RIDE_FROM_POLLNIVNEACH_TO_SOPHANEM = 14500;
            public static final int LEAGUE_TASK_USE_BROKEN_HANDZS_TELESCOPE = 14501;
            public static final int LEAGUE_TASK_DEPOSIT_AN_ITEM_USING_PEER_THE_SEER = 14502;
            public static final int LEAGUE_TASK_PICK_SOME_SWEETCORN_FROM_A_FIELD = 14503;
            public static final int LEAGUE_TASK_SALUTE_NEXT_TO_A_STATUE_OF_QUOATLOS = 14504;
            public static final int LEAGUE_TASK_BRIMSTAIL_RUNE_ESSENCE_MINE_TELEPORT = 14505;
            public static final int LEAGUE_TASK_SCATTER_SOME_ASHES_IN_YAMAS_LAIR = 14506;
            public static final int LEAGUE_TASK_SHEAR_AN_ALPACA = 14507;
            public static final int LEAGUE_TASK_USE_THE_BANK_IN_NARDAH = 14508;
            public static final int LEAGUE_TASK_SIT_NEAR_A_STOLEN_CABBAGE = 14509;
            public static final int LEAGUE_TASK_STEP_ONTO_AN_ENT_TRAIL = 14510;
            public static final int LEAGUE_TASK_TALK_TO_A_GLADIATOR = 14511;
            public static final int LEAGUE_TASK_TRAVEL_IN_ACHILKAS_BOAT = 14512;
            public static final int LEAGUE_TASK_TRAVEL_TO_ALDARIN_VIA_FAIRY_RING = 14513;
            public static final int LEAGUE_TASK_TRAVEL_USING_THE_QUETZAL_TRANSPORT_SYSTEM = 14514;
            public static final int LEAGUE_TASK_USE_THE_BANK_IN_LLETYA = 14515;
            public static final int LEAGUE_TASK_CHARTER_A_SHIP_FROM_PRIFDDINAS_TO_PORT_TYRAS = 14516;
            public static final int LEAGUE_TASK_INSPECT_A_GREEN_FLAME = 14517;
            public static final int LEAGUE_TASK_ADMIRE_SOME_BEAUTIFUL_SCENERY = 14518;
            public static final int LEAGUE_TASK_BOW_NEAR_A_QUETZAL = 14519;
            public static final int LEAGUE_TASK_VISIT_FEROX_ENCLAVE = 14520;
            public static final int LEAGUE_TASK_ENTER_THE_WILDERNESS_GOD_WARS_DUNGEON = 14521;
            public static final int LEAGUE_TASK_ENTER_THE_WILDERNESS_RESOURCE_AREA = 14522;
            public static final int LEAGUE_TASK_UNLOCK_ALL_CATACOMBS_ENTRANCES = 14523;
            public static final int LEAGUE_TASK_ENTER_LEVEL_3_OF_THE_CHASM_OF_FIRE = 14524;
            public static final int LEAGUE_TASK_PAY_ATLAS_TO_RETRAIN_YOU = 14525;
            public static final int LEAGUE_TASK_CUT_A_CACTUS_IN_THE_KHARIDIAN_DESERT = 14526;
            public static final int LEAGUE_TASK_CHARTER_A_SHIP_FROM_SUNSET_COAST_TO_CIVITAS = 14527;
            public static final int LEAGUE_TASK_CRY_NEAR_A_CHILD = 14528;
            public static final int LEAGUE_TASK_DANCE_NEAR_A_BARD = 14529;
            public static final int LEAGUE_TASK_DRINK_A_CUP_OF_TEA_IN_AUBURN_VALLEY = 14530;
            public static final int LEAGUE_TASK_DRINK_FROM_A_BIRD_BATH = 14531;
            public static final int LEAGUE_TASK_DRINK_A_POISON_CHALICE = 14532;
            public static final int LEAGUE_TASK_DRINK_SOME_MOONLITE = 14533;
            public static final int LEAGUE_TASK_EXIT_CIVITAS_VIA_THE_SECRET_PASSAGE = 14534;
            public static final int LEAGUE_TASK_FILL_A_BUCKET_WITH_SAND_AT_THE_SUNSET_COAST = 14535;
            public static final int LEAGUE_TASK_DRINK_SOME_OF_ALIS_TEA = 14536;
            public static final int LEAGUE_TASK_ENTER_THE_KALPHITE_LAIR = 14537;
            public static final int LEAGUE_TASK_FILL_SOMETHING_UP_FROM_A_WATER_PUMP = 14538;
            public static final int LEAGUE_TASK_ACTIVATE_3_SET_EFFECTS = 14539;
            public static final int LEAGUE_TASK_LOOT_A_DRAGONSTONE_FROM_THE_ROGUES_CASTLE = 14540;
            public static final int LEAGUE_TASK_OPEN_A_CHEST_WITH_THE_MOON_KEY = 14541;
            public static final int LEAGUE_TASK_OPEN_THE_VARLAMORE_MOON_CHEST = 14542;
            public static final int LEAGUE_TASK_TRADE_WITH_BARDUR = 14543;
            public static final int LEAGUE_TASK_COLLECT_MISCELLANIA_RESOURCES_AT_FULL_APPROVAL = 14544;
            public static final int LEAGUE_TASK_UNLOCK_PERMANENT_BOAT_TRAVEL_WITH_ANDRAS = 14545;
            public static final int LEAGUE_TASK_ENTER_THE_MYTHS_GUILD = 14546;
            public static final int LEAGUE_TASK_FEED_LONGRAMBLE = 14547;
            public static final int LEAGUE_TASK_FULLY_UNLOCK_THE_MYCELIUM_TRANSPORTATION_SYSTEM = 14548;
            public static final int LEAGUE_TASK_GET_A_CHAIR_TO_FOLLOW_YOU = 14549;
            public static final int LEAGUE_TASK_GET_SENT_TO_PORT_SARIM_JAIL = 14550;
            public static final int LEAGUE_TASK_GO_THROUGH_THE_AL_KHARID_GATE = 14551;
            public static final int LEAGUE_TASK_HAVE_ELSIE_TELL_YOU_A_STORY = 14552;
            public static final int LEAGUE_TASK_HAVE_NED_MAKE_YOU_SOME_ROPE = 14553;
            public static final int LEAGUE_TASK_HAVE_THE_WISE_OLD_MAN_GET_ANGRY_WITH_YOU = 14554;
            public static final int LEAGUE_TASK_INSULT_AGGIE_THE_WITCH = 14555;
            public static final int LEAGUE_TASK_MAKE_A_PIE_FOR_ROMILY = 14556;
            public static final int LEAGUE_TASK_MILK_A_COW = 14557;
            public static final int LEAGUE_TASK_PAN_FOR_AN_UNCUT_JADE = 14558;
            public static final int LEAGUE_TASK_PAY_SANIBOCH_FOR_PERMANENT_ACCESS = 14559;
            public static final int LEAGUE_TASK_PET_A_STRAY_DOG_IN_VARROCK = 14560;
            public static final int LEAGUE_TASK_PET_THE_MUSEUM_CAMP_DOG = 14561;
            public static final int LEAGUE_TASK_PICK_A_CABBAGE_IN_VARROCK = 14562;
            public static final int LEAGUE_TASK_PICK_A_PINEAPPLE_ON_KARAMJA = 14563;
            public static final int LEAGUE_TASK_PRAY_AT_AN_ALTAR_IN_LUMBRIDGE = 14564;
            public static final int LEAGUE_TASK_PRAY_AT_THE_SOUTHERN_CHAOS_TEMPLE = 14565;
            public static final int LEAGUE_TASK_PRAY_AT_THE_WESTERN_CHAOS_TEMPLE = 14566;
            public static final int LEAGUE_TASK_PROVIDE_TERRY_A_BUCKLE = 14567;
            public static final int LEAGUE_TASK_PURCHASE_A_HALO_FROM_CASTLEWARS = 14568;
            public static final int LEAGUE_TASK_PURCHASE_AN_ONYX_IN_MOR_UL_REK = 14569;
            public static final int LEAGUE_TASK_REACH_THE_LIGHTHOUSE_VIA_STEPPING_STONES = 14570;
            public static final int LEAGUE_TASK_SCORE_A_GOAL_IN_GNOMEBALL = 14571;
            public static final int LEAGUE_TASK_ACTIVATE_ALL_STATUES_OF_ATES = 14572;
            public static final int LEAGUE_TASK_SLASH_A_WEB_IN_VARROCK_SEWERS = 14573;
            public static final int LEAGUE_TASK_SLEEP_IN_PARAMAYA_INN = 14574;
            public static final int LEAGUE_TASK_BUILD_ALL_QUETZAL_LANDING_SITES = 14575;
            public static final int LEAGUE_TASK_STROKE_YOUR_CAT = 14576;
            public static final int LEAGUE_TASK_TAKE_A_CANOE_TO_CHAMPIONS_GUILD = 14577;
            public static final int LEAGUE_TASK_TALK_TO_HANS = 14578;
            public static final int LEAGUE_TASK_TAN_A_COW_HIDE_IN_THE_KHARIDIAN_DESERT = 14579;
            public static final int LEAGUE_TASK_TASTE_A_DORGESHUUN_DELICACY = 14580;
            public static final int LEAGUE_TASK_TRADE_IN_A_CAT_FOR_DEATH_RUNES = 14581;
            public static final int LEAGUE_TASK_TRAVEL_TO_FOSSIL_ISLAND = 14582;
            public static final int LEAGUE_TASK_UPSET_THE_HOMELESS = 14583;
            public static final int LEAGUE_TASK_USE_A_DIGSITE_PENDANT_TO_TELEPORT_TO_FOSSIL_ISLAND = 14584;
            public static final int LEAGUE_TASK_USE_A_FAIRY_RING = 14585;
            public static final int LEAGUE_TASK_USE_A_LEVER_TO_TELEPORT_TO_EDGEVILLE = 14586;
            public static final int LEAGUE_TASK_USE_THE_NORTHERN_STAIRCASE_IN_LUMBRIDGE = 14587;
            public static final int LEAGUE_TASK_HAVE_THE_TAXIDERMIST_STUFF_SOMETHING_FOR_YOU = 14588;
            public static final int LEAGUE_TASK_BUILD_A_BANK_ON_FOSSIL_ISLAND = 14589;
            public static final int LEAGUE_TASK_BUY_A_CANDLE_IN_LUMBRIDGE = 14590;
            public static final int LEAGUE_TASK_CHARM_GERTRUDE = 14591;
            public static final int LEAGUE_TASK_COMPLETE_A_TASK_FOR_THE_WISE_OLD_MAN = 14592;
            public static final int LEAGUE_TASK_COMPLETE_THE_NATURAL_HISTORY_QUIZ = 14593;
            public static final int LEAGUE_TASK_DRINK_A_BEER_IN_THE_LONGHALL = 14594;
            public static final int LEAGUE_TASK_DRINK_FROM_THE_TEARS_OF_GUTHIX = 14595;
            public static final int LEAGUE_TASK_EAT_A_MAGIC_CABBAGE = 14596;
            public static final int LEAGUE_TASK_ENTER_DRAYNOR_MANOR = 14597;
            public static final int LEAGUE_TASK_ENTER_SOPHANEM = 14598;
            public static final int LEAGUE_TASK_ENTER_THE_BRIMHAVEN_DUNGEON = 14599;
            public static final int LEAGUE_TASK_ENTER_THE_TAI_BWO_WANNAI_HARDWOOD_GROVE = 14600;
            public static final int LEAGUE_TASK_ENTER_ZANARIS = 14601;
            public static final int LEAGUE_TASK_FEED_A_DRAYNOR_GUARD_SOME_STEW = 14602;
            public static final int LEAGUE_TASK_PET_FRANK = 14603;
            public static final int LEAGUE_TASK_BURY_OURG_BONE = 14604;
            public static final int LEAGUE_TASK_FILL_A_CRATE_WITH_BANANAS = 14605;
            public static final int LEAGUE_TASK_FLEX_IN_FRONT_OF_THE_GUNS = 14606;
            public static final int LEAGUE_TASK_OPEN_THE_ENHANCED_CRYSTAL_CHEST = 14607;
            public static final int LEAGUE_TASK_CRAFT_AN_ETERNAL_TELEPORT_CRYSTAL = 14608;
            public static final int LEAGUE_TASK_FIND_EVERY_MEMORIAM_CRYSTAL = 14609;
            public static final int LEAGUE_TASK_MAX_OUT_10_FRAGMENTS = 14610;
            public static final int LEAGUE_TASK_OBTAIN_A_KEBAB_FROM_THE_CERTER_BROTHERS = 14611;
            public static final int LEAGUE_TASK_ACTIVATE_2_SET_EFFECTS = 14612;
            public static final int LEAGUE_TASK_MAX_OUT_ALL_FRAGMENTS = 14613;
            public static final int LEAGUE_TASK_HAND_IN_CRYSTAL_SEED_FOR_ACORN = 14614;
            public static final int LEAGUE_TASK_INVESTIGATE_MEZTLAN = 14615;
            public static final int LEAGUE_TASK_OBTAIN_A_CAKE_FROM_A_NEST = 14616;
            public static final int LEAGUE_TASK_JUMP_ON_THE_STEPPING_STONES_IN_YAMAS_LAIR_666_TIMES = 14617;
            public static final int LEAGUE_TASK_ACTIVATE_THE_STATUE_OF_ATES = 14618;
            public static final int LEAGUE_TASK_HEADBANG_WITH_KETSAL_KUK = 14619;
            public static final int LEAGUE_TASK_BURY_SOME_WYRM_BONES_NEAR_A_WYRM_SKELETON = 14620;
            public static final int LEAGUE_TASK_TALK_TO_THE_VOICE_OF_YAMA = 14621;
            public static final int LEAGUE_TASK_ENTER_A_DARK_CAVE_IN_VARLAMORE = 14622;
            public static final int LEAGUE_TASK_FILL_A_GRAPE_BARREL_FOR_THE_FOREMAN = 14623;
            public static final int LEAGUE_TASK_TELEPORT_TO_XERICS_HONOUR = 14624;
            public static final int LEAGUE_TASK_CAST_KOUREND_CASTLE_TELEPORT = 14625;
            public static final int LEAGUE_TASK_CREATE_100_JUNIPER_CHARCOAL = 14626;
            public static final int LEAGUE_TASK_DIG_25_SALTPETRE = 14627;
            public static final int LEAGUE_TASK_FULLY_DEPLETE_A_BLOOD_ESSENCE = 14628;
            public static final int LEAGUE_TASK_MAX_OUT_1_FRAGMENT = 14629;
            public static final int LEAGUE_TASK_SMUGGLE_SOME_RUM = 14630;
            public static final int LEAGUE_TASK_SPAWN_A_SKELETAL_MAGE = 14631;
            public static final int LEAGUE_TASK_ACTIVATE_1_SET_EFFECT = 14632;
            public static final int LEAGUE_TASK_FIND_THE_NEEDLE = 14633;
            public static final int LEAGUE_TASK_ORDER_A_DRINK_AT_THE_OLD_NITE = 14634;
            public static final int LEAGUE_TASK_DESTROY_THE_ONE_RING_TO_RULE_THEM_ALL = 14635;
            public static final int LEAGUE_TASK_ENTER_THE_DEEP_WILDERNESS_DUNGEON = 14636;
            public static final int LEAGUE_TASK_STORE_10K_BOWSTRINGS_INSIDE_A_BOWSTRING_SPOOL = 14637;
            public static final int LEAGUE_TASK_FIX_A_RUSTY_SCIMITAR = 14638;
            public static final int LEAGUE_TASK_HANG_A_PAINTING_OF_A_WATERMILL = 14639;
            public static final int LEAGUE_TASK_OPEN_THE_CRYSTAL_CHEST = 14640;
            public static final int LEAGUE_TASK_ENTER_THE_WARRIORS_GUILD = 14641;
            public static final int LEAGUE_TASK_THROW_A_SHOT_PUT_12_YARDS = 14642;
            public static final int LEAGUE_TASK_MINE_RUNITE_NEITIZNOT = 14643;
            public static final int LEAGUE_TASK_FILL_UP_20_BUCKETS_OF_SAND_IN_RELLEKKA = 14644;
            public static final int LEAGUE_TASK_PRAY_AT_THE_ELIDINIS_STATUETTE = 14645;
            public static final int LEAGUE_TASK_COMMUNE_A_PHAROAHS_SCEPTRE_TO_THE_NECROPOLIS = 14646;
            public static final int LEAGUE_TASK_OFFEND_BANDITS = 14647;
            public static final int LEAGUE_TASK_TELEGRAB_A_BLOODY_BRACER = 14648;
            public static final int LEAGUE_TASK_PICK_UP_10_WHITEBERRIES_IN_TIRANNWN = 14649;
            public static final int LEAGUE_TASK_RING_ALL_OF_THE_PRIFDDINAS_BELLS = 14650;
            public static final int LEAGUE_TASK_USE_AN_ELVEN_TELEPORT_CRYSTAL = 14651;
            public static final int LEAGUE_TASK_SELL_20_IRON_SHEETS = 14652;
            public static final int LEAGUE_TASK_USE_A_HERRING_ON_A_MIGHTY_TREE = 14653;
            public static final int LEAGUE_TASK_FEED_A_GHAST_SOME_SWEETS = 14654;
            public static final int LEAGUE_TASK_READ_A_PRAYER_BOOK_NEAR_A_LECTERN = 14655;
            public static final int LEAGUE_TASK_WEAR_MULTIPLE_SNELMS = 14656;
            public static final int LEAGUE_TASK_HIS_FAITHFUL_SERVANTS = 14657;
            public static final int LEAGUE_TASK_FEED_A_DOG_SOME_BONES = 14658;
            public static final int LEAGUE_TASK_GET_A_HAIRCUT = 14659;
            public static final int LEAGUE_TASK_MAKE_SOME_FLOUR = 14660;
            public static final int LEAGUE_TASK_OBTAIN_A_KEBAB_FROM_RANDOM_EVENT = 14661;
            public static final int LEAGUE_TASK_OPEN_THE_LEAGUES_MENU = 14662;
            public static final int LEAGUE_TASK_OPEN_THE_MYSTERY_BOX = 14663;
            public static final int LEAGUE_TASK_PICK_6_FLAX = 14664;
            public static final int LEAGUE_TASK_PICK_6_WHEAT_6_CABBAGES_AND_6_POTATOES = 14665;
            public static final int LEAGUE_TASK_TALK_TO_ANY_PORT_MASTER = 14666;
            public static final int LEAGUE_TASK_TRADE_A_HERB_WITH_JEKYLL = 14667;
            public static final int LEAGUE_TASK_TURN_OFF_YOUR_RUN = 14668;
            public static final int LEAGUE_TASK_UPSET_SANDWICH_LADY = 14669;
            public static final int LEAGUE_TASK_COMPLETE_ASCENT_OF_ARCEUUS = 14670;
            public static final int ACTION_COMPLETE_THE_VALE_TOTEMS_MINIQUEST = 14671;
            public static final int LEAGUE_TASK_COMPLETE_CLIENT_OF_KOUREND = 14672;
            public static final int LEAGUE_TASK_COMPLETE_FORSAKEN_TOWER = 14673;
            public static final int LEAGUE_TASK_COMPLETE_QUEEN_OF_THIEVES = 14674;
            public static final int LEAGUE_TASK_THE_FROZEN_DOOR = 14675;
            public static final int LEAGUE_TASK_COMPLETE_LAND_OF_THE_GOBLINS = 14676;
            public static final int LEAGUE_TASK_COMPLETE_ELEMENTAL_WORKSHOP_II = 14677;
            public static final int LEAGUE_TASK_COMPLETE_PATH_OF_GLOUPHRIE = 14678;
            public static final int LEAGUE_TASK_COMPLETE_DEATH_ON_THE_ISLE = 14679;
            public static final int LEAGUE_TASK_COMPLETE_PERILOUS_MOONS = 14680;
            public static final int ACTION_COMPLETE_SHADOWS_OF_CUSTODIA = 14681;
            public static final int ACTION_COMPLETE_THE_FINAL_DAWN = 14682;
            public static final int ACTION_COMPLETE_THE_HEART_OF_DARKNESS = 14683;
            public static final int LEAGUE_TASK_ASK_FOR_A_QUEST_FROM_BOB = 14684;
            public static final int LEAGUE_TASK_COMPLETE_A_KINGDOM_DIVIDED = 14685;
            public static final int LEAGUE_TASK_COMPLETE_A_SOULS_BANE = 14686;
            public static final int LEAGUE_TASK_COMPLETE_A_TASTE_OF_HOPE = 14687;
            public static final int LEAGUE_TASK_COMPLETE_BENEATH_CURSED_SANDS = 14688;
            public static final int LEAGUE_TASK_COMPLETE_DEFENDER_OF_VARROCK = 14689;
            public static final int LEAGUE_TASK_COMPLETE_DEMON_SLAYER = 14690;
            public static final int LEAGUE_TASK_COMPLETE_ELEMENTAL_WORKSHOP_I = 14691;
            public static final int LEAGUE_TASK_COMPLETE_ENAKHRAS_LAMENT = 14692;
            public static final int LEAGUE_TASK_COMPLETE_GERTRUDES_CAT = 14693;
            public static final int LEAGUE_TASK_COMPLETE_GHOSTS_AHOY = 14694;
            public static final int LEAGUE_TASK_COMPLETE_HAUNTED_MINE = 14695;
            public static final int LEAGUE_TASK_COMPLETE_ROMEO_JULIET = 14696;
            public static final int LEAGUE_TASK_COMPLETE_RUNE_MYSTERIES = 14697;
            public static final int LEAGUE_TASK_COMPLETE_SHADOW_OF_THE_STORM = 14698;
            public static final int LEAGUE_TASK_COMPLETE_SHEEP_SHEARER = 14699;
            public static final int LEAGUE_TASK_COMPLETE_SINS_OF_THE_FATHER = 14700;
            public static final int LEAGUE_TASK_COMPLETE_SLEEPING_GIANTS = 14701;
            public static final int LEAGUE_TASK_COMPLETE_SPIRITS_OF_THE_ELID = 14702;
            public static final int LEAGUE_TASK_COMPLETE_THE_GOLEM = 14703;
            public static final int LEAGUE_TASK_COMPLETE_THE_RESTLESS_GHOST = 14704;
            public static final int LEAGUE_TASK_COMPLETE_VAMPYRE_SLAYER = 14705;
            public static final int LEAGUE_TASK_FINISH_CURSE_OF_ARRAV = 14706;
            public static final int LEAGUE_TASK_COMPLETE_A_PORCINE_OF_INTEREST = 14707;
            public static final int LEAGUE_TASK_COMPLETE_BELOW_ICE_MOUNTAIN = 14708;
            public static final int LEAGUE_TASK_COMPLETE_DEATH_TO_THE_DORGESHUUN = 14709;
            public static final int LEAGUE_TASK_COMPLETE_THE_LOST_TRIBE = 14710;
            public static final int LEAGUE_TASK_COMPLETE_DEPTHS_OF_DESPAIR = 14711;
            public static final int LEAGUE_TASK_COMPLETE_A_RIBBITING_TALE = 14712;
            public static final int LEAGUE_TASK_COMPLETE_MEAT_AND_GREET = 14713;
            public static final int ACTION_COMPLETE_AT_FIRST_LIGHT_QUEST = 14714;
            public static final int LEAGUE_TASK_COMPLETE_GETTING_AHEAD = 14715;
            public static final int LEAGUE_TASK_COMPLETE_THE_GARDEN_OF_DEATH = 14716;
            public static final int LEAGUE_TASK_COMPLETE_IN_SEARCH_OF_KNOWLEDGE = 14717;
            public static final int LEAGUE_TASK_COMPLETE_TALE_OF_THE_RIGHTEOUS = 14718;
            public static final int LEAGUE_TASK_COMPLETE_OLAFS_QUEST = 14719;
            public static final int LEAGUE_TASK_COMPLETE_ROYAL_TROUBLE = 14720;
            public static final int LEAGUE_TASK_COMPLETE_THRONE_OF_MISCELLANIA = 14721;
            public static final int LEAGUE_TASK_COMPLETE_FISHING_CONTEST = 14722;
            public static final int LEAGUE_TASK_COMPLETE_MONKS_FRIEND = 14723;
            public static final int LEAGUE_TASK_COMPLETE_SEA_SLUG = 14724;
            public static final int LEAGUE_TASK_COMPLETE_THE_OBSERVATORY_QUEST = 14725;
            public static final int LEAGUE_TASK_COMPLETE_TOWER_OF_LIFE = 14726;
            public static final int LEAGUE_TASK_COMPLETE_THE_GIANT_DWARF = 14727;
            public static final int LEAGUE_TASK_COMPLETE_WITCHS_POTION = 14728;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_10 = 14729;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_20 = 14730;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_30 = 14731;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_40 = 14732;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_5 = 14733;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_50 = 14734;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_60 = 14735;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_70 = 14736;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_80 = 14737;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_90 = 14738;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_95 = 14739;
            public static final int LEAGUE_TASK_ACHIEVE_YOUR_FIRST_LEVEL_UP = 14740;
            public static final int LEAGUE_TASK_ACTIVATE_SMITE = 14741;
            public static final int LEAGUE_TASK_BEAT_JACKY_JESTER = 14742;
            public static final int LEAGUE_TASK_BLOW_100_LIGHT_ORBS = 14743;
            public static final int LEAGUE_TASK_BUILD_A_DEMONIC_THRONE = 14744;
            public static final int LEAGUE_TASK_BUILD_A_GILDED_ALTAR = 14745;
            public static final int LEAGUE_TASK_BUILD_A_MAHOGANY_PORTAL = 14746;
            public static final int LEAGUE_TASK_BUILD_A_ROOM_IN_YOUR_PLAYER_OWNED_HOUSE = 14747;
            public static final int LEAGUE_TASK_BUILD_A_SPIRIT_TREE_FAIRY_RING = 14748;
            public static final int LEAGUE_TASK_BUILD_AN_OAK_LARDER = 14749;
            public static final int LEAGUE_TASK_BURN_100_WILLOW_LOGS = 14750;
            public static final int LEAGUE_TASK_BURN_100_YEW_LOGS = 14751;
            public static final int LEAGUE_TASK_BURN_25_MAPLE_LOGS = 14752;
            public static final int LEAGUE_TASK_BURN_SOME_FOOD = 14753;
            public static final int LEAGUE_TASK_BURN_SOME_MAGIC_LOGS = 14754;
            public static final int LEAGUE_TASK_BURN_SOME_NORMAL_LOGS = 14755;
            public static final int LEAGUE_TASK_BURN_SOME_OAK_LOGS = 14756;
            public static final int LEAGUE_TASK_CAST_HOME_TELEPORT = 14757;
            public static final int ACTION_CAST_LOW_LEVEL_ALCHEMY = 14758;
            public static final int LEAGUE_TASK_CATCH_10_COD = 14759;
            public static final int LEAGUE_TASK_CATCH_100_SHARK = 14760;
            public static final int LEAGUE_TASK_CATCH_100_SWORDFISH = 14761;
            public static final int LEAGUE_TASK_CATCH_100_TUNA = 14762;
            public static final int LEAGUE_TASK_CATCH_20_MACKEREL = 14763;
            public static final int LEAGUE_TASK_CATCH_50_TUNA = 14764;
            public static final int LEAGUE_TASK_CATCH_500_CHINCHOMPAS = 14765;
            public static final int LEAGUE_TASK_CATCH_75_LOBSTERS = 14766;
            public static final int LEAGUE_TASK_CATCH_75_TROUT = 14767;
            public static final int LEAGUE_TASK_CATCH_A_BABY_IMPLING = 14768;
            public static final int LEAGUE_TASK_CATCH_A_BUTTERFLY = 14769;
            public static final int LEAGUE_TASK_CATCH_A_DRAGON_IMPLING = 14770;
            public static final int LEAGUE_TASK_CATCH_A_HERRING = 14771;
            public static final int LEAGUE_TASK_CATCH_A_SHRIMP = 14772;
            public static final int LEAGUE_TASK_CATCH_A_SWAMP_LIZARD_OR_SALAMANDER = 14773;
            public static final int LEAGUE_TASK_CATCH_AN_ANCHOVY = 14774;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_FRUIT_TREE = 14775;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_TREE = 14776;
            public static final int LEAGUE_TASK_CHOP_100_WILLOW_LOGS = 14777;
            public static final int LEAGUE_TASK_CHOP_100_YEW_LOGS = 14778;
            public static final int LEAGUE_TASK_CHOP_75_MAGIC_LOGS = 14779;
            public static final int LEAGUE_TASK_CHOP_SOME_LOGS = 14780;
            public static final int LEAGUE_TASK_CHOP_SOME_LOGS_WITH_A_RUNE_AXE = 14781;
            public static final int LEAGUE_TASK_CHOP_SOME_LOGS_WITH_A_STEEL_AXE = 14782;
            public static final int LEAGUE_TASK_CLEAN_100_GRIMY_AVANTOE = 14783;
            public static final int LEAGUE_TASK_CLEAN_15_GRIMY_TARROMIN = 14784;
            public static final int LEAGUE_TASK_CLEAN_25_GRIMY_GUAM_LEAFS = 14785;
            public static final int LEAGUE_TASK_CLEAN_50_GRIMY_CADANTINE = 14786;
            public static final int LEAGUE_TASK_CLEAN_50_GRIMY_LANTADYME = 14787;
            public static final int LEAGUE_TASK_CLEAN_50_GRIMY_RANARR_WEED = 14788;
            public static final int LEAGUE_TASK_CLEAN_A_GRIMY_AVANTOE = 14789;
            public static final int LEAGUE_TASK_CLEAN_A_GRIMY_GUAM = 14790;
            public static final int ACTION_ALCHEMY_500 = 14791;
            public static final int LEAGUE_TASK_COOK_100_PIES = 14792;
            public static final int LEAGUE_TASK_COOK_100_SHARKS = 14793;
            public static final int LEAGUE_TASK_COOK_100_SWORDFISH = 14794;
            public static final int LEAGUE_TASK_COOK_25_MEAT_PIES = 14795;
            public static final int LEAGUE_TASK_COOK_50_TUNA = 14796;
            public static final int LEAGUE_TASK_COOK_SHRIMP = 14797;
            public static final int LEAGUE_TASK_COOK_SOMETHING_WITH_AN_APRON_ON = 14798;
            public static final int LEAGUE_TASK_CRAFT_100_UNPOWERED_ORBS = 14799;
            public static final int LEAGUE_TASK_CRAFT_20_SILVER_ITEMS = 14800;
            public static final int LEAGUE_TASK_CRAFT_200_ESSENCE_INTO_RUNES = 14801;
            public static final int LEAGUE_TASK_CRAFT_2500_ESSENCE_INTO_RUNES = 14802;
            public static final int LEAGUE_TASK_CRAFT_30_BLUE_DRAGONHIDE_BODIES = 14803;
            public static final int LEAGUE_TASK_CRAFT_4_RUNES_WITH_1_ESSENCE = 14804;
            public static final int LEAGUE_TASK_CRAFT_A_DRAGONSTONE_AMULET = 14805;
            public static final int ACTION_CRAFT_A_SAPPHIRE_AMULET = 14806;
            public static final int LEAGUE_TASK_CRAFT_AN_EMERALD_RING = 14807;
            public static final int LEAGUE_TASK_CRAFT_AN_ONYX_AMULET = 14808;
            public static final int LEAGUE_TASK_CRAFT_ANY_COMBINATION_RUNE = 14809;
            public static final int ACTION_CRAFT_LEATHER_CHAPS = 14810;
            public static final int LEAGUE_TASK_CREATE_A_COMPOST_POTION = 14811;
            public static final int LEAGUE_TASK_CREATE_A_GREEN_DHIDE_SHIELD = 14812;
            public static final int LEAGUE_TASK_CREATE_A_GUTHIX_REST_TEA = 14813;
            public static final int LEAGUE_TASK_CREATE_A_MITHRIL_GRAPPLE = 14814;
            public static final int ACTION_CREATE_A_RED_DHIDE_SHIELD = 14815;
            public static final int LEAGUE_TASK_CREATE_AN_ANTIPOISON = 14816;
            public static final int LEAGUE_TASK_CUT_50_MAPLE_LOGS = 14817;
            public static final int ACTION_CUT_A_RUBY = 14818;
            public static final int LEAGUE_TASK_DRINK_A_STRENGTH_POTION = 14819;
            public static final int LEAGUE_TASK_ENTER_YOUR_PLAYER_OWNED_HOUSE = 14820;
            public static final int LEAGUE_TASK_REPAIR_A_FISHING_CRANE = 14821;
            public static final int LEAGUE_TASK_TURN_IN_1_LIBRARY_BOOK = 14822;
            public static final int LEAGUE_TASK_COMPLETE_1_FARMING_CONTRACT = 14823;
            public static final int LEAGUE_TASK_LOCATE_A_RUNECRAFTING_ALTAR_WITH_A_TALISMAN = 14824;
            public static final int LEAGUE_TASK_MINE_15_COAL = 14825;
            public static final int LEAGUE_TASK_MINE_SOME_COPPER_ORE = 14826;
            public static final int LEAGUE_TASK_CATCH_A_CRIMSON_SWIFT_IN_THE_FELDIP_HILLS = 14827;
            public static final int LEAGUE_TASK_FERMENT_100_JUGS_OF_WINE = 14828;
            public static final int LEAGUE_TASK_SWITCH_TO_THE_LUNAR_SPELLBOOK = 14829;
            public static final int LEAGUE_TASK_CATCH_A_CERULEAN_TWITCH = 14830;
            public static final int LEAGUE_TASK_CATCH_A_POLAR_KEBBIT = 14831;
            public static final int LEAGUE_TASK_CATCH_A_WILD_KEBBIT = 14832;
            public static final int LEAGUE_TASK_CHOP_DOWN_AN_ARCTIC_PINE_TREE = 14833;
            public static final int LEAGUE_TASK_CUT_THE_SWAYING_TREE = 14834;
            public static final int LEAGUE_TASK_COMPLETE_THE_GNOME_STRONGHOLD_AGILITY_COURSE = 14835;
            public static final int LEAGUE_TASK_RAKE_A_FARMING_PATCH = 14836;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_500 = 14837;
            public static final int LEAGUE_TASK_COOK_A_RABBIT_IN_TIRANNWN = 14838;
            public static final int LEAGUE_TASK_SMITH_A_BRONZE_FULL_HELM = 14839;
            public static final int LEAGUE_TASK_SMITH_A_BRONZE_PLATESKIRT = 14840;
            public static final int LEAGUE_TASK_STEAL_A_CHOCOLATE_SLICE = 14841;
            public static final int LEAGUE_TASK_STEAL_SOME_SILK = 14842;
            public static final int LEAGUE_TASK_VISIT_THE_RUNE_ESSENCE_MINE = 14843;
            public static final int LEAGUE_TASK_GIVE_THURGO_A_REDBERRY_PIE = 14844;
            public static final int LEAGUE_TASK_FILL_A_BUCKET_WITH_SUPERCOMPOST = 14845;
            public static final int LEAGUE_TASK_FILL_A_GIANT_POUCH = 14846;
            public static final int LEAGUE_TASK_FILL_A_HARD_STASH_UNIT = 14847;
            public static final int LEAGUE_TASK_MOVE_YOUR_HOUSE_TO_TAVERLEY = 14848;
            public static final int LEAGUE_TASK_CRAFT_AN_AIR_RUNE = 14849;
            public static final int LEAGUE_TASK_FILL_A_LARGE_POUCH = 14850;
            public static final int LEAGUE_TASK_FILL_A_MEDIUM_STASH_UNIT = 14851;
            public static final int LEAGUE_TASK_SMITH_SHAYZIEN_5 = 14852;
            public static final int LEAGUE_TASK_FLETCH_100_DRAGON_JAVELINS = 14853;
            public static final int LEAGUE_TASK_FLETCH_1000_ARROW_SHAFTS = 14854;
            public static final int LEAGUE_TASK_CATCH_150_ANGLERFISH = 14855;
            public static final int LEAGUE_TASK_FLETCH_150_IRON_ARROWS = 14856;
            public static final int LEAGUE_TASK_FLETCH_200_MAGIC_LONGBOW_U = 14857;
            public static final int LEAGUE_TASK_FLETCH_25_MAPLE_LONGBOW_U = 14858;
            public static final int LEAGUE_TASK_FLETCH_25_OAK_STOCKS = 14859;
            public static final int LEAGUE_TASK_FLETCH_50_WILLOW_LONGBOW_U = 14860;
            public static final int LEAGUE_TASK_COMPLETE_A_ROOFTOP_AGILITY_COURSE = 14861;
            public static final int LEAGUE_TASK_FLETCH_50_YEW_LONGBOW_U = 14862;
            public static final int LEAGUE_TASK_FLETCH_A_MAGIC_SHIELD = 14863;
            public static final int LEAGUE_TASK_CRAFT_A_LEATHER_BODY = 14864;
            public static final int LEAGUE_TASK_CRAFT_ANY_RUNE = 14865;
            public static final int LEAGUE_TASK_FLETCH_A_RUNE_CROSSBOW = 14866;
            public static final int LEAGUE_TASK_FLETCH_A_WILLOW_SHORTBOW_U = 14867;
            public static final int LEAGUE_TASK_FLETCH_AN_OAK_SHORTBOW = 14868;
            public static final int LEAGUE_TASK_FLETCH_SOME_ARROW_SHAFTS = 14869;
            public static final int ACTION_MINE_SOME_COAL_FROM_STONE_CUTTER_OUTPOST = 14870;
            public static final int LEAGUE_TASK_TRIM_YOUR_BEARD_IN_CAM_TORUM = 14871;
            public static final int LEAGUE_TASK_CHOP_A_TREE_IN_THE_TLATI_RAINFOREST = 14872;
            public static final int LEAGUE_TASK_CROSS_A_TRAP_IN_ISAFDAR = 14873;
            public static final int LEAGUE_TASK_CUT_A_SAPPHIRE = 14874;
            public static final int LEAGUE_TASK_FLETCH_SOME_BROAD_ARROWS_OR_BOLTS = 14875;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_REDWOOD_TREE = 14876;
            public static final int LEAGUE_TASK_MAKE_50_FORGOTTEN_BREWS = 14877;
            public static final int LEAGUE_TASK_BLAST_FURNACE_100_RUNITE_BARS = 14878;
            public static final int LEAGUE_TASK_MINE_1000_ANCIENT_ESSENCE = 14879;
            public static final int LEAGUE_TASK_MAKE_AN_EXTENDED_ANTIFIRE_POTION = 14880;
            public static final int LEAGUE_TASK_MINE_SOME_RUNITE_ORE_IN_THE_WILDERNESS = 14881;
            public static final int LEAGUE_TASK_COOK_A_DARK_CRAB = 14882;
            public static final int LEAGUE_TASK_TRAVEL_BETWEEN_YOUR_SPIRIT_TREES = 14883;
            public static final int LEAGUE_TASK_CHOP_500_REDWOOD_LOGS = 14884;
            public static final int LEAGUE_TASK_COOK_100_ANGLERFISH = 14885;
            public static final int LEAGUE_TASK_OBTAIN_A_ZALCANO_SHARD = 14886;
            public static final int LEAGUE_TASK_SMITH_500_RUNITE_BOLTS_UNF_IN_PRIFDDINAS = 14887;
            public static final int LEAGUE_TASK_CATCH_300_RED_CHINCHOMPAS_IN_TIRANNWN = 14888;
            public static final int LEAGUE_TASK_CHECK_THE_HEALTH_OF_5_CRYSTAL_TREES = 14889;
            public static final int LEAGUE_TASK_BAREHAND_CATCH_A_SHARK = 14890;
            public static final int LEAGUE_TASK_CATCH_150_RED_CHINCHOMPAS_IN_KANDARIN = 14891;
            public static final int LEAGUE_TASK_CRAFT_100_SOUL_RUNES = 14892;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_DRAGONFRUIT_TREE_IN_CATHERBY = 14893;
            public static final int LEAGUE_TASK_FLETCH_250_REDWOOD_LOGS_INTO_ARROW_SHAFTS = 14894;
            public static final int LEAGUE_TASK_COMPLETE_THE_ARDOUGNE_AGILITY_COURSE = 14895;
            public static final int LEAGUE_TASK_CRAFT_A_WRATH_RUNE = 14896;
            public static final int LEAGUE_TASK_CREATE_100_DIVINE_RANGING_POTIONS = 14897;
            public static final int LEAGUE_TASK_MAKE_200_BRUTAL_RUNE_ARROW = 14898;
            public static final int LEAGUE_TASK_CREATE_100_DIVINE_SUPER_COMBAT_POTIONS = 14899;
            public static final int LEAGUE_TASK_DISSECT_250_SACRED_EELS = 14900;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_ATTACK_XP = 14901;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_DEFENCE_XP = 14902;
            public static final int LEAGUE_TASK_MINE_100_RUNITE_ORE_IN_TIRANNWN = 14903;
            public static final int LEAGUE_TASK_REDEEM_50_WILDERNESS_AGILITY_TICKETS = 14904;
            public static final int LEAGUE_TASK_SACRIFICE_SOME_DRAGON_BONES_AT_THE_CHAOS_TEMPLE = 14906;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_HITPOINTS_XP = 14907;
            public static final int LEAGUE_TASK_CATCH_100_DARK_CRABS = 14908;
            public static final int LEAGUE_TASK_PURCHASE_THE_CHUGGING_BARREL = 14909;
            public static final int LEAGUE_TASK_CATCH_100_TECU_SALAMANDERS = 14910;
            public static final int LEAGUE_TASK_CATCH_50_MOONLIGHT_ANTELOPES = 14911;
            public static final int ACTION_FINISH_REDWOOD_ENT_TOTEM_DECORATION = 14912;
            public static final int LEAGUE_TASK_CROSS_THE_CHAOS_TEMPLE_STONE_SHORTCUT = 14913;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_MAGIC_XP = 14914;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_RANGED_XP = 14915;
            public static final int LEAGUE_TASK_PURCHASE_A_CELESTIAL_RING = 14916;
            public static final int LEAGUE_TASK_CRAFT_50_LAW_RUNES = 14917;
            public static final int LEAGUE_TASK_HARVEST_A_SNAPDRAGON_IN_WEISS = 14918;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_STRENGTH_XP = 14919;
            public static final int LEAGUE_TASK_TRAP_A_SABRETOOTHED_KYATT = 14920;
            public static final int LEAGUE_TASK_HARVEST_A_RANARR_WEED = 14921;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_ATTACK_XP = 14922;
            public static final int LEAGUE_TASK_TURN_IN_50_LIBRARY_BOOKS = 14923;
            public static final int LEAGUE_TASK_FISH_AN_OLD_BOOT_IN_STRANGLEWOOD = 14924;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_DEFENCE_XP = 14925;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_HITPOINTS_XP = 14926;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_MAGIC_XP = 14927;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_RANGED_XP = 14928;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_STRENGTH_XP = 14929;
            public static final int LEAGUE_TASK_LAND_A_HOOP_ON_A_STICK = 14930;
            public static final int LEAGUE_TASK_LIGHT_A_BULLSEYE_LANTERN = 14931;
            public static final int LEAGUE_TASK_LIGHT_A_TORCH = 14932;
            public static final int LEAGUE_TASK_MAKE_20_RANGING_POTIONS = 14933;
            public static final int LEAGUE_TASK_MAKE_20_STAMINA_POTIONS = 14934;
            public static final int LEAGUE_TASK_MAKE_30_PRAYER_POTIONS = 14935;
            public static final int LEAGUE_TASK_MAKE_A_4_DOSE_POTION = 14936;
            public static final int ACTION_MAKE_A_MEAT_PIZZA = 14937;
            public static final int LEAGUE_TASK_MAKE_A_SARADOMIN_BREW = 14938;
            public static final int LEAGUE_TASK_MAKE_A_SUPER_COMBAT_POTION = 14939;
            public static final int LEAGUE_TASK_MAKE_AN_ATTACK_POTION = 14940;
            public static final int LEAGUE_TASK_MINE_10_SILVER = 14941;
            public static final int LEAGUE_TASK_MINE_5_TIN_ORE = 14942;
            public static final int LEAGUE_TASK_MINE_50_ADAMANTITE_ORE = 14943;
            public static final int LEAGUE_TASK_HARVEST_25_ZAMORAKS_GRAPES = 14944;
            public static final int LEAGUE_TASK_MINE_50_IRON_ORE = 14945;
            public static final int LEAGUE_TASK_MINE_50_MITHRIL_ORE = 14946;
            public static final int LEAGUE_TASK_USE_YOUR_PORTAL_NEXUS_TO_TELEPORT_TO_WEISS = 14947;
            public static final int LEAGUE_TASK_OBTAIN_RUNITE_ORE_AT_THE_BLAST_MINE = 14948;
            public static final int ACTION_MINE_SOME_CLAY = 14949;
            public static final int LEAGUE_TASK_MINE_SOME_ORE_WITH_A_RUNE_PICKAXE = 14950;
            public static final int LEAGUE_TASK_MINE_SOME_ORE_WITH_A_STEEL_PICKAXE = 14951;
            public static final int LEAGUE_TASK_OBTAIN_100_MILLION_XP_COMBAT_SKILL = 14952;
            public static final int LEAGUE_TASK_OBTAIN_100_MILLION_XP_NONCOMBAT_SKILL = 14953;
            public static final int LEAGUE_TASK_OBTAIN_200_MILLION_XP_COMBAT_SKILL = 14954;
            public static final int LEAGUE_TASK_OBTAIN_200_MILLION_XP_NONCOMBAT_SKILL = 14955;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_AGILITY_XP = 14956;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_CONSTRUCTION_XP = 14957;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_COOKING_XP = 14958;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_CRAFTING_XP = 14959;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_FARMING_XP = 14960;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_FIREMAKING_XP = 14961;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_FISHING_XP = 14962;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_FLETCHING_XP = 14963;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_HERBLORE_XP = 14964;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_HUNTER_XP = 14965;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_MINING_XP = 14966;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_PRAYER_XP = 14967;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_RUNECRAFT_XP = 14968;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_SLAYER_XP = 14969;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_SMITHING_XP = 14970;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_THIEVING_XP = 14971;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_WOODCUTTING_XP = 14972;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_XP_5_NONCOMBAT_SKILLS = 14973;
            public static final int LEAGUE_TASK_OBTAIN_25_MILLION_XP_COMBAT_SKILL = 14974;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_AGILITY_XP = 14975;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_CONSTRUCTION_XP = 14976;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_COOKING_XP = 14977;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_CRAFTING_XP = 14978;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_FARMING_XP = 14979;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_FIREMAKING_XP = 14980;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_FISHING_XP = 14981;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_FLETCHING_XP = 14982;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_HERBLORE_XP = 14983;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_HUNTER_XP = 14984;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_MINING_XP = 14985;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_PRAYER_XP = 14986;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_RUNECRAFT_XP = 14987;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_SLAYER_XP = 14988;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_SMITHING_XP = 14989;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_THIEVING_XP = 14990;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_WOODCUTTING_XP = 14991;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_XP_3_NONCOMBAT_SKILLS = 14992;
            public static final int LEAGUE_TASK_OBTAIN_35_MILLION_XP_COMBAT_SKILL = 14993;
            public static final int LEAGUE_TASK_STEAL_FROM_A_GEM_STALL_IN_KOUREND = 14994;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_AGILITY_XP = 14995;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_ATTACK_XP = 14996;
            public static final int LEAGUE_TASK_BUILD_A_GILDED_PORTAL_NEXUS = 14997;
            public static final int LEAGUE_TASK_BUILD_A_GOLD_SINK = 14998;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_CONSTRUCTION_XP = 14999;
            public static final int LEAGUE_TASK_BUILD_AN_OBELISK = 15000;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_COOKING_XP = 15001;
            public static final int LEAGUE_TASK_CATCH_A_LUCKY_IMPLING_BAREHANDED = 15002;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_CRAFTING_XP = 15003;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_DEFENCE_XP = 15004;
            public static final int LEAGUE_TASK_BLAST_FURNACE_100_MITHRIL_BARS = 15005;
            public static final int LEAGUE_TASK_PLANT_100_BOLOGANO_SEEDS = 15006;
            public static final int LEAGUE_TASK_PLANT_100_LOGAVANO_SEEDS = 15007;
            public static final int LEAGUE_TASK_CAST_FERTILE_SOIL = 15008;
            public static final int LEAGUE_TASK_PLANT_AN_ANIMA_SEED = 15009;
            public static final int LEAGUE_TASK_PICKPOCKET_A_HERO = 15010;
            public static final int LEAGUE_TASK_CAST_MOONCLAN_TELEPORT = 15011;
            public static final int LEAGUE_TASK_CAST_SPELLBOOK_SWAP = 15012;
            public static final int LEAGUE_TASK_COMPLETE_25_LAPS_OF_THE_RELLEKKA_AGILITY_COURSE = 15013;
            public static final int LEAGUE_TASK_BURN_200_REDWOOD_LOGS = 15014;
            public static final int LEAGUE_TASK_PICKPOCKET_A_KING_WORM = 15015;
            public static final int LEAGUE_TASK_TRADE_IN_1000_MINNOWS = 15016;
            public static final int ACTION_FINISH_YEW_ENT_TOTEM_DECORATION = 15017;
            public static final int LEAGUE_TASK_MAKE_100_PRAYER_REGENERATION_POTIONS = 15018;
            public static final int LEAGUE_TASK_MAKE_50_GOADING_POTIONS = 15019;
            public static final int LEAGUE_TASK_CHOP_200_REDWOOD_LOGS = 15020;
            public static final int LEAGUE_TASK_COMPLETE_50_FARMING_CONTRACTS = 15021;
            public static final int ACTION_MAKE_A_GREENMAN_CARVING = 15022;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_PAPAYA_TREE_IN_THE_GNOME_STRONGHOLD = 15023;
            public static final int ACTION_MAKE_A_GREENMAN_STATUE = 15024;
            public static final int LEAGUE_TASK_OBTAIN_THE_HUNTSMANS_KIT = 15025;
            public static final int LEAGUE_TASK_COMPLETE_THE_SHAYZIEN_ADVANCED_AGILITY_COURSE = 15026;
            public static final int LEAGUE_TASK_OPEN_A_MASTER_HUNTERS_LOOT_SACK = 15027;
            public static final int LEAGUE_TASK_COMPLETE_THE_RELLEKKA_AGILITY_COURSE = 15028;
            public static final int LEAGUE_TASK_COMPLETE_THE_SEERS_VILLAGE_AGILITY_COURSE = 15029;
            public static final int LEAGUE_TASK_CRAFT_100_BLOOD_RUNES = 15030;
            public static final int LEAGUE_TASK_PURCHASE_THE_REAGENTS_POUCH = 15031;
            public static final int LEAGUE_TASK_FLETCH_100_MAPLE_LONGBOW_U_IN_KANDARIN = 15032;
            public static final int LEAGUE_TASK_STEAL_A_BLESSED_BONE_STATUETTE = 15033;
            public static final int LEAGUE_TASK_CREATE_A_CATHERBY_TELEPORT_TABLET = 15034;
            public static final int LEAGUE_TASK_CREATE_10_DYNAMITE = 15035;
            public static final int LEAGUE_TASK_CATCH_25_SUNLIGHT_ANTELOPES = 15036;
            public static final int LEAGUE_TASK_ENTER_THE_FARMING_GUILDS_HIGH_TIER = 15037;
            public static final int LEAGUE_TASK_PICKPOCKET_CLUE_FROM_HERO = 15038;
            public static final int LEAGUE_TASK_CHECK_THE_HEALTH_OF_MAHOGANY_TREE_IN_MARCELLUSS_PATCH = 15039;
            public static final int LEAGUE_TASK_CHOP_20_MAGIC_LOGS_IN_VARLAMORE = 15040;
            public static final int LEAGUE_TASK_FLETCH_A_REDWOOD_SHIELD = 15041;
            public static final int LEAGUE_TASK_COMPLETE_50_HUNTER_RUMOURS = 15042;
            public static final int LEAGUE_TASK_COOK_100_MOONLIGHT_ANTELOPES = 15043;
            public static final int LEAGUE_TASK_CRAFT_1000_SUNFIRE_RUNES = 15044;
            public static final int LEAGUE_TASK_CREATE_100_JUGS_OF_BLESSED_SUNFIRE_WINE = 15045;
            public static final int ACTION_FLETCH_SOME_ATLATL_DARTS = 15046;
            public static final int LEAGUE_TASK_BLAST_FURNACE_100_ADAMANT_BARS = 15047;
            public static final int LEAGUE_TASK_CAST_FULL_SINISTER_OFFERING = 15048;
            public static final int LEAGUE_TASK_CHOP_A_MAGIC_LOG_AT_THE_FORSAKEN_TOWER = 15049;
            public static final int LEAGUE_TASK_OBTAIN_ADAMANTITE_ORE_AT_THE_BLAST_MINE = 15050;
            public static final int LEAGUE_TASK_SERVE_20_PIZZAS_IN_THE_MESS_HALL = 15051;
            public static final int LEAGUE_TASK_CAST_RESURRECT_CROPS = 15052;
            public static final int LEAGUE_TASK_DAGANNOTH_BONE_POH_ALTAR = 15053;
            public static final int LEAGUE_TASK_GUARDIANS_OF_THE_RIFT_1_RIFT_CLOSED = 15054;
            public static final int LEAGUE_TASK_GUARDIANS_OF_THE_RIFT_10_RIFTS_CLOSED = 15055;
            public static final int LEAGUE_TASK_GUARDIANS_OF_THE_RIFT_25_RIFTS_CLOSED = 15056;
            public static final int LEAGUE_TASK_HARVEST_SOME_SWEETCORN_IN_PRIFDDINAS = 15057;
            public static final int LEAGUE_TASK_HAVE_DREW_CREATE_500_BUCKETS = 15058;
            public static final int LEAGUE_TASK_HAVE_ZAHUR_MAKE_1000_UNFINISHED_POTIONS = 15059;
            public static final int LEAGUE_TASK_MAKE_20_MAGIC_POTIONS = 15060;
            public static final int LEAGUE_TASK_MAKE_50_MENAPHITE_REMEDIES = 15061;
            public static final int LEAGUE_TASK_MAKE_50_TEAK_PLANKS_IN_PRIFDDINAS = 15062;
            public static final int LEAGUE_TASK_MAKE_A_COMBAT_POTION = 15063;
            public static final int LEAGUE_TASK_MINE_15_GRANITE_IN_THE_NECROPOLIS = 15064;
            public static final int LEAGUE_TASK_MINE_200_RUNITE_ORE_IN_TIRANNWN = 15065;
            public static final int LEAGUE_TASK_MINE_25_PURE_ESSENCE = 15066;
            public static final int LEAGUE_TASK_MINE_30_CHUNKS_OF_GRANITE = 15067;
            public static final int LEAGUE_TASK_MINE_5_GEM_ROCKS_IN_A_ROW_WITHOUT_FAILING = 15068;
            public static final int LEAGUE_TASK_MOVE_YOUR_HOUSE_TO_POLLNIVNEACH = 15069;
            public static final int LEAGUE_TASK_MOVE_YOUR_HOUSE_TO_RELLEKKA = 15070;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_FARMING_XP = 15071;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_FIREMAKING_XP = 15072;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_FISHING_XP = 15073;
            public static final int LEAGUE_TASK_OBTAIN_A_MAGIC_SEED_FROM_A_BIRDS_NEST = 15074;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_FLETCHING_XP = 15075;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_HERBLORE_XP = 15076;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_HITPOINTS_XP = 15077;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_HUNTER_XP = 15078;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_MAGIC_XP = 15079;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_MINING_XP = 15080;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_PRAYER_XP = 15081;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_RANGED_XP = 15082;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_RUNECRAFT_XP = 15083;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_SLAYER_XP = 15084;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_SMITHING_XP = 15085;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_STRENGTH_XP = 15086;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_THIEVING_XP = 15087;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_WOODCUTTING_XP = 15088;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_XP_3_NONCOMBAT_SKILLS = 15089;
            public static final int LEAGUE_TASK_OBTAIN_50_MILLION_XP_COMBAT_SKILL = 15090;
            public static final int LEAGUE_TASK_OBTAIN_800_COINS_FROM_COIN_POUCHES_AT_ONCE = 15091;
            public static final int LEAGUE_TASK_BUILD_A_MARBLE_LECTERN = 15092;
            public static final int LEAGUE_TASK_BUILD_AN_ACHIEVEMENT_GALLERY = 15093;
            public static final int LEAGUE_TASK_OBTAIN_A_BIRD_NEST = 15094;
            public static final int LEAGUE_TASK_OBTAIN_A_CASKET_FROM_FISHING = 15095;
            public static final int LEAGUE_TASK_CATCH_50_GREY_CHINCHOMPAS = 15096;
            public static final int LEAGUE_TASK_OBTAIN_A_CLUE_GEODE_WHILE_MINING = 15097;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_PALM_TREE = 15098;
            public static final int LEAGUE_TASK_OBTAIN_A_GEM_WHILE_MINING = 15099;
            public static final int LEAGUE_TASK_OBTAIN_A_HIGH_LEVEL_SEED_FROM_A_BIRDS_NEST = 15100;
            public static final int LEAGUE_TASK_CLEAN_100_GRIMY_DWARF_WEED = 15101;
            public static final int LEAGUE_TASK_OBTAIN_A_SKILLING_PET = 15102;
            public static final int LEAGUE_TASK_COMPLETE_250_LAPS_OF_A_ROOFTOP_AGILITY_COURSE = 15103;
            public static final int ACTION_OBTAIN_AN_OLD_BOOT_FROM_A_FISHING_SPOT = 15104;
            public static final int LEAGUE_TASK_COOK_20_REDBERRY_PIES = 15105;
            public static final int LEAGUE_TASK_OPEN_28_COIN_POUCHES_AT_ONCE = 15106;
            public static final int LEAGUE_TASK_CREATE_A_BLACK_DHIDE_SHIELD = 15107;
            public static final int LEAGUE_TASK_PICKPOCKET_A_CITIZEN = 15108;
            public static final int LEAGUE_TASK_DIG_UP_SOME_MAGIC_ROOTS = 15109;
            public static final int LEAGUE_TASK_PICKPOCKET_A_MASTER_FARMER = 15110;
            public static final int LEAGUE_TASK_PLANT_SEEDS_IN_AN_ALLOTMENT_PATCH = 15111;
            public static final int LEAGUE_TASK_PROTECT_YOUR_CROPS = 15112;
            public static final int LEAGUE_TASK_PURCHASE_A_PLAYER_OWNED_HOUSE = 15113;
            public static final int LEAGUE_TASK_RAKE_A_FLOWER_PATCH = 15114;
            public static final int LEAGUE_TASK_FLETCH_A_MAGIC_SHORTBOW = 15115;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_10 = 15116;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_20 = 15117;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_30 = 15118;
            public static final int LEAGUE_TASK_PICKPOCKET_A_BANDIT_IN_THE_BANDIT_CAMP = 15119;
            public static final int LEAGUE_TASK_PICKPOCKET_A_BULLSEYE_LANTERN_FROM_A_CAVE_GOBLIN = 15120;
            public static final int LEAGUE_TASK_PICKPOCKET_A_DIAMOND_FROM_A_TZHAAR = 15121;
            public static final int LEAGUE_TASK_PICKPOCKET_A_HAM_MEMBER = 15122;
            public static final int LEAGUE_TASK_PICKPOCKET_A_MENAPHITE_THUG_50_TIMES = 15123;
            public static final int LEAGUE_TASK_PICKPOCKET_A_VARROCK_GUARD = 15124;
            public static final int LEAGUE_TASK_PICKPOCKET_A_VYRE_50_TIMES = 15125;
            public static final int LEAGUE_TASK_HARVEST_ANY_HERB_AT_THE_TROLL_STRONGHOLD = 15126;
            public static final int LEAGUE_TASK_MINE_200_SOFT_CLAY_IN_TIRANNWN = 15127;
            public static final int LEAGUE_TASK_RECEIVE_A_TORSTOL_FROM_A_HERBIBOAR = 15128;
            public static final int LEAGUE_TASK_SET_UP_A_MAGIC_BIRD_HOUSE = 15129;
            public static final int LEAGUE_TASK_SET_UP_A_YEW_BIRD_HOUSE = 15130;
            public static final int LEAGUE_TASK_SMELT_A_STEEL_BAR_IN_PORT_PHASMATYS = 15131;
            public static final int LEAGUE_TASK_SMITH_1000_ADAMANT_DART_TIPS = 15132;
            public static final int LEAGUE_TASK_SMITH_1000_MITHRIL_DART_TIPS = 15133;
            public static final int LEAGUE_TASK_SMITH_1000_RUNE_DART_TIPS = 15134;
            public static final int LEAGUE_TASK_SMITH_500_RUNE_DART_TIPS_IN_PRIFDDINAS = 15135;
            public static final int LEAGUE_TASK_PICKPOCKET_AN_ELF_50_TIMES = 15136;
            public static final int LEAGUE_TASK_STEAL_A_DIAMOND_IN_DORGESHKAAN = 15137;
            public static final int LEAGUE_TASK_STEAL_FROM_THE_VARROCK_TEA_STALL = 15138;
            public static final int ACTION_FINISH_WILLOW_ENT_TOTEM_DECORATION = 15139;
            public static final int LEAGUE_TASK_RECEIVE_A_DRAGONSTONE_AMULET_FROM_AN_IMPLING = 15140;
            public static final int LEAGUE_TASK_TAKE_A_SHORTCUT_ACROSS_THE_SHILO_VILLAGE_RIVER = 15141;
            public static final int LEAGUE_TASK_TAKE_THE_ADVANCED_SHORTCUT_TO_THE_COSMIC_ALTAR = 15142;
            public static final int LEAGUE_TASK_GIVE_A_DWARF_REDBERRY_ANTELOPE = 15143;
            public static final int LEAGUE_TASK_MINE_20_MITHRIL_ORE_IN_THE_STONECUTTER_OUTPOST = 15144;
            public static final int LEAGUE_TASK_USE_A_PRAYER_ALTAR_TO_RESTORE_90_PRAYER_IN_PRIFDDINAS = 15145;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_CRYSTAL_TREE = 15146;
            public static final int LEAGUE_TASK_MINE_250_BLESSED_BONE_SHARDS = 15147;
            public static final int LEAGUE_TASK_TURN_IN_A_PYRAMID_TOP_TO_SIMON_TEMPLETON = 15148;
            public static final int LEAGUE_TASK_CHOP_100_TEAK_LOGS_IN_PRIFDDINAS = 15149;
            public static final int LEAGUE_TASK_USE_ENRICHED_BONES_ON_THE_STRANGE_MACHINE = 15150;
            public static final int LEAGUE_TASK_MINE_A_CALCIFIED_DEPOSIT = 15151;
            public static final int LEAGUE_TASK_USE_THE_RANGE_IN_LUMBRIDGE_CASTLE = 15152;
            public static final int LEAGUE_TASK_WORSHIP_THE_ECTOFUNTUS = 15153;
            public static final int LEAGUE_TASK_OPEN_AN_ADEPT_HUNTERS_LOOT_SACK = 15154;
            public static final int LEAGUE_TASK_PAY_AN_URCHIN_FOR_INFORMATION = 15155;
            public static final int LEAGUE_TASK_BURN_20_BLISTERWOOD_LOGS = 15156;
            public static final int LEAGUE_TASK_BURN_20_PYRE_LOGS = 15157;
            public static final int LEAGUE_TASK_INFERIOR_DEMONBANE_DEMON_KILL = 15158;
            public static final int LEAGUE_TASK_CAST_BONES_TO_PEACHES = 15159;
            public static final int LEAGUE_TASK_MINE_50_VOLCANIC_SULPHUR = 15160;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_40 = 15161;
            public static final int LEAGUE_TASK_HARVEST_AN_IRIT_LEAF = 15162;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_5 = 15163;
            public static final int LEAGUE_TASK_CAST_PADDEWWA_TELEPORT = 15164;
            public static final int LEAGUE_TASK_MAKE_50_OAK_PLANKS_IN_KOUREND = 15165;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_50 = 15166;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_60 = 15167;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_70 = 15168;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_80 = 15169;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_90 = 15170;
            public static final int LEAGUE_TASK_PICKPOCKET_A_KNIGHT_OF_VARLAMORE_20_TIMES = 15171;
            public static final int LEAGUE_TASK_MAKE_A_PINEAPPLE_PIZZA = 15172;
            public static final int LEAGUE_TASK_OFFER_AN_EGG_TO_A_SHRINE = 15173;
            public static final int LEAGUE_TASK_REACH_BASE_LEVEL_95 = 15174;
            public static final int LEAGUE_TASK_STEAL_100_VALUABLES = 15175;
            public static final int LEAGUE_TASK_CATCH_30_ORANGE_SALAMANDERS = 15176;
            public static final int LEAGUE_TASK_CHOP_50_MAGIC_LOGS_IN_TIRANNWN = 15177;
            public static final int LEAGUE_TASK_CATCH_50_KARAMBWAN = 15178;
            public static final int LEAGUE_TASK_STEAL_15_HOUSE_KEYS = 15179;
            public static final int LEAGUE_TASK_OBTAIN_A_MARK_OF_GRACE = 15180;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_AGILITY = 15181;
            public static final int LEAGUE_TASK_PICKPOCKET_A_GUARD = 15182;
            public static final int LEAGUE_TASK_COMPLETE_50_LAPS_OF_THE_PRIFDDINAS_AGILITY_COURSE = 15183;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_ATTACK = 15184;
            public static final int LEAGUE_TASK_PICKPOCKET_A_RANARR_SEED = 15185;
            public static final int LEAGUE_TASK_CATCH_A_HERBIBOAR_150_TIMES = 15186;
            public static final int LEAGUE_TASK_CATCH_A_HERBIBOAR_50_TIMES = 15187;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_CONSTRUCTION = 15188;
            public static final int LEAGUE_TASK_STEAL_25_VALUABLES = 15189;
            public static final int LEAGUE_TASK_CATCH_A_KARAMBWANJI = 15190;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_COOKING = 15191;
            public static final int LEAGUE_TASK_STEAL_A_HOUSE_KEY = 15192;
            public static final int LEAGUE_TASK_CATCH_A_SALMON = 15193;
            public static final int LEAGUE_TASK_CATCH_A_SALMON_ON_KARAMJA = 15194;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_CRAFTING = 15195;
            public static final int LEAGUE_TASK_CATCH_A_SWAMP_LIZARD_IN_MORYTANIA = 15196;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_DEFENCE = 15197;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_FARMING = 15198;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_CACTUS = 15199;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_CALQUAT_TREE = 15200;
            public static final int LEAGUE_TASK_COMPLETE_THE_PRIFDDINAS_AGILITY_COURSE = 15201;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_FIREMAKING = 15202;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_MAGIC_TREE_IN_LUMBRIDGE = 15203;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_MAHOGANY_TREE = 15204;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_FISHING = 15205;
            public static final int LEAGUE_TASK_COMPLETE_THE_PRIFDDINAS_AGILITY_COURSE_IN_110 = 15206;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_POTATO_CACTUS = 15207;
            public static final int LEAGUE_TASK_PLANT_100_GOLOVANOVA_SEEDS = 15208;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_FLETCHING = 15209;
            public static final int LEAGUE_TASK_CRAFT_50_DEATH_RUNES = 15210;
            public static final int LEAGUE_TASK_STEAL_FROM_THE_FORTIS_SPICE_STALL = 15211;
            public static final int LEAGUE_TASK_CHOP_100_BLISTERWOOD_LOGS = 15212;
            public static final int LEAGUE_TASK_CRAFT_A_PIECE_OF_CRYSTAL_ARMOUR = 15213;
            public static final int LEAGUE_TASK_CREATE_25_DIVINE_SUPER_ATTACK_POTIONS = 15214;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_HERBLORE = 15215;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_HITPOINTS = 15216;
            public static final int LEAGUE_TASK_RESTORE_14_PRAYER_POINTS_IN_HOSIDIUS = 15217;
            public static final int LEAGUE_TASK_STORE_SOME_BOWSTRINGS_INSIDE_A_BOWSTRING_SPOOL = 15218;
            public static final int LEAGUE_TASK_SMELT_A_MITHRIL_BAR_IN_A_VOLCANIC_FISSURE = 15219;
            public static final int LEAGUE_TASK_SMITH_SHAYZIEN_1 = 15220;
            public static final int LEAGUE_TASK_CREATE_25_DIVINE_SUPER_STRENGTH_POTIONS = 15221;
            public static final int LEAGUE_TASK_SMITH_STEEL_IN_KOUREND_CASTLE = 15222;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_HUNTER = 15223;
            public static final int LEAGUE_TASK_CHOP_A_LOG_FROM_A_POTATO_TREE = 15224;
            public static final int LEAGUE_TASK_SERVE_20_STEWS_IN_THE_MESS_HALL = 15225;
            public static final int LEAGUE_TASK_CHOP_A_SULLIUSCEP_CAP = 15226;
            public static final int LEAGUE_TASK_TELEPORT_TO_CAM_TORUM_USING_A_CALCIFIED_MOTH = 15227;
            public static final int LEAGUE_TASK_STEAL_A_CHISEL = 15228;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_MAGIC = 15229;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_MINING = 15230;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_PRAYER = 15231;
            public static final int LEAGUE_TASK_SMITH_A_STEEL_PLATEBODY = 15232;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_RANGED = 15233;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_RUNECRAFT = 15234;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_SLAYER = 15235;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_SMITHING = 15236;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_STRENGTH = 15237;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_THIEVING = 15238;
            public static final int LEAGUE_TASK_REACH_LEVEL_99_WOODCUTTING = 15239;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_100 = 15240;
            public static final int LEAGUE_TASK_STEAL_1_ARTEFACT = 15241;
            public static final int LEAGUE_TASK_STEAL_15_ARTEFACTS = 15242;
            public static final int LEAGUE_TASK_TELEPORT_TO_CIVITAS_ILLA_FORTIS = 15243;
            public static final int LEAGUE_TASK_COMPLETE_10_LAPS_OF_THE_DRAYNOR_AGILITY_COURSE = 15244;
            public static final int LEAGUE_TASK_USE_THE_POTTERY_OVEN_IN_CIVITAS = 15245;
            public static final int LEAGUE_TASK_COMPLETE_10_LAPS_OF_THE_VARROCK_AGILITY_COURSE = 15246;
            public static final int LEAGUE_TASK_BREAK_DOWN_10_CALCIFIED_DEPOSITS = 15247;
            public static final int LEAGUE_TASK_STEAL_A_COW_BELL_IN_RELLEKKA = 15248;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_1000 = 15249;
            public static final int LEAGUE_TASK_STEAL_A_GOLOVANOVA_FRUIT_TOP = 15250;
            public static final int LEAGUE_TASK_BUILD_A_QUETZAL_LANDING_SITE = 15251;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_1250 = 15252;
            public static final int LEAGUE_TASK_DISSECT_50_SACRED_EELS = 15253;
            public static final int LEAGUE_TASK_CATCH_A_JERBOA = 15254;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_1500 = 15255;
            public static final int LEAGUE_TASK_COMPLETE_THE_AL_KHARID_AGILITY_COURSE = 15256;
            public static final int LEAGUE_TASK_COMPLETE_THE_CANIFIS_AGILITY_COURSE = 15257;
            public static final int LEAGUE_TASK_COMPLETE_THE_DRAYNOR_AGILITY_COURSE = 15258;
            public static final int LEAGUE_TASK_MAKE_50_ANCIENT_BREWS = 15259;
            public static final int LEAGUE_TASK_LIGHT_A_PYRE_SHIP = 15260;
            public static final int LEAGUE_TASK_STEAL_A_FISH = 15261;
            public static final int LEAGUE_TASK_HARVEST_SOME_SNAPE_GRASS_IN_PRIFDDINAS = 15262;
            public static final int LEAGUE_TASK_MAKE_100_MAHOGANY_PLANKS_IN_PRIFDDINAS = 15263;
            public static final int LEAGUE_TASK_STEAL_A_WOODEN_STOCK = 15264;
            public static final int LEAGUE_TASK_MOVE_YOUR_HOUSE_TO_YANILLE = 15265;
            public static final int LEAGUE_TASK_TURN_IN_10_LIBRARY_BOOKS = 15266;
            public static final int LEAGUE_TASK_COMPLETE_THE_VARROCK_AGILITY_COURSE = 15267;
            public static final int LEAGUE_TASK_COMPLETE_THE_WEREWOLF_AGILITY_COURSE = 15268;
            public static final int LEAGUE_TASK_SACRIFICE_SOME_BONES_AT_THE_CHAOS_TEMPLE = 15269;
            public static final int LEAGUE_TASK_CONVERT_2000_DAEYALT_SHARDS_INTO_ESSENCE = 15270;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_1750 = 15271;
            public static final int LEAGUE_TASK_BUILD_A_WAKA_CANOE = 15272;
            public static final int LEAGUE_TASK_CAPTURE_10_SANDWORMS = 15273;
            public static final int LEAGUE_TASK_COOK_100_KARAMBWANS = 15274;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_2000 = 15275;
            public static final int LEAGUE_TASK_CATCH_A_PYRE_FOX = 15276;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_2100 = 15277;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_2200 = 15278;
            public static final int ACTION_CAST_HIGH_LEVEL_ALCHEMY = 15279;
            public static final int LEAGUE_TASK_COOK_20_KARAMBWANS_IN_A_ROW = 15280;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_2277 = 15281;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_250 = 15282;
            public static final int LEAGUE_TASK_COOK_20_SHARKS_IN_DARKMEYER = 15283;
            public static final int LEAGUE_TASK_CATCH_10_PIKE = 15284;
            public static final int LEAGUE_TASK_CATCH_100_LOBSTERS = 15285;
            public static final int LEAGUE_TASK_USE_THE_ABYSS = 15286;
            public static final int LEAGUE_TASK_MAKE_100_ANTIVENOM_POTIONS = 15287;
            public static final int ACTION_TOTAL_LEVEL_666 = 15288;
            public static final int LEAGUE_TASK_REACH_TOTAL_LEVEL_750 = 15289;
            public static final int LEAGUE_TASK_CAST_DEGRIME_SPELL_FULL_CLEAN = 15290;
            public static final int LEAGUE_TASK_CHOP_15_YEW_LOGS_IN_SHAYZIEN = 15291;
            public static final int LEAGUE_TASK_REDECORATE_YOUR_PLAYER_OWNED_HOUSE = 15292;
            public static final int LEAGUE_TASK_COMPLETE_10_HUNTER_RUMOURS = 15293;
            public static final int LEAGUE_TASK_CATCH_25_SARDINES = 15294;
            public static final int LEAGUE_TASK_CATCH_50_SALMON = 15295;
            public static final int LEAGUE_TASK_CATCH_50_SWORDFISH = 15296;
            public static final int LEAGUE_TASK_USE_SOME_ICY_BASALT_TO_TELEPORT_TO_WEISS = 15297;
            public static final int LEAGUE_TASK_CRAFT_50_COSMIC_RUNES = 15298;
            public static final int LEAGUE_TASK_OBTAIN_20_GOLDEN_NUGGETS = 15299;
            public static final int LEAGUE_TASK_CRAFT_50_NATURE_RUNES = 15300;
            public static final int LEAGUE_TASK_CRAFT_50_WATER_RUNES = 15301;
            public static final int LEAGUE_TASK_CRAFT_A_CELESTIAL_SIGNET = 15302;
            public static final int LEAGUE_TASK_SELL_SOME_SILK_TO_A_SILK_TRADER = 15303;
            public static final int LEAGUE_TASK_CRAFT_A_FIRE_RUNE = 15304;
            public static final int LEAGUE_TASK_CRAFT_A_GHORROCK_TELEPORT_TABLET = 15305;
            public static final int LEAGUE_TASK_CRAFT_A_LAVA_RUNE_AT_THE_FIRE_ALTAR = 15306;
            public static final int LEAGUE_TASK_CATCH_A_GREY_CHINCHOMPA = 15307;
            public static final int LEAGUE_TASK_CRAFT_A_RUNE_USING_DAEYALT_ESSENCE = 15308;
            public static final int LEAGUE_TASK_CRAFT_A_SNELM = 15309;
            public static final int LEAGUE_TASK_PICKPOCKET_A_KNIGHT_OF_ARDOUGNE_50_TIMES = 15310;
            public static final int LEAGUE_TASK_CRAFT_AN_AIR_BATTLESTAFF_IN_THE_WILDERNESS = 15311;
            public static final int LEAGUE_TASK_SMELT_A_BRONZE_BAR = 15312;
            public static final int LEAGUE_TASK_SMELT_A_RUNITE_BAR = 15313;
            public static final int LEAGUE_TASK_SMELT_A_STEEL_BAR = 15314;
            public static final int LEAGUE_TASK_SMELT_AN_IRON_BAR = 15315;
            public static final int LEAGUE_TASK_CRAFT_SOME_POTTERY_IN_SOPHANEM = 15316;
            public static final int LEAGUE_TASK_CHOP_25_JUNIPER_LOGS = 15317;
            public static final int LEAGUE_TASK_COMPLETE_10_LAPS_OF_THE_VARLAMORE_AGILITY_COURSE = 15318;
            public static final int LEAGUE_TASK_SMITH_10_STEEL_BOLTS_UNF = 15319;
            public static final int LEAGUE_TASK_CATCH_A_SABRETOOTHED_KEBBIT = 15320;
            public static final int LEAGUE_TASK_COMPLETE_50_LAPS_OF_A_ROOFTOP_AGILITY_COURSE = 15321;
            public static final int LEAGUE_TASK_SMITH_150_IRON_ARROWTIPS = 15322;
            public static final int LEAGUE_TASK_COOK_10_SARDINES = 15323;
            public static final int LEAGUE_TASK_COOK_100_LOBSTERS = 15324;
            public static final int LEAGUE_TASK_COMPLETE_25_HUNTER_RUMOURS = 15325;
            public static final int LEAGUE_TASK_SMITH_250_MITHRIL_BOLTS_UNF = 15326;
            public static final int LEAGUE_TASK_COOK_20_PIKE = 15327;
            public static final int LEAGUE_TASK_CREATE_THE_COLOSSAL_RUNE_POUCH = 15328;
            public static final int LEAGUE_TASK_CREATE_THE_LONG_ROPE_SHORTCUT_IN_DARKMEYER = 15329;
            public static final int LEAGUE_TASK_MINE_25_GOLD_ROCKS_IN_TIRANNWN = 15330;
            public static final int LEAGUE_TASK_BURY_SOME_LAVA_DRAGON_BONES = 15331;
            public static final int LEAGUE_TASK_SMITH_A_DRAGONFIRE_SHIELD = 15332;
            public static final int LEAGUE_TASK_SMITH_A_RUNE_ITEM = 15333;
            public static final int ACTION_SMITH_A_STEEL_2H_SWORD = 15334;
            public static final int LEAGUE_TASK_CATCH_A_SAPPHIRE_GLACIALIS = 15335;
            public static final int LEAGUE_TASK_SNARE_15_TROPICAL_WAGTAILS = 15336;
            public static final int LEAGUE_TASK_DISMANTLE_20_FILLED_BIRD_HOUSES = 15337;
            public static final int LEAGUE_TASK_MOVE_YOUR_HOUSE_TO_PRIFDDINAS = 15338;
            public static final int LEAGUE_TASK_SUCCESSFULLY_HOP_OVER_THE_TRIPWIRE_IN_TIRANNWN = 15339;
            public static final int LEAGUE_TASK_CRAFT_A_CHAOS_RUNE_USING_A_CHAOS_CORE = 15340;
            public static final int LEAGUE_TASK_CRAFT_A_RUBY_AMULET = 15341;
            public static final int LEAGUE_TASK_ENTER_THE_COOKS_GUILD = 15342;
            public static final int LEAGUE_TASK_COMPLETE_THE_FALADOR_AGILITY_COURSE = 15343;
            public static final int LEAGUE_TASK_STEAL_30_ARTEFACTS = 15344;
            public static final int LEAGUE_TASK_COMPLETE_10_FARMING_CONTRACTS = 15345;
            public static final int LEAGUE_TASK_COMPLETE_THE_SHAYZIEN_BASIC_AGILITY_COURSE = 15346;
            public static final int LEAGUE_TASK_STEAL_FROM_A_FUR_STALL = 15347;
            public static final int LEAGUE_TASK_ENTER_THE_FARMING_GUILDS_MID_TIER = 15348;
            public static final int LEAGUE_TASK_SNARE_5_CRIMSON_SWIFT = 15349;
            public static final int LEAGUE_TASK_COMPLETE_A_HUNTER_RUMOUR = 15350;
            public static final int LEAGUE_TASK_SNARE_A_BIRD = 15351;
            public static final int LEAGUE_TASK_SNARE_A_BIRD_20_TIMES = 15352;
            public static final int LEAGUE_TASK_SPIN_A_BALL_OF_WOOL = 15353;
            public static final int LEAGUE_TASK_STEAL_FROM_A_GEM_STALL = 15354;
            public static final int ACTION_STEAL_SOME_BREAD = 15355;
            public static final int LEAGUE_TASK_SUCCESSFULLY_COOK_5_PIECES_OF_FOOD = 15356;
            public static final int LEAGUE_TASK_SUCCESSFULLY_CUT_A_RED_TOPAZ = 15357;
            public static final int LEAGUE_TASK_CREATE_A_QUETZAL_WHISTLE = 15358;
            public static final int LEAGUE_TASK_CAST_RESURRECT_SUPERIOR_GHOST = 15359;
            public static final int LEAGUE_TASK_SUCCESSFULLY_PICKPOCKET_A_CITIZEN_10_TIMES_IN_A_ROW = 15360;
            public static final int LEAGUE_TASK_STEAL_FROM_A_SILVER_STALL = 15361;
            public static final int LEAGUE_TASK_FLETCH_100_YEW_SHORTBOWS = 15362;
            public static final int LEAGUE_TASK_TELEPORT_USING_LAW_RUNES = 15363;
            public static final int LEAGUE_TASK_FLETCH_200_DRAGON_DARTS = 15364;
            public static final int LEAGUE_TASK_TURN_ANY_LOGS_INTO_A_PLANK = 15365;
            public static final int LEAGUE_TASK_ENTER_THE_FARMING_GUILD = 15366;
            public static final int LEAGUE_TASK_ENTER_THE_WOODCUTTING_GUILD = 15367;
            public static final int LEAGUE_TASK_SMITH_AN_IRON_AXE = 15368;
            public static final int LEAGUE_TASK_SMITH_SOME_BRONZE_CLAWS = 15369;
            public static final int LEAGUE_TASK_EAT_SOME_COOKED_DASHING_KEBBIT = 15370;
            public static final int LEAGUE_TASK_EXCHANGE_AN_ENT_SEED_WITH_AN_ENT = 15371;
            public static final int LEAGUE_TASK_FISH_100_INFERNAL_EELS = 15372;
            public static final int LEAGUE_TASK_FISH_A_HOUSE_KEY = 15373;
            public static final int ACTION_FINISH_MAPLE_ENT_TOTEM_DECORATION = 15374;
            public static final int ACTION_FINISH_OAK_ENT_TOTEM_DECORATION = 15375;
            public static final int LEAGUE_TASK_CATCH_A_MOONLIGHT_MOTH_BAREHANDED = 15376;
            public static final int LEAGUE_TASK_GROW_SPIRIT_TREE_ON_KARAMJA = 15377;
            public static final int LEAGUE_TASK_CHOP_DENSE_JUNGLE = 15378;
            public static final int LEAGUE_TASK_CRAFT_A_BODY_RUNE = 15379;
            public static final int LEAGUE_TASK_ENTER_THE_CRAFTING_GUILD = 15380;
            public static final int LEAGUE_TASK_CATCH_A_SNOWY_KNIGHT = 15381;
            public static final int LEAGUE_TASK_COMPLETE_THE_PENGUIN_AGILITY_COURSE = 15382;
            public static final int LEAGUE_TASK_CRAFT_50_ASTRAL_RUNES = 15383;
            public static final int LEAGUE_TASK_STEAL_FROM_A_SPICE_STALL = 15384;
            public static final int LEAGUE_TASK_TRAP_A_SPINED_LARUPIA_IN_THE_FELDIP_HILLS = 15385;
            public static final int LEAGUE_TASK_CATCH_A_MONKFISH = 15386;
            public static final int LEAGUE_TASK_CATCH_A_RED_SALAMANDER = 15387;
            public static final int LEAGUE_TASK_COMPLETE_THE_BARBARIAN_OUTPOST_AGILITY_COURSE = 15388;
            public static final int LEAGUE_TASK_ENTER_THE_FISHING_GUILD = 15389;
            public static final int LEAGUE_TASK_DRINK_KOVACS_GROG = 15390;
            public static final int LEAGUE_TASK_LEARN_HOW_TO_MAKE_SWAMPBARK = 15391;
            public static final int LEAGUE_TASK_LEARN_HOW_TO_MAKE_BLOODBARK = 15392;
            public static final int LEAGUE_TASK_THIEVE_A_DIAMOND_FROM_A_GEM_STALL_IN_TIRANNWN = 15393;
            public static final int LEAGUE_TASK_THIEVE_A_TIARA_FROM_A_SILVER_STALL_IN_TIRANNWN = 15394;
            public static final int LEAGUE_TASK_CATCH_A_CRYSTAL_IMPLING = 15395;
            public static final int LEAGUE_TASK_CHECK_A_GROWN_PAPAYA_TREE_IN_LLETYA = 15396;
            public static final int LEAGUE_TASK_CHOP_100_MAPLES_IN_TIRANNWN = 15397;
            public static final int LEAGUE_TASK_CATCH_A_BLACK_CHINCHOMPA = 15398;
            public static final int LEAGUE_TASK_CATCH_A_BLACK_SALAMANDER = 15399;
            public static final int LEAGUE_TASK_COMPLETE_THE_WILDERNESS_AGILITY_COURSE = 15400;
            public static final int LEAGUE_TASK_CROSS_PILLAR_SHORTCUT_REVENANT_CAVES = 15401;
            public static final int LEAGUE_TASK_MINE_RUNITE_DRAGON_PICKAXE_IN_WILDERNESS = 15402;
            public static final int LEAGUE_TASK_CRAFT_100_AETHER_RUNES = 15403;
        }
    }

    public static final class BingoEvents {
        private BingoEvents() {}

        public static final int ID = 119;
        public static final int COL_GRID = 0;
        public static final int COL_EVENT_NAME = 1;
        public static final int COL_EVENT_END = 2;
        public static final int COL_REWARD_LEAGUE_RELICS = 3;
        public static final int COL_REWARD_ONCE_PER_EVENT_COUNT = 4;
        public static final int COL_EVENT_POINT_OBJECT_GRANT = 5;
        public static final int COL_EVENT_POINT_OBJECT_GRANT_NEGATIVE = 6;
        public static final int COL_EVENT_MAINGAME_REWARD = 7;

        public static final class Row {
            private Row() {}

            public static final int JAGEX_BINGO_EVENT1 = 6926;
        }
    }

    public static final class BingoGrids {
        private BingoGrids() {}

        public static final int ID = 120;
        public static final int COL_GRID_LAYOUT = 0;
        public static final int COL_BINGO_POINTS_TO_PROGRESS = 1;
        public static final int COL_CHALLENGE = 2;
        public static final int COL_REWARD_SELECTION = 3;

        public static final class Row {
            private Row() {}

            public static final int EVENT1_GRID1 = 6927;
        }
    }

    public static final class RewardSelection {
        private RewardSelection() {}

        public static final int ID = 121;
        public static final int COL_REWARD = 0;
        public static final int COL_DISPLAY_GRAPHIC = 1;
        public static final int COL_DISPLAY_OBJECT = 2;

        public static final class Row {
            private Row() {}

            public static final int BINGO_RS_7X7_ROW_0 = 6863;
            public static final int BINGO_RS_7X7_ROW_1 = 6864;
            public static final int BINGO_RS_7X7_ROW_2 = 6865;
            public static final int BINGO_RS_7X7_ROW_3 = 6866;
            public static final int BINGO_RS_7X7_ROW_4 = 6867;
            public static final int BINGO_RS_7X7_ROW_5 = 6868;
            public static final int BINGO_RS_7X7_ROW_6 = 6869;
            public static final int BINGO_RS_7X7_COL_0 = 6870;
            public static final int BINGO_RS_7X7_COL_1 = 6871;
            public static final int BINGO_RS_7X7_COL_2 = 6872;
            public static final int BINGO_RS_7X7_COL_3 = 6873;
            public static final int BINGO_RS_7X7_COL_4 = 6874;
            public static final int BINGO_RS_7X7_COL_5 = 6875;
            public static final int BINGO_RS_7X7_COL_6 = 6876;
            public static final int BINGO_RS_7X7_ROW_0_COL_0 = 6877;
            public static final int BINGO_RS_7X7_ROW_0_COL_1 = 6878;
            public static final int BINGO_RS_7X7_ROW_0_COL_2 = 6879;
            public static final int BINGO_RS_7X7_ROW_0_COL_3 = 6880;
            public static final int BINGO_RS_7X7_ROW_0_COL_4 = 6881;
            public static final int BINGO_RS_7X7_ROW_0_COL_5 = 6882;
            public static final int BINGO_RS_7X7_ROW_0_COL_6 = 6883;
            public static final int BINGO_RS_7X7_ROW_1_COL_0 = 6884;
            public static final int BINGO_RS_7X7_ROW_1_COL_1 = 6885;
            public static final int BINGO_RS_7X7_ROW_1_COL_2 = 6886;
            public static final int BINGO_RS_7X7_ROW_1_COL_3 = 6887;
            public static final int BINGO_RS_7X7_ROW_1_COL_4 = 6888;
            public static final int BINGO_RS_7X7_ROW_1_COL_5 = 6889;
            public static final int BINGO_RS_7X7_ROW_1_COL_6 = 6890;
            public static final int BINGO_RS_7X7_ROW_2_COL_0 = 6891;
            public static final int BINGO_RS_7X7_ROW_2_COL_1 = 6892;
            public static final int BINGO_RS_7X7_ROW_2_COL_2 = 6893;
            public static final int BINGO_RS_7X7_ROW_2_COL_3 = 6894;
            public static final int BINGO_RS_7X7_ROW_2_COL_4 = 6895;
            public static final int BINGO_RS_7X7_ROW_2_COL_5 = 6896;
            public static final int BINGO_RS_7X7_ROW_2_COL_6 = 6897;
            public static final int BINGO_RS_7X7_ROW_3_COL_0 = 6898;
            public static final int BINGO_RS_7X7_ROW_3_COL_1 = 6899;
            public static final int BINGO_RS_7X7_ROW_3_COL_2 = 6900;
            public static final int BINGO_RS_7X7_ROW_3_COL_3 = 6901;
            public static final int BINGO_RS_7X7_ROW_3_COL_4 = 6902;
            public static final int BINGO_RS_7X7_ROW_3_COL_5 = 6903;
            public static final int BINGO_RS_7X7_ROW_3_COL_6 = 6904;
            public static final int BINGO_RS_7X7_ROW_4_COL_0 = 6905;
            public static final int BINGO_RS_7X7_ROW_4_COL_1 = 6906;
            public static final int BINGO_RS_7X7_ROW_4_COL_2 = 6907;
            public static final int BINGO_RS_7X7_ROW_4_COL_3 = 6908;
            public static final int BINGO_RS_7X7_ROW_4_COL_4 = 6909;
            public static final int BINGO_RS_7X7_ROW_4_COL_5 = 6910;
            public static final int BINGO_RS_7X7_ROW_4_COL_6 = 6911;
            public static final int BINGO_RS_7X7_ROW_5_COL_0 = 6912;
            public static final int BINGO_RS_7X7_ROW_5_COL_1 = 6913;
            public static final int BINGO_RS_7X7_ROW_5_COL_2 = 6914;
            public static final int BINGO_RS_7X7_ROW_5_COL_3 = 6915;
            public static final int BINGO_RS_7X7_ROW_5_COL_4 = 6916;
            public static final int BINGO_RS_7X7_ROW_5_COL_5 = 6917;
            public static final int BINGO_RS_7X7_ROW_5_COL_6 = 6918;
            public static final int BINGO_RS_7X7_ROW_6_COL_0 = 6919;
            public static final int BINGO_RS_7X7_ROW_6_COL_1 = 6920;
            public static final int BINGO_RS_7X7_ROW_6_COL_2 = 6921;
            public static final int BINGO_RS_7X7_ROW_6_COL_3 = 6922;
            public static final int BINGO_RS_7X7_ROW_6_COL_4 = 6923;
            public static final int BINGO_RS_7X7_ROW_6_COL_5 = 6924;
            public static final int BINGO_RS_7X7_ROW_6_COL_6 = 6925;
            public static final int EMPTY_REWARD_SELECTION = 6928;
        }
    }

    public static final class Reward {
        private Reward() {}

        public static final int ID = 122;
        public static final int COL_NAME = 0;
        public static final int COL_DESC = 1;
        public static final int COL_SHOW_DERIVED_DESC = 2;
        public static final int COL_DISPLAY_GRAPHIC = 3;
        public static final int COL_DISPLAY_OBJECT = 4;
        public static final int COL_XP_BOOST_PERCENTAGE_ALL_STAT = 5;
        public static final int COL_XP_BOOST_PERCENTAGE = 6;
        public static final int COL_QUEST_UNLOCK = 7;
        public static final int COL_OBJECT = 8;
        public static final int COL_EVENT_GENILAMP_OBJVARS = 9;
        public static final int COL_ACHIEVEMENT_DIARY_UNLOCK = 10;
        public static final int COL_COMBAT_ACHIEVEMENT_UNLOCK = 11;
        public static final int COL_LEAGUE_RELIC = 12;
        public static final int COL_COMBAT_MASTERY_TIER = 13;
        public static final int COL_RECLAIMABLE = 14;
        public static final int COL_ONCE_PER_EVENT = 15;
        public static final int COL_CONFIRM_DIALOGUE = 16;
        public static final int COL_CONFIRM_WARNING_TEXT = 17;
        public static final int COL_GRANT_ARTICLE = 18;
        public static final int COL_UNLOCK_SPOTANIM = 19;

        public static final class Row {
            private Row() {}

            public static final int REWARD_NYI = 6929;
            public static final int REWARD_SCYTHE_OF_VITUR = 6930;
            public static final int REWARD_TWISTED_BOW = 6931;
            public static final int REWARD_TUMEKENS_SHADOW = 6932;
            public static final int REWARD_DIVINE_RUNE_POUCH = 6933;
            public static final int REWARD_DAMNED_GLOVES = 6934;
            public static final int REWARD_XP_LAMP_25K = 6935;
            public static final int REWARD_XP_LAMP_100K = 6936;
            public static final int REWARD_AVERNIC_TREADS_MAX = 6937;
            public static final int REWARD_AMULET_OF_THE_MONARCHS = 6938;
            public static final int REWARD_SUNLIGHT_SPEAR_AND_SUNLIT_BRACERS = 6939;
            public static final int REWARD_EMPEROR_RING = 6940;
            public static final int REWARD_THE_DOGSWORD = 6941;
            public static final int REWARD_DRYGORE_BLOWPIPE = 6942;
            public static final int REWARD_THOUSAND_DRAGON_WARD = 6943;
            public static final int REWARD_THUNDER_KHOPESH = 6944;
            public static final int REWARD_DEVILS_ELEMENT = 6945;
            public static final int REWARD_XP_BOOST_ALL_200 = 6946;
            public static final int REWARD_POWER_MINER = 6947;
            public static final int REWARD_LUMBERJACK = 6948;
            public static final int REWARD_ANIMAL_WRANGLER = 6949;
            public static final int REWARD_GOLDEN_GOD = 6950;
            public static final int REWARD_DODGY_DEALS = 6951;
            public static final int REWARD_CLUE_COMPASS = 6952;
            public static final int REWARD_FAIRY_FLIGHT = 6953;
            public static final int REWARD_BANKERS_NOTE = 6954;
            public static final int REWARD_PRODUCTION_PRODIGY = 6955;
            public static final int REWARD_LAST_RECALL = 6956;
            public static final int REWARD_FRIENDLY_FORAGER = 6957;
            public static final int REWARD_CORNER_CUTTER = 6958;
            public static final int REWARD_UNNATURAL_SELECTION = 6959;
            public static final int REWARD_LAST_STAND = 6960;
            public static final int REWARD_SLAYER_MASTER = 6961;
            public static final int REWARD_GRIMOIRE = 6962;
            public static final int REWARD_SPECIALIST = 6963;
            public static final int REWARD_BANK_HEIST = 6964;
            public static final int REWARD_GUARDIAN = 6965;
            public static final int REWARD_BOTTOMLESS_BREW = 6966;
            public static final int REWARD_SPELLS_DO_NOT_CONSUME_RUNES = 6967;
            public static final int REWARD_MINIMUM_POTENTIAL = 6968;
            public static final int REWARD_SPIKY_AURA = 6969;
            public static final int REWARD_GATHERER = 6970;
            public static final int REWARD_CRAFTSMAN = 6971;
            public static final int REWARD_COMBATANT = 6972;
            public static final int REWARD_BATTLEHAT = 6973;
            public static final int REWARD_CORRUPTED_SHARK = 6974;
            public static final int REWARD_FARMERS_FORTUNE = 6975;
            public static final int REWARD_CRIT_ALL_STYLES = 6976;
            public static final int REWARD_INCREASE_ALL_COMBAT_MASTERIES = 6977;
            public static final int REWARD_AUTO_COMPLETE_ALL_ACHIEVEMENT_DIARIES = 6978;
            public static final int REWARD_AUTO_COMPLETE_ALL_COMBAT_ACHIEVEMENTS = 6979;
            public static final int REWARD_QUEST_DESERTTREASURE2 = 6980;
            public static final int REWARD_QUEST_DRAGONSLAYER2 = 6981;
            public static final int REWARD_QUEST_MONKEYMADNESS = 6982;
            public static final int REWARD_QUEST_SONGOFTHEELVES = 6983;
            public static final int REWARD_QUEST_SINSOFTHEFATHER = 6984;
            public static final int REWARD_QUEST_KINGDOMDIVIDED = 6985;
            public static final int REWARD_QUEST_PERILOUSMOONS = 6986;
            public static final int REWARD_QUEST_FINALDAWN = 6987;
            public static final int REWARD_QUEST_WHILEGUTHIXSLEEPS = 6988;
        }
    }

    public static final class TeleportGeneric {
        private TeleportGeneric() {}

        public static final int ID = 123;
        public static final int COL_NAME = 0;
        public static final int COL_DESTINATION = 1;
        public static final int COL_BESPOKE_REQUIREMENTS = 2;
        public static final int COL_BESPOKE_SIDEEFFECTS = 3;
        public static final int COL_FORCED_ALTERNATE_DESTINATION = 4;
        public static final int COL_QUEST_REQUIREMENT_PARTIAL = 5;
        public static final int COL_QUEST_REQUIREMENT_FULL = 6;
        public static final int COL_SKILL_REQUIREMENT = 7;
        public static final int COL_REGION = 8;
    }

    public static final class PollFilters {
        private PollFilters() {}

        public static final int ID = 124;
        public static final int COL_CATEGORY = 0;
        public static final int COL_ID = 1;
        public static final int COL_OPBASE = 2;
        public static final int COL_ICON = 3;
        public static final int COL_DESC = 4;

        public static final class Row {
            private Row() {}

            public static final int POLL_STATUS_NOTOPEN = 7029;
            public static final int POLL_STATUS_HASVOTED = 7030;
            public static final int POLL_STATUS_INELIGIBLE = 7031;
            public static final int POLL_STATUS_CANVOTE = 7032;
        }
    }

    public static final class UiHighlightingFxPulse {
        private UiHighlightingFxPulse() {}

        public static final int ID = 125;
        public static final int COL_TRANSPARENCY_RANGE = 0;
        public static final int COL_STEP_DURATION = 1;
    }

    public static final class UiHighlightingStyleBorder {
        private UiHighlightingStyleBorder() {}

        public static final int ID = 126;
        public static final int COL_WIDTH = 0;
        public static final int COL_COLOUR = 1;
        public static final int COL_PADDING = 2;
        public static final int COL_FX = 3;

        public static final class Row {
            private Row() {}

            public static final int UI_HIGHLIGHTING_STYLE_TUTORIAL_CTA = 7034;
            public static final int UI_HIGHLIGHTING_STYLE_TUTORIAL_ALERT = 7036;
        }
    }

    public static final class RestrictContentObj {
        private RestrictContentObj() {}

        public static final int ID = 127;
        public static final int COL_RELATED_CONTENT = 0;
        public static final int COL_CATEGORY = 1;
        public static final int COL_OBJ = 2;

        public static final class Row {
            private Row() {}

            public static final int RESTRICT_CONTENT_SAILING_OBJ_CATEGORIES = 7072;
        }
    }

    public static final class MusicAreaGroup {
        private MusicAreaGroup() {}

        public static final int ID = 128;
        public static final int COL_AREA = 0;
    }

    public static final class Amenity {
        private Amenity() {}

        public static final int ID = 129;
        public static final int COL_PRINT_NAME = 0;
        public static final int COL_INLINE_NAME = 1;
        public static final int COL_BEHAVIOUR_ID = 2;
        public static final int COL_STATREQ = 3;
        public static final int COL_MATERIALS = 4;
        public static final int COL_NEEDS_HAMMER = 5;
        public static final int COL_NEEDS_SAW = 6;
        public static final int COL_MATERIALS_XP_SCALE = 7;
        public static final int COL_BUILD_XP = 8;
        public static final int COL_HAMMER_ANIM = 9;
        public static final int COL_CUSTOM_ANIM = 10;
        public static final int COL_DUMMYOBJ = 11;

        public static final class Row {
            private Row() {}

            public static final int AMENITY_WATER_PUMP = 7109;
            public static final int AMENITY_COOKING_POT = 7110;
            public static final int AMENITY_SPINNING_WHEEL = 7111;
            public static final int AMENITY_POTTERY_WHEEL = 7112;
            public static final int AMENITY_LOOM = 7113;
            public static final int AMENITY_POTTERY_OVEN = 7114;
            public static final int AMENITY_ANVIL = 7115;
            public static final int AMENITY_ROWBOAT = 7116;
            public static final int AMENITY_FURNACE = 7117;
            public static final int AMENITY_BANKCHEST = 7118;
        }
    }

    public static final class FletchingBlowpipeCrafting {
        private FletchingBlowpipeCrafting() {}

        public static final int ID = 142;
        public static final int COL_LEVEL_REQUIRED = 0;
        public static final int COL_XP_GIVEN = 1;
        public static final int COL_LOG_RESOURCE = 2;
        public static final int COL_LOG_QUANTITY = 3;
        public static final int COL_SECONDARY_RESOURCE = 4;
        public static final int COL_SECONDARY_QUANTITY = 5;
    }

    public static final class SailingBtTrialCore {
        private SailingBtTrialCore() {}

        public static final int ID = 143;
        public static final int COL_TRIAL_ID = 0;
        public static final int COL_TRIAL_NAME = 1;
        public static final int COL_SAILING_REQ = 2;
        public static final int COL_CREWMATES_ALLOWED = 3;
        public static final int COL_COLLECTABLE_BONUS_XP = 4;
        public static final int COL_COLLECTABLE_INTERACTION_RANGE = 5;
        public static final int COL_PETRATE = 6;
        public static final int COL_PAINT_ODDS = 7;
        public static final int COL_TRACKER_ICON1 = 8;
        public static final int COL_TRACKER_ICON2 = 9;
        public static final int COL_TRACKER_ICON3 = 10;
        public static final int COL_TRACKER_ICON4 = 11;
        public static final int COL_PRIMARY_REQ_DESC = 12;
        public static final int COL_SECONDARY_REQ_DESC = 13;
        public static final int COL_TRIAL_BOUNDARY_SW = 14;
        public static final int COL_TRIAL_BOUNDARY_NE = 15;
        public static final int COL_TRIAL_BOUNDARY_AREA = 16;
        public static final int COL_TRIAL_START_COORD = 17;
        public static final int COL_TRIAL_START_FACINGANGLE = 18;
        public static final int COL_TRIAL_HINTARROW_INITIAL = 19;
        public static final int COL_COLLECTABLE_LOC = 20;
        public static final int COL_COLLECTABLE_OBJ = 21;
        public static final int COL_RANK_DATA = 22;
        public static final int COL_REWARD_SWORDFISH = 23;
        public static final int COL_REWARD_SHARK = 24;
        public static final int COL_REWARD_MARLIN = 25;
    }

    public static final class SailingBtGwenithGlideCrystalData {
        private SailingBtGwenithGlideCrystalData() {}

        public static final int ID = 144;
        public static final int COL_CRYSTAL_LOC = 0;
        public static final int COL_CRYSTAL_COORD = 1;
        public static final int COL_FADE_DATA = 2;
        public static final int COL_PORTALS = 3;
    }

    public static final class SailingBtGwenithGlidePortals {
        private SailingBtGwenithGlidePortals() {}

        public static final int ID = 145;
        public static final int COL_AREA = 0;
        public static final int COL_DESTINATION = 1;
        public static final int COL_DESTINATION_ALT = 2;
        public static final int COL_FLAG_DESTINATION_ALT = 3;
        public static final int COL_BARRACUDA_OBJECTIVE = 4;
        public static final int COL_BARRACUDA_RANK_MIN = 5;
        public static final int COL_BARRACUDA_RANK_MIN_DESTINATION_OVERRIDE = 6;
    }

    public static final class SailingBtJubblyJivePillars {
        private SailingBtJubblyJivePillars() {}

        public static final int ID = 146;
        public static final int COL_ID = 0;
        public static final int COL_CLICKBOX_COORD = 1;
        public static final int COL_JUBBLY_SPAWN = 2;
        public static final int COL_PROJANIM_DESTINATION = 3;
    }

    public static final class BoatLocationSpriteData {
        private BoatLocationSpriteData() {}

        public static final int ID = 147;
        public static final int COL_LOCATION = 0;
        public static final int COL_SPRITE = 1;
        public static final int COL_NAME = 2;
    }

    public static final class BoatFacilitiesDefaultSpriteData {
        private BoatFacilitiesDefaultSpriteData() {}

        public static final int ID = 148;
        public static final int COL_FACILITY_SUBTYPE = 0;
        public static final int COL_SPRITE = 1;
    }

    public static final class BoatSelectionType {
        private BoatSelectionType() {}

        public static final int ID = 149;
        public static final int COL_TYPE = 0;
        public static final int COL_TITLE = 1;
        public static final int COL_SELECTION_TEXT = 2;
        public static final int COL_SELECTION_ENABLED = 3;
        public static final int COL_SHOW_RECENT_BOAT = 4;
        public static final int COL_SHOW_RETRIEVAL_COST = 5;
        public static final int COL_SHOW_BOAT_HEALTH = 6;
        public static final int COL_SHOW_EMPTY_BOAT_SLOTS = 7;
        public static final int COL_SHOW_CARGO_HOLD = 8;
        public static final int COL_BOTTLED_SHIPS_NOT_ALLOWED = 9;
        public static final int COL_CAPSIZED_SHIPS_NOT_ALLOWED = 10;
        public static final int COL_LOST_SHIPS_NOT_ALLOWED = 11;
        public static final int COL_ONLY_BOATS_AT_CURRENT_PORT = 12;
        public static final int COL_REQUIRES_TELEPORT_FOCUS = 13;
        public static final int COL_REQUIRES_GREATER_TELEPORT_FOCUS = 14;
        public static final int COL_REQUIRES_EMPTY_CARGO_HOLD = 15;
    }

    public static final class SailingChanceEncounters {
        private SailingChanceEncounters() {}

        public static final int ID = 150;
        public static final int COL_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_WEIGHTING = 2;
    }

    public static final class SailingChanceEncounterRescueNpcs {
        private SailingChanceEncounterRescueNpcs() {}

        public static final int ID = 151;
        public static final int COL_ADRIFT_NPC = 0;
        public static final int COL_PASSENGER_NPC = 1;
        public static final int COL_DIALOGUE_ADRIFT = 2;
        public static final int COL_DIALOGUE_PASSENGER_NPC = 3;
        public static final int COL_DIALOGUE_PASSENGER_PLAYER = 4;
        public static final int COL_DIALOGUE_COMPLETE = 5;
        public static final int COL_MES_BOARDING = 6;
        public static final int COL_MES_COMPLETE = 7;
        public static final int COL_PICKUP_XP = 8;
        public static final int COL_RESCUE_XP = 9;
    }

    public static final class SailingChanceEncountersLostGoods {
        private SailingChanceEncountersLostGoods() {}

        public static final int ID = 152;
        public static final int COL_ADRIFT_NPC = 0;
        public static final int COL_SAILING_LEVEL = 1;
        public static final int COL_XP = 2;
    }

    public static final class SailingChanceEncountersLostGoodsResource {
        private SailingChanceEncountersLostGoodsResource() {}

        public static final int ID = 153;
        public static final int COL_REWARD = 0;
        public static final int COL_MES = 1;
    }

    public static final class SailingChartingCore {
        private SailingChartingCore() {}

        public static final int ID = 154;
        public static final int COL_UNIQUE_ID = 0;
        public static final int COL_HINT = 1;
        public static final int COL_SAILING_SEA = 2;
        public static final int COL_SAILING_SEA_SECONDARY = 3;
        public static final int COL_GIVE_REPEAT_XP = 4;
        public static final int COL_CHARTING_TYPE = 5;
        public static final int COL_HAZARD = 6;
        public static final int COL_REQUIRED_DOCK = 7;
    }

    public static final class SailingChartingGeneric {
        private SailingChartingGeneric() {}

        public static final int ID = 155;
        public static final int COL_SAILING_CHARTING_CORE = 0;
        public static final int COL_FEEDBACK_STRING = 1;
        public static final int COL_ALREADY_CHARTED_FEEDBACK = 2;
    }

    public static final class SailingChartingSpyglass {
        private SailingChartingSpyglass() {}

        public static final int ID = 156;
        public static final int COL_SAILING_CHARTING_CORE = 0;
        public static final int COL_INZONE = 1;
        public static final int COL_CAMERA_CONTROL = 2;
    }

    public static final class SailingChartingCurrentDuck {
        private SailingChartingCurrentDuck() {}

        public static final int ID = 157;
        public static final int COL_SAILING_CHARTING_CORE = 0;
        public static final int COL_INZONE = 1;
        public static final int COL_DUCK_ROUTE = 2;
        public static final int COL_DUCK_LIFETIME = 3;
    }

    public static final class SailingChartingDrinkCrate {
        private SailingChartingDrinkCrate() {}

        public static final int ID = 158;
        public static final int COL_SAILING_CHARTING_CORE = 0;
        public static final int COL_COORD = 1;
        public static final int COL_DRINK_NAMEDOBJ = 2;
        public static final int COL_DRINK_OBJ = 3;
        public static final int COL_REPEATABLE = 4;
        public static final int COL_EXTRA_WARNING = 5;
    }

    public static final class SailingChartingWeatherTroll {
        private SailingChartingWeatherTroll() {}

        public static final int ID = 159;
        public static final int COL_SAILING_CHARTING_CORE = 0;
        public static final int COL_WEATHER_TROLL_COORD = 1;
        public static final int COL_WEATHER_TROLL_FACING_DIRECTION = 2;
        public static final int COL_WEATHER_DATA_COORD = 3;
        public static final int COL_CLOCKWISE_WINDS = 4;
    }

    public static final class SailingChartingMermaidGuide {
        private SailingChartingMermaidGuide() {}

        public static final int ID = 160;
        public static final int COL_SAILING_CHARTING_CORE = 0;
        public static final int COL_COORD = 1;
        public static final int COL_HINT = 2;
        public static final int COL_REQUIRED_ITEMS = 3;
        public static final int COL_QUANTITIES_NEEDED = 4;
        public static final int COL_REQUIRED_ALTERNATIVES = 5;
        public static final int COL_NPC = 6;
    }

    public static final class SailingChartingToolRecovery {
        private SailingChartingToolRecovery() {}

        public static final int ID = 161;
        public static final int COL_NAME = 0;
        public static final int COL_INLINE_NAME = 1;
        public static final int COL_TOOL = 2;
        public static final int COL_UNIQUE_ID = 3;
    }

    public static final class SailingCombatFacility {
        private SailingCombatFacility() {}

        public static final int ID = 162;
        public static final int COL_TYPE_ID = 0;
        public static final int COL_FACILITY_LOC = 1;
        public static final int COL_DUMMY_OBJ = 2;
        public static final int COL_SAILING_LEVEL_REQUIRED = 3;
        public static final int COL_RANGED_LEVEL_REQUIRED = 4;
        public static final int COL_ACCURACY = 5;
        public static final int COL_DAMAGE = 6;
        public static final int COL_ACCURACY_HEAVY = 7;
        public static final int COL_DAMAGE_HEAVY = 8;
        public static final int COL_AMMUNITION_MAX_TIER = 9;
        public static final int COL_ATTACK_RATE = 10;
        public static final int COL_DAMAGE_TYPE = 11;
        public static final int COL_ATTACK_RANGE = 12;
        public static final int COL_HUMAN_ATTACK_ANIM = 13;
        public static final int COL_HUMAN_IDLE_ANIM = 14;
        public static final int COL_HUMAN_ENGAGE_ANIM = 15;
        public static final int COL_IDLE_ANIM_DISENGAGED = 16;
        public static final int COL_IDLE_ANIM_ENGAGED = 17;
        public static final int COL_ENGAGE_ANIM = 18;
        public static final int COL_ATTACK_ANIM = 19;
        public static final int COL_ATTACK_SPOTANIM_NORTH = 20;
        public static final int COL_ATTACK_SPOTANIM_EAST = 21;
        public static final int COL_ATTACK_SPOTANIM_SOUTH = 22;
        public static final int COL_ATTACK_SPOTANIM_WEST = 23;
        public static final int COL_ATTACK_SOUND = 24;
    }

    public static final class SailingCombatSupportFacility {
        private SailingCombatSupportFacility() {}

        public static final int ID = 163;
        public static final int COL_FACILITY_LOC_INACTIVE = 0;
        public static final int COL_FACILITY_LOC_ACTIVE = 1;
        public static final int COL_LEVEL_REQUIRED = 2;
        public static final int COL_HUMAN_INTERACTION_ANIM = 3;
        public static final int COL_INTERACTION_ANIM = 4;
        public static final int COL_ACTIVATE_SFX = 5;
        public static final int COL_DEACTIVATE_SFX = 6;
        public static final int COL_CHARGE_SFX = 7;
        public static final int COL_INACTIVE_STATS = 8;
        public static final int COL_ACTIVE_STATS = 9;
        public static final int COL_UPKEEP_COST = 10;
        public static final int COL_INTERACT_VERB_ACTIVATE = 11;
        public static final int COL_INTERACT_VERB_DEACTIVATE = 12;
    }

    public static final class SailingBoatFacilityStats {
        private SailingBoatFacilityStats() {}

        public static final int ID = 164;
        public static final int COL_BOAT_HP_MAX = 0;
        public static final int COL_BOAT_DEFENCE = 1;
        public static final int COL_BOAT_STABDEFENCE = 2;
        public static final int COL_BOAT_SLASHDEFENCE = 3;
        public static final int COL_BOAT_CRUSHDEFENCE = 4;
        public static final int COL_BOAT_HEAVYRANGEDDEFENCE = 5;
        public static final int COL_BOAT_STANDARDRANGEDDEFENCE = 6;
        public static final int COL_BOAT_LIGHTRANGEDDEFENCE = 7;
        public static final int COL_BOAT_MAGICDEFENCE = 8;
        public static final int COL_BOAT_MAGICLEVEL = 9;
        public static final int COL_BOAT_ARMOUR = 10;
        public static final int COL_BOAT_AIRMAGICARMOUR = 11;
        public static final int COL_BOAT_EARTHMAGICARMOUR = 12;
        public static final int COL_BOAT_FIREMAGICARMOUR = 13;
        public static final int COL_BOAT_WATERMAGICARMOUR = 14;
        public static final int COL_BOAT_LIGHTRANGEDARMOUR = 15;
        public static final int COL_BOAT_STANDARDRANGEDARMOUR = 16;
        public static final int COL_BOAT_HEAVYRANGEDARMOUR = 17;
        public static final int COL_BOAT_STABARMOUR = 18;
        public static final int COL_BOAT_SLASHARMOUR = 19;
        public static final int COL_BOAT_CRUSHARMOUR = 20;
        public static final int COL_BOAT_STORMRESISTANCE = 21;
        public static final int COL_BOAT_RAPIDRESISTANCE = 22;
        public static final int COL_BOAT_CRYSTALHELM_RESISTANCE = 23;
        public static final int COL_BOAT_BASESPEED = 24;
        public static final int COL_BOAT_SPEEDCAP = 25;
        public static final int COL_BOAT_ACCELERATION = 26;
        public static final int COL_BOAT_SPEEDBOOST_DURATION = 27;
        public static final int COL_BOAT_CARGOHOLD_SIZE = 28;
        public static final int COL_BOAT_FETID_WATER_RESISTANT = 29;
        public static final int COL_BOAT_CRYSTAL_FLECKED_RESISTANT = 30;
        public static final int COL_BOAT_TANGLED_KELP_RESISTANT = 31;
        public static final int COL_BOAT_ICY_SEAS_RESISTANT = 32;
        public static final int COL_BOAT_MAX_WIND_MOTES = 33;
        public static final int COL_BOAT_ADDITIONAL_RECOVERY_COST_PERCENTAGE = 34;
        public static final int COL_BOAT_AMMO_SAVE_PERCENTAGE = 35;
        public static final int COL_BOAT_AUTO_REPAIR_RATE = 36;
    }

    public static final class SailingCombatFacilityAmmunition {
        private SailingCombatFacilityAmmunition() {}

        public static final int ID = 165;
        public static final int COL_AMMUNITION_ID = 0;
        public static final int COL_AMMUNITION_OBJ = 1;
        public static final int COL_AMMUNITION_TIER = 2;
        public static final int COL_AMMUNITION_ACCURACY = 3;
        public static final int COL_AMMUNITION_STRENGTH = 4;
        public static final int COL_AMMUNITION_ACCURACY_HEAVY = 5;
        public static final int COL_AMMUNITION_STRENGTH_HEAVY = 6;
        public static final int COL_AMMUNITION_TYPE = 7;
        public static final int COL_TRAVEL_ANIM = 8;
        public static final int COL_IMPACT_ANIM = 9;
    }

    public static final class SailingBoat {
        private SailingBoat() {}

        public static final int ID = 166;
        public static final int COL_TYPE_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_INLINE_NAME = 2;
        public static final int COL_DISPLAYNAME = 3;
        public static final int COL_LEVEL_REQUIRED = 4;
        public static final int COL_PLAYER_CAPACITY = 5;
        public static final int COL_BOAT_SIZE = 6;
        public static final int COL_SIZE_DESC = 7;
        public static final int COL_COMBINED_NAVIGATION = 8;
        public static final int COL_ORIGIN = 9;
        public static final int COL_ENTRY = 10;
        public static final int COL_WIDTH = 11;
        public static final int COL_HEIGHT = 12;
        public static final int COL_TILE_WIDTH = 13;
        public static final int COL_TILE_HEIGHT = 14;
        public static final int COL_PLAYER_ACCESS_LEVELS = 15;
        public static final int COL_HP_COORD = 16;
        public static final int COL_ENTITY_DATA = 17;
        public static final int COL_RETRIEVAL_COST = 18;
        public static final int COL_WAKE_COORD = 19;
        public static final int COL_WAKE_COORD2 = 20;
        public static final int COL_BT_PROP_COORD = 21;
        public static final int COL_DIRECTION16_ACCELERATION = 22;
        public static final int COL_DIRECTION16_TURNSPEED = 23;
        public static final int COL_KEEL_OPTION = 24;
        public static final int COL_HULL_OPTION = 25;
        public static final int COL_SAIL_OPTION = 26;
        public static final int COL_SAIL_PATTERN_OPTION = 27;
        public static final int COL_STEERING_OPTION = 28;
        public static final int COL_FLAG_OPTION = 29;
        public static final int COL_TRIM_OPTION = 30;
        public static final int COL_BRAZIER_OPTION = 31;
        public static final int COL_HOTSPOT = 32;
        public static final int COL_STATIC_FACILITY = 33;
        public static final int COL_FACILITY_AMOUNT = 34;
        public static final int COL_SAME_FACILITIES_PORT_AND_STARBOARD = 35;
        public static final int COL_CREW_CAPACITY = 36;
        public static final int COL_CREW_SAIL_POSITION = 37;
        public static final int COL_CREW_HELM_POSITION = 38;
        public static final int COL_HP_NPC = 39;
        public static final int COL_TEMPORARY_BOAT = 40;
        public static final int COL_BOAT_SPRITE = 41;
        public static final int COL_CRYSTAL_HELM_VARIANT = 42;
        public static final int COL_CAPSIZING_ANIM = 43;
        public static final int COL_EMITTER_COORDS = 44;
        public static final int COL_GUN_PORT = 45;
    }

    public static final class SailingSidepanelFacility {
        private SailingSidepanelFacility() {}

        public static final int ID = 167;
        public static final int COL_WIDGET = 0;
        public static final int COL_FACILITY_CAN_LOCK_IN = 1;
        public static final int COL_FACILITY_CAN_BE_ASSIGNED = 2;
        public static final int COL_LINKED_FACILITY = 3;
        public static final int COL_SIDEPANEL_PRIORITY_TYPE = 4;
        public static final int COL_ICON = 5;
    }

    public static final class SailingSidepanelWidgetButton {
        private SailingSidepanelWidgetButton() {}

        public static final int ID = 168;
        public static final int COL_DEFAULT_OPTEXT = 0;
        public static final int COL_DEFAULT_GRAPHIC = 1;
        public static final int COL_DEFAULT_BUTTON_CAPTION = 2;
        public static final int COL_TOGGLE_ENABLED = 3;
        public static final int COL_TOGGLE_OPTEXT = 4;
        public static final int COL_TOGGLE_GRAPHIC = 5;
        public static final int COL_TOGGLE_BUTTON_CAPTION = 6;
        public static final int COL_LONG_BUTTON = 7;
        public static final int COL_OP2 = 8;
        public static final int COL_OP3 = 9;
        public static final int COL_OP4 = 10;
    }

    public static final class SailingSidepanelWidgetButtonTargetNpc {
        private SailingSidepanelWidgetButtonTargetNpc() {}

        public static final int ID = 169;
        public static final int COL_DEFAULT_TARGET_VERB = 0;
        public static final int COL_DEFAULT_GRAPHIC = 1;
        public static final int COL_DEFAULT_BUTTON_CAPTION = 2;
        public static final int COL_LONG_BUTTON = 3;
    }

    public static final class SailingSidepanelWidgetGraphic {
        private SailingSidepanelWidgetGraphic() {}

        public static final int ID = 170;
        public static final int COL_GRAPHIC = 0;
    }

    public static final class SailingSidepanelWidgetObject {
        private SailingSidepanelWidgetObject() {}

        public static final int ID = 171;
        public static final int COL_OBJECT = 0;
        public static final int COL_AMOUNT = 1;
    }

    public static final class SailingSidepanelWidgetText {
        private SailingSidepanelWidgetText() {}

        public static final int ID = 172;
        public static final int COL_TEXT = 0;
    }

    public static final class SailingCrew {
        private SailingCrew() {}

        public static final int ID = 173;
        public static final int COL_UNIQUE_ID = 0;
        public static final int COL_WORLD_NPC = 1;
        public static final int COL_BOAT_NPC = 2;
        public static final int COL_CARGO_NPC = 3;
        public static final int COL_SPRITE = 4;
        public static final int COL_STAT_REQUIREMENT = 5;
        public static final int COL_QUEST_REQUIREMENT = 6;
        public static final int COL_SPECIAL_REQUIREMENT = 7;
        public static final int COL_ORDER_STRING_IDLE = 8;
        public static final int COL_ORDER_STRING_SAILS = 9;
        public static final int COL_ORDER_STRING_HELM = 10;
        public static final int COL_ORDER_STRING_HELM_AND_SAILS = 11;
        public static final int COL_ORDER_STRING_SALVAGE = 12;
        public static final int COL_ORDER_STRING_CARGO = 13;
        public static final int COL_ORDER_STRING_CANNON = 14;
        public static final int COL_ORDER_STRING_REPAIRS = 15;
        public static final int COL_ORDER_STRING_REPAIRFAILURE = 16;
        public static final int COL_ORDER_STRING_CHUM_STATION = 17;
        public static final int COL_ORDER_STRING_TRAWLING_NET = 18;
        public static final int COL_ORDER_STRING_TRAWLING_NET_CATCH = 19;
        public static final int COL_PANIC_STRING = 20;
        public static final int COL_ENCOUNTER_STRING = 21;
        public static final int COL_STAT_HELMSMANSHIP = 22;
        public static final int COL_STAT_PRIVATEERING = 23;
        public static final int COL_STAT_DECKHANDINESS = 24;
    }

    public static final class SailingShipyard {
        private SailingShipyard() {}

        public static final int ID = 174;
        public static final int COL_SHIPYARD_ID = 0;
        public static final int COL_ENTRY = 1;
        public static final int COL_FACECOORD = 2;
        public static final int COL_GANGPLANK = 3;
        public static final int COL_BUILDSPACE_ORIGIN = 4;
        public static final int COL_BUILDSPACE_WIDTH = 5;
        public static final int COL_BUILDSPACE_HEIGHT = 6;
        public static final int COL_BUILDSPACE_ANCHOR = 7;
        public static final int COL_BOAT_ROTATION = 8;
        public static final int COL_NPC = 9;
        public static final int COL_OBJ = 10;
    }

    public static final class SailingBoatHotspot {
        private SailingBoatHotspot() {}

        public static final int ID = 175;
        public static final int COL_PLACEHOLDER = 0;
        public static final int COL_SHAPE = 1;
        public static final int COL_OPTION = 2;
    }

    public static final class SailingBoatFacility {
        private SailingBoatFacility() {}

        public static final int ID = 176;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_CUSTOMISATION_HIDE_DESC = 2;
        public static final int COL_FACILITY_TYPE = 3;
        public static final int COL_FACILITY_SUBTYPE = 4;
        public static final int COL_FACILITY_TYPE_SPRITE = 5;
        public static final int COL_LOC = 6;
        public static final int COL_ALTERNATE_LOCS = 7;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 8;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 9;
        public static final int COL_CUSTOMISATION_LOC_ANIM = 10;
        public static final int COL_HIDDEN_MODEL = 11;
        public static final int COL_SAILING_REQUIREMENT = 12;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 13;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 14;
        public static final int COL_OTHER_STAT_REQUIREMENT = 15;
        public static final int COL_QUEST_REQUIREMENT = 16;
        public static final int COL_MATERIAL = 17;
        public static final int COL_BUILD_MAX = 18;
        public static final int COL_VARIANTS = 19;
        public static final int COL_FACILITY_STATS = 20;
        public static final int COL_SIDEPANEL_LAYOUT_DATA = 21;
        public static final int COL_CREW_FACILITY = 22;
        public static final int COL_CREW_STAT_REQUIREMENT = 23;
        public static final int COL_COMBAT_FACILITY_DATA = 24;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 25;
        public static final int COL_FACILITY_BOTTLE_ID = 26;
    }

    public static final class SailingBoatKeel {
        private SailingBoatKeel() {}

        public static final int ID = 177;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_CUSTOMISATION_HIDE_DESC = 2;
        public static final int COL_LOC = 3;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 4;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 5;
        public static final int COL_SAILING_REQUIREMENT = 6;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 7;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 8;
        public static final int COL_OTHER_STAT_REQUIREMENT = 9;
        public static final int COL_QUEST_REQUIREMENT = 10;
        public static final int COL_MATERIAL = 11;
        public static final int COL_FACILITY_STATS = 12;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 13;
    }

    public static final class SailingBoatHull {
        private SailingBoatHull() {}

        public static final int ID = 178;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_CUSTOMISATION_HIDE_DESC = 2;
        public static final int COL_LOC = 3;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 4;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 5;
        public static final int COL_ORIGIN = 6;
        public static final int COL_SAILING_REQUIREMENT = 7;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 8;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 9;
        public static final int COL_OTHER_STAT_REQUIREMENT = 10;
        public static final int COL_QUEST_REQUIREMENT = 11;
        public static final int COL_MATERIAL = 12;
        public static final int COL_FACILITY_STATS = 13;
        public static final int COL_SIDEPANEL_LAYOUT_DATA = 14;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 15;
        public static final int COL_HULL_MATERIAL = 16;
        public static final int COL_GUNPORT_DATA = 17;
    }

    public static final class SailingBoatSail {
        private SailingBoatSail() {}

        public static final int ID = 179;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_CUSTOMISATION_HIDE_DESC = 2;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 3;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 4;
        public static final int COL_HIDDEN_MODEL = 5;
        public static final int COL_SAILING_REQUIREMENT = 6;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 7;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 8;
        public static final int COL_OTHER_STAT_REQUIREMENT = 9;
        public static final int COL_QUEST_REQUIREMENT = 10;
        public static final int COL_MATERIAL = 11;
        public static final int COL_LOC = 12;
        public static final int COL_FACILITY_STATS = 13;
        public static final int COL_SIDEPANEL_LAYOUT_DATA = 14;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 15;
        public static final int COL_PATTERN_DEFAULT = 16;
    }

    public static final class SailingBoatSailFx {
        private SailingBoatSailFx() {}

        public static final int ID = 180;
        public static final int COL_SAIL_REVERSE = 0;
        public static final int COL_SAIL_DOWN = 1;
        public static final int COL_SAIL_HALF = 2;
        public static final int COL_SAIL_FULL = 3;
        public static final int COL_SAIL_TRANSITION_DOWN_TO_HALF = 4;
        public static final int COL_SAIL_TRANSITION_DOWN_TO_FULL = 5;
        public static final int COL_SAIL_TRANSITION_HALF_TO_DOWN = 6;
        public static final int COL_SAIL_TRANSITION_FULL_TO_DOWN = 7;
        public static final int COL_SAIL_TRANSITION_HALF_TO_FULL = 8;
        public static final int COL_SAIL_TRANSITION_FULL_TO_HALF = 9;
        public static final int COL_SAIL_SFX_DOWN_TO_HALF = 10;
        public static final int COL_SAIL_SFX_DOWN_TO_FULL = 11;
        public static final int COL_SAIL_SFX_HALF_TO_DOWN = 12;
        public static final int COL_SAIL_SFX_FULL_TO_DOWN = 13;
        public static final int COL_SAIL_SFX_HALF_TO_FULL = 14;
        public static final int COL_SAIL_SFX_FULL_TO_HALF = 15;
        public static final int COL_SAIL_SFX_LUFFING_DOWN = 16;
        public static final int COL_SAIL_SFX_LUFFING_HALF = 17;
        public static final int COL_SAIL_SFX_LUFFING_FULL = 18;
        public static final int COL_SAIL_SFX_SPEEDBOOST = 19;
        public static final int COL_SAIL_VFX_LUFFING_REVERSE = 20;
        public static final int COL_SAIL_VFX_LUFFING_DOWN = 21;
        public static final int COL_SAIL_VFX_LUFFING_HALF = 22;
        public static final int COL_SAIL_VFX_LUFFING_FULL = 23;
        public static final int COL_SAIL_VFX_SPEEDBOOST = 24;
        public static final int COL_SAIL_TRIM_HUMAN = 25;
    }

    public static final class SailingBoatSteering {
        private SailingBoatSteering() {}

        public static final int ID = 181;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_CUSTOMISATION_HIDE_DESC = 2;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 3;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 4;
        public static final int COL_HIDDEN_MODEL = 5;
        public static final int COL_LOC = 6;
        public static final int COL_WHEEL = 7;
        public static final int COL_ANIMATION = 8;
        public static final int COL_SAILING_REQUIREMENT = 9;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 10;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 11;
        public static final int COL_OTHER_STAT_REQUIREMENT = 12;
        public static final int COL_QUEST_REQUIREMENT = 13;
        public static final int COL_MATERIAL = 14;
        public static final int COL_FACILITY_STATS = 15;
        public static final int COL_SIDEPANEL_LAYOUT_DATA = 16;
        public static final int COL_CREW_STAT_REQUIREMENT = 17;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 18;
    }

    public static final class SailingBoatSteeringFx {
        private SailingBoatSteeringFx() {}

        public static final int ID = 182;
        public static final int COL_STEERING_TRIM_ANIM = 0;
        public static final int COL_STEERING_TRIM_ANIM_HUMAN = 1;
        public static final int COL_STEERING_SPEEDBOOSTED_ANIM = 2;
        public static final int COL_STEERING_SPEEDBOOSTED_ANIM_HUMAN = 3;
        public static final int COL_STEERING_SPEEDBOOSTED_ANIM_END = 4;
        public static final int COL_STEERING_SPEEDBOOSTED_ANIM_END_HUMAN = 5;
        public static final int COL_STEERING_ANIM_ENGAGE = 6;
        public static final int COL_STEERING_ANIM_ENGAGE_HUMAN = 7;
        public static final int COL_STEERING_ANIM_IN_USE_HUMAN = 8;
        public static final int COL_STEERING_ANIM_IN_USE = 9;
        public static final int COL_STEERING_ANIM_IDLE = 10;
        public static final int COL_STEERING_SFX_TRIM = 11;
        public static final int COL_STEERING_SFX_ENGAGE = 12;
        public static final int COL_STEERING_SFX_DISENGAGE = 13;
    }

    public static final class SailingBoatHullOrnament {
        private SailingBoatHullOrnament() {}

        public static final int ID = 183;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_LOC = 2;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 3;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 4;
        public static final int COL_HIDDEN_MODEL = 5;
        public static final int COL_ORIGIN = 6;
        public static final int COL_SAILING_REQUIREMENT = 7;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 8;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 9;
        public static final int COL_OTHER_STAT_REQUIREMENT = 10;
        public static final int COL_QUEST_REQUIREMENT = 11;
        public static final int COL_MATERIAL = 12;
        public static final int COL_FACILITY_STATS = 13;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 14;
    }

    public static final class SailingBoatFlag {
        private SailingBoatFlag() {}

        public static final int ID = 184;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 2;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 3;
        public static final int COL_HIDDEN_MODEL = 4;
        public static final int COL_LOC = 5;
        public static final int COL_SAILING_REQUIREMENT = 6;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 7;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 8;
        public static final int COL_OTHER_STAT_REQUIREMENT = 9;
        public static final int COL_QUEST_REQUIREMENT = 10;
        public static final int COL_MATERIAL = 11;
        public static final int COL_FACILITY_STATS = 12;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 13;
    }

    public static final class SailingBoatBrazier {
        private SailingBoatBrazier() {}

        public static final int ID = 185;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 2;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 3;
        public static final int COL_CUSTOMISATION_LOC_ANIM = 4;
        public static final int COL_HIDDEN_MODEL = 5;
        public static final int COL_LOC = 6;
        public static final int COL_SAILING_REQUIREMENT = 7;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 8;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 9;
        public static final int COL_OTHER_STAT_REQUIREMENT = 10;
        public static final int COL_QUEST_REQUIREMENT = 11;
        public static final int COL_MATERIAL = 12;
        public static final int COL_FACILITY_STATS = 13;
        public static final int COL_FACILITY_TYPE_SPRITE = 14;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 15;
    }

    public static final class SailingBoatTrim {
        private SailingBoatTrim() {}

        public static final int ID = 186;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_LOC = 2;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 3;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 4;
        public static final int COL_SAILING_REQUIREMENT = 5;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 6;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 7;
        public static final int COL_OTHER_STAT_REQUIREMENT = 8;
        public static final int COL_QUEST_REQUIREMENT = 9;
        public static final int COL_MATERIAL = 10;
        public static final int COL_FACILITY_STATS = 11;
        public static final int COL_SIDEPANEL_LAYOUT_DATA = 12;
        public static final int COL_CUSTOMISATION_BUILDABLE = 13;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 14;
    }

    public static final class SailingBoatNameOptions {
        private SailingBoatNameOptions() {}

        public static final int ID = 187;
        public static final int COL_DEFAULT_ = 0;
        public static final int COL_OPTION = 1;
    }

    public static final class SailingCustomisationLocAngles {
        private SailingCustomisationLocAngles() {}

        public static final int ID = 188;
        public static final int COL_ANGLES = 0;
        public static final int COL_ANGLES_LARGE_MODIFIERS = 1;
    }

    public static final class SailingCustomisationTab {
        private SailingCustomisationTab() {}

        public static final int ID = 189;
        public static final int COL_TYPE = 0;
        public static final int COL_TAB_ID = 1;
        public static final int COL_TAB_NAME = 2;
        public static final int COL_TAB_ICON = 3;
        public static final int COL_FACILITY_TYPE = 4;
        public static final int COL_FACILITY_SUBTYPE = 5;
        public static final int COL_FACILITY_DESC = 6;
        public static final int COL_OFFSET = 7;
    }

    public static final class SailingShoal {
        private SailingShoal() {}

        public static final int ID = 190;
        public static final int COL_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_ENTITY_DATA = 2;
        public static final int COL_TARGETSPEED = 3;
        public static final int COL_TURNSPEED = 4;
        public static final int COL_ACCELERATION = 5;
        public static final int COL_SHOAL_SPAWN = 6;
        public static final int COL_SHOAL_RARESPAWN_ID = 7;
        public static final int COL_SHOAL_RARESPAWN_LEVEL_REQUIRED = 8;
        public static final int COL_SHOAL_RARESPAWN = 9;
        public static final int COL_SHOAL_RARESPAWN_MODEL = 10;
        public static final int COL_SHOAL_LOOT = 11;
        public static final int COL_SHOAL_LOOT_RARESPAWN = 12;
        public static final int COL_LEVEL_REQUIRED = 13;
        public static final int COL_SHOAL_ACCEPTED_BAIT = 14;
        public static final int COL_SHOAL_MAX_DEPTH = 15;
        public static final int COL_SHOAL_DEPTH_CHANGE_RATE = 16;
    }

    public static final class SailingShoalSpecific {
        private SailingShoalSpecific() {}

        public static final int ID = 191;
        public static final int COL_DEBUG_NAME = 0;
        public static final int COL_SHOAL_DATA = 1;
        public static final int COL_ORIGIN = 2;
        public static final int COL_WIDTH = 3;
        public static final int COL_HEIGHT = 4;
        public static final int COL_ANGLE = 5;
        public static final int COL_SPAWN = 6;
        public static final int COL_RESPAWNRATE = 7;
        public static final int COL_PATROL_ROUTE = 8;
    }

    public static final class SailingShoalDroptable {
        private SailingShoalDroptable() {}

        public static final int ID = 192;
        public static final int COL_SHOAL_FISH_GIVEN = 0;
        public static final int COL_RARE_FISH_CHANCE = 1;
        public static final int COL_PAINT_ODDS = 2;
        public static final int COL_LOWCHANCE = 3;
        public static final int COL_HIGHCHANCE = 4;
    }

    public static final class SailingTrawlingAnimations {
        private SailingTrawlingAnimations() {}

        public static final int ID = 193;
        public static final int COL_HUMAN_IDLE = 0;
        public static final int COL_HUMAN_ENGAGE = 1;
        public static final int COL_HUMAN_LOWER_TO_1 = 2;
        public static final int COL_HUMAN_LOWER_TO_2_OR_3 = 3;
        public static final int COL_HUMAN_RAISE = 4;
        public static final int COL_HUMAN_RESET_TO_IDLE = 5;
        public static final int COL_TRAWLING_NET_LOWER_TO_1 = 6;
        public static final int COL_TRAWLING_NET_LOWER_TO_2 = 7;
        public static final int COL_TRAWLING_NET_LOWER_TO_3 = 8;
        public static final int COL_TRAWLING_NET_RAISE_TO_2 = 9;
        public static final int COL_TRAWLING_NET_RAISE_TO_1 = 10;
        public static final int COL_TRAWLING_NET_RESET_TO_IDLE = 11;
    }

    public static final class SailingDock {
        private SailingDock() {}

        public static final int ID = 194;
        public static final int COL_DOCK_ID = 0;
        public static final int COL_NICE_NAME = 1;
        public static final int COL_INLINE_NAME = 2;
        public static final int COL_TYPE = 3;
        public static final int COL_LEVEL_REQUIRED = 4;
        public static final int COL_QUEST_REQUIRED = 5;
        public static final int COL_PORT_TASK_QUEST_REQUIRED = 6;
        public static final int COL_LAND_COORD = 7;
        public static final int COL_SEA_COORD = 8;
        public static final int COL_SEA_COORD_ANCHOR = 9;
        public static final int COL_BOAT_ROTATION = 10;
        public static final int COL_INZONE_LAND = 11;
        public static final int COL_INZONE_SEA = 12;
        public static final int COL_DOCK_SPRITE_SMALL = 13;
        public static final int COL_DRINK_CRATE_POS = 14;
        public static final int COL_DRINK_CRATE_ANGLE = 15;
        public static final int COL_PORT_TASK_GAURANTEED_BOUNTY = 16;
    }

    public static final class SailingNpcBoat {
        private SailingNpcBoat() {}

        public static final int ID = 195;
        public static final int COL_NAME = 0;
        public static final int COL_INLINE_NAME = 1;
        public static final int COL_DEBUG_NAME = 2;
        public static final int COL_ORIGIN = 3;
        public static final int COL_ENTRY = 4;
        public static final int COL_WIDTH = 5;
        public static final int COL_HEIGHT = 6;
        public static final int COL_MULTIWAY_DECK = 7;
        public static final int COL_SPAWN = 8;
        public static final int COL_ANGLE = 9;
        public static final int COL_NPC_SPAWN = 10;
        public static final int COL_ENTITY_DATA = 11;
        public static final int COL_RESPAWNRATE = 12;
        public static final int COL_BEHAVIOUR = 13;
        public static final int COL_PATROL = 14;
        public static final int COL_WANDERRANGE = 15;
        public static final int COL_SAIL = 16;
        public static final int COL_STATIC_SPAWN = 17;
        public static final int COL_ALLOW_INSTANCE = 18;
        public static final int COL_SAILING_NPC_BOAT_BASE_STATS = 19;
        public static final int COL_HP_COORD = 20;
        public static final int COL_HP_NPC = 21;
        public static final int COL_WEAPON_DATA = 22;
        public static final int COL_HELM_DATA = 23;
        public static final int COL_HULL_DATA = 24;
        public static final int COL_GUN_PORT = 25;
        public static final int COL_CAPSIZING_ANIM = 26;
        public static final int COL_DROP_NPC = 27;
        public static final int COL_DEATH_DROP = 28;
        public static final int COL_COLLECTED_DROP = 29;
    }

    public static final class SailingBoatCargoholdWhitelistObj {
        private SailingBoatCargoholdWhitelistObj() {}

        public static final int ID = 196;
        public static final int COL_ENTRY = 0;
        public static final int COL_ENTRY_CATEGORY = 1;
    }

    public static final class PortTask {
        private PortTask() {}

        public static final int ID = 197;
        public static final int COL_TASK_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_TASK_TYPE = 2;
        public static final int COL_LEVEL_REQUIRED = 3;
        public static final int COL_STARTING_PORT = 4;
        public static final int COL_CARGO_PORT = 5;
        public static final int COL_ENDING_PORT = 6;
        public static final int COL_CANCELLABLE = 7;
        public static final int COL_XP_REWARD = 8;
        public static final int COL_GIVE_COIN_REWARD = 9;
        public static final int COL_FACILITIES_REQUIRED = 10;
        public static final int COL_FACILITITES_RECOMMENDED = 11;
        public static final int COL_BOAT_COMBAT_REQUIRED = 12;
        public static final int COL_REGULAR_COMBAT_REQUIRED = 13;
        public static final int COL_COMBAT_RECOMMENDED = 14;
        public static final int COL_DELIVERY_OBJECT = 15;
        public static final int COL_CARGO = 16;
        public static final int COL_PORT_CHECKPOINT = 17;
        public static final int COL_BOUNTY_TARGET_ALIVE = 18;
        public static final int COL_BOUNTY_TARGET_DEAD = 19;
        public static final int COL_BOUNTY_OBJECT = 20;
        public static final int COL_BOUNTY_OBJECT_AMOUNT = 21;
        public static final int COL_BOUNTY_OBJECT_RARITY = 22;
        public static final int COL_TASK_BOARD_GRAPHIC = 23;
        public static final int COL_FLAVOUR_TEXT = 24;
        public static final int COL_RELATED_TASKS = 25;
        public static final int COL_GUIDE_TASK = 26;
        public static final int COL_SIDE_EFFECT_ID = 27;
    }

    public static final class TaskBoardLayout {
        private TaskBoardLayout() {}

        public static final int ID = 198;
        public static final int COL_LAYOUT_ID = 0;
        public static final int COL_STYLE_TASK = 1;
        public static final int COL_TASKS_ON_FIRST_ROW = 2;
        public static final int COL_TASKS_ON_SUBSEQUENT_ROWS = 3;
    }

    public static final class SailingAnchorAnims {
        private SailingAnchorAnims() {}

        public static final int ID = 199;
        public static final int COL_SAILING_ANCHOR_INACTIVE = 0;
        public static final int COL_SAILING_ANCHOR_ACTIVE = 1;
        public static final int COL_SAILING_ANCHOR_DROP = 2;
        public static final int COL_SAILING_ANCHOR_PULL = 3;
        public static final int COL_SAILING_ANCHOR_HUMAN_DROP = 4;
        public static final int COL_SAILING_ANCHOR_HUMAN_PULL = 5;
    }

    public static final class SailingSeaHazard {
        private SailingSeaHazard() {}

        public static final int ID = 200;
        public static final int COL_HAZARD_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_SHIP_DAMAGE = 2;
        public static final int COL_EFFECT_FREQUENCY = 3;
        public static final int COL_HAZARD_AREA = 4;
        public static final int COL_VALIDATE_AREA = 5;
        public static final int COL_SFX_EMITTER_LOCS = 6;
    }

    public static final class SailingSea {
        private SailingSea() {}

        public static final int ID = 201;
        public static final int COL_UNIQUE_ID = 0;
        public static final int COL_OCEAN_ID = 1;
        public static final int COL_NAME = 2;
        public static final int COL_TYPE = 3;
        public static final int COL_OCEAN = 4;
        public static final int COL_REQUIRED_LEVEL = 5;
        public static final int COL_XP_LEVEL = 6;
        public static final int COL_CENTRE_COORD = 7;
        public static final int COL_HAS_CHARTING_RAPIDS = 8;
        public static final int COL_SFX_EMITTER_LOCS = 9;
    }

    public static final class SailingShipwreckCluster {
        private SailingShipwreckCluster() {}

        public static final int ID = 202;
        public static final int COL_CONTROLLER_COORD = 0;
        public static final int COL_DEBUG_NAME = 1;
        public static final int COL_ACTIVE_SHIPWRECKS_MAX = 2;
        public static final int COL_SHIPWRECK_DATA = 3;
        public static final int COL_SHIPWRECK_COORD = 4;
    }

    public static final class SailingShipwreck {
        private SailingShipwreck() {}

        public static final int ID = 203;
        public static final int COL_DEBUG_NAME = 0;
        public static final int COL_SAILING_SHIPWRECK_DURABILITY = 1;
        public static final int COL_ACTIVE_LOC = 2;
        public static final int COL_INACTIVE_LOC = 3;
        public static final int COL_LEVELREQ = 4;
        public static final int COL_SALVAGE = 5;
        public static final int COL_XP = 6;
        public static final int COL_LOWCHANCE = 7;
        public static final int COL_HIGHCHANCE = 8;
        public static final int COL_SHIPWRECK_FULL_TO_DEPLETED = 9;
        public static final int COL_SHIPWRECK_DEPLETED_TO_FULL = 10;
        public static final int COL_SHIPWRECK_IDLE_ANIM = 11;
        public static final int COL_SHIPWRECK_STUMP_IDLE_ANIM = 12;
        public static final int COL_PET_CHANCE = 13;
    }

    public static final class SailingSalvagingHookAnimations {
        private SailingSalvagingHookAnimations() {}

        public static final int ID = 204;
        public static final int COL_SAILING_SALVAGING_LOC_DROP_ANIM = 0;
        public static final int COL_SAILING_SALVAGING_LOC_IDLE02_ANIM = 1;
        public static final int COL_SAILING_SALVAGING_LOC_RESET_ANIM = 2;
        public static final int COL_SAILING_SALVAGING_PLAYER_DROP_ANIM = 3;
        public static final int COL_SAILING_SALVAGING_PLAYER_DROP_ANIM_CREW = 4;
        public static final int COL_SAILING_SALVAGING_PLAYER_INTERACT_ANIM = 5;
        public static final int COL_SAILING_SALVAGING_PLAYER_RESET_ANIM = 6;
    }

    public static final class ThievingChest {
        private ThievingChest() {}

        public static final int ID = 205;
        public static final int COL_THIEVING_REQ = 0;
        public static final int COL_THIEVING_XP = 1;
        public static final int COL_STAT_LOW = 2;
        public static final int COL_STAT_HIGH = 3;
        public static final int COL_LOCKPICK_STAT_LOW = 4;
        public static final int COL_LOCKPICK_STAT_HIGH = 5;
        public static final int COL_TELEPORT_STAT_LOW = 6;
        public static final int COL_TELEPORT_STAT_HIGH = 7;
        public static final int COL_OPEN_CHEST = 8;
        public static final int COL_LOOTCONTAINER_DETAILS = 9;
        public static final int COL_GLE_NPC = 10;
    }

    public static final class CharteringDestinations {
        private CharteringDestinations() {}

        public static final int ID = 206;
        public static final int COL_CHARTERING_DESTINATION_ID = 0;
        public static final int COL_CHARTERING_DESTINATION_NAME = 1;
        public static final int COL_CHARTERING_DESTINATION_INLINE_NAME = 2;
        public static final int COL_CHARTERING_DESTINATION_PORT_COORD = 3;
        public static final int COL_CHARTERING_DESTINATION_X_POS = 4;
        public static final int COL_CHARTERING_DESTINATION_Y_POS = 5;
        public static final int COL_CHARTERING_DESTINATION_INZONE = 6;
        public static final int COL_CHARTERING_SHOP_INV = 7;
        public static final int COL_RELATED_CONTENT = 8;
    }

    public static final class CharteringCosts {
        private CharteringCosts() {}

        public static final int ID = 207;
        public static final int COL_CHARTERING_COSTS = 0;
    }

    public static final class PatchyData {
        private PatchyData() {}

        public static final int ID = 208;
        public static final int COL_ID = 0;
        public static final int COL_COMBINED = 1;
        public static final int COL_INGREDIENTS = 2;
    }

    public static final class SkillGuideV2InlineIcon {
        private SkillGuideV2InlineIcon() {}

        public static final int ID = 209;
        public static final int COL_ID = 0;
        public static final int COL_GRAPHIC = 1;
        public static final int COL_SIZE = 2;
        public static final int COL_OFFSET = 3;
        public static final int COL_MARGIN = 4;
    }

    public static final class DeadmanskullInterfaceTab {
        private DeadmanskullInterfaceTab() {}

        public static final int ID = 210;
        public static final int COL_NAME = 0;
        public static final int COL_TAB_NUMBER = 1;
        public static final int COL_COMBAT_SIGIL = 2;
        public static final int COL_SKILLING_SIGIL = 3;
        public static final int COL_UTILITY_SIGIL = 4;
    }

    public static final class CowbossScenerynpcs {
        private CowbossScenerynpcs() {}

        public static final int ID = 211;
        public static final int COL_ENTRY = 0;
    }

    public static final class SkillGuideSubsections {
        private SkillGuideSubsections() {}

        public static final int ID = 212;
        public static final int COL_SKILL = 0;
        public static final int COL_ID = 1;
        public static final int COL_HEADER = 2;
        public static final int COL_MEMBERSONLY = 3;
    }

    public static final class SkillFeatures {
        private SkillFeatures() {}

        public static final int ID = 213;
        public static final int COL_ICON = 0;
        public static final int COL_SPRITE = 1;
        public static final int COL_TEXT = 2;
        public static final int COL_SKILL = 3;
        public static final int COL_QUEST = 4;
        public static final int COL_OTHERREQ = 5;
        public static final int COL_MEMBERSONLY = 6;
        public static final int COL_OTHERDATA_MAGIC = 7;
        public static final int COL_OTHERDATA_SAILING = 8;
        public static final int COL_OTHERDATA_CONSTRUCTION = 9;
    }

    public static final class MinigameTeleport {
        private MinigameTeleport() {}

        public static final int ID = 214;
        public static final int COL_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_MEMBERS_ONLY = 2;
        public static final int COL_MINIGAME_ICON = 3;
    }

    public static final class SailingGunPorts {
        private SailingGunPorts() {}

        public static final int ID = 215;
        public static final int COL_MATERIAL = 0;
        public static final int COL_PORT_MID_CLOSED = 1;
        public static final int COL_PORT_MID_OPEN = 2;
        public static final int COL_PORT_STERN_CLOSED = 3;
        public static final int COL_PORT_STERN_OPEN = 4;
        public static final int COL_STARBOARD_MID_CLOSED = 5;
        public static final int COL_STARBOARD_MID_OPEN = 6;
        public static final int COL_STARBOARD_STERN_CLOSED = 7;
        public static final int COL_STARBOARD_STERN_OPEN = 8;
    }

    public static final class TalentTree {
        private TalentTree() {}

        public static final int ID = 216;
        public static final int COL_DRAW_COORD = 0;
        public static final int COL_LINKED_NODES = 1;
        public static final int COL_NAME = 2;
        public static final int COL_EFFECT = 3;
        public static final int COL_NODE_TYPE = 4;
        public static final int COL_NODE_SIZE = 5;
        public static final int COL_NODE_SPRITE = 6;
        public static final int COL_DEBUG_NAME = 7;
    }

    public static final class TalentDebug {
        private TalentDebug() {}

        public static final int ID = 217;
        public static final int COL_BUILD_NAME = 0;
        public static final int COL_POINTS_USED = 1;
        public static final int COL_NODE = 2;
        public static final int COL_BUILD_STYLE = 3;
        public static final int COL_BUILD_STYLE_SUB = 4;
    }

    public static final class LeagueRelicTeleportItem {
        private LeagueRelicTeleportItem() {}

        public static final int ID = 218;
        public static final int COL_TITLE = 0;
        public static final int COL_ITEM = 1;
        public static final int COL_LOCATION = 2;
        public static final int COL_RESPECT_WILDY_RESTRICTIONS = 3;
    }

    public static final class LeagueRelicClueDirectTeleportItem {
        private LeagueRelicClueDirectTeleportItem() {}

        public static final int ID = 219;
        public static final int COL_ITEM = 0;
        public static final int COL_NULL_CLUE_TEXT = 1;
        public static final int COL_NULL_CLUE_COORD_TEXT = 2;
    }

    public static final class LeagueRelicEffectToggleList {
        private LeagueRelicEffectToggleList() {}

        public static final int ID = 220;
        public static final int COL_TOGGLE = 0;
    }

    public static final class LeagueRelicEffectToggle {
        private LeagueRelicEffectToggle() {}

        public static final int ID = 221;
        public static final int COL_TOGGLE_DEBUGNAME = 0;
        public static final int COL_TOGGLE_RELIC = 1;
        public static final int COL_TOGGLE_DESC = 2;
        public static final int COL_TOGGLE_ON_MESSAGE = 3;
        public static final int COL_TOGGLE_OFF_MESSAGE = 4;
        public static final int COL_TOGGLE_INVERT = 5;
        public static final int COL_TOGGLE_BIT = 6;
    }

    public static final class LeagueGuardianData {
        private LeagueGuardianData() {}

        public static final int ID = 222;
        public static final int COL_NAME = 0;
        public static final int COL_BODY_DATA = 1;
        public static final int COL_ANIM_DATA = 2;
    }

    public static final class LeagueGuardianBodyData {
        private LeagueGuardianBodyData() {}

        public static final int ID = 223;
        public static final int COL_HANDS_MODEL = 0;
        public static final int COL_FEET_MODEL = 1;
        public static final int COL_LEGS_MODEL = 2;
        public static final int COL_JAW_MODEL = 3;
        public static final int COL_HEAD_MODEL = 4;
        public static final int COL_HAT_MODEL = 5;
        public static final int COL_BODY_MODEL = 6;
        public static final int COL_SINGLE_BODY_MODEL = 7;
        public static final int COL_SINGLE_BODY_MODEL_EXTRA = 8;
    }

    public static final class LeagueGuardianAnimData {
        private LeagueGuardianAnimData() {}

        public static final int ID = 224;
        public static final int COL_READY_ANIM = 0;
        public static final int COL_RUN_ANIM = 1;
        public static final int COL_WALK_ANIM = 2;
        public static final int COL_SPAWN_ANIM = 3;
        public static final int COL_SPAWN_SPOTANIM = 4;
        public static final int COL_DESPAWN_ANIM = 5;
        public static final int COL_DESPAWN_SPOTANIM = 6;
        public static final int COL_WEAPON_MODEL = 7;
        public static final int COL_ATTACK_ANIM = 8;
        public static final int COL_TRAVEL_SPOTANIM = 9;
        public static final int COL_LAUNCH_SPOTANIM = 10;
        public static final int COL_IMPACT_SPOTANIM = 11;
        public static final int COL_ATTACK_SOUND = 12;
        public static final int COL_CHANGE_STYLE_SPOTANIM = 13;
    }

    public static final class Transmutation {
        private Transmutation() {}

        public static final int ID = 225;
        public static final int COL_TIER_ITEM = 0;
        public static final int COL_NAME = 1;
    }

    public static final class ButlersBellActions {
        private ButlersBellActions() {}

        public static final int ID = 226;
        public static final int COL_ACTION_ID = 0;
        public static final int COL_RAW_NOUN = 1;
        public static final int COL_PROCESSED_NOUN = 2;
        public static final int COL_PROCESSED_XP = 3;
        public static final int COL_XP_SKILL = 4;
        public static final int COL_GATHER_BASE_RATE = 5;
        public static final int COL_RESOURCE_WEIGHTING = 6;
    }

    public static final class SpellOverrideList {
        private SpellOverrideList() {}

        public static final int ID = 227;
        public static final int COL_OVERRIDE = 0;
    }

    public static final class SpellOverride {
        private SpellOverride() {}

        public static final int ID = 228;
        public static final int COL_BASE_SPELL = 0;
        public static final int COL_OVERRIDE_SPELL = 1;
        public static final int COL_RELIC_UNLOCK = 2;
    }

    public static final class SailingSidepanelWidgetObjbutton {
        private SailingSidepanelWidgetObjbutton() {}

        public static final int ID = 229;
        public static final int COL_DEFAULT_OPTEXT = 0;
        public static final int COL_TOGGLE_ENABLED = 1;
        public static final int COL_TOGGLE_OPTEXT = 2;
        public static final int COL_OBJECT = 3;
        public static final int COL_AMOUNT = 4;
        public static final int COL_OP2 = 5;
        public static final int COL_OP3 = 6;
        public static final int COL_OP4 = 7;
    }

    public static final class SailingBoatSailPattern {
        private SailingBoatSailPattern() {}

        public static final int ID = 230;
        public static final int COL_NAME = 0;
        public static final int COL_DESCRIPTION = 1;
        public static final int COL_CUSTOMISATION_HIDE_DESC = 2;
        public static final int COL_CUSTOMISATION_LOC_VALUES = 3;
        public static final int COL_CUSTOMISATION_LOC_OVERRIDE = 4;
        public static final int COL_HIDDEN_MODEL = 5;
        public static final int COL_SAILING_REQUIREMENT = 6;
        public static final int COL_CONSTRUCTION_REQUIREMENT = 7;
        public static final int COL_CONSTRUCTION_COINS_ALTERNATIVE = 8;
        public static final int COL_OTHER_STAT_REQUIREMENT = 9;
        public static final int COL_QUEST_REQUIREMENT = 10;
        public static final int COL_MATERIAL = 11;
        public static final int COL_LOC = 12;
        public static final int COL_FACILITY_STATS = 13;
        public static final int COL_SIDEPANEL_LAYOUT_DATA = 14;
        public static final int COL_FACILITY_CUSTOMISATION_ORDER = 15;
    }

    public static final class SailingNpcBoatBaseStats {
        private SailingNpcBoatBaseStats() {}

        public static final int ID = 231;
        public static final int COL_BOAT_HP_MAX = 0;
        public static final int COL_BOAT_DEFENCE = 1;
        public static final int COL_BOAT_STABDEFENCE = 2;
        public static final int COL_BOAT_SLASHDEFENCE = 3;
        public static final int COL_BOAT_CRUSHDEFENCE = 4;
        public static final int COL_BOAT_HEAVYRANGEDDEFENCE = 5;
        public static final int COL_BOAT_STANDARDRANGEDDEFENCE = 6;
        public static final int COL_BOAT_LIGHTRANGEDDEFENCE = 7;
        public static final int COL_BOAT_MAGICDEFENCE = 8;
        public static final int COL_BOAT_MAGICLEVEL = 9;
        public static final int COL_BOAT_ARMOUR = 10;
        public static final int COL_BOAT_AIRMAGICARMOUR = 11;
        public static final int COL_BOAT_EARTHMAGICARMOUR = 12;
        public static final int COL_BOAT_FIREMAGICARMOUR = 13;
        public static final int COL_BOAT_WATERMAGICARMOUR = 14;
        public static final int COL_BOAT_LIGHTRANGEDARMOUR = 15;
        public static final int COL_BOAT_STANDARDRANGEDARMOUR = 16;
        public static final int COL_BOAT_HEAVYRANGEDARMOUR = 17;
        public static final int COL_BOAT_STABARMOUR = 18;
        public static final int COL_BOAT_SLASHARMOUR = 19;
        public static final int COL_BOAT_CRUSHARMOUR = 20;
        public static final int COL_BOAT_TARGETSPEED = 21;
        public static final int COL_BOAT_TURNSPEED = 22;
        public static final int COL_BOAT_ACCELERATION = 23;
    }

    public static final class SailingNpcBoatWeapon {
        private SailingNpcBoatWeapon() {}

        public static final int ID = 232;
        public static final int COL_FACILITY_LOC = 0;
        public static final int COL_ACCURACY = 1;
        public static final int COL_DAMAGE = 2;
        public static final int COL_ATTACK_RATE = 3;
        public static final int COL_DAMAGE_TYPE = 4;
        public static final int COL_ATTACK_RANGE = 5;
        public static final int COL_AMMUNITION_DATA = 6;
        public static final int COL_HUMAN_ATTACK_ANIM = 7;
        public static final int COL_HUMAN_IDLE_ANIM = 8;
        public static final int COL_HUMAN_ENGAGE_ANIM = 9;
        public static final int COL_IDLE_ANIM_DISENGAGED = 10;
        public static final int COL_IDLE_ANIM_ENGAGED = 11;
        public static final int COL_ENGAGE_ANIM = 12;
        public static final int COL_ATTACK_ANIM = 13;
        public static final int COL_ATTACK_SPOTANIM_NORTH = 14;
        public static final int COL_ATTACK_SPOTANIM_EAST = 15;
        public static final int COL_ATTACK_SPOTANIM_SOUTH = 16;
        public static final int COL_ATTACK_SPOTANIM_WEST = 17;
    }

    public static final class SailingNpcBoatSteering {
        private SailingNpcBoatSteering() {}

        public static final int ID = 233;
        public static final int COL_LOC = 0;
        public static final int COL_ANIMATION_DATA = 1;
    }

    public static final class AmbientSfx {
        private AmbientSfx() {}

        public static final int ID = 234;
        public static final int COL_AMBIENTSOUND = 0;
        public static final int COL_AREA = 1;
        public static final int COL_AREA_EXCLUDE = 2;
        public static final int COL_INZONE = 3;
        public static final int COL_INZONE_EXCLUDE = 4;
    }

    public static final class DrakanAttackList {
        private DrakanAttackList() {}

        public static final int ID = 235;
        public static final int COL_ATTACK = 0;
    }

    public static final class DrakanAttackSequenceList {
        private DrakanAttackSequenceList() {}

        public static final int ID = 236;
        public static final int COL_ATTACK_INSTRUCTION = 0;
        public static final int COL_ATTACK_WEIGHT_OVERRIDE = 1;
    }

    public static final class DrakanAttackSequence {
        private DrakanAttackSequence() {}

        public static final int ID = 237;
        public static final int COL_ATTACK = 0;
        public static final int COL_ACTION_DELAY = 1;
        public static final int COL_TELEGRAPH = 2;
        public static final int COL_FACING_START_END = 3;
        public static final int COL_FREEZE_START_DURATION = 4;
    }

    public static final class DrakanTileAttack {
        private DrakanTileAttack() {}

        public static final int ID = 238;
        public static final int COL_TELEGRAPH = 0;
        public static final int COL_HIT_VISUALS = 1;
        public static final int COL_DODGE_VISUALS = 2;
        public static final int COL_PAIRED_VISUALS = 3;
        public static final int COL_HIT_TILE_OFFSETS = 4;
        public static final int COL_DODGE_TILE_OFFSETS = 5;
        public static final int COL_FACE_TARGET = 6;
        public static final int COL_DYNAMIC_TRACKING = 7;
        public static final int COL_OFFSET_RANDOMISATION = 8;
        public static final int COL_DAMAGE_STYLE = 9;
        public static final int COL_PRAYER_REDUCTION = 10;
        public static final int COL_DAMAGE_RANGE = 11;
    }

    public static final class DrakanTrackingAttack {
        private DrakanTrackingAttack() {}

        public static final int ID = 239;
        public static final int COL_TELEGRAPH = 0;
        public static final int COL_HIT_VISUALS = 1;
        public static final int COL_PAIRED_VISUALS = 2;
        public static final int COL_DAMAGE_STYLE = 3;
        public static final int COL_PRAYER_REDUCTION = 4;
        public static final int COL_DAMAGE_RANGE = 5;
    }

    public static final class DrakanUniqueAttack {
        private DrakanUniqueAttack() {}

        public static final int ID = 240;
        public static final int COL_TELEGRAPH = 0;
    }

    public static final class DrakanVisuals {
        private DrakanVisuals() {}

        public static final int ID = 241;
        public static final int COL_ANIM = 0;
        public static final int COL_SPOTANIM = 1;
        public static final int COL_SPOTANIM_TARGET = 2;
        public static final int COL_TILE_NPC_DURATION = 3;
        public static final int COL_TILE_SPOTANIM = 4;
        public static final int COL_SPOTANIM_HEIGHT = 5;
        public static final int COL_SOUND_AREA = 6;
    }

    public static final class DrakanAnims {
        private DrakanAnims() {}

        public static final int ID = 242;
        public static final int COL_LAME_WING = 0;
        public static final int COL_BLOOD_WING = 1;
    }

    public static final class DrakanSpotanims {
        private DrakanSpotanims() {}

        public static final int ID = 243;
        public static final int COL_LAME_WING = 0;
        public static final int COL_BLOOD_WING = 1;
        public static final int COL_PICKING_MODE = 2;
    }

    public static final class DrakanSpotanimProjanimPairs {
        private DrakanSpotanimProjanimPairs() {}

        public static final int ID = 244;
        public static final int COL_PAIR = 0;
    }

    public static final class DrakanFightAlly {
        private DrakanFightAlly() {}

        public static final int ID = 245;
        public static final int COL_DODGE_CHANCE = 0;
        public static final int COL_DODGE_ANIM_SHORT = 1;
        public static final int COL_DODGE_ANIM_LONG = 2;
        public static final int COL_PRAY_SWITCH_CHANCE = 3;
        public static final int COL_DODGE_BYPASS_CHANCE = 4;
    }

    public static final class CastleDrakanRoom {
        private CastleDrakanRoom() {}

        public static final int ID = 246;
        public static final int COL_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_ORIGIN = 2;
        public static final int COL_WIDTH = 3;
        public static final int COL_HEIGHT = 4;
        public static final int COL_MAP_DETAILS = 5;
        public static final int COL_UNIQUE_ITEM = 6;
        public static final int COL_UNIQUE_SEARCHABLE = 7;
        public static final int COL_CONSUMABLE_ITEM = 8;
        public static final int COL_CONSUMABLE_SEARCHABLE = 9;
        public static final int COL_LOC_HIGHLIGHT = 10;
        public static final int COL_ENEMY = 11;
    }

    public static final class CastleDrakanDoor {
        private CastleDrakanDoor() {}

        public static final int ID = 247;
        public static final int COL_ID = 0;
        public static final int COL_ROOM_1 = 1;
        public static final int COL_ROOM_2 = 2;
        public static final int COL_ROOM_1_DOOR_L = 3;
        public static final int COL_ROOM_1_DOOR_R = 4;
        public static final int COL_ROOM_2_DOOR_L = 5;
        public static final int COL_ROOM_2_DOOR_R = 6;
        public static final int COL_ROOM_1_ENTRY_DIRECTION = 7;
        public static final int COL_ROOM_2_ENTRY_DIRECTION = 8;
        public static final int COL_MAP_DETAILS = 9;
        public static final int COL_UNLOCK_SIDE = 10;
        public static final int COL_KEY = 11;
        public static final int COL_EMBLEM = 12;
        public static final int COL_SFX = 13;
    }

    public static final class CastleDrakanStairs {
        private CastleDrakanStairs() {}

        public static final int ID = 248;
        public static final int COL_STAIRS_COORD = 0;
        public static final int COL_DESTINATION_ROOM = 1;
        public static final int COL_DESTINATION_COORD = 2;
        public static final int COL_DESTINATION_FACING = 3;
        public static final int COL_SFX = 4;
    }

    public static final class SangvestiDrakanPatrol {
        private SangvestiDrakanPatrol() {}

        public static final int ID = 249;
        public static final int COL_DESTINATION = 0;
        public static final int COL_DEBUG_ID = 1;
        public static final int COL_CONNECTED_NODES = 2;
    }

    public static final class SangvestiSpawn {
        private SangvestiSpawn() {}

        public static final int ID = 250;
        public static final int COL_ID = 0;
        public static final int COL_COORD = 1;
        public static final int COL_NPC = 2;
        public static final int COL_OBJ = 3;
    }

    public static final class SotfaForestVariant {
        private SotfaForestVariant() {}

        public static final int ID = 251;
        public static final int COL_RANDOM_ORDER = 0;
        public static final int COL_ENCOUNTER = 1;
    }

    public static final class SotfaForestEncounter {
        private SotfaForestEncounter() {}

        public static final int ID = 252;
        public static final int COL_ID = 0;
        public static final int COL_NAME = 1;
        public static final int COL_ORIGIN = 2;
        public static final int COL_WIDTH = 3;
        public static final int COL_HEIGHT = 4;
        public static final int COL_ENTRY = 5;
    }

    public static final class SotfaForestMaxillaBeastPatrol {
        private SotfaForestMaxillaBeastPatrol() {}

        public static final int ID = 253;
        public static final int COL_PATROL = 0;
    }

    public static final class CoordinateSetList {
        private CoordinateSetList() {}

        public static final int ID = 254;
        public static final int COL_SET = 0;
    }

    public static final class CoordinateSet {
        private CoordinateSet() {}

        public static final int ID = 255;
        public static final int COL_OFFSET = 0;
        public static final int COL_MAX_SEARCH_RANGE = 1;
    }

    public static final class PreparationRecipe {
        private PreparationRecipe() {}

        public static final int ID = 256;
        public static final int COL_INGREDIENT = 0;
        public static final int COL_COOKING_LEVEL_REQUIRED = 1;
        public static final int COL_OUTPUT = 2;
        public static final int COL_COOKING_TIME = 3;
        public static final int COL_MESSAGE = 4;
        public static final int COL_XP = 5;
        public static final int COL_BUYABLE_COST = 6;
        public static final int COL_ANIM = 7;
        public static final int COL_HIDDEN_IN_LIST = 8;
    }

    public static final class RiverFishing {
        private RiverFishing() {}

        public static final int ID = 257;
        public static final int COL_SPOT = 0;
        public static final int COL_QUEST_REQUIREMENTS = 1;
        public static final int COL_STAT_REQUIREMENTS = 2;
        public static final int COL_EQUIPMENT_REQUIREMENTS = 3;
        public static final int COL_FISH_SPAWN_PATTERN_OFFSET_ORDERED = 4;
        public static final int COL_FISH_TRAVEL = 5;
        public static final int COL_STATRANDOM_FAILURE = 6;
        public static final int COL_STATRANDOM_SUCCESS = 7;
        public static final int COL_STATRANDOM_LOW_HIGH = 8;
        public static final int COL_XP_PER_LEVEL_NUMERATOR = 9;
        public static final int COL_XP_FLAT = 10;
        public static final int COL_XP_MIN = 11;
        public static final int COL_STREAM_FACING = 12;
        public static final int COL_SUCCESS_AUDIO = 13;
        public static final int COL_FAILURE_AUDIO = 14;
    }

    public static final class ArrowFletching {
        private ArrowFletching() {}

        public static final int ID = 258;
        public static final int COL_ARROWHEAD = 0;
        public static final int COL_REGULAR_PRODUCT = 1;
        public static final int COL_SEEKING_PRODUCT = 2;
        public static final int COL_BATCH_SIZE = 3;
        public static final int COL_LEVELREQ = 4;
        public static final int COL_HEADLESSNAME_SINGLE = 5;
        public static final int COL_HEADLESSNAME_PLURAL = 6;
        public static final int COL_ARROWNAME_PLURAL = 7;
        public static final int COL_TIPNAME_SINGLE = 8;
        public static final int COL_TIPNAME_PLURAL = 9;
        public static final int COL_XP = 10;

        public static final class Row {
            private Row() {}

            public static final int BRONZE_ARROWS = 16924;
            public static final int IRON_ARROWS = 16925;
            public static final int STEEL_ARROWS = 16926;
            public static final int MITHRIL_ARROWS = 16927;
            public static final int ADAMANT_ARROWS = 16928;
            public static final int RUNE_ARROWS = 16929;
            public static final int AMETHYST_ARROWS = 16930;
            public static final int DRAGON_ARROWS = 16931;
            public static final int SLAYER_BROAD_ARROWS = 16932;
            public static final int ATLATL_DART = 16933;
        }
    }

    public static GamevalEntry[] entries() { return DBTableIDEntries.entries(); }

    public static Optional<GamevalEntry> byId(int id) {
        return DBTableIDEntries.byId(id);
    }
}
