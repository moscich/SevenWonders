package com.moscichowski.WebWonders.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:3000"))//, methods = arrayOf(RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET), allowedHeaders = arrayOf("origin", "content-type", "accept", "x-requested-with"))
@RequestMapping("/security")
class SecurityController {

    @RequestMapping(method = [RequestMethod.POST])
    fun postAction(): Any? {
        return "ok"
    }

}