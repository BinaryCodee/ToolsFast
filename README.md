<div align="center">

# ⚒️ ToolsFast

### Premium tools suite for Paper / Spigot 1.20.x — 1.21.x

Self-destruct system · Custom items · 16 built-in tools · 9 abilities · Shop providers · Statistics · Leaderboard · Particle engine · PlaceholderAPI · Multi-module NMS · Public API

[![Version](https://img.shields.io/badge/version-1.0.0-C77DFF?style=flat-square)](https://github.com/BinaryCodee/toolsfast/releases)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-C71A36?style=flat-square&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Paper](https://img.shields.io/badge/Paper-1.20%20—%201.21-00B3A4?style=flat-square)](https://papermc.io/)
[![License](https://img.shields.io/badge/license-Premium-C77DFF?style=flat-square)](LICENSE)
[![JavaDoc](https://img.shields.io/badge/JavaDoc-online-C77DFF?style=flat-square)](https://toolsfast.netlify.app/apidocs/)

[Installation](#installation) · [Commands](#commands) · [Custom Items](#custom-items) · [API](#public-api) · [JavaDoc](#javadoc) · [Placeholders](#placeholders)

</div>

---

## ✨ Features

- **⌛ Self Destruct System** — Add expiration to any item (immediate or delayed-on-first-use). Batch processing: one task handles thousands of items without lag.
- **📦 Custom Items Loader** — Define items via YAML in `plugins/ToolsFast/custom-items/`. Hot reload, no code changes required.
- **💎 16 Built-in Tools** — Amethyst Drill, Hammer, Axe, Shovel, Bucket, Sell Axe, Multi Tool, Infinite Firework, Harvester Hoe, Trench/Tray Pickaxe, Sand/Ice/Craft/Sell/Lightning Wand.
- **🎯 9 Abilities** — `BREAK_3X3`, `BREAK_5X5`, `BREAK_7X7`, `AUTO_PICKUP`, `AUTO_SMELT`, `TREE_CAPITATOR`, `AUTO_REPLANT`, `TRENCH_BREAK`, `TRAY_BREAK`.
- **🏪 Shop Providers** — Built-in fallback + Vault worth. Public registry for external shops (EconomyShopGUI, ShopGUIPlus, PrimeSell, SellWorth).
- **📊 Statistics & Leaderboard** — Blocks, money, trees, crops, items sold. Async DB persistence + in-memory cache.
- **🎨 Particle Engine** — Extensible particle system with custom effects registration.
- **🔌 PlaceholderAPI** — 15+ built-in placeholders + custom placeholder registry.
- **🗄️ Database** — SQLite (default) or MySQL. Async thread pool, no blocking main thread.
- **🧩 Multi-Module NMS** — ServiceLoader-based NMS abstraction. Add new Minecraft versions by adding a module, no plugin rewrite.
- **🌐 Public API** — Separate `toolsfast-api` artifact for third-party plugin integration.

---

## 📦 Repository Structure

This repository contains four artifacts in a single Maven multi-module project:

```
toolsfast/
├── toolsfast-api/             📚 Public API (interfaces, events, contracts)
├── toolsfast-nms/             🔌 NMS abstraction + ServiceLoader + Fallback
├── toolsfast-nms-1_20/        📦 NMS implementation for 1.20.x
├── toolsfast-nms-1_21/        📦 NMS implementation for 1.21.x
├── toolsfast-plugin/          🧩 Main plugin (shade finale)
└── pom.xml                    🔧 Parent POM
```

| Artifact | What it is | Who uses it |
|---|---|---|
| `ToolsFast-1.0.0.jar` | Full plugin, drop into `plugins/` | Server admins |
| `toolsfast-api-1.0.0.jar` | API only, 15 KB | Plugin developers (as `provided` dependency) |
| Sources (`src/`) | Complete multi-module project | Contributors |
| JavaDoc | Online reference for every public type | Plugin developers |

> 📥 **Downloads**: see [Releases](https://github.com/BinaryCodee/toolsfast/releases) for the latest `ToolsFast-1.0.0.jar` and `toolsfast-api-1.0.0.jar`.

---

## 🚀 Installation

### Requirements

- **Server**: Paper or Spigot 1.20.x / 1.21.x
- **Java**: 21 or higher
- **Optional soft-dependencies**: Vault, PlaceholderAPI, WorldGuard, EconomyShopGUI, ShopGUIPlus, PrimeSell, SellWorth

### Steps

1. Download `ToolsFast-1.0.0.jar` from [Releases](https://github.com/BinaryCodee/toolsfast/releases)
2. Drop it into your server's `plugins/` folder
3. Restart the server — first run generates config files and 17 sample custom items
4. Verify in console: `[ToolsFast] Enabled v1.0.0`
5. Run `/toolsfast` in-game to open the main GUI

### File structure after first run

```
plugins/ToolsFast/
├── config.yml                 # main configuration
├── messages.yml               # chat messages, titles, action bars
├── gui.yml                    # GUI layout
├── tools.yml                  # built-in tool overrides
├── data.db                    # SQLite database (default)
└── custom-items/              # your custom items go here
    ├── amethyst_drill.yml
    ├── amethyst_hammer.yml
    └── ... (17 sample items)
```

---

## 🎮 Commands

**Main command**: `/toolsfast` (aliases: `/tf`, `/tools`)

| Command | Permission | Description |
|---|---|---|
| `/toolsfast` | `toolsfast.menu` | Open main GUI |
| `/toolsfast give <player> <tool> [amount]` | `toolsfast.admin` | Give a tool |
| `/toolsfast list` | `toolsfast.menu` | List all tools |
| `/toolsfast selfdestruct <time>` | `toolsfast.selfdestruct` | Add expiration to held item |
| `/toolsfast selfdestruct delayed <time>` | `toolsfast.selfdestruct` | Delayed expiration (starts on first use) |
| `/toolsfast selfdestruct remove` | `toolsfast.selfdestruct` | Remove expiration from held item |
| `/toolsfast stats` | `toolsfast.menu` | Show your statistics |
| `/toolsfast leaderboard` | `toolsfast.menu` | Open leaderboard GUI |
| `/toolsfast reload` | `toolsfast.admin` | Reload configuration |
| `/toolsfast help` | `toolsfast.menu` | Show help |

### Time formats

| Suffix | Unit | Example |
|---|---|---|
| `s` (or none) | seconds | `30s`, `30` |
| `m` | minutes | `10m` |
| `h` | hours | `5h` |
| `d` | days | `1d` |
| `w` | weeks | `2w` |

Combinations accepted: `1d12h30m`, `2w3d`, `90m`.

---

## 🛠️ Custom Items

Create a `.yml` file in `plugins/ToolsFast/custom-items/`:

```yaml
id: amethyst_drill

material: NETHERITE_PICKAXE

name: "&#C77DFF&lAMETHYST DRILL"

lore:
  - "&8Mining Tool"
  - ""
  - "&fAbilities:"
  - "&d▸ &fBreak 3x3"
  - "&d▸ &fAuto Pickup"
  - ""
  - "&fBlocks Mined:"
  - "&d%blocks%"

unbreakable: true
glow: false
custom-model-data: -1

abilities:
  - BREAK_3X3
  - AUTO_PICKUP
```

### YAML fields

| Field | Type | Default | Description |
|---|---|---|---|
| `id` | String | required | Unique identifier |
| `material` | Material | `STONE` | Bukkit material |
| `name` | String | `"&f"+id` | Display name (HEX supported) |
| `lore` | List<String> | — | Item lore |
| `abilities` | List<String> | — | Ability IDs |
| `unbreakable` | boolean | false | Make item unbreakable |
| `glow` | boolean | false | Glow effect (hidden enchant) |
| `custom-model-data` | int | -1 | Resource pack model data |
| `amount` | int | 1 | Stack size |

### Available abilities

| Ability | Trigger | Behavior |
|---|---|---|
| `BREAK_3X3` | BlockBreak | Break 3x3 area around block |
| `BREAK_5X5` | BlockBreak | Break 5x5 area around block |
| `BREAK_7X7` | BlockBreak | Break 7x7 area around block |
| `AUTO_PICKUP` | BlockBreak | Auto-collect drops to inventory |
| `AUTO_SMELT` | BlockBreak | Auto-smelt ores (Iron Ore → Iron Ingot, etc.) |
| `TREE_CAPITATOR` | BlockBreak | Chop entire trees (BFS, max 500 blocks) |
| `AUTO_REPLANT` | BlockBreak | Auto-replant after harvesting mature crops |
| `TRENCH_BREAK` | BlockBreak | Break configurable cube (default 3x3x3) |
| `TRAY_BREAK` | BlockBreak | Break horizontal layer (default 5x5) |

### Lore placeholders

| Placeholder | Replaced with |
|---|---|
| `%blocks%` | Player's total blocks mined |
| `%trees%` | Player's total trees chopped |
| `%crops%` | Player's total crops harvested |
| `%money%` | Player's total money generated |
| `%items_sold%` | Player's total items sold |
| `%durability%` | Remaining durability |

---

## 🔧 Built-in Tools

| ID | Material | Default Abilities |
|---|---|---|
| `amethyst_drill` | NETHERITE_PICKAXE | BREAK_3X3, AUTO_PICKUP |
| `amethyst_hammer` | NETHERITE_PICKAXE | BREAK_5X5, AUTO_PICKUP |
| `amethyst_axe` | NETHERITE_AXE | TREE_CAPITATOR |
| `amethyst_shovel` | NETHERITE_SHOVEL | BREAK_3X3, AUTO_PICKUP |
| `amethyst_bucket` | BUCKET | — (interactive: absorb water/lava) |
| `amethyst_sell_axe` | NETHERITE_AXE | — (interactive: sell chest content) |
| `amethyst_multi_tool` | NETHERITE_PICKAXE | BREAK_3X3, TREE_CAPITATOR, AUTO_PICKUP |
| `infinite_firework` | FIREWORK_ROCKET | — (interactive: elytra boost, no consume) |
| `harvester_hoe` | NETHERITE_HOE | AUTO_REPLANT, AUTO_PICKUP |
| `trench_pickaxe` | NETHERITE_PICKAXE | TRENCH_BREAK, AUTO_PICKUP |
| `tray_pickaxe` | NETHERITE_PICKAXE | TRAY_BREAK, AUTO_PICKUP |
| `sand_wand` | STICK | — (interactive: fill sand column) |
| `ice_wand` | STICK | — (interactive: remove ice) |
| `craft_wand` | STICK | — (interactive: open workbench) |
| `sell_wand` | STICK | — (interactive: sell container content) |
| `lightning_wand` | STICK | — (interactive: strike lightning) |

---

## 🌐 Public API

ToolsFast exposes a complete public API for third-party plugins. Add only `toolsfast-api` as a `provided` dependency — the full plugin must be installed on the server.

### Maven

```xml
<repositories>
    <repository>
        <id>BinaryCodee-repo</id>
        <url>https://repo.BinaryCodee.it/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>it.bypasser</groupId>
        <artifactId>toolsfast-api</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Or install the jar locally

```bash
mvn install:install-file \
    -Dfile=toolsfast-api-1.0.0.jar \
    -DgroupId=it.bypasser \
    -DartifactId=toolsfast-api \
    -Dversion=1.0.0 \
    -Dpackaging=jar
```

### plugin.yml

```yaml
name: MyPlugin
main: it.example.myplugin.MyPlugin
api-version: '1.20'
softdepend:
  - ToolsFast
  - Vault
  - PlaceholderAPI
```

### Quick start

```java
import it.bypasser.toolsfast.api.ToolsFastAPI;

@Override
public void onEnable() {
    if (getServer().getPluginManager().getPlugin("ToolsFast") == null) {
        getLogger().warning("ToolsFast not found, integration disabled.");
        return;
    }

    ToolsFastAPI api = ToolsFastAPI.get();

    // Give a tool
    api.giveTool(player, "amethyst_drill", 1);

    // Apply 1-hour self-destruct to held item
    ItemStack held = player.getInventory().getItemInMainHand();
    api.selfDestruct().apply(held, java.time.Duration.ofHours(1).toMillis());

    // Read statistics
    long blocks = api.statistics().getBlocks(player.getUniqueId());

    // Register a custom ability
    api.abilities().register(new MyLightningBreakAbility());

    // Register a custom shop provider
    api.shopProviders().register(new EconomyShopGUIProvider());

    // Register a custom particle effect
    api.particles().registerParticle("amethyst_burst", new AmethystBurstEffect());

    // Register a custom placeholder
    api.placeholders().register("level", player ->
        String.valueOf(api.statistics().getBlocks(player.getUniqueId()) / 1000));
}
```

### API sub-systems

| Method | Returns | Purpose |
|---|---|---|
| `api.customItems()` | `CustomItemManager` | Custom item management |
| `api.tools()` | `ToolRegistry` | Tool registration |
| `api.selfDestruct()` | `SelfDestructManager` | Item expiration |
| `api.statistics()` | `StatisticsManager` | Player statistics |
| `api.shopProviders()` | `ShopProviderRegistry` | Shop provider registration |
| `api.particles()` | `ParticleEngine` | Particle effects |
| `api.abilities()` | `AbilityRegistry` | Ability registration |
| `api.enchants()` | `EnchantRegistry` | Token enchant registration |
| `api.placeholders()` | `PlaceholderRegistry` | Custom placeholder registration |

### Custom events

All events are in `it.bypasser.toolsfast.api.events`:

```java
@EventHandler
public void onToolReceive(ToolReceiveEvent event) {
    Player player = event.player();
    String toolId = event.toolId();
    ItemStack item = event.item();
    // Modify or cancel
    event.setCancelled(true);
}

@EventHandler
public void onExpire(SelfDestructExpireEvent event) {
    // Fired when a self-destruct item expires
}

@EventHandler
public void onSell(SellEvent event) {
    // Fired when items are sold via Sell Axe / Sell Wand
    double money = event.money();
    int itemsSold = event.itemsSold();
}

@EventHandler
public void onStatUpdate(StatisticsUpdateEvent event) {
    // Fired when a player's stats change
    if (event.type() == StatisticsUpdateEvent.StatType.BLOCKS
            && event.newValue() >= 100_000) {
        // Reward milestone
    }
}
```

### Implementing a custom ability

```java
public class LightningBreak implements Ability {

    @Override public String id() { return "LIGHTNING_BREAK"; }

    @Override
    public void onBreak(BlockBreakEvent event, ItemStack tool) {
        event.getBlock().getWorld()
             .strikeLightning(event.getBlock().getLocation());
    }
}

// Register
ToolsFastAPI.get().abilities().register(new LightningBreak());
```

Use it in a custom item YAML:

```yaml
abilities:
  - BREAK_3X3
  - LIGHTNING_BREAK    # your custom ability
```

---

## 📖 JavaDoc

Complete JavaDoc for all public types in `toolsfast-api` is available online:

<div align="center">

### 🔗 [**JavaDoc Online**](https://toolsfast.netlify.app/)

[![JavaDoc](https://img.shields.io/badge/JavaDoc-ToolsFast_API-C77DFF?style=for-the-badge&logo=read-the-docs&logoColor=white)](https://toolsfast.netlify.app/)

</div>

### Generate JavaDoc locally

```bash
git clone https://github.com/BinaryCodee/toolsfast.git
cd toolsfast
mvn -pl toolsfast-api -am javadoc:javadoc

# Output: toolsfast-api/target/reports/apidocs/index.html
```

### Public types reference

| Type | Kind | Description |
|---|---|---|
| `ToolsFastAPI` | interface | Main API entry point |
| `ToolsFastAPI.CustomItemManager` | interface | Custom item manager |
| `ToolsFastAPI.ToolRegistry` | interface | Tool registry |
| `ToolsFastAPI.SelfDestructManager` | interface | Self destruct manager |
| `ToolsFastAPI.StatisticsManager` | interface | Statistics manager |
| `ToolsFastAPI.ShopProviderRegistry` | interface | Shop provider registry |
| `ToolsFastAPI.ParticleEngine` | interface | Particle engine |
| `ToolsFastAPI.AbilityRegistry` | interface | Ability registry |
| `ToolsFastAPI.EnchantRegistry` | interface | Enchant registry |
| `ToolsFastAPI.PlaceholderRegistry` | interface | Placeholder registry |
| `CustomItem` | interface | Custom item contract |
| `ToolDefinition` | interface | Tool definition contract |
| `Ability` | interface | Ability contract |
| `TokenEnchant` | interface | Token enchant contract |
| `ShopProvider` | interface | Shop provider contract |
| `ParticleEffect` | interface | Particle effect contract |
| `PlaceholderProvider` | interface | Placeholder provider contract |
| `ToolsFastAPIHolder` | class | Static singleton holder |
| `ToolReceiveEvent` | class | Tool receive event |
| `SelfDestructExpireEvent` | class | Self destruct expire event |
| `SellEvent` | class | Sell event |
| `StatisticsUpdateEvent` | class | Statistics update event |
| `StatisticsUpdateEvent.StatType` | enum | Stat type enum |

---

## 🔌 Placeholders

Requires [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI). Prefix: `%toolsfast_<id>%`.

### Player statistics

| Placeholder | Output |
|---|---|
| `%toolsfast_blocks%` | Total blocks mined |
| `%toolsfast_money%` | Total money generated |
| `%toolsfast_trees%` | Total trees chopped |
| `%toolsfast_crops%` | Total crops harvested |
| `%toolsfast_items_sold%` | Total items sold |

### Held tool

| Placeholder | Output |
|---|---|
| `%toolsfast_tool%` | Held tool ID (or "Nessuno") |
| `%toolsfast_tool_level%` | Tool level (always "1" for standard tools) |
| `%toolsfast_tool_uses%` | Uses of held tool |

### Self destruct

| Placeholder | Output |
|---|---|
| `%toolsfast_remaining%` | Remaining time (e.g. `1h 23m 45s`) or empty |
| `%toolsfast_expired%` | `true` if expired, `false` otherwise |

### Leaderboard

| Placeholder | Output |
|---|---|
| `%toolsfast_top_blocks_1%` ... `%toolsfast_top_blocks_5%` | Top 5 by blocks |
| `%toolsfast_top_money_1%` ... `%toolsfast_top_money_5%` | Top 5 by money |
| `%toolsfast_top_trees_1%` ... `%toolsfast_top_trees_5%` | Top 5 by trees |

---

## ⚙️ Configuration

### `config.yml` (main)

```yaml
prefix: "&8[&#C77DFF&lToolsFast&8] &7"

database:
  mysql:
    enabled: false
    host: localhost
    port: 3306
    database: toolsfast
    user: root
    password: ""
    ssl: false

self-destruct:
  tick-interval-ticks: 20
  lore-update-ticks: 10
  expire:
    sound: BLOCK_GLASS_BREAK
    particle: END_ROD
    particle-count: 30

abilities:
  require-sneak: false
  tree-capitator:
    max-blocks: 500
  trench:
    size: 3
  tray:
    radius: 2

shop:
  provider: fallback

infinite-firework:
  cooldown-ms: 500

lightning-wand:
  cooldown-ms: 1000
```

### `messages.yml` (style)

```yaml
prefix: "&8[&#C77DFF&lToolsFast&8] &7"

tool-received:
  - ""
  - " &#C77DFF&lTOOLSFAST"
  - " &fHai ricevuto:"
  - " &#C77DFF%tool%"
  - ""

actionbar:
  mining: "&d⚒ &f+%blocks% blocchi"
  sell: "&a$%money% &7aggiunti"
  tree: "&2🌳 &fAlbero abbattuto"
```

All messages support HEX colors via `&#RRGGBB` syntax.

---

## 🏗️ Build from source

Requirements: JDK 21+, Maven 3.9+.

```bash
git clone https://github.com/BinaryCodee/toolsfast.git
cd toolsfast
mvn clean package
```

Output:

```
toolsfast-plugin/target/ToolsFast-1.0.0.jar          # full plugin (shaded)
toolsfast-api/target/toolsfast-api-1.0.0.jar         # API only
```

To build only the API module:

```bash
mvn -pl toolsfast-api -am clean package
```

To regenerate JavaDoc:

```bash
mvn -pl toolsfast-api -am javadoc:javadoc
# Output: toolsfast-api/target/reports/apidocs/
```

---

## 🧱 Architecture

### Multi-module NMS via ServiceLoader

ToolsFast uses Java's `ServiceLoader` to load the correct NMS implementation at runtime. Adding a new Minecraft version only requires adding a new module — no core plugin changes.

```
NmsFactory.get()
    │
    ├─ ServiceLoader.load(NmsAdapter.class)
    │       │
    │       ├─ FallbackAdapter      (from toolsfast-nms)
    │       ├─ NmsAdapter_v1_20     (from toolsfast-nms-1_20)
    │       └─ NmsAdapter_v1_21     (from toolsfast-nms-1_21)
    │
    └─ Returns adapter matching Bukkit version
```

### Performance optimizations

- **Self-destruct**: single batch task for all players, no per-item task
- **Statistics**: in-memory `ConcurrentHashMap` cache + async DB persistence
- **Database**: dedicated 4-thread pool, no main-thread blocking
- **Particles**: only sent to players within 64 blocks, max 200 requests/tick
- **Lore updates**: diff-checked, no redundant inventory writes
- **WorldGuard**: integration via reflection (no hard dependency)

---

## 📊 Statistics & Database

### Supported backends

- **SQLite** (default) — file at `plugins/ToolsFast/data.db`
- **MySQL** — enable in `config.yml` under `database.mysql.enabled: true`

### Schema

| Table | Purpose |
|---|---|
| `tf_stats` | Per-player statistics (blocks, money, trees, crops, items_sold) |
| `tf_tool_uses` | Per-tool use count per player |
| `tf_self_destruct` | Self-destruct records |

All writes are async via a 4-thread pool. Reads from in-memory cache (O(1)).

---

## 📜 Permissions

| Permission | Default | Description |
|---|---|---|
| `toolsfast.use` | true | Basic plugin use |
| `toolsfast.menu` | true | Open GUI |
| `toolsfast.selfdestruct` | op | Self-destruct command |
| `toolsfast.admin` | op | Give, reload, etc. |

---

## 🤝 Integration with other plugins

ToolsFast integrates as **soft-dependency** with:

| Plugin | Integration |
|---|---|
| [Vault](https://github.com/MilkBowl/Vault) | Economy (deposit/withdraw on sell) |
| [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) | 15+ built-in placeholders |
| [WorldGuard](https://github.com/EngineHub/WorldGuard) | Region protection check before break |
| [EconomyShopGUI](https://github.com/saitoxbes/EconomyShopGUI) | Via custom `ShopProvider` (implement API) |
| [ShopGUIPlus](https://github.com/PyvesB/ShopGUIPlus) | Via custom `ShopProvider` (implement API) |

Register your own `ShopProvider` via `api.shopProviders().register(...)`.

---

## 👤 Author

**BinaryCodee**

- GitHub: [@BinaryCodee](https://github.com/BinaryCodee)

---

<div align="center">

**Built with ❤️ for the Minecraft community**

If you find ToolsFast useful, consider ⭐ starring the repository!

[Report an issue](https://github.com/BinaryCodee/toolsfast/issues) · [Request a feature](https://github.com/BinaryCodee/toolsfast/issues) · [Releases](https://github.com/BinaryCodee/toolsfast/releases)

</div>
