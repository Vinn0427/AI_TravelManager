import request from '@/utils/request'

/**
 * 旅行计划API
 */
export const planApi = {
  // 保存旅行计划
  savePlan(data) {
    return request({
      url: '/plan/save',
      method: 'post',
      data
    })
  },

  // 获取旅行计划详情
  getPlanById(id) {
    return request({
      url: `/plan/${id}`,
      method: 'get'
    })
  },

  // 获取旅行计划列表
  getPlans(params) {
    return request({
      url: '/plan/list',
      method: 'get',
      params
    })
  },

  // 更新旅行计划
  updatePlan(id, data) {
    return request({
      url: `/plan/${id}`,
      method: 'put',
      data
    })
  },

  // 删除旅行计划
  deletePlan(id) {
    return request({
      url: `/plan/${id}`,
      method: 'delete'
    })
  }
}

