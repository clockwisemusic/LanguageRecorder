package com.example.lachlan.myfirstapp.code;

/**
 * Created by lachlan on 7/05/2015.
 */
public class Person {

    public int personid = 0;
    public String name = "";
    public int age = 0;
    public String gender = "";
    public String livesin = "";
    public int livesinyears = 0;
    public String firstlanguage = "";
    public String secondlanguage = "";
    public String thirdlanguage = "";
    public String otherlanguages = "";
    public String education = "";

    public Person() {

    }

    public Person( int _id, String _name, int _age, String _gender, String _livesin,
                   int _livesinyears, String _firstlanguage, String _secondlanguage,
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

    public String toString()
    {
        return( name );
    }
}
