package com.moscichowski.WebWonders

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/")
class HelloController {

    @RequestMapping(method = [RequestMethod.GET])
    fun index(): String {
        return "Greetings from Spring Boot!"
    }

    @RequestMapping(method = [RequestMethod.POST])
    fun postTest(@RequestBody load: Payload): String {
        return "Greetings ${load.test} == ${load.smth}"
    }
}

data class Payload(val test: String, val smth: Int)