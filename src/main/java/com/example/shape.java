package com.example;

abstract class Shape {
    // abstrakte Methode, die in abgeleiteten Klassen implementiert werden muss
    abstract void draw();

    // konkrete Methode
    void displayInfo() {
        System.out.println("This is a shape.");
    }
}
