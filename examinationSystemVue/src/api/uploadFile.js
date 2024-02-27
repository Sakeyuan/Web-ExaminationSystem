import http from "@/utils/http";

const UPLOAD_STR = '/file';
const uploadObj = {
    uploadFace(params) {
        return http.postFile(`${UPLOAD_STR}/uploadFace`, params)
    },
    getFace(params) {
        return http.get(`${UPLOAD_STR}/getFace/`+ params)
    },
    getAvatar(params) {
        return http.get(`${UPLOAD_STR}/getAvatar/`+ params)
    },
    getAvatarByToken() {
        return http.get(`${UPLOAD_STR}/getAvatar`)
    },
    uploadPersonalInfo(params) {
        return http.postFile(`${UPLOAD_STR}/uploadPersonalInfo`, params)
    },
    faceRecognition(params) { 
        return http.postFile(`${UPLOAD_STR}/faceRecognition`, params)
    }
}
export default uploadObj;
