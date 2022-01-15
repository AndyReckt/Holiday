package me.andyreckt.holiday.utils;


public class Pair<A, B> {

    private A a;
    private B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Pair)) {
            return false;
        }

        Pair p = (Pair) o;
        return this.a.equals(p.getA()) && this.b.equals(p.getB());
    }

    public static Pair fromString(String s) {
        String[] nums = s.split(",");
        return new Pair<>(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
    }

    @Override
    public String toString() {
        return this.a + "," + this.b;
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

}

