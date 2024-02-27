import http from "@/utils/http";

const USER_STR = '/user'

const userObj = {
    getPersonalInfo() {
        return http.get(`${USER_STR}/getPersonalInfo`);
    },
    getAvatar() {
        return http.get(`${USER_STR}/getAvatar`);
    }
}

export default userObj;
