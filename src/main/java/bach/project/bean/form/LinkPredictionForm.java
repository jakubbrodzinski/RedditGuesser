package bach.project.bean.form;

import bach.project.bean.annotation.Regexp;
import bach.project.utils.AppProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class LinkPredictionForm {
    @NotBlank(message = "Must not be empty")
    @Regexp(regexp = AppProperties.REDDIT_LINK_REGEXP,message = "We only support /r/science posts for now")
    private String link;
    @Min(value = 1,message = "Must be at least 1")
    @Max(value = 100,message = "For this momemnt we support only up to 100 comments per try")
    private Integer ammount=1;
    @Min(value = 0,message = "Must be no less than 0%")
    @Max(value = 100,message = "We cannot provide more than 100% predictions")
    private Integer percentage;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getAmmount() {
        return ammount;
    }

    public void setAmmount(Integer ammount) {
        this.ammount = ammount;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
}
