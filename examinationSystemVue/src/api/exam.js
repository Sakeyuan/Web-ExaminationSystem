import http from "@/utils/http";

const EXAM_STR = '/exam';

const examObj = {
    submitAnswers(params) {
        return http.post(`${EXAM_STR}/submitExam`, params)
    },
    getGradeDetail(params) {
        return http.get(`${EXAM_STR}/getGradeDetail/`+ params)
    }
}

export default examObj;
