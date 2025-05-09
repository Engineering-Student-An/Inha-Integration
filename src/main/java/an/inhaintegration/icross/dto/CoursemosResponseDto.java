package an.inhaintegration.icross.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CoursemosResponseDto {

    private String result;
    private List<CourseResponseDto> data;

    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("data")
    public List<CourseResponseDto> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<CourseResponseDto> data) {
        this.data = data;
    }
}
