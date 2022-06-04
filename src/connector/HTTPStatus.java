package connector;

public enum HTTPStatus {
    SC_OK(200,"OK"),
    SC_NOT_FOUND(404,"File Not Found");
    private int statusCode;
    private String reason;
    HTTPStatus(int statusCode, String reason){
        this.statusCode=statusCode;
        this.reason=reason;
    }
    public int getStatusCode(){
        return statusCode;
    }
    public String getReason(){
        return reason;
    }

}
