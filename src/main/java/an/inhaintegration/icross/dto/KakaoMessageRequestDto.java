package an.inhaintegration.icross.dto;

import lombok.Data;

@Data
public class KakaoMessageRequestDto {

    private String objType;
    private String text;
    private String webUrl;
    private String mobileUrl;
    private String btnTitle;
}