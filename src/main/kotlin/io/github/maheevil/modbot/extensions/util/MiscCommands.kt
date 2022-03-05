package io.github.maheevil.modbot.extensions.util

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalBoolean
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalChannel
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalInt
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.createInvite
import io.github.maheevil.modbot.guildConfigDataMap
import io.github.maheevil.modbot.util.config.GuildConfigData
import io.github.maheevil.modbot.util.config.putToHashMap
import kotlinx.coroutines.flow.firstOrNull

class MiscCommands : Extension(){
    override val name = "misc"

    override suspend fun setup() {
        publicSlashCommand {
            name = "invite"
            description = "provides a invite link to this server"

            action{
                //Removed the guild check because this is a guild only slash command
                respond {
                    if(guild == null){
                        content = "This is guild only command"
                        return@action
                    }

                    content = getInviteLink(channel.fetchChannel() as TextChannel, guild!!)
                }
            }
        }

        publicSlashCommand {
            name = "config"
            description = "Allows changing the config for the server"

            check { hasPermission(Permission.ManageGuild) }

            publicSubCommand(::ConfigSetArgs){
                name = "set"
                description = "Set config values"

                check { hasPermission(Permission.ManageGuild) }

                action {
                    if (guild == null) {
                        respond { content = "This is a guild only command" }
                        return@action
                    }
                    if (arguments.alertLogs == null && arguments.modLogs == null && arguments.joinAndLeaveLogs == null && arguments.inviteCode == null && arguments.raidPingCount == null) {
                        respond { content = "Please enter atleast one value" }
                        return@action
                    }

                    val oldData: GuildConfigData? = guildConfigDataMap[guild!!.id.toLong()]
                    val newGuildConfigData: GuildConfigData = GuildConfigData(
                        arguments.joinAndLeaveLogs?.id ?: oldData?.joinLeaveLogsChannel,
                        arguments.modLogs?.id ?: oldData?.modLogsChannel,
                            arguments.alertLogs?.id ?: oldData?.alertLogsChannel,
                            arguments.inviteCode ?: oldData?.invite,
                        arguments.raidPingCount ?: oldData?.raidPingCount
                    )

                    putToHashMap(guild!!.id.toLong(),newGuildConfigData)

                    respond {
                        content = "Done!"
                    }
                }
            }

            publicSubCommand(::ConfigRemoveArgs){
                name = "remove"
                description = "Remove config values, by default it clears all configs."

                check { hasPermission(Permission.ManageGuild) }

                action {
                    if (guild == null) {
                        respond { content = "This is a guild only command" }
                        return@action
                    }
                    if (arguments.alertLogs == null && arguments.modLogs == null && arguments.joinAndLeaveLogs == null && arguments.inviteCode == null && arguments.raidPingCount == null) {
                        putToHashMap(guild!!.id.toLong(), GuildConfigData(null,null,null,null,null))
                        respond { content = "Cleared all values" }
                        return@action
                    }

                    val oldData: GuildConfigData? = guildConfigDataMap[guild!!.id.toLong()]
                    if(oldData == null){
                        respond { content = "There is no config entry" }
                        respond {  }
                    }
                    val newGuildConfigData: GuildConfigData = GuildConfigData(
                            if(arguments.joinAndLeaveLogs == true) null else oldData?.joinLeaveLogsChannel,
                            if(arguments.modLogs == true) null else oldData?.modLogsChannel,
                            if(arguments.alertLogs == true) null else oldData?.alertLogsChannel,
                            if(arguments.inviteCode == true) null else oldData?.invite,
                            if(arguments.raidPingCount == true) null else oldData?.raidPingCount
                    )

                    putToHashMap(guild!!.id.toLong(),newGuildConfigData)

                    respond {
                        content = "Done!"
                    }
                }
            }
        }
    }

    inner class ConfigSetArgs : Arguments(){
        val joinAndLeaveLogs by optionalChannel {
            name = "JoinAndLeaveLogs"
            description = "The channel where join and leave logs will be posted"
        }
        val modLogs by optionalChannel {
            name = "ModLogs"
            description = "The channel where moderation logs will be posted"
        }
        val alertLogs by optionalChannel {
            name = "AlertLogs"
            description = "The channel where auto-moderation alerts logs will be posted"
        }
        val inviteCode by optionalString {
            name = "Invite"
            description = "The invite code for the invite command"
        }
        val raidPingCount by optionalInt {
            name = "RaidPingCount"
            description = "Maximum pings allowed before anti-raid triggers. leave null to turn this off"
        }
    }

    inner class ConfigRemoveArgs : Arguments(){
        val joinAndLeaveLogs by optionalBoolean {
            name = "JoinAndLeaveLogs"
            description = "The channel where join and leave logs will be posted"
        }
        val modLogs by optionalBoolean {
            name = "ModLogs"
            description = "The channel where moderation logs will be posted"
        }
        val alertLogs by optionalBoolean {
            name = "AlertLogs"
            description = "The channel where auto-moderation alerts logs will be posted"
        }
        val inviteCode by optionalBoolean {
            name = "Invite"
            description = "The invite code for the invite command"
        }
        val raidPingCount by optionalBoolean {
            name = "RaidPingCount"
            description = "Maximum pings allowed before anti-raid triggers. leave null to turn this off"
        }
    }

    suspend fun getInviteLink(channel: TextChannel, guild: GuildBehavior) : String{
        return "https://discord.gg/" + suspend { guildConfigDataMap[guild.id.toLong()]?.invite
                    ?: channel.invites.firstOrNull()?.code
                    ?: guild.invites.firstOrNull()?.code
                    ?: channel.createInvite {
                        reason = "AutoGenerated Invite"
                        temporary = false
                    }.code
        }.invoke()
    }
}