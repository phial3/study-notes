package basic.q12;

import java.util.Random;

/**
 * 获取随机数
 *
 * @author samin
 * @date 2021-01-10
 */
public class RandomFunction {

    public static void main(String[] args) {
        // 使用 Random 类的有参和无参方法
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(random.nextInt(100));
        }
        // 有参方法加了 seed 入参以后，不过运行多次，结果一样，不是真正意义上的随机数
        Random random1 = new Random(100);
        System.out.println("-------------------------");
        for (int i = 0; i < 10; i++) {
            System.out.println(random1.nextInt(100));
        }

        // 使用 Math 类的 random()
        System.out.println("--------------------------------");
        for (int i = 0; i < 3; i++) {
            System.out.println((int) (Math.random() * 10));
        }

        /*
         * (int)(Math.random()*n) 生成大于等于 0 小于 n 的随机数。
         * (int)(Math.randon()*n)+m 生成大于等于 m 小于 m+n 的之间的随机数。
         * (int)(Math.random()*(n-m)+m) 生成从 m 到 n 范围内的数，包含 m 不包含 n
         *
         * (char)('a'+Math.random()*('z'-'a'+1)) 随机生成a~z之间的字符
         * (char)(cha1+Math.random()*(cha2-cha1+1)) 随机生成cha1~cha2的字符
         */
    }
}
