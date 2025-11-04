/**
 * Web Speech API 语音识别工具类
 * 支持浏览器内置的语音转文字功能
 */
export class SpeechRecognitionUtil {
  constructor() {
    // 检测浏览器是否支持语音识别
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
    
    if (!SpeechRecognition) {
      throw new Error('您的浏览器不支持语音识别功能。请使用 Chrome、Edge 等现代浏览器。')
    }

    this.recognition = new SpeechRecognition()
    this.isRecording = false
    
    // 配置语音识别参数
    this.recognition.lang = 'zh-CN' // 中文识别
    this.recognition.continuous = false // 不连续识别
    this.recognition.interimResults = false // 不返回临时结果
    
    // 绑定事件处理
    this.recognition.onstart = () => {
      this.isRecording = true
    }
    
    this.recognition.onend = () => {
      this.isRecording = false
    }
    
    this.recognition.onerror = (event) => {
      this.isRecording = false
      throw new Error(`语音识别错误: ${event.error}`)
    }
  }

  /**
   * 开始语音识别
   * @returns {Promise<string>} 识别结果文本
   */
  start() {
    return new Promise((resolve, reject) => {
      if (this.isRecording) {
        reject(new Error('语音识别正在进行中'))
        return
      }

      let finalTranscript = ''

      this.recognition.onresult = (event) => {
        // 获取识别结果
        for (let i = event.resultIndex; i < event.results.length; i++) {
          const transcript = event.results[i][0].transcript
          if (event.results[i].isFinal) {
            finalTranscript += transcript
          }
        }
        
        if (finalTranscript) {
          resolve(finalTranscript.trim())
        }
      }

      this.recognition.onerror = (event) => {
        this.isRecording = false
        reject(new Error(`语音识别失败: ${event.error}`))
      }

      this.recognition.onend = () => {
        this.isRecording = false
        if (finalTranscript) {
          resolve(finalTranscript.trim())
        } else {
          reject(new Error('未识别到语音内容'))
        }
      }

      try {
        this.recognition.start()
      } catch (error) {
        reject(new Error(`启动语音识别失败: ${error.message}`))
      }
    })
  }

  /**
   * 停止语音识别
   */
  stop() {
    if (this.isRecording) {
      this.recognition.stop()
      this.isRecording = false
    }
  }

  /**
   * 检查浏览器是否支持语音识别
   */
  static isSupported() {
    return !!(window.SpeechRecognition || window.webkitSpeechRecognition)
  }
}

