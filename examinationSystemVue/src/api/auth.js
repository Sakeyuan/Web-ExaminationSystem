import http from "@/utils/http";

const AUTH_STR = '/auth';

const authObj = {
    checkToken() {
        return http.post(`${AUTH_STR}/checkToken`)
    },
    refreshToken(params) {
        return http.postRefreshToken(`${AUTH_STR}/refreshToken`,params)
    }
}

export default authObj;
