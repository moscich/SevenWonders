package com.moscichowski.WebWonders.common

import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.ServletException
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletResponse
import javax.servlet.ServletRequest
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.http.HttpServletRequest


//class CustomFilter : GenericFilterBean() {
//
//    @Throws(IOException::class, ServletException::class)
//    override fun doFilter(
//            request: ServletRequest,
//            response: ServletResponse,
//            chain: FilterChain) {
//        val servletRequest = (request as HttpServletRequest)
//        chain.doFilter(request, response)
//
//    }
//}
//
//@Configuration
//class CustomWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {
//
//    @Throws(Exception::class)
//    override fun configure(http: HttpSecurity) {
//        http.addFilterBefore(
//                CustomFilter(), BasicAuthenticationFilter::class.java)
//    }
//}