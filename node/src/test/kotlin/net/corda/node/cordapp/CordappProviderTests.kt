package net.corda.node.cordapp

import net.corda.core.node.services.AttachmentStorage
import net.corda.node.internal.cordapp.CordappLoader
import net.corda.node.internal.cordapp.CordappProvider
import net.corda.testing.node.MockAttachmentStorage
import org.junit.Assert
import org.junit.Test

class CordappProviderTests {
    @Test
    fun `isolated jar is loaded into the attachment store`() {
        val attachmentStore = MockAttachmentStorage()
        val isolatedJAR = this::class.java.getResource("isolated.jar")!!
        val loader = CordappLoader.createDevMode(listOf(isolatedJAR))
        val provider = CordappProvider(attachmentStore, loader)

        provider.start()
        val maybeAttachmentId = provider.getCordappAttachmentId(provider.cordapps.first())

        Assert.assertNotNull(maybeAttachmentId)
        Assert.assertNotNull(attachmentStore.openAttachment(maybeAttachmentId!!))
    }

    @Test
    fun `empty jar is not loaded into the attachment store`() {
        val attachmentStore = MockAttachmentStorage()
        val isolatedJAR = this::class.java.getResource("empty.jar")!!
        val loader = CordappLoader.createDevMode(listOf(isolatedJAR))
        val provider = CordappProvider(attachmentStore, loader)

        provider.start()
        
        Assert.assertNull(provider.getCordappAttachmentId(provider.cordapps.first()))
    }

    @Test
    fun `test that we find a cordapp class that is loaded into the store`() {
        val attachmentStore = MockAttachmentStorage()
        val isolatedJAR = this::class.java.getResource("isolated.jar")!!
        val loader = CordappLoader.createDevMode(listOf(isolatedJAR))
        val provider = CordappProvider(attachmentStore, loader)
        val className = "net.corda.finance.contracts.isolated.AnotherDummyContract"

        val expected = provider.cordapps.first()
        val actual = provider.getCordappForClass(className)

        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual)
    }
}
