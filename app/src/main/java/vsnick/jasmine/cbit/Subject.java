package vsnick.jasmine.cbit;

/**
 * Created by vsnick on 25-08-2016.
 */
class Subject{
    public String subName;
    public String teacher;
    public String classesHeld;
    public String classesAtten;
    public String percentage;
    Subject(String subName,String teacher,String classesHeld,String classesAtten,String percentage)
    {
        this.subName=subName;
        this.teacher = teacher;
        this.classesHeld = classesHeld;
        this.classesAtten = classesAtten;
        this.percentage = percentage;
    }
    public String toString()
    {
        String sub="Total";
        if(subName.indexOf(":")>0)
            sub= subName.substring(0,subName.indexOf(":"));
        return (sub+"\t"+percentage+"\n");
    }
}
