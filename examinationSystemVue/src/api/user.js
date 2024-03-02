import http from "@/utils/http";

const USER_STR = '/user'

const userObj = {
    getPersonalInfo() {
        return http.get(`${USER_STR}/getPersonalInfo`);
    },
    getAvatar() {
        return http.get(`${USER_STR}/getAvatar`);
    },
    sendCode(params) {
        return http.post(`${USER_STR}/sendVerificationCode`,params);
    },
    setPassword(params) {
        return http.post(`${USER_STR}/setPassword`,params);
    }
}

export default userObj;
