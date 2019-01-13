package bach.project.service;

import bach.project.bean.model.User;

import java.util.Optional;

public interface PredictionService {
    void predictSingleComment(User user, Optional<String> threadTitle, String singleComment);
    void predictWholeLink(User user,String link,int percentage,int ammount);
}
