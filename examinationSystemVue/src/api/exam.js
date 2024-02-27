import http from "@/utils/http";

const EXAM_STR = '/exam';

const examObj = {
    submitAnswers(params) {
        return http.post(`${EXAM_STR}/submitExam`, params)
    },


}

export default examObj;
