package org.example.HttpMonitorHelper;

import java.util.List;

public class Answer {
    private List<String> options;
    private String quesTypeStr;
    private String content;
    private List<String> rightOptions;
    private String quesId;

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getQuesTypeStr() {
        return quesTypeStr;
    }

    public void setQuesTypeStr(String quesTypeStr) {
        this.quesTypeStr = quesTypeStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getRightOptions() {
        return rightOptions;
    }

    public void setRightOptions(List<String> rightOptions) {
        this.rightOptions = rightOptions;
    }

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }
}
