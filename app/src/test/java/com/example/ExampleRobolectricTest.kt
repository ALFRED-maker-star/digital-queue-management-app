package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.QueueRepository
import com.example.model.TicketStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Before
  fun setUp() {
    QueueRepository.resetDemoQueues()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("QuickQueue", appName)
  }

  @Test
  fun `test join queue and ticket generation`() {
    val ticket = QueueRepository.joinQueue("cat_cashier", "John Doe", "Quick cash deposit")
    assertNotNull(ticket)
    assertTrue(ticket.ticketNumber.startsWith("A-"))
    assertEquals("John Doe", ticket.customerName)
    assertEquals("cat_cashier", ticket.categoryId)
  }

  @Test
  fun `test counter call next advances queue`() {
    val calledTicket = QueueRepository.callNext(1)
    assertNotNull(calledTicket)
    assertEquals(1, calledTicket?.assignedCounter)
    assertEquals(TicketStatus.SERVING, calledTicket?.status)
  }
}
