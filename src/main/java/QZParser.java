import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 新强智教务解析
 *
 * @author GreyOVO
 */
public class QZParser {

    private Document document;
    private ArrayList<CourseWrapper> resultList = new ArrayList<CourseWrapper>();

    /**
     * 入口方法
     *
     * @param html html字符串
     */
    public ArrayList<CourseWrapper> parse(String html) {
        this.document = Jsoup.parse(html);
        System.out.println(document.title() + "\n");

        Elements table = document.getElementsByClass("el-table__body-wrapper");
        Elements tableRow = table.select("tbody").select("tr");

        for (int row = 0; row < 6; row++) {
            /*例 row = 0：1-2节*/
            Elements tableData = tableRow.get(row).select("td");
            for (int col = 1; col < 8; col++) {
                /*例 col = 1：周一*/
                Elements content = tableData.get(col).select("div > div > div");

                /*content: 一个格子中的内容。可能包含多个课程。*/
                int offset = 9; // 每个课程中包含9个div
                int courseNum = content.size() / 9; //格子里包含的课程数
                if (courseNum == 0) {
                    continue;
                }
                CourseWrapper course;
                for (int num = 0; num < courseNum; num++) {
                    course = getCourseByElement(content.get(num * offset), row, col);
                    resultList.add(course);
                }
            }
        }
        System.out.println("一共解析了" + resultList.size() + "节课");
        return resultList;
    }

    /**
     * 解析单个课程的内容
     *
     * @param element 单个课程信息节点div
     * @param row     所在行，据此推断节数
     * @param col     所在列，据此推断星期几
     */
    private CourseWrapper getCourseByElement(Element element, int row, int col) {
//        System.out.println(element.select("div").get(3).text());
        Elements info = element.select("div");

        String name = getClassName(info.get(4).text());
        String teacher = info.get(5).text();
        String position = info.get(8).text();
        int sectionStart = getSectionStart(row);
        int sectionContinue = getSectionContinue(info.get(7).text());
        List<Integer> week = getWeeksList(info.get(7).text());

        System.out.println("name = " + name);
        System.out.println("position = " + position);
        System.out.println("teacher = " + teacher);
        System.out.println("day = " + col);
        System.out.println("sectionStart = " + sectionStart);
        System.out.println("sectionContinue = " + sectionContinue);
//        System.out.println("week = " + week);
        System.out.println("==============");
        return new CourseWrapper(name, position, teacher, col, sectionStart, sectionContinue, week);
    }

    /**
     * 获取上课周次
     */
    private ArrayList<Integer> getWeeksList(String str) {
        /*截取周次信息（因为该串可能包含节次信息）使用括号匹配*/
        String regex = "\\(.*?\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find())
            str = matcher.group(0);
        str = str.substring(1, str.length() - 2); //去除两端括号和“周”字

        /*使用逗号 分割多个区间*/
        String[] rangeArr = str.split(",");

        ArrayList<Integer> weekList = new ArrayList<Integer>();
        for (String elem : rangeArr) {
            String[] range = elem.split("-");
            if (range.length == 1) {
                weekList.add(Integer.parseInt(range[0].trim())); // 添加trim()以规避可能出现的空格
            } else {
                int start = Integer.parseInt(range[0].trim());
                int end = Integer.parseInt(range[1].trim());
                for (int i = start; i < end + 1; i++) {
                    weekList.add(i);
                }
            }
        }
        return weekList;
    }

    /**
     * 获取课程起始节次
     *
     * @param row 根据行数确定起始节次。
     *            例 row = 0 -> 1-2节
     */
    private int getSectionStart(int row) {
        return row * 2 + 1;
    }

    /**
     * 获取课程持续节数
     */
    private int getSectionContinue(String str) {
        if (!str.contains("节")) {
            /*默认为两小节*/
            return 2;
        } else {
            int index = str.indexOf("节");  //“节”字前的表示区间
            str = str.substring(0, index);
            String[] range = str.split("-");
            int start = Integer.parseInt(range[0].trim());
            int end = Integer.parseInt(range[1].trim());
            return end - start + 1;
        }
    }

    /**
     * 获取课程名，主要是去除星号
     */
    private String getClassName(String str) {
        if (str == null) return "";
        return str.charAt(0) == '*' ? str.substring(2) : str;
    }

    public ArrayList<CourseWrapper> getResultList() {
        return resultList;
    }

    @Test
    public void testRegex() {
        getWeeksList("11-12节(2 -9周)");
    }
}
