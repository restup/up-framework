package com.many.fields;

import com.github.restup.annotations.ApiName;

import javax.persistence.Transient;

@ApiName("a2j")
public class A2J {
    private Long id;
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;
    private String f;
    private String g;
    private String h;
    @Transient
    private String i;
    @Transient
    private String j;

    public A2J() {
    }

    public A2J(Long id, String a, String b, String c, String d, String e, String f, String g, String h, String i,
               String j) {
        super();
        this.id = id;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
        this.j = j;
    }

}
