package com.epic.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.http.content.*
import io.ktor.server.locations.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<AuthenticationException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden)
        }
    
    }
    
    install(Locations) {
    }

    routing {
        get("/") {
            val items = listOf("cat", "dog", "rabbit")
            call.respondText(items.joinToString())
        }

        get("/five") {
            val items = listOf("five things")
            call.respondText(items.joinToString())
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
        get<MyLocation> {
                call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
            }
            // Register nested routes
            get<Type.Edit> {
                call.respondText("Inside $it")
            }
            get<Type.List> {
                call.respondText("Inside $it")
            }
    }
}
class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")
@Location("/type/{name}") data class Type(val name: String) {
    @Location("/edit")
    data class Edit(val type: Type)

    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}
