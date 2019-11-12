package verysecuresystems.api

import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.lens.Query
import verysecuresystems.*
import verysecuresystems.external.EntryLogger
import verysecuresystems.external.UserDirectory

class KnockKnockOO(private val userDirectory: UserDirectory,
                   private val entryLogger: EntryLogger,
                   private val inhabitants: Inhabitants) {

    fun getRoute(): ContractRoute {
        val getUsername = Query.map(::Username).required("username")
        val message = Body.auto<Message>().toLens()

        val userEntry: HttpHandler = { request ->
            val username = getUsername(request)
            userDirectory.lookup(username)?.name
                    ?.let {
                        if (inhabitants.add(it)) {
                            entryLogger.enter(it)
                            Response(Status.ACCEPTED).with(message of Message("Access granted"))
                        } else {
                            Response(Status.CONFLICT).with(message of Message("User is already inside building"))
                        }
                    }
                    ?: Response(Status.NOT_FOUND).with(message of Message("Unknown user"))
        }

        return "/knock" meta {
            queries += getUsername
            summary = "User enters the building"
            returning(Status.ACCEPTED, message to Message("Access granted"))
            returning(Status.NOT_FOUND, message to Message("Unknown user"))
            returning(Status.CONFLICT, message to Message("User is already inside building"))
            returning(Status.UNAUTHORIZED to "Incorrect key")
        } bindContract Method.POST to userEntry
    }

}