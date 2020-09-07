import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int sizeOfMatrx = sc.nextInt();
        int bound = sc.nextInt();

        if (sizeOfMatrx > bound) {
            System.out.println("impossible");
        } else {
            List<Integer> list = new ArrayList<>();
            int sum = 0;
            for (int i = 1; i < sizeOfMatrx + 1; i++) {
                list.add(i);
                sum += list.get(i - 1);
            }

            int size = list.size() - 1;
            while (!isPrime(sum)) {
                if (list.get(size) == bound) {
                    size--;
                    bound--;
                }
                list.add(size, list.remove(size) + 1);
                sum = 0;
                sum += list.stream().mapToInt(x -> x).sum();
            }
            if (sum == -1) {
                System.out.println("impossible");
            } else {
                for (int i = 0; i < sizeOfMatrx; i++) {
                    for (int m = 0; m < sizeOfMatrx; m++) {
                        System.out.print(list.get(m) + " ");
                    }
                    System.out.println();
                    list.add(list.remove(0));
                }
            }


        }
        sc.close();
    }

    static boolean isPrime(int num) {
        if (num < 2)
            return false;
        if (num == 2)
            return true;
        if (num % 2 == 0)
            return false;
        //num must be odd
        for (int i = 3; i <= Math.sqrt(num); i += 2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }


}
