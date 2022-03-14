package com.example.lib

import com.alibaba.fastjson.annotation.JSONField

class OriginAnchorItem : BaseItem() {
    @JSONField(name = "Id")
    var id = 0

    @JSONField(name = "Title")
    var title: String = ""

    @JSONField(name = "ImgUrl")
    var iconURL: String = ""

    @JSONField(name = "PackageCount")
    var packageCount: Int = 0

}