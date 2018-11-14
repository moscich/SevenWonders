package com.moscichowski.WebWonders.common

import org.springframework.stereotype.Component
import java.util.*

@Component
class InvitationCodeGenerator {
    fun generateInviteCode(): String {
        return UUID.randomUUID().toString()
    }
}