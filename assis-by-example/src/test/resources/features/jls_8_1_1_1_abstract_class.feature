Feature: JLS 8.1.1.1-1 – Abstract Class Declaration
  This scenario is based on the official Java Language Specification example
  8.1.1.1-1. It validates that ASSIS correctly renders abstract classes
  and inheritance relationships.

  Scenario: Render JLS example 8.1.1.1-1 – Abstract class declaration
    Given a file named "Point.java" with the content
      """
      abstract class Point {
          int x, y;
      
          void move(int dx, int dy) {
              x += dx;
              y += dy;
          }
      
          abstract void alert();
      }
      """
    And a file named "ColoredPoint.java" with the content
      """
      abstract class ColoredPoint extends Point {
          int color;
      }
      """
    And a file named "SimplePoint.java" with the content
      """
      class SimplePoint extends Point {
          void alert() {
              System.out.println("Alert");
          }
      }
      """
    When ASSIS generates a class diagram
    Then the diagram contains
      """
      abstract class "Point"
      """
    And the diagram contains
      """
      abstract class "ColoredPoint"
      """
    And the diagram contains
      """
      class "SimplePoint"
      """
    And the diagram contains
      """
      "ColoredPoint" --|> "Point"
      """
    And the diagram contains
      """
      "SimplePoint" --|> "Point"
      """
