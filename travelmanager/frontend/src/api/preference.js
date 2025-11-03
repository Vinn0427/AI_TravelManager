import request from '@/utils/request'

export const preferenceApi = {
  get() {
    return request({
      url: '/preference',
      method: 'get'
    })
  },
  save(data) {
    return request({
      url: '/preference/save',
      method: 'post',
      data
    })
  }
}


