import request from '@/utils/request'

export const budgetApi = {
  analyze() {
    return request({
      url: '/budget/analyze',
      method: 'get'
    })
  }
}


