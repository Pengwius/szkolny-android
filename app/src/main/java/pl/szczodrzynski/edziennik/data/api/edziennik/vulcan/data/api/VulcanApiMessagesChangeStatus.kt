/*
 * Copyright (c) Kacper Ziubryniewicz 2019-11-12
 */

package pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.data.api

import pl.szczodrzynski.edziennik.data.api.VULCAN_API_ENDPOINT_MESSAGES_CHANGE_STATUS
import pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.DataVulcan
import pl.szczodrzynski.edziennik.data.api.edziennik.vulcan.data.VulcanApi
import pl.szczodrzynski.edziennik.data.db.entity.Message.TYPE_SENT
import pl.szczodrzynski.edziennik.data.db.full.MessageFull
import pl.szczodrzynski.edziennik.data.db.entity.MessageRecipient
import pl.szczodrzynski.edziennik.data.db.entity.Metadata

class VulcanApiMessagesChangeStatus(
        override val data: DataVulcan,
        private val messageObject: MessageFull,
        val onSuccess: () -> Unit
) : VulcanApi(data) {
    companion object {
        const val TAG = "VulcanApiMessagesChangeStatus"
    }

    init {
        data.profile?.also { profile ->
            apiGet(TAG, VULCAN_API_ENDPOINT_MESSAGES_CHANGE_STATUS, parameters = mapOf(
                    "WiadomoscId" to messageObject.id,
                    "FolderWiadomosci" to "Odebrane",
                    "Status" to "Widoczna",
                    "LoginId" to data.studentLoginId,
                    "IdUczen" to data.studentId
            )) { _, _ ->

                if (!messageObject.seen) {
                    data.setSeenMetadataList.add(Metadata(
                            profileId,
                            Metadata.TYPE_MESSAGE,
                            messageObject.id,
                            true,
                            true,
                            messageObject.addedDate
                    ))
                }

                if (messageObject.type != TYPE_SENT) {
                    val messageRecipientObject = MessageRecipient(
                            profileId,
                            -1,
                            -1,
                            System.currentTimeMillis(),
                            messageObject.id
                    )

                    data.messageRecipientList.add(messageRecipientObject)
                }

                onSuccess()
            }
        }
    }
}
