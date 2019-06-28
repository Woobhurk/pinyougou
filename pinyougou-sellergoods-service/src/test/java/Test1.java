
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class Test1 {



    //StringUtils中 isNotEmpty 和isNotBlank的区别

    @Test
    public void Test(){
        // 判断某字符串是否为空，为空的标准是str==null或str.length()==0

      //  System.out.println("StringUtils.isEmpty(null)"+StringUtils.isEmpty(null));
      //  System.out.println("StringUtils.isEmpty('')"+StringUtils.isEmpty(""));
      //  System.out.println("StringUtils.isEmpty(' ')"+StringUtils.isEmpty(" ")); //StringUtils中空格作非空处理
       // System.out.println("StringUtils.isEmpty('\t \n \f \r')"+StringUtils.isEmpty("\t \n \f \r"));
       // System.out.println("StringUtils.isEmpty('\b')"+StringUtils.isEmpty("\b"));







        //判断某字符串是否为空或长度为0或由空白符(whitespace)构成

        //System.out.println("isBlank(null)"+StringUtils.isBlank(null));
        //System.out.println("isBlank('')"+StringUtils.isBlank(""));
        //System.out.println("isBlank(' ')"+StringUtils.isBlank(" "));
        ////对于制表符、换行符、换页符和回车符"isBlank(null)"+StringUtils.isBlank()均识为空白符
        //System.out.println("isBlank('\t \n \f \r')"+StringUtils.isBlank("\t \n \f \r"));
        //System.out.println("isBlank('\b')"+StringUtils.isBlank("\b"));  //"\b"为单词边界符


    }
}
