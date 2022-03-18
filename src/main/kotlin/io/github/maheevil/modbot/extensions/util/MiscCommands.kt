/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.maheevil.modbot.extensions.util

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.createInvite
import dev.kord.core.entity.channel.TextChannel
import io.github.maheevil.modbot.guildConfigDataMap
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
    }

    private suspend fun getInviteLink(channel: TextChannel, guild: GuildBehavior) : String{
        return "https://discord.gg/" + suspend { guildConfigDataMap[guild.id.toLong()]?.invite
                    ?: channel.invites.firstOrNull()?.code
                    ?: guild.invites.firstOrNull()?.code
                    ?: channel.createInvite {
                        temporary = false
                    }
        }.invoke()
    }
}