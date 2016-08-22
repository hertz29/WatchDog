/**
 * Created by Hertz on 23/12/2015.
 */
public class test {


    public static String maxSubstring(String s1, String s2){
        return maxSubstring(s1,s2,0,0,0,0,0);
    }

    public static String maxSubstring(String s1, String s2, int index, int tempSum, int tempIndex, int minIndex, int maxSum){
        if(s2.length() <= index) return s2.substring(minIndex, minIndex + maxSum);
        if(s1.indexOf(s2.charAt(index)) < 0){
            if(tempSum < maxSum){
                return maxSubstring(s1,s2, index+1, 0, index+1,minIndex, maxSum);
            }
            else{
                return maxSubstring(s1, s2, index+1, 0, index+1, tempIndex, tempSum);
            }
        }
        else{
            return maxSubstring(s1, s2, index+1, tempSum+1, tempIndex, minIndex, maxSum);
        }
    }


    public static void main(String[] args){
        String s1 = "xyz";
        String s2 = "yxcabyzxyb";

        System.out.println(maxSubstring(s1, s2));

    }
}
