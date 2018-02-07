package per.sue.gear3.help;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sure on 2017/11/12.
 */

public class VerifyInputHelper {

    List<VerifyInputRule> verifyInputRuleList = new ArrayList<>();

    public VerifyInputRule  verify(EditText editText){
        VerifyInputRule verifyInputRule = new VerifyInputRule(editText);
        verifyInputRuleList.add(verifyInputRule);
        return verifyInputRule;
    }

    public boolean verify(){
        boolean correct = true;
        for(VerifyInputRule verifyInputRule : verifyInputRuleList){
            if(verifyInputRule.verifyEmpty){
                if(verifyInputRule.targetView instanceof EditText){
                    EditText editText = (EditText)verifyInputRule.targetView;
                    if(TextUtils.isEmpty(editText.getText())){
                        correct = false;
                        editText.setError(verifyInputRule.emptyMessage);
                        editText.requestFocus();
                        break;
                    }
                }
            }
        }
        return correct;
    }


    public static class VerifyInputRule{
        protected View targetView;
        protected boolean verifyEmpty;
        protected String emptyMessage;

        public VerifyInputRule(View targetView) {
            this.targetView = targetView;
        }

        public VerifyInputRule empty(String errorMessage){
            verifyEmpty = true;
            emptyMessage = errorMessage;
            return this;
        };

    }
}
