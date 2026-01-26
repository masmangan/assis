package p1;

class B {}

class A {
    void m() {
        Class<?> c = B.class;
    }
}