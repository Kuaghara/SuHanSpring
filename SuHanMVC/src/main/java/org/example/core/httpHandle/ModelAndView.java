package org.example.core.httpHandle;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

/// 内啥，这属于是半废弃的东西，只做了Json的返回设计，ModelAndView的返回设计就是输出字符串+名字
public class ModelAndView {
    private String viewName;
    private Map<String, Object> model = new HashMap<>();
    private JSONArray jsonArray = new JSONArray();

    public ModelAndView() {
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public void addObject(String key, Object value) {
        model.put(key, value);
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public String getViewName() {
        return viewName;
    }

    public void addJsonMessage(String key, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, message);
        jsonArray.add(jsonObject);
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    /**
     * 获取JSON字符串表示
     */
    public String toJSONString() {
        JSONObject result = new JSONObject();
        result.put("viewName", viewName);
        result.put("model", model);
        result.put("jsonArray", jsonArray);
        return result.toJSONString();
    }
}
