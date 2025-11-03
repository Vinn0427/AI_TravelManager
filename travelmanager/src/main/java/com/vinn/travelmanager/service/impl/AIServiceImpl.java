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

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        prompt.append("  ]\n");
        prompt.append("}\n");
        prompt.append("\n重要：请确保预算总和不超过总预算。每个景点必须包含dayNumber字段（1到").append(request.getDays()).append("）。");

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

            return planDTO;
        } catch (Exception e) {
            logger.error("解析AI响应失败: " + aiResponse, e);
            // 如果解析失败，返回一个基本结构
            PlanSaveDTO planDTO = new PlanSaveDTO();
            planDTO.setDestination(request.getDestination());
            planDTO.setStartDate(LocalDate.parse(request.getStartDate()));
            planDTO.setTotalDays(request.getDays());
            planDTO.setEndDate(planDTO.getStartDate().plusDays(request.getDays() - 1));
            planDTO.setBudget(BigDecimal.valueOf(request.getBudget()));
            planDTO.setSpots(new ArrayList<>());
            planDTO.setBudgets(new ArrayList<>());
            return planDTO;
        }
    }
}

