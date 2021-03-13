package org.jpm.models;

import org.apache.commons.lang3.text.StrBuilder;
import org.hibernate.validator.constraints.Length;
import org.jpm.ui.data.PictureImage;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

/**
 * Main Bean class that we build the form for.
 * <p>
 * Uses Bean Validation (JSR-303) annotations for automatic validation.
 */
public class FormDetails {

    private Long id;

    @NotNull
    @Length(min = 1, max = 100)
    private String fullname;

    @NotNull
    private String form;

    @NotNull
    private String house;

    private String prefectRole;

    private PictureImage startPrepPicture;
    private PictureImage endOfPrepPicture;
    private PictureImage havingFunPicture;

    // update these, you'll need to update FormQuestionEnum

    @NotNull
    @Length(min = 1, max = 200)
    private String q1;

    @NotNull
    @Length(min = 1, max = 200)
    private String q2;

    @NotNull
    @Length(min = 1, max = 200)
    private String q3;

    @NotNull
    @Length(min = 1, max = 200)
    private String q4;

    @NotNull
    @Length(min = 1, max = 200)
    private String q5;

    @NotNull
    @Length(min = 1, max = 200)
    private String q6;

    @NotNull
    @Length(min = 1, max = 200)
    private String q7;

    @NotNull
    @Length(min = 1, max = 200)
    private String q8;

    @NotNull
    @Length(min = 1, max = 200)
    private String q9;

    @NotNull
    @Length(min = 1, max = 200)
    private String q10;


    public String displayData() throws NoSuchFieldException, IllegalAccessException {


        StrBuilder sb = new StrBuilder();
        sb.append(fullname).appendNewLine().appendNewLine();
        sb.append("Form: ").append(form).appendNewLine().appendNewLine();
        sb.append("House: ").append(house).appendNewLine().appendNewLine();
        if(prefectRole != null && !prefectRole.isEmpty()) {
            sb.append("Prefect Role: ").append(prefectRole).appendNewLine().appendNewLine();
        }
        for(FormQuestion question : FormQuestion.getOrderedQuestions()) {
            sb.append(question.getDescription()).appendNewLine();


            Field field = this.getClass().getDeclaredField(question.getPojoField());
            field.setAccessible(true);
            String value = (String) field.get(this);

            sb.append(value).appendNewLine().appendNewLine();
        }

        return sb.toString();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getPrefectRole() {
        return prefectRole;
    }

    public void setPrefectRole(String prefectRole) {
        this.prefectRole = prefectRole;
    }

    public PictureImage getStartPrepPicture() {
        return startPrepPicture;
    }

    public void setStartPrepPicture(PictureImage startPrepPicture) {
        this.startPrepPicture = startPrepPicture;
    }

    public PictureImage getEndOfPrepPicture() {
        return endOfPrepPicture;
    }

    public void setEndOfPrepPicture(PictureImage endOfPrepPicture) {
        this.endOfPrepPicture = endOfPrepPicture;
    }

    public PictureImage getHavingFunPicture() {
        return havingFunPicture;
    }

    public void setHavingFunPicture(PictureImage havingFunPicture) {
        this.havingFunPicture = havingFunPicture;
    }

    // generic questions

    public String getQ1() {
        return q1;
    }

    public void setQ1(String q1) {
        this.q1 = q1;
    }

    public String getQ2() {
        return q2;
    }

    public void setQ2(String q2) {
        this.q2 = q2;
    }

    public String getQ3() {
        return q3;
    }

    public void setQ3(String q3) {
        this.q3 = q3;
    }

    public String getQ4() {
        return q4;
    }

    public void setQ4(String q4) {
        this.q4 = q4;
    }

    public String getQ5() {
        return q5;
    }

    public void setQ5(String q5) {
        this.q5 = q5;
    }

    public String getQ6() {
        return q6;
    }

    public void setQ6(String q6) {
        this.q6 = q6;
    }

    public String getQ7() {
        return q7;
    }

    public void setQ7(String q7) {
        this.q7 = q7;
    }

    public String getQ8() {
        return q8;
    }

    public void setQ8(String q8) {
        this.q8 = q8;
    }

    public String getQ9() {
        return q9;
    }

    public void setQ9(String q9) {
        this.q9 = q9;
    }

    public String getQ10() {
        return q10;
    }

    public void setQ10(String q10) {
        this.q10 = q10;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return super.hashCode();
        }
        return id.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (id == null) {
            return false;
        }
        if (!(obj instanceof FormDetails)) {
            return false;
        }
        FormDetails other = (FormDetails) obj;
        return id.equals(other.id);
    }
}