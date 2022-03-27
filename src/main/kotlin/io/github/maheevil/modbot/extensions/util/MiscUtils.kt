package io.github.maheevil.modbot.extensions.util

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.isNullOrBot
import com.kotlindiscord.kord.extensions.utils.respond
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.createInvite
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.maheevil.modbot.guildConfigDataMap
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock

class MiscUtils : Extension() {
    override val name = "miscutils"
    private val urlPatternRegex = Regex(
            "https?:\\/\\/discord.com\\/channels\\/(\\d+)\\/(\\d+)\\/(\\d+)"
    )

    override suspend fun setup() {
        event<MessageCreateEvent>{
            action {
                if(event.message.author.isNullOrBot())
                    return@action

                event.message.respond {
                    for(matchResult in urlPatternRegex.findAll(event.message.content)){
                        val message = (event.getGuild()
                                ?.getChannelOrNull(Snowflake(matchResult.groupValues[2])) as TextChannel?)
                                ?.getMessageOrNull(Snowflake(matchResult.groupValues[3])) ?: continue
                        val messageAuthor = message.author ?: continue
                        embeds.add(getMessagePreviewEmbed(message,messageAuthor,matchResult.value))
                    }
                }
            }
        }

        publicSlashCommand(::MessagePreviewArgs) {
            name = "preview-message"
            description = "previews message from the message url"

            action {
                val matchResult = urlPatternRegex.find(arguments.messageUrl)
                if(matchResult == null){
                    respond { content = "Not valid url" }
                    return@action
                }
                val message = (guild
                        ?.getChannelOrNull(Snowflake(matchResult.groupValues[2])) as TextChannel?)
                        ?.getMessageOrNull(Snowflake(matchResult.groupValues[3])) ?: return@action
                val messageAuthor = message.author ?: return@action

                respond {
                    embeds.add(getMessagePreviewEmbed(message,messageAuthor,matchResult.value))
                }
            }
        }

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

    inner class MessagePreviewArgs : Arguments(){
        val messageUrl by string {
            name = "message_url"
            description = "url of the message for the preview"
        }
    }

    private fun getMessagePreviewEmbed(message: Message,messageAuthor: User,fullUrl: String) : EmbedBuilder{
        val embed = EmbedBuilder()
        embed.title = "Message Preview"
        embed.url = fullUrl
        embed.color = Color(0xd9d904)
        embed.author {
            name = messageAuthor.username
            icon = messageAuthor.avatar?.url
        }
        embed.description = message.content
        embed.image = message.attachments.firstOrNull()?.url
        embed.timestamp = Clock.System.now()
        return embed
    }

    private suspend fun getInviteLink(channel: TextChannel, guild: GuildBehavior) : String{
        return "https://discord.gg/" + suspend {
            guildConfigDataMap[guild.id.toLong()]?.invite
                ?: channel.invites.firstOrNull()?.code
                ?: guild.invites.firstOrNull()?.code
                ?: channel.createInvite {
                    temporary = false
                }
        }.invoke()
    }
}