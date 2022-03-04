package io.github.maheevil.modbot.util.config

import dev.kord.common.entity.Snowflake
import io.github.maheevil.modbot.guildConfigDataMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.nio.file.Paths

@Serializable
data class GuildConfigData(val joinLeaveLogsChannel: Snowflake?, val alertLogsChannel: Snowflake?, val modLogsChannel: Snowflake?, val invite: String?)

fun deserializeAndLoadFromJson(){
    val jsonConfigFile = File(Paths.get("").toAbsolutePath().toString().plus("/config/config.json")).readText(Charsets.UTF_8)
    guildConfigDataMap = Json.decodeFromString(jsonConfigFile)
}

fun serializeAndSaveToJson(){
    val serializedJson = Json.encodeToString(guildConfigDataMap)
    val path = Paths.get("").toAbsolutePath().toString().plus("/config/config.json")
    val fileWriter = FileWriter(path)
    fileWriter.write(serializedJson)
    fileWriter.close()
}

fun putToHashMap(guildId: Long ,guildConfigData: GuildConfigData){
    guildConfigDataMap[guildId] = guildConfigData
    serializeAndSaveToJson()
}
