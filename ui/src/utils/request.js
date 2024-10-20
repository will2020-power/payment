//封装请求库
import Axios from 'axios'

//通过create创建axios实例
const request = Axios.create({
    //配置公共请求地址
    baseURL: process.env.VUE_APP_API_HOST,
    //配置请求超时
    timeout: 3000
})


//导出创建的axios实例
export default ({method, url, data}) => {
    return request({
        method,
        url,
        //如果方法为get则使用params传参，否则使用data传值
        [method.toLowerCase() === 'get' ? 'params' : 'data']: data
    })
}
