//所有请求接口集合
import request from '@/utils/request'

// 获取支付状态
export const reqPay = id => request({ url: '/query/' + id, method: 'get' })
