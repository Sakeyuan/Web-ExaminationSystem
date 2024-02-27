import http from "@/utils/http";

const IP_STR = '/ip';

const ipObj = {
    getIp() {
        return http.get(`${IP_STR}/getIp`)
    },
}

export default ipObj;
