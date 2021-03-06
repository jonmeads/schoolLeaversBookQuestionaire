package org.jpm.ui.data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.hibernate.validator.constraints.Length;

/**
 * Main Bean class that we build the form for.
 * <p>
 * Uses Bean Validation (JSR-303) annotations for automatic validation.
 */
public class FormDetails {

    private Long id;

    @NotNull
    @Length(min = 1, max = 32)
    private String firstname;
    @NotNull
    @Length(min = 1, max = 32)
    private String lastname;

    @NotNull
    private String form;

    @NotNull
    private String house;

    private String prefectRole;

    @NotNull
    @Length(min = 1, max = 200)
    private String bestMemory;

    @NotNull
    @Length(min = 1, max = 200)
    private String missAboutPrep;

    @NotNull
    @Length(min = 1, max = 200)
    private String lookingForwardTo;

    @NotNull
    @Length(min = 1, max = 200)
    private String proudestMoment;

    @NotNull
    @Length(min = 1, max = 200)
    private String favSubject;

    @NotNull
    @Length(min = 1, max = 200)
    private String faveGame;

    @NotNull
    @Length(min = 1, max = 200)
    private String likeWhenOlder;

    @NotNull
    @Length(min = 1, max = 200)
    private String favOuterSchool;

    private PictureImage startPrepPicture;
    private PictureImage endOfPrepPicture;

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

    public String getBestMemory() {
        return bestMemory;
    }

    public void setBestMemory(String bestMemory) {
        this.bestMemory = bestMemory;
    }

    public String getMissAboutPrep() {
        return missAboutPrep;
    }

    public void setMissAboutPrep(String missAboutPrep) {
        this.missAboutPrep = missAboutPrep;
    }

    public String getLookingForwardTo() {
        return lookingForwardTo;
    }

    public void setLookingForwardTo(String lookingForwardTo) {
        this.lookingForwardTo = lookingForwardTo;
    }

    public String getProudestMoment() {
        return proudestMoment;
    }

    public void setProudestMoment(String proudestMoment) {
        this.proudestMoment = proudestMoment;
    }

    public String getFavSubject() {
        return favSubject;
    }

    public void setFavSubject(String favSubject) {
        this.favSubject = favSubject;
    }

    public String getFaveGame() {
        return faveGame;
    }

    public void setFaveGame(String faveGame) {
        this.faveGame = faveGame;
    }

    public String getLikeWhenOlder() {
        return likeWhenOlder;
    }

    public void setLikeWhenOlder(String likeWhenOlder) {
        this.likeWhenOlder = likeWhenOlder;
    }

    public String getFavOuterSchool() {
        return favOuterSchool;
    }

    public void setFavOuterSchool(String favOuterSchool) {
        this.favOuterSchool = favOuterSchool;
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