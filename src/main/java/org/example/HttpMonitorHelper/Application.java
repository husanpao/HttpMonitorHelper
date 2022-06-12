package org.example.HttpMonitorHelper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {
    private static final Log LOGGER = LogFactory.get();
    private static Map<String, Answer> answers = new HashMap<>();
    private static Answer answer;

    public static void main(String[] args) {
        loadAnswers();
        HttpUtil.createServer(1002)
                .addAction("/handle", (req, res) -> {
                    JSONObject obj = JSONUtil.parseObj(req.getBody());
                    String url = obj.getStr("url");
                    JSONObject data = obj.getJSONObject("data");
                    String result = Handle(url, obj.getJSONObject("data"));
                    res.write(result);
                })
                .start();
    }

    public static void loadAnswers() {
        answers.clear();
        File json = FileUtil.file("/root/httpMonitor/answer.json");
        if (!json.exists()) {
            json = FileUtil.file("answer.json");
        }
        JSONArray arrays = JSONUtil.readJSONArray(json, Charset.defaultCharset());
        for (JSONObject o : arrays.jsonIter()
        ) {
            answers.put(o.getStr("content"), JSONUtil.toBean(o, Answer.class));
        }
        LOGGER.debug("题库数量：{}", answers.size());
    }

    public static String Handle(String url, JSONObject body) {
        JSONObject data = body.getJSONObject("data");
        if (url.endsWith("regist/activity") || url.endsWith("regist/competition")) {
            data.set("memberName", "[小助手]" + data.getStr("memberName"));
        }
        if (url.endsWith("ques/startCompetition") || url.endsWith("ques/answerQues")) {
            LOGGER.debug("url:{}\ndata:{}", url, data.toString());

            if (answer != null) {
                JSONArray rightOptions = data.getJSONArray("rightOptions");
                answer.setRightOptions(JSONUtil.toList(rightOptions, String.class));
                answers.put(answer.getContent(), answer);
                LOGGER.debug("题库数量：{},收录成功~：{}", answers.size(), JSONUtil.toJsonStr(answer));
                answer = null;
                JSONArray jsonStr = JSONUtil.parseArray(answers.values());

                File json = FileUtil.file("/root/httpMonitor/answer.json");
                if (!json.exists()) {
                    json = FileUtil.file("answer.json");
                    FileUtil.move(json, FileUtil.file(StrUtil.format("back/{}.json", System.currentTimeMillis())), true);
                    FileUtil.writeString(jsonStr.toString(), "answer.json", Charset.defaultCharset());
                } else {
                    FileUtil.move(json, FileUtil.file(StrUtil.format("/root/httpMonitor/back/{}.json", System.currentTimeMillis())), true);
                    FileUtil.writeString(jsonStr.toString(), "/root/httpMonitor/answer.json", Charset.defaultCharset());
                }
                loadAnswers();
            }
            JSONObject ques = data.getJSONObject("ques");
            String content = ques.getStr("content");
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
                LOGGER.debug("题库数量：{},没有收录~：{}", answers.size(), ques.toString());
                if (answer == null) {
                    answer = JSONUtil.toBean(ques, Answer.class);
                }
            }
        }
        return body.toString();
    }
}
