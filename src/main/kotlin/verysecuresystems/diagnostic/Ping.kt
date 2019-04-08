package verysecuresystems.diagnostic

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind

object Ping {
    operator fun invoke() = "/ping" bind Method.GET to { Response(Status.OK).body("pong") }
}