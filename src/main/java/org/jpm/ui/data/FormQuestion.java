package org.jpm.ui.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum FormQuestion {

    /*
     * changes here to the number of questions need the FormDetails POJO changed too
     */

    Q1("Best memory of St Columbas?","q1", 1),
    Q2("What I'll miss about Prep","q2", 2),
    Q3("What I'm looking forward to at Senior School","q3",3),
    Q4("My Proudest Moment", "q4",4),
    Q5("Your favourite subject", "q5",5),
    Q6("Your favourite Game/Craze/Movie", "q6",6),
    Q7("What would you like to do when older?", "q7",7),
    Q8("Favourite thing to do out of school?", "q8",8),
    Q9("What was the worst thing about lock down?", "q9",9);

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

    public List<FormQuestion> getOrderedQuestions() {
        return  Arrays.stream(FormQuestion.values())
                .sorted(Comparator.comparing(FormQuestion::getOrder))
                .collect(Collectors.toList());
    }
}
