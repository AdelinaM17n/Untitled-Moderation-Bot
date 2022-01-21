package io.github.maheevil.modbot.extensions.util.suggestions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingCoalescingString
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.publicButton
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.addReaction
import dev.kord.common.Color
import dev.kord.core.behavior.edit
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.embed
import io.github.maheevil.modbot.TEST_SERVER_ID
import kotlinx.datetime.Clock


class SuggestionCommand : Extension() {
    override val name = "suggestion";

    override suspend fun setup() {
        publicSlashCommand {
            name = "suggestion"
            description = "suggestion related commands"
            guild(TEST_SERVER_ID)

            publicSubCommand(::CreateSubCommandArgs){
                name = "create"
                description = "create a suggestion"

                action{
                    if (guild == null){
                        respond { content = "This is a guild only command" }
                        return@action
                    }

                    val messageID = respond {
                        embed {
                            title = arguments.title
                            color = Color(0x09850b)
                            field("Suggestion",false){arguments.suggestionString}
                            field("votes",false){"0"}
                            timestamp = Clock.System.now()
                        }
                    }.message.id

                    channel.getMessage(messageID).addReaction("\uD83D\uDC4D")
                    channel.getMessage(messageID).addReaction("\uD83D\uDC4E")

                    (channel.asChannel() as TextChannel).startPublicThreadWithMessage(messageID,arguments.title).addUser(user.id)
                }
            }
        }
    }

    inner class CreateSubCommandArgs : Arguments(){
        val suggestionString by coalescedString("suggestion", "the content of the suggestion")
        val title by defaultingCoalescingString("title", "Title at the top of the embed","Suggestion")
    }
}