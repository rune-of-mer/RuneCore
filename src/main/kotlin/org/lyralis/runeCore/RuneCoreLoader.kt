package org.lyralis.runeCore

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository

/**
 * InvUI を Paper の library loader を通してロードするためのクラス
 *
 * Paper 1.20.5+ では Mojang-mapped runtime がデフォルトになり、
 * InvUI の inventory-access モジュールが正しくリマップされるために必要
 */
@Suppress("UnstableApiUsage")
class RuneCoreLoader : PluginLoader {
    override fun classloader(classpathBuilder: PluginClasspathBuilder) {
        val resolver = MavenLibraryResolver()

        // InvUI のリポジトリを追加
        resolver.addRepository(
            RemoteRepository
                .Builder(
                    "xenondevs",
                    "default",
                    "https://repo.xenondevs.xyz/releases/",
                ).build(),
        )

        // InvUI の依存関係を追加
        resolver.addDependency(
            Dependency(
                DefaultArtifact("xyz.xenondevs.invui:invui:pom:1.49"),
                null,
            ),
        )

        classpathBuilder.addLibrary(resolver)
    }
}
