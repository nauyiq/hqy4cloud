package com.hqy.cloud.common.bind;

public class DataResponse extends MessageResponse {

    private static final long serialVersionUID = 8024449562864881L;
    
    public DataResponse(){
    	
    }

    private Object data;
    
    public DataResponse(boolean result, String message ) {
        super(result, message);
    }
    

    public DataResponse(boolean result, String message, Object data) {
        super(result, message);
        this.data = data;
    }
    
    public DataResponse(boolean result, String message, Integer code, Object data) {
        super(result, message,code);
        this.data = data;
    }


    public Object getData() {

        return data;
    }

    public void setData(Object data) {

        this.data = data;
    }
    

}
