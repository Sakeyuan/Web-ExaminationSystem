import http from "@/utils/http";

const PAPER_STR = '/paper';

const paperObj = {
    releasePaper(params) {
        return http.post(`${PAPER_STR}/releasePaper`, params)
    },
    getAllPaperListByTeacherId(params) {
        return http.post(`${PAPER_STR}/getAllPaperListByTeacherId`, params)
    },
    deletePaperById(params) { 
        return http.delete(`${PAPER_STR}/deletePaperById/`+ params)
    },
    batchDelById(params) {
        return http.post(`${PAPER_STR}/batchDelById`, params)
    },
    getUnFinishPaperByStudentId(params) { 
        return http.post(`${PAPER_STR}/getUnFinishPaperByStudentId`, params)
    },
    getFinishPaperByStudentId(params) { 
        return http.post(`${PAPER_STR}/getFinishPaperByStudentId`, params)
    },
    getNoCorrectPaper(params) {
        return http.post(`${PAPER_STR}/getNoCorrectPaper`, params)
    },
    getFinishCorrectPaper(params) { 
        return http.post(`${PAPER_STR}/getFinishCorrectPaper`, params)
    },
    getOnePaperWithContentAndAnswer(params) {
        return http.get(`${PAPER_STR}/getOnePaperWithContentAndAnswer?`+ params)
    },
    getOnePaperWithContentAndScore(params) { 
        return http.get(`${PAPER_STR}/getOnePaperWithContentAndScore?`+ params)
    },
    getOnePaperWithContentAndScoreAndEmpty(params) {
        return http.get(`${PAPER_STR}/getOnePaperWithContentAndScoreAndEmpty?`+ params)
    },
    submitCorrectAnswer(params) { 
        return http.post(`${PAPER_STR}/submitCorrectAnswer`, params)
    },
    getStudentHomeData(params) { 
        return http.get(`${PAPER_STR}/getStudentHomeData/`+ params)
    },
    addPaperClass(params) {
        return http.post(`${PAPER_STR}/addPaperClass`, params)
    },
    favorite(params) {
        return http.post(`${PAPER_STR}/favorite`, params)
    },
    getFavorite(params) {
        return http.get(`${PAPER_STR}/getFavorite/`+ params)
    },
    cancelFavorite(params) { 
        return http.delete(`${PAPER_STR}/cancelFavorite/`+ params)
    },
    setPaperReleased(params) {
        return http.post(`${PAPER_STR}/setPaperReleased`, params)
    }
}

export default paperObj;
