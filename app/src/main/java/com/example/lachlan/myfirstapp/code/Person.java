package com.example.lachlan.myfirstapp.code;

/**
 * Created by lachlan on 7/05/2015.
 */
public class Person {

    public int personid = 0;
    public String name = "";
    public Integer age;
    public String gender = "";
    public String livesin = "";
    public Integer livesinyears;
    public String firstlanguage = "";
    public String secondlanguage = "";
    public String thirdlanguage = "";
    public String otherlanguages = "";
    public String education = "";

    public PersonWord[] Words;

    public Person() {

    }

    public Person( int _id, String _name, Integer _age, String _gender, String _livesin,
                   Integer _livesinyears, String _firstlanguage, String _secondlanguage,
                   String _thirdlanguage, String _otherlanguages, String _education)
    {
        personid = _id;
        name = _name;
        age = _age;
        gender = _gender;
        livesin = _livesin;
        livesinyears = _livesinyears;
        firstlanguage = _firstlanguage;
        secondlanguage = _secondlanguage;
        thirdlanguage = _thirdlanguage;
        otherlanguages = _otherlanguages;
        education = _education;
    }

    public String getAsJson(PersonWord[] words) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        addField(sb, "name", this.name, true);
        addField(sb, "age", this.age, true);
        addField(sb, "gender", this.gender, true);
        addField(sb, "livesin", this.livesin, true);
        addField(sb, "livesinyears", this.livesinyears, true);
        addField(sb, "firstlanguage", this.firstlanguage, true);
        addField(sb, "secondlanguage", this.secondlanguage, true);
        addField(sb, "thirdlanguage", this.thirdlanguage, true);
        addField(sb, "otherlanguages", this.otherlanguages, true);
        addField(sb, "education", this.education, true);

        sb.append("words: [");

        for (int i=0;i<words.length;i++) {
            sb.append("{ itemid: ");
            sb.append(words[i].itemid);
            sb.append(", word: '");
            sb.append(words[i].word);
            sb.append("'}");
            if (i<words.length-1) {
                sb.append(",");
            }
        }
        sb.append("]");


        sb.append("}");
        return sb.toString();
    }

    private void addField(StringBuilder sb, String name, Object value, Boolean addComma) {

        if (value != null) {
            sb.append(name);
            sb.append(": '");
            sb.append(value);
            sb.append("'");
            if (addComma) {
                sb.append(",");
            }
        }
    }

    public String toString()
    {
        return( name );
    }
}
