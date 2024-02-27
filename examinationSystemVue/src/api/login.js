import http from "@/utils/http";

const LOGIN_STR = '/login';

const loginObj = {
    login(params) {
        return http.post(`${LOGIN_STR}/uploadLoginInfo`, params)
    },
}

export default loginObj;
