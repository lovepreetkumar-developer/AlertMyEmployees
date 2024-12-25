package com.alert.me.firebase

import android.app.Activity
import com.alert.me.utils.CustomProgress
import com.alert.me.utils.showMessageDialog
import com.alert.me.utils.showToast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class FcmNotificationsSender(
    private var userFcmToken: String,
    private var title: String,
    private var message: String,
    private var mActivity: Activity
) {
    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey =
        "AAAAECvY1Gs:APA91bFPDYdVuqLhyVRhu4RYDqevmI8_t9i7jEa_MXszq-sF6E1DyqazmJBBRjqWQZ2mg69EOmtqcYt2NJXdC6U9m8IBJx1NJBvI33yl1frVEP6K2OatLma9by1BnoQdzHM2PuaL1KFb"

    fun sendNotification(mCustomProgress: CustomProgress) {
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notificationObject = JSONObject()
            notificationObject.put("title", title)
            notificationObject.put("message", message)
            notificationObject.put("icon", "icon") // enter icon that exists in drawable only
            notificationObject.put(
                "click_action",
                "USER_HOME_SCREEN"
            ) // enter icon that exists in drawable only
            mainObj.put("data", notificationObject)
            val request: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, postUrl, mainObj, Response.Listener {
                    mCustomProgress.hide()
                    mActivity.showMessageDialog("Notification sent successfully")
                    // code run is got response
                }, Response.ErrorListener {
                    mCustomProgress.hide()
                    mActivity.showToast("Notification failed.")
                    // code run is got error
                }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val header: MutableMap<String, String> = HashMap()
                        header["content-type"] = "application/json"
                        header["authorization"] = "key=$fcmServerKey"
                        return header
                    }
                }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}