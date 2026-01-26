package p1;

class B {}

class A {
    void m(Object o) {
        B b = (B) o;
    }
}