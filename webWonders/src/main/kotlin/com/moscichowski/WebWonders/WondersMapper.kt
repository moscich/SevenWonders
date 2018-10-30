package com.moscichowski.WebWonders

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class WondersMapper: ObjectMapper() {
    init {
        super.registerModule(ActionJsonModule())
    }
}
