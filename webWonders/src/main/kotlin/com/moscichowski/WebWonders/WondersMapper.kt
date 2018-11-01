package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.builder.CardJsonModule
import org.springframework.stereotype.Component

@Component
class WondersMapper: ObjectMapper() {
    init {
        super.registerModule(ActionJsonModule())
        super.registerModule(CardJsonModule())
    }
}
