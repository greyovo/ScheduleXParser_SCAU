import java.io.*;
import java.util.ArrayList;

public class TestMain {
    public static void main(String[] args) {

        QZParser parser = new QZParser();
        StringBuilder html = new StringBuilder();

        /*读入HTML文件*/
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/sample2021-2.html"));
            String str;
            while ((str = br.readLine()) != null) {
                html.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<CourseWrapper> result = parser.parse(html.toString());
        System.out.println("result.size() = " + result.size());
    }
}
