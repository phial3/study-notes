package structural.bridge;

public class White implements Color {

    @Override
    public void paint(String shape) {
        System.out.println("白色的" + shape);
    }
}
