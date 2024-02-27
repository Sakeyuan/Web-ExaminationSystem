
import instance from './request'
 
const http ={
    /**
     * methods: 请求
     * @param url 请求地址 
     * @param params 请求参数
     */
    get(url,params){
        const config = {
            method: 'get',
            url:url
        }
        if(params) config.params = params
        return instance(config)
    },
    post(url,params){
        const config = {
            method: 'post',
            url: url,
            headers: {
                'Content-Type':'application/json' 
            }
        }
        if(params) config.data = params
        return instance(config)
    },
    put(url,params){
        const config = {
            method: 'put',
            url:url
        }
        if(params) config.params = params
        return instance(config)
    },
    delete(url,params){
        const config = {
            method: 'delete',
            url:url
        }
        if(params) config.params = params
        return instance(config)
    },
    postFile(url,params) {
        const config = {
            method: 'post',
            url: url,
            headers: {
                'Content-Type': 'multipart/form-data;charset=utf-8', 
            }
        }
        if(params) config.data = params
        return instance(config)
    },
    postRefreshToken(url, params) {
        const config = {
            method: 'post',
            url: url,
            headers: {
                'Authorization': `Bearer ${params}`, 
            }
        }
        if(params) config.data = params
        return instance(config)
    }
}

export default http