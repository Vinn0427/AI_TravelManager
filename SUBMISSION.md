# 作业提交说明（AI_TravelManager）

- GitHub 仓库地址：[AI_TravelManager](https://github.com/Vinn0427/AI_TravelManager)

本说明仅包含“如何下载并运行 Docker 镜像”的必要步骤。

---

## 一、从镜像仓库拉取并运行

请根据助教提供的镜像仓库地址拉取镜像：

```bash
# 登录
docker login crpi-c9tfawa5klaz0t0o.cn-hangzhou.personal.cr.aliyuncs.com
# 拉取镜像
docker pull crpi-c9tfawa5klaz0t0o.cn-hangzhou.personal.cr.aliyuncs.com/vinn/travel_manager:latest

# 运行容器（注入必要的环境变量）
docker run -d --name travel_manager -p 8080:8080 -e DASHSCOPE_API_KEY="sk-0f7eee26ffc44b10a5def1662c0680c6" -e MAP_API_KEY="80d8e9044a2286b10d9409609c4b7130" crpi-c9tfawa5klaz0t0o.cn-hangzhou.personal.cr.aliyuncs.com/vinn/travel_manager:latest

# 访问应用
# 后端将托管前端静态资源，浏览器访问：
# http://localhost:8080
```

参数说明：
- `DASHSCOPE_API_KEY`：阿里百炼大模型平台 API Key：sk-0f7eee26ffc44b10a5def1662c0680c6
- `MAP_API_KEY`：高德地图 JS API Key：80d8e9044a2286b10d9409609c4b7130

---

如需更多项目说明（功能列表、目录结构、接口说明等），请参考仓库首页的 README 文档。

