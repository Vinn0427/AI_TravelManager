package com.vinn.travelmanager.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinn.travelmanager.dto.AIGenerateRequestDTO;
import com.vinn.travelmanager.dto.PlanSaveDTO;
import com.vinn.travelmanager.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务实现类（调用阿里百炼大模型）
 */
@Service
public class AIServiceImpl implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);

    @Value("${ai.dashscope.api-key:your-api-key}")
    private String apiKey;

    @Value("${ai.dashscope.base-url:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String baseUrl;

    @Value("${ai.dashscope.model:qwen-turbo}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 配置RestTemplate的超时设置
    public AIServiceImpl() {
        this.restTemplate = createRestTemplate();
    }
    
    /**
     * 创建配置了超时的RestTemplate
     */
    private RestTemplate createRestTemplate() {
        RestTemplate template = new RestTemplate();
        
        // 使用Spring内置的SimpleClientHttpRequestFactory配置超时
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
            new org.springframework.http.client.SimpleClientHttpRequestFactory();
        
        // 连接超时时间（建立连接的时间）：10秒
        factory.setConnectTimeout(java.time.Duration.ofSeconds(10));
        
        // 读取超时时间（等待响应的时间）：60秒（AI生成可能需要较长时间）
        factory.setReadTimeout(java.time.Duration.ofSeconds(60));
        
        template.setRequestFactory(factory);
        return template;
    }

    @Override
    public PlanSaveDTO generateTravelPlan(AIGenerateRequestDTO request) {
        try {
            // 构建提示词
            String prompt = buildPrompt(request);

            // 调用阿里百炼API
            String aiResponse = callDashScopeAPI(prompt);

            // 解析AI返回的JSON
            PlanSaveDTO planDTO = parseAIResponse(aiResponse, request);

            return planDTO;
        } catch (Exception e) {
            logger.error("生成旅行计划失败", e);
            throw new RuntimeException("AI生成旅行计划失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String analyzeBudget(String prompt) {
        try {
            String content = callDashScopeAPI(prompt);
            // 预算分析返回为自然语言文本，直接返回
            return content;
        } catch (Exception e) {
            logger.error("预算分析失败", e);
            throw new RuntimeException("AI预算分析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(AIGenerateRequestDTO request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的旅行规划师。请根据以下需求生成一份详细的旅行计划。\n\n");
        prompt.append("需求信息：\n");
        prompt.append("- 目的地：").append(request.getDestination()).append("\n");
        prompt.append("- 出发日期：").append(request.getStartDate()).append("\n");
        prompt.append("- 旅行天数：").append(request.getDays()).append("天\n");
        prompt.append("- 预算：").append(request.getBudget()).append("元\n");
        prompt.append("- 同行人数：").append(request.getPeople()).append("人\n");
        
        if (request.getPreferences() != null && !request.getPreferences().isEmpty()) {
            prompt.append("- 旅行偏好：").append(String.join("、", request.getPreferences())).append("\n");
        }
        
        if (request.getRequirements() != null && !request.getRequirements().isEmpty()) {
            prompt.append("- 特殊需求：").append(request.getRequirements()).append("\n");
        }

        prompt.append("\n请严格按照以下JSON格式返回旅行计划（不要包含任何markdown格式，只返回纯JSON）：\n");
        prompt.append("{\n");
        prompt.append("  \"title\": \"旅行计划标题\",\n");
        prompt.append("  \"destination\": \"").append(request.getDestination()).append("\",\n");
        prompt.append("  \"startDate\": \"").append(request.getStartDate()).append("\",\n");
        prompt.append("  \"endDate\": \"结束日期（YYYY-MM-DD）\",\n");
        prompt.append("  \"totalDays\": ").append(request.getDays()).append(",\n");
        prompt.append("  \"budget\": ").append(request.getBudget()).append(",\n");
        prompt.append("  \"spots\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"dayNumber\": 1,\n");
        prompt.append("      \"name\": \"景点名称\",\n");
        prompt.append("      \"location\": \"景点位置描述\",\n");
        prompt.append("      \"latitude\": 纬度（数字，可选）,\n");
        prompt.append("      \"longitude\": 经度（数字，可选）\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"budgets\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"category\": \"交通\",\n");
        prompt.append("      \"amount\": 预算金额（数字）\n");
        prompt.append("    },\n");
        prompt.append("    {\n");
        prompt.append("      \"category\": \"住宿\",\n");
        prompt.append("      \"amount\": 预算金额（数字）\n");
        prompt.append("    },\n");
        prompt.append("    {\n");
        prompt.append("      \"category\": \"餐饮\",\n");
        prompt.append("      \"amount\": 预算金额（数字）\n");
        prompt.append("    },\n");
        prompt.append("    {\n");
        prompt.append("      \"category\": \"景点\",\n");
        prompt.append("      \"amount\": 预算金额（数字）\n");
        prompt.append("    },\n");
        prompt.append("    {\n");
        prompt.append("      \"category\": \"购物\",\n");
        prompt.append("      \"amount\": 预算金额（数字）\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"dailyGuides\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"dayNumber\": 1,\n");
        prompt.append("      \"guide\": \"第1天的详细路线指引文本。必须包含：1）交通方式推荐（如何到达景点、推荐交通路线）2）住宿推荐（推荐的酒店/民宿名称、位置、价格区间）3）景点游览路线（按时间顺序的景点推荐，包括景点名称、游览时间、门票信息）4）餐厅推荐（早餐、午餐、晚餐推荐，包括餐厅名称、特色菜、人均消费）。根据预算合理安排，确保内容详细实用。\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        prompt.append("\n【重要要求，必须严格遵守】：\n");
        prompt.append("1. 请确保预算总和不超过总预算。每个景点必须包含dayNumber字段（1到").append(request.getDays()).append("）。\n");
        prompt.append("2. 【必需】dailyGuides数组是必需字段，绝对不能省略！必须包含每一天（1到").append(request.getDays()).append("天）的路线指引。即使只有1天行程，也要生成dailyGuides数组。\n");
        prompt.append("3. 每天的guide字段必须是一段详细的文字描述（至少200字），必须包含以下四个部分：\n");
        prompt.append("   a) 交通方式：推荐当天的交通路线和方式（如：地铁、公交、出租车、步行等），包括具体的路线指引和预计时间\n");
        prompt.append("   b) 住宿推荐：推荐的住宿地点（如果是多天行程，第一天需推荐，后续几天说明是否需要更换住宿），包括酒店/民宿名称、位置、价格区间、预订建议\n");
        prompt.append("   c) 景点游览：按时间顺序列出当天推荐的景点，包括景点名称、最佳游览时间、门票价格或免费信息、游览时长、特色亮点、注意事项\n");
        prompt.append("   d) 餐厅推荐：推荐当天的早餐、午餐、晚餐餐厅，包括餐厅名称、推荐菜品、人均消费、位置信息、营业时间\n");
        prompt.append("4. 所有推荐必须考虑预算限制，提供性价比高的选择。\n");
        prompt.append("5. guide字段使用中文，语言自然流畅，便于用户阅读和理解。使用段落分隔，每个部分之间用换行分隔。\n");
        prompt.append("6. 【再次强调】JSON中必须包含dailyGuides字段，且数组长度必须等于旅行天数。如果不包含dailyGuides字段，生成的计划将无法使用！");

        return prompt.toString();
    }

    /**
     * 调用阿里百炼API
     */
    private String callDashScopeAPI(String prompt) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 构建请求体（按照阿里百炼API格式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            // input对象包含messages
            Map<String, Object> input = new HashMap<>();
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            input.put("messages", messages);
            requestBody.put("input", input);

            // parameters对象包含其他参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", 0.7);
            parameters.put("top_p", 0.9);
            parameters.put("result_format", "message");
            requestBody.put("parameters", parameters);

            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                entity,
                (Class<Map<String, Object>>) (Class<?>) Map.class
            );

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Object output = body.get("output");
                if (output instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> outputMap = (Map<String, Object>) output;
                    // 阿里百炼API返回格式：output.choices[0].message.content
                    Object choices = outputMap.get("choices");
                    if (choices instanceof List && !((List<?>) choices).isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> firstChoice = (Map<String, Object>) ((List<?>) choices).get(0);
                        @SuppressWarnings("unchecked")
                        Map<String, Object> messageObj = (Map<String, Object>) firstChoice.get("message");
                        String content = (String) messageObj.get("content");
                        
                        // 清理可能包含的markdown代码块标记
                        content = content.replace("```json", "").replace("```", "").trim();
                        
                        return content;
                    }
                } else {
                    // 如果output直接是字符串，直接返回
                    if (output instanceof String) {
                        String content = (String) output;
                        content = content.replace("```json", "").replace("```", "").trim();
                        return content;
                    }
                }
            }

            // 记录详细的错误信息
            logger.error("API调用失败，响应状态: {}, 响应体: {}", response.getStatusCode(), response.getBody());
            throw new RuntimeException("API调用失败: " + response.getBody());
        } catch (Exception e) {
            logger.error("调用阿里百炼API失败", e);
            throw new RuntimeException("调用AI服务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析AI返回的JSON
     */
    private PlanSaveDTO parseAIResponse(String aiResponse, AIGenerateRequestDTO request) {
        try {
            // 记录AI返回的原始JSON（用于调试）
            logger.info("AI返回的原始JSON（前500字符）: {}", 
                aiResponse.length() > 500 ? aiResponse.substring(0, 500) : aiResponse);
            
            // 解析JSON
            JsonNode rootNode = objectMapper.readTree(aiResponse);

            PlanSaveDTO planDTO = new PlanSaveDTO();
            planDTO.setDestination(rootNode.path("destination").asText(request.getDestination()));
            planDTO.setStartDate(LocalDate.parse(rootNode.path("startDate").asText(request.getStartDate())));
            planDTO.setTotalDays(rootNode.path("totalDays").asInt(request.getDays()));
            
            // 计算结束日期
            String endDateStr = rootNode.path("endDate").asText();
            if (endDateStr != null && !endDateStr.isEmpty()) {
                planDTO.setEndDate(LocalDate.parse(endDateStr));
            } else {
                planDTO.setEndDate(planDTO.getStartDate().plusDays(planDTO.getTotalDays() - 1));
            }

            // 设置预算
            double budgetValue = rootNode.path("budget").asDouble(request.getBudget());
            planDTO.setBudget(BigDecimal.valueOf(budgetValue));

            // 解析景点
            JsonNode spotsNode = rootNode.path("spots");
            List<PlanSaveDTO.SpotDTO> spots = new ArrayList<>();
            if (spotsNode.isArray()) {
                for (JsonNode spotNode : spotsNode) {
                    PlanSaveDTO.SpotDTO spotDTO = new PlanSaveDTO.SpotDTO();
                    spotDTO.setDayNumber(spotNode.path("dayNumber").asInt());
                    spotDTO.setName(spotNode.path("name").asText());
                    spotDTO.setLocation(spotNode.path("location").asText());
                    
                    if (spotNode.has("latitude") && !spotNode.path("latitude").isNull()) {
                        spotDTO.setLatitude(BigDecimal.valueOf(spotNode.path("latitude").asDouble()));
                    }
                    if (spotNode.has("longitude") && !spotNode.path("longitude").isNull()) {
                        spotDTO.setLongitude(BigDecimal.valueOf(spotNode.path("longitude").asDouble()));
                    }
                    
                    spots.add(spotDTO);
                }
            }
            planDTO.setSpots(spots);

            // 解析预算
            JsonNode budgetsNode = rootNode.path("budgets");
            List<PlanSaveDTO.BudgetDTO> budgets = new ArrayList<>();
            if (budgetsNode.isArray()) {
                for (JsonNode budgetNode : budgetsNode) {
                    PlanSaveDTO.BudgetDTO budgetDTO = new PlanSaveDTO.BudgetDTO();
                    budgetDTO.setCategory(budgetNode.path("category").asText());
                    budgetDTO.setAmount(BigDecimal.valueOf(budgetNode.path("amount").asDouble()));
                    budgets.add(budgetDTO);
                }
            }
            planDTO.setBudgets(budgets);

            // 解析每日路线指引
            JsonNode dailyGuidesNode = rootNode.path("dailyGuides");
            List<PlanSaveDTO.DailyGuideDTO> dailyGuides = new ArrayList<>();
            
            logger.info("检查dailyGuides字段是否存在: {}", rootNode.has("dailyGuides"));
            
            if (dailyGuidesNode != null && !dailyGuidesNode.isMissingNode()) {
                logger.info("dailyGuidesNode类型: {}, 是否为数组: {}", 
                    dailyGuidesNode.getNodeType(), dailyGuidesNode.isArray());
                
                if (dailyGuidesNode.isArray()) {
                    logger.info("dailyGuides数组大小: {}", dailyGuidesNode.size());
                    for (JsonNode guideNode : dailyGuidesNode) {
                        PlanSaveDTO.DailyGuideDTO guideDTO = new PlanSaveDTO.DailyGuideDTO();
                        int dayNumber = guideNode.path("dayNumber").asInt(0);
                        String guide = guideNode.path("guide").asText("");
                        
                        logger.info("解析路线指引 - 第{}天, 内容长度: {}", dayNumber, guide.length());
                        
                        if (dayNumber > 0 && !guide.isEmpty()) {
                            guideDTO.setDayNumber(dayNumber);
                            guideDTO.setGuide(guide);
                            dailyGuides.add(guideDTO);
                        } else {
                            logger.warn("跳过无效的路线指引 - dayNumber: {}, guide长度: {}", dayNumber, guide.length());
                        }
                    }
                } else {
                    logger.warn("dailyGuides不是数组类型，实际类型: {}", dailyGuidesNode.getNodeType());
                }
            } else {
                logger.warn("dailyGuides字段不存在或为空");
            }
            
            planDTO.setDailyGuides(dailyGuides);
            logger.info("最终解析的dailyGuides数量: {}", dailyGuides.size());

            return planDTO;
        } catch (Exception e) {
            logger.error("解析AI响应失败，原始响应内容: {}", aiResponse, e);
            // 如果解析失败，返回一个基本结构
            PlanSaveDTO planDTO = new PlanSaveDTO();
            planDTO.setDestination(request.getDestination());
            planDTO.setStartDate(LocalDate.parse(request.getStartDate()));
            planDTO.setTotalDays(request.getDays());
            planDTO.setEndDate(planDTO.getStartDate().plusDays(request.getDays() - 1));
            planDTO.setBudget(BigDecimal.valueOf(request.getBudget()));
            planDTO.setSpots(new ArrayList<>());
            planDTO.setBudgets(new ArrayList<>());
            planDTO.setDailyGuides(new ArrayList<>()); // 也要初始化dailyGuides
            return planDTO;
        }
    }
}

