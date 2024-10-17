package com.peiyingr.yummyfit.Response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class YummyFitResponse {
    
    // private int status_code; // HTTP Status Code
    private Boolean error;
    private Boolean ok; 
    private String message = ""; // 錯誤訊息
    private Object data; // 回傳資料

    public boolean isOk() {
        return ok;
    }
}
