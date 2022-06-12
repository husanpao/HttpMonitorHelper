package org.example.HttpMonitorHelper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationTest {

    private Map<String, Answer> answers = new HashMap<>();
    private Answer answer;

    public void loadAnswers() {
        JSONArray arrays = JSONUtil.readJSONArray(FileUtil.file("answer.json"), Charset.defaultCharset());
        for (JSONObject o : arrays.jsonIter()
        ) {
            answers.put(o.getStr("content"), JSONUtil.toBean(o, Answer.class));
        }
    }

    public String Handle(String url, JSONObject body) {
        JSONObject data = body.getJSONObject("data");
        if (url.endsWith("regist/activity") || url.endsWith("regist/competition")) {
            data.set("memberName", "[小助手]" + data.getStr("memberName"));
        }
        if (url.endsWith("ques/startCompetition") || url.endsWith("ques/answerQues")) {
            Console.log("url:{}\ndata:{}", url, data.toString());
            JSONObject ques = data.getJSONObject("ques");
            String content = ques.getStr("content");
            if (answer != null) {
                JSONArray rightOptions = data.getJSONArray("rightOptions");
                answer.setRightOptions(JSONUtil.toList(rightOptions, String.class));
                answers.put(answer.getContent(), answer);
                Console.log("收录成功~：{}", JSONUtil.toJsonStr(answer));
                answer = null;
                JSONArray jsonStr = JSONUtil.parseArray(answers.values());
                FileUtil.move(FileUtil.file("answer.json"), FileUtil.file(StrUtil.format("{}.json", System.currentTimeMillis())), true);
                FileUtil.writeString(jsonStr.toString(), "answer.json", Charset.defaultCharset());
                loadAnswers();
            }

            if (answers.containsKey(content)) {
                Answer answer = answers.get(content);
                List<String> newopt = new ArrayList<>();
                for (String option : answer.getOptions()
                ) {
                    for (String right : answer.getRightOptions()) {
                        if (right.equals(option)) {
                            option = option + "(正确)";
                            break;
                        }
                    }
                    newopt.add(option);
                }
                ques.set("options", newopt);
            } else {
                Console.log("没有收录~：{}", ques.toString());
                if (answer == null) {
                    answer = JSONUtil.toBean(ques, Answer.class);
                }
            }
        }
        return body.toString();
    }

    @Test
    public void HandleTest() {
        loadAnswers();
        JSONObject data = JSONUtil.parseObj("{\n" +
                "    \"result\": {\n" +
                "        \"msg\": \"成功\"\n" +
                "    },\n" +
                "    \"data\": {\n" +
                "        \"ques\": {\n" +
                "            \"quesNo\": 1,\n" +
                "            \"options\": [\n" +
                "                \"生活设施\",\n" +
                "                \"福利设施\",\n" +
                "                \"安全设施\"\n" +
                "            ],\n" +
                "            \"quesTypeStr\": \"单选题\",\n" +
                "            \"quesId\": \"LChYuhJF0e33EBQk\",\n" +
                "            \"content\": \"生产经营单位新建、改建、扩建工程项目的（  ），必须与主体工程同时设计1、同时施工、同时投入生产和使用。\",\n" +
                "            \"quesType\": 1\n" +
                "        }\n" +
                "    }\n" +
                "}");
        Handle("https://aqy-app.lgb360.com:443/aqy/ques/startCompetition", data);
        data = JSONUtil.parseObj("{\n" +
                "    \"result\": {\n" +
                "        \"msg\": \"成功\"\n" +
                "    },\n" +
                "    \"data\": {\n" +
                "        \"answeredOptions\": [\n" +
                "            \"生活设施\"\n" +
                "        ],\n" +
                "        \"ques\": {\n" +
                "            \"quesNo\": 2,\n" +
                "            \"options\": [\n" +
                "                \"保障\",\n" +
                "                \"实现\",\n" +
                "                \"促进\"\n" +
                "            ],\n" +
                "            \"quesTypeStr\": \"单选题\",\n" +
                "            \"quesId\": \"BCREH1IIDIcuGBQ4\",\n" +
                "            \"content\": \"《安全生产法》总则第一条：为了加强安全生产工作，防止和减少生产安全事故，保障人民群众生命和财产安全，（  ）经济社会持续健康发展，制定本法。\",\n" +
                "            \"quesType\": 1\n" +
                "        },\n" +
                "        \"rightOptions\": [\n" +
                "            \"安全设施\"\n" +
                "        ]\n" +
                "    }\n" +
                "}");
        Handle("https://aqy-app.lgb360.com:443/aqy/ques/answerQues", data);
    }
}
