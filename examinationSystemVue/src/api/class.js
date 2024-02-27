import http from "@/utils/http";

const CLASS_STR = '/class';

const classObj = {
    getAllClassByIdPage(params) {
        return http.get(`${CLASS_STR}/getAllClassByIdPage`, params);
    },
    removeClass(params) {
        return http.delete(`${CLASS_STR}/removeClass/` + params);
    },
    batchRemoveClass(params) {
        return http.post(`${CLASS_STR}/batchRemoveClass`, params);
    },
    createClassByName(params) { 
        return http.post(`${CLASS_STR}/createClassByName`, params);
    },
    getAllClassByTeacherId(params) {
        return http.get(`${CLASS_STR}/getAllClassByTeacherId/`+ params);
    },
    getClassByIds(params) { 
        return http.post(`${CLASS_STR}/getClassByIds`, params);
    },
    getClassByToken() {
        return http.get(`${CLASS_STR}/getClassByToken`);
    },
    addClass(params) { 
        return http.post(`${CLASS_STR}/addClass`, params);
    },
    bowOutClass() {
        return http.delete(`${CLASS_STR}/bowOutClass`)
    }
}

export default classObj;
