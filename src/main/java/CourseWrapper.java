import java.util.ArrayList;
import java.util.List;

public class CourseWrapper {
    private String name;
    private String position;
    private String teacher;
    /*星期几*/
    private int day;
    /*开始节数*/
    private int sectionStart;
    /*持续节数*/
    private int sectionContinue;
    /*哪几周上课*/
    private List<Integer> week;

    public CourseWrapper(String name, String position, String teacher, int day, int sectionStart, int sectionContinue, List<Integer> week) {
        this.name = name;
        this.position = position;
        this.teacher = teacher;
        this.day = day;
        this.sectionStart = sectionStart;
        this.sectionContinue = sectionContinue;
        this.week = week;
    }

    @Override
    public String toString() {
        return name + ":\n" + teacher +
                " | " + position +
                " | 星期" + day +
                " | 起始节" + sectionStart +
                " | 持续节" + sectionContinue +
                " | 周次" + week.toString() +
                "\n";
    }
}
