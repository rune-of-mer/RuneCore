常に日本語で返してください．

----

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

RuneCore is a Minecraft Paper server plugin (core plugin for "Rune of Mer" server) written in Kotlin. It targets Paper API 1.21 and requires Java 21.

## Build Commands

```bash
# Build plugin JAR (includes ktlint format/check)
./gradlew buildPlugin

# Build shadow JAR only
./gradlew shadowJar

# Format code with ktlint
./gradlew ktlintFormat

# Check code style
./gradlew ktlintCheck
```

## Development Server

The `./x` script manages the Docker-based debug environment (Paper + MariaDB):

```bash
./x start     # Build plugin and start debug server
./x stop      # Stop containers
./x restart   # Restart minecraft container
./x clean     # Stop and remove volumes
./x rcon      # Open RCON console
./x logs      # View minecraft logs
```

## Architecture

### Package Structure (`org.lyralis.runeCore`)

- `command/` - Custom command system using Paper's LifecycleEventManager
    - `annotation/` - `@PlayerOnlyCommand`， `@CommandPermission` annotations
    - `register/` - `RuneCommand` interface， `CommandRegistry`， `CommandResult`， `RuneCommandContext`
    - `impl/` - Command implementations (naming: `Rune{Feature}Command`)
- `component/` - Message utilities and player actions
- `config/` - YAML configuration loading with data classes
- `database/` - MariaDB integration via Exposed ORM
    - `repository/` - Data access layer with `RepositoryResult` sealed class
    - `table/` - Exposed table definitions
    - `model/` - Data classes
    - `impl/` - Service implementations (e.g.， experience system)
- `gui/` - GUI system using invUI DSL
- `item/` - Custom item system with item registry
- `listener/` - Bukkit event listeners
- `permission/` - Permission system using sealed classes

### Command System

Commands implement `RuneCommand` interface， return `CommandResult` variants， and are registered in `RuneCore.kt` via `CommandRegistry`. Use `@PlayerOnlyCommand` for player-only commands (enables `context.playerOrThrow`). Use `@CommandPermission` for permission-gated commands.

Subcommand implementations should be placed in subdirectories within `impl/` (e.g.， `impl/experience/` for experience-related commands).

```kotlin
@PlayerOnlyCommand
class RuneExampleCommand : RuneCommand {
    override val name = "example"
    override val description = "An example command"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        return CommandResult.Success("Command executed!")
    }
}
```

### Database System

#### Database Overview

RuneCore uses MariaDB for data persistence. The `runecore_db` database contains the following tables，all using player UUID as primary key:

- **Players** - Base player data including level， experience， and balance
- **PlayerStats** - Player statistics such as kills， mob kills， and deaths

#### Data Access Pattern

Database operations follow a layered architecture:

1. **Repository Layer** - Located in `org.lyralis.runeCore.database.repository`
    - Direct database access using Exposed ORM
    - Returns `RepositoryResult` sealed class for type-safe error handling
    - Methods should use single UPDATE operations with result checking rather than separate existence checks

2. **Service Layer** - Located in `org.lyralis.runeCore.database.impl`
    - Business logic and higher-level operations
    - Coordinates multiple repository calls
    - Handles caching and state management
    - Returns plain values or Kotlin types (not wrapped in RepositoryResult)

```kotlin
// Repository - Low-level database access
class PlayerRepository {
    fun getExperience(uuid: UUID): RepositoryResult<ULong> {
        return try {
            transaction {
                Players.selectAll()
                    .where { Players.uuid eq uuid }
                    .map { it[Players.experience] }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player not found")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
    }
}

// Service - High-level business logic
class ExperienceService(
    private val playerRepository: PlayerRepository
) {
    fun grantExperience(player: Player، amount: ULong): ULong? {
        val uuid = player.uniqueId
        when (val result = playerRepository.addExperience(uuid، amount)) {
            is RepositoryResult.Success -> {
                // Perform additional operations
                return getExperience(uuid)
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to grant experience: ${result.exception.message}")
                return null
            }
            else -> return null
        }
    }
}
```

#### RepositoryResult Pattern

All repository methods return `RepositoryResult`، a sealed class for type-safe error handling. Always use `when` expressions to handle all cases:

```kotlin
when (val result = playerRepository.findByUUID(uuid)) {
    is RepositoryResult.Success -> {
        // Access data via result.data
        val playerData = result.data
        logger.info("Player level: ${playerData.level}")
    }
    is RepositoryResult.NotFound -> {
        logger.warning("Player not found: ${result.message}")
    }
    is RepositoryResult.InsufficientBalance -> {
        logger.warning("Insufficient balance: ${result.current} < ${result.required}")
    }
    is RepositoryResult.Error -> {
        logger.severe("Database error: ${result.exception.message}")
    }
}
```

#### Database Best Practices

- Always handle `RepositoryResult` exhaustively using `when` expressions
- Use single UPDATE operations with result checking: `if (updated > 0) Success else NotFound`
- Never use direct SQL queries; use Exposed ORM exclusively
- Implement caching in the Service layer for frequently accessed data
- Use UUID (from `java.util`) for all player identification， not Minecraft's username

### Message Components

Use extension functions from `MessageComponent.kt` for consistent styling: `"text".systemMessage()`， `"text".errorMessage()`， `"text".infoMessage()`.

### ActionBar Notifications

Use `ActionBarManager.showTemporaryNotification()` for displaying status information in the action bar. Do not use `Player.sendActionBar()` directly as it conflicts with status bar display.

```kotlin
ActionBarManager.showTemporaryNotification(player، "+50 EXP".infoMessage())
```

### GUI System

GUI implementation uses invUI with a custom DSL. All GUI generation methods are extension functions on `Player`.

**Standard GUI:**
```kotlin
player.openGui {
    title = "Settings Menu"
    rows = 3

    structure {
        +"# # # # # # # # #"
        +"# A . B . C . D #"
        +"# # # # # # # # #"
    }

    decoration('#'، Material.BLACK_STAINED_GLASS_PANE)

    item('A') {
        material = Material.DIAMOND_SWORD
        displayName = "Combat Settings"
        onClick { _ ->
            GuiResult.Success(Unit)
        }
    }
}
```

**Confirmation Dialog:**
```kotlin
player.showConfirmation {
    title = "Delete Confirmation"
    message = "Are you sure?"

    onResult { result ->
        when (result) {
            ConfirmationResult.Confirmed -> deleteItem()
            ConfirmationResult.Denied -> {}
            ConfirmationResult.Cancelled -> {}
        }
    }
}
```

**Paginated GUI:**
```kotlin
player.showPaginatedGui<CustomItem> {
    title = "Item List"
    items(ItemRegistry.getAllItems())

    render { item ->
        item.createItemStack()
    }

    onItemClick { item، _ ->
        GuiResult.Success(Unit)
    }
}
```

### Custom Items

Custom items implement the `CustomItem` interface and can optionally implement sub-interfaces from `CustomItemType` (Usable، Consumable، Equippable، Weapon، Armor).

All items must be registered in `ItemRegistry` during plugin initialization. Items use PersistentDataContainer (PDC) for identification.

```kotlin
object ExampleWeapon : CustomItem، CustomItemType.Weapon {
    override val id = "example_sword"
    override val displayName = "Example Sword"
    override val material = Material.DIAMOND_SWORD
    override val rarity = ItemRarity.RARE
    override val equipmentSlot = EquipmentSlot.HAND
    override val stats = CustomItemStatus(
        attackDamage = 8.0،
        attackSpeed = 1.0
    )
}
```

### Permission System

Permissions are defined as sealed classes in `Permission.kt`. Two steps are required to add new permissions:

1. Add the permission node to `paper-plugin.yml`
2. Define the corresponding sealed class in `Permission.kt`

Always use `Permission.Admin.SomePermission::class` with `@CommandPermission` annotation.

Use permission checker DSL for runtime checks:
```kotlin
player.requirePermissionAll {
    +Permission.Admin.DebugMode
    +Permission.Admin.DebugModeSwitchingGameMode
}
```

## Code Style

- Kotlin only (no Java)
- Follow Kotlin Coding Conventions
- No wildcard imports
- Commit messages: Conventional Commits in English، no gitmoji
- Run `./gradlew ktlintFormat` before commits
- Use `java.util.UUID` (not Kotlin's `Uuid`) for Minecraft API compatibility

## Message and Logger

- Player messages are always displayed in Japanese
- Console logs are always displayed in English
- Use appropriate helper functions: `.systemMessage()`، `.errorMessage()`، `.infoMessage()` for consistency
