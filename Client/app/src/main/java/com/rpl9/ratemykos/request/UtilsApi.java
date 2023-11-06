package com.rpl9.ratemykos.request;


public class UtilsApi {
    public static final String BASE_URL_API = "http://localhost:3000";

    public static com.rpl9.ratemykos.request.BaseApiService getApiService(){
        return com.rpl9.ratemykos.request.RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

}
