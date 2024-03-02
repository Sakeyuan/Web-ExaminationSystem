import http from "@/utils/http";

const AUTH_STR = '/auth';

const authObj = {
    checkToken() {
        return http.post(`${AUTH_STR}/checkToken`)
    },
    refreshToken(params) {
        return http.postRefreshToken(`${AUTH_STR}/refreshToken`,params)
    },
    verifyCode(params) { 
        return http.post(`${AUTH_STR}/verifyCode`,params)
    }
}

export default authObj;
