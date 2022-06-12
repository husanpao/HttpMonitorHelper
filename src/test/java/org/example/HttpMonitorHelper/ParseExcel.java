package org.example.HttpMonitorHelper;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParseExcel {

    @Test
    public void parse() {
        ExcelReader reader = ExcelUtil.getReader("data.xls");
        //test
        List<List<Object>> readAll = reader.read();
        List<Answer> answers_list = new ArrayList<>();
        for (List<Object> line : readAll
        ) {
            Answer answer = new Answer();
            String question = StrUtil.format("{}", line.get(1));
            question = question.replaceAll("（）", "（  ）").trim();
            question = question.replaceAll("\\\\(\\\\)", "（  ）").trim();
            answer.setContent(question);
            String right = StrUtil.format("{}", line.get(2));
            List<String> options = new ArrayList<>();
            List<String> rightOptions = new ArrayList<>();
            for (int i = 3; i < 7; i++) {
                String option = StrUtil.format("{}", line.get(i));
                String temp = "";
                if (i == 3) {
                    temp = "A";
                }
                if (i == 4) {
                    temp = "B";
                }
                if (i == 5) {
                    temp = "C";
                }
                if (i == 6) {
                    temp = "D";
                }
                options.add(option.trim());
                if (right.contains(temp)) {
                    rightOptions.add(option.trim());
                }
            }
            answer.setRightOptions(rightOptions);
            answer.setOptions(options);
            Console.log("Question:{}  \noptions:{} \nrightoptions:{}", question, JSONUtil.toJsonStr(options), JSONUtil.toJsonStr(rightOptions));
        }
    }
}
