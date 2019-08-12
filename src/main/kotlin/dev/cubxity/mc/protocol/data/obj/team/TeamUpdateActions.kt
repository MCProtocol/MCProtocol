/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.team

import dev.cubxity.mc.protocol.entities.Message

abstract class AbstractTeamAction

data class CreateTeamAction(
    var teamDisplayName: Message,
    var allowFriendlyFire: Boolean,
    var canSeeInvisiblesOnTeam: Boolean,
    var nameTagVisibility: String,
    var collisionRule: String,
    var teamColor: TeamColor,
    var teamPrefix: Message,
    var teamSuffix: Message,
    var entities: ArrayList<String>
) : AbstractTeamAction()

class RemoveTeamAction : AbstractTeamAction()

data class UpdateTeamInfoAction(
    var teamDisplayName: Message,
    var allowFriendlyFire: Boolean,
    var canSeeInvisiblesOnTeam: Boolean,
    var nameTagVisibility: String,
    var collisionRule: String,
    var teamColor: TeamColor,
    var teamPrefix: Message,
    var teamSuffix: Message
) : AbstractTeamAction()

data class AddPlayersAction(var entities: ArrayList<String>) : AbstractTeamAction()
data class RemovePlayersAction(var entities: ArrayList<String>) : AbstractTeamAction()