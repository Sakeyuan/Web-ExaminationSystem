package com.sake.examination_system.util;

import com.google.gson.Gson;

public class WebRTCData {
    private String code;
    private String id;
    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static WebRTCData fromJson(String json) {
        return new Gson().fromJson(json, WebRTCData.class);
    }

    public static class Data {
        private Description description;

        public Description getDescription() {
            return description;
        }

        public void setDescription(Description description) {
            this.description = description;
        }
    }

    public static class Description {
        private String type;
        private String sdp;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSdp() {
            return sdp;
        }

        public void setSdp(String sdp) {
            this.sdp = sdp;
        }
    }

}
