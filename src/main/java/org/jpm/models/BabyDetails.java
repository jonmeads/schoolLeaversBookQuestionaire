package org.jpm.models;

import org.apache.commons.lang3.text.StrBuilder;
import org.hibernate.validator.constraints.Length;
import org.jpm.ui.data.PictureImage;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class BabyDetails {

    private Long id;

    @NotNull
    @Length(min = 1, max = 100)
    private String fullname;


    @NotNull
    private PictureImage babyPicture;


    public String displayData() {

        StrBuilder sb = new StrBuilder();
        sb.append(fullname).appendNewLine();

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
        return Objects.equals(id, that.id) && Objects.equals(fullname, that.fullname) && Objects.equals(babyPicture, that.babyPicture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullname, babyPicture);
    }

    @Override
    public String toString() {
        return "BabyDetails{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", babyPicture=" + babyPicture +
                '}';
    }
}
