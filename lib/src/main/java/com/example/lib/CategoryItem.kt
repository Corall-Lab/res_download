package com.example.lib

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

class CategoryItem: Serializable {
    @JSONField(name = "id")
    var id = 0

    @JSONField(name = "title")
    var title: String = ""

    @JSONField(name = "imgUrl")
    var imgUrl: String = ""

    @JSONField(name = "anchors")
    var anchorList: List<AnchorItem> = arrayListOf()
}