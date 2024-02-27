import http from "@/utils/http";

const TITLE_STR = '/title';

const titleObj = {
    createTitle(params) {
        return http.post(`${TITLE_STR}/createTitle`, params)
    },
    getAllTitlesByTeacherId(params) {
        return http.post(`${TITLE_STR}/getAllTitlesByTeacherId`, params)
    },
    deleteTitleById(params) {
        return http.delete(`${TITLE_STR}/deleteTitleById/`+ params)
    },
    batchDeleteTitleById(params) {
        return http.post(`${TITLE_STR}/batchDeleteTitleById`,params)
    },
    getTitleById(params) {
        return http.post(`${TITLE_STR}/getTitleById`,params);
    },
    getAllTitleType() {
        return http.get(`${TITLE_STR}/getAllTitleType`);
    }
}

export default titleObj;
