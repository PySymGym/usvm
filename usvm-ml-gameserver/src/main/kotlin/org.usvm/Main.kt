package org.usvm

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.cli.required
import org.usvm.errors.UnknownModeError
import org.usvm.gameserver.Searcher
import org.usvm.plugins.configureSockets

@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val parser = ArgParser("usvm.ML.GameServer.Runner", skipExtraArguments = true)
    val mode by parser.option(
        ArgType.Choice(listOf("server", "sendmodel"), { it }),
        fullName = "mode",
        description = "Mode to run application. EachStep --- to run each step validation, Model - to run model validation."
    )
    val port by parser.option(
        ArgType.Int,
        fullName = "port",
        shortName = "port",
        description = "Port to communicate with game client"
    ).default(8100)
    parser.parse(args)

    when (mode?.lowercase()) {
        "server" -> {
            embeddedServer(Netty, port = port) { module() }.start(wait = true)
        }

        "sendmodel" -> {
            val smParser = ArgParser("usvm.ML.GameServer.Runner", skipExtraArguments = true)
            val stepsToPlay by smParser.option(
                ArgType.Int,
                fullName = "stepstoplay",
                description = "Steps to play"
            ).required()
            val stepsToStart by smParser.option(
                ArgType.Int,
                fullName = "stepstostart",
                description = "Steps to start"
            ).required()

            class DefaultSearcher : Subcommand("defaultSearcher", "Default searcher") {
                val defaultSearcher by smParser.option(
                    ArgType.String,
                    fullName = "defaultsearcher",
                    description = "Default searcher"
                ).required()
                var result: Searcher = Searcher.BFS

                override fun execute() {
                    result = if (defaultSearcher.lowercase() == "bfs") Searcher.BFS else Searcher.DFS
                }
            }

            val defaultSearcher = DefaultSearcher()
            val assemblyFullName by smParser.option(
                ArgType.String,
                fullName = "assemblyfullname",
                description = "Assembly full name"
            ).required()
            val nameOfObjectToCover by smParser.option(
                ArgType.String,
                fullName = "nameofobjecttocover",
                description = "Name of object to cover"
            ).required()
            val mapName by smParser.option(
                ArgType.String,
                fullName = "mapname",
                description = "Map name"
            ).required()
            val outFolder by smParser.option(
                ArgType.String,
                fullName = "outfolder",
                description = "Out folder"
            ).required()
            val model by smParser.option(
                ArgType.String,
                fullName = "model",
                description = "Path to model"
            ).required()
            val useGPU by smParser.option(
                ArgType.Boolean,
                fullName = "usegpu",
                description = "Use gpu"
            ).default(false)
            smParser.subcommands(defaultSearcher)
            smParser.parse(args)
            sendModelRunner(
                port,
                stepsToPlay,
                stepsToStart,
                defaultSearcher.result,
                assemblyFullName,
                nameOfObjectToCover,
                mapName,
                outFolder,
                model,
                useGPU
            )
        }

        else -> throw UnknownModeError(mode ?: "")
    }
}

fun Application.module() {
    configureSockets()
}
