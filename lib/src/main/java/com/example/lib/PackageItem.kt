package com.example.lib

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class PackageItem: Serializable {
    @JSONField(name = "id")
    var id = 0

    @JSONField(name = "title")
    var title: String = ""

    @JSONField(name = "sourceUrl")
    var sourceUrl: String = ""

    @JSONField(name = "size")
    var fileSize: String = ""

    @JSONField(name = "time")
    var playTime: String = ""
}