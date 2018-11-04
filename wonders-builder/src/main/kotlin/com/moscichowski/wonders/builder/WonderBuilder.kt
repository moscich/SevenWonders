package com.moscichowski.wonders.builder

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.moscichowski.wonders.model.Wonder

class WonderBuilder {
    fun getWonders(): List<Wonder> {
        val resource = this.javaClass.classLoader.getResource("wonders.json")
        val json = resource.openStream().bufferedReader().use { it.readText() }
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(CardJsonModule())
        return objectMapper.readValue(json, object : TypeReference<List<Wonder>>() {})
    }
}