/**
 * 认证工具类
 */
export const authUtils = {
  // 设置token
  setToken(token) {
    localStorage.setItem('token', token)
  },

  // 获取token
  getToken() {
    return localStorage.getItem('token')
  },

  // 移除token
  removeToken() {
    localStorage.removeItem('token')
  },

  // 设置用户信息
  setUserInfo(userInfo) {
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },

  // 获取用户信息
  getUserInfo() {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  },

  // 移除用户信息
  removeUserInfo() {
    localStorage.removeItem('userInfo')
  },

  // 判断是否登录
  isLoggedIn() {
    return !!this.getToken()
  },

  // 清除所有认证信息
  clearAuth() {
    this.removeToken()
    this.removeUserInfo()
  }
}

