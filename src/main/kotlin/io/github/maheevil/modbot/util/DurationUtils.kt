/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.maheevil.modbot.util

import kotlin.time.Duration

fun durationToHuman(duration: Duration) : String{
    val minutes = duration.inWholeMinutes % 60 ; val hours = duration.inWholeHours % 24 ; val days = duration.inWholeDays

    return duration.toString()
            .replace("d", if(days.toInt() == 1) " day" else " days")
            .replace("h", if(hours.toInt() == 1) " hour" else " hours")
            .replace("m", if(minutes.toInt() == 1) " minute" else " minutes")
}

/*
fun durationToHuman(duration: Duration) : String{
    val minutes = duration.inWholeMinutes % 60
    val hours = duration.inWholeHours % 24
    val days = duration.inWholeDays

    return "${ if(days > 0) "$days Day${ if(days < 2) " " else "s "}" else ""}${ if(hours > 0) "$hours Hour${ if(hours < 2) " " else "s "}" else "" }${ if(minutes > 0) "$minutes Minute${ if(minutes < 2) "" else "s"}" else "" }"
}
*/
