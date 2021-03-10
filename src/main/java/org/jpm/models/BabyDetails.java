package org.jpm.models;

import org.apache.commons.lang3.text.StrBuilder;
import org.hibernate.validator.constraints.Length;
import org.jpm.ui.data.PictureImage;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class BabyDetails {

    private Long id;

    @NotNull
    @Length(min = 1, max = 50)
    private String firstname;

    @NotNull
    @Length(min = 1, max = 50)
    private String lastname;

    @NotNull
    private PictureImage babyPicture;


    public String displayData() {

        StrBuilder sb = new StrBuilder();
        sb.append(firstname).append(" ").append(lastname).appendNewLine();

        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public PictureImage getBabyPicture() {
        return babyPicture;
    }

    public void setBabyPicture(PictureImage babyPicture) {
        this.babyPicture = babyPicture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BabyDetails that = (BabyDetails) o;
        return Objects.equals(id, that.id) && Objects.equals(firstname, that.firstname) && Objects.equals(lastname, that.lastname) && Objects.equals(babyPicture, that.babyPicture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, babyPicture);
    }

    @Override
    public String toString() {
        return "BabyDetails{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", babyPicture=" + babyPicture +
                '}';
    }
}
