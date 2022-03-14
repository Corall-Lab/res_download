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
    const val CATEGORY = "https://api.voiceyouth.putaotec.com/voice/category"
    const val PACKAGE = "https://api.voiceyouth.putaotec.com/voice/package"
    val header: String =
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwMWYzYmFlMS0zYTBkLTQxNWItYmY5NS0wNTFhNmY0NDcxNjEiLCJpYXQiOiIyMDIyLzMvMTEgODo0NjoxMSIsIm5hbWVpZCI6IjQ2Mzk1NjYiLCJ0eXAiOiIxIiwibmJmIjoxNjQ2OTg4MzcxLCJleHAiOjE2NDc1OTMxNzEsImlzcyI6InRlc3RfbiIsImF1ZCI6InRlc3RfbiJ9.JUcZQf4Weh-Kg2oioUTfq_UFK7XXdZtul4U6rEyCLnY"
    var customMaps =
        "{\"ai\":\"963c5a7309e91344\",\"av\":\"2.1.2\",\"bundleid\":\"com.qgvoice.youth\",\"c\":\"oppo\",\"ci\":\"0ecc837c6078ca52b600f71fd660e70d\",\"imei\":\"\",\"ln\":\"zh_CN_#Hans\",\"mac\":\"B8:C3:85:7C:06:4C\",\"mt\":\"POT-AL00a\",\"n\":\"魔音变声器青春版\",\"ns\":1,\"oaid\":\"9ff767ff-0bff-9782-be7d-7efdd7bcf89f\",\"pid\":\"1\",\"sign\":\"5A:D0:73:CC:6C:B4:5F:EA:38:88:24:40:A9:BF:7B:68:DF:7F:CB:8F\",\"sng\":\"qwejuishkjadoqkklansh\",\"sv\":\"10\",\"ts\":1646991511}"
    const val CODE_SUCCESS = 200

    @JvmStatic
    fun main(args: Array<String>) {

    }

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
            .addHeader("pkg", "com.qgvoice.youth")
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
        stringBuilder.append("&bundleid=").append("com.qgvoice.youth")
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