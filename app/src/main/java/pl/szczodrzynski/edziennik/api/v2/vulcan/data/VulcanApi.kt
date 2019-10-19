/*
 * Copyright (c) Kacper Ziubryniewicz 2019-10-19
 */

package pl.szczodrzynski.edziennik.api.v2.vulcan.data

import com.google.gson.JsonObject
import im.wangchao.mhttp.Request
import im.wangchao.mhttp.Response
import im.wangchao.mhttp.callback.JsonCallbackHandler
import io.github.wulkanowy.signer.signContent
import pl.szczodrzynski.edziennik.api.v2.*
import pl.szczodrzynski.edziennik.api.v2.models.ApiError
import pl.szczodrzynski.edziennik.api.v2.vulcan.DataVulcan
import pl.szczodrzynski.edziennik.utils.Utils.d
import java.net.HttpURLConnection
import java.util.*

open class VulcanApi(open val data: DataVulcan) {
    companion object {
        const val TAG = "VulcanApi"
    }

    val profileId
        get() = data.profile?.id ?: -1

    val profile
        get() = data.profile

    fun apiGet(tag: String, endpoint: String, method: Int = POST, payload: JsonObject? = null, onSuccess: (json: JsonObject) -> Unit) {
        d(tag, "Request: Librus/Api - ${data.fullApiUrl}/$endpoint")

        val finalPayload = JsonObject()
        finalPayload.addProperty("IdUczen", data.studentId)
        finalPayload.addProperty("IdOkresKlasyfikacyjny", data.studentSemesterId)
        finalPayload.addProperty("IdOddzial", data.studentClassId)
        finalPayload.addProperty("RemoteMobileTimeKey", System.currentTimeMillis() / 1000)
        finalPayload.addProperty("TimeKey", System.currentTimeMillis() / 1000 - 1)
        finalPayload.addProperty("RequestId", UUID.randomUUID().toString())
        finalPayload.addProperty("RemoteMobileAppVersion", VULCAN_API_APP_VERSION)
        finalPayload.addProperty("RemoteMobileAppName", VULCAN_API_APP_NAME)

        val callback = object : JsonCallbackHandler() {
            override fun onSuccess(json: JsonObject?, response: Response?) {
                if (json == null && response?.parserErrorBody == null) {
                    data.error(ApiError(TAG, ERROR_RESPONSE_EMPTY)
                            .withResponse(response))
                    return
                }

                // TODO: Vulcan error handling

                if (json == null) {
                    data.error(ApiError(tag, ERROR_RESPONSE_EMPTY)
                            .withResponse(response))
                    return
                }

                try {
                    onSuccess(json)
                } catch (e: Exception) {
                    data.error(ApiError(tag, EXCEPTION_VULCAN_API_REQUEST)
                            .withResponse(response)
                            .withThrowable(e)
                            .withApiResponse(json))
                }
            }

            override fun onFailure(response: Response?, throwable: Throwable?) {
                data.error(ApiError(tag, ERROR_REQUEST_FAILURE)
                        .withResponse(response)
                        .withThrowable(throwable))
            }
        }

        Request.builder()
                .url("${data.fullApiUrl}$endpoint")
                .userAgent(VULCAN_API_USER_AGENT)
                .addHeader("RequestCertificateKey", data.apiCertificateKey)
                .addHeader("RequestSignatureValue",
                        signContent(VULCAN_API_PASSWORD, data.apiCertificatePfx, finalPayload.toString()))
                .apply {
                    when (method) {
                        GET -> get()
                        POST -> post()
                    }
                }
                .setJsonBody(finalPayload)
                .allowErrorCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .allowErrorCode(HttpURLConnection.HTTP_FORBIDDEN)
                .allowErrorCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .callback(callback)
                .build()
                .enqueue()
    }
}
