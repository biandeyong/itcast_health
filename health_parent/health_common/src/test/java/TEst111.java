import com.itheima.utils.TencentSMS;
import com.itheima.utils.ValidateCodeUtils;
import org.junit.Test;

public class TEst111 {

    @Test
    public void test1(){
        //验证码必须是数字的String
        String s = ValidateCodeUtils.generateValidateCode(4).toString();
        TencentSMS.TencentSMS("18161339491",s);
//        txSMSUtils.sendMessage("15308261939",s);
    }
}
