package verysecuresystems.api

import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.http4k.testing.assertApproved
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import verysecuresystems.*
import verysecuresystems.external.EntryLogger
import verysecuresystems.external.UserDirectory

@ExtendWith(JsonApprovalTest::class)
class KnockKnockOOTest {

    @Test
    fun `user is accepted`(approver: Approver) {
        val userDirectory = mockk<UserDirectory>()
        val entryLogger = mockk<EntryLogger>()
        val inhabitants = mockk<Inhabitants>()

        val knockknock = KnockKnockOO(userDirectory, entryLogger, inhabitants)

        val username = Username("bob")
        val entry = UserEntry("bob", true, 0)

        every { userDirectory.lookup(username) } returns User(Id(1), Username("bob"), EmailAddress("a@b"))
        every { inhabitants.add(username) } returns true
        every { entryLogger.enter(username) } returns entry

        val app = knockknock.getRoute()

        approver.assertApproved(app(Request(Method.POST, "/knock").query("username", "bob")), Status.ACCEPTED)
    }

}