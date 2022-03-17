package com.example.lib

import com.alibaba.fastjson.JSON
import com.example.lib.DesInfoUtil.decrypt
import com.example.lib.DesInfoUtil.encrypt
import com.example.lib.DesInfoUtil.key
import com.example.lib.DesInfoUtil.toHexString
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

object NetWork {
    const val PACKAGE_NAME = ""

    const val CATEGORY = ""
    const val PACKAGE = ""
    val header: String =
        ""
    var customMaps =
        ""
    const val CODE_SUCCESS = 200

    @JvmStatic
    fun requestWithToken(url: String, jsonStr: String, callback: NetworkCallback) {
        val formBody = getLastUrl(jsonStr)
        val okHttpClient = OkHttpClient()
        val body = RequestBody.create("application/json".toMediaTypeOrNull(),formBody)
        var request: Request? = null
        request = Request.Builder()
            .url(url)
            .addHeader("Authorization", header)
            .addHeader("vapi", "1")
            .addHeader("pkg", PACKAGE_NAME)
            .post(body).build()
        val call = okHttpClient.newCall(request)
        try {
            val response = call.execute()
            if (response.isSuccessful && response.body != null) {
                if (response.code == CODE_SUCCESS) {
                    try {
                        val responseString = response.body!!.string()
                        val tmp = JSON.parseObject(responseString, BaseItem::class.java)
                        val desStr = decrypt(tmp.data, key)
                        val item = JSON.parseObject(desStr, BaseItem::class.java)
                        callback.callback(item)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getLastUrl(content: String?): String {
        return getLastUrl(content, false)
    }

    fun getStampsLastUrl(url: String, hasToken: Boolean): String {
        val stringBuilder = StringBuilder(url)
        if (url.contains("?")) {
            stringBuilder.append("&av=").append("2.1.2")
        } else {
            stringBuilder.append("?av=").append("2.1.2")
        }
        stringBuilder.append("&bundleid=").append(PACKAGE_NAME)
        stringBuilder.append("&c=").append("oppo")
        return stringBuilder.toString()
    }

    fun getLastUrl(content: String?, hasToken: Boolean): String {
        try {
            val maps: MutableMap<String, Any?> = HashMap()
            maps["data"] = content
            maps["query"] = customMaps
            val finalMap: MutableMap<String, Any> = HashMap()
            finalMap["data"] = toHexString(encrypt(JSON.toJSONString(maps), key))
            return JSON.toJSONString(finalMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}