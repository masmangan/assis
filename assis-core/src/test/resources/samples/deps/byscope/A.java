package p1;

class B {
    void foo() {}
}

class A {
    void m(B b) {
        b.foo();   // NameExpr scope
    }
}