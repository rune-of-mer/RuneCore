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
  - `annotation/` - `@PlayerOnlyCommand`, `@CommandPermission` annotations
  - `register/` - `RuneCommand` interface, `CommandRegistry`, `CommandResult`, `RuneCommandContext`
  - `impl/` - Command implementations (naming: `Rune{Feature}Command`)
- `component/` - Message utilities and player actions
- `config/` - YAML configuration loading with data classes
- `database/` - MariaDB integration via Exposed ORM
  - `repository/` - Data access layer with `RepositoryResult` sealed class
  - `table/` - Exposed table definitions
  - `model/` - Data classes
  - `impl/` - Service implementations (e.g., experience system)
- `listener/` - Bukkit event listeners
- `permission/` - Permission system using sealed classes

### Command System

Commands implement `RuneCommand` interface, return `CommandResult` variants, and are registered in `RuneCore.kt` via `CommandRegistry`. Use `@PlayerOnlyCommand` for player-only commands (enables `context.playerOrThrow`). Use `@CommandPermission("node")` for permission-gated commands.

### Database Pattern

Repository methods return `RepositoryResult` sealed class - always handle with `when` expression for both `Success` and `Error` cases.

### Message Components

Use extension functions from `MessageComponent.kt` for consistent styling: `"text".systemMessage()`, `"text".errorMessage()`.

## Code Style

- Kotlin only (no Java)
- Follow Kotlin Coding Conventions
- No wildcard imports
- Commit messages: Conventional Commits in English, no gitmoji
- Run `./gradlew ktlintFormat` before commits
