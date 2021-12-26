package io.github.maheevil.modbot.extensions.moderation.autoModeration

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.common.entity.Permission
import dev.kord.core.event.message.MessageCreateEvent

class RaidProt : Extension() {
    override val name = "raidprot"
    override suspend fun setup() {
        event<MessageCreateEvent>{
            action{
                if(event.getGuild() == null || event.message.author == null || event.message.getAuthorAsMember()?.getPermissions()?.contains(Permission.MentionEveryone) == true)
                    return@action

                if(event.message.mentionedUserIds.count() > 20 ){
                    event.message.delete()
                    event.message.getAuthorAsMember()?.kick("Anti-Raid. more than 20 users pinged in one message")
                }
            }
        }
    }
}

