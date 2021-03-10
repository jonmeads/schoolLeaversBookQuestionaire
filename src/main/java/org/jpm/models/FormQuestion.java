package org.jpm.models;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum FormQuestion {

    /*
     * changes here to the number of questions need the FormDetails POJO changed too
     */

    Q1("My best memory of St Columba's:","q1", 1),
    Q2("What I'll miss about Prep:","q2", 2),
    Q3("What I'm looking forward to at Senior School...","q3",3),
    Q4("My proudest moment:", "q4",4),
    Q5("My favourite subject and why:", "q5",5),
    Q6("My favourite book / game / craze / movie...", "q6",6),
    Q7("When I'm older...?", "q7",7),
    Q8("My favourite thing to do out of school:", "q8",8),
    Q9("A random fact about me...", "q9",9),
    Q10("If I had a super power it be...", "q10",10);

    private String description;
    private String pojoField;
    private Integer order;

    FormQuestion(String description, String pojoField, Integer order) {
        this.description = description;
        this.pojoField = pojoField;
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public String getPojoField() {
        return pojoField;
    }

    public Integer getOrder() {
        return order;
    }

    public static List<FormQuestion> getOrderedQuestions() {
        return  Arrays.stream(FormQuestion.values())
                .sorted(Comparator.comparing(FormQuestion::getOrder))
                .collect(Collectors.toList());
    }
}
