package com.example.lachlan.myfirstapp.code;

/**
 * Created by lachlan on 7/05/2015.
 */
public class PersonWord {

    public int personid;
    public int itemid;
    public String word;

    public PersonWord(int _personid, int _itemid, String _word) {
        personid = _personid;
        itemid = _itemid;
        word = _word;
    }

    public String getAsJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        addField(sb, "itemid", this.itemid, true);
        addField(sb, "word", this.word, false);
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

}
