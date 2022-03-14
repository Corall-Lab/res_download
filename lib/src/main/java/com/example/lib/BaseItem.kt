package com.example.lib

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

open class BaseItem : Serializable {
    @JSONField(name = "error")
    var error = 0

    @JSONField(name = "message")
    var message: String = ""

    //    @Override
    @JSONField(name = "data")
    var data: String = ""

}