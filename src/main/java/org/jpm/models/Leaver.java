package org.jpm.models;

import java.util.Objects;

public class Leaver {

    private String session;
    private String name;
    private Integer form;
    private Integer baby;

    public Leaver(String session) {
        this.session = session;
    }

    public Leaver(String session, String name, Integer form, Integer baby) {
        this.session = session;
        this.name = name;
        this.form = form;
        this.baby = baby;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getForm() {
        return form;
    }

    public void setForm(Integer form) {
        this.form = form;
    }

    public Integer getBaby() {
        return baby;
    }

    public void setBaby(Integer baby) {
        this.baby = baby;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leaver leaver = (Leaver) o;
        return Objects.equals(session, leaver.session) && Objects.equals(name, leaver.name) && Objects.equals(form, leaver.form) && Objects.equals(baby, leaver.baby);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, name, form, baby);
    }

    @Override
    public String toString() {
        return "Leaver{" +
                "session='" + session + '\'' +
                ", name='" + name + '\'' +
                ", form=" + form +
                ", baby=" + baby +
                '}';
    }
}

