package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GGPacket
import com.example.data.model.PacketType
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GG Offline Chat", appName)
  }

  @Test
  fun `verify phone number is properly masked for privacy`() {
    val profile = UserProfile(
      displayName = "Arjun",
      mobileNumber = "9876543210",
      countryCode = "+91"
    )
    assertEquals("+91 ••••• ••210", profile.maskedPhoneNumber)
  }

  @Test
  fun `verify gg protocol packet serialization roundtrip`() {
    val packet = GGPacket(
      type = PacketType.CHAT_MSG,
      packetId = "test-uuid-1234",
      senderName = "Arjun",
      senderMaskedPhone = "+91 ••••• ••210",
      senderAvatar = 2,
      payload = "Hello over offline Bluetooth RFCOMM!"
    )
    val jsonString = packet.toJsonString()
    val parsed = GGPacket.fromJsonString(jsonString)

    assertNotNull(parsed)
    assertEquals(PacketType.CHAT_MSG, parsed?.type)
    assertEquals("test-uuid-1234", parsed?.packetId)
    assertEquals("Hello over offline Bluetooth RFCOMM!", parsed?.payload)
  }
}
