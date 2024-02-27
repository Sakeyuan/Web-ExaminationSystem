import http from "@/utils/http";

const REGISTER_STR = '/register';

const registerObj = {
    uploadRegisterInfo(params) {
        return http.post(`${REGISTER_STR}/uploadRegisterInfo`, params)
    },
    teacherRegister(params) {
        return http.post(`${REGISTER_STR}/teacherRegister`, params);
    }
}

export default registerObj;
