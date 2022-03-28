/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.maheevil.modbot.utils

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
data class GuildConfigData(
        val joinLeaveLogsChannel: Snowflake?,
        val alertLogsChannel: Snowflake?,
        val modLogsChannel: Snowflake?,
        val invite: String?,
        val raidPingCount: Int?,
        val msgPreview: Boolean?
        )

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
