package com.example.lib

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class AnchorItem : Serializable {
    @JSONField(name = "id")
    var id = 0

    @JSONField(name = "title")
    var title: String = ""

    @JSONField(name = "count")
    var packageCount: Int = 0

    @JSONField(name = "packages")
    var packageList: List<PackageItem> = arrayListOf()

}